# Restaurant Search API Documentation

## Overview

The Restaurant Search API provides advanced search capabilities with multiple filters to help users find restaurants or individual outlets based on their preferences and location.

## Endpoint

```
POST /api/v1/restaurants/search
Content-Type: application/json
```

## Features

- **Mandatory Delivery Pincode**: Always required for serviceability check
- **Multiple Filters**: Cuisine, budget, ratings, price range, search term, etc.
- **Flexible Response**: Can return either restaurants (grouped) or individual outlets
- **Pagination**: Supports paging for large result sets
- **Sorting**: Multiple sort options (rating, price, distance, popularity)
- **Real-time**: Filters for currently open restaurants

## Request Body

### SearchRestaurantRequest

```json
{
  "deliveryPincode": "560001",        // REQUIRED: 6-digit pincode
  "cuisineTypes": ["Italian", "Chinese"],  // Optional: List of cuisines
  "budgetType": "MID_RANGE",          // Optional: BUDGET, AFFORDABLE, MID_RANGE, PREMIUM, LUXURY
  "minRating": 4.0,                   // Optional: 0.0 to 5.0
  "maxRating": 5.0,                   // Optional: 0.0 to 5.0
  "searchTerm": "pizza",              // Optional: Search in names
  "returnOutlets": false,              // Optional: true for outlets, false for restaurants
  "vegetarianOnly": true,             // Optional: Filter veg restaurants
  "currentlyOpen": true,              // Optional: Filter open restaurants
  "minPrice": 300.0,                  // Optional: Min cost for two
  "maxPrice": 1000.0,                 // Optional: Max cost for two
  "sortBy": "rating",                 // Optional: rating, price, distance, popularity
  "sortDirection": "desc",            // Optional: asc, desc
  "page": 0,                          // Optional: Page number (default: 0)
  "size": 20                          // Optional: Page size (default: 20, max: 100)
}
```

### Budget Types

| Budget Type | Price Range (for two) |
|-------------|----------------------|
| BUDGET      | Under ₹300          |
| AFFORDABLE  | ₹300 - ₹600         |
| MID_RANGE   | ₹600 - ₹1200        |
| PREMIUM     | ₹1200 - ₹2000       |
| LUXURY      | Above ₹2000         |

### Sort Options

- **rating**: Sort by average rating
- **price**: Sort by cost for two
- **distance**: Sort by proximity (approximate)
- **popularity**: Sort by total orders

## Response Format

### When returnOutlets = false (Default)

Returns aggregated restaurant information:

```json
{
  "success": true,
  "message": "Found 5 restaurants",
  "data": {
    "content": [
      {
        "restaurantId": "rest-123",
        "name": "Italian Delight",
        "description": "Authentic Italian cuisine",
        "cuisineTypes": ["Italian", "Continental"],
        "averageCostForTwo": 800.0,
        "averageRating": 4.5,
        "totalReviews": 150,
        "status": "APPROVED",
        "acceptsOrders": true,
        "outletCount": 3,
        "nearestOutletDistanceKm": 2.5,
        "estimatedDeliveryMinutes": 35,
        "deliveryFee": 20.0,
        "anyOutletOpen": true,
        "vegetarianOnly": false,
        "minimumOrderValue": 100.0,
        "imageUrl": "https://example.com/restaurant.jpg",
        "tags": ["Free Delivery", "Popular"],
        "nearbyOutlets": [
          {
            "outletId": "outlet-456",
            "outletName": "MG Road Branch",
            "area": "MG Road",
            "city": "Bangalore",
            "distanceKm": 2.5,
            "currentlyOpen": true
          }
        ]
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20
    },
    "totalElements": 5,
    "totalPages": 1,
    "last": true,
    "first": true
  }
}
```

### When returnOutlets = true

Returns individual outlet information:

```json
{
  "success": true,
  "message": "Found 8 outlets",
  "data": {
    "content": [
      {
        "outletId": "outlet-456",
        "outletName": "MG Road Branch",
        "restaurantId": "rest-123",
        "restaurantName": "Italian Delight",
        "description": "Authentic Italian cuisine",
        "cuisineTypes": ["Italian", "Continental"],
        "averageCostForTwo": 800.0,
        "averageRating": 4.5,
        "totalReviews": 75,
        "status": "APPROVED",
        "acceptsOrders": true,
        "address": {
          "street": "123 MG Road",
          "area": "MG Road",
          "city": "Bangalore",
          "state": "Karnataka",
          "pincode": "560001",
          "landmark": "Near Metro Station",
          "latitude": 12.9716,
          "longitude": 77.5946
        },
        "distanceKm": 2.5,
        "estimatedDeliveryMinutes": 35,
        "deliveryFee": 20.0,
        "currentlyOpen": true,
        "todayTimings": "10:00 - 23:00",
        "vegetarianOnly": false,
        "minimumOrderValue": 100.0,
        "imageUrl": "https://example.com/outlet.jpg",
        "tags": ["Free Delivery"]
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20
    },
    "totalElements": 8,
    "totalPages": 1
  }
}
```

