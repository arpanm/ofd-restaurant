# Restaurant Service Scripts

This directory contains all utility scripts for the Restaurant Service.

## Available Scripts

| Script | Purpose | Usage |
|--------|---------|-------|
| `manage-services.sh` | Start/stop MongoDB & Kafka | `./scripts/manage-services.sh start` |
| `run-tests.sh` | Run tests with coverage reports | `./scripts/run-tests.sh` |
| `SET_JAVA_21.sh` | Set Java 21 environment | `source ./scripts/SET_JAVA_21.sh` |
| `load-mock-data.sh` | Load mock restaurant data | `./scripts/load-mock-data.sh` |
| `insert-mock-data.js` | MongoDB mock data script | (Used by load-mock-data.sh) |

---

## Service Management Scripts

### manage-services.sh

Manages MongoDB and Kafka services required for running the application.

**Commands:**
```bash
./scripts/manage-services.sh start      # Start all services
./scripts/manage-services.sh stop       # Stop all services
./scripts/manage-services.sh restart    # Restart all services
./scripts/manage-services.sh status     # Check service status
```

### run-tests.sh

Runs the complete test suite with coverage reports and service checks.

**Usage:**
```bash
./scripts/run-tests.sh
```

**Features:**
- Pre-flight checks for Java and services
- Runs all test suites
- Generates JaCoCo coverage reports
- Generates Surefire test reports
- Displays test summary

**Reports Generated:**
- `target/site/jacoco/index.html` - Coverage report
- `target/site/surefire-report.html` - Test report

### SET_JAVA_21.sh

Sets the Java environment to Java 21 (required for the project).

**Usage:**
```bash
source ./scripts/SET_JAVA_21.sh
```

**Note:** Must use `source` to set environment variables in the current shell.

---

## Mock Data Scripts

### Overview

The mock data scripts help you populate your MongoDB database with realistic restaurant data for testing and development purposes.

### Files

- **`insert-mock-data.js`** - MongoDB shell script that inserts mock restaurant data
- **`load-mock-data.sh`** - Bash wrapper script for convenient execution

### Mock Data Included

The script inserts **6 restaurants** with **7 outlets** across Bangalore:

| Restaurant | Cuisine | Price Range | Outlets | Pincode(s) |
|------------|---------|-------------|---------|------------|
| Bella Italia | Italian, Mediterranean | ₹1200/2 | 2 | 560095, 560038 |
| Sattvik Bhavan | Indian (Veg) | ₹600/2 | 1 | 560041 |
| Dragon Wok | Chinese, Asian, Thai | ₹900/2 | 1 | 560066 |
| Quick Bites | Fast Food, American | ₹400/2 | 1 | 560001 |
| The Royal Feast | Continental, Fine Dining | ₹2500/2 | 1 | 560001 |
| Fusion Kitchen | Multi-Cuisine | ₹800/2 | 1 | 560102 |

### Features

Each restaurant includes:
- ✅ Complete restaurant details (name, description, cuisine types)
- ✅ Multiple outlets with addresses and coordinates
- ✅ Operating hours for all days
- ✅ Serviceability configuration with pincodes
- ✅ Owner information
- ✅ FSSAI license details
- ✅ Contract and fee configurations
- ✅ Ratings, reviews, and order statistics
- ✅ Budget type coverage (BUDGET to PREMIUM)
- ✅ Vegetarian and non-vegetarian options

## Usage

### Method 1: Using the Shell Script (Recommended)

```bash
# From the restaurant-service directory
./scripts/load-mock-data.sh

# Or specify a different database name
./scripts/load-mock-data.sh foodai_restaurant_test
```

The script will:
1. Check if MongoDB is running
2. Verify mongosh is installed
3. Clear existing data (optional)
4. Insert mock restaurant data
5. Display a summary

### Method 2: Direct MongoDB Shell

```bash
# From the restaurant-service directory
mongosh foodai_restaurant_dev < scripts/insert-mock-data.js
```

### Method 3: Interactive MongoDB Shell

```bash
mongosh foodai_restaurant_dev
> load("scripts/insert-mock-data.js")
```

## Prerequisites

### 1. MongoDB Running

```bash
# Start MongoDB
brew services start mongodb-community@7.0

# Check status
brew services list | grep mongodb
```

### 2. MongoDB Shell (mongosh)

```bash
# Install if not already installed
brew install mongosh

# Verify installation
mongosh --version
```

## Testing the Mock Data

### 1. Start the Application

```bash
source ./scripts/SET_JAVA_21.sh
mvn spring-boot:run
```

### 2. Test Search API

**Search by Pincode (Koramangala):**
```bash
curl -X POST http://localhost:8081/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{
    "deliveryPincode": "560095"
  }'
```

