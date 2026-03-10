# Script Updates Summary

**Date**: November 25, 2025  
**Status**: Completed ✅

## What Was Fixed

### Problem
The `insert-mock-data.js` MongoDB script had major structural mismatches with the current domain model:
- Used incorrect field names (`name`, `email`, `phone` instead of `ownerName`, `ownerEmail`, `ownerPhone`)
- Missing required fields (`isPrimaryContact`)
- Invalid enum values (`ContactType.PHONE` doesn't exist)
- Contract structure didn't match `ContractVO`
- Data bypassed validation layers

### Solution
Updated `load-mock-data.sh` to use the proven **API-based loading** approach instead of direct MongoDB insertion.

## Changes Made

### 1. Updated `scripts/load-mock-data.sh`
**Before**: Used `mongosh` to insert data directly via `insert-mock-data.js`  
**After**: Calls `load-via-api-v2.sh` to load data via REST API

**Benefits**:
- ✅ Data goes through all validation layers
- ✅ Enum values are validated
- ✅ Structure guaranteed to match domain model
- ✅ Business logic properly applied

### 2. Added Deprecation Warning to `scripts/insert-mock-data.js`
Added comprehensive warning header explaining:
- Why the script is deprecated
- What the issues are
- Recommended alternative approach

### 3. Updated `scripts/load-via-api-v2.sh`
Fixed `ContactType` values:
- ❌ `"PHONE"` (invalid)
- ✅ `"PRIMARY"` (valid)

Added missing fields:
- `designation`
- `isActive`

## Current Scripts

### ✅ Recommended Workflow

```bash
# Start application
./mvnw.sh run dev

# Load mock data (in new terminal)
./scripts/load-mock-data.sh
```

### Available Scripts

| Script | Purpose | Status |
|--------|---------|--------|
| `load-mock-data.sh` | **Main entry point** for loading data | ✅ Recommended |
| `load-via-api-v2.sh` | Core API-based data loading | ✅ Working |
| `test-full-flow.sh` | Complete automation (clean → start → load → test) | ✅ Working |
| `insert-mock-data.js` | Direct MongoDB insertion | ⚠️  Deprecated |

## Usage Examples

### Load Mock Data (Simple)
```bash
# Prerequisite: Application must be running
./mvnw.sh run dev

# In new terminal
./scripts/load-mock-data.sh
```

### Complete Test Flow (Automated)
```bash
# Everything automated: clean DB, start app, load data, test
./scripts/test-full-flow.sh
```

### Clean and Reload Data
```bash
# Clean database
mongosh foodai_restaurant_dev --eval "db.restaurants.deleteMany({})"

# Reload via API
./scripts/load-mock-data.sh
```

### Verify Loaded Data
```bash
# List restaurant names
curl 'http://localhost:8081/api/v1/restaurants?page=0&size=10' | jq '.data.content[].name'

# Full details
curl 'http://localhost:8081/api/v1/restaurants?page=0&size=10' | jq '.data.content[]'
```

## Mock Data Details

The scripts load 3 restaurants:

1. **Bella Italia** (Italian, Mediterranean)
   - Owner: Mario Rossi
   - Platform Fee: 20% (₹10-₹100)
   - Delivery: ₹40 (Customer pays)

2. **Sattvik Bhavan** (Indian, North Indian, South Indian)
   - Owner: Rajesh Kumar
   - Platform Fee: 18% (₹8-₹80)
   - Delivery: ₹30 (Customer pays)

3. **Dragon Wok** (Chinese, Asian, Thai)
   - Owner: Wei Chen
   - Platform Fee: 22% (₹12-₹120)
   - Delivery: ₹50 (Customer pays)

## Valid Enum Values Reference

When creating new mock data, use these valid values:

**ContactType**: `PRIMARY`, `SECONDARY`, `EMERGENCY`, `MANAGER`, `SUPPORT`  
**DocumentType**: `FSSAI`, `GST`, `PAN`, `BANK_PROOF`  
**PaymentFeePayor**: `RESTAURANT`, `CUSTOMER`, `PLATFORM`  
**OnboardingType**: `SELF_SERVICE`, `ASSISTED`  
**RestaurantStatus**: `PENDING`, `APPROVED`, `SUSPENDED`, `REJECTED`, `DEACTIVATED`

## Documentation

Comprehensive guides available in `cursor-md/`:

- **MOCK_DATA_LOADING_GUIDE.md** - Complete guide for loading mock data
- **API_DATA_LOADING_SUCCESS.md** - Details on the API loading fix
- **SCRIPT_UPDATES_SUMMARY.md** - This document

## Verification

After loading data, verify:

```bash
✅ 3 restaurants loaded
✅ All have PENDING status
✅ All have valid owners, contacts, documents, contracts
✅ No validation errors
✅ No enum conversion errors
```

**Test command**:
```bash
curl -s 'http://localhost:8081/api/v1/restaurants?page=0&size=10' | \
  jq -r '.data.content[] | "✅ \(.name) - \(.status) - Owner: \(.owners[0].ownerName)"'
```

**Expected output**:
```
✅ Dragon Wok - PENDING - Owner: Wei Chen
✅ Sattvik Bhavan - PENDING - Owner: Rajesh Kumar
✅ Bella Italia - PENDING - Owner: Mario Rossi
```

## Next Steps

To add more mock restaurants:
1. Edit `scripts/load-via-api-v2.sh`
2. Add new curl POST request following existing pattern
3. Use valid enum values from reference above
4. Test with `./scripts/load-mock-data.sh`

See **MOCK_DATA_LOADING_GUIDE.md** for detailed examples.

