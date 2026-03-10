# Cursor AI Prompt for Restaurant Service Generation

## Instruction for Cursor

Create a complete **restaurant-service** for the FoodAI platform following all guardrails from `backend/.cursor/rules` and templates from `backend/SERVICE_TEMPLATE_SPEC.md`.

## Service Specification

**Service**: restaurant-service  
**Domain**: restaurant  
**Database**: MongoDB  
**Port**: 8081

### Purpose
Manage restaurant onboarding (self-service and assisted), approval workflow, and restaurant operations. This service is critical for the marketplace platform.

### Key Entities & Value Objects

#### Restaurant (Aggregate Root)
```java
@Document(collection = "restaurants")
public class Restaurant {
    @Id private String id;
    
    // Basic Information (Brand/Chain Level)
    private String name; // Restaurant brand name
    private String description;
    private String logo;
    private String coverImage;
    
    // Multiple Owners
    private List<OwnerVO> owners; // Can have multiple owners/partners
    
    // Multiple Contact Details (Brand Level)
    private List<ContactVO> contacts; // Primary, secondary, emergency contacts
    
    // Cuisine Types (common across outlets)
    private List<String> cuisineTypes;
    
    // Documents (Value Object) - Brand level
    private List<DocumentVO> documents;
    
    // Contract (Value Object) - Brand level with detailed fee structure
    private ContractVO contract;
    
    // Onboarding Info (Brand level)
    private OnboardingType onboardingType; // SELF_SERVICE, ASSISTED
    private String onboardedBy; // userId of operations agent (if assisted)
    
    // Outlets (Collection of entities)
    private List<RestaurantOutlet> outlets; // Multiple outlets per restaurant
    
    // Ratings & Stats (Aggregated from all outlets)
    private Double averageRating;
    private Integer totalReviews;
    private Integer totalOrders;
    
    // Brand Settings
    private Boolean acceptsOrders; // Master switch for all outlets
    
    // Audit fields (comprehensive)
    private String createdBy; // userId who created
    @CreatedDate private Instant createdAt;
    private String updatedBy; // userId who last updated
    @LastModifiedDate private Instant updatedAt;
    private boolean deleted;
    private String deletedBy; // userId who deleted
    private Instant deletedAt;
}
```

#### RestaurantOutlet (Entity within Restaurant Aggregate)
```java
public class RestaurantOutlet {
    private String outletId; // Unique outlet identifier
    
    // Outlet Information
    private String outletName; // e.g., "Main Branch", "MG Road Branch"
    private String outletCode; // e.g., "RES001-OUT001"
    
    // Address (Value Object) - Outlet specific
    private AddressVO address;
    
    // Multiple Contact Details - Outlet specific
    private List<ContactVO> contacts; // Manager, support, emergency contacts
    
    // Operating Hours (Value Object) - Outlet specific
    private List<OperatingHoursVO> operatingHours;
    
    // Status & Approval - Outlet specific
    private OutletStatus status; // NOT_ONBOARDED, PENDING_APPROVAL, APPROVED, ACTIVE, SUSPENDED, REJECTED
    private String approvedBy;
    private Instant approvedAt;
    private String rejectionReason;
    
    // Serviceability - Outlet specific
    private ServiceabilityConfig serviceabilityConfig; // Contains list of serviceable pincodes
    
    // TAT Configuration - Outlet specific
    private TATConfig tatConfig;
    
    // Outlet Settings
    private Boolean acceptsOrders; // Outlet level switch
    private Double minimumOrderValue;
    private Double deliveryFee;
    private Boolean selfDelivery;
    
    // Outlet Stats
    private Double averageRating;
    private Integer totalReviews;
    private Integer totalOrders;
    private Integer currentOrderQueue; // Current pending orders
    
    // Audit fields (comprehensive)
    private String createdBy; // userId who created
    private Instant createdAt;
    private String updatedBy; // userId who last updated
    private Instant updatedAt;
    private boolean deleted;
    private String deletedBy; // userId who deleted
    private Instant deletedAt;
}
```

#### Value Objects

**AddressVO**:
```java
public class AddressVO {
    private String street;
    private String city;
    private String state;
    private String pincode;
    private Double latitude;
    private Double longitude;
    private String landmark;
}
```

**OwnerVO**:
```java
public class OwnerVO {
    private String ownerId; // User ID
    private String ownerName;
    private String ownerEmail; // @Email
    private String ownerPhone; // @Pattern(regexp = "^[6-9]\\d{9}$")
    private Double ownershipPercentage; // Ownership stake
    private OwnerRole role; // PRIMARY_OWNER, CO_OWNER, PARTNER, INVESTOR
    private Boolean isPrimaryContact; // Primary decision maker
    private Instant addedAt;
    private String addedBy; // Who added this owner
}
```

**ContactVO**:
```java
public class ContactVO {
    private String contactId; // Unique contact identifier
    private ContactType contactType; // PRIMARY, SECONDARY, EMERGENCY, MANAGER, SUPPORT
    private String name; // Contact person name
    private String phone; // @Pattern(regexp = "^[6-9]\\d{9}$")
    private String alternatePhone;
    private String email; // @Email
    private String designation; // Manager, Owner, Support Executive, etc.
    private Boolean isActive; // Active contact or not
    private Instant addedAt;
    private String addedBy;
}
```

**OperatingHoursVO**:
```java
public class OperatingHoursVO {
    private DayOfWeek dayOfWeek;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Boolean isClosed;
}
```

**DocumentVO**:
```java
public class DocumentVO {
    private DocumentType type; // FSSAI, GST, PAN, BANK_PROOF
    private String url;
    private DocumentStatus verificationStatus; // PENDING, VERIFIED, REJECTED
    private Instant uploadedAt;
    private String remarks;
}
```

**ContractVO**:
```java
public class ContractVO {
    private String contractId;
    private String contractUrl; // Signed contract document URL
    private Boolean signed;
    private Instant signedAt;
    private String signedBy; // Owner who signed
    
    // Platform Fee Structure
    private PlatformFeeConfig platformFee;
    
    // Delivery Fee Structure
    private DeliveryFeeConfig deliveryFee;
    
    // Payment Gateway Fee
    private PaymentGatewayFeeConfig paymentGatewayFee;
    
    // Penalty Structure
    private PenaltyConfig penalties;
    
    // Contract Validity
    private Instant validFrom;
    private Instant validUntil;
    private Boolean autoRenewal;
    
    // Audit
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;
}
```

**PlatformFeeConfig**:
```java
public class PlatformFeeConfig {
    private FeeType feeType; // PERCENTAGE, FIXED, HYBRID
    
    // Percentage based
    private Double percentageRate; // e.g., 15% per order
    
    // Fixed based
    private Double fixedAmountPerOrder; // e.g., ₹20 per order
    
    // Min/Max caps
    private Double minFeePerOrder; // Minimum fee charged
    private Double maxFeePerOrder; // Maximum fee charged (cap)
    
    // Slabs (optional - for tiered pricing)
    private List<FeeSlabVO> feeSlabs; // Different rates for different order values
}
```

**FeeSlabVO**:
```java
public class FeeSlabVO {
    private Double minOrderValue;
    private Double maxOrderValue;
    private Double percentageRate; // Rate for this slab
    private Double fixedAmount; // Or fixed amount for this slab
}
```

