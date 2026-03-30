# ---- Stage 1: Build ----
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven wrapper and pom.xml first (for dependency caching)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Make mvnw executable and download dependencies
RUN chmod +x mvnw && ./mvnw dependency:resolve -B

# Copy source code and build
COPY src/ src/
RUN ./mvnw package -DskipTests -B

# ---- Stage 2: Run ----
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Render uses PORT env variable
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]