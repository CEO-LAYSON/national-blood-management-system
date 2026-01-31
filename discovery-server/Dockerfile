FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the JAR file from the target directory
COPY target/*.jar app.jar

# Expose the port the application runs on
EXPOSE 8761

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
