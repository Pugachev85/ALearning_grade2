#!/bin/bash

echo "🛑 Останавливаем Spring сервисы..."

cd /Users/adm/OpenideProjects/ALearning_grade2

# Останавливаем процессы по PID файлам
for pid_file in logs/*.pid; do
    if [ -f "$pid_file" ]; then
        pid=$(cat $pid_file)
        service_name=$(basename $pid_file .pid)
        echo "Останавливаем $service_name (PID: $pid)..."
        kill $pid 2>/dev/null && echo "✅ $service_name остановлен" || echo "❌ $service_name уже остановлен"
        rm $pid_file 2>/dev/null
    fi
done

# Принудительно завершаем все Java процессы наших сервисов
pkill -f "java.*SNAPSHOT.jar" && echo "✅ Все сервисы остановлены" || echo "❌ Не было запущенных сервисов"

echo ""
echo "🧹 Очищаем логи..."
rm -f logs/*.log 2>/dev/null

echo "🎯 Инфраструктура (Docker) продолжает работать."
echo "Чтобы остановить инфраструктуру: docker-compose down"