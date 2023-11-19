# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17-slim AS builder

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src src
RUN mvn clean package

FROM openjdk:17-jdk-slim

WORKDIR /app
COPY --from=builder /app/target/customer.service-0.0.2.jar .

ENTRYPOINT ["java", "-jar", "customer.service-0.0.2.jar"]