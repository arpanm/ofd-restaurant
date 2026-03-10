# FoodAI Platform - Domain Model & Bounded Contexts

## Domain-Driven Design Overview

This document defines all bounded contexts, aggregates, entities, value objects, and domain services for the FoodAI platform.

## Bounded Contexts (Microservices)

### 1. User Management Context (`user-service`) ✅ IMPLEMENTED

**Purpose**: Manage user accounts, profiles, preferences, diet plans, and feedback. (Loyalty removed in restaurant-app backend.) Called by restaurant-service to create owner user after onboarding.

**Aggregates**:
- **User** (Root)
  - UserId (Identity)
  - Email, Phone
  - FirstName, LastName, DisplayName
  - AvatarUrl, DateOfBirth, Gender
  - Addresses (collection of AddressVO)
  - DietaryPreferences (VO)
  - NotificationPreferences (VO)
  - PersonalizationSettings (VO)
  - PaymentMethods (collection of PaymentMethodVO)
  - CuisinePreferences (list)
  - FavoriteRestaurantIds, FavoriteMenuItemIds (lists)
  - RecentSearches (list)
  - ReferralCode, ReferredBy
  - Status, EmailVerified, PhoneVerified
  - TotalOrders, TotalSpent, AverageOrderValue
  - CreatedAt, UpdatedAt, DeletedAt

- **DietPlan** (Root)
  - PlanId (Identity)
  - UserId
  - Name, Description, PlanType
  - StartDate, EndDate, DurationDays
  - DailyCalorie/Protein/Carb/Fat Targets
  - Meals (collection of DietMealVO)
  - HealthGoals, Restrictions, PreferredCuisines
  - BudgetPerMeal, TotalBudget, ActualSpent
  - AutoOrderEnabled, AutoOrderLeadTime
  - Status (DRAFT, SCHEDULED, ACTIVE, PAUSED, COMPLETED)

- **Feedback** (Root)
  - FeedbackId (Identity)
  - UserId, OrderId
  - FeedbackType, Category
  - EntityType, EntityId
  - Title, Description
  - Ratings (overall, food, delivery, packaging, value)
  - ImageUrls, Tags
  - Sentiment (VO), Priority, Status
  - AssignedTo, Resolution
  - Compensation (VO)
  - Comments (collection of FeedbackCommentVO)

**Value Objects**:
- `AddressVO`: label, type, recipientName, contactPhone, flatNumber, building, street, landmark, area, city, state, pincode, lat, lon, deliveryInstructions
- `DietaryPreferencesVO`: vegetarian, vegan, glutenFree, dairyFree, jain, halal, kosher, keto, lowCarb, paleo, allergies, avoidIngredients, spicePreference, dailyTargets, healthGoals
- `NotificationPreferencesVO`: pushEnabled, smsEnabled, emailEnabled, whatsappEnabled, orderUpdates, promotions, recommendations, quietHours
- `PersonalizationSettingsVO`: aiRecommendationsEnabled, aiChatEnabled, voiceOrderingEnabled, learningFromHistoryEnabled, chatSettings
- `PaymentMethodVO`: type (CARD, UPI, WALLET, NET_BANKING, COD), cardDetails, upiId, walletProvider
- `AchievementVO`: achievementId, name, description, iconUrl, category, pointsAwarded, unlockedAt
- `BadgeVO`: badgeId, name, description, iconUrl, badgeTier, category, rarity, earnedAt
- `ChallengeProgressVO`: challengeId, name, targetCount, currentCount, startDate, endDate, pointsReward, status
- `DietMealVO`: mealType, scheduledDate/Time, suggestedItems, targetNutrition, orderId, status
- `CompensationVO`: type (REFUND, CREDITS, COUPON), amount, status
- `FeedbackCommentVO`: authorId, text, attachments, internalNote, createdAt

**Domain Services**:
- `UserValidationService`: validate email uniqueness, phone format
- `UserPreferenceService`: manage and update preferences
- `LoyaltyPointsService`: earn, redeem, expire points
- `TierManagementService`: calculate and update tier status
- `AchievementService`: check and award achievements
- `ReferralService`: process referrals and award bonuses
- `DietPlanGenerationService`: AI-powered diet plan creation
- `MealSchedulingService`: schedule and track meals
- `FeedbackModerationService`: moderate and route feedback

**Events Published**:
- `user.registered`
- `user.profile.updated`
- `user.deleted`
- `loyalty.points.earned`
- `loyalty.points.redeemed`
- `loyalty.tier.changed`
- `loyalty.referral.processed`
- `dietplan.created`
- `dietplan.meal.completed`
- `feedback.submitted`
- `feedback.resolved`

