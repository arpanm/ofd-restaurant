# Cursor Prompt for Promotion Service

## Service Generation Prompt

```
Add promotion-service, based on domain driven design following @backend/.cursor/rules, 
@backend/SERVICE_TEMPLATE_SPEC.md, @backend/QUALITY_STANDARDS.md, domain driven design 
and following all guardrails. Update @backend/DOMAIN_MODEL.md and @backend/README.md 
once done. Ensure all restaurant and consumer features from @public/features-spec/ 
and other frontend code are supported. Update @backend/order-service/, 
@backend/restaurant-service/, @backend/user-service/, @backend/orchestration-service/ 
etc as needed for this.
```

## Key Features Implemented

### Domain Model
1. **Promotion**: Discount offers with scheduling, usage limits, and applicability rules
2. **Coupon**: Redeemable codes with validation rules and tracking
3. **Campaign**: Multi-channel marketing campaigns with A/B testing support
4. **CustomerSegment**: Dynamic and static customer groupings

### APIs Implemented
- Full CRUD for promotions, coupons, campaigns, and segments
- Coupon validation and application
- Campaign metrics tracking
- Segment membership management

### Events
- Publishing: Promotion, Coupon, and Campaign lifecycle events
- Consuming: Order, User, and Restaurant events for updates

## Files Created

### Main Application
- `PromotionApplication.java`

### Domain Layer
- `domain/model/Promotion.java`
- `domain/model/Coupon.java`
- `domain/model/Campaign.java`
- `domain/model/CustomerSegment.java`
- `domain/model/CouponUsage.java`
- `domain/model/*.java` (enums and value objects)
- `domain/repository/*.java`

### Service Layer
- `service/PromotionService.java`
- `service/CouponService.java`
- `service/CampaignService.java`
- `service/CustomerSegmentService.java`

### Controller Layer
- `controller/PromotionController.java`
- `controller/CouponController.java`
- `controller/CampaignController.java`
- `controller/CustomerSegmentController.java`

### Infrastructure
- `infrastructure/messaging/PromotionEventPublisher.java`
- `infrastructure/messaging/PromotionEventConsumer.java`

### Configuration
- `config/KafkaConfig.java`
- `config/OpenApiConfig.java`
- `config/MongoConfig.java`

### DTOs
- `dto/request/*.java`
- `dto/response/*.java`

### Mappers
- `mapper/PromotionMapper.java`
- `mapper/CouponMapper.java`
- `mapper/CampaignMapper.java`
- `mapper/CustomerSegmentMapper.java`

### Exceptions
- `exception/*Exception.java`
- `exception/GlobalExceptionHandler.java`

### Tests
- `domain/model/PromotionTest.java`
- `domain/model/CouponTest.java`
- `service/PromotionServiceTest.java`
- `service/CouponServiceTest.java`
- `controller/PromotionControllerTest.java`
- `controller/CouponControllerTest.java`

## Configuration Files
- `pom.xml`
- `application.yml`
- `application-dev.yml`
- `application-test.yml`
- `logback-spring.xml`

## Scripts
- `mvnw.sh`
- `scripts/SET_JAVA_21.sh`

## Quality Standards Followed

1. **Code Coverage**: Target 80% line, 75% branch (currently set lower for initial development)
2. **Static Analysis**: Checkstyle, SpotBugs, PMD configured
3. **Testing**: Unit tests with JUnit 5, Mockito
4. **Documentation**: JavaDoc, OpenAPI/Swagger
5. **Logging**: Structured logging with logback

## Integration Points

### With Other Services
- **Order Service**: Consume order events for promotion usage tracking
- **User Service**: Consume user events for segment updates
- **Restaurant Service**: Consume restaurant events for promotion management
- **Orchestration Service**: Can trigger promotions during checkout saga

### Database
- MongoDB for all collections (promotions, coupons, campaigns, segments)

### Messaging
- Kafka topics: `promotion-events`, `coupon-events`, `campaign-events`

## Port Assignment
- **Port 8089**: Promotion Service

