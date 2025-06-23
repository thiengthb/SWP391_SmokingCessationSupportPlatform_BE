FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

COPY ../pom.xml .
COPY ../.mvn .mvn
COPY ../mvnw mvnw.cmd ./

RUN mvn dependency:go-offline

COPY ../src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