**DeliveryFeeConfig**:
```java
public class DeliveryFeeConfig {
    private FeeType feeType; // PERCENTAGE, FIXED, HYBRID
    
    // Who pays delivery fee
    private DeliveryFeePayor payor; // RESTAURANT, CUSTOMER, SPLIT, PLATFORM
    
    // If restaurant pays
    private Double percentageRate; // % of order value
    private Double fixedAmountPerOrder;
    
    // Split ratio (if SPLIT)
    private Double restaurantSharePercentage; // e.g., 60%
    private Double customerSharePercentage; // e.g., 40%
    
    // Min/Max caps
    private Double minFeePerOrder;
    private Double maxFeePerOrder;
    
    // Distance-based slabs
    private List<DistanceFeeSlabVO> distanceSlabs;
}
```

**DistanceFeeSlabVO**:
```java
public class DistanceFeeSlabVO {
    private Double minDistanceKm;
    private Double maxDistanceKm;
    private Double feeAmount; // Fee for this distance range
}
```

**PaymentGatewayFeeConfig**:
```java
public class PaymentGatewayFeeConfig {
    private FeeType feeType; // PERCENTAGE, FIXED
    
    // Who bears PG fee
    private PaymentFeePayor payor; // RESTAURANT, CUSTOMER, PLATFORM
    
    // Fee structure
    private Double percentageRate; // e.g., 2%
    private Double fixedAmount; // e.g., ₹2 per transaction
    
    // Min/Max
    private Double minFee;
    private Double maxFee;
}
```

**PenaltyConfig**:
```java
public class PenaltyConfig {
    // Rating-based penalties
    private RatingPenaltyVO ratingPenalty;
    
    // Review-based penalties
    private ReviewPenaltyVO reviewPenalty;
    
    // Cancellation penalties
    private CancellationPenaltyVO cancellationPenalty;
    
    // Pricing rule violation
    private PricingViolationPenaltyVO pricingViolationPenalty;
    
    // Quality violations
    private QualityViolationPenaltyVO qualityViolationPenalty;
    
    // Delay penalties
    private DelayPenaltyVO delayPenalty;
    
    // Other violations
    private List<ViolationPenaltyVO> otherViolations;
}
```

**RatingPenaltyVO**:
```java
public class RatingPenaltyVO {
    private Boolean enabled;
    
    // Thresholds
    private Double criticalRatingThreshold; // e.g., < 3.0
    private Double warningRatingThreshold; // e.g., < 3.5
    
    // Penalties (per order or monthly)
    private Double criticalPenaltyPercentage; // e.g., 5% deduction
    private Double criticalPenaltyFixed; // e.g., ₹500
    
    private Double warningPenaltyPercentage; // e.g., 2% deduction
    private Double warningPenaltyFixed; // e.g., ₹200
    
    // Suspension threshold
    private Double suspensionRatingThreshold; // e.g., < 2.5
    private Integer continuousPoorRatingDays; // Consecutive days below threshold
}
```

**ReviewPenaltyVO**:
```java
public class ReviewPenaltyVO {
    private Boolean enabled;
    
    // Negative review penalties
    private Double negativeReviewPenaltyAmount; // Per negative review
    
    // Threshold for action
    private Integer negativeReviewThresholdCount; // e.g., 5 negative reviews
    private Integer negativeReviewThresholdDays; // Within days (e.g., 7 days)
    
    // Penalty
    private Double penaltyAmount; // Fixed penalty
    private Double penaltyPercentage; // Or % of monthly revenue
}
```

**CancellationPenaltyVO**:
```java
public class CancellationPenaltyVO {
    private Boolean enabled;
    
    // Restaurant-initiated cancellation penalties
    private Double cancellationPenaltyAmount; // Per cancellation
    
    // Threshold before penalty
    private Integer allowedCancellationsPerMonth; // Free cancellations
    
    // Escalating penalties
    private Double firstViolationPenalty; // e.g., ₹100
    private Double secondViolationPenalty; // e.g., ₹250
    private Double thirdViolationPenalty; // e.g., ₹500
    
    // Cancellation rate threshold
    private Double maxCancellationRatePercentage; // e.g., 5%
    private Double penaltyForExceedingRate; // Penalty if exceeded
    
    // Suspension threshold
    private Double suspensionCancellationRate; // e.g., > 10%
}
```

**PricingViolationPenaltyVO**:
```java
public class PricingViolationPenaltyVO {
    private Boolean enabled;
    
    // Price mismatch penalty
    private Double priceMismatchPenalty; // Per incident
    
    // Unauthorized price increase
    private Double unauthorizedIncreaseThreshold; // e.g., > 10% increase
    private Double unauthorizedIncreasePenalty;
    
    // Item unavailability marking as different price
    private Double availabilityMismatchPenalty;
    
    // MRP violation (charging above MRP)
    private Double mrpViolationPenalty;
}
```

**QualityViolationPenaltyVO**:
```java
public class QualityViolationPenaltyVO {
    private Boolean enabled;
    
    // Food quality complaints
    private Double foodQualityComplaintPenalty;
    
    // Hygiene violations
    private Double hygieneViolationPenalty;
    
    // Packaging issues
    private Double packagingIssuePenalty;
    
    // Wrong/missing items
    private Double wrongItemPenalty;
    private Double missingItemPenalty;
    
    // Threshold for suspension
    private Integer qualityComplaintsThreshold; // e.g., 10 complaints
    private Integer qualityComplaintsDays; // Within days (e.g., 30 days)
}
```

**DelayPenaltyVO**:
```java
public class DelayPenaltyVO {
    private Boolean enabled;
    
    // TAT violation penalty
    private Integer allowedDelayMinutes; // Buffer beyond promised TAT
    
    // Penalty structure
    private Double penaltyPerMinuteDelay; // e.g., ₹10 per minute
    private Double maxDelayPenaltyPerOrder; // Cap per order
    
    // Chronic delay penalties
    private Double chronicDelayThresholdPercentage; // e.g., > 20% orders delayed
    private Double chronicDelayPenalty; // Monthly penalty
}
```

**ViolationPenaltyVO**:
```java
public class ViolationPenaltyVO {
    private String violationType; // FSSAI_VIOLATION, UNAUTHORIZED_OPERATION, etc.
    private String description;
    private Double penaltyAmount;
    private PenaltyAction action; // WARNING, FINE, SUSPENSION, TERMINATION
    private Integer suspensionDays; // If suspension
}
```

**ServiceabilityConfig**:
```java
public class ServiceabilityConfig {
    private List<String> serviceablePincodes; // List of pincodes this outlet serves
    private Double maxDeliveryRadius; // in km
    private Boolean useRadiusBased; // true = radius based, false = pincode based
    private ServiceabilityRules rules; // Additional rules
}

public class ServiceabilityRules {
    private Boolean checkInventory; // Check inventory before accepting order
    private Boolean checkCapacity; // Check kitchen capacity
    private Integer maxOrdersPerHour; // Max orders this outlet can handle per hour
    private List<String> excludedPincodes; // Pincodes explicitly excluded
}
```

**TATConfig**:
```java
public class TATConfig {
    // Base TAT (Turn Around Time)
    private Integer basePrepTimeMinutes; // Base food preparation time
    
    // Dynamic TAT factors
    private Integer tatPerOrderInQueue; // Additional minutes per order in queue
    private Integer peakHourExtraMinutes; // Extra minutes during peak hours
    
    // Peak hours definition
    private List<TimeRangeVO> peakHours; // e.g., 12:00-14:00, 19:00-22:00
    
    // TAT Calculation Settings
    private Boolean considerRiderAvailability; // Factor in rider availability
    private Boolean considerBatching; // Factor in order batching
    private Boolean considerDistance; // Factor in delivery distance
    
    // SLA
    private Integer maxTATMinutes; // Maximum TAT promised to customer
    private Integer bufferTimeMinutes; // Buffer time for safety
}

public class TimeRangeVO {
    private LocalTime startTime;
    private LocalTime endTime;
}
```

