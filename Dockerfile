# Stage 1: Build the application
FROM maven:3.9.7-sapmachine-22 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

COPY /data.csv /app/data.csv
# Stage 2: Create the runtime image
FROM openjdk:24-ea-jdk-oracle

WORKDIR /app
COPY --from=builder /app/target/document-0.0.1-SNAPSHOT.jar /app/smartKrow.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/smartKrow.jar"]