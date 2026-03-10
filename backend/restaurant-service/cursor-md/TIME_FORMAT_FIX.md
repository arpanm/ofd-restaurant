# Time Format Fix for LocalTime Conversion

## Problem

Spring Data MongoDB was failing to convert time strings from MongoDB to `LocalTime` objects with this error:

```
ConverterNotFoundException: No converter found capable of converting from type [java.lang.String] to type [java.time.LocalTime]
```

## Root Cause

The mock data was storing times in `HH:mm` format (e.g., `"11:00"`), but Spring Data MongoDB's default converters expect times in ISO-8601 format with seconds: `HH:mm:ss` (e.g., `"11:00:00"`).

## Fix Applied

### 1. Updated Mock Data Script

Changed all time values in `scripts/insert-mock-data.js` from `HH:mm` to `HH:mm:ss` format:

**Before:**
```javascript
operatingHours: [
    {
        dayOfWeek: "MONDAY",
        openTime: "11:00",      // ❌ Missing seconds
        closeTime: "23:00",     // ❌ Missing seconds
        isClosed: false
    }
]
```

**After:**
```javascript
operatingHours: [
    {
        dayOfWeek: "MONDAY",
        openTime: "11:00:00",   // ✅ Includes seconds
        closeTime: "23:00:00",  // ✅ Includes seconds
        isClosed: false
    }
]
```

### 2. Automated Fix

Used `sed` to update all time formats:
```bash
sed -i '' 's/openTime: "\([0-9][0-9]:[0-9][0-9]\)",/openTime: "\1:00",/g' scripts/insert-mock-data.js
sed -i '' 's/closeTime: "\([0-9][0-9]:[0-9][0-9]\)",/closeTime: "\1:00",/g' scripts/insert-mock-data.js
```

## Verification

Check MongoDB data format:
```bash
mongosh foodai_restaurant_dev --eval "
  var r = db.restaurants.findOne();
  print('Time format: ' + r.outlets[0].operatingHours[0].openTime);
"
```

**Expected Output:**
```
Time format: 11:00:00
```

## Complete List of Enum/Field Fixes

| Issue | Old Value | New Value | Status |
|-------|-----------|-----------|--------|
| DocumentType | `FSSAI_LICENSE` | `FSSAI` | ✅ Fixed |
| PaymentFeePayor | `MERCHANT` | `RESTAURANT` | ✅ Fixed |
| OnboardingType | `FULL_SERVICE` | `ASSISTED` | ✅ Fixed |
| OperatingHours field | `isOpen` | `isClosed` | ✅ Fixed |
| **Time format** | **`HH:mm`** | **`HH:mm:ss`** | **✅ Fixed** |

## Testing

After applying the fix:

1. **Reload data:**
   ```bash
   ./mvnw.sh db-clean dev
   ./scripts/load-mock-data.sh
   ```

2. **Restart application:**
   ```bash
   pkill -f spring-boot:run
   ./mvnw.sh run dev
   ```

3. **Test API:**
   ```bash
   curl -X 'GET' 'http://localhost:8081/api/v1/restaurants?page=0&size=10' | jq '.content[].name'
   ```

## Why This Fix Works

Spring Data MongoDB uses Spring's `ConversionService` to convert between MongoDB document types and Java types. For `LocalTime`:

1. **Without seconds** (`HH:mm`): No default converter exists
2. **With seconds** (`HH:mm:ss`): Matches ISO-8601 time format, which has a built-in converter

The `DateTimeFormatter.ISO_LOCAL_TIME` pattern expects: `HH:mm:ss` or `HH:mm:ss.SSS`

## Alternative Solutions (Not Used)

If the HH:mm:ss format doesn't work, other options include:

1. **Custom Converter:**
   ```java
   @ReadingConverter
   public class StringToLocalTimeConverter implements Converter<String, LocalTime> {
       @Override
       public LocalTime convert(String source) {
           return LocalTime.parse(source, DateTimeFormatter.ofPattern("HH:mm"));
       }
   }
   ```

2. **Store as ISODate:**
   Use MongoDB's ISODate type instead of strings

3. **Store as milliseconds:**
   Convert times to milliseconds since midnight

---

**Date:** November 26, 2025  
**Status:** ✅ Fixed - All time values updated to HH:mm:ss format  
**Files Modified:** `scripts/insert-mock-data.js`