#### Enums

**RestaurantStatus**: PENDING, APPROVED, ACTIVE, SUSPENDED, REJECTED _(Brand level)_  
**OutletStatus**: NOT_ONBOARDED, PENDING_APPROVAL, APPROVED, ACTIVE, SUSPENDED, REJECTED _(Outlet level)_  
**OnboardingType**: SELF_SERVICE, ASSISTED  
**DocumentType**: FSSAI, GST, PAN, BANK_PROOF  
**DocumentStatus**: PENDING, VERIFIED, REJECTED  
**OwnerRole**: PRIMARY_OWNER, CO_OWNER, PARTNER, INVESTOR  
**ContactType**: PRIMARY, SECONDARY, EMERGENCY, MANAGER, SUPPORT  
**FeeType**: PERCENTAGE, FIXED, HYBRID  
**DeliveryFeePayor**: RESTAURANT, CUSTOMER, SPLIT, PLATFORM  
**PaymentFeePayor**: RESTAURANT, CUSTOMER, PLATFORM  
**PenaltyAction**: WARNING, FINE, SUSPENSION, TERMINATION

### History & Audit Tables

For critical fields, maintain complete audit trail in separate history collections.

#### RestaurantHistory
```java
@Document(collection = "restaurant_history")
public class RestaurantHistory {
    @Id private String id;
    private String restaurantId; // Reference to restaurant
    private String fieldName; // Which field changed
    private Object oldValue; // Previous value
    private Object newValue; // New value
    private ChangeType changeType; // CREATE, UPDATE, DELETE, STATUS_CHANGE
    private String changedBy; // userId who made the change
    private Instant changedAt;
    private String changeReason; // Optional reason for change
    private String sourceIp; // IP address of change origin
    private String userAgent; // Browser/app info
}
```

#### OutletHistory
```java
@Document(collection = "outlet_history")
public class OutletHistory {
    @Id private String id;
    private String restaurantId;
    private String outletId;
    private String fieldName;
    private Object oldValue;
    private Object newValue;
    private ChangeType changeType;
    private String changedBy;
    private Instant changedAt;
    private String changeReason;
    private String sourceIp;
    private String userAgent;
}
```

#### ContractHistory
```java
@Document(collection = "contract_history")
public class ContractHistory {
    @Id private String id;
    private String restaurantId;
    private String contractId;
    private String fieldName; // e.g., "platformFee.percentageRate"
    private Object oldValue;
    private Object newValue;
    private ChangeType changeType;
    private String changedBy;
    private Instant changedAt;
    private String changeReason; // Required for contract changes
    private Boolean requiresResigning; // If change requires new signature
    private String approvedBy; // Operations manager who approved change
    private Instant approvedAt;
}
```

#### ServiceabilityHistory
```java
@Document(collection = "serviceability_history")
public class ServiceabilityHistory {
    @Id private String id;
    private String restaurantId;
    private String outletId;
    private String changeType; // PINCODE_ADDED, PINCODE_REMOVED, RADIUS_CHANGED, RULES_UPDATED
    private List<String> addedPincodes;
    private List<String> removedPincodes;
    private Double oldRadius;
    private Double newRadius;
    private String changedBy;
    private Instant changedAt;
    private String changeReason;
}
```

#### PenaltyHistory
```java
@Document(collection = "penalty_history")
public class PenaltyHistory {
    @Id private String id;
    private String restaurantId;
    private String outletId; // If outlet-specific
    private String orderId; // If order-related
    private String penaltyType; // RATING, REVIEW, CANCELLATION, PRICING, QUALITY, DELAY, etc.
    private String violationType; // Specific violation
    private Double penaltyAmount;
    private String description;
    private PenaltyStatus status; // PENDING, APPLIED, WAIVED, DISPUTED
    private String appliedBy; // Operations team member
    private Instant appliedAt;
    private String waivedBy; // If waived
    private Instant waivedAt;
    private String waiverReason;
    private String disputeReason; // If disputed by restaurant
}
```

**ChangeType Enum**: CREATE, UPDATE, DELETE, STATUS_CHANGE, APPROVAL, REJECTION, ACTIVATION, SUSPENSION  
**PenaltyStatus Enum**: PENDING, APPLIED, WAIVED, DISPUTED, RESOLVED

### Indexes for History Tables

```java
// RestaurantHistory indexes
@CompoundIndex(name = "restaurant_field_time", def = "{'restaurantId': 1, 'fieldName': 1, 'changedAt': -1}")
@CompoundIndex(name = "restaurant_time", def = "{'restaurantId': 1, 'changedAt': -1}")

// OutletHistory indexes
@CompoundIndex(name = "outlet_field_time", def = "{'outletId': 1, 'fieldName': 1, 'changedAt': -1}")

// ContractHistory indexes
@CompoundIndex(name = "contract_time", def = "{'restaurantId': 1, 'changedAt': -1}")

// ServiceabilityHistory indexes
@CompoundIndex(name = "serviceability_outlet_time", def = "{'outletId': 1, 'changedAt': -1}")

// PenaltyHistory indexes
@CompoundIndex(name = "penalty_restaurant_time", def = "{'restaurantId': 1, 'appliedAt': -1}")
@CompoundIndex(name = "penalty_status", def = "{'status': 1, 'appliedAt': -1}")
```

### Onboarding draft (save/resume by step) – IMPLEMENTED
- **OnboardingDraft** entity (MongoDB `onboarding_drafts`), **OnboardingDraftStatus** (DRAFT, SUBMITTED).
- **OnboardingDraftService**: `createDraft()`, `getDraft(draftId)`, `saveDraft(draftId, SaveOnboardingDraftRequest)`, `submitFromDraft(SubmitOnboardingByDraftRequest)`.
- **OnboardingDraftController**: `POST /onboarding/draft`, `GET /onboarding/draft/{draftId}`, `PATCH /onboarding/draft/{draftId}`, `POST /onboarding/submit`.
- **Post-submit**: `RestaurantService.afterOnboardingSubmitted(restaurantId, SubmitOnboardingRequest)` calls **UserServiceClient** (WebClient to user-service) to create owner user, and **NotificationService** (interface; stub `LoggingNotificationService` logs email/SMS; replace with real provider).

### API Endpoints to Generate

#### Restaurant CRUD (Brand Level)
1. `POST /api/v1/restaurants` - Create restaurant (brand)
2. `GET /api/v1/restaurants/{id}` - Get restaurant by ID
3. `GET /api/v1/restaurants` - Get all restaurants (paginated)
4. `PUT /api/v1/restaurants/{id}` - Update restaurant
5. `DELETE /api/v1/restaurants/{id}` - Soft delete restaurant

#### Outlet Management
6. `POST /api/v1/restaurants/{restaurantId}/outlets` - Add new outlet
7. `GET /api/v1/restaurants/{restaurantId}/outlets` - Get all outlets for a restaurant
8. `GET /api/v1/restaurants/{restaurantId}/outlets/{outletId}` - Get specific outlet
9. `PUT /api/v1/restaurants/{restaurantId}/outlets/{outletId}` - Update outlet
10. `DELETE /api/v1/restaurants/{restaurantId}/outlets/{outletId}` - Soft delete outlet
11. `PATCH /api/v1/restaurants/{restaurantId}/outlets/{outletId}/status` - Update outlet status

