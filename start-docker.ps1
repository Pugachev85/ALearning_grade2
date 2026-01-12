<#====================================================================>
# start-docker.ps1 – полная автоматизация сборки и поднятия микросервисов
# (аналог Bash‑скрипта для macOS/Linux)
#====================================================================#>

# --------------------------------------------------------------------
# 1. Настройки
# --------------------------------------------------------------------
# Путь к корню проекта (измените под свой каталог)
$ProjectRoot = "C:\Users\adm\OpenideProjects\ALearning_grade2"

# Пауза между проверками (сек)
$SleepInterval = 5

# Остановить скрипт при любой ошибке
$ErrorActionPreference = 'Stop'

# --------------------------------------------------------------------
# 2. Цветные выводы
# --------------------------------------------------------------------
function Write-Info   { param([string]$Message) Write-Host "ℹ $Message"   -ForegroundColor Cyan   }
function Write-Success{ param([string]$Message) Write-Host "✔ $Message"   -ForegroundColor Green  }
function Write-Warn   { param([string]$Message) Write-Host "⚠ $Message"   -ForegroundColor Yellow }
function Write-Error  { param([string]$Message) Write-Host "✖ $Message"   -ForegroundColor Red   }

# --------------------------------------------------------------------
# 3. Вспомогательные функции
# --------------------------------------------------------------------
function Wait-ForPort {
    param(
        [string]$Host,
        [int]   $Port,
        [string]$Label,
        [int]   $Timeout = 120
    )
    $elapsed = 0
    Write-Info "⏳ Ожидаем $Label ($Host:$Port) ≤ $Timeout сек..."

    while (-not (Test-NetConnection -ComputerName $Host -Port $Port -InformationLevel Quiet)) {
        Start-Sleep -Seconds $SleepInterval
        $elapsed += $SleepInterval
        if ($elapsed -gt $Timeout) {
            Write-Warn "$Label НЕ откликнулся за $Timeout сек"
            return $false
        }
    }
    Write-Success "$Label открыт"
    return $true
}

function Wait-ForHttp {
    param(
        [string]$Url,
        [string]$Label,
        [int]   $Timeout = 120
    )
    $elapsed = 0
    Write-Info "⏳ Ожидаем $Label ($Url) ≤ $Timeout сек..."

    while ($true) {
        try {
            # HEAD‑запрос, не выводит тело
            Invoke-WebRequest -Uri $Url -Method Head -UseBasicParsing -TimeoutSec 5 -ErrorAction Stop | Out-Null
            Write-Success "$Label доступен"
            return $true
        } catch {
            # ничего не делаем – просто ждём
        }

        Start-Sleep -Seconds $SleepInterval
        $elapsed += $SleepInterval
        if ($elapsed -gt $Timeout) {
            Write-Warn "$Label НЕ ответил за $Timeout сек"
            return $false
        }
    }
}

# --------------------------------------------------------------------
# 4. Переходим в каталог проекта
# --------------------------------------------------------------------
Set-Location -Path $ProjectRoot
Write-Info "Текущий каталог — $ProjectRoot"

# --------------------------------------------------------------------
# 5. Maven‑сборка
# --------------------------------------------------------------------
Write-Info "🔨 Maven clean package -DskipTests"
mvn clean package -DskipTests
Write-Success "Maven‑сборка завершена"

# --------------------------------------------------------------------
# 6. Остановка предыдущих контейнеров
# --------------------------------------------------------------------
Write-Info "🛑 Останавливаем текущие контейнеры"
docker compose down --remove-orphans
Write-Success "Контейнеры остановлены"

# --------------------------------------------------------------------
# 7. Пересборка образов
# --------------------------------------------------------------------
Write-Info "🔧 Пересобираем Docker‑образы без кэша"
docker compose build --no-cache
Write-Success "Образы пересобраны"

# --------------------------------------------------------------------
# 8. Запуск базовой инфраструктуры
# --------------------------------------------------------------------
Write-Info "🚀 Поднимаем PostgreSQL, Zookeeper, Kafka, Kafka‑UI"
docker compose up -d postgres zookeeper kafka kafka-ui

# Ждём, пока PostgreSQL примет соединения
if (-not (Wait-ForPort -Host "localhost" -Port 5432 -Label "PostgreSQL" -Timeout 90)) { exit 1 }

# Ждём, пока Kafka откроет порт 9092
if (-not (Wait-ForPort -Host "localhost" -Port 9092 -Label "Kafka (порт 9092)" -Timeout 90)) { exit 1 }

# --------------------------------------------------------------------
# 9. Config Server
# --------------------------------------------------------------------
Write-Info "🔐 Поднимаем Config Server"
docker compose up -d config-server
if (-not (Wait-ForHttp -Url "http://localhost:8888/actuator/health" -Label "Config Server" -Timeout 120)) { exit 1 }

# --------------------------------------------------------------------
# 10. Service Discovery (Eureka)
# --------------------------------------------------------------------
Write-Info "🧭 Поднимаем Eureka"
docker compose up -d service-discovery
if (-not (Wait-ForHttp -Url "http://localhost:8761" -Label "Eureka Dashboard" -Timeout 120)) { exit 1 }

# --------------------------------------------------------------------
# 11. Бизнес‑сервисы + Gateway
# --------------------------------------------------------------------
Write-Info "🏭 Запускаем User‑service, Notification‑service и Gateway"
docker compose up -d user-service notification-service gateway

# Даем Spring‑Boot‑сервисам время на регистрацию в Eureka (≈30 сек)
Write-Info "⏳ Ждём пока сервисы закончат регистрацию в Eureka"
Start-Sleep -Seconds 30

# --------------------------------------------------------------------
# 12. Итоги
# --------------------------------------------------------------------
Write-Host ""
Write-Host "==================== ИТоги ====================" -ForegroundColor Cyan
docker compose ps

@"
🌐 Eureka Dashboard:          http://localhost:8761
🚪 Gateway (API):             http://localhost:8080
👥 User Service:               http://localhost:8081
📨 Notification Service:       http://localhost:8082
📊 Kafka UI:                   http://localhost:8083
🏭 Thymeleaf frontend:        http://localhost:8081/users

🧪 Примеры запросов:
  curl http://localhost:8080/api/users
  curl -X POST http://localhost:8080/api/users `
       -H 'Content-Type: application/json' `
       -d '{"name":"Test","email":"test@test.com","age":25}'
"@ | Write-Host -ForegroundColor Cyan
