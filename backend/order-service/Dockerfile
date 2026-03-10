# Multi-stage build for order-service
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build

COPY pom.xml .
RUN apk add --no-cache maven && mvn -B dependency:go-offline -DskipTests || true

COPY src ./src
RUN mvn -B package -DskipTests -Dmaven.javadoc.skip=true

# Runtime
FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache wget
WORKDIR /app
RUN adduser -D -u 1000 appuser

COPY --from=builder /build/target/*.jar app.jar
RUN chown -R appuser:appuser /app
USER appuser

EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
