# API Data Loading Success ✅

**Date**: November 25, 2025  
**Status**: Completed

## Issue Fixed

### Problem
The `load-via-api-v2.sh` script was failing to create restaurants with error:
```
Cannot deserialize value of type 'ContactType' from String "PHONE": 
not one of the values accepted for Enum class: [EMERGENCY, PRIMARY, SECONDARY, MANAGER, SUPPORT]
```

### Root Cause
The script was using an invalid `ContactType` value: `"PHONE"`

### Solution
Updated all contact definitions in `scripts/load-via-api-v2.sh` to use valid `ContactType.PRIMARY` instead of the non-existent `"PHONE"` value.

**Valid ContactType values:**
- `PRIMARY` - Primary contact person
- `SECONDARY` - Secondary/backup contact
- `EMERGENCY` - Emergency contact
- `MANAGER` - Restaurant/outlet manager contact
- `SUPPORT` - Support executive contact

### Changes Made

1. **Updated `scripts/load-via-api-v2.sh`**:
   - Changed `contactType` from `"PHONE"` to `"PRIMARY"`
   - Added missing `designation` field
   - Added missing `isActive` field
   - Fixed response parsing to properly detect success

2. **Created `scripts/test-full-flow.sh`**:
   - Complete automation: clean DB → start server → load data → test
   - Handles all steps in sequence
   - Provides detailed status messages

## Usage

### Quick Start
```bash
# Complete flow (recommended for testing)
./scripts/test-full-flow.sh
```

### Individual Steps
```bash
# 1. Clean MongoDB
mongosh foodai_restaurant_dev --eval "db.restaurants.deleteMany({})"

# 2. Start application (if not running)
./mvnw.sh run dev

# 3. Load mock data via API
./scripts/load-via-api-v2.sh

# 4. Test the API
curl 'http://localhost:8081/api/v1/restaurants?page=0&size=10' | jq '.data.content[].name'
```

## Verification

### Test Results
```bash
curl 'http://localhost:8081/api/v1/restaurants?page=0&size=10' | jq -r '.data.content[].name'
```

**Output:**
```
Dragon Wok
Sattvik Bhavan
Bella Italia
```

### Full Response Structure
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "content": [
      {
        "id": "...",
        "name": "Dragon Wok",
        "description": "Authentic Chinese and Asian fusion cuisine",
        "cuisineTypes": ["Chinese", "Asian", "Thai"],
        "owners": [...],
        "contacts": [...],
        "documents": [...],
        "contract": {...},
        "status": "PENDING",
        "averageRating": 0.0,
        "totalReviews": 0,
        ...
      },
      ...
    ],
    "totalElements": 3,
    "totalPages": 1,
    ...
  }
}
```

## Mock Data Loaded

1. **Bella Italia**
   - Cuisines: Italian, Mediterranean
   - Owner: Mario Rossi
   - Platform Fee: 20% (₹10-₹100)
   - Delivery Fee: ₹40 (Customer pays)

2. **Sattvik Bhavan**
   - Cuisines: Indian, North Indian, South Indian
   - Owner: Rajesh Kumar
   - Platform Fee: 18% (₹8-₹80)
   - Delivery Fee: ₹30 (Customer pays)

3. **Dragon Wok**
   - Cuisines: Chinese, Asian, Thai
   - Owner: Wei Chen
   - Platform Fee: 22% (₹12-₹120)
   - Delivery Fee: ₹50 (Customer pays)

## Scripts Available

### 1. `scripts/test-full-flow.sh`
Complete end-to-end test including:
- Clean MongoDB
- Kill existing processes
- Start application
- Wait for readiness
- Load mock data
- Test API

**Usage:**
```bash
./scripts/test-full-flow.sh
```

### 2. `scripts/load-via-api-v2.sh`
Load mock data via REST API (requires running server)

**Usage:**
```bash
./scripts/load-via-api-v2.sh
```

### 3. `mvnw.sh`
Maven wrapper with MongoDB management

**Usage:**
```bash
./mvnw.sh run dev          # Start in dev mode
./mvnw.sh db-status dev    # Check DB status
./mvnw.sh db-clean dev     # Clean DB data
./mvnw.sh db-mock dev      # Load mock data (via mongosh)
```

## Status

✅ **WORKING** - All 3 mock restaurants successfully loaded via API  
✅ **VERIFIED** - GET /api/v1/restaurants returns all restaurants  
✅ **SCRIPTS** - Automation scripts created and tested  

## Next Steps

To add more mock restaurants, edit `scripts/load-via-api-v2.sh` and add new curl POST requests following the same format.

Remember to use valid enum values:
- `ContactType`: PRIMARY, SECONDARY, EMERGENCY, MANAGER, SUPPORT
- `DocumentType`: FSSAI, GST, PAN, BANK_PROOF
- `PaymentFeePayor`: RESTAURANT, CUSTOMER, PLATFORM
- `OnboardingType`: SELF_SERVICE, ASSISTED

