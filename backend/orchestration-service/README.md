# Orchestration Service

> API Gateway for the restaurant app backend. Routes all frontend requests to user, restaurant, menu, order, and promotion services. (Saga and Checkout have been removed in this repo.)

## Overview

The Orchestration Service acts as the **API Gateway only**:

- Single entry point for all frontend requests (port 8085)
- Routes to backend microservices based on URL path
- No Saga engine, no Checkout flow, no MongoDB/Kafka in this service

## Features

- **Request routing**: Path-based routing to the five backend services
- **Authentication forwarding**: Forwards JWT and headers to downstream services
- **Request/response**: Forwards body and query params; returns downstream response
- **Logging**: Request logging with correlation (e.g. X-Request-ID)
- **Timeouts**: Configurable per-service timeouts via WebClient

## API Gateway Routes

All frontend requests go through port **8085** and are routed as follows:

| Path Prefix | Target Service | Port |
|-------------|----------------|------|
| `/api/v1/user/**` | User Service | 8083 |
| `/api/v1/restaurant/**` | Restaurant Service | 8081 |
| `/api/v1/menu/**` | Menu Service | 8082 |
| `/api/v1/order/**` | Order Service | 8084 |
| `/api/v1/promotion/**` | Promotion Service | 8089 |

### Example Routing

```
Frontend Request                                    → Backend Service
--------------------------------------------------------------------------------
GET  /api/v1/user/me                                 → user-service:8083/api/v1/me
POST /api/v1/restaurant/restaurants/onboarding/draft → restaurant-service:8081/api/v1/restaurants/onboarding/draft
GET  /api/v1/menu/restaurants/{id}                   → menu-service:8082/api/v1/restaurants/{id}
GET  /api/v1/order/orders/restaurant/{id}           → order-service:8084/api/v1/orders/restaurant/{id}
GET  /api/v1/promotion/active                        → promotion-service:8089/api/v1/active
```

## API Endpoints (Gateway itself)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /actuator/health | Service health check |
| GET | /api-docs | OpenAPI documentation |
| GET | /swagger-ui.html | Swagger UI |

(All other paths are proxied to the appropriate backend service.)

## Database

None. This service is stateless and does not persist data.

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| SERVER_PORT | Service port | 8085 |
| USER_SERVICE_URL | User service base URL | http://localhost:8083 |
| RESTAURANT_SERVICE_URL | Restaurant service base URL | http://localhost:8081 |
| MENU_SERVICE_URL | Menu service base URL | http://localhost:8082 |
| ORDER_SERVICE_URL | Order service base URL | http://localhost:8084 |
| PROMOTION_SERVICE_URL | Promotion service base URL | http://localhost:8089 |

### Gateway Timeouts

```yaml
gateway:
  timeout:
    connect: 5000    # Connection timeout (ms)
    read: 30000      # Read timeout (ms)
    write: 30000     # Write timeout (ms)
```

## Running Locally

```bash
# Set up Java 21
source scripts/SET_JAVA_21.sh

# Run tests
./mvnw.sh test

# Run with dev profile
./mvnw.sh run dev

# Build
./mvnw.sh build
```

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                    Orchestration Service                         │
│                    (Port 8085 - API Gateway)                    │
├─────────────────────────────────────────────────────────────────┤
│  GatewayController   – Route by path prefix                      │
│  GatewayService      – WebClient-based proxy to backend services│
│  GatewayConfig       – WebClient beans per service              │
└─────────────────────────────────────────────────────────────────┘
                           │
         ┌─────────────────┼─────────────────┐
         ▼                 ▼                 ▼
┌───────────────┐  ┌───────────────┐  ┌───────────────┐
│ User (8083)  │  │ Restaurant    │  │ Menu (8082)   │
└───────────────┘  │ (8081)        │  └───────────────┘
                   └───────────────┘
         ┌─────────────────┼─────────────────┐
         ▼                 ▼
┌───────────────┐  ┌───────────────┐
│ Order (8084)  │  │ Promotion     │
└───────────────┘  │ (8089)        │
                   └───────────────┘
```

## Quality Standards

- Follow backend `QUALITY_STANDARDS.md` and `SERVICE_TEMPLATE_SPEC.md`
- All tests must pass
- OpenAPI documentation for gateway endpoints

## Support

See `backend/README.md`, `backend/DOMAIN_MODEL.md`, and `backend/QUALITY_STANDARDS.md` for development standards.
