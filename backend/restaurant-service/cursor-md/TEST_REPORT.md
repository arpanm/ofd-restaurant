# Restaurant Service - Test Report

## Test Coverage Summary

This document describes the comprehensive test suite for the Restaurant Service.

## Test Structure

### 1. Unit Tests

#### Domain Model Tests
- **RestaurantTest.java** - Tests for Restaurant entity
  - Builder pattern creation
  - Outlets list operations
  - Multiple owners handling
  - Audit fields management

#### Mapper Tests
- **RestaurantMapperTest.java** - MapStruct mapper tests
  - Request DTO to Entity mapping
  - Entity to Response DTO mapping
  - Value Object mappings
  - Address, OperatingHours mappings

#### Service Tests
- **RestaurantServiceTest.java** - Business logic tests
  - Create restaurant successfully
  - Duplicate restaurant validation
  - Get restaurant by ID
  - Restaurant not found scenarios
  - Update restaurant
  - Soft delete restaurant
  - Approve restaurant
  - Ownership percentage validation

#### Exception Handler Tests
- **GlobalExceptionHandlerTest.java** - Exception handling tests
  - RestaurantNotFoundException handling
  - OutletNotFoundException handling
  - RestaurantAlreadyExistsException handling
  - InvalidRestaurantStatusException handling
  - Generic exception handling

### 2. Controller Tests (MockMvc)

- **RestaurantControllerTest.java** - REST API tests
  - POST /api/v1/restaurants - Create restaurant
  - GET /api/v1/restaurants/{id} - Get restaurant
  - GET /api/v1/restaurants - Get all with pagination
  - GET /api/v1/restaurants/search - Search restaurants
  - PUT /api/v1/restaurants/{id} - Update restaurant
  - DELETE /api/v1/restaurants/{id} - Delete restaurant
  - GET /api/v1/restaurants/by-owner/{ownerId} - Get by owner
  - POST /api/v1/restaurants/{id}/approve - Approve restaurant
  - Validation error scenarios

### 3. Repository Tests (Testcontainers)

- **RestaurantRepositoryTest.java** - Data access tests
  - Save and retrieve restaurant
  - Find by ID and not deleted
  - Find by name (case-insensitive)
  - Find by cuisine type
  - Search by name or cuisine
  - Handle outlets in restaurant
  - Find restaurant by outlet ID

### 4. Integration Tests (Testcontainers)

- **RestaurantIntegrationTest.java** - End-to-end tests
  - Full CRUD flow (Create, Read, Update, Delete)
  - Validation error scenarios
  - Search functionality
  - 404 Not Found scenarios
  - Business rule validation (ownership percentage)

### 5. Context Load Test

- **RestaurantApplicationTest.java** - Application startup test
  - Spring Boot context loads successfully

## Running Tests

### Run All Tests
```bash
mvn clean test
```

### Run Tests with Coverage Report
```bash
./scripts/run-tests.sh
```

### Run Only Unit Tests
```bash
mvn test -Dtest="*Test"
```

### Run Only Integration Tests
```bash
mvn test -Dtest="*IntegrationTest"
```

### Generate Reports
```bash
# Generate JaCoCo coverage report
mvn jacoco:report

# Generate Surefire test report
mvn surefire-report:report

# Generate Maven site with all reports
mvn site
```

## Test Reports Location

After running tests, reports are generated in the following locations:

### JaCoCo Code Coverage Report
```
target/site/jacoco/index.html
```
- Line coverage
- Branch coverage
- Complexity metrics
- Coverage by package/class/method

### Surefire Test Report
```
target/site/surefire-report.html
```
- Test execution summary
- Success/failure counts
- Execution time per test
- Stack traces for failures

### XML Test Reports
```
target/surefire-reports/*.xml
```
- JUnit XML format
- Can be consumed by CI/CD tools
- Jenkins, GitLab CI, GitHub Actions compatible

## Coverage Requirements

### Minimum Coverage Thresholds

- **Line Coverage**: 70% (enforced by JaCoCo)
- **Branch Coverage**: 60% (enforced by JaCoCo)

### Current Coverage (Expected)

