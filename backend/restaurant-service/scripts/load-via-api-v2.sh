#!/bin/bash

# Load Mock Data via REST API (Correct DTO Fields)

API_BASE="http://localhost:8081/api/v1/restaurants"

echo "========================================="
echo "Loading Mock Data via REST API"
echo "========================================="
echo ""

# Check if application is running
if ! curl -s "$API_BASE?page=0&size=1" > /dev/null 2>&1; then
    echo "❌ Error: Application is not running on port 8081"
    echo "   Please start: ./mvnw.sh run dev"
    exit 1
fi

echo "✅ Application is running"
echo ""

# Restaurant 1: Bella Italia
echo "Creating: Bella Italia..."
RESPONSE=$(curl -s -X POST "$API_BASE" \
  -H "Content-Type: application/json" \
  -d '{
  "name": "Bella Italia",
  "description": "Authentic Italian cuisine with handmade pasta and wood-fired pizzas",
  "cuisineTypes": ["Italian", "Mediterranean"],
  "owners": [{
    "ownerName": "Mario Rossi",
    "ownerEmail": "mario@bellaitalia.com",
    "ownerPhone": "9876543210",
    "ownershipPercentage": 100.0,
    "role": "PRIMARY_OWNER",
    "isPrimaryContact": true
  }],
  "contacts": [{
    "contactType": "PRIMARY",
    "name": "Restaurant Manager",
    "phone": "8012345678",
    "email": "contact@bellaitalia.com",
    "designation": "Manager",
    "isActive": true
  }],
  "documents": [{
    "type": "FSSAI",
    "url": "https://example.com/docs/bella-italia-fssai.pdf",
    "remarks": "Valid until 2025-01-15"
  }],
  "contract": {
    "platformFee": {
      "feeType": "PERCENTAGE",
      "percentageRate": 20.0,
      "minFeePerOrder": 10.0,
      "maxFeePerOrder": 100.0
    },
    "deliveryFee": {
      "feeType": "FIXED",
      "payor": "CUSTOMER",
      "fixedAmountPerOrder": 40.0
    },
    "paymentGatewayFee": {
      "feeType": "PERCENTAGE",
      "payor": "RESTAURANT",
      "percentageRate": 2.0
    }
  },
  "createdBy": "admin@foodai.com"
}')

if echo "$RESPONSE" | jq -e '.success' > /dev/null 2>&1; then
  echo "$RESPONSE" | jq -r '"  ✅ Created: \(.data.name) (ID: \(.data.id))"'
else
  echo "  ❌ Failed to create restaurant"
  echo "$RESPONSE" | jq '.' || echo "$RESPONSE"
fi
echo ""

# Restaurant 2: Sattvik Bhavan  
echo "Creating: Sattvik Bhavan..."
RESPONSE=$(curl -s -X POST "$API_BASE" \
  -H "Content-Type: application/json" \
  -d '{
  "name": "Sattvik Bhavan",
  "description": "Pure vegetarian Indian cuisine with traditional flavors",
  "cuisineTypes": ["Indian", "North Indian", "South Indian"],
  "owners": [{
    "ownerName": "Rajesh Kumar",
    "ownerEmail": "rajesh@sattvikbhavan.com",
    "ownerPhone": "9876543211",
    "ownershipPercentage": 100.0,
    "role": "PRIMARY_OWNER",
    "isPrimaryContact": true
  }],
  "contacts": [{
    "contactType": "PRIMARY",
    "name": "Restaurant Manager",
    "phone": "8012345680",
    "email": "contact@sattvikbhavan.com",
    "designation": "Manager",
    "isActive": true
  }],
  "documents": [{
    "type": "FSSAI",
    "url": "https://example.com/docs/sattvik-bhavan-fssai.pdf",
    "remarks": "Valid until 2025-02-01"
  }],
  "contract": {
    "platformFee": {
      "feeType": "PERCENTAGE",
      "percentageRate": 18.0,
      "minFeePerOrder": 8.0,
      "maxFeePerOrder": 80.0
    },
    "deliveryFee": {
      "feeType": "FIXED",
      "payor": "CUSTOMER",
      "fixedAmountPerOrder": 30.0
    },
    "paymentGatewayFee": {
      "feeType": "PERCENTAGE",
      "payor": "RESTAURANT",
      "percentageRate": 2.0
    }
  },
  "createdBy": "admin@foodai.com"
}')

