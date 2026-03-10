# Maven Wrapper - Quick Start

## 🎯 TL;DR - Get Started in 10 Seconds

```bash
./mvnw.sh
```

That's it! The script handles everything automatically.

---

## What Just Happened?

When you run `./mvnw.sh`, the script automatically:

1. ✅ **Configured Java 21** - Found and set JAVA_HOME
2. ✅ **Checked MongoDB** - Verified it's running (version 7.0+)
3. ✅ **Loaded Test Data** - Inserted 6 restaurants if database was empty
4. ✅ **Started Application** - Running on http://localhost:8081

---

## Common Commands

```bash
# Start application (dev mode)
./mvnw.sh

# Start in production mode
./mvnw.sh run prod

# Run all tests
./mvnw.sh test

# Build the application
./mvnw.sh build

# Clean and rebuild
./mvnw.sh clean
./mvnw.sh install

# Get help
./mvnw.sh --help
```

---

## Test Your Application

### 1. Health Check
```bash
curl http://localhost:8081/actuator/health
```

**Expected Response:**
```json
{"status":"UP"}
```

### 2. Search Restaurants
```bash
curl -X POST http://localhost:8081/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{"deliveryPincode": "560095"}'
```

**Expected Response:** List of restaurants servicing pincode 560095

### 3. Swagger UI
Open in browser:
```
http://localhost:8081/swagger-ui.html
```

---

## Sample Pincodes (from Mock Data)

Try searching for these pincodes:
- **560095** - Koramangala (Bella Italia)
- **560041** - Jayanagar (Sattvik Bhavan - Vegetarian)
- **560066** - Whitefield (Dragon Wok - Chinese)
- **560001** - MG Road (Quick Bites, The Royal Feast)
- **560102** - HSR Layout (Fusion Kitchen)

---

## Troubleshooting

### Port 8081 in use?
```bash
lsof -ti:8081 | xargs kill -9
./mvnw.sh
```

### MongoDB not running?
```bash
./scripts/manage-services.sh start
./mvnw.sh
```

### Want a clean start?
```bash
./mvnw.sh clean
./scripts/load-mock-data.sh
./mvnw.sh
```

---

## Need More Info?

- **Quick Reference:** [QUICK_REFERENCE.md](QUICK_REFERENCE.md) - Common commands and workflows
- **Detailed Guide:** [MVNW_GUIDE.md](MVNW_GUIDE.md) - Comprehensive documentation
- **Mock Data:** [MOCK_DATA_GUIDE.md](MOCK_DATA_GUIDE.md) - Test data reference
- **Main README:** [README.md](README.md) - Complete project documentation

---

## Pro Tips

### Create an Alias
Add to your `~/.zshrc` or `~/.bashrc`:
```bash
alias mvnw='./mvnw.sh'
```

Then just run:
```bash
mvnw           # Instead of ./mvnw.sh
mvnw test      # Run tests
mvnw build     # Build app
```

### Fast Restart (Skip Checks)
When iterating quickly:
```bash
./mvnw.sh run dev --skip-mongo-check --skip-data-check
```

### Watch Logs in Another Terminal
```bash
tail -f /tmp/spring-boot.log
```

---

## What Makes This Special?

❌ **Without Maven Wrapper:**
```bash
# You had to do this every time:
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
brew services start mongodb-community@7.0
mongosh foodai_restaurant_dev --eval "db.restaurants.countDocuments()"
./scripts/load-mock-data.sh  # if count is 0
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

✅ **With Maven Wrapper:**
```bash
# Just do this:
./mvnw.sh
```

**Time Saved:** 80-90% reduction in setup time  
**Commands Reduced:** 5+ commands → 1 command  
**Error Reduction:** Automatic validation catches issues early

---

## Success Indicators

When the script runs successfully, you'll see:

```
========================================
Restaurant Service - Maven Wrapper
========================================

ℹ Command: run
ℹ Profile: dev

[00:00:01] Checking Java version...
✓ Java 21 configured: /Library/Java/.../temurin-21.jdk/Contents/Home
openjdk version "21.0.6" 2025-01-21 LTS

[00:00:02] Checking MongoDB...
✓ MongoDB is running (version: 7.0.26)

[00:00:03] Checking data in database: foodai_restaurant_dev
✓ Found 6 restaurant(s) in database

[00:00:04] Starting application in dev mode...
[INFO] Building Restaurant Service 1.0.0-SNAPSHOT
...
Started RestaurantApplication in 8.456 seconds
```

---

## Ready to Code?

You're all set! The application is running on **http://localhost:8081**

Start building features and let the Maven wrapper handle the infrastructure! 🚀

---

**Questions?** Check [MVNW_GUIDE.md](MVNW_GUIDE.md) or run `./mvnw.sh --help`