#### Onboarding & Approval (Brand + Outlet Level)
12. `POST /api/v1/restaurants/onboard/self-service` - Self-service restaurant onboarding
13. `POST /api/v1/restaurants/onboard/assisted` - Assisted onboarding
14. `POST /api/v1/restaurants/{restaurantId}/outlets/{outletId}/approve` - Approve outlet
15. `POST /api/v1/restaurants/{restaurantId}/outlets/{outletId}/reject` - Reject outlet
16. `GET /api/v1/restaurants/outlets/pending-approval` - Get all outlets pending approval

#### Serviceability Management
17. `PUT /api/v1/restaurants/{restaurantId}/outlets/{outletId}/serviceability` - Update serviceability config
18. `GET /api/v1/restaurants/{restaurantId}/outlets/{outletId}/serviceability` - Get serviceability config
19. `POST /api/v1/restaurants/{restaurantId}/outlets/{outletId}/serviceability/check` - Check if pincode is serviceable

#### TAT Management
20. `PUT /api/v1/restaurants/{restaurantId}/outlets/{outletId}/tat-config` - Update TAT configuration
21. `GET /api/v1/restaurants/{restaurantId}/outlets/{outletId}/tat-config` - Get TAT configuration
22. `POST /api/v1/restaurants/{restaurantId}/outlets/{outletId}/tat/calculate` - Calculate TAT for given order params

#### Search & Discovery
23. `GET /api/v1/restaurants/search` - Search restaurants by name, city, cuisine
24. `GET /api/v1/restaurants/outlets/search` - Search outlets by location, pincode
25. `GET /api/v1/restaurants/by-owner/{ownerId}` - Get restaurants by owner
26. `GET /api/v1/restaurants/outlets/by-pincode/{pincode}` - Get outlets serving a pincode
27. `GET /api/v1/restaurants/outlets/by-status/{status}` - Get outlets by status

#### Contract Management
28. `PUT /api/v1/restaurants/{restaurantId}/contract` - Update contract
29. `POST /api/v1/restaurants/{restaurantId}/contract/resign` - Request contract re-signing
30. `GET /api/v1/restaurants/{restaurantId}/contract` - Get contract details
31. `GET /api/v1/restaurants/{restaurantId}/contract/history` - Get contract change history

#### Owner Management
32. `POST /api/v1/restaurants/{restaurantId}/owners` - Add new owner
33. `PUT /api/v1/restaurants/{restaurantId}/owners/{ownerId}` - Update owner details
34. `DELETE /api/v1/restaurants/{restaurantId}/owners/{ownerId}` - Remove owner
35. `GET /api/v1/restaurants/{restaurantId}/owners` - Get all owners

#### Contact Management
36. `POST /api/v1/restaurants/{restaurantId}/contacts` - Add contact (brand level)
37. `PUT /api/v1/restaurants/{restaurantId}/contacts/{contactId}` - Update contact
38. `DELETE /api/v1/restaurants/{restaurantId}/contacts/{contactId}` - Remove contact
39. `POST /api/v1/restaurants/{restaurantId}/outlets/{outletId}/contacts` - Add outlet contact
40. `PUT /api/v1/restaurants/{restaurantId}/outlets/{outletId}/contacts/{contactId}` - Update outlet contact
41. `DELETE /api/v1/restaurants/{restaurantId}/outlets/{outletId}/contacts/{contactId}` - Remove outlet contact

#### History & Audit
42. `GET /api/v1/restaurants/{restaurantId}/history` - Get restaurant change history
43. `GET /api/v1/restaurants/{restaurantId}/outlets/{outletId}/history` - Get outlet change history
44. `GET /api/v1/restaurants/{restaurantId}/serviceability/history` - Get serviceability change history
45. `GET /api/v1/restaurants/{restaurantId}/penalties` - Get penalty history
46. `POST /api/v1/restaurants/{restaurantId}/penalties/{penaltyId}/dispute` - Dispute a penalty
47. `POST /api/v1/restaurants/{restaurantId}/penalties/{penaltyId}/waive` - Waive a penalty (ops only)

### DTOs to Generate

#### Request DTOs - Restaurant (Brand Level)
- `CreateRestaurantRequest`: Brand info, multiple owners, contacts, cuisines, documents, contract
- `UpdateRestaurantRequest`: Updateable brand fields
- `SelfServiceOnboardingRequest`: Complete brand onboarding with owners
- `AssistedOnboardingRequest`: Similar + onboardedBy field

#### Request DTOs - Outlet Level
- `CreateOutletRequest`: Outlet name, address, multiple contacts, operating hours, serviceability pincodes, TAT config
- `UpdateOutletRequest`: Updateable outlet fields
- `ApproveOutletRequest`: approvedBy, approval notes
- `RejectOutletRequest`: rejectionReason
- `UpdateOutletStatusRequest`: newStatus, reason
- `UpdateServiceabilityRequest`: serviceablePincodes list, radius, rules
- `UpdateTATConfigRequest`: TAT configuration parameters
- `CalculateTATRequest`: orderId, deliveryPincode, currentTime, orderItems count

#### Request DTOs - Contract
- `UpdateContractRequest`: Platform fee, delivery fee, payment gateway fee, penalties
- `RequestContractResigningRequest`: Reason for re-signing, changes made

#### Request DTOs - Owner & Contact
- `AddOwnerRequest`: Owner details, ownership %, role
- `UpdateOwnerRequest`: Updateable owner fields
- `AddContactRequest`: Contact details, type, designation
- `UpdateContactRequest`: Updateable contact fields

#### Request DTOs - Penalty
- `ApplyPenaltyRequest`: Penalty type, amount, reason, restaurant/outlet/order ID
- `DisputePenaltyRequest`: Dispute reason, supporting documents
- `WaivePenaltyRequest`: Waiver reason (ops only)

#### Response DTOs - Restaurant
- `RestaurantResponse`: Full restaurant with all outlets, owners, contacts, contract
- `RestaurantSummaryResponse`: Brief restaurant summary
- `OnboardingStatusResponse`: Onboarding progress
- `OwnerResponse`: Owner details
- `ContactResponse`: Contact details

#### Response DTOs - Outlet
- `OutletResponse`: Full outlet details with contacts
- `OutletSummaryResponse`: Brief outlet summary for listings
- `ServiceabilityResponse`: Serviceability configuration with pincodes
- `TATResponse`: Calculated TAT with breakdown
- `TATConfigResponse`: TAT configuration details

#### Response DTOs - Contract
- `ContractResponse`: Complete contract with fee structures and penalties
- `ContractSummaryResponse`: Brief contract summary
- `FeeBreakdownResponse`: Detailed fee breakdown for an order

#### Response DTOs - History & Audit
- `RestaurantHistoryResponse`: Change history entry
- `OutletHistoryResponse`: Outlet change history entry
- `ContractHistoryResponse`: Contract change history entry
- `ServiceabilityHistoryResponse`: Serviceability change history
- `PenaltyHistoryResponse`: Penalty history entry with status

### Domain Services

**RestaurantValidationService**:
- `validateForCreation()`: Validate restaurant before creation
- `validateDocuments()`: Validate document uploads (FSSAI, GST, PAN, Bank)
- `validateContractSigning()`: Validate contract is signed before activation

**OutletValidationService**:
- `validateForCreation()`: Validate outlet before creation
- `validateOperatingHours()`: Validate hours don't overlap
- `validateAddress()`: Validate address and geo-coordinates
- `validateServiceabilityConfig()`: Validate serviceability rules

**RestaurantApprovalService**:
- `approveRestaurant()`: Approve restaurant brand (contract level)
- `rejectRestaurant()`: Reject restaurant with reason

