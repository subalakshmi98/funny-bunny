############### 1. BUILD STAGE ##################
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B package -DskipTests


############### 2. RUNTIME STAGE ##################
FROM amazoncorretto:17-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY src/main/db ./src/main/db
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
