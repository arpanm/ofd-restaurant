# Mock Data Loading Guide

**Updated**: November 25, 2025  
**Status**: Validated & Working

## Overview

This guide explains how to load mock restaurant data into the system. We provide two approaches, with **API-based loading** being the recommended method.

## ✅ Recommended: API-Based Loading

### Why API-Based Loading?

1. **Data Validation**: Requests go through all DTOs and validation layers
2. **Type Safety**: Jackson automatically validates enum values and field types
3. **Business Logic**: All business rules and transformations are applied
4. **Error Detection**: Clear error messages for any data issues
5. **Consistency**: Data structure guaranteed to match domain model

### Quick Start

```bash
# Ensure application is running
./mvnw.sh run dev

# In a new terminal, load mock data
./scripts/load-mock-data.sh
```

### Manual Loading

```bash
# Start application first
./mvnw.sh run dev

# Then load data via API
./scripts/load-via-api-v2.sh
```

### What Gets Loaded

The scripts load 3 restaurants via REST API:

1. **Bella Italia**
   - Cuisines: Italian, Mediterranean
   - Owner: Mario Rossi (mario@bellaitalia.com)
   - Platform Fee: 20% (₹10-₹100 range)
   - Delivery Fee: ₹40 (Customer pays)
   - Payment Gateway Fee: 2% (Restaurant pays)

2. **Sattvik Bhavan**
   - Cuisines: Indian, North Indian, South Indian
   - Owner: Rajesh Kumar (rajesh@sattvikbhavan.com)
   - Platform Fee: 18% (₹8-₹80 range)
   - Delivery Fee: ₹30 (Customer pays)
   - Payment Gateway Fee: 2% (Restaurant pays)

3. **Dragon Wok**
   - Cuisines: Chinese, Asian, Thai
   - Owner: Wei Chen (wei@dragonwok.com)
   - Platform Fee: 22% (₹12-₹120 range)
   - Delivery Fee: ₹50 (Customer pays)
   - Payment Gateway Fee: 2% (Restaurant pays)

### Verification

```bash
# Check if data loaded successfully
curl 'http://localhost:8081/api/v1/restaurants?page=0&size=10' | jq '.data.content[].name'

# Expected output:
# "Dragon Wok"
# "Sattvik Bhavan"
# "Bella Italia"

# View full details
curl 'http://localhost:8081/api/v1/restaurants?page=0&size=10' | jq '.data.content[]'
```

## ⚠️  Deprecated: Direct MongoDB Loading

### Why Not Recommended?

The `insert-mock-data.js` script has been deprecated because:

