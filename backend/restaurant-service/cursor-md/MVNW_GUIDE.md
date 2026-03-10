# Maven Wrapper Guide

## Overview

The `mvnw.sh` script is a comprehensive Maven wrapper that automates the entire development workflow for the Restaurant Service. It handles environment setup, dependency checks, database initialization, and application startup.

## Features

✅ **Automated Java 21 Setup** - Automatically detects and configures Java 21  
✅ **MongoDB Health Check** - Verifies MongoDB is running and accessible  
✅ **Smart Mock Data Loading** - Loads test data only when database is empty  
✅ **Profile Management** - Easy switching between dev/test/prod environments  
✅ **Port Conflict Resolution** - Detects and resolves port conflicts  
✅ **Color-Coded Output** - Clear, readable terminal output  
✅ **Error Handling** - Comprehensive error detection and reporting  

## Quick Start

```bash
# Run in development mode (default)
./mvnw.sh

# Run in production mode
./mvnw.sh run prod

# Run tests
./mvnw.sh test

# Build the application
./mvnw.sh build
```

## Usage

### Basic Syntax

```bash
./mvnw.sh [command] [profile] [options]
```

### Commands

| Command | Description | Example |
|---------|-------------|---------|
| `run` | Run the application (default) | `./mvnw.sh run dev` |
| `test` | Run all tests | `./mvnw.sh test` |
| `build` | Build the application | `./mvnw.sh build prod` |
| `clean` | Clean build artifacts | `./mvnw.sh clean` |
| `install` | Clean install | `./mvnw.sh install` |
| `compile` | Compile source code | `./mvnw.sh compile` |
| `db-clean` | Clean MongoDB database | `./mvnw.sh db-clean dev` |

### Profiles

| Profile | Description | Database |
|---------|-------------|----------|
| `dev` | Development (default) | `foodai_restaurant_dev` |
| `test` | Testing | `restaurant-service-test` |
| `prod` | Production | (configured in yaml) |

### Options

| Option | Description |
|--------|-------------|
| `--skip-mongo-check` | Skip MongoDB availability check |
| `--skip-data-check` | Skip mock data loading check |
| `--force-clean` | Run tests during install |

## Examples

### Development Workflow

```bash
# 1. Clean start in dev mode
./mvnw.sh clean
./mvnw.sh run dev

# 2. Run with fresh data
./mvnw.sh clean
./mvnw.sh run dev

# 3. Quick restart (skip checks)
./mvnw.sh run dev --skip-mongo-check --skip-data-check
```

### Testing

```bash
# Run all tests
./mvnw.sh test

# Run tests with coverage
./scripts/run-tests.sh

# Build without tests
./mvnw.sh build dev
```

### Production Deployment

```bash
# Build for production
./mvnw.sh build prod

# Run in production mode
./mvnw.sh run prod --skip-data-check
```

### Database Management

```bash
# Clean database (interactive with confirmation)
./mvnw.sh db-clean dev

# What it does:
# 1. Shows current document count
# 2. Asks for confirmation to delete
# 3. Deletes all restaurants from database
# 4. Asks if you want to reload mock data
# 5. Optionally loads fresh mock data
```

### Troubleshooting

```bash
# Clean everything and rebuild
./mvnw.sh clean
./mvnw.sh install --force-clean

# Clean database and start fresh
./mvnw.sh db-clean dev
./mvnw.sh run dev

# Skip all checks and just run
./mvnw.sh run dev --skip-mongo-check --skip-data-check
```

## What the Script Does

### 1. Java Setup (Automatic)

```
[00:00:01] Checking Java version...
✓ Java 21 configured: /Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
openjdk version "21.0.6" 2025-01-21 LTS
```

The script:
- Locates Java 21 installation
- Sets `JAVA_HOME` environment variable
- Updates `PATH` to use Java 21
- Verifies Java version

### 2. MongoDB Check (Automatic)

```
[00:00:02] Checking MongoDB...
✓ MongoDB is running (version: 7.0.4)
```

The script:
- Checks if MongoDB is running
- Attempts to start MongoDB if not running
- Verifies database accessibility
- Reports MongoDB version

### 3. Data Check (Smart)

```
[00:00:03] Checking data in database: foodai_restaurant_dev
⚠ No restaurant data found in database: foodai_restaurant_dev
ℹ Loading mock data...
✓ Mock data loaded successfully
```

The script:
- Parses database name from profile configuration
- Checks if `restaurants` collection has data
- Loads mock data automatically (dev/test only)
- Skips mock data in production

### 4. Application Startup

```
[00:00:04] Starting application in dev mode...
[INFO] Scanning for projects...
[INFO] Building Restaurant Service 1.0.0-SNAPSHOT
...
```

The script:
- Checks if port 8081 is available
- Starts application with correct profile
- Streams Maven output

## Configuration Files

The script automatically reads MongoDB configuration from:

### Development
```yaml
# src/main/resources/application-dev.yml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/foodai_restaurant_dev
```

### Test
```yaml
# src/test/resources/application-test.yml
spring:
  data:
    mongodb:
      database: restaurant-service-test
```

### Production
```yaml
# src/main/resources/application-prod.yml
spring:
  data:
    mongodb:
      uri: ${MONGODB_URI}  # From environment
```

## Environment Variables

The script sets:

```bash
export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home"
export PATH="$JAVA_HOME/bin:$PATH"
```

## Prerequisites

### Required

- ✅ Java 21 (automatically detected)
- ✅ Maven 3.9+ (must be installed)
- ✅ MongoDB 7.0+ (auto-started if using brew)