## Example Requests

### 1. Basic Search by Pincode

```bash
curl -X POST http://localhost:8080/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{
    "deliveryPincode": "560001"
  }'
```

### 2. Search with Cuisine and Rating Filters

```bash
curl -X POST http://localhost:8080/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{
    "deliveryPincode": "560001",
    "cuisineTypes": ["Italian", "Chinese"],
    "minRating": 4.0
  }'
```

### 3. Budget-based Search

```bash
curl -X POST http://localhost:8080/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{
    "deliveryPincode": "560001",
    "budgetType": "MID_RANGE",
    "sortBy": "price",
    "sortDirection": "asc"
  }'
```

### 4. Search for Currently Open Restaurants

```bash
curl -X POST http://localhost:8080/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{
    "deliveryPincode": "560001",
    "currentlyOpen": true,
    "sortBy": "rating",
    "sortDirection": "desc"
  }'
```

### 5. Search for Vegetarian Outlets

```bash
curl -X POST http://localhost:8080/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{
    "deliveryPincode": "560001",
    "vegetarianOnly": true,
    "returnOutlets": true,
    "minPrice": 200,
    "maxPrice": 800
  }'
```

### 6. Full-featured Search

```bash
curl -X POST http://localhost:8080/api/v1/restaurants/search \
  -H "Content-Type: application/json" \
  -d '{
    "deliveryPincode": "560001",
    "cuisineTypes": ["Italian", "Chinese"],
    "budgetType": "MID_RANGE",
    "minRating": 4.0,
    "maxRating": 5.0,
    "searchTerm": "pizza",
    "vegetarianOnly": false,
    "currentlyOpen": true,
    "minPrice": 500,
    "maxPrice": 1200,
    "sortBy": "rating",
    "sortDirection": "desc",
    "page": 0,
    "size": 10,
    "returnOutlets": false
  }'
```

## Validation Rules

### Field Validations

| Field | Type | Required | Validation |
|-------|------|----------|------------|
| deliveryPincode | String | Yes | Must be 6-digit valid pincode |
| cuisineTypes | List | No | - |
| budgetType | Enum | No | BUDGET, AFFORDABLE, MID_RANGE, PREMIUM, LUXURY |
| minRating | Double | No | 0.0 to 5.0 |
| maxRating | Double | No | 0.0 to 5.0 |
| searchTerm | String | No | - |
| returnOutlets | Boolean | No | Default: false |
| vegetarianOnly | Boolean | No | - |
| currentlyOpen | Boolean | No | - |
| minPrice | Double | No | Must be non-negative |
| maxPrice | Double | No | Must be non-negative |
| sortBy | String | No | rating, price, distance, popularity |
| sortDirection | String | No | asc, desc |
| page | Integer | No | >= 0 |
| size | Integer | No | 1 to 100 |

### Cross-field Validations

- `minRating` must be <= `maxRating`
- `minPrice` must be <= `maxPrice`

## Error Responses

### 400 Bad Request - Invalid Pincode

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": [
    {
      "field": "deliveryPincode",
      "message": "Pincode must be a valid 6-digit number"
    }
  ]
}
```

### 400 Bad Request - Missing Required Field

```json
{
  "success": false,
  "message": "Validation failed",
  "errors": [
    {
      "field": "deliveryPincode",
      "message": "Delivery pincode is required"
    }
  ]
}
```

### 400 Bad Request - Invalid Rating Range

```json
{
  "success": false,
  "message": "Invalid rating range: minRating must be <= maxRating"
}
```

## Performance Considerations

1. **Indexing**: The following fields are indexed for optimal search performance:
   - `outlets.serviceabilityConfig.serviceablePincodes`
   - `cuisineTypes`
   - `averageRating`
   - `status`

2. **Pagination**: Always use pagination for production environments to avoid loading large datasets.

3. **Caching**: Search results are cached based on the request parameters. Cache TTL: 5 minutes.

## Implementation Details

### Key Classes

- **SearchRestaurantRequest**: DTO for search criteria
- **RestaurantSearchResponse**: DTO for restaurant search results
- **OutletSearchResponse**: DTO for outlet search results
- **BudgetType**: Enum for budget categories
- **RestaurantSearchRepository**: Custom repository for dynamic queries
- **SearchMapper**: Maps domain models to response DTOs

### Technologies Used

- Spring Data MongoDB for flexible querying
- Dynamic query building with Criteria API
- Pagination support with Spring Data
- Jakarta Bean Validation for request validation

## Testing

Comprehensive test coverage includes:

- Unit tests for request validation
- Service layer tests with mocked dependencies
- Repository tests (require Docker for Testcontainers)
- Mapper tests for DTO conversions
- Controller integration tests

Run tests:
```bash
mvn test
```

## Future Enhancements

1. Geo-spatial search using actual coordinates
2. Advanced filtering (dietary preferences, facilities)
3. Personalized search based on user history
4. Search analytics and recommendations
5. Elasticsearch integration for full-text search

