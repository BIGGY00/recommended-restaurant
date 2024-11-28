# Use a lightweight JDK base image with specified architecture
FROM --platform=linux/amd64 eclipse-temurin:17-jdk-alpine

# Set a working directory
WORKDIR /app

# Copy the built JAR file into the container
COPY target/recommended-restaurant-1.0-SNAPSHOT.jar app.jar

# Set the entry point to run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]


# Use a compatible lightweight JDK base image
# FROM eclipse-temurin:17-jdk-jammy

# # Set a working directory
# WORKDIR /app

# # Copy the built JAR file to the container
# COPY target/recommended-restaurant-1.0-SNAPSHOT.jar app.jar

# # Expose the port your application runs on (default 8080 for Spring Boot)
# EXPOSE 8080

# # Run the application
# ENTRYPOINT ["java", "-jar", "/app/app.jar"]
