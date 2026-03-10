# Menu Service - Quick Start Guide

## 🚀 Getting Started in 5 Minutes

### Prerequisites
- Java 17 (`brew install openjdk@17`)
- Maven 3.8+
- MongoDB 6.0+ (running on localhost:27017)
- Kafka (running on localhost:9092)
- Redis (running on localhost:6379)

### 🎯 Recommended: Use mvnw.sh

The easiest way to build and run the service:

```bash
# Navigate to service directory
cd backend/menu-service

# Install Java 17 (if not already installed)
brew install openjdk@17
sudo ln -sfn /usr/local/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk

# Build the service (mvnw.sh handles Java 17 automatically)
./mvnw.sh install

# Run the service
./mvnw.sh run

# Or run in production mode
./mvnw.sh run prod
```

The `mvnw.sh` script automatically:
- ✅ Finds and configures Java 17
- ✅ Checks MongoDB availability
- ✅ Handles port conflicts
- ✅ Manages different profiles

### Alternative: Manual Maven Commands

If you prefer to use Maven directly:

```bash
# Set Java 17 for current session
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Build the project
mvn clean install

# Run the service (development profile)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

---

## 📍 Access Points

| Resource | URL |
|----------|-----|
| **API Base** | http://localhost:8082/api/v1 |
| **Swagger UI** | http://localhost:8082/swagger-ui.html |
| **OpenAPI Docs** | http://localhost:8082/v3/api-docs |
| **Health Check** | http://localhost:8082/actuator/health |
| **Metrics** | http://localhost:8082/actuator/metrics |

---

## 🧪 Quick API Test

### 1. Create a Menu Item

```bash
curl -X POST http://localhost:8082/api/v1/menu-items \
  -H "Content-Type: application/json" \
  -d '{
    "restaurantId": "rest123",
    "name": "Butter Chicken",
    "description": "Rich and creamy North Indian curry",
    "category": "Main Course",
    "basePrice": 350.00,
    "available": true,
    "vegetarian": false,
    "preparationTime": 25,
    "spiceLevel": 3,
    "nutritionalInfo": {
      "calories": 450,
      "protein": 28,
      "carbohydrates": 35,
      "fat": 22
    }
  }'
```

### 2. Get Menu Item by ID

```bash
curl http://localhost:8082/api/v1/menu-items/{id}
```

### 3. Get All Menu Items for a Restaurant

```bash
curl http://localhost:8082/api/v1/menu-items/restaurant/rest123
```

### 4. Search Menu Items

```bash
curl -X POST http://localhost:8082/api/v1/menu-items/search \
  -H "Content-Type: application/json" \
  -d '{
    "restaurantId": "rest123",
    "category": "Main Course",
    "vegetarian": true,
    "minPrice": 200,
    "maxPrice": 500
  }'
```

---

## 🔧 Configuration

### MongoDB Connection
Edit `application-dev.yml`:
```yaml
spring:
  data:
    mongodb:
      host: localhost
      port: 27017
      database: menu-service
```

### Kafka Configuration
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
```

### Redis Configuration
```yaml
spring:
  redis:
    host: localhost
    port: 6379
```

---

## 🧪 Running Tests

### Run All Tests
```bash
mvn test
```

### Run Only Unit Tests
```bash
mvn test -Dtest="*Test"
```

### Run Only Integration Tests
```bash
mvn test -Dtest="*IntegrationTest"
```

### Generate Coverage Report
```bash
mvn jacoco:report
open target/site/jacoco/index.html
```

---

## 📊 Test Coverage Target

- **Minimum Coverage**: 80%
- **Current Coverage**: Check `target/site/jacoco/index.html` after running tests

---

## 🐛 Troubleshooting

### Issue: MongoDB Connection Error
**Solution**: Ensure MongoDB is running
```bash
# Check if MongoDB is running
mongosh --eval "db.version()"

# Start MongoDB (if using Docker)
docker run -d -p 27017:27017 --name mongodb mongo:6.0.3
```

### Issue: Kafka Connection Error
**Solution**: Ensure Kafka is running
```bash
# Check if Kafka is running
docker ps | grep kafka

# Start Kafka (if using Docker Compose)
cd backend
docker-compose up -d kafka
```

### Issue: Redis Connection Error
**Solution**: Ensure Redis is running
```bash
# Check if Redis is running
redis-cli ping

# Start Redis (if using Docker)
docker run -d -p 6379:6379 --name redis redis:7-alpine
```

### Issue: Tests Failing
**Solution**: Tests use Testcontainers, ensure Docker is running
```bash
# Check if Docker is running
docker --version
docker ps
```

---

## 📚 Learn More

- **Implementation Details**: See `IMPLEMENTATION_STATUS.md`
- **Implementation Guide**: See `IMPLEMENTATION_GUIDE.md`
- **Service README**: See `README.md`
- **Architecture**: See `@backend/SERVICE_TEMPLATE_SPEC.md`
- **Domain Model**: See `@backend/DOMAIN_MODEL.md`

---

## 🎯 Next Steps

1. ✅ Service is running
2. ✅ Create your first menu item
3. ✅ Explore Swagger UI
4. ✅ Run tests
5. ✅ Check code coverage
6. ✅ Review implementation guide
7. ✅ Deploy to staging/production

---

## 💡 Pro Tips

- Use **Swagger UI** for interactive API testing
- Enable **dev profile** for detailed logging
- Check **Actuator endpoints** for monitoring
- Review **JaCoCo reports** for coverage insights
- Use **Postman/Insomnia** for advanced API testing

---

**Happy Coding! 🚀**

