
<h3>Добавление Swagger-документации и HATEOAS в API.</h3>

<p>Задокументировать существующее API (из задания 4) с помощью Swagger (Springdoc OpenAPI), чтобы можно было легко изучить и тестировать API через веб-интерфейс.
    Добавить поддержку HATEOAS, чтобы API предоставляло ссылки для навигации по ресурсам.</p>

<h3>Перед запуском приложения выполнить команду: "docker-compose up -d" </h3>

Frontend по адресу http://localhost:8080/users


Документация API будет доступна по адресу:

Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI JSON: http://localhost:8080/api-docs


REST API с HATEOAS  доступен по адресу /api/users с поддержкой:

GET /api/users - получить всех пользователей

GET /api/users/{id} - получить пользователя по ID

POST /api/users - создать пользователя

PUT /api/users/{id} - обновить пользователя

DELETE /api/users/{id} - удалить пользователя