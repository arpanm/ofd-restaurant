# insert-mock-data.js Fix - Complete Rewrite

**Date**: November 25, 2025  
**Status**: ✅ Fixed and Verified

## Summary

The `insert-mock-data.js` script has been completely rewritten to match the exact data structure that the REST API creates. The new script inserts data directly into MongoDB with the correct field names, nested structures, and enum values.

## What Was Fixed

### Before (Old Structure)
The old script had major incompatibilities:

```javascript
// ❌ OLD - Wrong field names
owners: [
    {
        name: "Mario Rossi",           // Wrong: should be ownerName
        email: "mario@...",             // Wrong: should be ownerEmail
        phone: "+91...",                // Wrong: should be ownerPhone
        role: "PRIMARY_OWNER",
        sharePercentage: 100.0          // Wrong: should be ownershipPercentage
        // Missing: isPrimaryContact
    }
]

// ❌ OLD - Invalid ContactType
contact: {
    type: "PHONE",                      // Wrong: PHONE doesn't exist
    value: "+91...",
    isPrimary: true
}

// ❌ OLD - Wrong contract structure
contract: {
    platformFee: {
        feeType: "PERCENTAGE",
        value: 20.0,                    // Wrong: should be percentageRate
        minimumFee: 10.0,               // Wrong: should be minFeePerOrder
        maximumFee: 100.0               // Wrong: should be maxFeePerOrder
    }
}
```

### After (New Structure)
The new script matches the API structure exactly:

```javascript
// ✅ NEW - Correct field names
owners: [
    {
        ownerId: null,
        ownerName: "Mario Rossi",       // ✅ Correct
        ownerEmail: "mario@...",        // ✅ Correct
        ownerPhone: "9876543210",       // ✅ Correct
        ownershipPercentage: 100.0,     // ✅ Correct
        role: "PRIMARY_OWNER",
        isPrimaryContact: true,         // ✅ Added
        addedAt: null,
        addedBy: null
    }
]

// ✅ NEW - Valid ContactType
contacts: [
    {
        contactId: null,
        contactType: "PRIMARY",         // ✅ Valid enum value
        name: "Restaurant Manager",
        phone: "8012345678",
        alternatePhone: null,
        email: "contact@...",
        designation: "Manager",         // ✅ Added
        isActive: true,                 // ✅ Added
        addedAt: null,
        addedBy: null
    }
]

// ✅ NEW - Correct contract structure
contract: {
    contractId: generateUUID(),
    contractUrl: null,
    signed: false,
    signedAt: null,
    signedBy: null,
    platformFee: {
        feeType: "PERCENTAGE",
        percentageRate: 20.0,           // ✅ Correct field name
        fixedAmountPerOrder: null,
        minFeePerOrder: 10.0,           // ✅ Correct field name
        maxFeePerOrder: 100.0,          // ✅ Correct field name
        feeSlabs: null
    },
    deliveryFee: {
        feeType: "FIXED",
        payor: "CUSTOMER",
        percentageRate: null,
        fixedAmountPerOrder: 40.0,      // ✅ Correct
        restaurantSharePercentage: null,
        customerSharePercentage: null,
        minFeePerOrder: null,
        maxFeePerOrder: null,
        distanceSlabs: null
    },
    paymentGatewayFee: {
        feeType: "PERCENTAGE",
        payor: "RESTAURANT",
        percentageRate: 2.0,
        fixedAmount: null,
        minFee: null,
        maxFee: null
    },
    penalties: null,
    validFrom: getTimestamp(),
    validUntil: null,
    autoRenewal: null,
    createdBy: null,
    createdAt: getTimestamp(),
    updatedBy: null,
    updatedAt: getTimestamp()
}
```

## Complete Data Structure

The new script creates restaurants with this exact structure:

```javascript
{
    _id: UUID,                          // MongoDB UUID
    name: String,
    description: String,
    logo: null,
    coverImage: null,
    cuisineTypes: [String],
    owners: [OwnerVO],                  // See structure above
    contacts: [ContactVO],              // See structure above
    documents: [DocumentVO],            // type, url, verificationStatus, uploadedAt, remarks
    contract: ContractVO,               // See structure above
    onboardingType: "SELF_SERVICE",
    onboardedBy: null,
    outlets: [],                        // Empty for now
    averageRating: 0.0,
    totalReviews: 0,
    totalOrders: 0,
    acceptsOrders: true,
    status: "PENDING",
    createdBy: "admin@foodai.com",
    createdAt: Date,
    updatedAt: Date,
    deleted: false,
    _class: "com.foodai.restaurant.domain.model.Restaurant"
}
```

## Usage

### Option 1: Direct MongoDB Loading (New Script)
```bash
# Load data directly via MongoDB
mongosh foodai_restaurant_dev < scripts/insert-mock-data.js

# Verify
curl 'http://localhost:8081/api/v1/restaurants?page=0&size=10' | jq '.data.content[].name'
```

### Option 2: API-Based Loading (Still Recommended)
```bash
# Start application first
./mvnw.sh run dev

# Load via API
./scripts/load-mock-data.sh
```

