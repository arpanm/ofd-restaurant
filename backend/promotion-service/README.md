# Promotion Service

The Promotion Service manages all promotional activities, coupons, marketing campaigns, and customer segments for the FoodAI platform.

## Overview

This service provides:
- **Promotion Management**: Create, update, and manage promotional offers
- **Coupon System**: Issue, validate, and track coupon usage
- **Campaign Management**: Orchestrate marketing campaigns across channels
- **Customer Segmentation**: Create and manage customer segments for targeted promotions

## Technology Stack

| Technology     | Version | Purpose                      |
| -------------- | ------- | ---------------------------- |
| Java           | 21      | Programming language         |
| Spring Boot    | 3.2.1   | Application framework        |
| MongoDB        | 6.0+    | Primary database             |
| Apache Kafka   | 3.4+    | Event streaming              |
| MapStruct      | 1.5.5   | Object mapping               |
| Lombok         | 1.18.30 | Boilerplate reduction        |
| JUnit 5        | 5.10.0  | Testing framework            |
| Springdoc      | 2.3.0   | API documentation            |

## Getting Started

### Prerequisites

- Java 21 (Temurin or OpenJDK recommended)
- MongoDB 6.0+
- Apache Kafka 3.4+ (optional for dev)
- Maven 3.9+

### Building

```bash
# Using the wrapper script
./mvnw.sh build

# Or with parent Maven
cd ..
./mvnw clean compile -pl promotion-service
```

### Running

```bash
# Using the wrapper script
./mvnw.sh run

# Or directly
./mvnw spring-boot:run -pl promotion-service
```

The service will start on port `8089` by default.

### Testing

```bash
# Run all tests
./mvnw.sh test

# Run with coverage
./mvnw.sh verify
```

## API Endpoints

### Promotions

| Method | Endpoint                                         | Description                         |
| ------ | ------------------------------------------------ | ----------------------------------- |
| POST   | `/api/v1/promotions`                             | Create a promotion                  |
| GET    | `/api/v1/promotions/{id}`                        | Get promotion by ID                 |
| GET    | `/api/v1/promotions/code/{code}`                 | Get promotion by code               |
| GET    | `/api/v1/promotions`                             | Get all promotions (paginated)      |
| GET    | `/api/v1/promotions/restaurant/{id}`             | Get promotions by restaurant        |
| GET    | `/api/v1/promotions/restaurant/{id}/active`      | Get active promotions               |
| PUT    | `/api/v1/promotions/{id}/activate`               | Activate a promotion                |
| PUT    | `/api/v1/promotions/{id}/pause`                  | Pause a promotion                   |
| PUT    | `/api/v1/promotions/{id}/resume`                 | Resume a promotion                  |
| PUT    | `/api/v1/promotions/{id}/end`                    | End a promotion                     |
| DELETE | `/api/v1/promotions/{id}`                        | Delete a promotion                  |

### Coupons

| Method | Endpoint                                         | Description                         |
| ------ | ------------------------------------------------ | ----------------------------------- |
| POST   | `/api/v1/coupons`                                | Create a coupon                     |
| GET    | `/api/v1/coupons/{id}`                           | Get coupon by ID                    |
| GET    | `/api/v1/coupons/code/{code}`                    | Get coupon by code                  |
| GET    | `/api/v1/coupons`                                | Get all coupons (paginated)         |
| GET    | `/api/v1/coupons/restaurant/{id}/valid`          | Get valid coupons                   |
| GET    | `/api/v1/coupons/platform`                       | Get platform-wide coupons           |
| POST   | `/api/v1/coupons/validate`                       | Validate a coupon                   |
| POST   | `/api/v1/coupons/apply`                          | Apply a coupon to order             |
| PUT    | `/api/v1/coupons/{id}/activate`                  | Activate a coupon                   |
| PUT    | `/api/v1/coupons/{id}/deactivate`                | Deactivate a coupon                 |
| DELETE | `/api/v1/coupons/{id}`                           | Delete a coupon                     |

### Campaigns

