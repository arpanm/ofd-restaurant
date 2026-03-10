# Quick Reference Card

## 🚀 Maven Wrapper - Common Commands

### Start Application

```bash
# Development (with auto-setup)
./mvnw.sh

# Production
./mvnw.sh run prod

# Skip all checks (fast start)
./mvnw.sh run dev --skip-mongo-check --skip-data-check
```

### Testing

```bash
# Run all tests
./mvnw.sh test

# Run with coverage report
./scripts/run-tests.sh
```

### Build

```bash
# Build for dev
./mvnw.sh build dev

# Build for production
./mvnw.sh build prod

# Clean install
./mvnw.sh install
```

### Database Management

```bash
# Clean MongoDB database (with confirmation)
./mvnw.sh db-clean dev

# It will:
# - Show current document count
# - Ask for confirmation
# - Delete all restaurants
# - Optionally reload mock data
```

### Database Management

```bash
# Clean database (recommended - with confirmation)
./mvnw.sh db-clean dev

# Load mock data
./scripts/load-mock-data.sh

# Check MongoDB manually
mongosh foodai_restaurant_dev --eval "db.restaurants.countDocuments()"

# Clear database manually (no confirmation!)
mongosh foodai_restaurant_dev --eval "db.restaurants.deleteMany({})"
```

### Services

```bash
# Start all services (MongoDB + Kafka)
./scripts/manage-services.sh start

# Stop all services
./scripts/manage-services.sh stop

# Check status
./scripts/manage-services.sh status
```

## 📂 Project Structure

```
restaurant-service/
├── mvnw.sh                  # 🆕 Main Maven wrapper
├── run-tests.sh             # Test runner with reports
├── manage-services.sh       # Service management
├── scripts/
│   ├── load-mock-data.sh    # Mock data loader
│   └── insert-mock-data.js  # Mock data definitions
├── src/main/java/           # Application code
├── src/test/java/           # Test code
└── docs/                    # Documentation
```

## 🔧 Environment Setup (One-Time)

```bash
# 1. Install Java 21
brew install openjdk@21

# 2. Install MongoDB
brew install mongodb-community@7.0

# 3. Install Kafka (optional)
brew install kafka

# 4. Make scripts executable
chmod +x mvnw.sh run-tests.sh manage-services.sh scripts/*.sh

# 5. Set Java 21 (temporary)
source ./scripts/SET_JAVA_21.sh

# 6. Start services
./scripts/manage-services.sh start
```

## 🐛 Troubleshooting

### Port 8081 in use?

```bash
lsof -ti:8081 | xargs kill -9
```

### MongoDB not running?

```bash
brew services start mongodb-community@7.0
```

### Java version wrong?

```bash
source ./scripts/SET_JAVA_21.sh
java -version  # Should show 21.0.6
```

### Clean start?

```bash
./mvnw.sh clean
./scripts/load-mock-data.sh
./mvnw.sh run dev
```

## 📡 API Endpoints

### Health Check
```bash
curl http://localhost:8081/actuator/health
```

### Get All Restaurants
```bash
curl http://localhost:8081/api/v1/restaurants?page=0&size=10
```

### Search Restaurants
```bash
curl -X POST http://localhost:8081/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{"deliveryPincode": "560095"}'
```

### Swagger UI
```
http://localhost:8081/swagger-ui.html
```

## 📝 Configuration Files

| File | Purpose | Used By |
|------|---------|---------|
| `application.yml` | Base config | All profiles |
| `application-dev.yml` | Dev config | Development |
| `application-test.yml` | Test config | Testing |
| `application-prod.yml` | Prod config | Production |

## 🗄️ Databases

| Profile | Database Name | Auto Mock Data? |
|---------|---------------|-----------------|
| dev | `foodai_restaurant_dev` | ✅ Yes |
| test | `restaurant-service-test` | ✅ Yes |
| prod | (from config) | ❌ No |

## 🎯 Quick Workflows

### Morning Startup

```bash
./scripts/manage-services.sh start
./mvnw.sh run dev
```

### After Code Changes

```bash
# Just restart (fastest)
./mvnw.sh run dev --skip-mongo-check --skip-data-check

# Or with checks
./mvnw.sh run dev
```

### Before Committing

```bash
./mvnw.sh test
./mvnw.sh build dev
```

### Production Deployment

```bash
./mvnw.sh clean
./mvnw.sh install --force-clean
./mvnw.sh build prod
./mvnw.sh run prod --skip-data-check
```

## 🆘 Help

```bash
# Maven wrapper help
./mvnw.sh --help

# View documentation
cat MVNW_GUIDE.md
cat MOCK_DATA_GUIDE.md
cat README.md
```

## 🔗 Links

- [Main README](README.md) - Complete documentation
- [Maven Wrapper Guide](MVNW_GUIDE.md) - Detailed mvnw.sh usage
- [Mock Data Guide](MOCK_DATA_GUIDE.md) - Test data reference
- [Search API Docs](SEARCH_API_DOCUMENTATION.md) - API documentation
- [Quick Start](QUICK_START.md) - Getting started guide

---

**💡 Pro Tips:**

1. **Create an alias:** Add to `~/.zshrc`: `alias mvnw='./mvnw.sh'`
2. **Use tmux/screen:** Run services in separate panes
3. **Watch logs:** `tail -f /tmp/spring-boot.log` in another terminal
4. **Hot reload:** Use Spring DevTools for faster development

**⚡ Fastest Way to Start:**

```bash
./mvnw.sh
```

That's it! The script handles everything automatically. 🎉

