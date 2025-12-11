# Stage 1: Build the application
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies (for better caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -B

# Stage 2: Run the application
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (Render will use the PORT environment variable)
EXPOSE 8080

# Set active profile to production
ENV SPRING_PROFILES_ACTIVE=production

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
