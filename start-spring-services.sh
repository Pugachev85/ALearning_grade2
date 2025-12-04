#!/bin/bash

echo " 🚀 Запуск Spring сервисов вручную..."

# Переходим в корень проекта
cd /Users/adm/OpenideProjects/ALearning_grade2

echo " 🔍 Проверяем, что инфраструктура запущена..."
if ! docker-compose ps | grep -q "Up"; then
    echo " ❌ Инфраструктура не запущена. Запускаем..."
    docker-compose up -d postgres zookeeper kafka kafka-ui
    echo " ⏳ Ждем готовности инфраструктуры..."
    sleep 20
fi

echo " ✅ Инфраструктура запущена:"
docker-compose ps | grep "Up"

# Функция для запуска сервиса
start_service() {
    local service_name=$1
    local port=$2
    local profile=$3

    echo ""
    echo " 🔄 Запуск $service_name на порту $port (профиль: $profile)..."

    # Переходим в директорию сервиса
    cd $service_name

    # Проверяем, что сервис собран
    if [ ! -f "target/$service_name-1.0-SNAPSHOT.jar" ]; then
        echo " ❌ JAR файл не найден. Собираем $service_name..."
        mvn clean package -DskipTests
    fi

    # Запускаем сервис в фоне
    nohup java -jar target/$service_name-1.0-SNAPSHOT.jar \
        --server.port=$port \
        --spring.profiles.active=$profile \
        --eureka.client.service-url.defaultZone=http://localhost:8761/eureka \
        > ../logs/$service_name.log 2>&1 &

    # Сохраняем PID процесса
    echo $! > ../logs/$service_name.pid

    cd ..

    echo " ✅ $service_name запущен (PID: $(cat logs/$service_name.pid))"
    echo " 📄 Логи: logs/$service_name.log"

    # Даем время сервису запуститься
    sleep 8
}

# Создаем папку для логов
mkdir -p logs

echo ""
echo " 🧹 Останавливаем предыдущие запуски..."
pkill -f "java.*SNAPSHOT.jar" || echo "Не было предыдущих запусков"

echo ""
echo " 🔨 Собираем проекты если нужно..."
for service in config-server service-discovery user-service notification-service gateway; do
    if [ ! -f "$service/target/$service-1.0-SNAPSHOT.jar" ]; then
        echo "Собираем $service..."
        cd $service
        mvn clean package -DskipTests
        cd ..
    fi
done

echo ""
echo " 🎯 Запускаем сервисы в правильном порядке..."

# 1. Config Server
start_service "config-server" "8888" "native"

# Проверяем, что Config Server запустился
echo " ⏳ Проверяем Config Server..."
sleep 10
if curl -s http://localhost:8888/actuator/health > /dev/null; then
    echo " ✅ Config Server запущен и работает"
else
    echo " ❌ Config Server не ответил. Проверьте логи: tail -f logs/config-server.log"
fi

# 2. Service Discovery (Eureka)
start_service "service-discovery" "8761" "docker"

# Ждем пока Eureka запустится
echo " ⏳ Ждем запуск Eureka..."
sleep 15

# 3. User Service
start_service "user-service" "8081" "docker"

# 4. Notification Service
start_service "notification-service" "8082" "docker"

# 5. Gateway
start_service "gateway" "8080" "docker"

echo ""
echo " ⏳ Ожидаем регистрацию сервисов в Eureka..."
sleep 30

echo ""
echo " 🔍 Проверяем статус сервисов..."

# Проверяем Eureka
if curl -s http://localhost:8761 > /dev/null; then
    echo " ✅ Eureka Dashboard: http://localhost:8761"
else
    echo " ❌ Eureka не запущен"
fi

# Проверяем Gateway
if curl -s http://localhost:8080/actuator/health > /dev/null; then
    echo " ✅ Gateway: http://localhost:8080"
else
    echo " ❌ Gateway не запущен"
fi

# Проверяем Config Server
if curl -s http://localhost:8888/actuator/health > /dev/null; then
    echo " ✅ Config Server: http://localhost:8888"
else
    echo " ❌ Config Server не запущен"
fi

echo ""
echo " 📋 Активные процессы:"
ps aux | grep "SNAPSHOT.jar" | grep -v grep

echo ""
echo " 📊 Логи сервисов:"
echo "   tail -f logs/config-server.log"
echo "   tail -f logs/service-discovery.log"
echo "   tail -f logs/user-service.log"
echo "   tail -f logs/notification-service.log"
echo "   tail -f logs/gateway.log"

echo ""
echo " 🎯 Тестовые команды:"
echo "   curl http://localhost:8080/api/users"
echo "   curl http://localhost:8761"
echo "   curl http://localhost:8888/user-service/default"
echo "   адрес фронтэнд http://localhost:8081/users"

echo ""
echo " 🛑 Команды для остановки:"
echo "   ./stop-spring-services.sh  - остановить все сервисы"
echo "   pkill -f 'java.*SNAPSHOT.jar'  - принудительная остановка"

echo ""
echo " ✅ Все сервисы запущены!"
echo " 🎉 Система готова к работе!"
