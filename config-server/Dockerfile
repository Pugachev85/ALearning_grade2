FROM amazoncorretto:17-al2-full

WORKDIR /app

# Копируем JAR файл в контейнер
COPY target/config-server-1.0-SNAPSHOT.jar app.jar

# Создаем папку для конфигов
RUN mkdir -p /app/config-repo

EXPOSE 8888

ENTRYPOINT ["java", "-jar", "app.jar"]
