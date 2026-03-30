# Use lightweight Java image
FROM openjdk:21-jdk-slim

# Set working directory
WORKDIR /app

# Copy jar file
COPY target/tms-*.jar app.jar

# Expose port (Render uses this)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]