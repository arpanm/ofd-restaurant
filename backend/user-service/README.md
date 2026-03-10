# User Service

> User Management Service for FoodAI Platform (restaurant app backend)

## Overview

The User Service manages user-related functionality for the restaurant app:
- User registration and profile management
- Address management
- Dietary preferences and restrictions
- Diet planning and meal scheduling
- Customer feedback and support tickets
- AI personalization settings
- Search history and recommendations

**In this repo**: Loyalty (points, tiers, achievements, badges, challenges) has been **removed**. The service is also **called by restaurant-service** after onboarding submit to create the owner user (POST /api/v1/users) so the owner can log in.

## Features

### User Management
- User registration with phone/email
- Profile CRUD operations
- Multiple address management (home, work, etc.)
- Favorite restaurants and menu items
- Phone and email verification

### Diet Planning
- Custom and AI-generated diet plans
- Meal scheduling with nutritional targets
- Auto-ordering for scheduled meals
- Progress tracking
- Reminder settings

### Customer Feedback
- Order reviews and ratings
- Support tickets
- Complaint management
- Sentiment analysis
- Compensation handling

### AI Personalization
- Recommendation preferences
- Chat assistant settings
- Learning preferences
- Privacy controls

## API Endpoints

### Users
```
POST   /api/v1/users                                    - Create user
GET    /api/v1/users/{id}                               - Get user by ID
GET    /api/v1/users/by-phone/{phone}                   - Get user by phone
GET    /api/v1/users/by-email?email=...                 - Get user by email
GET    /api/v1/users                                    - List all users (paginated)
GET    /api/v1/users/search?query=...                   - Search users
PUT    /api/v1/users/{id}                               - Update user
DELETE /api/v1/users/{id}?deletedBy=...                 - Soft delete user
```

### Addresses
```
GET    /api/v1/users/{userId}/addresses                 - Get user addresses
POST   /api/v1/users/{userId}/addresses                 - Add address
PUT    /api/v1/users/{userId}/addresses/{addressId}     - Update address
DELETE /api/v1/users/{userId}/addresses/{addressId}     - Remove address
```

### Favorites
```
POST   /api/v1/users/{userId}/favorites/restaurants/{restaurantId}   - Add favorite
DELETE /api/v1/users/{userId}/favorites/restaurants/{restaurantId}   - Remove favorite
```

### Verification
```
POST   /api/v1/users/{userId}/verify-email              - Verify email
POST   /api/v1/users/{userId}/verify-phone              - Verify phone
```

### Loyalty
```
POST   /api/v1/loyalty/accounts                         - Create loyalty account
GET    /api/v1/loyalty/accounts/user/{userId}           - Get by user ID
GET    /api/v1/loyalty/accounts/{id}                    - Get by account ID
POST   /api/v1/loyalty/accounts/user/{userId}/earn      - Earn points
POST   /api/v1/loyalty/accounts/user/{userId}/redeem    - Redeem points
POST   /api/v1/loyalty/referrals/process                - Process referral
GET    /api/v1/loyalty/accounts/tier/{tier}             - Get accounts by tier
GET    /api/v1/loyalty/leaderboard                      - Get leaderboard
```

## Technology Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.2.x
- **Database**: MongoDB
- **Cache**: Redis
- **Messaging**: Apache Kafka
- **API Docs**: OpenAPI 3.0 (Springdoc)

## Database Collections

- `users` - User profiles and preferences
- `loyalty_accounts` - Loyalty points, tiers, achievements
- `diet_plans` - Diet plans and scheduled meals
- `feedback` - Customer feedback and support tickets

## Events Published

| Topic | Description |
|-------|-------------|
| `user.registered` | User account created |
| `user.profile.updated` | User profile updated |
| `user.deleted` | User account deleted |
| `loyalty.points.earned` | Points earned |
| `loyalty.points.redeemed` | Points redeemed |
| `loyalty.tier.changed` | Tier level changed |
| `loyalty.referral.processed` | Referral bonus awarded |

## Events Consumed

| Topic | Action |
|-------|--------|
| `order.completed` | Award loyalty points |
| `order.reviewed` | Process order review |

## Configuration

### Environment Variables

```bash
# MongoDB
SPRING_DATA_MONGODB_URI=mongodb://localhost:27017/foodai_user

# Redis
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379

# Kafka
SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Server
SERVER_PORT=8081
```

## Running Locally

### Prerequisites
- Java 21
- MongoDB 7.x
- Redis 7.x
- Apache Kafka

### Start Service
```bash
cd user-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Access Swagger UI
```
http://localhost:8081/swagger-ui.html
```

## Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn verify
```

### Check Coverage
```bash
mvn clean test jacoco:report
# Open target/site/jacoco/index.html
```

## Domain Model

### User (Aggregate Root)
- Profile information (name, email, phone, avatar)
- Multiple addresses with geolocation
- Dietary preferences and restrictions
- Notification preferences
- AI personalization settings
- Saved payment methods
- Favorite restaurants and items

### LoyaltyAccount (Aggregate Root)
- Points balance (current, lifetime earned/redeemed)
- Tier status and benefits
- Achievements and badges
- Active challenges
- Referral tracking
- Streak information

### DietPlan (Aggregate Root)
- Plan details (type, duration, targets)
- Scheduled meals with nutritional info
- Auto-order settings
- Progress tracking
- Reminder settings

### Feedback (Aggregate Root)
- Ratings (overall, food, delivery, packaging, value)
- Comments and responses
- Status and priority
- Sentiment analysis
- Compensation tracking

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     User Service                            │
├─────────────────────────────────────────────────────────────┤
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │    User      │  │   Loyalty    │  │  Diet Plan   │      │
│  │  Controller  │  │  Controller  │  │  Controller  │      │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
│         │                 │                 │               │
│  ┌──────┴───────┐  ┌──────┴───────┐  ┌──────┴───────┐      │
│  │    User      │  │   Loyalty    │  │  Diet Plan   │      │
│  │   Service    │  │   Service    │  │   Service    │      │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
│         │                 │                 │               │
│  ┌──────┴───────┐  ┌──────┴───────┐  ┌──────┴───────┐      │
│  │    User      │  │   Loyalty    │  │  Diet Plan   │      │
│  │  Repository  │  │  Repository  │  │  Repository  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
├─────────────────────────────────────────────────────────────┤
│              MongoDB          Redis          Kafka          │
└─────────────────────────────────────────────────────────────┘
```

## Feature Mapping

This service covers the following feature specifications:

| Feature Spec | Covered Domains |
|-------------|-----------------|
| USER_ONBOARDING.md | User registration, profile, addresses |
| CONSUMER_INTERFACE.md | User preferences, favorites |
| CHECKOUT_FLOW.md | Addresses, payment methods |
| AI_CHAT_BUYING_JOURNEY.md | Personalization settings |
| DIET_PLANNER.md | Diet plans, meal scheduling |
| DIET_PLANNER_ORDER_TRACKING.md | Diet plan progress |
| FOOD_CUSTOMIZATION_INSTRUCTIONS.md | Default customizations |
| AI_PERSONALIZATION_FEATURES.md | AI settings, preferences |
| LOYALTY_GAMIFICATION.md | Points, tiers, achievements |
| MAP_BASED_ORDER_TRACKING.md | Address geolocation |
| CUSTOMER_FEEDBACK_TICKETING.md | Feedback, support tickets |
| SEARCH_RECOMMENDATION_ENGINE.md | Search history |

## License

Copyright © 2025 FoodAI Platform. All rights reserved.

