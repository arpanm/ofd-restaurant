# mvnw.sh Update Summary

**Date**: November 25, 2025  
**Status**: ✅ Updated and Enhanced

## Overview

The `mvnw.sh` Maven wrapper script has been updated to work correctly with both data loading methods (MongoDB direct insert and REST API) and includes new database management commands.

## What Changed

### 1. Fixed Mock Data Loading Logic

**Problem**: The script was trying to call `load-mock-data.sh` before the application started, but this script now requires the application to be running (uses REST API).

**Solution**: Updated to use `insert-mock-data.js` for pre-application data loading via direct MongoDB insertion.

#### Before
```bash
if [ -x "./scripts/load-mock-data.sh" ]; then
    ./scripts/load-mock-data.sh "$db_name" > /dev/null 2>&1 || {
        print_warning "Failed to load mock data automatically"
    }
fi
```

#### After
```bash
if [ -f "./scripts/insert-mock-data.js" ]; then
    mongosh "$db_name" --quiet < ./scripts/insert-mock-data.js > /dev/null 2>&1 && {
        print_success "Mock data loaded successfully"
    } || {
        print_warning "Failed to load mock data automatically"
        print_info "You can load it manually after app starts:"
        print_info "  ./scripts/load-mock-data.sh"
    }
fi
```

### 2. Added New Commands

#### `db-status` - Show Database Status
Shows comprehensive information about the database:
- Connection status
- Total restaurant count
- Status breakdown (PENDING, APPROVED, etc.)
- Sample restaurants list

**Usage:**
```bash
./mvnw.sh db-status dev
```

**Output:**
```
✓ Connected to database: foodai_restaurant_dev

ℹ Total Restaurants: 3

ℹ Status Breakdown:
  PENDING: 3

ℹ Sample Restaurants:
  • Bella Italia (Italian, Mediterranean) - PENDING
  • Sattvik Bhavan (Indian, North Indian, South Indian) - PENDING
  • Dragon Wok (Chinese, Asian, Thai) - PENDING
```

---

#### `db-mock` - Load Mock Data
Loads mock data directly into MongoDB using `insert-mock-data.js`:
- Checks if data already exists
- Optionally clears existing data
- Loads 3 restaurants via MongoDB script
- Shows summary after loading

**Usage:**
```bash
./mvnw.sh db-mock dev
```

**Features:**
- ✅ Works without running application
- ✅ Prompts before clearing existing data
- ✅ Shows count after loading
- ✅ Fast execution

---

### 3. Updated `db-clean` Command

Now uses MongoDB script for reloading data instead of API-based script.

**Before:**
```bash
./scripts/load-mock-data.sh "$db_name"  # Required running app
```

**After:**
```bash
mongosh "$db_name" --quiet < ./scripts/insert-mock-data.js  # Works immediately
```

## Complete Command Reference

### Application Commands

| Command | Description | Requires App? | Requires MongoDB? |
|---------|-------------|---------------|-------------------|
| `run` | Run the application | No | Yes |
| `test` | Run tests | No | Yes |
| `build` | Build the application | No | No |
| `clean` | Clean build artifacts | No | No |
| `install` | Clean install | No | No |
| `compile` | Compile source code | No | No |

### Database Commands

| Command | Description | Requires App? | Requires MongoDB? |
|---------|-------------|---------------|-------------------|
| `db-status` | Show database status | No | Yes |
| `db-mock` | Load mock data | No | Yes |
| `db-clean` | Clean database | No | Yes |

## Usage Examples

### Check Database Status
```bash
./mvnw.sh db-status dev
```

### Load Mock Data
```bash
# Load mock data before starting app
./mvnw.sh db-mock dev

# Then start the application
./mvnw.sh run dev
```

### Clean and Reload Data
```bash
# Clean database (will prompt to reload)
./mvnw.sh db-clean dev

# Or do it in separate steps
./mvnw.sh db-clean dev
./mvnw.sh db-mock dev
```

### Run Application
```bash
# Run with automatic data check
./mvnw.sh run dev

# Run without data check
./mvnw.sh run dev --skip-data-check

# Run in production
./mvnw.sh run prod
```

### Build and Test
```bash
# Run tests
./mvnw.sh test

# Build without tests
./mvnw.sh build dev

# Clean install without tests
./mvnw.sh install

# Clean install with tests
./mvnw.sh install --force-clean
```

## Automatic Data Loading

When you run the application with `./mvnw.sh run dev`:

1. **Checks Database**: Counts documents in the restaurants collection
2. **If Empty**: Automatically loads mock data using `insert-mock-data.js`
3. **If Has Data**: Shows count and continues
4. **Production**: Never loads mock data automatically

### Skip Automatic Loading

```bash
./mvnw.sh run dev --skip-data-check
```

## Data Loading Workflow

### Scenario 1: Fresh Start (Empty Database)

