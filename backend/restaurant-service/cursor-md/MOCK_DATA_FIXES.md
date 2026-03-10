# Mock Data Format Fixes

## Issues Fixed

### 1. Missing `deleted` Field
**Problem**: The `getAllRestaurants()` method filters by `deleted=false`, but mock data didn't have this field.

**Fix Applied**:
```bash
mongosh foodai_restaurant_dev --eval "
db.restaurants.updateMany(
  { deleted: { \$exists: false } },
  { \$set: { deleted: false } }
);"
```

### 2. Wrong Enum Value for OwnerRole
**Problem**: Mock data used `"PRIMARY"` but the enum is `PRIMARY_OWNER`.

**Enum Values**:
- `PRIMARY_OWNER` (not PRIMARY)
- `CO_OWNER`
- `PARTNER`
- `INVESTOR`

**Fix Applied**:
```bash
mongosh foodai_restaurant_dev --eval "
db.restaurants.updateMany(
  { 'owners.role': 'PRIMARY' },
  { \$set: { 'owners.\$[].role': 'PRIMARY_OWNER' } }
);"
```

### 3. Mock Data Script Updated
Updated `scripts/insert-mock-data.js` to include:
- `deleted: false` field
- `role: "PRIMARY_OWNER"` instead of `role: "PRIMARY"`

## Testing

### Method 1: Reload Fresh Mock Data
```bash
./scripts/load-mock-data.sh
```

### Method 2: Manual Verification
```bash
# Check data format
mongosh foodai_restaurant_dev --eval "
db.restaurants.findOne(
  {name: 'Bella Italia'}, 
  {name: 1, deleted: 1, 'owners.role': 1, createdAt: 1}
)"

# Should output:
# {
#   name: 'Bella Italia',
#   owners: [ { role: 'PRIMARY_OWNER' } ],
#   deleted: false,
#   createdAt: ISODate('2025-11-25...')
# }
```

### Method 3: Test Endpoint
```bash
# Start application
source ./scripts/SET_JAVA_21.sh
mvn spring-boot:run

# Test in another terminal
curl -X GET 'http://localhost:8081/api/v1/restaurants?page=0&size=3' \
  -H 'accept: application/json'
```

## Additional Checks Needed

If the endpoint still fails, check for other potential issues:

1. **Other Enum Fields**: Verify all enum fields match the domain model:
   - `RestaurantStatus` (PENDING, APPROVED, REJECTED, SUSPENDED)
   - `OnboardingType` (FULL_SERVICE, SELF_SERVICE, HYBRID)
   - `OutletStatus` (DRAFT, PENDING, OPERATIONAL, TEMPORARILY_CLOSED, PERMANENTLY_CLOSED)
   - `ContactType` (PHONE, EMAIL, WHATSAPP, WEBSITE)
   - `DocumentType` (FSSAI_LICENSE, GST_CERTIFICATE, etc.)
   - `DocumentStatus` (PENDING, VERIFIED, REJECTED, EXPIRED)

2. **Required Fields**: Ensure all required fields are present:
   - `id` (auto-generated)
   - `name`
   - `cuisineTypes`
   - `status`
   - `acceptsOrders`
   - `onboardingType`
   - `outlets`
   - `owners`
   - `deleted`
   - `createdAt`
   - `updatedAt`

3. **Nested Object Structure**: Verify nested objects match the domain model structure.

## Quick Fix Command

Run this to fix all common issues at once:

```bash
mongosh foodai_restaurant_dev --eval "
// Fix deleted field
db.restaurants.updateMany(
  { deleted: { \$exists: false } },
  { \$set: { deleted: false } }
);

// Fix owner role
db.restaurants.updateMany(
  { 'owners.role': 'PRIMARY' },
  { \$set: { 'owners.\$[].role': 'PRIMARY_OWNER' } }
);

print('Fixed common data issues');
db.restaurants.countDocuments({deleted: false});
"
```

## Debugging

To see detailed errors:

```bash
# Check application logs
tail -f /tmp/spring-boot.log | grep ERROR

# Or run with debug logging
mvn spring-boot:run -Dlogging.level.com.foodai.restaurant=DEBUG
```

## Complete Reset

If issues persist, reload everything from scratch:

```bash
# Stop application
pkill -f "spring-boot:run"

# Clear database
mongosh foodai_restaurant_dev --eval "db.restaurants.deleteMany({})"

# Reload mock data
./scripts/load-mock-data.sh

# Restart application
source ./scripts/SET_JAVA_21.sh
mvn spring-boot:run
```