**Events Consumed**:
- `order.completed` (to award loyalty points)
- `order.reviewed` (to process order review)

**Database**: MongoDB (users, loyalty_accounts, diet_plans, feedback collections)

---

### 2. Restaurant Management Context (`restaurant-service`)

**Purpose**: Manage restaurants, onboarding (with save/resume by step), outlets, contracts, serviceability. Post-onboarding: create owner user (via user-service) and trigger verification (email/SMS stub).

**Aggregates**:
- **Restaurant** (Root)
  - RestaurantId (Identity)
  - Name, Description, Cuisine Types
  - Owners (OwnerVO), Contacts (ContactVO), Documents (DocumentVO), Contract (ContractVO)
  - FSSAI, GST, PAN, BankAccount (BankAccountVO)
  - Status (PENDING, APPROVED, ACTIVE, SUSPENDED, REJECTED)
  - Outlets (RestaurantOutlet collection)
  - CreatedAt, UpdatedAt

- **OnboardingDraft** (Root – application-level draft for multi-step onboarding)
  - DraftId (Identity)
  - currentStep (1–6), status (DRAFT, SUBMITTED)
  - All form fields (name, description, cuisineTypes, address, contact, owner, legal, bank, contract, menu) as optional for partial save
  - contractSigned, signatureText, assistanceRequested
  - createdAt, updatedAt

**Value Objects**:
- `OperatingHoursVO`, `ContactVO`, `DocumentVO`, `BankAccountVO`, `ContractVO`, etc.

**Application / Integration**:
- **OnboardingDraftService**: create draft, get draft, save draft (PATCH by step), submit from draft (creates Restaurant + first Outlet, then calls post-submit).
- **RestaurantService.afterOnboardingSubmitted**: calls **UserServiceClient** (HTTP to user-service) to create owner user for login; calls **NotificationService** (stub: logs “would send email/SMS”; replace with real provider later).

**Domain Services**:
- `RestaurantApprovalService`, `RestaurantAvailabilityService`, etc.

**Events Published**:
- `restaurant.registered`, `restaurant.approved`, `restaurant.menu.updated`, `restaurant.status.changed`

**Database**: MongoDB (restaurants, onboarding_drafts, history collections)

---

### 3. Menu Management Context (`menu-service`)

**Purpose**: Manage menu items, categories, pricing, availability, customizations, and nutritional information

**Aggregates**:
- **MenuItem** (Root)
  - MenuItemId (Identity)
  - RestaurantId
  - Name
  - Description
  - Category
  - BasePrice
  - Images (list)
  - Available (boolean)
  - PreparationTime (minutes)
  - SpiceLevel (0-5)
  - Dietary flags: vegetarian, vegan, glutenFree, containsDairy, containsNuts, halal, jain
  - NutritionalInfo (VO)
  - Ingredients (list)
  - Allergens (set)
  - Customizations (list of CustomizationVO)
  - AvailabilitySchedule (VO)
  - DynamicPricingConfig (VO)
  - Tags (set)
  - Serves (portions)
  - Status (enum: ACTIVE, INACTIVE, OUT_OF_STOCK, PENDING_APPROVAL, DISCONTINUED)
  - TotalOrders (counter)
  - AverageRating (aggregated)
  - ReviewCount (counter)
  - CreatedAt, UpdatedAt, Deleted

- **MenuCategory** (Root)
  - MenuCategoryId (Identity)
  - RestaurantId
  - Name
  - Description
  - IconUrl
  - DisplayOrder
  - Active (boolean)
  - CreatedAt, UpdatedAt, Deleted

**Value Objects**:
- `NutritionalInfoVO`: calories, protein, carbs, fat, fiber, sugar, sodium, saturatedFat, transFat, cholesterol
- `CustomizationVO`: id, name, options[], required, multiSelect, minSelections, maxSelections, defaultOptionId, displayOrder
- `CustomizationOptionVO`: id, name, description, additionalCost, available, displayOrder
- `AvailabilityScheduleVO`: alwaysAvailable, availableDays[], startTime, endTime, seasonalStartDate, seasonalEndDate
- `DynamicPricingConfigVO`: enabled, peakHourMultiplier, offPeakMultiplier, priceFloor, priceCeiling, demandBasedPricing, inventoryBasedPricing, priceTimeSlots[]
- `PriceTimeSlotVO`: days[], startTime, endTime, multiplier

**Domain Services**:
- `MenuItemAvailabilityService`: check real-time availability based on schedule
- `MenuPricingService`: calculate final price with customizations and dynamic pricing
- `MenuSearchService`: advanced search and filtering

