# Stage 1: Build with Maven and OpenJDK 21
FROM maven:3.9.8-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Use existing slim OpenJDK 21 image
FROM openjdk:25-ea-21-slim

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
COPY .env .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]