**OutletApprovalService**:
- `approveOutlet()`: Approve outlet and change status to APPROVED
- `rejectOutlet()`: Reject outlet with reason
- `activateOutlet()`: Activate approved outlet (change to ACTIVE)
- `suspendOutlet()`: Suspend active outlet

**ServiceabilityService**:
- `checkPincodeServiceability()`: Check if a pincode is serviceable by outlet
- `getServiceableOutlets()`: Get all outlets serving a specific pincode
- `updateServiceabilityConfig()`: Update outlet's serviceability configuration
- `validateServiceabilityRules()`: Validate serviceability rules (inventory, capacity)

**TATCalculationService**:
- `calculateTAT()`: Calculate TAT for a given order
  - Consider base prep time
  - Add queue time (orders in queue × tatPerOrderInQueue)
  - Add peak hour time (if current time is in peak hours)
  - Consider rider availability (from rider-service via API/event)
  - Consider batching potential (from delivery optimization)
  - Consider delivery distance and route
- `getTATBreakdown()`: Get detailed breakdown of TAT calculation
- `updateTATConfig()`: Update outlet's TAT configuration
- `isPeakHour()`: Check if current time is peak hour

**ContractService**:
- `updateContract()`: Update contract terms
- `calculatePlatformFee()`: Calculate platform fee for an order
- `calculateDeliveryFee()`: Calculate delivery fee based on contract
- `calculatePaymentGatewayFee()`: Calculate PG fee for an order
- `calculateTotalFees()`: Get complete fee breakdown for an order
- `validateContractChanges()`: Validate if contract changes are allowed
- `requiresResigning()`: Check if changes require new signature
- `generateContractDocument()`: Generate contract PDF

**PenaltyService**:
- `applyPenalty()`: Apply penalty to restaurant/outlet
- `calculateRatingPenalty()`: Auto-calculate penalty based on rating
- `calculateCancellationPenalty()`: Calculate penalty for cancellation
- `calculateDelayPenalty()`: Calculate penalty for delivery delay
- `disputePenalty()`: Restaurant disputes a penalty
- `waivePenalty()`: Operations waives a penalty
- `resolvePenaltyDispute()`: Resolve disputed penalty
- `getPendingPenalties()`: Get all pending penalties
- `getMonthlyPenaltySummary()`: Get penalty summary for billing

**OwnerService**:
- `addOwner()`: Add new owner/partner
- `updateOwner()`: Update owner details
- `removeOwner()`: Remove owner (validate constraints)
- `validateOwnershipPercentages()`: Ensure total ownership = 100%
- `transferPrimaryOwnership()`: Transfer primary owner role

**ContactService**:
- `addContact()`: Add new contact
- `updateContact()`: Update contact details
- `removeContact()`: Remove contact
- `setPrimaryContact()`: Set primary contact
- `getActiveContacts()`: Get all active contacts
- `notifyContacts()`: Send notifications to relevant contacts

**HistoryService**:
- `recordChange()`: Record change in history table
- `getChangeHistory()`: Get change history for entity
- `getFieldHistory()`: Get history for specific field
- `compareVersions()`: Compare two versions of entity
- `auditTrail()`: Generate complete audit trail
- `detectAnomalies()`: Detect suspicious changes

### Repository Methods

```java
public interface RestaurantRepository extends MongoRepository<Restaurant, String> {
    // Restaurant (Brand Level) Queries
    Optional<Restaurant> findByIdAndDeletedFalse(String id);
    Page<Restaurant> findByDeletedFalse(Pageable pageable);
    List<Restaurant> findByOwnerIdAndDeletedFalse(String ownerId);
    Page<Restaurant> findByNameContainingIgnoreCaseOrCuisineTypesContaining(
        String name, String cuisine, Pageable pageable);
    
    // Outlet Queries (using MongoDB's array queries)
    @Query("{'outlets.outletId': ?0, 'outlets.deleted': false}")
    Optional<Restaurant> findByOutletId(String outletId);
    
    @Query("{'outlets.status': ?0, 'outlets.deleted': false}")
    List<Restaurant> findByOutletStatus(OutletStatus status);
    
    @Query("{'outlets.address.pincode': ?0, 'outlets.deleted': false, 'outlets.status': 'ACTIVE'}")
    List<Restaurant> findByOutletAddressPincode(String pincode);
    
    @Query("{'outlets.address.city': ?0, 'outlets.deleted': false, 'outlets.status': 'ACTIVE'}")
    List<Restaurant> findByOutletCity(String city);
    
    // Serviceability Queries
    @Query("{'outlets.serviceabilityConfig.serviceablePincodes': ?0, 'outlets.deleted': false, 'outlets.status': 'ACTIVE', 'outlets.acceptsOrders': true}")
    List<Restaurant> findRestaurantsServicingPincode(String pincode);
    
    // TAT and Capacity Queries
    @Query("{'outlets.outletId': ?0}")
    Optional<Restaurant> findRestaurantByOutletId(String outletId);
    
    // Geo-spatial queries for radius-based serviceability
    @Query("{'outlets.address.latitude': {$gte: ?0, $lte: ?1}, 'outlets.address.longitude': {$gte: ?2, $lte: ?3}, 'outlets.deleted': false, 'outlets.status': 'ACTIVE'}")
    List<Restaurant> findOutletsInBoundingBox(Double minLat, Double maxLat, Double minLon, Double maxLon);
    
    // Owner queries
    @Query("{'owners.ownerId': ?0}")
    List<Restaurant> findByOwnerId(String ownerId);
}
```

### History Repository Interfaces

```java
public interface RestaurantHistoryRepository extends MongoRepository<RestaurantHistory, String> {
    List<RestaurantHistory> findByRestaurantIdOrderByChangedAtDesc(String restaurantId);
    List<RestaurantHistory> findByRestaurantIdAndFieldNameOrderByChangedAtDesc(String restaurantId, String fieldName);
    List<RestaurantHistory> findByChangedByOrderByChangedAtDesc(String changedBy);
    
    @Query("{'restaurantId': ?0, 'changedAt': {$gte: ?1, $lte: ?2}}")
    List<RestaurantHistory> findByRestaurantIdAndDateRange(String restaurantId, Instant fromDate, Instant toDate);
}

public interface OutletHistoryRepository extends MongoRepository<OutletHistory, String> {
    List<OutletHistory> findByOutletIdOrderByChangedAtDesc(String outletId);
    List<OutletHistory> findByRestaurantIdAndOutletIdOrderByChangedAtDesc(String restaurantId, String outletId);
    List<OutletHistory> findByOutletIdAndFieldNameOrderByChangedAtDesc(String outletId, String fieldName);
}

public interface ContractHistoryRepository extends MongoRepository<ContractHistory, String> {
    List<ContractHistory> findByRestaurantIdOrderByChangedAtDesc(String restaurantId);
    List<ContractHistory> findByContractIdOrderByChangedAtDesc(String contractId);
    List<ContractHistory> findByRequiresResigningTrue();
}

public interface ServiceabilityHistoryRepository extends MongoRepository<ServiceabilityHistory, String> {
    List<ServiceabilityHistory> findByOutletIdOrderByChangedAtDesc(String outletId);
    List<ServiceabilityHistory> findByRestaurantIdOrderByChangedAtDesc(String restaurantId);
}

public interface PenaltyHistoryRepository extends MongoRepository<PenaltyHistory, String> {
    List<PenaltyHistory> findByRestaurantIdOrderByAppliedAtDesc(String restaurantId);
    List<PenaltyHistory> findByOutletIdOrderByAppliedAtDesc(String outletId);
    List<PenaltyHistory> findByStatusOrderByAppliedAtDesc(PenaltyStatus status);
    List<PenaltyHistory> findByRestaurantIdAndStatus(String restaurantId, PenaltyStatus status);
    
    @Query("{'restaurantId': ?0, 'appliedAt': {$gte: ?1, $lte: ?2}}")
    List<PenaltyHistory> findByRestaurantIdAndDateRange(String restaurantId, Instant fromDate, Instant toDate);
    
    @Aggregation(pipeline = {
        "{ $match: { 'restaurantId': ?0, 'status': 'APPLIED', 'appliedAt': {$gte: ?1, $lte: ?2} } }",
        "{ $group: { _id: '$penaltyType', totalAmount: { $sum: '$penaltyAmount' }, count: { $sum: 1 } } }"
    })
    List<PenaltySummary> getPenaltySummaryByType(String restaurantId, Instant fromDate, Instant toDate);
}
```