## Both Methods Now Work Identically

### MongoDB Script Method
```bash
mongosh foodai_restaurant_dev < scripts/insert-mock-data.js
```
**Result**: 3 restaurants inserted
- ✅ Bella Italia
- ✅ Sattvik Bhavan
- ✅ Dragon Wok

### API Method
```bash
./scripts/load-via-api-v2.sh
```
**Result**: 3 restaurants created via POST API
- ✅ Bella Italia
- ✅ Sattvik Bhavan
- ✅ Dragon Wok

### Both Produce Identical Data
```bash
curl 'http://localhost:8081/api/v1/restaurants?page=0&size=10'
```
**Result**: Same structure regardless of insertion method

## Verification Test

```bash
# Clean database
mongosh foodai_restaurant_dev --eval "db.restaurants.deleteMany({})"

# Insert via MongoDB script
mongosh foodai_restaurant_dev < scripts/insert-mock-data.js

# Verify data structure
curl -s 'http://localhost:8081/api/v1/restaurants?page=0&size=1' | \
  jq '.data.content[0] | {
    name,
    owner: .owners[0].ownerName,
    cuisines: .cuisineTypes,
    contact: .contacts[0].contactType,
    platformFee: .contract.platformFee.percentageRate,
    deliveryFee: .contract.deliveryFee.fixedAmountPerOrder,
    status
  }'
```

**Expected Output**:
```json
{
  "name": "Dragon Wok",
  "owner": "Wei Chen",
  "cuisines": ["Chinese", "Asian", "Thai"],
  "contact": "PRIMARY",
  "platformFee": 22.0,
  "deliveryFee": 50.0,
  "status": "PENDING"
}
```

## Key Differences from API Method

### MongoDB Script
✅ **Pros**:
- Faster execution (direct DB insert)
- Works even if application is not running
- Good for initial setup or testing

❌ **Cons**:
- Bypasses validation logic
- Bypasses business rules
- No error handling for invalid data
- Must maintain sync with domain model manually

### API Method
✅ **Pros**:
- Full validation applied
- Business logic executed
- Type safety guaranteed
- Error messages for invalid data
- Automatically stays in sync with domain model

❌ **Cons**:
- Requires application to be running
- Slightly slower (HTTP overhead)

## Recommendation

**For Development**: Either method works equally well now  
**For Production Setup**: Use API method for data integrity  
**For Testing**: MongoDB script is faster for setup/teardown

## Files Updated

1. **`scripts/insert-mock-data.js`**
   - Complete rewrite
   - Now matches domain model structure exactly
   - Same 3 restaurants as API script

2. **`scripts/load-mock-data.sh`**
   - Updated to use API method by default
   - Can be modified to use MongoDB script if preferred

## Testing Results

### ✅ MongoDB Script Test
```bash
$ mongosh foodai_restaurant_dev < scripts/insert-mock-data.js
Inserting mock restaurant data...
Creating: Bella Italia...
✓ Inserted: Bella Italia
Creating: Sattvik Bhavan...
✓ Inserted: Sattvik Bhavan
Creating: Dragon Wok...
✓ Inserted: Dragon Wok
========================================
Mock Data Insertion Complete!
Total Restaurants Inserted: 3
```

### ✅ API Verification
```bash
$ curl -s 'http://localhost:8081/api/v1/restaurants?page=0&size=10' | jq '.data.totalElements, .data.content[].name'
3
"Dragon Wok"
"Sattvik Bhavan"
"Bella Italia"
```

### ✅ Structure Verification
All fields match the expected domain model:
- ✅ Correct owner field names (ownerName, ownerEmail, ownerPhone)
- ✅ Valid ContactType enum (PRIMARY)
- ✅ Correct contract structure (percentageRate, minFeePerOrder, maxFeePerOrder)
- ✅ All nested objects properly structured
- ✅ All required fields present

## Comparison with load-via-api-v2.sh

Both scripts now create identical data:

| Field | insert-mock-data.js | load-via-api-v2.sh | Match? |
|-------|---------------------|---------------------|--------|
| Restaurant names | Bella Italia, Sattvik Bhavan, Dragon Wok | Same | ✅ |
| Owner structure | ownerName, ownerEmail, ownerPhone, isPrimaryContact | Same | ✅ |
| Contact structure | contactType: PRIMARY, designation, isActive | Same | ✅ |
| Contract structure | percentageRate, minFeePerOrder, maxFeePerOrder | Same | ✅ |
| Enum values | PRIMARY, FSSAI, RESTAURANT, CUSTOMER | Same | ✅ |
| Status | PENDING | PENDING | ✅ |
| Outlets | [] (empty) | [] (empty) | ✅ |

## Next Steps

You can now use either method for loading mock data:

1. **Quick Setup** (MongoDB script):
   ```bash
   mongosh foodai_restaurant_dev < scripts/insert-mock-data.js
   ```

2. **Validated Setup** (API):
   ```bash
   ./scripts/load-mock-data.sh
   ```

Both will produce identical, working data that the application can read and process correctly.

