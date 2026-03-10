# 🔧 Fix Build Failure - Java Version Issue

## ❌ Problem

You're seeing this error:
```
Fatal error compiling: java.lang.ExceptionInInitializerError: 
com.sun.tools.javac.code.TypeTag :: UNKNOWN
```

**Root Cause**: Your terminal is using **Java 25** instead of **Java 21**.

---

## ✅ Quick Fix (Current Terminal Session)

Run this in your terminal where the build is failing:

```bash
cd backend/restaurant-service
source ./scripts/SET_JAVA_21.sh
```

Then try building again:
```bash
./scripts/run-tests.sh
# OR
mvn clean compile
```

---

## ✅ Permanent Fix (All Future Terminal Sessions)

Add this line to your shell configuration file:

### For Zsh (Default on macOS)
```bash
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 21)' >> ~/.zshrc
source ~/.zshrc
```

### For Bash
```bash
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 21)' >> ~/.bashrc
source ~/.bashrc
```

---

## 🔍 Verify Java Version

After applying the fix, verify:

```bash
java -version
```

**Expected Output:**
```
openjdk version "21.0.6" 2025-01-21 LTS
OpenJDK Runtime Environment Temurin-21.0.6+7
```

**NOT Java 25!**

---

## 🚀 Step-by-Step Fix

### 1. Check Current Java Version
```bash
java -version
```

If it shows `25.x.x`, you need to switch to Java 21.

### 2. Apply Quick Fix
```bash
cd backend/restaurant-service
source ./scripts/SET_JAVA_21.sh
```

### 3. Verify It Worked
```bash
java -version
# Should show 21.x.x

mvn -version
# Should show "Java version: 21.x.x"
```

### 4. Try Building
```bash
mvn clean compile
```

### 5. Run Tests
```bash
./scripts/run-tests.sh
```

---

## 💡 Why This Happens

- macOS can have multiple Java versions installed
- The system might default to Java 25 (newest)
- Spring Boot 3.2.1 requires Java 17-21 (not 25)
- JaCoCo 0.8.13 supports up to Java 21
- Maven compiler plugin needs Java 21 for this project

---

## 🎯 Quick Commands Reference

```bash
# Check Java version
java -version

# List all installed Java versions
/usr/libexec/java_home -V

# Switch to Java 21 (temporary - current session only)
export JAVA_HOME=$(/usr/libexec/java_home -v 21)

# Switch to Java 21 (permanent)
echo 'export JAVA_HOME=$(/usr/libexec/java_home -v 21)' >> ~/.zshrc
source ~/.zshrc

# Verify Maven is using Java 21
mvn -version

# Clean and compile
mvn clean compile

# Run tests
./scripts/run-tests.sh
```

---

## 🆘 Still Having Issues?

### If Java 21 is not installed:
```bash
brew install openjdk@21
```

### If the fix doesn't persist:
1. Check which shell you're using:
   ```bash
   echo $SHELL
   ```

2. Add to the correct config file:
   - `/bin/zsh` → edit `~/.zshrc`
   - `/bin/bash` → edit `~/.bashrc`

### If Maven still uses wrong Java:
```bash
# Force Maven to use specific Java
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
export PATH=$JAVA_HOME/bin:$PATH
mvn clean compile
```

---

## ✅ Success Indicators

After fixing, you should see:

1. **Java version check:**
   ```bash
   $ java -version
   openjdk version "21.0.6"  ✅
   ```

2. **Maven version check:**
   ```bash
   $ mvn -version
   Java version: 21.0.6  ✅
   ```

3. **Build succeeds:**
   ```bash
   $ mvn clean compile
   [INFO] BUILD SUCCESS  ✅
   ```

4. **Tests run:**
   ```bash
   $ ./scripts/run-tests.sh
   44 tests passing  ✅
   ```

---

## 📝 Alternative: Use Cursor IDE Settings

If you prefer, configure Java 21 in Cursor IDE:

1. Open Cursor Settings
2. Go to **Languages & Frameworks → Build Tools → Maven**
3. Set **JDK for importer** to Java 21 (Temurin 21.0.6)
4. Apply and restart Cursor

Then tests will use Java 21 when run from the IDE.

---

## 🎉 After Fixing

Once Java 21 is configured:

```bash
# This will work:
./scripts/run-tests.sh

# This will work:
mvn clean test

# This will work:
mvn clean package

# Tests in IDE will work too!
```

---

*For more info, see:*
- `QUICK_START.md` - Quick reference
- `RUN_TESTS_SUCCESS.md` - Full test guide
- `KAFKA_MONGODB_SETUP.md` - Complete setup

