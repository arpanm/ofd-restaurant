# Menu Service

**Version**: 1.0.0  
**Port**: 8082  
**Database**: MongoDB  
**Message Broker**: Kafka

---

## 🎯 Overview

The Menu Service is a comprehensive microservice for managing menu items, categories, pricing, availability, customizations, and nutritional information for restaurants on the FoodAI platform.

### Key Features

✅ **Menu Item Management**
- CRUD operations for menu items
- Bulk import/export capabilities
- Category organization
- Image management
- Search and filtering

✅ **Pricing Management**
- Base pricing
- Dynamic pricing (peak/off-peak hours)
- Demand-based pricing
- Inventory-based pricing
- Price floor and ceiling controls

✅ **Customization System**
- Multi-level customizations
- Required/optional customizations
- Single/multi-select options
- Additional cost management

✅ **Nutritional Information**
- Comprehensive nutritional data
- Calories, protein, carbs, fat tracking
- Allergen information
- Dietary tags (vegetarian, vegan, gluten-free, etc.)

✅ **Availability Management**
- Real-time availability status
- Time-based availability (breakfast/lunch/dinner)
- Day-of-week scheduling
- Seasonal availability
- Out-of-stock handling

✅ **Performance Tracking**
- Order count tracking
- Rating aggregation
- Popularity metrics
- Sales analytics

---

## 📁 Project Structure

```
menu-service/
├── src/
│   ├── main/
│   │   ├── java/com/foodai/menu/
│   │   │   ├── MenuApplication.java
│   │   │   ├── config/
│   │   │   │   ├── MongoConfig.java
│   │   │   │   ├── KafkaConfig.java
│   │   │   │   └── RedisConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── MenuItemController.java
│   │   │   │   └── MenuCategoryController.java
│   │   │   ├── service/
│   │   │   │   ├── MenuItemService.java
│   │   │   │   ├── MenuCategoryService.java
│   │   │   │   └── MenuPricingService.java
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   ├── MenuItem.java
│   │   │   │   │   ├── MenuCategory.java
│   │   │   │   │   ├── MenuItemStatus.java
│   │   │   │   │   ├── NutritionalInfoVO.java
│   │   │   │   │   ├── CustomizationVO.java
│   │   │   │   │   ├── CustomizationOptionVO.java
│   │   │   │   │   ├── AvailabilityScheduleVO.java
│   │   │   │   │   ├── DynamicPricingConfigVO.java
│   │   │   │   │   └── PriceTimeSlotVO.java
│   │   │   │   └── repository/
│   │   │   │       ├── MenuItemRepository.java
│   │   │   │       └── MenuCategoryRepository.java
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   ├── CreateMenuItemRequest.java
│   │   │   │   │   ├── UpdateMenuItemRequest.java
│   │   │   │   │   ├── MenuItemSearchRequest.java
│   │   │   │   │   ├── CreateMenuCategoryRequest.java
│   │   │   │   │   └── BulkMenuItemRequest.java
│   │   │   │   └── response/
│   │   │   │       ├── MenuItemResponse.java
│   │   │   │       ├── MenuItemDetailResponse.java
│   │   │   │       ├── MenuCategoryResponse.java
│   │   │   │       └── MenuStatsResponse.java
│   │   │   ├── mapper/
│   │   │   │   ├── MenuItemMapper.java
│   │   │   │   └── MenuCategoryMapper.java
│   │   │   └── exception/
│   │   │       ├── MenuItemNotFoundException.java
│   │   │       ├── MenuCategoryNotFoundException.java
│   │   │       ├── DuplicateMenuItemException.java
│   │   │       ├── InvalidPriceException.java
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-test.yml
│   │       └── logback-spring.xml
│   └── test/
│       ├── java/com/foodai/menu/
│       │   ├── controller/
│       │   │   ├── MenuItemControllerTest.java
│       │   │   └── MenuCategoryControllerTest.java
│       │   ├── service/
│       │   │   ├── MenuItemServiceTest.java
│       │   │   └── MenuCategoryServiceTest.java
│       │   ├── domain/
│       │   │   └── model/
│       │   │       └── MenuItemTest.java
│       │   ├── integration/
│       │   │   └── MenuItemIntegrationTest.java
│       │   └── MenuApplicationTest.java
│       └── resources/
│           └── application-test.yml
├── pom.xml
└── README.md
```

---

## 🚀 Quick Start

### Prerequisites
- Java 21
- Maven 3.8+
- MongoDB 7.0+
- Kafka 3.6+
- Redis 7.0+

### Running Locally

```bash
# Clone repository
cd backend/menu-service

# Start dependencies (MongoDB, Kafka, Redis)
docker-compose up -d

# Build
mvn clean install

# Run
mvn spring-boot:run
```

The service will start on `http://localhost:8082`

---

## 📝 API Endpoints

### Menu Items

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST   | `/api/v1/menu-items` | Create menu item |
| GET    | `/api/v1/menu-items/{id}` | Get menu item by ID |
| GET    | `/api/v1/menu-items` | List all menu items |
| GET    | `/api/v1/menu-items/restaurant/{restaurantId}` | Get items by restaurant |
| PUT    | `/api/v1/menu-items/{id}` | Update menu item |
| PATCH  | `/api/v1/menu-items/{id}/availability` | Update availability |
| DELETE | `/api/v1/menu-items/{id}` | Delete menu item |
| POST   | `/api/v1/menu-items/search` | Search menu items |
| POST   | `/api/v1/menu-items/bulk` | Bulk create/update |
| GET    | `/api/v1/menu-items/restaurant/{restaurantId}/top-selling` | Top selling items |

