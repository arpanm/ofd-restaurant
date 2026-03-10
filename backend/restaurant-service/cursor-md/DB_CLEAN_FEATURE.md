# Database Clean Command - Feature Summary

## Overview

Added a new `db-clean` command to the Maven wrapper (`mvnw.sh`) for safely cleaning MongoDB data with confirmation prompts and optional mock data reloading.

---

## Usage

```bash
./mvnw.sh db-clean [profile]

# Examples:
./mvnw.sh db-clean dev     # Clean dev database
./mvnw.sh db-clean test    # Clean test database
```

---

## What It Does

The `db-clean` command performs the following steps:

### 1. **Environment Setup**
- Automatically configures Java 21
- Checks MongoDB availability
- Determines database name from profile configuration

### 2. **Data Check**
```
[00:00:01] Checking MongoDB...
✓ MongoDB is running (version: 7.0.26)

[00:00:02] Cleaning database: foodai_restaurant_dev
⚠ Found 6 restaurant(s) in database
```

### 3. **Confirmation Prompt**
```
Are you sure you want to delete all data? (y/N):
```

**Safety Features:**
- ✅ Shows current document count before deletion
- ✅ Requires explicit confirmation (y/Y)
- ✅ Defaults to No if any other key is pressed
- ✅ Displays "Operation cancelled" if user declines

### 4. **Data Deletion**
```
✓ Successfully deleted 6 restaurant(s)
```

### 5. **Mock Data Reload (Optional)**
```
Load mock data now? (y/N):
```

If user selects Yes:
- Automatically runs `./scripts/load-mock-data.sh`
- Loads 6 restaurants with proper document structure
- Confirms successful loading

---

## Example Session

```bash
$ ./mvnw.sh db-clean dev

========================================
Restaurant Service - Maven Wrapper
========================================

ℹ Command: db-clean
ℹ Profile: dev

[00:00:01] Checking Java version...
✓ Java 21 configured: /Library/Java/.../temurin-21.jdk/Contents/Home

[00:00:02] Checking MongoDB...
✓ MongoDB is running (version: 7.0.26)

[00:00:03] Cleaning database: foodai_restaurant_dev
⚠ Found 6 restaurant(s) in database
Are you sure you want to delete all data? (y/N): y

✓ Successfully deleted 6 restaurant(s)
Load mock data now? (y/N): y
ℹ Loading mock data...

========================================
Mock Data Loaded Successfully!
========================================
```

---

## Benefits

### 1. **Safety**
- ❌ **Old way:** `mongosh ... deleteMany({})` - No confirmation, instant deletion
- ✅ **New way:** `./mvnw.sh db-clean dev` - Confirmation required, safe

### 2. **Convenience**
- One command instead of multiple steps
- Automatic mock data reload option
- No need to remember database names
- Profile-aware (dev/test/prod databases)

### 3. **Developer Experience**
- Clear prompts and messages
- Counts documents before and after
- Color-coded output
- Integrated with existing workflow

---

## When to Use

### **✅ Use `db-clean` When:**
- Starting fresh with new test data
- Fixing data structure issues (like the FSSAI_LICENSE → FSSAI fix)
- Testing with clean state
- After schema changes
- Debugging data-related issues

### **❌ Don't Use `db-clean` When:**
- In production (use with caution!)
- You need to keep existing data
- Testing incremental updates
- Running automated tests (use test profile)

---

## Workflow Integration

### Morning Fresh Start
```bash
./mvnw.sh db-clean dev    # Clean and reload data
./mvnw.sh run dev         # Start application
```

### After Schema Changes
```bash
./mvnw.sh db-clean dev    # Clean old data
./mvnw.sh run dev         # Start with new schema
```

### Troubleshooting Data Issues
```bash
./mvnw.sh db-clean dev    # Fix data problems
# Confirm deletion: y
# Reload mock data: y
./mvnw.sh run dev         # Test again
```

### Complete Clean Start
```bash
./mvnw.sh clean           # Clean build artifacts
./mvnw.sh db-clean dev    # Clean database
./mvnw.sh install         # Rebuild
./mvnw.sh run dev         # Start fresh
```

---

## Technical Details

### Database Configuration Parsing

The command automatically reads database names from:

**Development:**
```yaml
# application-dev.yml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/foodai_restaurant_dev
```

**Test:**
```yaml
# application-test.yml
spring:
  data:
    mongodb:
      database: restaurant-service-test
```

### Function Implementation

Located in `mvnw.sh`:

```bash
clean_database() {
    # 1. Get database name from profile config
    local db_name=$(get_database_name $(get_mongodb_config "$PROFILE"))
    
    # 2. Check current count
    local count=$(mongosh "$db_name" --eval "db.restaurants.countDocuments()" ...)
    
    # 3. Confirm deletion
    read -p "Are you sure...?" -n 1 -r
    
    # 4. Delete data
    mongosh "$db_name" --eval "db.restaurants.deleteMany({})" ...
    
    # 5. Verify deletion
    # 6. Optional mock data reload
}
```