1. **Structure Mismatches**: Field names don't match current domain model
   - Uses `name`, `email`, `phone` instead of `ownerName`, `ownerEmail`, `ownerPhone`
   - Missing required fields like `isPrimaryContact`
   - Invalid `ContactType` enum values (uses `"PHONE"` which doesn't exist)

2. **No Validation**: Data bypasses all validation logic
3. **Type Errors**: Can cause runtime errors due to incorrect data types
4. **Maintenance**: Requires manual sync with domain model changes

### If You Must Use It

If you need to insert data directly via MongoDB:

```bash
# WARNING: This may fail or cause errors
mongosh foodai_restaurant_dev < scripts/insert-mock-data.js
```

**Known Issues**:
- Owner structure incompatible
- Contact types invalid
- Contract structure doesn't match ContractVO
- Many fields may not exist in current model

## Script Reference

### 1. `scripts/load-mock-data.sh`
**Recommended entry point** for loading mock data.

**What it does**:
- Checks if application is running
- Calls `load-via-api-v2.sh` to load data
- Provides usage instructions

**Usage**:
```bash
./scripts/load-mock-data.sh
```

**Prerequisites**:
- Application must be running on port 8081

---

### 2. `scripts/load-via-api-v2.sh`
Core script that loads data via REST API.

**What it does**:
- Creates 3 restaurants via POST `/api/v1/restaurants`
- Tests with GET `/api/v1/restaurants`
- Shows results

**Usage**:
```bash
./scripts/load-via-api-v2.sh
```

**Prerequisites**:
- Application running on port 8081
- `jq` installed for JSON processing

---

### 3. `scripts/test-full-flow.sh`
Complete end-to-end test automation.

**What it does**:
1. Cleans MongoDB database
2. Kills existing Java processes
3. Starts application
4. Waits for application to be ready
5. Loads mock data via API
6. Tests GET endpoint

**Usage**:
```bash
./scripts/test-full-flow.sh
```

**Prerequisites**:
- MongoDB running
- `jq` installed

---

### 4. `scripts/insert-mock-data.js` (Deprecated)
Direct MongoDB insertion script.

**Status**: ⚠️  DEPRECATED - Use API-based loading instead

**Issues**: See "Deprecated: Direct MongoDB Loading" section above

---

## Adding More Restaurants

To add more mock restaurants, edit `scripts/load-via-api-v2.sh`:

```bash
# Add new restaurant after existing ones
echo "Creating: New Restaurant..."
RESPONSE=$(curl -s -X POST "$API_BASE" \
  -H "Content-Type: application/json" \
  -d '{
  "name": "New Restaurant",
  "description": "Description here",
  "cuisineTypes": ["Cuisine1", "Cuisine2"],
  "owners": [{
    "ownerName": "Owner Name",
    "ownerEmail": "owner@example.com",
    "ownerPhone": "9876543210",
    "ownershipPercentage": 100.0,
    "role": "PRIMARY_OWNER",
    "isPrimaryContact": true
  }],
  "contacts": [{
    "contactType": "PRIMARY",
    "name": "Contact Name",
    "phone": "8012345678",
    "email": "contact@example.com",
    "designation": "Manager",
    "isActive": true
  }],
  "documents": [{
    "type": "FSSAI",
    "url": "https://example.com/docs/fssai.pdf",
    "remarks": "Valid document"
  }],
  "contract": {
    "platformFee": {
      "feeType": "PERCENTAGE",
      "percentageRate": 20.0,
      "minFeePerOrder": 10.0,
      "maxFeePerOrder": 100.0
    },
    "deliveryFee": {
      "feeType": "FIXED",
      "payor": "CUSTOMER",
      "fixedAmountPerOrder": 40.0
    },
    "paymentGatewayFee": {
      "feeType": "PERCENTAGE",
      "payor": "RESTAURANT",
      "percentageRate": 2.0
    }
  },
  "createdBy": "admin@foodai.com"
}')

if echo "$RESPONSE" | jq -e '.success' > /dev/null 2>&1; then
  echo "$RESPONSE" | jq -r '"  ✅ Created: \(.data.name) (ID: \(.data.id))"'
else
  echo "  ❌ Failed to create restaurant"
  echo "$RESPONSE" | jq '.'
fi
echo ""
```

## Valid Enum Values

When creating or modifying mock data, use these valid enum values:

### ContactType
- `PRIMARY` - Primary contact person
- `SECONDARY` - Secondary/backup contact
- `EMERGENCY` - Emergency contact
- `MANAGER` - Restaurant/outlet manager contact
- `SUPPORT` - Support executive contact

### DocumentType
- `FSSAI` - FSSAI License
- `GST` - GST Registration
- `PAN` - PAN Card
- `BANK_PROOF` - Bank Account Proof

### PaymentFeePayor
- `RESTAURANT` - Restaurant pays
- `CUSTOMER` - Customer pays
- `PLATFORM` - Platform absorbs

### OnboardingType
- `SELF_SERVICE` - Self-registered
- `ASSISTED` - Onboarded by operations team

### RestaurantStatus
- `PENDING` - Awaiting verification
- `APPROVED` - Verified and active
- `SUSPENDED` - Temporarily disabled
- `REJECTED` - Application rejected
- `DEACTIVATED` - Permanently deactivated

## Troubleshooting

### Application Not Running

**Error**: `ERROR: Application is not running on port 8081`

**Solution**:
```bash
# Start the application
./mvnw.sh run dev

# OR
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

### Invalid Enum Value

**Error**: `Cannot deserialize value of type 'ContactType' from String "PHONE"`

**Solution**: Use valid enum values from the "Valid Enum Values" section above.

---

### Validation Errors

**Error**: `"validationErrors": [...]`

**Solution**: Check that all required fields are present and valid:
- `ownerName`, `ownerEmail`, `ownerPhone` (not `name`, `email`, `phone`)
- `isPrimaryContact` field in owners
- `contactType` must be one of: PRIMARY, SECONDARY, EMERGENCY, MANAGER, SUPPORT
- All required nested objects (platformFee, deliveryFee, etc.)

---

### Data Already Exists

To clean and reload:

```bash
# Clean database
mongosh foodai_restaurant_dev --eval "db.restaurants.deleteMany({})"

# Reload data
./scripts/load-mock-data.sh
```

---

## Complete Workflow Example

```bash
# 1. Clean database
mongosh foodai_restaurant_dev --eval "db.restaurants.deleteMany({})"

# 2. Start application
./mvnw.sh run dev

# 3. Wait for application to start (check logs)
# Look for: "Started RestaurantApplication"

# 4. In a new terminal, load mock data
cd /path/to/restaurant-service
./scripts/load-mock-data.sh

# 5. Verify data loaded
curl 'http://localhost:8081/api/v1/restaurants?page=0&size=10' | jq '.data.content[].name'

# 6. Test in Swagger UI
open http://localhost:8081/swagger-ui.html
```

## Summary

✅ **Use**: `./scripts/load-mock-data.sh` (Recommended)  
✅ **Alternative**: `./scripts/load-via-api-v2.sh` (Direct API loading)  
✅ **Full Test**: `./scripts/test-full-flow.sh` (Complete automation)  
❌ **Avoid**: `scripts/insert-mock-data.js` (Deprecated, has structural issues)

All data should be loaded via the REST API to ensure validation, consistency, and compatibility with the domain model.