### Menu Categories

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST   | `/api/v1/menu-categories` | Create category |
| GET    | `/api/v1/menu-categories/{id}` | Get category by ID |
| GET    | `/api/v1/menu-categories/restaurant/{restaurantId}` | Get categories by restaurant |
| PUT    | `/api/v1/menu-categories/{id}` | Update category |
| DELETE | `/api/v1/menu-categories/{id}` | Delete category |

### Swagger Documentation

- Swagger UI: `http://localhost:8082/swagger-ui.html`
- API Docs: `http://localhost:8082/api-docs`

---

## 🔧 Configuration

### application.yml

```yaml
spring:
  application:
    name: menu-service
  data:
    mongodb:
      uri: mongodb://localhost:27017/menu_db
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: menu-service
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
  redis:
    host: localhost
    port: 6379

server:
  port: 8082

logging:
  level:
    com.foodai: DEBUG
```

---

## 📊 Domain Model

### MenuItem Aggregate

```java
MenuItem {
  id: String
  restaurantId: String
  name: String
  description: String
  category: String
  basePrice: BigDecimal
  images: List<String>
  available: boolean
  preparationTime: Integer
  spiceLevel: Integer
  
  // Dietary
  vegetarian: boolean
  vegan: boolean
  glutenFree: boolean
  
  // Nutrition
  nutritionalInfo: NutritionalInfoVO
  
  // Customization
  customizations: List<CustomizationVO>
  
  // Availability
  availabilitySchedule: AvailabilityScheduleVO
  
  // Pricing
  dynamicPricingConfig: DynamicPricingConfigVO
  
  // Performance
  totalOrders: Integer
  averageRating: BigDecimal
}
```

### Key Value Objects

- **NutritionalInfoVO**: calories, protein, carbs, fat, etc.
- **CustomizationVO**: customization groups with options
- **CustomizationOptionVO**: individual customization options with pricing
- **AvailabilityScheduleVO**: time-based availability rules
- **DynamicPricingConfigVO**: peak/off-peak pricing, demand-based pricing

---

## 🧪 Testing

### Run Tests

```bash
# Unit tests
mvn test

# Integration tests
mvn verify

# With coverage report
mvn clean test jacoco:report
```

### Coverage Target

- Line Coverage: >= 80%
- Branch Coverage: >= 75%

### Test Structure

1. **Unit Tests**
   - Service layer tests with mocked repositories
   - Domain model validation tests
   - Mapper tests

2. **Integration Tests**
   - Full API tests with Testcontainers
   - MongoDB integration
   - Kafka event testing

---

## 🔐 Security

- API key validation at API Gateway
- Restaurant ownership validation
- Input sanitization
- SQL/NoSQL injection prevention

---

## 📈 Monitoring

### Health Check

```bash
curl http://localhost:8082/actuator/health
```

### Metrics

- Prometheus: `http://localhost:8082/actuator/prometheus`
- Metrics: `http://localhost:8082/actuator/metrics`

---

## 🚢 Deployment

### Docker Build

```bash
docker build -t foodai/menu-service:1.0.0 .
```

### Kubernetes

```bash
kubectl apply -f k8s/deployment.yml
```

---

## 📝 Events Published

- `menu.item.created` - When a new menu item is created
- `menu.item.updated` - When a menu item is updated
- `menu.item.deleted` - When a menu item is deleted
- `menu.item.availability.changed` - When availability changes
- `menu.item.price.updated` - When price is updated
- `menu.category.created` - When a category is created

---

## 📝 Events Consumed

- `restaurant.approved` - To activate menu items
- `order.created` - To track item orders
- `review.created` - To update ratings

---

## 🔄 Data Flow

### Create Menu Item Flow

1. Restaurant calls `POST /api/v1/menu-items`
2. Validate request (name, price, etc.)
3. Check for duplicates
4. Save to MongoDB
5. Publish `menu.item.created` event to Kafka
6. Cache in Redis
7. Return response

### Calculate Dynamic Price Flow

1. Get base price from MenuItem
2. Check current time (peak/off-peak)
3. Check demand level
4. Check inventory level
5. Apply multipliers
6. Apply floor/ceiling constraints
7. Return final price

---

## 🛠️ Tools & Technologies

- **Framework**: Spring Boot 3.2.1
- **Language**: Java 21
- **Database**: MongoDB 7.0
- **Cache**: Redis 7.0
- **Messaging**: Apache Kafka 3.6
- **Mapper**: MapStruct 1.5.5
- **Documentation**: SpringDoc OpenAPI 2.3.0
- **Logging**: Logback with Logstash encoder
- **Testing**: JUnit 5, Mockito, Testcontainers, REST Assured

---

## 📚 Additional Documentation

- [API Documentation](docs/API.md)
- [Domain Model](docs/DOMAIN_MODEL.md)
- [Testing Guide](docs/TESTING.md)
- [Deployment Guide](docs/DEPLOYMENT.md)

---

## 👥 Team

FoodAI Backend Team

---

## 📄 License

Proprietary - FoodAI Platform


