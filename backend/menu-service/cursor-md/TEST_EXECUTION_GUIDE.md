# Menu Service Test Execution Guide

## Quick Start

### Run All Tests
```bash
cd backend/menu-service
./mvnw.sh test
```

### Run Specific Test Classes
```bash
# Feature tests only
./mvnw.sh test -Dtest="*FeatureTest"

# Service layer tests only
./mvnw.sh test -Dtest="*ServiceTest"

# API integration tests only
./mvnw.sh test -Dtest="*APIIntegrationTest"

# Domain model tests only
./mvnw.sh test -Dtest="MenuItemTest,MenuCategoryTest"
```

### Run Single Test Method
```bash
./mvnw.sh test -Dtest="MenuCustomizationFeatureTest#shouldAllowSpiceLevelCustomization"
```

---

## Test Categories

### 1. Feature Tests (`src/test/java/com/foodai/menu/feature/`)
**Purpose**: Test complete feature workflows based on UI specs

**Files**:
- `MenuCustomizationFeatureTest.java` - Food customization (12 tests)
- `SmartPricingFeatureTest.java` - Dynamic pricing (15 tests)
- `DietaryPreferencesFeatureTest.java` - Dietary filters (9 tests)
- `MenuSearchAndFilterFeatureTest.java` - Search/filter (7 tests)
- `MenuAvailabilityFeatureTest.java` - Availability management (8 tests)
- `AIMenuSuggestionsFeatureTest.java` - AI optimization (14 tests)

**Run Command**:
```bash
./mvnw.sh test -Dtest="*FeatureTest"
```

---

### 2. Service Layer Tests (`src/test/java/com/foodai/menu/service/`)
**Purpose**: Test business logic and service methods

**Files**:
- `MenuItemServiceTest.java` - CRUD operations
- `MenuCategoryServiceTest.java` - Category management

**Run Command**:
```bash
./mvnw.sh test -Dtest="*ServiceTest"
```

---

### 3. API Integration Tests (`src/test/java/com/foodai/menu/api/`)
**Purpose**: Test REST endpoints and HTTP contracts

**Files**:
- `MenuAPIIntegrationTest.java` - Full API testing (15+ tests)

**Run Command**:
```bash
./mvnw.sh test -Dtest="*APIIntegrationTest"
```

---

### 4. Controller Tests (`src/test/java/com/foodai/menu/controller/`)
**Purpose**: Test controller layer with MockMvc

**Files**:
- `MenuItemControllerTest.java`
- `MenuCategoryControllerTest.java`

**Run Command**:
```bash
./mvnw.sh test -Dtest="*ControllerTest"
```

---

### 5. Domain Model Tests (`src/test/java/com/foodai/menu/domain/model/`)
**Purpose**: Test domain logic and validation

**Files**:
- `MenuItemTest.java` - Core domain logic

**Run Command**:
```bash
./mvnw.sh test -Dtest="MenuItemTest"
```

---

### 6. Mapper Tests (`src/test/java/com/foodai/menu/mapper/`)
**Purpose**: Test DTO <-> Entity conversions

**Files**:
- `MenuItemMapperTest.java`

**Run Command**:
```bash
./mvnw.sh test -Dtest="*MapperTest"
```

---

### 7. Integration Tests (`src/test/java/com/foodai/menu/integration/`)
**Purpose**: Test with real MongoDB (Testcontainers)

**Files**:
- `MenuItemIntegrationTest.java`

**Run Command**:
```bash
./mvnw.sh integration-test
```

---

## Code Coverage

### Generate Coverage Report
```bash
./mvnw.sh clean test jacoco:report
```

### View Coverage Report
```bash
# macOS
open target/site/jacoco/index.html

# Linux
xdg-open target/site/jacoco/index.html

# Windows
start target/site/jacoco/index.html
```

### Coverage Thresholds
- Overall: 80%
- Domain Models: 90%
- Services: 85%
- Controllers: 80%

---

## Test Filtering

### By Tag (if using JUnit Tags)
```bash
./mvnw.sh test -Dgroups="integration"
./mvnw.sh test -Dgroups="unit"
```

### By Pattern
```bash
# All tests with "Customization" in name
./mvnw.sh test -Dtest="*Customization*"

# All tests with "API" in name
./mvnw.sh test -Dtest="*API*"
```

---

