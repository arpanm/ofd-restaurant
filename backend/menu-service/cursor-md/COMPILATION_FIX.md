# Menu Service - Setup Guide

## 🚀 Quick Start with mvnw.sh

The Menu Service includes a **Maven wrapper script** (`mvnw.sh`) that automatically:
- ✅ Finds and configures Java 17
- ✅ Checks MongoDB availability
- ✅ Handles port conflicts
- ✅ Manages different profiles (dev/test/prod)

## ✅ Solution: Use mvnw.sh (RECOMMENDED)

### Step 1: Install Java 17

**On macOS (using Homebrew)**:
```bash
# Install Java 17
brew install openjdk@17

# Link it
sudo ln -sfn /usr/local/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk

# Verify installation
/usr/libexec/java_home -V
```

### Step 2: Use mvnw.sh

```bash
cd backend/menu-service

# Build and install
./mvnw.sh install

# Run the service
./mvnw.sh run

# Run tests
./mvnw.sh test

# See all options
./mvnw.sh --help
```

The script will **automatically**:
- Find Java 17 and configure JAVA_HOME
- Check if MongoDB is running
- Start MongoDB if needed (on macOS with Homebrew)
- Build/run the application

## 📋 Available Commands

```bash
# Show help
./mvnw.sh --help

# Build commands
./mvnw.sh install           # Clean install (skips tests)
./mvnw.sh install --force-clean  # Clean install with tests
./mvnw.sh build dev         # Build for dev profile
./mvnw.sh compile           # Compile only
./mvnw.sh clean             # Clean build artifacts

# Run commands
./mvnw.sh run               # Run in dev mode (default)
./mvnw.sh run prod          # Run in production mode
./mvnw.sh run dev --skip-mongo-check  # Skip MongoDB check

# Test command
./mvnw.sh test              # Run all tests
```

## 🔧 Script Features

The `mvnw.sh` script automatically handles:

### ✅ Java Version Management
- Finds Java 17 using `/usr/libexec/java_home -v 17`
- Sets `JAVA_HOME` and `PATH` automatically
- Verifies Java version before building
- Clear error messages if Java 17 is not found

### ✅ MongoDB Management
- Checks if MongoDB is running
- Attempts to start MongoDB automatically (macOS with Homebrew)
- Verifies MongoDB version
- Can skip MongoDB check with `--skip-mongo-check` flag

### ✅ Port Management
- Checks if port 8082 is in use
- Offers to kill conflicting processes
- Ensures clean startup

### ✅ Profile Support
- **dev**: Development profile (default)
- **test**: Test profile
- **prod**: Production profile

## 🚀 Quick Start After Fix

Once you've resolved the Java version issue:

```bash
cd backend/menu-service

# Clean build
mvn clean install

# Run tests
mvn test

# Generate coverage report
mvn jacoco:report
open target/site/jacoco/index.html

# Run the service
mvn spring-boot:run

# Access Swagger UI
open http://localhost:8082/swagger-ui.html
```

## 🔧 Files Already Fixed

The following compilation errors were already resolved:

✅ Removed duplicate methods from `MenuCategoryRepository`  
✅ Fixed `CustomizationVO` constructor parameters  
✅ Fixed `CustomizationOptionVO` constructor parameters  
✅ Updated Maven Compiler Plugin to 3.13.0  
✅ Added `<release>17</release>` configuration  

**The only remaining issue is the Java version incompatibility.**

## 📝 Verification

After installing Java 17, verify everything works:

```bash
# Check Java version
java -version
# Output should be: openjdk version "17.x.x"

# Check Maven can find Java 17
mvn -version
# Output should show Java version: 17.x.x

# Compile
cd backend/menu-service
mvn clean compile
# Should complete successfully

# Run all tests
mvn test
# All tests should pass

# Package
mvn package
# Should create menu-service-1.0.0-SNAPSHOT.jar
```

## 💡 Why Java 17?

The project specifies Java 17 because:
- ✅ Long-Term Support (LTS) release
- ✅ Stable and widely adopted
- ✅ Best compatibility with Spring Boot 3.2.x
- ✅ All Spring Boot starters support it
- ✅ Production-ready

## 🎯 Next Steps

1. **Install Java 17** (Solution 1)
2. **Run `mvn clean install`**
3. **Verify all tests pass**
4. **Access Swagger UI**: http://localhost:8082/swagger-ui.html
5. **Review `QUICK_START.md`** for API testing

---

**Need Help?**
- Maven Compiler Plugin docs: https://maven.apache.org/plugins/maven-compiler-plugin/
- Java 17 download: https://adoptium.net/temurin/releases/?version=17
- Spring Boot docs: https://docs.spring.io/spring-boot/docs/current/reference/html/

