FROM amazoncorretto:17-al2-full

WORKDIR /app

COPY target/gateway-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
