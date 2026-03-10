# Cursor AI Prompt for Menu Service Implementation

## Service Overview

**Service Name**: menu-service  
**Domain**: menu  
**Purpose**: Manage menu items, categories, pricing, availability, customizations, and nutritional information for restaurants  
**Database**: MongoDB  
**Port**: 8082

---

## Complete Implementation Prompt

Use this prompt with Cursor AI to generate the complete Menu Service:

```
Create a complete menu-service following the FoodAI microservice template defined in @backend/.cursor/rules and @backend/SERVICE_TEMPLATE_SPEC.md:

Service: menu-service
Domain: menu
Purpose: Manage restaurant menu items, categories, pricing, availability, customizations, and nutritional information
Database: MongoDB
Port: 8082

Key Entities:
1. MenuItem (Aggregate Root)
   - id, restaurantId, name, description, category
   - basePrice, images[], available, preparationTime, spiceLevel
   - Dietary flags: vegetarian, vegan, glutenFree, containsDairy, containsNuts, halal, jain
   - nutritionalInfo (VO): calories, protein, carbs, fat, fiber, sugar, sodium
   - customizations[] (VO): customization groups with options and pricing
   - availabilitySchedule (VO): time-based availability rules
   - dynamicPricingConfig (VO): peak/off-peak pricing, demand-based pricing
   - tags[], ingredients[], allergens[]
   - totalOrders, averageRating, reviewCount
   - status (enum): ACTIVE, INACTIVE, OUT_OF_STOCK, PENDING_APPROVAL, DISCONTINUED
   - createdAt, updatedAt, deleted

2. MenuCategory (Aggregate Root)
   - id, restaurantId, name, description
   - iconUrl, displayOrder, active
   - createdAt, updatedAt, deleted

Value Objects:
- NutritionalInfoVO: calories, protein, carbs, fat, fiber, sugar, sodium, etc.
- CustomizationVO: name, options[], required, multiSelect, minSelections, maxSelections
- CustomizationOptionVO: name, description, additionalCost, available
- AvailabilityScheduleVO: alwaysAvailable, availableDays[], startTime, endTime, seasonalDates
- DynamicPricingConfigVO: enabled, peakHourMultiplier, offPeakMultiplier, priceFloor, priceCeiling
- PriceTimeSlotVO: days[], startTime, endTime, multiplier

Business Logic (in Domain):
- MenuItem.calculateFinalPrice(customizations): Calculate price with customizations
- MenuItem.isAvailableNow(): Check real-time availability based on schedule
- MenuItem.updateRating(newRating): Aggregate ratings
- MenuItem.recordOrder(): Track order count
- MenuItem.markOutOfStock() / markInStock()
- DynamicPricingConfigVO.calculatePrice(): Apply dynamic pricing logic
- AvailabilityScheduleVO.isAvailableNow(): Time-based availability check

Generate complete implementation with:

1. Domain Layer (com.foodai.menu.domain.model)
   - MenuItem entity with complete business logic
   - MenuCategory entity
   - MenuItemStatus enum
   - All Value Objects with validation

2. Repository Layer (com.foodai.menu.domain.repository)
   - MenuItemRepository with queries:
     * findByRestaurantIdAndDeletedFalse
     * findByRestaurantIdAndCategoryAndDeletedFalse
     * findByRestaurantIdAndAvailableTrueAndDeletedFalse
     * findByRestaurantIdAndVegetarianAndDeletedFalse
     * searchByName (regex search)
     * findByRestaurantIdAndTagsContainingAndDeletedFalse
     * findTopSellingItems (sorted by totalOrders)
     * findHighlyRatedItems (rating >= threshold)
   - MenuCategoryRepository with standard queries

3. DTOs (com.foodai.menu.dto)
   Request DTOs:
   - CreateMenuItemRequest: All fields with Jakarta validation
   - UpdateMenuItemRequest: All fields with validation
   - UpdateAvailabilityRequest: availability flag
   - MenuItemSearchRequest: Search filters
   - CreateMenuCategoryRequest: Category fields
   - BulkMenuItemRequest: For bulk operations
   
   Response DTOs:
   - MenuItemResponse: Summary view
   - MenuItemDetailResponse: Complete view with all details
   - MenuCategoryResponse: Category data
   - MenuStatsResponse: Restaurant menu statistics

4. Mappers (com.foodai.menu.mapper)
   - MenuItemMapper (MapStruct)
   - MenuCategoryMapper (MapStruct)

5. Service Layer (com.foodai.menu.service)
   - MenuItemService: CRUD + business operations
   - MenuCategoryService: Category management
   - MenuPricingService: Dynamic pricing calculations
   - MenuAvailabilityService: Availability checking
   - MenuSearchService: Advanced search and filtering

6. Controller Layer (com.foodai.menu.controller)
   - MenuItemController: Complete CRUD APIs
     * POST /api/v1/menu-items - Create
     * GET /api/v1/menu-items/{id} - Get by ID
     * GET /api/v1/menu-items/restaurant/{restaurantId} - Get by restaurant
     * PUT /api/v1/menu-items/{id} - Update
     * PATCH /api/v1/menu-items/{id}/availability - Update availability
     * DELETE /api/v1/menu-items/{id} - Soft delete
     * POST /api/v1/menu-items/search - Search with filters
     * GET /api/v1/menu-items/restaurant/{restaurantId}/top-selling - Top items
   - MenuCategoryController: Category CRUD
   - MenuPricingController: Pricing operations

7. Exception Handling (com.foodai.menu.exception)
   - MenuItemNotFoundException
   - MenuCategoryNotFoundException
   - DuplicateMenuItemException
   - InvalidPriceException
   - InvalidAvailabilityException
   - GlobalExceptionHandler with @RestControllerAdvice
   - ApiErrorResponse

8. Configuration (com.foodai.menu.config)
   - MongoConfig: MongoDB configuration with auditing
   - KafkaConfig: Kafka producer/consumer
   - RedisConfig: Redis caching
   - OpenApiConfig: Swagger documentation

9. Resources
   - application.yml: Base configuration
   - application-dev.yml: Development config
   - application-test.yml: Test config
   - logback-spring.xml: Structured logging

10. Unit Tests (80%+ coverage MANDATORY)
    - MenuItemServiceTest
    - MenuCategoryServiceTest
    - MenuItemControllerTest
    - MenuCategoryControllerTest
    - MenuItemTest (domain validation)
    - MenuCategoryTest
    - MenuItemMapperTest
    - All tests with AAA pattern (Arrange-Act-Assert)

11. Integration Tests (Testcontainers)
    - MenuItemIntegrationTest
    - MenuCategoryIntegrationTest
    - MenuSearchIntegrationTest

Follow STRICTLY:
- Java 17 (NOT 21)
- Domain-Driven Design principles from @backend/.cursor/rules
- Layered architecture: Controller → Service → Domain → Repository
- CRUD API standards: POST, GET, PUT, DELETE with proper HTTP status codes
- Error handling: Global exception handler, standardized error responses
- Validation: Jakarta Bean Validation at DTO level, business validation in domain
- Logging: Structured logging with Logstash format, correlation IDs
- OpenAPI documentation: @Operation, @ApiResponse on all endpoints
- Soft delete: Use deleted boolean flag, never hard delete
- Pagination: All list endpoints must support pagination
- Auditing: @CreatedDate, @LastModifiedDate on all entities
- MapStruct: componentModel = "spring", unmappedTargetPolicy = ERROR

Events to Publish (Kafka):
- menu.item.created
- menu.item.updated
- menu.item.deleted
- menu.item.availability.changed
- menu.item.price.updated
- menu.category.created

DO NOT proceed until:
✅ All classes generated with complete implementation
✅ All unit tests written with 80%+ coverage
✅ All integration tests written
✅ ALL tests pass
✅ Code follows exact patterns from SERVICE_TEMPLATE_SPEC.md
✅ JavaDoc on all public classes and methods
✅ OpenAPI documentation complete
✅ Error handling follows global exception handler pattern
✅ Structured logging with correlation IDs
✅ No hardcoded values, use configuration
```

