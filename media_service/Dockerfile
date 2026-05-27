# Build stage
FROM maven:3.9-eclipse-temurin-21 as builder
WORKDIR /build
# Copy entire project
COPY . .
# Build all modules (Maven will find parent pom automatically)
RUN mvn clean package -DskipTests
# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /build/media_service/target/media_service-1.0.0-SNAPSHOT.jar app.jar

EXPOSE 3005
ENTRYPOINT ["java", "-jar", "app.jar"]