if echo "$RESPONSE" | jq -e '.success' > /dev/null 2>&1; then
  echo "$RESPONSE" | jq -r '"  ✅ Created: \(.data.name) (ID: \(.data.id))"'
else
  echo "  ❌ Failed to create restaurant"
  echo "$RESPONSE" | jq '.' || echo "$RESPONSE"
fi
echo ""

# Restaurant 3: Dragon Wok
echo "Creating: Dragon Wok..."
RESPONSE=$(curl -s -X POST "$API_BASE" \
  -H "Content-Type: application/json" \
  -d '{
  "name": "Dragon Wok",
  "description": "Authentic Chinese and Asian fusion cuisine",
  "cuisineTypes": ["Chinese", "Asian", "Thai"],
  "owners": [{
    "ownerName": "Wei Chen",
    "ownerEmail": "wei@dragonwok.com",
    "ownerPhone": "9876543212",
    "ownershipPercentage": 100.0,
    "role": "PRIMARY_OWNER",
    "isPrimaryContact": true
  }],
  "contacts": [{
    "contactType": "PRIMARY",
    "name": "Restaurant Manager",
    "phone": "8012345681",
    "email": "contact@dragonwok.com",
    "designation": "Manager",
    "isActive": true
  }],
  "documents": [{
    "type": "FSSAI",
    "url": "https://example.com/docs/dragon-wok-fssai.pdf",
    "remarks": "Valid until 2025-03-01"
  }],
  "contract": {
    "platformFee": {
      "feeType": "PERCENTAGE",
      "percentageRate": 22.0,
      "minFeePerOrder": 12.0,
      "maxFeePerOrder": 120.0
    },
    "deliveryFee": {
      "feeType": "FIXED",
      "payor": "CUSTOMER",
      "fixedAmountPerOrder": 50.0
    },
    "paymentGatewayFee": {
      "feeType": "PERCENTAGE",
      "payor": "RESTAURANT",
      "percentageRate": 2.0
    }
  },
  "createdBy": "admin@foodai.com"
}')

if echo "$RESPONSE" | jq -e '.success' > /dev/null 2>&1; then
  echo "$RESPONSE" | jq -r '"  ✅ Created: \(.data.name) (ID: \(.data.id))"'
else
  echo "  ❌ Failed to create restaurant"
  echo "$RESPONSE" | jq '.' || echo "$RESPONSE"
fi
echo ""

echo "========================================="
echo "Mock Data Loading Complete!"
echo "========================================="
echo ""
echo "Testing: GET /api/v1/restaurants"
GET_RESPONSE=$(curl -s "$API_BASE?page=0&size=10")

if echo "$GET_RESPONSE" | jq -e '.success' > /dev/null 2>&1; then
  TOTAL=$(echo "$GET_RESPONSE" | jq -r '.data.totalElements')
  echo "✅ SUCCESS! Found $TOTAL restaurants:"
  echo "$GET_RESPONSE" | jq -r '.data.content[] | "  • \(.name) (\(.cuisineTypes | join(", ")))"'
else
  echo "❌ Failed to fetch restaurants"
  echo "$GET_RESPONSE" | jq '.' || echo "$GET_RESPONSE"
fi

echo ""
echo "You can now test with:"
echo "  curl 'http://localhost:8081/api/v1/restaurants?page=0&size=10' | jq '.content[].name'"
echo ""