---

## Key Domain Features

### MenuItem Domain Logic

The MenuItem aggregate root contains rich business logic:

1. **Price Calculation**
   ```java
   public BigDecimal calculateFinalPrice(List<String> selectedCustomizations) {
       // Base price + customization costs
   }
   ```

2. **Availability Checking**
   ```java
   public boolean isAvailableNow() {
       // Check availability schedule, time slots, seasonal dates
   }
   ```

3. **Rating Management**
   ```java
   public void updateRating(BigDecimal newRating) {
       // Aggregate ratings, update average
   }
   ```

4. **Dynamic Pricing**
   ```java
   // In DynamicPricingConfigVO
   public BigDecimal calculatePrice(basePrice, isPeakHour, demandLevel, inventoryLevel) {
       // Apply peak/off-peak, demand-based, inventory-based pricing
   }
   ```

### Repository Queries

Specialized queries for menu management:

- **Search**: `searchByName(restaurantId, nameRegex, pageable)`
- **Filtering**: By category, dietary preferences, tags, status
- **Analytics**: Top-selling, highly-rated items
- **Availability**: Find available items only

---

## API Examples

### Create Menu Item
```bash
POST /api/v1/menu-items
{
  "restaurantId": "rest123",
  "name": "Butter Chicken",
  "description": "Rich and creamy North Indian curry",
  "category": "Main Course",
  "basePrice": 350.00,
  "preparationTime": 25,
  "spiceLevel": 3,
  "vegetarian": false,
  "nutritionalInfo": {
    "calories": 450,
    "protein": 28,
    "carbohydrates": 35,
    "fat": 22
  },
  "customizations": [
    {
      "name": "Spice Level",
      "options": [
        {"name": "Mild", "additionalCost": 0},
        {"name": "Medium", "additionalCost": 0},
        {"name": "Extra Spicy", "additionalCost": 0}
      ],
      "required": true,
      "multiSelect": false
    }
  ]
}
```

