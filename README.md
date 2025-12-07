<H2> Задание  №8</H2>

Создать docker-compose.yml, который развернет всю микросервисную систему, включая Kafka, PostgreSQL, API Gateway, Service Discovery, External Configuration и 2 микросервиса(user-service и notification-service, созданные ранее). Проверить, что сервисы корректно взаимодействуют друг с другом в контейнерной среде.
<H3>Для запуска приложения выполните скрипт: 
- start-docker.sh (Linux, MacOS),
- start-docker.ps1 (Windows PowerShall)</H3>

🌐 Eureka Dashboard:   http://localhost:8761

🚪 Gateway (API):      http://localhost:8080

👥 User Service:        http://localhost:8081

Notification Service:   http://localhost:8082

📊 Kafka UI:            http://localhost:8083

🏭 Thymeleaf frontend:      http://localhost:8081/users

🔧 Configuration Server     http://localhost:8888/user-service/default
                            http://localhost:8888/notification-service/default
                            http://localhost:8888/gateway/default

🧪 Примеры запросов:
<p>curl http://localhost:8080/api/users</p>
<p>curl -X POST http://localhost:8080/api/users \\
-H 'Content-Type: application/json' \\
-d '{"name":"Test","email":"test@test.com","age":25}'</p>