## Common Issues & Solutions

### Issue 1: MongoDB Connection Error
**Error**: `Could not start MongoDB container`

**Solution**:
```bash
# Start MongoDB manually
brew services start mongodb-community

# Or use Docker
docker run -d -p 27017:27017 mongo:latest
```

---

### Issue 2: Port Already in Use
**Error**: `Port 8082 is already in use`

**Solution**:
```bash
# Kill process on port 8082
lsof -ti:8082 | xargs kill -9

# Or change port in application-test.yml
server.port: 8083
```

---

### Issue 3: Java Version Mismatch
**Error**: `Fatal error compiling: java.lang.ExceptionInInitializerError`

**Solution**:
```bash
# Install Java 17
brew install openjdk@17

# Link it
sudo ln -sfn /usr/local/opt/openjdk@17/libexec/openjdk.jdk \
  /Library/Java/JavaVirtualMachines/openjdk-17.jdk

# Verify
java -version  # Should show 17.x.x
```

---

### Issue 4: Tests Fail Due to Missing Dependencies
**Error**: `ClassNotFoundException`

**Solution**:
```bash
# Clean and rebuild
./mvnw.sh clean install -DskipTests

# Then run tests
./mvnw.sh test
```

---

## Test Data Management

### Reset Test Database
Tests are `@Transactional`, so they auto-rollback.

### Manual Cleanup (if needed)
```bash
# Connect to MongoDB
mongosh

# Drop test database
use menuservice_test
db.dropDatabase()
```

---

## Performance Testing

### Run with Performance Profiling
```bash
./mvnw.sh test -Dtest="*FeatureTest" -DargLine="-Xmx2g -XX:+PrintGCDetails"
```

### Measure Test Execution Time
```bash
time ./mvnw.sh test
```

---

## CI/CD Integration

### GitHub Actions Example
```yaml
- name: Run Tests
  run: |
    cd backend/menu-service
    ./mvnw.sh clean test

- name: Upload Coverage
  run: |
    ./mvnw.sh jacoco:report
    bash <(curl -s https://codecov.io/bash)
```

---

## Debugging Tests

### Run with Debug Output
```bash
./mvnw.sh test -X  # Maven debug mode
```

### Run Specific Test in Debug Mode
```bash
# In IDE (IntelliJ/VS Code): Right-click test method > Debug
```

### View SQL Queries
Enable in `application-test.yml`:
```yaml
logging:
  level:
    org.springframework.data.mongodb: DEBUG
```

---

## Test Reports

### Surefire Reports
Located at: `target/surefire-reports/`

**View HTML Report**:
```bash
open target/surefire-reports/index.html
```

### Test Summary
```bash
# After running tests, check console output for:
# Tests run: X, Failures: Y, Errors: Z, Skipped: W
```

---

## Best Practices

### 1. Run Tests Before Commit
```bash
git add .
./mvnw.sh test  # Must pass
git commit -m "Your message"
```

### 2. Run Full Suite Periodically
```bash
# Clean build with tests
./mvnw.sh clean install
```

### 3. Check Coverage Regularly
```bash
./mvnw.sh jacoco:report
# Ensure coverage > 80%
```

### 4. Fix Failing Tests Immediately
Don't commit with failing tests.

---

## Quick Reference

| Command | Purpose |
|---------|---------|
| `./mvnw.sh test` | Run all tests |
| `./mvnw.sh test -Dtest="ClassName"` | Run specific class |
| `./mvnw.sh test -Dtest="ClassName#methodName"` | Run specific method |
| `./mvnw.sh integration-test` | Run integration tests |
| `./mvnw.sh jacoco:report` | Generate coverage report |
| `./mvnw.sh clean test` | Clean build + run tests |
| `./mvnw.sh test -DskipTests` | Skip tests (not recommended) |
| `./mvnw.sh test -Dtest="*FeatureTest"` | Run all feature tests |
| `./mvnw.sh test -X` | Debug mode |

---

## Contact & Support

**Test Issues**: Create issue in project repository  
**Coverage Questions**: Review `COMPREHENSIVE_TEST_COVERAGE_SUMMARY.md`  
**Architecture Questions**: Review `backend/SERVICE_TEMPLATE_SPEC.md`

---

**Last Updated**: December 1, 2025  
**Maintainer**: FoodAI Development Team

