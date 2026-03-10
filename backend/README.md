# FoodAI Platform - Backend Services (Restaurant App)

> Backend for the **restaurant app only**: onboarding, dashboard, menu, orders, promotions, campaigns, segments. Consumer-only flows (checkout, cart, loyalty, saga) have been removed.

## Restaurant-only cleanup (what was removed)

- **orchestration-service**: Checkout and Saga controllers, saga engine, Kafka/Mongo dependencies. Kept: API Gateway only (routes to user, restaurant, menu, order, promotion).
- **user-service**: Loyalty controller, LoyaltyService, and loyalty domain/DTOs. Kept: User CRUD and auth-related endpoints.
- **order-service**: Cart and Payment controllers. Order controller trimmed to restaurant-facing APIs only: get order, get by number, get restaurant orders, update status, cancel, assign rider. Removed: create order, get user orders, get active/past orders, rate order, reorder.
- **restaurant-service**, **menu-service**, **promotion-service**: Restaurant-relevant. Restaurant-service adds: onboarding draft (save/resume by step), post-onboarding owner user creation via user-service, and verification email/SMS (stub) for login.

## Overview

This backend implements a Domain-Driven Design (DDD) architecture with **five core microservices** plus an API Gateway (orchestration-service). We follow a **sensible microservices boundary**: not over-split (e.g. no separate notification or payment service in this repo), but clear separation by bounded context (user, restaurant, menu, order, promotion). All services follow uniform coding standards, testing requirements, and API patterns defined in `QUALITY_STANDARDS.md`, `SERVICE_TEMPLATE_SPEC.md`, and `DOMAIN_MODEL.md`.

## Architecture

- **Design Pattern**: Domain-Driven Design (DDD)
- **Architecture Style**: Microservices with Event-Driven Communication
- **API Gateway**: Orchestration Service (port 8085)
- **Language**: Java 21
- **Framework**: Spring Boot 3.2.1
- **Build Tool**: Maven 3.8+

### System Architecture Flow

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                              FRONTEND (React + Redux)                            │
│                                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐         │
│  │   Consumer   │  │  Restaurant  │  │ Diet Planner │  │ Super Admin  │         │
│  │   Interface  │  │  Dashboard   │  │              │  │  Dashboard   │         │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘         │
│         │                 │                 │                 │                  │
│  ┌──────▼─────────────────▼─────────────────▼─────────────────▼───────┐         │
│  │                    Redux Store (State Management)                   │         │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐       │         │
│  │  │  auth   │ │  cart   │ │  order  │ │  menu   │ │ promo   │       │         │
│  │  │  Slice  │ │  Slice  │ │  Slice  │ │  Slice  │ │ Slice   │       │         │
│  │  └────┬────┘ └────┬────┘ └────┬────┘ └────┬────┘ └────┬────┘       │         │
│  └───────┼───────────┼───────────┼───────────┼───────────┼────────────┘         │
│          │           │           │           │           │                       │
│  ┌───────▼───────────▼───────────▼───────────▼───────────▼────────────┐         │
│  │                       API Service Layer (Axios)                     │         │
│  │  userService | restaurantService | menuService | orderService | ... │         │
│  └─────────────────────────────────┬───────────────────────────────────┘         │
└────────────────────────────────────┼─────────────────────────────────────────────┘
                                     │
                                     │ HTTP/HTTPS (All requests to port 8085)
                                     ▼