**Domain Methods** (Business Logic in Entities):
- `MenuItem.calculateFinalPrice(selectedCustomizations)`: Calculate price including customizations
- `MenuItem.isAvailableNow()`: Check if available based on schedule and status
- `MenuItem.updateRating(newRating)`: Aggregate ratings
- `MenuItem.recordOrder()`: Track order count
- `MenuItem.markOutOfStock() / markInStock()`: Availability management
- `MenuItem.validate()`: Business rule validation
- `DynamicPricingConfigVO.calculatePrice()`: Apply dynamic pricing logic
- `AvailabilityScheduleVO.isAvailableNow()`: Time-based availability check

**Events Published**:
- `menu.item.created`
- `menu.item.updated`
- `menu.item.deleted`
- `menu.item.availability.changed`
- `menu.item.price.updated`
- `menu.category.created`
- `menu.category.updated`

**Events Consumed**:
- `restaurant.approved` (to activate menu items)
- `order.created` (to track item orders)
- `review.created` (to update ratings)

**Database**: MongoDB (menu_items, menu_categories collections)

---

### 4. Order Management Context (`order-service`) ✅ IMPLEMENTED

**Purpose**: In the restaurant-app backend, order-service exposes **restaurant-facing APIs only**: get order, get by number, get restaurant orders, update status, cancel, assign rider. Cart and Payment **controllers** have been removed; create order, get user orders, active/past orders, rate, reorder are not exposed.

**Aggregates**:

- **Order** (Root)
  - OrderId (Identity)
  - OrderNumber (Human-readable, e.g., ORD-20250115-001)
  - UserId
  - OrderType (SINGLE_RESTAURANT, MULTI_RESTAURANT, AI_CHAT, DIET_PLAN, PARTY_PLANNER, REORDER, SCHEDULED)
  - Status (CREATED, CONFIRMED, PREPARING, READY, PICKED_UP, IN_TRANSIT, DELIVERED, CANCELLED, DELIVERY_FAILED, RETURN_INITIATED, RETURNED)
  - Items (collection of OrderItem)
  - DeliveryAddress (VO)
  - OrderTotal (VO)
  - PaymentMethod (UPI, CARD, NET_BANKING, WALLET, COD, BNPL, EMI)
  - PaymentStatus (PENDING, PROCESSING, SUCCESS, FAILED, REFUNDED, PARTIALLY_REFUNDED, CANCELLED, COD_PENDING, COD_COLLECTED)
  - PaymentTransactionId, PaymentGatewayOrderId
  - Timeline (collection of OrderTimelineEntryVO)
  - SpecialInstructions, DeliveryInstructions
  - ScheduledDeliveryTime, EstimatedDeliveryTime, ActualDeliveryTime
  - RiderId, RiderName, RiderPhone
  - DietPlanId, PartyPlanId, ChatSessionId, ReorderFromOrderId
  - CancellationReason, CancelledBy
  - LoyaltyPointsEarned, LoyaltyPointsRedeemed
  - CustomerRating, CustomerReview, Reviewed
  - Deleted, CreatedAt, UpdatedAt

- **OrderItem** (Child Entity)
  - OrderItemId (Identity)
  - MenuItemId, Name, Description
  - RestaurantId, RestaurantName
  - ImageUrl
  - UnitPrice, Quantity, TotalPrice
  - Customizations (collection of OrderItemCustomizationVO)
  - SpecialInstructions
  - Vegetarian, SpiceLevel
  - ItemStatus (PENDING, PREPARING, READY, UNAVAILABLE, CANCELLED)
  - CreatedAt, UpdatedAt

- **Cart** (Root - MongoDB)
  - CartId (Identity)
  - UserId
  - Items (collection of CartItem)
  - AppliedCouponCode, CouponDiscount
  - SelectedAddressId, SelectedPaymentMethod
  - SpecialInstructions
  - ScheduledDeliveryTime
  - Source (web, mobile, ai_chat, diet_planner, party_planner)
  - DietPlanId, PartyPlanId, ChatSessionId
  - Tip
  - CreatedAt, UpdatedAt

- **CartItem** (Child Entity)
  - CartItemId (Identity)
  - MenuItemId, Name, Description
  - RestaurantId, RestaurantName
  - ImageUrl
  - UnitPrice, Quantity, TotalPrice
  - Customizations (collection of CartItemCustomization)
  - SpecialInstructions
  - Vegetarian, SpiceLevel, PreparationTime