### Events to Publish (Kafka)

```java
// Topics - Restaurant (Brand Level)
"restaurant.registered"   // When restaurant brand completes registration
"restaurant.approved"     // When restaurant brand approved
"restaurant.rejected"     // When restaurant brand rejected
"restaurant.updated"      // When restaurant details updated

// Topics - Outlet Level
"outlet.created"          // When new outlet is added
"outlet.approved"         // When outlet is approved
"outlet.rejected"         // When outlet is rejected
"outlet.activated"        // When outlet status changed to ACTIVE
"outlet.suspended"        // When outlet is suspended
"outlet.updated"          // When outlet details updated
"outlet.deleted"          // When outlet is soft deleted

// Topics - Serviceability
"outlet.serviceability.updated"  // When serviceability config updated
"outlet.pincode.added"           // When new pincode added to serviceability
"outlet.pincode.removed"         // When pincode removed from serviceability

// Topics - TAT
"outlet.tat.config.updated"      // When TAT configuration updated
"outlet.tat.calculated"          // When TAT is calculated for an order (for analytics)

// Topics - Contract
"contract.created"               // When contract is created
"contract.updated"               // When contract terms updated
"contract.signed"                // When contract is signed
"contract.resigned"              // When contract is re-signed after changes
"fee.structure.changed"          // When fee structure is modified

// Topics - Owner Management
"owner.added"                    // When new owner added
"owner.removed"                  // When owner removed
"owner.updated"                  // When owner details updated
"ownership.transferred"          // When primary ownership transferred

// Topics - Contact Management
"contact.added"                  // When new contact added
"contact.updated"                // When contact details updated
"contact.removed"                // When contact removed

// Topics - Penalty
"penalty.applied"                // When penalty is applied
"penalty.disputed"               // When restaurant disputes penalty
"penalty.waived"                 // When penalty is waived
"penalty.resolved"               // When dispute is resolved

// Event Format - Restaurant Level
{
  "eventId": "uuid",
  "eventType": "restaurant.approved",
  "timestamp": "2025-01-15T10:30:00Z",
  "version": "1.0",
  "payload": {
    "restaurantId": "restaurant-123",
    "name": "Awesome Restaurant Chain",
    "ownerId": "owner-123",
    "totalOutlets": 3
  },
  "metadata": {
    "approvedBy": "admin-456",
    "correlationId": "corr-123",
    "source": "restaurant-service"
  }
}

// Event Format - Outlet Level
{
  "eventId": "uuid",
  "eventType": "outlet.activated",
  "timestamp": "2025-01-15T10:30:00Z",
  "version": "1.0",
  "payload": {
    "restaurantId": "restaurant-123",
    "outletId": "outlet-456",
    "outletName": "MG Road Branch",
    "address": {
      "city": "Mumbai",
      "pincode": "400001",
      "latitude": 19.0760,
      "longitude": 72.8777
    },
    "status": "ACTIVE",
    "serviceablePincodes": ["400001", "400002", "400003"]
  },
  "metadata": {
    "activatedBy": "admin-456",
    "correlationId": "corr-123",
    "source": "restaurant-service"
  }
}

// Event Format - TAT Calculation (for analytics)
{
  "eventId": "uuid",
  "eventType": "outlet.tat.calculated",
  "timestamp": "2025-01-15T10:30:00Z",
  "version": "1.0",
  "payload": {
    "restaurantId": "restaurant-123",
    "outletId": "outlet-456",
    "orderId": "order-789",
    "calculatedTATMinutes": 45,
    "breakdown": {
      "basePrepTime": 25,
      "queueTime": 10,
      "peakHourExtra": 5,
      "deliveryTime": 15,
      "bufferTime": 5
    },
    "factors": {
      "ordersInQueue": 5,
      "isPeakHour": true,
      "riderAvailable": true,
      "batchingApplied": false,
      "deliveryDistance": 3.5
    }
  },
  "metadata": {
    "correlationId": "order-789",
    "source": "restaurant-service"
  }
}
```

### Testing Requirements

**Unit Tests** (90%+ coverage for domain):
- `RestaurantTest`: Domain entity tests
- `RestaurantServiceTest`: Service layer with mocked repository
- `RestaurantValidationServiceTest`: Validation logic
- `RestaurantApprovalServiceTest`: Approval workflow

**Integration Tests** (70%+ coverage):
- `RestaurantIntegrationTest`: End-to-end API tests with Testcontainers
- `RestaurantRepositoryTest`: Repository tests with real MongoDB

**Controller Tests** (80%+ coverage):
- `RestaurantControllerTest`: MockMvc tests for all endpoints

### Configuration Files

**application.yml**:
```yaml
spring:
  application:
    name: restaurant-service
  profiles:
    active: @spring.profiles.active@

server:
  port: 8081

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html

management:
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
```

**application-dev.yml**:
```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/foodai_restaurant_dev
  kafka:
    bootstrap-servers: localhost:9092
  redis:
    host: localhost
    port: 6379

logging:
  level:
    com.foodai: DEBUG
```

**logback-spring.xml**: Structured JSON logging with Logstash encoder

### Validation Rules

1. **Name**: @NotBlank, @Size(min=2, max=100)
2. **Phone**: @Pattern(regexp="^[6-9]\\d{9}$")
3. **Email**: @Email, @NotBlank
4. **Pincode**: @Pattern(regexp="^[1-9][0-9]{5}$")
5. **FSSAI Number**: Required, @Pattern(regexp="^[0-9]{14}$")
6. **GST Number**: Required, @Pattern(regexp="^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$")
7. **Minimum Order Value**: @Min(0)
8. **Delivery Radius**: @Min(1), @Max(50)

### Error Handling

Custom exceptions to create:
- `RestaurantNotFoundException`: When restaurant not found
- `RestaurantAlreadyExistsException`: When owner already has restaurant
- `InvalidRestaurantStatusException`: Invalid status transition
- `DocumentVerificationException`: Document validation failed

### Cache Strategy (Redis)

```java
@Cacheable(value = "restaurants", key = "#id")
public RestaurantResponse findById(String id)

@CacheEvict(value = "restaurants", key = "#id")
public RestaurantResponse update(String id, UpdateRestaurantRequest request)

@CacheEvict(value = "restaurants", allEntries = true)
public void clearAllCache()
```

## Generation Instructions

