# Mock Data Quick Reference

## Overview

Mock restaurant data for testing the Restaurant Service application.

## Data Summary

**Total Restaurants**: 6  
**Total Outlets**: 7  
**Coverage**: Bangalore city with multiple areas

## Restaurants

| # | Name | Cuisine | Price/2 | Rating | Veg | Outlets | Pincodes |
|---|------|---------|---------|--------|-----|---------|----------|
| 1 | **Bella Italia** | Italian, Mediterranean | ₹1200 | 4.5⭐ | No | 2 | 560095, 560038 |
| 2 | **Sattvik Bhavan** | Indian (N/S) | ₹600 | 4.3⭐ | **Yes** | 1 | 560041 |
| 3 | **Dragon Wok** | Chinese, Asian, Thai | ₹900 | 4.4⭐ | No | 1 | 560066 |
| 4 | **Quick Bites** | Fast Food, American | ₹400 | 4.0⭐ | No | 1 | 560001 |
| 5 | **The Royal Feast** | Continental, Fine Dining | ₹2500 | 4.7⭐ | No | 1 | 560001 |
| 6 | **Fusion Kitchen** | Multi-Cuisine | ₹800 | 4.2⭐ | No | 1 | 560102 |

## Budget Categories

- **BUDGET** (< ₹500): Quick Bites
- **MID_RANGE** (₹500-1500): Sattvik Bhavan, Dragon Wok, Bella Italia, Fusion Kitchen
- **PREMIUM** (> ₹1500): The Royal Feast

## Area Coverage

| Area | Pincode | Restaurants |
|------|---------|-------------|
| Koramangala | 560095 | Bella Italia |
| Indiranagar | 560038 | Bella Italia |
| Jayanagar | 560041 | Sattvik Bhavan |
| Whitefield | 560066 | Dragon Wok |
| MG Road | 560001 | Quick Bites, The Royal Feast |
| HSR Layout | 560102 | Fusion Kitchen |

## Quick Start

### 1. Load Mock Data

```bash
./scripts/load-mock-data.sh
```

### 2. Start Application

```bash
source ./scripts/SET_JAVA_21.sh
mvn spring-boot:run
```

### 3. Test Search

**Basic Search (Koramangala area):**
```bash
curl -X POST http://localhost:8081/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{"deliveryPincode": "560095"}'
```

**Budget Dining:**
```bash
curl -X POST http://localhost:8081/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{
    "deliveryPincode": "560001",
    "budgetType": "BUDGET"
  }'
```

**Vegetarian Only:**
```bash
curl -X POST http://localhost:8081/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{
    "deliveryPincode": "560041",
    "vegetarianOnly": true
  }'
```

**Premium Dining:**
```bash
curl -X POST http://localhost:8081/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{
    "deliveryPincode": "560001",
    "budgetType": "PREMIUM",
    "minRating": 4.5
  }'
```

**Italian Food:**
```bash
curl -X POST http://localhost:8081/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{
    "deliveryPincode": "560095",
    "cuisineTypes": ["Italian"]
  }'
```

## Test Scenarios

### Scenario 1: Weekend Dining in Koramangala
- **Pincode**: 560095
- **Expected**: Bella Italia (Italian, Mediterranean)
- **Features**: 2 outlets nearby, ₹1200/2, 4.5⭐

### Scenario 2: Quick Lunch in MG Road
- **Pincode**: 560001
- **Budget**: BUDGET
- **Expected**: Quick Bites
- **Features**: Fast service, ₹400/2, ₹25 delivery

### Scenario 3: Vegetarian Dinner in Jayanagar
- **Pincode**: 560041
- **Filter**: vegetarianOnly = true
- **Expected**: Sattvik Bhavan
- **Features**: Pure veg, ₹600/2, 4.3⭐

### Scenario 4: Special Occasion in MG Road
- **Pincode**: 560001
- **Budget**: PREMIUM
- **Min Rating**: 4.5
- **Expected**: The Royal Feast
- **Features**: Fine dining, ₹2500/2, 4.7⭐, self-delivery

### Scenario 5: Chinese Cravings in Whitefield
- **Pincode**: 560066
- **Cuisine**: Chinese
- **Expected**: Dragon Wok
- **Features**: Asian fusion, ₹900/2, 4.4⭐

### Scenario 6: Multi-cuisine in HSR Layout
- **Pincode**: 560102
- **Cuisines**: Indian, Chinese, Italian
- **Expected**: Fusion Kitchen
- **Features**: Variety, ₹800/2, 4.2⭐

## Viewing Data in MongoDB

```bash
mongosh foodai_restaurant_dev
```

**List all restaurants:**
```javascript
db.restaurants.find({}, {name: 1, cuisineTypes: 1, averageCostForTwo: 1})
```

**Find by pincode:**
```javascript
db.restaurants.find({
  "outlets.serviceabilityConfig.servicePincodes": "560095"
})
```

**Find vegetarian:**
```javascript
db.restaurants.find({vegetarianOnly: true})
```

**Find by price range:**
```javascript
db.restaurants.find({
  averageCostForTwo: {$gte: 500, $lte: 1000}
})
```

**Find by rating:**
```javascript
db.restaurants.find({
  averageRating: {$gte: 4.5}
})
```

## Swagger UI Testing

1. Open: http://localhost:8081/swagger-ui.html
2. Navigate to: **Restaurant Management** → **POST /api/v1/restaurants/search**
3. Click "Try it out"
4. Enter request body (example):

```json
{
  "deliveryPincode": "560095",
  "cuisineTypes": ["Italian"],
  "minRating": 4.0,
  "page": 0,
  "size": 10
}
```

5. Click "Execute"

## Cleaning Up

**Remove all mock data:**
```bash
mongosh foodai_restaurant_dev --eval "
  db.restaurants.deleteMany({});
  print('Mock data cleared!');
"
```

**Reload mock data:**
```bash
./scripts/load-mock-data.sh
```

## Files

- **`scripts/insert-mock-data.js`** - MongoDB insertion script
- **`scripts/load-mock-data.sh`** - Shell wrapper for easy execution
- **`scripts/README.md`** - Detailed documentation

## Troubleshooting

### Issue: Index error "cannot index parallel arrays"

**Solution:**
```bash
mongosh foodai_restaurant_dev --eval "
  db.restaurants.dropIndex('cuisine_city_idx');
  db.restaurants.dropIndex('name_city_idx');
  db.restaurants.dropIndex('outlet_pincode_idx');
  db.restaurants.dropIndex('outlet_status_idx');
"
```

Then reload the data.

### Issue: MongoDB not running

**Solution:**
```bash
brew services start mongodb-community@7.0
```

### Issue: Application fails to start

**Solution:**
```bash
source ./scripts/SET_JAVA_21.sh
mvn spring-boot:run
```

## Next Steps

1. ✅ Load mock data
2. ✅ Start application
3. ✅ Test search API
4. 📝 Add more test scenarios
5. 🔍 Test with real coordinates
6. 📊 Monitor performance

## Support

- Main Documentation: [README.md](README.md)
- Search API Details: [SEARCH_API_DOCUMENTATION.md](SEARCH_API_DOCUMENTATION.md)
- Scripts Documentation: [scripts/README.md](scripts/README.md)