┌────────────────────────────────────────────────────────────────────────────────────┐
│                    API GATEWAY (Orchestration Service - Port 8085)                  │
│                                                                                     │
│  ┌─────────────────────────────────────────────────────────────────────────────┐   │
│  │                         GatewayController                                    │   │
│  │  /api/v1/user/**     → UserService (8083)                                   │   │
│  │  /api/v1/restaurant/** → RestaurantService (8081)                           │   │
│  │  /api/v1/menu/**     → MenuService (8082)                                   │   │
│  │  /api/v1/order/**    → OrderService (8084)                                  │   │
│  │  /api/v1/promotion/** → PromotionService (8089)                             │   │
│  │  (saga/checkout removed in restaurant-only backend)                          │   │
│  └─────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                     │
│  ┌───────────────────────────────┐                                                 │
│  │      Authentication &         │  (Saga/Checkout removed in restaurant-only)     │
│  │      Authorization            │                                                 │
│  │  • JWT Token Validation       │                                                 │
│  │  • Role-Based Access          │                                                 │
│  │  • Request ID Tracing         │                                                 │
│  │  • Rate Limiting              │                                                 │
│  └───────────────────────────────┘                                                 │
└─────────────────────────────────────────────────────────────────────────────────────┘
                                     │
         ┌───────────────────────────┼───────────────────────────┐
         │                           │                           │
         ▼                           ▼                           ▼
┌─────────────────┐        ┌─────────────────┐        ┌─────────────────┐
│  User Service   │        │ Order Service   │        │ Menu Service    │
│    (8083)       │        │    (8084)       │        │    (8082)       │
│                 │        │                 │        │                 │
│ • Auth/Login    │        │ • Restaurant    │        │ • Categories    │
│ • Registration  │        │   orders only  │        │ • Menu Items    │
│ • Profile       │        │ • Order status │        │ • Pricing       │
│ • Addresses     │        │ • Cancel/assign│        │ • Availability  │
│ • Favorites     │        │ (Cart/Payment  │        │ • Search        │
│ (Loyalty removed)│        │  APIs removed) │        │                 │
│     [MongoDB]   │        │ [PostgreSQL +   │        │    [MongoDB]    │
│                 │        │     MongoDB]    │        │                 │
└────────┬────────┘        └────────┬────────┘        └────────┬────────┘
         │                          │                          │
         │                          ▼                          │
         │               ┌─────────────────┐                   │
         │               │    Kafka        │◄──────────────────┤
         ├───────────────►│  (Event Bus)   │                   │
         │               └────────┬────────┘                   │
         │                        │                            │
         ▼                        ▼                            ▼
┌─────────────────┐        ┌─────────────────┐
│ Restaurant Svc  │        │ Promotion Svc   │
│    (8081)       │        │    (8089)       │
│                 │        │                 │
│ • Onboarding    │        │ • Promotions    │
│   draft/save    │        │ • Coupons       │
│ • Management    │        │ • Campaigns     │
│ • Outlets       │        │ • Segments      │
│ • → user-svc    │        │    [MongoDB]    │
│   (owner user)  │        └─────────────────┘
│ • Notify stub   │  (Notification: stub in restaurant-service; optional separate svc later)
│    [MongoDB]    │
└─────────────────┘
```

### API Gateway Request Flow

```
Frontend Request → API Gateway (8085) → Route to Backend Service → Response

Example Flows:
─────────────────────────────────────────────────────────────────────────

1. User Login:
   POST /api/v1/user/auth/login
   → Gateway (8085) → User Service (8083) → JWT Token Response

2. Fetch Restaurants:
   GET /api/v1/restaurant/search?query=pizza&lat=12.9&lng=77.6
   → Gateway (8085) → Restaurant Service (8081) → Restaurant List

3. Restaurant onboarding (draft save/resume):
   POST /api/v1/restaurant/restaurants/onboarding/draft → Create draft, get draftId
   GET  /api/v1/restaurant/restaurants/onboarding/draft/{draftId} → Load draft (prefill)
   PATCH /api/v1/restaurant/restaurants/onboarding/draft/{draftId} → Save step
   POST /api/v1/restaurant/restaurants/onboarding/submit → Submit draft → create restaurant + owner user (user-service) + send verification (stub)

4. Apply Coupon:
   POST /api/v1/promotion/coupons/validate
   → Gateway (8085) → Promotion Service (8089) → Discount Calculated
```

### Frontend Integration

The frontend React application integrates with the backend through a comprehensive service layer:

```
Frontend (React + Redux)
│
├── src/services/               # API Service Layer
│   ├── api.config.ts          # Axios + Gateway Config (port 8085)
│   ├── user.service.ts        # → /api/v1/user/**
│   ├── restaurant.service.ts  # → /api/v1/restaurant/**
│   ├── menu.service.ts        # → /api/v1/menu/**
│   ├── order.service.ts       # → /api/v1/order/**
│   └── promotion.service.ts   # → /api/v1/promotion/**
│
├── src/store/slices/           # Redux State Management
│   ├── authSlice.ts           # User auth state
│   ├── restaurantSlice.ts     # Restaurant state
│   ├── menuSlice.ts           # Menu state
│   ├── cartSlice.ts           # Cart state
│   ├── orderSlice.ts          # Order state
│   └── promotionSlice.ts      # Promotion state
│
└── src/hooks/                  # React Hooks
    ├── useAuth.ts             # Authentication hook
    ├── useCart.ts             # Cart management hook
    └── useOrders.ts           # Order management hook
```

#### Environment Configuration

Frontend `.env`:
```bash
# API Gateway (Orchestration Service)
VITE_API_GATEWAY_URL=http://localhost:8085/api/v1

# WebSocket for real-time updates
VITE_WS_HOST=localhost:8085
```

#### Key Integration Patterns

1. **All requests go through API Gateway (port 8085)**
2. **JWT tokens managed automatically via interceptors**
3. **Request IDs added for distributed tracing**
4. **Automatic token refresh on 401 responses**
5. **Redux async thunks for API calls**
6. **Custom hooks for component-level state access**

## Standardized Versions

All backend services use consistent versions:

| Component | Version |
|-----------|---------|
| Java | 21 (Temurin) |
| Spring Boot | 3.2.1 |
| Spring Cloud | 2023.0.0 |
| MongoDB Driver | 4.11.1 |
| PostgreSQL Driver | 42.7.1 |
| MapStruct | 1.5.5.Final |
| Lombok | 1.18.30 |
| SpringDoc OpenAPI | 2.3.0 |
| Logstash Encoder | 7.4 |
| Testcontainers | 1.19.3 |
| REST Assured | 5.4.0 |
| Mockito | 5.14.0 |
| JaCoCo | 0.8.13 |
| Maven Compiler Plugin | 3.12.1 |
| Maven Surefire Plugin | 3.2.3 |
| Maven Failsafe Plugin | 3.2.3 |

## Service Ports

| Service | Port |
|---------|------|
| restaurant-service | 8081 |
| menu-service | 8082 |
| user-service | 8083 |
| order-service | 8084 |
| orchestration-service (API Gateway) | 8085 |
| promotion-service | 8089 |

## Technology Stack

### Databases
- **MongoDB**: Primary database for most services (users, restaurants, menus, etc.)
- **PostgreSQL**: Transactional data (orders, payments, ledgers)
- **Redis**: Caching layer
- **Elasticsearch**: Search and discovery

### Messaging & Events
- **Apache Kafka**: Event streaming between services
- **GCP Pub/Sub**: Alternative event bus

### API & Gateway
- **Spring Cloud Gateway**: API Gateway
- **OpenAPI 3.0**: API documentation (Springdoc)

## Project Structure

```
backend/
├── orchestration-service/         # API Gateway (routes only; no Saga in this repo)
├── user-service/                  # User CRUD, auth (Loyalty removed)
├── restaurant-service/            # Restaurant + onboarding draft, owner user, notification stub
├── menu-service/                  # Menu & pricing
├── order-service/                 # Order management (restaurant-facing APIs only)
├── promotion-service/             # Promotions & campaigns
├── DOMAIN_MODEL.md                # Bounded contexts and domain model
├── QUALITY_STANDARDS.md           # Quality gates and tools
├── SERVICE_TEMPLATE_SPEC.md       # Service template specification
└── README.md                      # This file
```

## Microservices (Restaurant App – 5 Core Services)

| Service | Port | Purpose |
|---------|------|---------|
| **orchestration-service** | 8085 | API Gateway only (routes to user, restaurant, menu, order, promotion). No Saga/Checkout in this repo. |
| **user-service** | 8083 | User CRUD, auth, addresses, favorites. Loyalty removed. Called by restaurant-service to create owner user after onboarding. |
| **restaurant-service** | 8081 | Restaurant onboarding (draft save/resume), management, outlets. Post-submit: create owner via user-service, send verification email/SMS (stub). |
| **menu-service** | 8082 | Menu items, categories, pricing, availability. |
| **order-service** | 8084 | Restaurant-facing order APIs only: get order, get by number, restaurant orders, update status, cancel, assign rider. Cart/Payment controllers removed. |
| **promotion-service** | 8089 | Promotions, coupons, campaigns, customer segments. |

## Development Standards

All services MUST follow the standards defined in:
- `DOMAIN_MODEL.md` - Bounded contexts and domain model
- `QUALITY_STANDARDS.md` - Quality gates and tools
- `SERVICE_TEMPLATE_SPEC.md` - Service templates and code patterns
- Each service’s `README.md` and `CURSOR_PROMPT.md` (if present) for service-specific behaviour

### Key Standards

#### 1. Layered Architecture (Mandatory)
```
Controller Layer (REST API)
    ↓
Application Service Layer (Orchestration)
    ↓
Domain Layer (Business Logic)
    ↓
Repository Layer (Data Access)
```

#### 2. CRUD API Pattern
```
POST   /api/v1/{resource}              - Create
GET    /api/v1/{resource}/{id}         - Get by ID
GET    /api/v1/{resource}              - Get all (paginated)
PUT    /api/v1/{resource}/{id}         - Update
DELETE /api/v1/{resource}/{id}         - Soft delete
```

#### 3. Testing Requirements
- **Minimum Code Coverage**: 80% (enforced by JaCoCo)
- **Unit Tests**: All classes
- **Integration Tests**: With Testcontainers
- **Repository Tests**: Real database testing
- **ALL tests MUST pass**

#### 4. Code Quality
- **Formatting**: Google Java Format (Spotless)
- **Style**: Google Checkstyle
- **Documentation**: JavaDoc for all public classes/methods
- **API Docs**: OpenAPI 3.0 (automatic)

## Using Cursor AI for Development

### Setup

1. Open the `backend` directory in Cursor
2. Cursor will automatically read `.cursor/rules`
3. All code generation will follow the defined standards

### Generating a New Service

Use this prompt template in Cursor:

```
Create {service-name}-service following the FoodAI microservice template:

Service: {service-name}-service
Domain: {domain}
Purpose: {brief description}
Database: {MongoDB/PostgreSQL}
Key Entities: {list entities}

Generate:
1. Complete module structure
2. All layers: Controller → Service → Domain → Repository
3. DTOs and Mappers
4. Configurations (MongoDB/PostgreSQL, Redis, Kafka)
5. Unit tests for each class
6. Integration tests with Testcontainers
7. Ensure 80%+ code coverage
8. Ensure ALL tests pass

Follow:
- Domain-Driven Design principles
- CRUD API standards from backend/.cursor/rules
- Error handling from common module
- Structured logging
- OpenAPI documentation

DO NOT proceed until:
- All tests are written
- All tests pass
- Code coverage >= 80%
```

### Example: Creating User Service

```
Create user-service following the FoodAI microservice template:

Service: user-service
Domain: user
Purpose: Manage user accounts, profiles, and preferences
Database: MongoDB
Key Entities: User, Address, Preferences

Generate complete implementation with:
- User entity with email, phone, profile, addresses
- CRUD operations
- MongoDB repository
- DTOs for create/update/response
- MapStruct mappers
- Unit tests with Mockito
- Integration tests with Testcontainers
- 80%+ code coverage
- All tests passing
```

## Building & Running

### Prerequisites
- Java 21 (Temurin recommended)
- Maven 3.8+
- Docker (for MongoDB, PostgreSQL, Kafka, Redis)
- MongoDB 7.0 (local or Docker)
- Redis (local or Docker)
- Kafka (local or Docker)

### Setting Up Java 21

Each service includes a `scripts/SET_JAVA_21.sh` script:

```bash
# Source the script to set JAVA_HOME
source {service-name}-service/scripts/SET_JAVA_21.sh
```

### Using Maven Wrapper Scripts

Each service includes a `mvnw.sh` script that handles:
- Java 21 version setup
- MongoDB availability check
- Application startup with profile selection

```bash
# Run service with wrapper
cd {service-name}-service
./mvnw.sh run          # Run in dev mode
./mvnw.sh run prod     # Run in production mode
./mvnw.sh test         # Run tests
./mvnw.sh build        # Build application
./mvnw.sh clean        # Clean build artifacts
./mvnw.sh db-status    # Show database status
./mvnw.sh -h           # Show help
```

### Backend Quality Validation Script

The backend-level `mvnw.sh` script validates all quality guardrails:

```bash
# From backend/ directory
cd backend/

# Validate changed services (git diff)
./mvnw.sh                      # Default: changeset mode
./mvnw.sh changeset            # Same as above

# Validate all services
./mvnw.sh all

# Validate specific service
./mvnw.sh service order-service
./mvnw.sh service user-service

# Quick validation (tests + coverage only)
./mvnw.sh quick all

# Full validation (includes security scan)
./mvnw.sh full all

# Cursor pre-completion validation
./mvnw.sh cursor

# Show help
./mvnw.sh help
```

**Quality Checks Performed:**
- ✅ Compilation
- ✅ Unit tests
- ✅ Code coverage (≥80% line, ≥75% branch)
- ✅ Checkstyle
- ✅ SpotBugs static analysis
- ✅ PMD code analysis
- ✅ OWASP dependency check (full mode only)

**Reports Generated:**
- `target/quality-reports/LATEST_QUALITY_REPORT.md` - Summary
- `target/quality-reports/quality-report-<timestamp>.txt` - Detailed log
- `target/quality-reports/<service>-test.log` - Test output

### Build All Services
```bash
mvn clean install
```

### Build Specific Service
```bash
cd {service-name}-service
mvn clean install
```

### Run Tests
```bash
mvn clean test
```

### Check Code Coverage
```bash
mvn clean test
# Open target/site/jacoco/index.html
```

### Run Service Locally
```bash
cd {service-name}-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Code Formatting
```bash
# Check formatting
mvn spotless:check

# Apply formatting
mvn spotless:apply
```

## Testing

### Running Unit Tests
```bash
mvn test
```

### Running Integration Tests
```bash
mvn verify
```

### Running Tests with Coverage
```bash
mvn clean test jacoco:report
```

### Coverage Reports
- Location: `target/site/jacoco/index.html`
- Minimum requirement: 80% line coverage, 75% branch coverage
- Build will fail if below threshold

## Configuration

Each service has environment-specific configuration:

- `application.yml` - Common configuration
- `application-dev.yml` - Development
- `application-staging.yml` - Staging
- `application-prod.yml` - Production

### Environment Variables

```bash
# MongoDB
SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/foodai

# PostgreSQL
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/foodai
SPRING_DATASOURCE_USERNAME=foodai
SPRING_DATASOURCE_PASSWORD=password

# Redis
SPRING_REDIS_HOST=localhost
SPRING_REDIS_PORT=6379

# Kafka
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Service Port
SERVER_PORT=8080
```

## API Documentation

Each service exposes OpenAPI documentation:

- **Swagger UI**: `http://localhost:{port}/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:{port}/api-docs`

## Monitoring & Health

All services expose Spring Boot Actuator endpoints:

- **Health**: `GET /actuator/health`
- **Metrics**: `GET /actuator/metrics`
- **Prometheus**: `GET /actuator/prometheus`

## Event-Driven Architecture

### Event Naming Convention
```
{domain}.{entity}.{event-type}

Examples:
- user.registered
- order.created
- payment.completed
- rider.assigned
```

### Event Format
```json
{
  "eventId": "uuid",
  "eventType": "order.created",
  "timestamp": "2025-01-15T10:30:00Z",
  "version": "1.0",
  "payload": {
    /* domain-specific data */
  },
  "metadata": {
    "userId": "user-123",
    "correlationId": "correlation-123",
    "source": "order-service"
  }
}
```

## CI/CD Pipeline

### Pre-commit Checks (Local)
1. Code formatting (Spotless)
2. Static analysis (Checkstyle)
3. Unit tests
4. Code coverage check

### CI Pipeline (Jenkins)
1. Build (Maven)
2. Unit tests
3. Integration tests
4. Code quality (SonarQube)
5. Security scan (Snyk, Trivy)
6. Build Docker image
7. Push to Artifact Registry
8. Deploy to dev (ArgoCD)

### Quality Gates
- Code Coverage: >= 80%
- Code Smells: < 5
- Bugs: 0
- Security Vulnerabilities: 0 (Critical/High)
- Technical Debt: < 5%

## Development Workflow

### 1. Create Feature Branch
```bash
git checkout -b feature/{service-name}/{feature-name}
```

### 2. Generate Service Code (using Cursor)
- Use the prompt template
- Verify generated code follows standards
- Ensure all tests are generated

### 3. Run Tests
```bash
mvn clean test
```

### 4. Check Coverage
```bash
mvn jacoco:report
# Open target/site/jacoco/index.html
# Ensure >= 80% coverage
```

### 5. Format Code
```bash
mvn spotless:apply
```

### 6. Run Quality Checks
```bash
mvn checkstyle:check
```

### 7. Commit Changes
```bash
git add .
git commit -m "feat({service}): {description}"
```

### 8. Push & Create PR
```bash
git push origin feature/{service-name}/{feature-name}
```

## Troubleshooting

### Build Failures

**Issue**: JaCoCo coverage below 80%
```bash
# Solution: Add more tests
# Check which classes need more coverage:
mvn jacoco:report
# Open target/site/jacoco/index.html
```

**Issue**: Checkstyle violations
```bash
# Solution: Auto-fix formatting
mvn spotless:apply
```

**Issue**: Tests failing
```bash
# Solution: Run tests in verbose mode
mvn test -X
# Check logs for details
```

### Docker Issues

**Issue**: MongoDB not starting
```bash
# Solution: Check if port is already in use
docker ps
docker stop <container-id>
```

**Issue**: Kafka not connecting
```bash
# Solution: Ensure Kafka is running
docker ps | grep kafka
# Restart Kafka if needed
```

## Best Practices

1. **Always** write tests before marking task complete
2. **Always** ensure 80%+ code coverage
3. **Always** use structured logging with correlation ID
4. **Always** validate inputs at controller layer
5. **Always** handle exceptions properly (use global exception handler)
6. **Never** hardcode secrets or credentials
7. **Never** expose stack traces in production
8. **Never** skip tests or reduce coverage threshold
9. **Use** soft delete instead of hard delete
10. **Use** pagination for list endpoints

## Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Domain-Driven Design](https://www.domainlanguage.com/ddd/)
- [Microservices Patterns](https://microservices.io/patterns/)
- [OpenAPI Specification](https://swagger.io/specification/)
- [Testcontainers](https://www.testcontainers.org/)

## Support

For questions or issues:
- Check `/.cursor/rules` for coding standards
- Check `/DOMAIN_MODEL.md` for domain details
- Check `/SERVICE_TEMPLATE_SPEC.md` for templates
- Check `@architecture.html` for architecture details
- Check `@project-plan.html` for development plan

## License

Copyright © 2025 FoodAI Platform. All rights reserved.


