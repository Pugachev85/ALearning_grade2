FROM amazoncorretto:17-al2-full

WORKDIR /app

COPY target/service-discovery-1.0-SNAPSHOT.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]
