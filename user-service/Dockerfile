FROM amazoncorretto:17-al2-full

WORKDIR /app

COPY target/user-service-1.0-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
