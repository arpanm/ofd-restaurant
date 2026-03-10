# Order Service

Order Management Service for the FoodAI Platform (restaurant app backend). In this repo it exposes **restaurant-facing order APIs only**.

## Overview

The Order Service in the restaurant app backend is scoped to **restaurant operations**:

- **Get order**: By ID or by order number
- **Restaurant orders**: List orders for a restaurant
- **Update status**: Mark preparing, ready, picked up, in transit, delivered
- **Cancel order**: Cancel with reason
- **Assign rider**: Assign delivery rider to order

**Removed in this repo**: Cart and Payment **controllers** (and their HTTP endpoints). Create order, get user orders, active/past orders, rate order, and reorder are not exposed. (Underlying cart/order/payment logic may still exist for internal or future use.)

## Supported Order Types

| Order Type | Description |
|------------|-------------|
| `SINGLE_RESTAURANT` | Standard order from one restaurant |
| `MULTI_RESTAURANT` | Order with items from multiple restaurants |
| `AI_CHAT` | Order placed through AI chat assistant |
| `DIET_PLAN` | Order as part of a diet plan subscription |
| `PARTY_PLANNER` | Order for party/event catering |
| `REORDER` | Repeat of a previous order |
| `SCHEDULED` | Future scheduled order |

## Tech Stack

- **Java 21** - Programming language
- **Spring Boot 3.2.1** - Application framework
- **PostgreSQL** - Primary database for orders and payments
- **MongoDB** - Document store for carts
- **Redis** - Caching layer
- **Kafka** - Event streaming for order events
- **MapStruct** - Object mapping
- **Springdoc OpenAPI** - API documentation

## API Endpoints (Restaurant-facing only in this repo)

### Order APIs (exposed)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/orders/{orderId}` | Get order by ID |
| GET | `/api/v1/orders/number/{orderNumber}` | Get order by number |
| GET | `/api/v1/orders/restaurant/{restaurantId}` | Get restaurant's orders |
| PATCH | `/api/v1/orders/{orderId}/status` | Update order status |
| POST | `/api/v1/orders/{orderId}/cancel` | Cancel order |
| POST | `/api/v1/orders/{orderId}/assign-rider` | Assign delivery rider |

### Not exposed in this repo

- Cart APIs (controller removed)
- Payment APIs (controller removed)
- Create order, get user orders, active/past orders, rate order, reorder (endpoints removed)

## Order Status Flow

```
CREATED → CONFIRMED → PREPARING → READY → PICKED_UP → IN_TRANSIT → DELIVERED
    ↓         ↓           ↓         ↓          ↓           ↓
    └─────────┴───────────┴─────────┴──────────┴───────────┴→ CANCELLED
```

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Service port | 8084 |
| `POSTGRES_HOST` | PostgreSQL host | localhost |
| `POSTGRES_PORT` | PostgreSQL port | 5432 |
| `POSTGRES_DB` | Database name | order_db |
| `POSTGRES_USER` | Database user | postgres |
| `POSTGRES_PASSWORD` | Database password | postgres |
| `MONGO_HOST` | MongoDB host | localhost |
| `MONGO_PORT` | MongoDB port | 27017 |
| `MONGO_DB` | MongoDB database | order_service |
| `REDIS_HOST` | Redis host | localhost |
| `REDIS_PORT` | Redis port | 6379 |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka servers | localhost:9092 |
| `RAZORPAY_KEY_ID` | Razorpay API key | - |
| `RAZORPAY_KEY_SECRET` | Razorpay secret | - |

## Running Locally

### Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL 15+
- MongoDB 7.0+
- Redis 7+
- Kafka (optional for events)

### Build & Run

```bash
# Build the project
./mvnw.sh clean package

# Run with dev profile
./mvnw.sh spring-boot:run -Dspring-boot.run.profiles=dev

# Or run the JAR directly
java -jar target/order-service-1.0.0-SNAPSHOT.jar --spring.profiles.active=dev
```

### Run Tests

```bash
# Run all tests
./mvnw.sh test

# Run with coverage
./mvnw.sh verify
```

## API Documentation

Once the service is running, access:
- Swagger UI: http://localhost:8084/swagger-ui.html
- OpenAPI JSON: http://localhost:8084/api-docs

## Domain Model

### Main Entities

- **Order**: Aggregate root representing a customer order
- **OrderItem**: Items within an order
- **Cart**: Shopping cart (MongoDB document)
- **CartItem**: Items within a cart
- **Payment**: Payment transaction record
- **Refund**: Refund transaction record

### Value Objects

- **DeliveryAddressVO**: Delivery address details
- **OrderTotalVO**: Order total breakdown
- **OrderTimelineEntryVO**: Status change history
- **OrderItemCustomizationVO**: Item customizations

## Kafka Topics

| Topic | Description |
|-------|-------------|
| `order.created` | Published when order is created |
| `order.confirmed` | Published when payment is successful |
| `order.cancelled` | Published when order is cancelled |
| `order.status.updated` | Published on status changes |
| `payment.completed` | Published when payment succeeds |
| `refund.initiated` | Published when refund is requested |

## Health & Metrics

- Health: http://localhost:8084/actuator/health
- Metrics: http://localhost:8084/actuator/prometheus
- Info: http://localhost:8084/actuator/info

