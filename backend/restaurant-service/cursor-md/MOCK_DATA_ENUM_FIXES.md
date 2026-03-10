# Mock Data Enum Fixes Summary

## Overview

The mock data script (`scripts/insert-mock-data.js`) had several enum value mismatches that were causing `IllegalArgumentException` errors when the application tried to deserialize MongoDB documents into Java objects.

## Enum Mismatches Found and Fixed

### 1. DocumentType Enum

**Error:** `No enum constant com.foodai.restaurant.domain.model.DocumentType.FSSAI_LICENSE`

**Java Enum Values:**
- `FSSAI`
- `GST`
- `PAN`
- `BANK_PROOF`

**Fix Applied:**
- Changed all occurrences of `"FSSAI_LICENSE"` to `"FSSAI"` in mock data
- Updated document structure from old fields (`number`, `issuedDate`, `expiryDate`, `status`, `verifiedAt`, `verifiedBy`) to new `DocumentVO` fields (`url`, `verificationStatus`, `uploadedAt`, `remarks`)

**Files Modified:** `scripts/insert-mock-data.js`

---

### 2. PaymentFeePayor Enum

**Error:** `No enum constant com.foodai.restaurant.domain.model.PaymentFeePayor.MERCHANT`

**Java Enum Values:**
- `RESTAURANT`
- `CUSTOMER`
- `PLATFORM`

**Fix Applied:**
- Changed all occurrences of `payor: "MERCHANT"` to `payor: "RESTAURANT"` (6 occurrences)

**Files Modified:** `scripts/insert-mock-data.js`

---

### 3. OnboardingType Enum

**Error:** `No enum constant com.foodai.restaurant.domain.model.OnboardingType.FULL_SERVICE`

**Java Enum Values:**
- `SELF_SERVICE`
- `ASSISTED`

**Fix Applied:**
- Changed all occurrences of `onboardingType: "FULL_SERVICE"` to `onboardingType: "ASSISTED"` (6 occurrences)

**Files Modified:** `scripts/insert-mock-data.js`

---

### 4. OperatingHoursVO Field Name Mismatch

**Error:** `No converter found capable of converting from type [java.lang.String] to type [java.time.LocalTime]`

**Root Cause:** Field name mismatch - Java model uses `isClosed` but mock data had `isOpen`

**Java Model Fields:**
```java
public class OperatingHoursVO {
    private DayOfWeek dayOfWeek;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Boolean isClosed;  // ✅ Correct field name
}
```

**Fix Applied:**
- Changed all occurrences of `isOpen: true` to `isClosed: false` (represents "not closed" = "open")
- No occurrences of `isOpen: false` were found (would need to be changed to `isClosed: true`)

**Files Modified:** `scripts/insert-mock-data.js`

---

## Verification

To verify the mock data is correct in MongoDB:

```bash
mongosh foodai_restaurant_dev --eval "
  var restaurant = db.restaurants.findOne();
  print('=== Mock Data Verification ===');
  print('1. PaymentFeePayor: ' + restaurant.contract.paymentGatewayFee.payor);
  print('2. OnboardingType: ' + restaurant.onboardingType);
  print('3. DocumentType: ' + restaurant.documents[0].type);
  print('4. Operating Hours (isClosed): ' + restaurant.outlets[0].operatingHours[0].isClosed);
"
```

**Expected Output:**
```
=== Mock Data Verification ===
1. PaymentFeePayor: RESTAURANT
2. OnboardingType: ASSISTED
3. DocumentType: FSSAI
4. Operating Hours (isClosed): false
```

---

## How to Apply Fixes

1. **Clean existing data:**
   ```bash
   ./mvnw.sh db-clean dev
   ```

2. **Reload corrected mock data:**
   ```bash
   ./scripts/load-mock-data.sh
   ```

3. **Restart the application:**
   ```bash
   ./mvnw.sh run dev
   ```

4. **Test the API:**
   ```bash
   curl -X 'GET' 'http://localhost:8081/api/v1/restaurants?page=0&size=10' -H 'accept: */*'
   ```

---

## Files Changed

### scripts/insert-mock-data.js

**Changes Summary:**
1. ✅ `FSSAI_LICENSE` → `FSSAI` (document type)
2. ✅ Document structure updated to match `DocumentVO` class
3. ✅ `MERCHANT` → `RESTAURANT` (payment fee payor) - 6 occurrences
4. ✅ `FULL_SERVICE` → `ASSISTED` (onboarding type) - 6 occurrences
5. ✅ `isOpen` → `isClosed` with inverted boolean values - all occurrences

---

## Root Cause Analysis

These issues occurred because:

1. **Enum Constant Naming:** Mock data used verbose/descriptive names (e.g., `FSSAI_LICENSE`, `FULL_SERVICE`) while Java enums used shorter names (e.g., `FSSAI`, `ASSISTED`)

2. **Field Renames:** Domain model evolved but mock data wasn't updated (e.g., `isOpen` → `isClosed`)

3. **Structure Changes:** `DocumentVO` class structure changed but mock data retained old fields

4. **No Validation:** Mock data was inserted directly into MongoDB without schema validation against Java domain models

---

## Prevention Strategy

To prevent similar issues in the future:

1. **Schema Validation:** Consider adding MongoDB schema validation that matches Java domain models
2. **Integration Tests:** Add tests that load mock data and verify deserialization works
3. **Documentation:** Keep mock data structure documentation synchronized with domain models
4. **Code Review:** Review mock data changes when domain models change

---

## Testing

After applying all fixes, the following should work without errors:

```bash
# 1. Get all restaurants
curl -X 'GET' 'http://localhost:8081/api/v1/restaurants?page=0&size=10' -H 'accept: */*'

# 2. Search by pincode
curl -X 'POST' 'http://localhost:8081/api/v1/restaurants/search' \
  -H 'Content-Type: application/json' \
  -d '{"deliveryPincode": "560095"}'

# 3. Get specific restaurant
curl -X 'GET' 'http://localhost:8081/api/v1/restaurants/{id}' -H 'accept: */*'
```

---

**Date:** November 26, 2025  
**Status:** ✅ All enum mismatches identified and fixed  
**Next Step:** Restart application and verify API works correctly