- **Payment** (Root)
  - PaymentId (Identity)
  - OrderId, UserId
  - Amount, Currency
  - PaymentProvider (razorpay, stripe)
  - GatewayOrderId, TransactionId, GatewaySignature
  - PaymentMethod, PaymentMethodDetails (JSON)
  - Status (PENDING, PROCESSING, SUCCESS, FAILED, REFUNDED, PARTIALLY_REFUNDED, CANCELLED, COD_PENDING, COD_COLLECTED)
  - FailureReason, Attempts
  - IpAddress, UserAgent, Metadata
  - CreatedAt, UpdatedAt, CompletedAt

- **Refund** (Root)
  - RefundId (Identity)
  - PaymentId, OrderId, UserId
  - Amount, OriginalAmount
  - Reason
  - RefundType (FULL, PARTIAL, CANCELLATION, QUALITY_ISSUE, MISSING_ITEMS, WRONG_ITEMS, LATE_DELIVERY, DELIVERY_FAILED, GOODWILL)
  - GatewayRefundId
  - Status (PENDING, PROCESSING, PROCESSED, FAILED, REJECTED)
  - InitiatedBy, FailureReason, Notes
  - CreatedAt, ProcessedAt

**Value Objects**:
- `DeliveryAddressVO`: label, recipientName, contactPhone, flatNumber, building, street, landmark, area, city, state, pincode, latitude, longitude, deliveryInstructions, getFullAddress()
- `OrderTotalVO`: subtotal, deliveryFee, platformFee, packagingCharges, gst, gstPercentage, discount, couponCode, loyaltyPointsValue, tip, total, currency, calculateTotal()
- `OrderTimelineEntryVO`: status, description, timestamp, updatedBy, notes
- `OrderItemCustomizationVO`: customizationId, customizationName, optionId, optionName, additionalCost
- `CartItemCustomization`: same structure as OrderItemCustomizationVO

**Domain Services**:
- `CartService`: manage cart operations (add/update/remove items, apply coupon, clear)
- `OrderService`: create orders, manage status, cancellation, rating, reorder
- `PaymentService`: initiate payment, verify payment, process refunds

**Domain Methods** (Business Logic in Entities):
- `Order.confirm()`: Confirm order after payment
- `Order.startPreparing(restaurantId)`: Mark order as preparing
- `Order.markReady(restaurantId)`: Mark order as ready for pickup
- `Order.markPickedUp(riderId, riderName, riderPhone)`: Assign rider and mark picked up
- `Order.markInTransit()`: Mark order as in transit
- `Order.markDelivered()`: Mark order as delivered
- `Order.cancel(reason, cancelledBy)`: Cancel order
- `Order.canCancel()`: Check if order can be cancelled
- `Order.canRefund()`: Check if order can be refunded
- `Order.calculateTotal()`: Calculate order total from items
- `Order.isMultiRestaurantOrder()`: Check if multi-restaurant order
- `Cart.addItem(item)`: Add item (merge if same item exists)
- `Cart.updateItemQuantity(itemId, quantity)`: Update quantity
- `Cart.removeItem(itemId)`: Remove item
- `Cart.clear()`: Clear all items
- `Cart.getSubtotal()`: Calculate subtotal
- `Cart.isMultiRestaurant()`: Check if multi-restaurant cart
- `Payment.markSuccess(transactionId)`: Mark payment successful
- `Payment.markFailed(reason)`: Mark payment failed
- `Refund.markProcessed(gatewayRefundId)`: Mark refund processed
- `Refund.isFullRefund()`: Check if full refund

**Events Published**:
- `order.created`
- `order.confirmed`
- `order.status.updated`
- `order.cancelled`
- `payment.completed`
- `refund.initiated`

**Events Consumed**:
- `payment.completed` (to confirm order)
- `payment.failed` (to handle failed payment)
- `rider.assigned` (to update delivery info)

**Database**: 
- PostgreSQL (orders, order_items, payments, refunds tables) - for transactional consistency
- MongoDB (carts collection) - for flexible cart storage

---

### 5. Payment Context (`payment-service`)

**Purpose**: Handle payments, refunds, settlements

**Aggregates**:
- **Payment** (Root)
  - PaymentId (Identity)
  - OrderId
  - UserId
  - Amount
  - PaymentMethod (UPI, CARD, WALLET, COD)
  - PaymentGateway (JioOnePay)
  - TransactionId
  - Status (PENDING, PROCESSING, SUCCESS, FAILED, REFUNDED)
  - CreatedAt, UpdatedAt, CompletedAt

- **Refund** (Root)
  - RefundId (Identity)
  - PaymentId
  - Amount
  - Reason
  - Status
  - ProcessedAt

