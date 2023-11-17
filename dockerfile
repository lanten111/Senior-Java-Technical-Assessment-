# Stage 1: Build the application
FROM maven:3.8.4-openjdk-17-slim AS builder

WORKDIR /app

# Copy the POM file first for better caching
COPY pom.xml .

# Download dependencies and cache them
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY src src
RUN mvn clean package -DskipTests

# Stage 2: Create the final container
FROM adoptopenjdk:17-jre-hotspot-slim

WORKDIR /app

# Copy the JAR file built in Stage 1
COPY --from=builder /app/target/your-app.jar .

# Expose the port your application will run on
EXPOSE 8080

# Set the entrypoint for the application
ENTRYPOINT ["java", "-jar", "your-app.jar"]