| Method | Endpoint                                         | Description                         |
| ------ | ------------------------------------------------ | ----------------------------------- |
| POST   | `/api/v1/campaigns`                              | Create a campaign                   |
| GET    | `/api/v1/campaigns/{id}`                         | Get campaign by ID                  |
| GET    | `/api/v1/campaigns`                              | Get all campaigns (paginated)       |
| GET    | `/api/v1/campaigns/restaurant/{id}/active`       | Get active campaigns                |
| PUT    | `/api/v1/campaigns/{id}`                         | Update a campaign                   |
| PUT    | `/api/v1/campaigns/{id}/activate`                | Activate a campaign                 |
| PUT    | `/api/v1/campaigns/{id}/pause`                   | Pause a campaign                    |
| PUT    | `/api/v1/campaigns/{id}/complete`                | Complete a campaign                 |
| POST   | `/api/v1/campaigns/{id}/metrics`                 | Record campaign metrics             |
| DELETE | `/api/v1/campaigns/{id}`                         | Delete a campaign                   |

### Customer Segments

| Method | Endpoint                                         | Description                         |
| ------ | ------------------------------------------------ | ----------------------------------- |
| POST   | `/api/v1/segments`                               | Create a segment                    |
| GET    | `/api/v1/segments/{id}`                          | Get segment by ID                   |
| GET    | `/api/v1/segments`                               | Get all segments (paginated)        |
| GET    | `/api/v1/segments/active`                        | Get active segments                 |
| GET    | `/api/v1/segments/user/{userId}`                 | Get segments for a user             |
| PUT    | `/api/v1/segments/{id}`                          | Update a segment                    |
| PUT    | `/api/v1/segments/{id}/activate`                 | Activate a segment                  |
| POST   | `/api/v1/segments/{id}/users/{userId}`           | Add user to segment                 |
| DELETE | `/api/v1/segments/{id}/users/{userId}`           | Remove user from segment            |
| DELETE | `/api/v1/segments/{id}`                          | Delete a segment                    |

## Domain Model

### Aggregates

- **Promotion**: Discount offers with scheduling and usage limits
- **Coupon**: Redeemable codes with validation rules
- **Campaign**: Marketing campaigns with multi-channel support
- **CustomerSegment**: User groupings for targeting

### Events Published

- `PROMOTION_CREATED`, `PROMOTION_ACTIVATED`, `PROMOTION_ENDED`
- `COUPON_CREATED`, `COUPON_APPLIED`, `COUPON_DEACTIVATED`
- `CAMPAIGN_CREATED`, `CAMPAIGN_ACTIVATED`, `CAMPAIGN_COMPLETED`

### Events Consumed

- `ORDER_COMPLETED` - Records promotion/coupon usage
- `USER_REGISTERED` - Updates segment membership
- `USER_TIER_CHANGED` - Updates segment membership
- `RESTAURANT_DEACTIVATED` - Ends associated promotions

## Configuration

### Application Properties

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/promotion_db
  kafka:
    bootstrap-servers: localhost:9092

server:
  port: 8086
```

### Environment Variables

| Variable             | Description            | Default               |
| -------------------- | ---------------------- | --------------------- |
| `MONGODB_URI`        | MongoDB connection URI | localhost:27017       |
| `KAFKA_SERVERS`      | Kafka bootstrap servers| localhost:9092        |
| `SERVER_PORT`        | Service port           | 8086                  |

## Project Structure

```
promotion-service/
├── src/main/java/com/foodai/promotion/
│   ├── PromotionApplication.java
│   ├── config/              # Configuration classes
│   ├── controller/          # REST controllers
│   ├── domain/
│   │   ├── model/           # Domain entities
│   │   └── repository/      # Repository interfaces
│   ├── dto/
│   │   ├── request/         # Request DTOs
│   │   └── response/        # Response DTOs
│   ├── exception/           # Custom exceptions
│   ├── infrastructure/
│   │   └── messaging/       # Kafka publishers/consumers
│   ├── mapper/              # MapStruct mappers
│   └── service/             # Business logic
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   └── logback-spring.xml
├── src/test/
│   ├── java/                # Test classes
│   └── resources/
│       └── application-test.yml
├── scripts/
│   └── SET_JAVA_21.sh
├── mvnw.sh
├── pom.xml
└── README.md
```

## API Documentation

OpenAPI documentation is available at:
- Swagger UI: `http://localhost:8089/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8089/api-docs`

## Health Check

The service exposes health endpoints:
- `GET /actuator/health` - Service health status
- `GET /actuator/info` - Service information
- `GET /actuator/metrics` - Service metrics

## License

Proprietary - FoodAI Platform