```bash
# Step 1: Start application (data loads automatically)
./mvnw.sh run dev

# Output:
# ⚠ No restaurant data found in database: foodai_restaurant_dev
# ℹ Loading mock data via MongoDB...
# ✓ Mock data loaded successfully
```

### Scenario 2: Pre-load Data Before Starting App

```bash
# Step 1: Load data first
./mvnw.sh db-mock dev

# Step 2: Start application (skips data loading)
./mvnw.sh run dev

# Output:
# ✓ Found 3 restaurant(s) in database
```

### Scenario 3: Use API to Load Data

```bash
# Step 1: Start application without data
./mvnw.sh run dev --skip-data-check

# Step 2: Load via API (in new terminal)
./scripts/load-mock-data.sh
```

## Benefits of the Update

### 1. Flexibility
- ✅ Can load data before or during app startup
- ✅ Can use MongoDB script or API method
- ✅ Can check database status anytime

### 2. Reliability
- ✅ Uses correct script for each context
- ✅ MongoDB script for pre-app loading
- ✅ API script for post-app loading

### 3. Convenience
- ✅ Single command to check database
- ✅ Single command to load data
- ✅ Interactive prompts for safety

### 4. Clarity
- ✅ Clear error messages
- ✅ Helpful suggestions
- ✅ Status information

## Configuration

### MongoDB Configuration Files

The script automatically reads MongoDB config from:

- **Dev**: `src/main/resources/application-dev.yml`
- **Test**: `src/test/resources/application-test.yml`
- **Prod**: `src/main/resources/application-prod.yml`

### Extracted Database Name

The script parses the YAML files to extract:
- `spring.data.mongodb.uri` (if present)
- OR builds URI from `host`, `port`, `database` fields

## Error Handling

### MongoDB Not Running

```bash
✗ MongoDB is not running!
ℹ Attempting to start MongoDB...
```

### Script Not Found

```bash
✗ Mock data script not found: ./scripts/insert-mock-data.js
ℹ You can load it via API after starting the app:
  ./scripts/load-mock-data.sh
```

### Database Connection Failed

```bash
✗ Cannot connect to database: foodai_restaurant_dev
```

## Comparison: Before vs After

### Before Update

```bash
# Loading mock data
./mvnw.sh run dev
# ❌ Error: load-mock-data.sh requires running app

# Checking database
mongosh foodai_restaurant_dev --eval "db.restaurants.countDocuments()"
# ❌ Manual command needed

# Loading data manually
mongosh foodai_restaurant_dev < scripts/insert-mock-data.js
# ❌ Manual command needed
```

### After Update

```bash
# Loading mock data
./mvnw.sh db-mock dev
# ✅ Works immediately

# Checking database
./mvnw.sh db-status dev
# ✅ Shows complete status

# Running app (auto-loads if empty)
./mvnw.sh run dev
# ✅ Loads data automatically if needed
```

## Integration with Other Scripts

### `mvnw.sh` + `insert-mock-data.js`
- Used for pre-application data loading
- Fast and direct MongoDB insertion
- No validation required

### `mvnw.sh` + `load-mock-data.sh`
- User can manually call after app starts
- Uses REST API with validation
- Requires running application

### `mvnw.sh` + `test-full-flow.sh`
- Complete automation available
- Handles entire lifecycle
- Good for testing

## Best Practices

### 1. Development Workflow
```bash
# Check what's in database
./mvnw.sh db-status dev

# Load fresh data if needed
./mvnw.sh db-mock dev

# Start application
./mvnw.sh run dev
```

### 2. Testing Workflow
```bash
# Clean and reload
./mvnw.sh db-clean dev  # Answer 'y' to reload

# Run tests
./mvnw.sh test
```

### 3. Production Workflow
```bash
# Never use db-mock in production
# Always verify data first
./mvnw.sh db-status prod

# Start with data check disabled
./mvnw.sh run prod --skip-data-check
```

## Summary of Changes

| Area | Change | Benefit |
|------|--------|---------|
| Data Loading | Use MongoDB script instead of API | Works before app starts |
| Commands | Added `db-status` | Check database anytime |
| Commands | Added `db-mock` | Load data easily |
| Help | Updated documentation | Clear usage |
| Error Messages | Improved suggestions | Better UX |

## Testing Results

### ✅ All Commands Tested and Working

```bash
✅ ./mvnw.sh db-status dev      # Shows status correctly
✅ ./mvnw.sh db-mock dev         # Loads data correctly
✅ ./mvnw.sh db-clean dev        # Cleans and reloads correctly
✅ ./mvnw.sh run dev             # Auto-loads if empty
✅ ./mvnw.sh --help              # Shows all commands
```

## Conclusion

The updated `mvnw.sh` script now:
- ✅ Works correctly with both data loading methods
- ✅ Provides convenient database management commands
- ✅ Handles all scenarios (empty DB, existing data, production)
- ✅ Gives clear feedback and helpful suggestions
- ✅ Integrates seamlessly with all other scripts

**Ready for use!** 🚀