- **Unit Tests**: 90%+ coverage of service layer
- **Controller Tests**: 80%+ coverage of controllers
- **Integration Tests**: 70%+ coverage end-to-end
- **Overall**: 75%+ coverage

## Test Technologies

### Testing Frameworks
- **JUnit 5** (Jupiter) - Test framework
- **Mockito** - Mocking framework
- **AssertJ** - Fluent assertions
- **MockMvc** - Controller testing
- **REST Assured** - Integration testing

### Test Infrastructure
- **Testcontainers** - MongoDB container for integration tests
- **Spring Boot Test** - Test context management
- **Embedded MongoDB** - Lightweight MongoDB for tests

## Test Best Practices

### 1. Test Naming Convention
```java
@DisplayName("Should create restaurant successfully when valid input provided")
void shouldCreateRestaurantSuccessfully_whenValidInput()
```

### 2. Given-When-Then Pattern
```java
// Given - Setup test data
CreateRestaurantRequest request = ...;

// When - Execute the action
RestaurantResponse response = service.createRestaurant(request);

// Then - Verify expectations
assertThat(response).isNotNull();
```

### 3. Test Independence
- Each test is independent
- No shared state between tests
- Setup in @BeforeEach
- Cleanup handled automatically

### 4. Meaningful Assertions
```java
assertThat(restaurant.getOwners())
    .hasSize(2)
    .extracting(OwnerVO::getOwnershipPercentage)
    .containsExactly(60.0, 40.0);
```

## CI/CD Integration

### Maven Commands for CI

```bash
# Run tests and fail build if coverage below threshold
mvn clean verify

# Run tests and generate reports
mvn clean test jacoco:report surefire-report:report

# Skip tests (not recommended)
mvn clean install -DskipTests
```

### Jenkins Integration

```groovy
stage('Test') {
    steps {
        sh 'mvn clean test'
    }
    post {
        always {
            junit 'target/surefire-reports/*.xml'
            jacoco(
                execPattern: 'target/jacoco.exec',
                classPattern: 'target/classes',
                sourcePattern: 'src/main/java'
            )
        }
    }
}
```

### GitHub Actions Integration

```yaml
- name: Run Tests
  run: mvn clean test

- name: Generate Coverage Report
  run: mvn jacoco:report

- name: Upload Coverage to Codecov
  uses: codecov/codecov-action@v3
  with:
    files: ./target/site/jacoco/jacoco.xml
```

## Continuous Monitoring

### Coverage Trends
- Track coverage over time
- Ensure coverage doesn't decrease
- Set up quality gates in CI/CD

### Test Execution Time
- Monitor test execution duration
- Optimize slow tests
- Parallelize test execution if needed

### Flaky Tests
- Identify and fix flaky tests
- Ensure deterministic test behavior
- Use proper wait strategies in integration tests

## Future Enhancements

### Planned Test Additions
- [ ] Performance tests with JMeter
- [ ] Mutation testing with PIT
- [ ] Contract testing with Pact
- [ ] Security testing with OWASP ZAP
- [ ] Load testing with Gatling

### Additional Coverage
- [ ] Additional service tests for all services
- [ ] More edge case scenarios
- [ ] Negative test cases
- [ ] Concurrent access tests

## Troubleshooting

### Common Issues

**1. Testcontainers Not Starting**
```bash
# Ensure Docker is running
docker ps

# Check Docker daemon logs
docker logs
```

**2. Tests Failing Locally**
```bash
# Clean and rebuild
mvn clean install

# Skip tests temporarily
mvn clean install -DskipTests
```

**3. Coverage Report Not Generated**
```bash
# Ensure tests run first
mvn clean test jacoco:report
```

## Summary

✅ **Comprehensive Test Suite** covering:
- Unit tests for all layers
- Controller tests with MockMvc
- Repository tests with Testcontainers
- Integration tests end-to-end
- Exception handling tests

✅ **Test Reporting** including:
- JaCoCo code coverage reports
- Surefire test execution reports
- XML reports for CI/CD integration

✅ **Quality Assurance**:
- 70%+ code coverage enforced
- All tests passing
- Automated report generation
- CI/CD ready

---

**Last Updated**: 2025-11-25  
**Version**: 1.0.0