### Step 1: Generate Complete Module Structure
```
restaurant-service/
├── src/
│   ├── main/
│   │   ├── java/com/foodai/restaurant/
│   │   │   ├── RestaurantApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── RestaurantController.java
│   │   │   │   ├── OutletController.java
│   │   │   │   ├── ServiceabilityController.java
│   │   │   │   ├── TATController.java
│   │   │   │   ├── ContractController.java
│   │   │   │   ├── OwnerController.java
│   │   │   │   ├── ContactController.java
│   │   │   │   ├── HistoryController.java
│   │   │   │   └── PenaltyController.java
│   │   │   ├── service/
│   │   │   │   ├── RestaurantService.java
│   │   │   │   ├── OutletService.java
│   │   │   │   ├── ServiceabilityService.java
│   │   │   │   ├── TATCalculationService.java
│   │   │   │   ├── ContractService.java
│   │   │   │   ├── PenaltyService.java
│   │   │   │   ├── OwnerService.java
│   │   │   │   ├── ContactService.java
│   │   │   │   └── HistoryService.java
│   │   │   ├── domain/
│   │   │   │   ├── model/
│   │   │   │   │   ├── Restaurant.java (Aggregate Root)
│   │   │   │   │   ├── RestaurantOutlet.java (Entity)
│   │   │   │   │   ├── RestaurantHistory.java
│   │   │   │   │   ├── OutletHistory.java
│   │   │   │   │   ├── ContractHistory.java
│   │   │   │   │   ├── ServiceabilityHistory.java
│   │   │   │   │   ├── PenaltyHistory.java
│   │   │   │   │   ├── OwnerVO.java
│   │   │   │   │   ├── AddressVO.java
│   │   │   │   │   ├── ContactVO.java
│   │   │   │   │   ├── OperatingHoursVO.java
│   │   │   │   │   ├── DocumentVO.java
│   │   │   │   │   ├── ContractVO.java
│   │   │   │   │   ├── PlatformFeeConfig.java
│   │   │   │   │   ├── DeliveryFeeConfig.java
│   │   │   │   │   ├── PaymentGatewayFeeConfig.java
│   │   │   │   │   ├── PenaltyConfig.java
│   │   │   │   │   ├── RatingPenaltyVO.java
│   │   │   │   │   ├── ReviewPenaltyVO.java
│   │   │   │   │   ├── CancellationPenaltyVO.java
│   │   │   │   │   ├── PricingViolationPenaltyVO.java
│   │   │   │   │   ├── QualityViolationPenaltyVO.java
│   │   │   │   │   ├── DelayPenaltyVO.java
│   │   │   │   │   ├── ViolationPenaltyVO.java
│   │   │   │   │   ├── FeeSlabVO.java
│   │   │   │   │   ├── DistanceFeeSlabVO.java
│   │   │   │   │   ├── ServiceabilityConfig.java
│   │   │   │   │   ├── ServiceabilityRules.java
│   │   │   │   │   ├── TATConfig.java
│   │   │   │   │   ├── TimeRangeVO.java
│   │   │   │   │   ├── RestaurantStatus.java (enum)
│   │   │   │   │   ├── OutletStatus.java (enum)
│   │   │   │   │   ├── OnboardingType.java (enum)
│   │   │   │   │   ├── DocumentType.java (enum)
│   │   │   │   │   ├── DocumentStatus.java (enum)
│   │   │   │   │   ├── OwnerRole.java (enum)
│   │   │   │   │   ├── ContactType.java (enum)
│   │   │   │   │   ├── FeeType.java (enum)
│   │   │   │   │   ├── DeliveryFeePayor.java (enum)
│   │   │   │   │   ├── PaymentFeePayor.java (enum)
│   │   │   │   │   ├── PenaltyAction.java (enum)
│   │   │   │   │   ├── ChangeType.java (enum)
│   │   │   │   │   └── PenaltyStatus.java (enum)
│   │   │   │   ├── repository/
│   │   │   │   │   ├── RestaurantRepository.java
│   │   │   │   │   ├── RestaurantHistoryRepository.java
│   │   │   │   │   ├── OutletHistoryRepository.java
│   │   │   │   │   ├── ContractHistoryRepository.java
│   │   │   │   │   ├── ServiceabilityHistoryRepository.java
│   │   │   │   │   └── PenaltyHistoryRepository.java
│   │   │   │   └── service/
│   │   │   │       ├── RestaurantValidationService.java
│   │   │   │       ├── OutletValidationService.java
│   │   │   │       ├── RestaurantApprovalService.java
│   │   │   │       └── OutletApprovalService.java
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   │   ├── CreateRestaurantRequest.java
│   │   │   │   │   ├── UpdateRestaurantRequest.java
│   │   │   │   │   ├── CreateOutletRequest.java
│   │   │   │   │   ├── UpdateOutletRequest.java
│   │   │   │   │   ├── SelfServiceOnboardingRequest.java
│   │   │   │   │   ├── AssistedOnboardingRequest.java
│   │   │   │   │   ├── ApproveOutletRequest.java
│   │   │   │   │   ├── RejectOutletRequest.java
│   │   │   │   │   ├── UpdateOutletStatusRequest.java
│   │   │   │   │   ├── UpdateServiceabilityRequest.java
│   │   │   │   │   ├── UpdateTATConfigRequest.java
│   │   │   │   │   └── CalculateTATRequest.java
│   │   │   │   └── response/
│   │   │   │       ├── RestaurantResponse.java
│   │   │   │       ├── RestaurantSummaryResponse.java
│   │   │   │       ├── OutletResponse.java
│   │   │   │       ├── OutletSummaryResponse.java
│   │   │   │       ├── OnboardingStatusResponse.java
│   │   │   │       ├── ServiceabilityResponse.java
│   │   │   │       ├── TATResponse.java
│   │   │   │       └── TATConfigResponse.java
│   │   │   ├── mapper/
│   │   │   │   ├── RestaurantMapper.java
│   │   │   │   └── OutletMapper.java
│   │   │   ├── config/
│   │   │   │   ├── MongoConfig.java
│   │   │   │   ├── RedisConfig.java
│   │   │   │   └── KafkaConfig.java
│   │   │   └── exception/
│   │   │       ├── RestaurantNotFoundException.java
│   │   │       ├── OutletNotFoundException.java
│   │   │       ├── RestaurantAlreadyExistsException.java
│   │   │       ├── InvalidRestaurantStatusException.java
│   │   │       ├── InvalidOutletStatusException.java
│   │   │       ├── DocumentVerificationException.java
│   │   │       └── ServiceabilityException.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-staging.yml
│   │       ├── application-prod.yml
│   │       └── logback-spring.xml
│   └── test/
│       ├── java/com/foodai/restaurant/
│       │   ├── controller/
│       │   │   ├── RestaurantControllerTest.java
│       │   │   ├── OutletControllerTest.java
│       │   │   ├── ServiceabilityControllerTest.java
│       │   │   └── TATControllerTest.java
│       │   ├── service/
│       │   │   ├── RestaurantServiceTest.java
│       │   │   ├── OutletServiceTest.java
│       │   │   ├── ServiceabilityServiceTest.java
│       │   │   ├── TATCalculationServiceTest.java
│       │   │   ├── RestaurantValidationServiceTest.java
│       │   │   ├── OutletValidationServiceTest.java
│       │   │   ├── RestaurantApprovalServiceTest.java
│       │   │   └── OutletApprovalServiceTest.java
│       │   ├── domain/
│       │   │   └── model/
│       │   │       ├── RestaurantTest.java
│       │   │       └── RestaurantOutletTest.java
│       │   ├── repository/
│       │   │   └── RestaurantRepositoryTest.java
│       │   └── integration/
│       │       ├── RestaurantIntegrationTest.java
│       │       ├── OutletIntegrationTest.java
│       │       └── ServiceabilityIntegrationTest.java
│       └── resources/
│           └── application-test.yml
```

### Step 2: Follow All Guardrails