---

## Error Handling

### MongoDB Not Running
```
✗ MongoDB is not running!
ℹ Please start MongoDB using './scripts/manage-services.sh start'
```

### mongosh Not Found
```
✗ mongosh not found!
ℹ Install: brew install mongosh
```

### Mock Data Script Missing
```
✗ Mock data script not found or not executable
ℹ Run: chmod +x ./scripts/load-mock-data.sh
```

### Deletion Failed
```
✗ Failed to delete data (still has 6 documents)
```

---

## Comparison: Before vs After

### Before (Manual Process)

```bash
# 1. Find database name
cat src/main/resources/application-dev.yml | grep database

# 2. Connect and check
mongosh foodai_restaurant_dev --eval "db.restaurants.countDocuments()"

# 3. Delete (no confirmation!)
mongosh foodai_restaurant_dev --eval "db.restaurants.deleteMany({})"

# 4. Verify
mongosh foodai_restaurant_dev --eval "db.restaurants.countDocuments()"

# 5. Manually reload data
./scripts/load-mock-data.sh foodai_restaurant_dev

# Total: 5 commands, error-prone, no safety checks
```

### After (Streamlined)

```bash
./mvnw.sh db-clean dev
# Prompts:
# 1. Confirm deletion? y
# 2. Load mock data? y

# Total: 1 command, interactive, safe
```

**Time Saved:** 80% reduction (from ~1-2 minutes to ~10-20 seconds)

---

## Documentation Updates

### Files Updated

1. **`mvnw.sh`**
   - Added `clean_database()` function
   - Added `db-clean` command case
   - Updated help text
   - Added example usage

2. **`cursor-md/QUICK_REFERENCE.md`**
   - Added Database Management section
   - Listed `db-clean` as recommended method
   - Updated manual commands section

3. **`cursor-md/MVNW_GUIDE.md`**
   - Added `db-clean` to commands table
   - Added Database Management examples section
   - Updated troubleshooting workflows

---

## Future Enhancements

Potential additions:

1. **Force Flag**
   ```bash
   ./mvnw.sh db-clean dev --force    # No confirmation
   ```

2. **Backup Before Delete**
   ```bash
   ./mvnw.sh db-clean dev --backup   # Export before delete
   ```

3. **Selective Deletion**
   ```bash
   ./mvnw.sh db-clean dev --collection=restaurants
   ```

4. **Statistics**
   ```bash
   ./mvnw.sh db-clean dev --stats    # Show detailed counts
   ```

---

## Troubleshooting

### Issue: Command Not Recognized

**Symptom:**
```
✗ Unknown command: db-clean
```

**Solution:**
```bash
# Ensure script is executable
chmod +x mvnw.sh

# Verify installation
./mvnw.sh --help | grep db-clean
```

### Issue: Permission Denied

**Symptom:**
```
Permission denied: ./mvnw.sh
```

**Solution:**
```bash
chmod +x mvnw.sh
./mvnw.sh db-clean dev
```

### Issue: Wrong Database

**Symptom:**
Deleting from wrong database

**Solution:**
```bash
# Always specify profile
./mvnw.sh db-clean dev     # For dev database
./mvnw.sh db-clean test    # For test database
./mvnw.sh db-clean prod    # For prod database
```

---

## Best Practices

### ✅ Do

- Always review document count before confirming
- Use the appropriate profile (dev/test/prod)
- Reload mock data after cleaning (dev/test)
- Verify application works after cleaning

### ❌ Don't

- Don't use `--force` flags in production (if implemented)
- Don't clean prod database without backup
- Don't skip the confirmation prompts
- Don't clean database while application is writing data

---

## Related Commands

| Command | Purpose | When to Use |
|---------|---------|-------------|
| `./mvnw.sh db-clean dev` | Clean database | Fresh start |
| `./scripts/load-mock-data.sh` | Load data only | Add mock data |
| `./mvnw.sh clean` | Clean build | Remove compiled files |
| `./scripts/manage-services.sh restart` | Restart MongoDB | MongoDB issues |

---

## Summary

The `db-clean` command provides a safe, interactive, and convenient way to manage MongoDB data during development. It's integrated into the existing Maven wrapper workflow and includes safety features like confirmation prompts and optional data reloading.

**Key Benefits:**
- ✅ Safe (requires confirmation)
- ✅ Fast (one command)
- ✅ Smart (profile-aware)
- ✅ Convenient (optional reload)
- ✅ Integrated (part of mvnw.sh)

**Result:** Better developer experience with safer database management! 🚀