**Value Objects**:
- `PaymentMethodVO`: type, details (card last 4, UPI id)
- `MoneyVO`: amount, currency

**Domain Services**:
- `PaymentProcessingService`: process payments via gateway
- `RefundService`: handle refund logic

**Events Published**:
- `payment.initiated`
- `payment.completed`
- `payment.failed`
- `refund.processed`

**Events Consumed**:
- `order.created` (to initiate payment)
- `order.cancelled` (to process refund)

**Database**: PostgreSQL (payments, refunds tables)

---

### 6. Delivery/Rider Context (`rider-service`)

**Purpose**: Manage riders, trips, assignments, payouts

**Aggregates**:
- **Rider** (Root)
  - RiderId (Identity)
  - Name
  - Phone
  - Email
  - Vehicle Details
  - Documents (DL, Aadhar, RC, Vehicle Reg)
  - Status (AVAILABLE, BUSY, OFFLINE, ON_BREAK)
  - CurrentLocation
  - Geofence
  - CreatedAt, UpdatedAt

- **Trip** (Root)
  - TripId (Identity)
  - RiderId
  - Orders (collection of OrderId)
  - StartLocation
  - EndLocation
  - PlannedRoute
  - ActualRoute
  - Status (ASSIGNED, STARTED, IN_PROGRESS, COMPLETED, CANCELLED)
  - PickupStops
  - DeliveryStops
  - StartedAt, CompletedAt

- **Attendance** (Root)
  - AttendanceId
  - RiderId
  - Date
  - CheckInTime
  - CheckOutTime
  - Status

**Value Objects**:
- `LocationVO`: latitude, longitude, timestamp
- `RouteVO`: waypoints[], distance, estimatedTime
- `VehicleVO`: type, number, make, model

**Domain Services**:
- `RiderAvailabilityService`: check rider availability
- `TripOptimizationService`: optimize delivery routes (VRPTW)
- `RiderPayoutService`: calculate earnings

**Events Published**:
- `rider.registered`
- `rider.status.changed`
- `trip.assigned`
- `trip.started`
- `trip.completed`
- `rider.location.updated`

**Events Consumed**:
- `order.ready` (to assign rider)

**Database**: 
- MongoDB (riders, trips, attendance collections)
- PostgreSQL (for payout calculations)

---

### 7. Promotion & Campaign Context (`promotion-service`) ✅ IMPLEMENTED

**Purpose**: Manage promotions, coupons, marketing campaigns, and customer segments for targeted offerings

**Aggregates**:
- **Promotion** (Root)
  - PromotionId (Identity)
  - Name, Description
  - Code (unique)
  - Type (PERCENTAGE, FLAT, BOGO, FREE_DELIVERY, FREE_ITEM, CASHBACK)
  - RestaurantId (optional - null for platform-wide)
  - Discount (VO)
  - Schedule (VO)
  - UsageLimit (VO)
  - Applicability (VO)
  - TargetSegmentIds
  - Status (DRAFT, SCHEDULED, ACTIVE, PAUSED, ENDED)
  - CurrentUsage
  - Tags, Priority
  - Stackable, AutoApply, FirstOrderOnly, NewUsersOnly
  - CreatedAt, UpdatedAt, DeletedAt

- **Coupon** (Root)
  - CouponId (Identity)
  - Code (unique)
  - Description
  - RestaurantId (optional - null for platform-wide)
  - DiscountType (PERCENTAGE, FLAT, FREE_DELIVERY, FREE_ITEM, BOGO)
  - DiscountValue, MaxDiscount
  - MinOrderValue
  - ValidFrom, ValidUntil
  - TotalLimit, PerUserLimit, CurrentUsage
  - FirstOrderOnly, NewUsersOnly
  - Active, Deleted
  - ApplicableItemIds, ApplicableCategoryIds
  - ExcludedItemIds, ExcludedCategoryIds
  - CreatedAt, UpdatedAt, DeletedAt

- **Campaign** (Root)
  - CampaignId (Identity)
  - Name, Description
  - RestaurantId (optional)
  - Type (PROMOTIONAL, SEASONAL, FLASH_SALE, LOYALTY, RE_ENGAGEMENT, AWARENESS)
  - Channels (EMAIL, SMS, PUSH, IN_APP, WHATSAPP)
  - TargetSegmentIds
  - Content (Map)
  - Schedule (VO)
  - Budget, SpentAmount
  - LinkedPromotionIds, LinkedCouponIds
  - Status (DRAFT, SCHEDULED, ACTIVE, PAUSED, COMPLETED, ARCHIVED)
  - TrackingEnabled, AbTestEnabled, VariantPercentage
  - Metrics (impressions, clicks, conversions)
  - CreatedAt, UpdatedAt, DeletedAt