### Search Menu Items
```bash
POST /api/v1/menu-items/search
{
  "restaurantId": "rest123",
  "category": "Main Course",
  "vegetarian": true,
  "minPrice": 200,
  "maxPrice": 500,
  "available": true
}
```

---

## Testing Requirements

### Minimum Coverage: 80%

**Unit Tests** (with Mockito):
- Test each service method in isolation
- Mock all dependencies
- Test happy paths and error cases
- Test validation logic

**Integration Tests** (with Testcontainers):
- Test complete API flows
- Use real MongoDB container
- Test with REST Assured
- Verify data persistence

**Domain Tests**:
- Test business logic in entities
- Test value object validation
- Test domain methods

### Example Test
```java
@Test
void shouldCreateMenuItem_whenValidInput() {
    // Arrange
    CreateMenuItemRequest request = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Butter Chicken")
        .basePrice(new BigDecimal("350"))
        .build();
    
    // Act
    MenuItemResponse response = service.create(request);
    
    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getName()).isEqualTo("Butter Chicken");
    verify(repository).save(any(MenuItem.class));
}
```

---

## Validation Rules

### MenuItem Validation
- ✅ name: 2-100 characters, required
- ✅ basePrice: > 0, required
- ✅ preparationTime: 5-120 minutes
- ✅ spiceLevel: 0-5
- ✅ restaurantId: required
- ✅ category: required
- ✅ customizations: at least one option per customization

### Business Rules
- ✅ Cannot mark available if restaurant is inactive
- ✅ Price floor cannot exceed price ceiling
- ✅ Customization groups must have at least one option
- ✅ Peak hour multiplier must be positive
- ✅ Availability schedule times must be valid

---

## Error Handling

All exceptions follow the global exception handler pattern:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
  
  @ExceptionHandler(MenuItemNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleNotFound(MenuItemNotFoundException ex) {
    // Return 404 with standardized error response
  }
  
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    // Return 400 with field-level validation errors
  }
}
```

---

## Configuration

### MongoDB Indexes
- Compound index: (restaurantId, name) - unique
- Index: restaurantId
- Index: category
- Index: tags

### Caching Strategy
- Cache menu items by restaurant (TTL: 1 hour)
- Cache categories by restaurant (TTL: 24 hours)
- Invalidate on create/update/delete

### Event Publishing
- Publish to Kafka on all CRUD operations
- Include correlation ID in all events
- Use structured event format

---

## Success Criteria

The implementation is complete when:

1. ✅ All 71 files are implemented
2. ✅ Code compiles without errors
3. ✅ ALL tests pass (unit + integration)
4. ✅ Code coverage >= 80%
5. ✅ Swagger UI accessible at http://localhost:8082/swagger-ui.html
6. ✅ Service starts successfully
7. ✅ All endpoints work as documented
8. ✅ No SonarQube violations
9. ✅ Follows ALL guardrails from @backend/.cursor/rules

---

## Quick Start Commands

```bash
# Build
cd backend/menu-service
mvn clean install

# Run tests
mvn test

# Check coverage
mvn jacoco:report
# Open target/site/jacoco/index.html

# Run service
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Access Swagger
open http://localhost:8082/swagger-ui.html
```

---

This prompt ensures the Menu Service follows the exact same patterns, standards, and quality as all other FoodAI microservices.

