# Multi-stage build for menu-service
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /build

COPY pom.xml .
RUN apk add --no-cache maven && mvn -B dependency:go-offline -Dmaven.test.skip=true || true

COPY src ./src
RUN mvn -B package -Dmaven.test.skip=true -Dmaven.javadoc.skip=true

# Runtime
FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache wget
WORKDIR /app
RUN adduser -D -u 1000 appuser

COPY --from=builder /build/target/*.jar app.jar
RUN chown -R appuser:appuser /app
USER appuser

EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