- **CustomerSegment** (Root)
  - SegmentId (Identity)
  - Name, Description
  - RestaurantId (optional)
  - Criteria (VO)
  - Dynamic (boolean - auto-calculated vs manual)
  - StaticUserIds (for manual segments)
  - UserCount
  - Active, Deleted
  - CreatedAt, UpdatedAt

- **CouponUsage** (Entity)
  - UsageId (Identity)
  - CouponId, UserId, OrderId
  - OrderValue, DiscountAmount
  - UsedAt

**Value Objects**:
- `DiscountVO`: type, value, maxDiscount, minOrderValue
- `ScheduleVO`: startTime, endTime, daysOfWeek, timeSlots, frequency
- `UsageLimitVO`: totalLimit, perUserLimit, perOrderLimit, perDayLimit
- `ApplicabilityVO`: applicableItemIds, applicableCategoryIds, excludedItemIds, excludedCategoryIds
- `SegmentCriteriaVO`: minOrders, maxOrders, minSpent, maxSpent, tierLevels, cuisinePreferences, locationIds, inactiveDays, registrationDateFrom/To

**Domain Services**:
- `PromotionService`: create, activate, pause, resume, end promotions
- `CouponService`: create, validate, apply, track coupon usage
- `CampaignService`: create, schedule, activate, track campaigns
- `CustomerSegmentService`: create, manage segments, calculate membership

**APIs**:
- `POST /api/v1/promotions` - Create promotion
- `GET /api/v1/promotions/{id}` - Get promotion
- `GET /api/v1/promotions/code/{code}` - Get by code
- `PUT /api/v1/promotions/{id}/activate|pause|resume|end` - Status transitions
- `POST /api/v1/coupons` - Create coupon
- `POST /api/v1/coupons/validate` - Validate coupon
- `POST /api/v1/coupons/apply` - Apply coupon to order
- `POST /api/v1/campaigns` - Create campaign
- `PUT /api/v1/campaigns/{id}/activate|pause|complete` - Campaign lifecycle
- `POST /api/v1/campaigns/{id}/metrics` - Record metrics
- `POST /api/v1/segments` - Create segment
- `GET /api/v1/segments/user/{userId}` - Get user's segments

**Events Published**:
- `PROMOTION_CREATED`, `PROMOTION_ACTIVATED`, `PROMOTION_PAUSED`, `PROMOTION_ENDED`
- `COUPON_CREATED`, `COUPON_APPLIED`, `COUPON_DEACTIVATED`
- `CAMPAIGN_CREATED`, `CAMPAIGN_ACTIVATED`, `CAMPAIGN_PAUSED`, `CAMPAIGN_COMPLETED`

**Events Consumed**:
- `ORDER_COMPLETED` - Record promotion/coupon usage
- `USER_REGISTERED` - Update segment membership
- `USER_TIER_CHANGED` - Update segment membership
- `RESTAURANT_DEACTIVATED` - End associated promotions/campaigns

**Database**: MongoDB (promotions, coupons, campaigns, segments, coupon_usage collections)

---

### 8. Search & Discovery Context (`search-service`)

**Purpose**: Search restaurants, menu items, and recommendations

**Aggregates**:
- **SearchIndex** (Not a traditional aggregate, more of a projection)
  - Indexed from Restaurant, MenuItem, etc.

**Domain Services**:
- `SearchIndexingService`: index data from other services
- `SearchQueryService`: execute search queries
- `RecommendationService`: AI-powered recommendations

**Events Consumed**:
- `restaurant.created`, `restaurant.updated`
- `menu.item.created`, `menu.item.updated`
- `order.completed` (for recommendation training)

**Database**: Elasticsearch (search indexes)

---

### 9. Review & Rating Context (`review-service`)

**Purpose**: Manage reviews, ratings, feedback

**Aggregates**:
- **Review** (Root)
  - ReviewId (Identity)
  - OrderId
  - UserId
  - RestaurantId (optional)
  - RiderId (optional)
  - MenuItemIds (optional, array)
  - OverallRating
  - FoodRating
  - DeliveryRating
  - PackagingRating
  - Comment
  - Images
  - Status (PENDING, PUBLISHED, HIDDEN)
  - CreatedAt

**Value Objects**:
- `RatingVO`: category, score (1-5)

**Domain Services**:
- `ReviewModerationService`: moderate reviews
- `RatingAggregationService`: calculate average ratings

**Events Published**:
- `review.created`
- `review.published`

**Events Consumed**:
- `order.delivered` (to request review)

