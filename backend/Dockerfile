# Etapa 1 — build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2 — runtime
FROM eclipse-temurin:17-jdk
WORKDIR /app

ENV PORT=8080
EXPOSE 8080

COPY --from=build /app/target/*.jar app.jar

CMD ["java", "-Dspring.profiles.active=prod", "-Dserver.port=${PORT}", "-jar", "app.jar"]
