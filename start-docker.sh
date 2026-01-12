#!/usr/bin/env bash
# -------------------------------------------------------------------------
# start-docker.sh  –  полная автоматизация сборки + поднятия микросервисов
# -------------------------------------------------------------------------

set -euo pipefail
IFS=$'\n\t'

# ----------  Цвета для вывода ----------
RED=$(printf '\033[31m')
GREEN=$(printf '\033[32m')
YELLOW=$(printf '\033[33m')
CYAN=$(printf '\033[36m')
RESET=$(printf '\033[0m')

info()    { printf "${CYAN}ℹ %s${RESET}\n" "$*"; }
success() { printf "${GREEN}✔ %s${RESET}\n" "$*"; }
warn()    { printf "${YELLOW}⚠ %s${RESET}\n" "$*"; }
error()   { printf "${RED}✖ %s${RESET}\n" "$*"; }

# ----------  Параметры ----------
PROJECT_ROOT="/Users/adm/OpenideProjects/ALearning_grade2"
cd "$PROJECT_ROOT"

# Пауза между проверками (сек)
SLEEP_INTERVAL=5

# ----------  Вспомогательные функции ----------
wait_for_port() {
    local host=$1 port=$2 label=$3 timeout=${4:-120}
    local elapsed=0
    info "⏳ Ожидаем $label ($host:$port) ≤ $timeout сек..."
    while ! nc -z "$host" "$port" >/dev/null 2>&1; do
        ((elapsed+=SLEEP_INTERVAL))
        if (( elapsed > timeout )); then
            warn "$label НЕ откликнулся за $timeout сек"
            return 1
        fi
        sleep "$SLEEP_INTERVAL"
    done
    success "$label открыт"
    return 0
}

wait_for_http() {
    local url=$1 label=$2 timeout=${3:-120}
    local elapsed=0
    info "⏳ Ожидаем $label ($url) ≤ $timeout сек..."
    while ! curl -fs "$url" >/dev/null 2>&1; do
        ((elapsed+=SLEEP_INTERVAL))
        if (( elapsed > timeout )); then
            warn "$label НЕ ответил за $timeout сек"
            return 1
        fi
        sleep "$SLEEP_INTERVAL"
    done
    success "$label доступен"
    return 0
}

# ----------  1. Maven‑сборка ----------
info "🔨 Maven clean package -DskipTests"
mvn clean package -DskipTests
success "Maven‑сборка завершена"

# ----------  2. Остановка предыдущих контейнеров ----------
info "🛑 Останавливаем текущие контейнеры"
docker compose down --remove-orphans
success "Контейнеры остановлены"

# ----------  3. Пересборка образов ----------
info "🔧 Пересобираем Docker‑образы без кэша"
docker compose build --no-cache
success "Образы пересобраны"

# ----------  4. Запуск базовой инфраструктуры ----------
info "🚀 Поднимаем PostgreSQL, Zookeeper, Kafka, Kafka‑UI"
docker compose up -d postgres zookeeper kafka kafka-ui

# Ждём, пока PostgreSQL примет соединения
wait_for_port "localhost" 5432 "PostgreSQL" 90 || exit 1

# Ждём, пока Kafka откроет порт 9092
wait_for_port "localhost" 9092 "Kafka (порт 9092)" 90 || exit 1

# ----------  5. Config Server ----------
info "🔐 Поднимаем Config Server"
docker compose up -d config-server
wait_for_http "http://localhost:8888/actuator/health" "Config Server" 120 || exit 1

# ----------  6. Service Discovery (Eureka) ----------
info "🧭 Поднимаем Eureka"
docker compose up -d service-discovery
wait_for_http "http://localhost:8761" "Eureka Dashboard" 120 || exit 1

# ----------  7. Бизнес‑сервисы и Gateway ----------
info "🏭 Запускаем User‑service, Notification‑service и Gateway"
docker compose up -d user-service notification-service gateway

# Даем сервисам время для инициализации Spring‑Boot (≈30 сек)
info "⏳ Ждем пока сервисы закончат регистрацию в Eureka"
sleep 30

# ----------  8. Итоги ----------
info ""
echo "==================== ${CYAN}Итоги${RESET} ===================="
docker compose ps
cat <<EOF

🌐 Eureka Dashboard:      http://localhost:8761
🚪 Gateway (API):         http://localhost:8080
👥 User Service:          http://localhost:8081
📨 Notification Service:  http://localhost:8082
📊 Kafka UI:              http://localhost:8083
🏭 Thymeleaf frontend:    http://localhost:8081/users
🔧 Configuration Server   http://localhost:8888/user-service/default
                          http://localhost:8888/notification-service/default
                          http://localhost:8888/gateway/default

🧪 Примеры запросов:
  curl http://localhost:8080/api/users
  curl -X POST http://localhost:8080/api/users \\
       -H 'Content-Type: application/json' \\
       -d '{"name":"Test","email":"test@test.com","age":25}'
EOF