**Database**: MongoDB (reviews collection)

---

### 10. Support & Ticketing Context (`support-service`)

**Purpose**: Manage customer support tickets, complaints

**Aggregates**:
- **Ticket** (Root)
  - TicketId (Identity)
  - UserId
  - OrderId (optional)
  - Category
  - Priority
  - Status (OPEN, IN_PROGRESS, RESOLVED, CLOSED)
  - Description
  - Comments (collection)
  - AssignedTo
  - CreatedAt, UpdatedAt, ResolvedAt

- **TicketComment** (Child Entity)
  - CommentId
  - UserId
  - Comment
  - CreatedAt

**Value Objects**:
- `TicketCategoryVO`: category, subcategory

**Domain Services**:
- `TicketRoutingService`: route tickets to appropriate team
- `TicketEscalationService`: escalate unresolved tickets

**Events Published**:
- `ticket.created`
- `ticket.resolved`

**Events Consumed**:
- `order.cancelled` (may create ticket)

**Database**: MongoDB (tickets collection)

---

### 11. Notification Context (`notification-service`)

**Purpose**: Send notifications via SMS, Email, Push, WhatsApp

**Aggregates**:
- **Notification** (Root)
  - NotificationId (Identity)
  - UserId
  - Channel (SMS, EMAIL, PUSH, WHATSAPP)
  - Template
  - Variables
  - Status (PENDING, SENT, DELIVERED, FAILED)
  - SentAt

**Value Objects**:
- `ChannelVO`: type, address (phone, email, device token)

**Domain Services**:
- `NotificationSenderService`: send via appropriate channel
- `NotificationTemplateService`: render templates

**Events Consumed**:
- `order.created`, `order.confirmed`, `order.delivered` (send notifications)
- `payment.completed`, `payment.failed`
- `user.registered` (welcome email)

**Database**: MongoDB (notifications collection)

---

### 12. Chat Context (`chat-service`)

**Purpose**: AI-powered chat assistant for ordering

**Aggregates**:
- **ChatSession** (Root)
  - SessionId (Identity)
  - UserId
  - Status
  - Messages (collection)
  - Context (current order being built)
  - CreatedAt, UpdatedAt

- **ChatMessage** (Child Entity)
  - MessageId
  - Role (USER, ASSISTANT)
  - Content
  - Timestamp

**Domain Services**:
- `ChatbotService`: process user messages and generate responses
- `ChatContextService`: maintain conversation context

**Events Published**:
- `chat.order.created` (when user completes order via chat)

**Database**: MongoDB (chat_sessions collection)

---

### 13. Diet & Party Planner Context (`planner-service`)

**Purpose**: Manage diet plans and party planning

**Aggregates**:
- **DietPlan** (Root)
  - DietPlanId (Identity)
  - UserId
  - Type (WEEKLY, MONTHLY)
  - StartDate
  - EndDate
  - Meals (collection of DietMeal)
  - Status

- **PartyPlan** (Root)
  - PartyPlanId
  - UserId
  - EventDate
  - GuestCount
  - MenuItems
  - TotalCost
  - Status

**Value Objects**:
- `DietMealVO`: date, mealType, menuItemId, restaurantId

**Domain Services**:
- `DietPlanGenerationService`: AI-powered diet plan creation
- `PartyPlanningService`: suggest menu for parties

**Database**: MongoDB (diet_plans, party_plans collections)

---

### 14. Analytics Context (`analytics-service`)

**Purpose**: Generate analytics, reports, dashboards

**Aggregates**:
- **Report** (Root)
  - ReportId
  - Type
  - Parameters
  - Data
  - GeneratedAt

**Domain Services**:
- `ReportGenerationService`: generate various reports
- `MetricsAggregationService`: aggregate metrics

**Events Consumed**:
- All events (for analytics)

**Database**: 
- Databricks (data lake for big data analytics)
- InfluxDB (time-series metrics)

---

### 15. Admin & Ledger Context (`admin-service`)

**Purpose**: Admin operations, financial ledgers, approvals

**Aggregates**:
- **FinancialLedger** (Root)
  - LedgerId
  - EntityType (RESTAURANT, RIDER, PLATFORM)
  - EntityId
  - TransactionType
  - Amount
  - Balance
  - Timestamp

- **Approval** (Root)
  - ApprovalId
  - EntityType
  - EntityId
  - Status
  - ApprovedBy
  - ApprovedAt

**Domain Services**:
- `LedgerService`: manage financial transactions
- `ApprovalWorkflowService`: handle approvals

**Database**: PostgreSQL (ledger, approvals tables) - for financial integrity

---

