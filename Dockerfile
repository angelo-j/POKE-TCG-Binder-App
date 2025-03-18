# ---- Step 1: Build the JAR ----
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app

# Copy the entire project
COPY . .

# Build the project, skipping tests if you want faster builds
RUN mvn clean package -DskipTests

# ---- Step 2: Create the runtime image ----
FROM eclipse-temurin:17-jdk-slim
WORKDIR /app

# Copy the jar from the build stage
# NOTE: This name must match the final jar in /target after "mvn clean package"
COPY --from=build /app/target/jordan-poke-tcg-webapp-1.0.jar app.jar

# Expose the Spring Boot port
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java", "-jar", "app.jar"]