**MANDATORY RULES** (from `backend/.cursor/rules`):
1. ✅ Use DDD principles - domain logic in domain layer
2. ✅ Follow layered architecture: Controller → Service → Domain → Repository
3. ✅ Use MapStruct for all mappings
4. ✅ Add Jakarta validation annotations on all DTOs
5. ✅ Implement soft delete (deleted boolean field)
6. ✅ Add @CreatedDate, @LastModifiedDate for auditing
7. ✅ Use structured logging with correlation ID
8. ✅ Add OpenAPI documentation (@Operation, @ApiResponse)
9. ✅ Return standardized ApiResponse<T> from controllers
10. ✅ Implement global exception handler
11. ✅ Use Testcontainers for integration tests
12. ✅ **80%+ code coverage MANDATORY**
13. ✅ **ALL tests MUST pass**

### Step 3: Generate Tests

For EACH class, generate corresponding test class:

**Example: RestaurantServiceTest**
```java
@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {
    @Mock private RestaurantRepository repository;
    @Mock private RestaurantMapper mapper;
    @Mock private KafkaTemplate<String, Object> kafkaTemplate;
    @InjectMocks private RestaurantService service;
    
    // Test methods:
    // - shouldCreateRestaurantSuccessfully_whenValidInput()
    // - shouldThrowValidationException_whenInvalidData()
    // - shouldReturnRestaurant_whenExists()
    // - shouldThrowNotFoundException_whenNotExists()
    // - shouldUpdateRestaurant_whenValid()
    // - shouldSoftDelete_whenRestaurantExists()
    // - shouldPublishEvent_whenRestaurantCreated()
}
```

**Example: RestaurantIntegrationTest**
```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
class RestaurantIntegrationTest {
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");
    
    @LocalServerPort private int port;
    
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }
    
    // Test methods using REST Assured
}
```

### Step 4: Verify Completion

**Before considering the task DONE, verify:**

1. ✅ All files in the structure exist
2. ✅ All classes have JavaDoc
3. ✅ All DTOs have validation annotations
4. ✅ All controllers have OpenAPI documentation
5. ✅ All tests are written
6. ✅ Run: `mvn clean test` - ALL tests pass
7. ✅ Run: `mvn jacoco:report` - Coverage >= 80%
8. ✅ No compiler errors
9. ✅ No linting errors
10. ✅ Structured logging in all services

## Expected Output

When complete, I should be able to:

```bash
cd restaurant-service
mvn clean test                      # ✅ All tests pass
mvn jacoco:report                   # ✅ Coverage >= 80%
mvn spring-boot:run                 # ✅ Service starts
curl http://localhost:8081/actuator/health  # ✅ Returns UP
curl http://localhost:8081/swagger-ui.html  # ✅ Opens Swagger UI
```

## Success Criteria

### Domain & Architecture
✅ Complete module structure created  
✅ Restaurant (Aggregate Root) with outlets collection and multiple owners  
✅ RestaurantOutlet entity with multiple contacts and all value objects  
✅ All value objects (40+ VOs including OwnerVO, ContactVO, ContractVO with fee structures, PenaltyConfig with 7+ penalty types)  
✅ History tables for audit trail (5 collections)  
✅ Repository with complex queries (brand, outlet, history, penalty level)  

### Business Logic
✅ Service layer for Restaurant (brand level)  
✅ Service layer for Outlet management  
✅ ServiceabilityService with pincode checking  
✅ TATCalculationService with dynamic TAT calculation  
✅ Validation services (Restaurant & Outlet)  
✅ Approval services (Restaurant & Outlet)  

### API Layer
✅ RestaurantController with 5 endpoints (CRUD)  
✅ OutletController with 6 endpoints (outlet management)  
✅ ServiceabilityController with 3 endpoints  
✅ TATController with 3 endpoints  
✅ ContractController with 4 endpoints (contract management)  
✅ OwnerController with 4 endpoints (owner management)  
✅ ContactController with 6 endpoints (contact management)  
✅ HistoryController with 4 endpoints (audit trail)  
✅ PenaltyController with 3 endpoints (penalty management)  
✅ **47 total API endpoints** with comprehensive OpenAPI documentation  

### Data & Integration
✅ DTOs with Jakarta validation (25+ request DTOs, 20+ response DTOs)  
✅ MapStruct mappers (RestaurantMapper, OutletMapper, ContractMapper, HistoryMapper)  
✅ Exception handling (10+ custom exceptions)  
✅ Configurations (MongoDB, Redis, Kafka)  
✅ **30+ Kafka events** (restaurant, outlet, serviceability, TAT, contract, owner, contact, penalty)  
✅ Caching with Redis (outlet lookups, serviceability checks, contract details)  
✅ **5 History Collections** for complete audit trail  

### Testing
✅ Unit tests for all services (90%+ coverage)  
✅ Unit tests for domain models  
✅ Controller tests with MockMvc (80%+ coverage)  
✅ Repository tests with Testcontainers  
✅ Integration tests (Restaurant, Outlet, Serviceability)  
✅ **Code coverage >= 80% MANDATORY**  
✅ **ALL tests pass**  

### Documentation
✅ README.md with setup instructions  
✅ OpenAPI/Swagger documentation  
✅ JavaDoc for all public methods  
✅ Configuration examples (dev, staging, prod)  

### Key Features Implemented

**Multi-Ownership & Contact Management:**
✅ Multiple owners per restaurant with ownership percentages  
✅ Owner roles (PRIMARY_OWNER, CO_OWNER, PARTNER, INVESTOR)  
✅ Multiple contacts per restaurant and outlet  
✅ Contact types (PRIMARY, SECONDARY, EMERGENCY, MANAGER, SUPPORT)  

**Multi-Outlet Management:**
✅ Multi-outlet support per restaurant (chain/brand)  
✅ Outlet-level status management (6 statuses)  
✅ Independent outlet approval workflows  

**Serviceability:**
✅ Pincode-based serviceability (explicit list per outlet)  
✅ Radius-based serviceability  
✅ Serviceability rules (inventory, capacity, max orders/hour)  
✅ Excluded pincodes support  

**Dynamic TAT Calculation:**
✅ Base prep time + Order queue + Peak hours  
✅ Rider availability + Batching potential + Delivery distance  
✅ Configurable per outlet with buffer time  

**Comprehensive Contract & Fee Management:**
✅ Platform fee (% or fixed with min/max caps, tiered slabs)  
✅ Delivery fee (who pays: restaurant/customer/split/platform)  
✅ Payment gateway fee (% or fixed)  
✅ Distance-based fee slabs  

**Automated Penalty System:**
✅ Rating-based penalties (thresholds, auto-deduction)  
✅ Review-based penalties (negative review tracking)  
✅ Cancellation penalties (escalating, rate-based)  
✅ Pricing violation penalties (unauthorized increases, MRP violations)  
✅ Quality violation penalties (food quality, hygiene, packaging)  
✅ Delay penalties (TAT violation, per-minute charges)  
✅ Penalty dispute & waiver workflows  

**Complete Audit Trail:**
✅ History tables for all critical entities  
✅ Field-level change tracking  
✅ CreatedBy, UpdatedBy, DeletedBy for all entities  
✅ Change reasons and source IP tracking  
✅ Contract change history with re-signing requirement  
✅ Penalty history with status tracking  

**Other Features:**
✅ Self-service & assisted onboarding  
✅ Document verification (FSSAI, GST, PAN, Bank)  
✅ Search & discovery APIs  
✅ Fee calculation APIs for orders  
✅ Monthly penalty summary reports  

---

**Now generate the complete restaurant-service following this specification!**