### 16. Orchestration Context (`orchestration-service`) ✅ IMPLEMENTED (API Gateway only)

**Purpose**: In the restaurant-app backend, orchestration-service acts as **API Gateway only**: it routes requests to user, restaurant, menu, order, and promotion services. Saga and Checkout controllers/engine have been removed; there is no distributed transaction engine in this repo.

**Gateway Routes**:
- `/api/v1/user/**` → user-service (8083)
- `/api/v1/restaurant/**` → restaurant-service (8081)
- `/api/v1/menu/**` → menu-service (8082)
- `/api/v1/order/**` → order-service (8084)
- `/api/v1/promotion/**` → promotion-service (8089)

**Database**: None (stateless gateway)

---

## Cross-Cutting Concerns

### API Gateway (`api-gateway`)
- Route requests to appropriate microservices
- JWT validation
- Rate limiting
- Request/response transformation
- Aggregation of responses (BFF pattern)

### Orchestration Layer (`orchestration-service`)
- Saga orchestration for distributed transactions
- Complex workflows (order placement, cancellation)
- Compensation logic

**Technology**: Spring Cloud Gateway, Resilience4j

---

## Data Flow Examples

### Example 1: Place Order
1. User calls API Gateway: `POST /api/v1/orders`
2. API Gateway validates JWT
3. API Gateway routes to Orchestration Service
4. Orchestration Service:
   - Calls User Service (validate user)
   - Calls Restaurant Service (validate restaurant availability)
   - Calls Menu Service (validate items, calculate price)
   - Calls Promotion Service (apply discount)
   - Calls Order Service (create order)
   - Publishes `order.created` event
5. Payment Service consumes event, initiates payment
6. Payment Service publishes `payment.completed` event
7. Order Service consumes event, confirms order
8. Notification Service sends confirmation SMS/Email

### Example 2: Assign Rider
1. Restaurant marks order as READY
2. Order Service publishes `order.ready` event
3. Rider Service consumes event
4. Trip Optimization Service finds available rider and optimal route
5. Rider Service assigns trip to rider
6. Rider Service publishes `trip.assigned` event
7. Order Service updates order with rider info
8. Notification Service notifies user and rider

---

## Database Distribution

| Service | Primary DB | Cache | Search | Events |
|---------|-----------|-------|--------|--------|
| user-service | MongoDB | Redis | - | Kafka |
| restaurant-service | MongoDB | Redis | Elasticsearch | Kafka |
| menu-service | MongoDB | Redis | Elasticsearch | Kafka |
| order-service | PostgreSQL | Redis | - | Kafka |
| payment-service | PostgreSQL | - | - | Kafka |
| rider-service | MongoDB + PostgreSQL | Redis | - | Kafka |
| promotion-service | MongoDB | Redis | - | Kafka |
| search-service | Elasticsearch | Redis | Elasticsearch | Kafka |
| review-service | MongoDB | - | Elasticsearch | Kafka |
| support-service | MongoDB | - | - | Kafka |
| notification-service | MongoDB | - | - | Kafka |
| chat-service | MongoDB | Redis | - | Kafka |
| planner-service | MongoDB | Redis | - | Kafka |
| analytics-service | Databricks + InfluxDB | - | - | Kafka |
| admin-service | PostgreSQL | - | - | Kafka |
| orchestration-service | MongoDB | Redis | - | Kafka |

---

## Common Patterns

### Event-Driven Architecture
- All services publish domain events to Kafka
- Services subscribe to events they're interested in
- Idempotent event handling (use event ID to prevent duplicates)

### CQRS (Command Query Responsibility Segregation)
- Write model: Optimized for updates (PostgreSQL)
- Read model: Optimized for queries (MongoDB, Elasticsearch)
- Events keep both in sync

### Saga Pattern
- Orchestration-based saga for complex workflows
- Compensation logic for rollbacks

### API Composition
- API Gateway aggregates data from multiple services
- Backend-for-Frontend (BFF) pattern

---

## Development Order (Priority)

### Phase 1: Core Services (MVP - 10 weeks)
1. user-service ✅
2. restaurant-service ✅
3. menu-service ✅
4. order-service ✅
5. payment-service
6. rider-service
7. notification-service
8. api-gateway
9. orchestration-service ✅

### Phase 2: Enhanced Features
10. search-service
11. review-service
12. promotion-service
13. support-service

### Phase 3: Advanced Features
14. chat-service
15. planner-service
16. analytics-service
17. admin-service

---

## Next Steps

1. Setup parent POM and common modules
2. Create service templates
3. Start with `user-service` as reference implementation
4. Follow with other services using the same pattern