### Optional

- `mongosh` - For database checks and mock data loading
- `lsof` - For port conflict detection

## Troubleshooting

### Java 21 Not Found

**Error:**
```
✗ Java 21 not found!
ℹ Please install Java 21: brew install openjdk@21
```

**Solution:**
```bash
brew install openjdk@21
```

### MongoDB Not Running

**Error:**
```
✗ MongoDB is not running!
```

**Solution:**
```bash
brew services start mongodb-community@7.0
```

Or skip the check:
```bash
./mvnw.sh run dev --skip-mongo-check
```

### Port 8081 In Use

**Prompt:**
```
⚠ Port 8081 is already in use
Do you want to kill the process and continue? (y/N):
```

**Options:**
1. Press `y` to automatically kill the process
2. Press `n` and manually stop it:
```bash
lsof -ti:8081 | xargs kill -9
```

### Mock Data Load Fails

**Warning:**
```
⚠ Failed to load mock data automatically
ℹ You can load it manually: ./scripts/load-mock-data.sh foodai_restaurant_dev
```

**Solution:**
```bash
./scripts/load-mock-data.sh foodai_restaurant_dev
```

Or skip the check:
```bash
./mvnw.sh run dev --skip-data-check
```

### Build Failures

**Solution:**
```bash
# Clean everything
./mvnw.sh clean

# Rebuild from scratch
./mvnw.sh install --force-clean

# Check Java version
java -version

# Check Maven version
mvn -version
```

## Advanced Usage

### Custom Maven Goals

You can extend the script by editing the `run_maven_command` function:

```bash
# Add in mvnw.sh
package)
    print_step "Packaging application..."
    mvn package -DskipTests
    ;;
```

### Custom Profiles

Add support for custom profiles in your `application-{profile}.yml`:

```yaml
# application-staging.yml
spring:
  profiles: staging
  data:
    mongodb:
      uri: mongodb://staging-server:27017/restaurant_staging
```

Then run:
```bash
./mvnw.sh run staging
```

### Environment-Specific Behavior

The script behaves differently based on profile:

**Development/Test:**
- ✅ Loads mock data automatically
- ✅ Verbose logging
- ✅ Auto-starts MongoDB

**Production:**
- ❌ No mock data loading
- ❌ Requires explicit database setup
- ❌ Fails fast on missing services

## Integration with IDEs

### IntelliJ IDEA

1. **Add as External Tool:**
   - Settings → Tools → External Tools → Add
   - Name: `Run Dev`
   - Program: `$ProjectFileDir$/backend/restaurant-service/mvnw.sh`
   - Arguments: `run dev`
   - Working directory: `$ProjectFileDir$/backend/restaurant-service`

2. **Add Run Configuration:**
   - Run → Edit Configurations → Add → Shell Script
   - Script path: `/path/to/mvnw.sh`
   - Script options: `run dev`

### VS Code

Add to `.vscode/tasks.json`:

```json
{
  "version": "2.0.0",
  "tasks": [
    {
      "label": "Run Dev",
      "type": "shell",
      "command": "./mvnw.sh run dev",
      "options": {
        "cwd": "${workspaceFolder}/backend/restaurant-service"
      },
      "problemMatcher": []
    }
  ]
}
```

## Comparison with Other Scripts

| Feature | `mvnw.sh` | `run-tests.sh` | `manage-services.sh` |
|---------|-----------|----------------|----------------------|
| Java setup | ✅ Auto | ✅ Manual | ❌ |
| MongoDB check | ✅ Yes | ✅ Yes | ✅ Manages |
| Mock data | ✅ Smart | ❌ | ❌ |
| Run app | ✅ Yes | ❌ | ❌ |
| Run tests | ✅ Yes | ✅ Detailed | ❌ |
| Port check | ✅ Yes | ❌ | ❌ |
| Profiles | ✅ All | ✅ Test only | ❌ |

## Best Practices

### Development

```bash
# Morning startup
./mvnw.sh run dev

# After code changes
./mvnw.sh compile
./mvnw.sh run dev --skip-data-check

# Before committing
./mvnw.sh test
```

### CI/CD Pipeline

```bash
# Build stage
./mvnw.sh clean
./mvnw.sh install --force-clean

# Test stage
./mvnw.sh test

# Package stage
./mvnw.sh build prod
```

### Production Deployment

```bash
# Pre-deployment checks
./mvnw.sh clean
./mvnw.sh build prod

# Deployment
./mvnw.sh run prod --skip-data-check
```

## Help

Display help message:

```bash
./mvnw.sh --help
./mvnw.sh -h
```

Output:
```
Restaurant Service - Maven Wrapper

USAGE:
    ./mvnw.sh [command] [profile] [options]

COMMANDS:
    run          Run the application (default)
    test         Run tests
    ...
```

## Related Documentation

- [README.md](README.md) - Main project documentation
- [QUICK_START.md](QUICK_START.md) - Getting started guide
- [MOCK_DATA_GUIDE.md](MOCK_DATA_GUIDE.md) - Mock data reference
- [scripts/README.md](scripts/README.md) - Scripts documentation

## Support

For issues or questions:
1. Check this guide
2. Run with `--help` flag
3. Check logs in `/tmp/spring-boot.log`
4. Review [TROUBLESHOOTING.md](TROUBLESHOOTING.md)

---

**Pro Tip:** Create an alias in your shell:

```bash
# Add to ~/.zshrc or ~/.bashrc
alias mvnw='./mvnw.sh'

# Then simply run:
mvnw run dev
mvnw test
```

