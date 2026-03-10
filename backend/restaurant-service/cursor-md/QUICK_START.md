# Quick Start Guide

## ⚡ 3-Step Quick Start

### 1. Start Services
```bash
cd backend/restaurant-service
./scripts/manage-services.sh start
```

### 2. Set Java 21 (IMPORTANT!)
```bash
source ./scripts/SET_JAVA_21.sh
# OR manually:
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

**Note**: If you see build errors, this is usually because Java 25 is active instead of Java 21. See `FIX_BUILD_FAILURE.md` for details.

### 3. Run Tests
```bash
./scripts/run-tests.sh
```

---

## 🎯 Common Commands

```bash
# Check everything is running
./manage-services.sh status

# Run all search tests
mvn test -Dtest="*Search*" -Djacoco.skip=true

# Stop services when done
./manage-services.sh stop
```

---

## 🚀 Run Tests in Cursor IDE

1. Open `RestaurantSearchControllerTest.java`
2. Click ▶️ button next to test class or method
3. Tests run automatically!

**Note**: Services must be running first!

---

## 📖 Full Documentation

- **Setup Guide**: `KAFKA_MONGODB_SETUP.md`
- **Complete Summary**: `INSTALLATION_SUCCESS_SUMMARY.md`

---

## ✅ Status

**All 16+ tests passing! 🎉**

Services:
- ✅ Kafka
- ✅ MongoDB  
- ✅ Java 21

