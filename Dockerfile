# Stage 1: Build the application
FROM maven:3.9.7-sapmachine-22 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM openjdk:24-ea-jdk-oracle

WORKDIR /app
COPY --from=builder /app/target/archiveapi-1.0.0.jar /app/archiveapi.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/archiveapi.jar"]