**Search by Cuisine:**
```bash
curl -X POST http://localhost:8081/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{
    "deliveryPincode": "560001",
    "cuisineTypes": ["Italian"]
  }'
```

**Search by Budget:**
```bash
curl -X POST http://localhost:8081/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{
    "deliveryPincode": "560001",
    "budgetType": "BUDGET"
  }'
```

**Search with Rating Filter:**
```bash
curl -X POST http://localhost:8081/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{
    "deliveryPincode": "560095",
    "minRating": 4.5
  }'
```

**Search Vegetarian Only:**
```bash
curl -X POST http://localhost:8081/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{
    "deliveryPincode": "560041",
    "vegetarianOnly": true
  }'
```

### 3. View in Swagger UI

Open your browser and navigate to:
```
http://localhost:8081/swagger-ui.html
```

Look for the `/api/v1/restaurants/search` endpoint under "Restaurant Management".

### 4. Verify Data in MongoDB

```bash
mongosh foodai_restaurant_dev

# Count restaurants
db.restaurants.countDocuments()

# List all restaurants
db.restaurants.find({}, {name: 1, cuisineTypes: 1, averageCostForTwo: 1})

# Find restaurants by pincode
db.restaurants.find({"outlets.serviceabilityConfig.servicePincodes": "560095"})

# Find vegetarian restaurants
db.restaurants.find({vegetarianOnly: true})
```

## Test Scenarios

### Scenario 1: Browse by Area
- **Pincode**: `560095` (Koramangala)
- **Expected**: Bella Italia

### Scenario 2: Budget Dining
- **Pincode**: `560001`
- **Budget**: `BUDGET`
- **Expected**: Quick Bites

### Scenario 3: Premium Dining
- **Pincode**: `560001`
- **Budget**: `PREMIUM`
- **Expected**: The Royal Feast

### Scenario 4: Vegetarian Food
- **Pincode**: `560041`
- **Filter**: `vegetarianOnly: true`
- **Expected**: Sattvik Bhavan

### Scenario 5: High-Rated Restaurants
- **Pincode**: `560038`
- **Min Rating**: `4.5`
- **Expected**: Bella Italia (Indiranagar outlet)

### Scenario 6: Multi-Cuisine
- **Pincode**: `560102`
- **Cuisine**: `["Indian", "Chinese"]`
- **Expected**: Fusion Kitchen

## Cleaning Up Mock Data

To remove all mock data:

```bash
mongosh foodai_restaurant_dev --eval "
  db.restaurants.deleteMany({});
  db.outlet_history.deleteMany({});
  db.restaurant_history.deleteMany({});
  db.contract_history.deleteMany({});
  db.penalty_history.deleteMany({});
  db.serviceability_history.deleteMany({});
  print('Mock data cleared!');
"
```

## Customizing Mock Data

To add more restaurants or modify existing ones:

1. Edit `insert-mock-data.js`
2. Follow the existing restaurant object structure
3. Ensure all required fields are included
4. Run the script again

### Required Fields

```javascript
{
    name: String,              // Restaurant name
    description: String,       // Description
    cuisineTypes: [String],    // Array of cuisine types
    averageCostForTwo: Number, // Price for two people
    averageRating: Number,     // Rating (0-5)
    totalReviews: Number,      // Number of reviews
    vegetarianOnly: Boolean,   // Veg only flag
    status: String,            // PENDING, APPROVED, etc.
    acceptsOrders: Boolean,    // Can accept orders
    onboardingType: String,    // FULL_SERVICE, etc.
    owners: [...],             // Owner details
    documents: [...],          // FSSAI, etc.
    outlets: [...],            // Outlet details
    contract: {...},           // Contract details
    createdAt: Date,
    updatedAt: Date
}
```

## Troubleshooting

### Error: MongoDB is not running

```bash
brew services start mongodb-community@7.0
```

### Error: mongosh command not found

```bash
brew install mongosh
```

### Error: Cannot connect to MongoDB

Check if MongoDB is listening on the default port:
```bash
lsof -i :27017
```

### Error: Database access denied

Ensure MongoDB is running without authentication or configure credentials:
```bash
mongosh mongodb://username:password@localhost:27017/foodai_restaurant_dev
```

## Additional Resources

- [MongoDB Shell Documentation](https://www.mongodb.com/docs/mongodb-shell/)
- [Restaurant Service API Documentation](../README.md)
- [Search Feature Documentation](../SEARCH_API_DOCUMENTATION.md)

## Support

For issues or questions:
1. Check the main [README.md](../README.md)
2. Review test files in `src/test/java/com/foodai/restaurant/`
3. Check application logs when running the service

