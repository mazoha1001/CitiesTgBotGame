FROM maven:4.0.0-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:21-jdk-slim
WORKDIR /app
# Собираем приложение
COPY --from=builder /app/target/CitiesTgBotGame-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/application.yml /app/application.yml
COPY allCities.json allCities.json
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
