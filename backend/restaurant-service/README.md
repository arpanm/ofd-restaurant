# Restaurant Service

**Version**: 1.0.0  
**Port**: 8081  
**Database**: MongoDB  
**Message Broker**: Kafka

---

## 🎯 New Developer? Start Here!

```bash
# 3 commands to get running:
cd backend/restaurant-service
chmod +x *.sh scripts/*.sh
./mvnw.sh
```

**That's it!** The application starts automatically with:
- ✅ Java 21 configured
- ✅ MongoDB running
- ✅ 6 test restaurants loaded
- ✅ API available at http://localhost:8081

**Test it:**
```bash
curl -X POST http://localhost:8081/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{"deliveryPincode": "560095"}'
```

---

## 🚀 Quick Links

- [Quick Start (3 steps)](#quick-start-recommended-) - Get started in 10 seconds
- [Mock Data](#mock-data) - Test with pre-loaded restaurant data
- [API Examples](#testing-with-mock-data) - Sample API calls
- [Troubleshooting](#troubleshooting) - Common issues and solutions
- [Scripts & Tools](#scripts-and-tools) - Helper scripts reference

**📚 External Guides:**
- [cursor-md/QUICK_START.md](cursor-md/QUICK_START.md) - Detailed quick start
- [cursor-md/QUICK_REFERENCE.md](cursor-md/QUICK_REFERENCE.md) - Command cheat sheet
- [cursor-md/MVNW_GUIDE.md](cursor-md/MVNW_GUIDE.md) - Maven wrapper guide
- [cursor-md/MOCK_DATA_GUIDE.md](cursor-md/MOCK_DATA_GUIDE.md) - Test data reference

---

## Overview

The Restaurant Service is a comprehensive microservice for managing restaurant onboarding, multi-outlet operations, contracts, serviceability, TAT calculation, and penalty management for the FoodAI platform.

## Key Features

### ✅ Multi-Ownership Management
- Support for multiple owners per restaurant with ownership percentages
- Owner roles: PRIMARY_OWNER, CO_OWNER, PARTNER, INVESTOR
- Automatic validation that ownership percentages sum to 100%

### ✅ Multi-Outlet Support
- Restaurant brands can have multiple outlets (chain/franchise model)
- Independent outlet approval workflows
- Outlet-level status management (6 statuses)
- Per-outlet configuration for serviceability and TAT

### ✅ Multiple Contact Management
- Brand-level and outlet-level contacts
- Contact types: PRIMARY, SECONDARY, EMERGENCY, MANAGER, SUPPORT
- Support for alternate phone numbers and designations

### ✅ Comprehensive Contract Management
- Platform fee (percentage/fixed with tiered slabs)
- Delivery fee configuration (who pays: restaurant/customer/split/platform)
- Payment gateway fee structure
- Distance-based delivery fee slabs

### ✅ Automated Penalty System
- **Rating-based penalties**: Auto-deduction based on rating thresholds
- **Review penalties**: Tracking negative reviews with threshold-based actions
- **Cancellation penalties**: Escalating penalties for excessive cancellations
- **Pricing violation penalties**: Unauthorized price increases, MRP violations
- **Quality penalties**: Food quality, hygiene, packaging issues
- **Delay penalties**: TAT violation with per-minute charges
- Penalty dispute and waiver workflows

### ✅ Serviceability Management
- Pincode-based serviceability (explicit list per outlet)
- Radius-based serviceability
- Serviceability rules (inventory check, capacity check, max orders/hour)
- Excluded pincodes support

### ✅ Dynamic TAT Calculation
- Base preparation time + Order queue + Peak hours
- Rider availability + Batching potential + Delivery distance
- Configurable per outlet with buffer time

### ✅ Complete Audit Trail
- 5 History collections (Restaurant, Outlet, Contract, Serviceability, Penalty)
- Field-level change tracking
- CreatedBy, UpdatedBy, DeletedBy for all entities
- Change reasons and source IP tracking
- Contract changes require approval and may need re-signing

### ✅ Event-Driven Architecture
- 30+ Kafka events for restaurant, outlet, serviceability, TAT, contract, owner, contact, and penalty operations
- Asynchronous event publishing for downstream services

### ✅ Onboarding draft (save/resume by step)
- **Create draft**: `POST /api/v1/restaurants/onboarding/draft` → returns `draftId` for frontend to store (e.g. localStorage)
- **Get draft**: `GET /api/v1/restaurants/onboarding/draft/{draftId}` → prefill form for resume
- **Save draft**: `PATCH /api/v1/restaurants/onboarding/draft/{draftId}` → save current step and form data (partial)
- **Submit from draft**: `POST /api/v1/restaurants/onboarding/submit` (body: `{ draftId, createdBy? }`) → creates restaurant + first outlet, marks draft SUBMITTED, then runs post-submit (owner user + verification)
- **Post-submit**: Creates owner user via **UserServiceClient** (HTTP to user-service) so owner can log in; triggers **NotificationService** (stub implementation logs “would send email/SMS”; replace with real provider for production)

## Technology Stack

- **Framework**: Spring Boot 3.2.1
- **Language**: Java 21
- **Database**: MongoDB 7.0
- **Cache**: Redis
- **Message Broker**: Apache Kafka
- **API Documentation**: OpenAPI 3 (Swagger)
- **Mapping**: MapStruct 1.5.5
- **Testing**: JUnit 5, Mockito, Testcontainers
- **Logging**: Logstash Logback Encoder (structured JSON logs)
- **Metrics**: Micrometer + Prometheus

## Architecture

### Domain-Driven Design (DDD)

```
Restaurant (Aggregate Root)
├── Owners (List<OwnerVO>)
├── Contacts (List<ContactVO>)
├── Documents (List<DocumentVO>)
├── Contract (ContractVO)
│   ├── Platform Fee
│   ├── Delivery Fee
│   ├── Payment Gateway Fee
│   └── Penalties (7+ types)
└── Outlets (List<RestaurantOutlet>)
    ├── Address (AddressVO)
    ├── Contacts (List<ContactVO>)
    ├── Operating Hours (List<OperatingHoursVO>)
    ├── Serviceability Config
    └── TAT Config
```

### Layered Architecture

```
┌─────────────────────────────────┐
│   Controller Layer (REST APIs)  │
├─────────────────────────────────┤
│   Service Layer (Business Logic)│
├─────────────────────────────────┤
│   Domain Layer (Entities & VOs) │
├─────────────────────────────────┤
│   Repository Layer (Data Access)│
└─────────────────────────────────┘
```

## Project Structure

```
restaurant-service/
├── src/
│   ├── main/
│   │   ├── java/com/foodai/restaurant/
│   │   │   ├── RestaurantApplication.java
│   │   │   ├── controller/           # REST controllers (12 endpoint groups)
│   │   │   ├── service/              # Business logic (RestaurantService, OnboardingDraftService, NotificationService stub)
│   │   │   ├── client/               # UserServiceClient (calls user-service to create owner)
│   │   │   ├── domain/
│   │   │   │   ├── model/            # Entities, VOs, Enums (40+ classes)
│   │   │   │   ├── repository/       # MongoDB repositories (6 repos)
│   │   │   │   └── service/          # Domain services (validation, approval)
│   │   │   ├── dto/
│   │   │   │   ├── request/          # Request DTOs (20+ DTOs)
│   │   │   │   └── response/         # Response DTOs (15+ DTOs)
│   │   │   ├── mapper/               # MapStruct mappers
│   │   │   ├── config/               # Configuration classes
│   │   │   └── exception/            # Custom exceptions + Global handler
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-test.yml
│   │       └── logback-spring.xml
│   └── test/
│       └── java/com/foodai/restaurant/
│           ├── service/               # Unit tests
│           ├── controller/            # Controller tests
│           └── integration/           # Integration tests
└── pom.xml
```

## API Endpoints

### Onboarding (draft save/resume and submit)
- `POST   /api/v1/restaurants/onboarding/draft` - Create draft; returns `{ draftId }`
- `GET    /api/v1/restaurants/onboarding/draft/{draftId}` - Get draft for prefill/resume
- `PATCH  /api/v1/restaurants/onboarding/draft/{draftId}` - Save draft (step + form data)
- `POST   /api/v1/restaurants/onboarding` - Submit full payload (direct, no draft)
- `POST   /api/v1/restaurants/onboarding/submit` - Submit from draft (body: `{ draftId, createdBy? }`)

### Restaurant Management (Brand Level)
- `POST   /api/v1/restaurants` - Create restaurant
- `GET    /api/v1/restaurants/{id}` - Get restaurant by ID
- `GET    /api/v1/restaurants` - Get all restaurants (paginated)
- `GET    /api/v1/restaurants/search?query=` - Search restaurants
- `GET    /api/v1/restaurants/by-owner/{ownerId}` - Get by owner
- `PUT    /api/v1/restaurants/{id}` - Update restaurant
- `DELETE /api/v1/restaurants/{id}` - Soft delete
- `POST   /api/v1/restaurants/{id}/approve` - Approve restaurant

### Outlet Management
- `POST   /api/v1/restaurants/{restaurantId}/outlets` - Add outlet
- `GET    /api/v1/restaurants/{restaurantId}/outlets` - Get all outlets
- `GET    /api/v1/restaurants/{restaurantId}/outlets/{outletId}` - Get outlet by ID
- `PUT    /api/v1/restaurants/{restaurantId}/outlets/{outletId}` - Update outlet
- `DELETE /api/v1/restaurants/{restaurantId}/outlets/{outletId}` - Delete outlet
- `POST   /api/v1/restaurants/{restaurantId}/outlets/{outletId}/approve` - Approve outlet
- `POST   /api/v1/restaurants/{restaurantId}/outlets/{outletId}/reject` - Reject outlet
- `PATCH  /api/v1/restaurants/{restaurantId}/outlets/{outletId}/status` - Update status

**Total**: 47 API endpoints across 9 controller groups

## Setup & Installation

### Prerequisites

- **Java 21** (required)
- Maven 3.8+
- MongoDB 7.0+
- Redis 6.0+ (optional - for caching)
- Kafka 3.0+ (optional - for event publishing)

### Quick Start (Recommended) 🚀

**The fastest way to get started in 3 steps:**

```bash
# 1. Navigate to service directory
cd backend/restaurant-service

# 2. Make scripts executable (one-time)
chmod +x mvnw.sh scripts/*.sh

# 3. Start services
./scripts/manage-services.sh start

# 4. Run the application with auto-setup
./mvnw.sh
```

That's it! The Maven wrapper (`mvnw.sh`) automatically:
- ✅ Configures Java 21
- ✅ Checks MongoDB availability
- ✅ Loads mock data if database is empty
- ✅ Starts the application in dev mode

The service will start on `http://localhost:8081`.

> **⚠️ Important:** If you see build errors, this is usually because Java 25 is active instead of Java 21. The `mvnw.sh` script handles this automatically, but for manual commands run: `source ./scripts/SET_JAVA_21.sh`

**📚 Documentation:**
- [cursor-md/QUICK_REFERENCE.md](cursor-md/QUICK_REFERENCE.md) - Common commands cheat sheet
- [cursor-md/QUICK_START.md](cursor-md/QUICK_START.md) - Detailed quick start guide
- [cursor-md/MVNW_GUIDE.md](cursor-md/MVNW_GUIDE.md) - Complete Maven wrapper documentation

### Maven Wrapper Commands

The `mvnw.sh` script provides a unified interface for all development tasks:

```bash
# Run in different profiles
./mvnw.sh                # Run in dev mode (default)
./mvnw.sh run dev        # Development with auto-setup
./mvnw.sh run test       # Test environment
./mvnw.sh run prod       # Production (no mock data)

# Build and test
./mvnw.sh build          # Build application
./mvnw.sh test           # Run all tests
./mvnw.sh compile        # Compile only

# Database management
./mvnw.sh db-clean dev   # Clean database (interactive, with confirmation)

# Maintenance
./mvnw.sh clean          # Clean build artifacts
./mvnw.sh install        # Clean install

# Advanced options
./mvnw.sh run dev --skip-mongo-check    # Skip MongoDB check
./mvnw.sh run dev --skip-data-check     # Skip mock data loading
./mvnw.sh install --force-clean         # Install with tests

# Get help
./mvnw.sh --help
```

**What the script does automatically:**
1. 🔧 Detects and configures Java 21
2. 🗄️ Checks MongoDB availability (auto-starts if using Homebrew)
3. 📊 Loads mock data if database is empty (dev/test only)
4. 🔍 Detects port conflicts and offers to resolve
5. 🧹 Cleans database with interactive confirmation (`db-clean` command)
6. 🚀 Starts application with correct profile

**📖 Complete Documentation:** See [cursor-md/MVNW_GUIDE.md](cursor-md/MVNW_GUIDE.md) for comprehensive guide including:
- Configuration file parsing
- Database management (`db-clean` command)
- Environment-specific behavior
- IDE integration (IntelliJ, VS Code)
- CI/CD usage
- Advanced customization

**🧹 Database Management:** See [cursor-md/DB_CLEAN_FEATURE.md](cursor-md/DB_CLEAN_FEATURE.md) for complete `db-clean` command documentation

### Alternative: Manual Setup

If you prefer manual setup or need more control:

1. **Set Java 21**
```bash
source ./scripts/SET_JAVA_21.sh
```

2. **Start infrastructure (MongoDB, Kafka)**
```bash
./scripts/manage-services.sh start

# Or using Docker Compose
docker-compose up -d
```

3. **Load mock data (optional)**
```bash
./scripts/load-mock-data.sh
```

4. **Build the project**
```bash
mvn clean install
```

5. **Run the application**
```bash
# Development mode
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Production mode
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### API Documentation

Once the service is running, access:
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI Docs**: http://localhost:8081/api-docs

### Health Check

```bash
curl http://localhost:8081/actuator/health
```

## Mock Data

The application automatically loads mock restaurant data when the database is empty (dev/test profiles only).

**Mock Data Summary:**
- **6 Restaurants** with 7 outlets total
- **Coverage**: Bangalore city (Koramangala, Indiranagar, Jayanagar, Whitefield, MG Road, HSR Layout)
- **Cuisines**: Italian, Indian, Chinese, American, Continental, Multi-cuisine
- **Price Range**: ₹400 - ₹2500 for two
- **Ratings**: 4.0⭐ - 4.7⭐

### Sample Restaurants

| Restaurant | Cuisine | Price/2 | Rating | Veg Only | Pincodes |
|------------|---------|---------|--------|----------|----------|
| **Bella Italia** | Italian, Mediterranean | ₹1200 | 4.5⭐ | No | 560095, 560038 |
| **Sattvik Bhavan** | Indian (N/S) | ₹600 | 4.3⭐ | **Yes** | 560041 |
| **Dragon Wok** | Chinese, Asian, Thai | ₹900 | 4.4⭐ | No | 560066 |
| **Quick Bites** | Fast Food, American | ₹400 | 4.0⭐ | No | 560001 |
| **The Royal Feast** | Continental, Fine Dining | ₹2500 | 4.7⭐ | No | 560001 |
| **Fusion Kitchen** | Multi-Cuisine | ₹800 | 4.2⭐ | No | 560102 |

### Managing Mock Data

```bash
# Clean database (recommended - interactive with confirmation)
./mvnw.sh db-clean dev
# - Shows current document count
# - Asks for confirmation
# - Deletes all restaurants
# - Optionally reloads mock data

# Load mock data manually
./scripts/load-mock-data.sh

# Verify data
mongosh foodai_restaurant_dev --eval "db.restaurants.countDocuments()"

# Clear data manually (no confirmation - use with caution!)
mongosh foodai_restaurant_dev --eval "db.restaurants.deleteMany({})"
```

### Testing with Mock Data

**Search by Pincode (Koramangala):**
```bash
curl -X POST http://localhost:8081/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{"deliveryPincode": "560095"}'
```

**Budget Dining:**
```bash
curl -X POST http://localhost:8081/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{"deliveryPincode": "560001", "budgetType": "BUDGET"}'
```

**Vegetarian Only:**
```bash
curl -X POST http://localhost:8081/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{"deliveryPincode": "560041", "vegetarianOnly": true}'
```

**Italian Cuisine:**
```bash
curl -X POST http://localhost:8081/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{"deliveryPincode": "560095", "cuisineTypes": ["Italian"]}'
```

**Premium Dining (High Rating):**
```bash
curl -X POST http://localhost:8081/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{"deliveryPincode": "560001", "budgetType": "PREMIUM", "minRating": 4.5}'
```

**📊 Full Mock Data Reference:** See [cursor-md/MOCK_DATA_GUIDE.md](cursor-md/MOCK_DATA_GUIDE.md) for complete test scenarios and data details.

## Testing

### Automated Testing

**Using Maven Wrapper (Recommended):**
```bash
# Run all tests
./mvnw.sh test

# Run tests with detailed reports and coverage
./scripts/run-tests.sh
```

**Manual Testing:**
```bash
# Run all tests
mvn clean test

# Run with code coverage
mvn clean test jacoco:report

# Run specific test
mvn test -Dtest="RestaurantSearchControllerTest"

# View coverage report
open target/site/jacoco/index.html
```

### Test Categories

- **Unit Tests**: Service layer with mocked dependencies (90%+ coverage)
- **Controller Tests**: MockMvc/integration tests for all endpoints (80%+ coverage)
- **Integration Tests**: End-to-end tests with MongoDB (70%+ coverage)
- **Repository Tests**: MongoDB repository tests with @DataMongoTest

**Coverage Requirement**: ≥80% (enforced by JaCoCo)

**Test Reports:** After running `./scripts/run-tests.sh`, view:
- Test Results: `target/surefire-reports/`
- Test HTML Report: `target/site/surefire-report.html`
- Coverage Report: `target/site/jacoco/index.html`

### Manual API Testing with Mock Data

Once the application is running with mock data loaded, you can test various scenarios:

**Test Scenarios:**

1. **Weekend Dining in Koramangala**
   - Pincode: 560095
   - Expected: Bella Italia (Italian, ₹1200/2, 4.5⭐)

2. **Quick Lunch in MG Road (Budget)**
   - Pincode: 560001, Budget: BUDGET
   - Expected: Quick Bites (Fast Food, ₹400/2)

3. **Vegetarian Dinner in Jayanagar**
   - Pincode: 560041, Filter: vegetarianOnly = true
   - Expected: Sattvik Bhavan (Pure veg, ₹600/2)

4. **Special Occasion in MG Road (Premium)**
   - Pincode: 560001, Budget: PREMIUM, Min Rating: 4.5
   - Expected: The Royal Feast (Fine dining, ₹2500/2, 4.7⭐)

5. **Chinese Cravings in Whitefield**
   - Pincode: 560066, Cuisine: Chinese
   - Expected: Dragon Wok (Asian fusion, ₹900/2)

6. **Multi-cuisine in HSR Layout**
   - Pincode: 560102, Cuisines: Indian, Chinese, Italian
   - Expected: Fusion Kitchen (₹800/2)

**📊 Complete Test Scenarios:** See [cursor-md/MOCK_DATA_GUIDE.md](cursor-md/MOCK_DATA_GUIDE.md) for detailed test cases, MongoDB queries, and Swagger UI examples.

### IDE Testing

**Cursor/IntelliJ/VS Code:**
1. Ensure services are running: `./scripts/manage-services.sh status`
2. Open test file (e.g., `RestaurantSearchControllerTest.java`)
3. Click ▶️ button next to test class or method
4. Tests run automatically with proper context

## Configuration

### MongoDB Configuration
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/foodai_restaurant_dev
      auto-index-creation: true
```

### Redis Configuration
```yaml
spring:
  data:
  redis:
      host: localhost
      port: 6379
      timeout: 2000ms
```

### Kafka Configuration
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

### Downstream: User Service (post-onboarding owner creation)
```yaml
services:
  user-service:
    url: ${USER_SERVICE_URL:http://localhost:8083}
```
Used by `UserServiceClient` to create owner user after onboarding submit. Verification email/SMS is handled by `NotificationService` (stub: `LoggingNotificationService`; replace with real provider for production).

## Caching Strategy

Redis caching is implemented for:
- **Restaurants**: TTL = 1 hour
- **Outlets**: TTL = 30 minutes
- **Contracts**: TTL = 2 hours
- **Serviceability**: TTL = 15 minutes

Cache is automatically invalidated on updates.

## Events Published (Kafka Topics)

### Restaurant Events
- `restaurant.registered` - Brand registration complete
- `restaurant.approved` - Brand approved by ops
- `restaurant.updated` - Brand details updated

### Outlet Events
- `outlet.created` - New outlet added
- `outlet.approved` - Outlet approved
- `outlet.activated` - Outlet activated
- `outlet.suspended` - Outlet suspended

### Serviceability Events
- `outlet.serviceability.updated` - Serviceability config changed
- `outlet.pincode.added` - New pincode added
- `outlet.pincode.removed` - Pincode removed

### Contract Events
- `contract.updated` - Contract terms updated
- `contract.signed` - Contract signed by owner

### Penalty Events
- `penalty.applied` - Penalty applied
- `penalty.disputed` - Penalty disputed by restaurant
- `penalty.waived` - Penalty waived by ops

**Total**: 30+ event types

## Logging

Structured JSON logging using Logstash Logback Encoder.

Example log entry:
```json
{
  "timestamp": "2025-01-15T10:30:00.123Z",
  "level": "INFO",
  "service": "restaurant-service",
  "logger": "com.foodai.restaurant.service.RestaurantService",
  "message": "Restaurant created successfully with ID: restaurant-123",
  "correlationId": "abc-123",
  "userId": "user-456"
}
```

## Monitoring & Metrics

Prometheus metrics exposed at: `/actuator/prometheus`

Key metrics:
- HTTP request duration and counts
- Database query performance
- Cache hit/miss rates
- Kafka producer/consumer metrics

## Error Handling

Global exception handler provides consistent error responses:

```json
{
  "timestamp": "2025-01-15T10:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Restaurant with ID 'xyz' not found",
  "path": "/api/v1/restaurants/xyz",
  "validationErrors": {}
}
```

## Validation Rules

- **Name**: 2-100 characters
- **Phone**: 10-digit Indian mobile number (starts with 6-9)
- **Email**: Valid email format
- **Pincode**: 6-digit Indian pincode
- **FSSAI**: 14-digit number
- **GST**: Valid GST format
- **Ownership**: Total must equal 100%

## Database Collections

### Main Collections
- `restaurants` - Restaurant aggregate root
- `onboarding_drafts` - Onboarding draft (save/resume by step); status DRAFT or SUBMITTED
- `restaurant_history` - Restaurant change history
- `outlet_history` - Outlet change history
- `contract_history` - Contract change history
- `serviceability_history` - Serviceability change history
- `penalty_history` - Penalty tracking

### Indexes

Compound indexes for optimal query performance:
- Restaurant by name + city
- Cuisine type + city
- Owner ID lookup
- Outlet status + deleted flag
- Serviceability pincode lookup

## Security

- All endpoints support authentication (add JWT/OAuth2 integration)
- Soft delete for data retention
- Audit trail for all critical operations
- IP tracking for changes

## Performance

- Caching with Redis for frequently accessed data
- MongoDB indexes for fast queries
- Asynchronous event publishing
- Connection pooling for MongoDB and Redis

## Development Guidelines

### Code Style
- Follow Google Java Style Guide
- Use Lombok for boilerplate reduction
- Write JavaDoc for public APIs

### Testing
- Write tests for all new features
- Maintain ≥80% code coverage
- Use meaningful test names with `@DisplayName`

### Git Workflow
- Feature branches from `main`
- Pull requests require review
- Run tests before committing

## Troubleshooting

### Common Issues

**1. Build Errors / Wrong Java Version**

The most common issue is using Java 25 instead of Java 21.

**Symptoms:**
- `java.lang.IllegalArgumentException: Unsupported class file major version 69`
- `BUILD FAILURE` during compilation
- JaCoCo errors

**Solutions:**
```bash
# Option 1: Use Maven wrapper (recommended - handles automatically)
./mvnw.sh

# Option 2: Set Java 21 manually
source ./scripts/SET_JAVA_21.sh
java -version  # Should show 21.0.6

# Option 3: Install Java 21 if not present
brew install openjdk@21
```

**2. MongoDB Not Running**

**Symptoms:**
- `MongoSocketOpenException`
- Application fails to start
- Tests fail

**Solutions:**
```bash
# Check if MongoDB is running
brew services list | grep mongodb

# Start MongoDB
./scripts/manage-services.sh start
# OR
brew services start mongodb-community@7.0

# Verify connection
mongosh --eval "db.version()"

# Skip check if testing without MongoDB
./mvnw.sh run dev --skip-mongo-check
```

**3. Port 8081 Already in Use**

**Symptoms:**
- `Port 8081 was already in use`
- Application fails to start

**Solutions:**
```bash
# Option 1: Use mvnw.sh (it will prompt to kill the process)
./mvnw.sh

# Option 2: Kill the process manually
lsof -ti:8081 | xargs kill -9

# Option 3: Find and stop the process
lsof -i:8081
kill -9 <PID>
```

**4. Mock Data Issues**

**Symptoms:**
- `cannot index parallel arrays`
- Duplicate data errors
- Empty search results
- Document structure mismatch (e.g., `No enum constant DocumentType.FSSAI_LICENSE`)

**Solutions:**
```bash
# Clean and reload data (recommended - interactive)
./mvnw.sh db-clean dev
# Confirm deletion: y
# Reload mock data: y

# Or manually clear and reload
mongosh foodai_restaurant_dev --eval "db.restaurants.deleteMany({})"
./scripts/load-mock-data.sh

# Drop problematic indexes
mongosh foodai_restaurant_dev --eval "
  db.restaurants.dropIndex('cuisine_city_idx');
  db.restaurants.dropIndex('outlet_pincode_idx');
"

# Skip data loading for testing
./mvnw.sh run dev --skip-data-check
```

**5. Tests Failing**

**Symptoms:**
- `ApplicationContext failure threshold exceeded`
- `No tests found`
- Coverage not met

**Solutions:**
```bash
# Ensure Java 21 and services are running
source ./scripts/SET_JAVA_21.sh
./scripts/manage-services.sh start

# Run tests with proper script
./scripts/run-tests.sh

# Clean and rebuild
./mvnw.sh clean
./mvnw.sh install --force-clean

# Run specific test
mvn test -Dtest="RestaurantSearchControllerTest"
```

**6. Kafka Connection Issues**

**Symptoms:**
- `TimeoutException` during startup
- Event publishing errors

**Solutions:**
```bash
# Start Kafka
./scripts/manage-services.sh start
# OR
brew services start kafka

# Verify Kafka is running
kafka-topics.sh --list --bootstrap-server localhost:9092

# For testing without Kafka, mock is already configured
```

### Quick Fixes

```bash
# Complete clean restart
./scripts/manage-services.sh stop
./mvnw.sh clean
./mvnw.sh db-clean dev    # Interactive database clean
./scripts/manage-services.sh start
./mvnw.sh

# Fast restart (skip all checks)
./mvnw.sh run dev --skip-mongo-check --skip-data-check

# Just clean database and restart
./mvnw.sh db-clean dev
./mvnw.sh run dev

# Check all services status
./scripts/manage-services.sh status
java -version
mvn -version
```

### Getting Help

1. **Check Documentation:**
   - [cursor-md/QUICK_START.md](cursor-md/QUICK_START.md) - Getting started guide
   - [cursor-md/MVNW_GUIDE.md](cursor-md/MVNW_GUIDE.md) - Maven wrapper troubleshooting
   - [cursor-md/MOCK_DATA_GUIDE.md](cursor-md/MOCK_DATA_GUIDE.md) - Mock data issues

2. **Run Diagnostics:**
   ```bash
   ./mvnw.sh --help
   ./scripts/manage-services.sh status
   ```

3. **View Logs:**
   ```bash
   # Application logs
   tail -f /tmp/spring-boot.log
   
   # Maven output
   mvn spring-boot:run -X  # Debug mode
   ```

## Scripts and Tools

The service includes several helper scripts for development workflow:

### Core Scripts

| Script | Purpose | Usage |
|--------|---------|-------|
| **mvnw.sh** | Maven wrapper with auto-setup | `./mvnw.sh [command] [profile]` |
| **run-tests.sh** | Run tests with detailed reports | `./scripts/run-tests.sh` |
| **manage-services.sh** | Start/stop MongoDB and Kafka | `./scripts/manage-services.sh [start\|stop\|status]` |
| **SET_JAVA_21.sh** | Set Java 21 environment | `source ./scripts/SET_JAVA_21.sh` |

### Data Scripts

| Script | Purpose | Usage |
|--------|---------|-------|
| **scripts/load-mock-data.sh** | Load mock restaurant data | `./scripts/load-mock-data.sh [db_name]` |
| **scripts/insert-mock-data.js** | MongoDB insertion script | (Called by load-mock-data.sh) |

### Typical Workflows

**Morning Startup:**
```bash
./scripts/manage-services.sh start
./mvnw.sh
```

**After Code Changes:**
```bash
./mvnw.sh compile
./mvnw.sh run dev --skip-data-check
```

**Before Committing:**
```bash
./scripts/run-tests.sh
./mvnw.sh build dev
```

**Clean Start:**
```bash
./scripts/manage-services.sh stop
./mvnw.sh clean
./mvnw.sh db-clean dev    # Clean database (interactive)
./scripts/manage-services.sh start
./mvnw.sh
```

**Fresh Database:**
```bash
./mvnw.sh db-clean dev    # Cleans and optionally reloads data
./mvnw.sh run dev         # Start with fresh data
```

### Documentation Files

| File | Content |
|------|---------|
| **cursor-md/QUICK_START.md** | 3-step quick start guide |
| **cursor-md/QUICK_REFERENCE.md** | Common commands cheat sheet (260+ lines) |
| **cursor-md/MVNW_GUIDE.md** | Complete Maven wrapper guide (520+ lines) |
| **cursor-md/DB_CLEAN_FEATURE.md** | Database clean command documentation |
| **cursor-md/MOCK_DATA_GUIDE.md** | Mock data reference and test scenarios |
| **cursor-md/CHANGES_SUMMARY.md** | Latest changes and updates |

## Future Enhancements

- [ ] Add more serviceability rules (weather, traffic)
- [ ] Implement ML-based TAT prediction
- [ ] Add real-time analytics dashboard
- [ ] Support for international restaurants
- [ ] Multi-language support for documents
- [ ] Advanced penalty dispute resolution workflow

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## License

Copyright © 2025 FoodAI Platform. All rights reserved.

## Contact

- **Team**: Backend Team
- **Email**: backend@foodai.com
- **Slack**: #restaurant-service

---

**Built with ❤️ for the FoodAI Platform**
