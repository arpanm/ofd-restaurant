// MongoDB Script to Insert Mock Restaurant Data
// This script inserts data matching the exact structure created by the REST API
// Usage: mongosh foodai_restaurant_dev < scripts/insert-mock-data.js
//
// RECOMMENDED: Use API-based loading instead (./scripts/load-mock-data.sh)
// This ensures all validation and business logic is applied.

// Clear existing data (optional - comment out if you want to keep existing data)
db.restaurants.deleteMany({});

print("Inserting mock restaurant data...");
print("");

// Helper function to generate UUIDs (simplified)
function generateUUID() {
    return UUID().toString().replace(/UUID\("(.*)"\)/, '$1');
}

// Helper function to get current timestamp
function getTimestamp() {
    return new Date();
}

// Restaurant 1: Bella Italia
print("Creating: Bella Italia...");
const bellaItalia = {
    _id: generateUUID(),
    name: "Bella Italia",
    description: "Authentic Italian cuisine with handmade pasta and wood-fired pizzas",
    logo: null,
    coverImage: null,
    cuisineTypes: ["Italian", "Mediterranean"],
    owners: [
        {
            ownerId: null,
            ownerName: "Mario Rossi",
            ownerEmail: "mario@bellaitalia.com",
            ownerPhone: "9876543210",
            ownershipPercentage: 100.0,
            role: "PRIMARY_OWNER",
            isPrimaryContact: true,
            addedAt: null,
            addedBy: null
        }
    ],
    contacts: [
        {
            contactId: null,
            contactType: "PRIMARY",
            name: "Restaurant Manager",
            phone: "8012345678",
            alternatePhone: null,
            email: "contact@bellaitalia.com",
            designation: "Manager",
            isActive: true,
            addedAt: null,
            addedBy: null
        }
    ],
    documents: [
        {
            type: "FSSAI",
            url: "https://example.com/docs/bella-italia-fssai.pdf",
            verificationStatus: null,
            uploadedAt: null,
            remarks: "Valid until 2025-01-15"
        }
    ],
    contract: {
        contractId: generateUUID(),
        contractUrl: null,
        signed: false,
        signedAt: null,
        signedBy: null,
        platformFee: {
            feeType: "PERCENTAGE",
            percentageRate: 20.0,
            fixedAmountPerOrder: null,
            minFeePerOrder: 10.0,
            maxFeePerOrder: 100.0,
            feeSlabs: null
        },
        deliveryFee: {
            feeType: "FIXED",
            payor: "CUSTOMER",
            percentageRate: null,
            fixedAmountPerOrder: 40.0,
            restaurantSharePercentage: null,
            customerSharePercentage: null,
            minFeePerOrder: null,
            maxFeePerOrder: null,
            distanceSlabs: null
        },
        paymentGatewayFee: {
            feeType: "PERCENTAGE",
            payor: "RESTAURANT",
            percentageRate: 2.0,
            fixedAmount: null,
            minFee: null,
            maxFee: null
        },
        penalties: null,
        validFrom: getTimestamp(),
        validUntil: null,
        autoRenewal: null,
        createdBy: null,
        createdAt: getTimestamp(),
        updatedBy: null,
        updatedAt: getTimestamp()
    },
    onboardingType: "SELF_SERVICE",
    onboardedBy: null,
    outlets: [],
    averageRating: 0.0,
    totalReviews: 0,
    totalOrders: 0,
    acceptsOrders: true,
    status: "PENDING",
    createdBy: "admin@foodai.com",
    createdAt: getTimestamp(),
    updatedAt: getTimestamp(),
    deleted: false,
    _class: "com.foodai.restaurant.domain.model.Restaurant"
};

db.restaurants.insertOne(bellaItalia);
print("✓ Inserted: Bella Italia (ID: " + bellaItalia._id + ")");
print("");

// Restaurant 2: Sattvik Bhavan
print("Creating: Sattvik Bhavan...");
const sattvikBhavan = {
    _id: generateUUID(),
    name: "Sattvik Bhavan",
    description: "Pure vegetarian Indian cuisine with traditional flavors",
    logo: null,
    coverImage: null,
    cuisineTypes: ["Indian", "North Indian", "South Indian"],
    owners: [
        {
            ownerId: null,
            ownerName: "Rajesh Kumar",
            ownerEmail: "rajesh@sattvikbhavan.com",
            ownerPhone: "9876543211",
            ownershipPercentage: 100.0,
            role: "PRIMARY_OWNER",
            isPrimaryContact: true,
            addedAt: null,
            addedBy: null
        }
    ],
    contacts: [
        {
            contactId: null,
            contactType: "PRIMARY",
            name: "Restaurant Manager",
            phone: "8012345680",
            alternatePhone: null,
            email: "contact@sattvikbhavan.com",
            designation: "Manager",
            isActive: true,
            addedAt: null,
            addedBy: null
        }
    ],
    documents: [
        {
            type: "FSSAI",
            url: "https://example.com/docs/sattvik-bhavan-fssai.pdf",
            verificationStatus: null,
            uploadedAt: null,
            remarks: "Valid until 2025-02-01"
        }
    ],
    contract: {
        contractId: generateUUID(),
        contractUrl: null,
        signed: false,
        signedAt: null,
        signedBy: null,
        platformFee: {
            feeType: "PERCENTAGE",
            percentageRate: 18.0,
            fixedAmountPerOrder: null,
            minFeePerOrder: 8.0,
            maxFeePerOrder: 80.0,
            feeSlabs: null
        },
        deliveryFee: {
            feeType: "FIXED",
            payor: "CUSTOMER",
            percentageRate: null,
            fixedAmountPerOrder: 30.0,
            restaurantSharePercentage: null,
            customerSharePercentage: null,
            minFeePerOrder: null,
            maxFeePerOrder: null,
            distanceSlabs: null
        },
        paymentGatewayFee: {
            feeType: "PERCENTAGE",
            payor: "RESTAURANT",
            percentageRate: 2.0,
            fixedAmount: null,
            minFee: null,
            maxFee: null
        },
        penalties: null,
        validFrom: getTimestamp(),
        validUntil: null,
        autoRenewal: null,
        createdBy: null,
        createdAt: getTimestamp(),
        updatedBy: null,
        updatedAt: getTimestamp()
    },
    onboardingType: "SELF_SERVICE",
    onboardedBy: null,
    outlets: [],
    averageRating: 0.0,
    totalReviews: 0,
    totalOrders: 0,
    acceptsOrders: true,
    status: "PENDING",
    createdBy: "admin@foodai.com",
    createdAt: getTimestamp(),
    updatedAt: getTimestamp(),
    deleted: false,
    _class: "com.foodai.restaurant.domain.model.Restaurant"
};

db.restaurants.insertOne(sattvikBhavan);
print("✓ Inserted: Sattvik Bhavan (ID: " + sattvikBhavan._id + ")");
print("");

// Restaurant 3: Dragon Wok
print("Creating: Dragon Wok...");
const dragonWok = {
    _id: generateUUID(),
    name: "Dragon Wok",
    description: "Authentic Chinese and Asian fusion cuisine",
    logo: null,
    coverImage: null,
    cuisineTypes: ["Chinese", "Asian", "Thai"],
    owners: [
        {
            ownerId: null,
            ownerName: "Wei Chen",
            ownerEmail: "wei@dragonwok.com",
            ownerPhone: "9876543212",
            ownershipPercentage: 100.0,
            role: "PRIMARY_OWNER",
            isPrimaryContact: true,
            addedAt: null,
            addedBy: null
        }
    ],
    contacts: [
        {
            contactId: null,
            contactType: "PRIMARY",
            name: "Restaurant Manager",
            phone: "8012345681",
            alternatePhone: null,
            email: "contact@dragonwok.com",
            designation: "Manager",
            isActive: true,
            addedAt: null,
            addedBy: null
        }
    ],
    documents: [
        {
            type: "FSSAI",
            url: "https://example.com/docs/dragon-wok-fssai.pdf",
            verificationStatus: null,
            uploadedAt: null,
            remarks: "Valid until 2025-03-01"
        }
    ],
    contract: {
        contractId: generateUUID(),
        contractUrl: null,
        signed: false,
        signedAt: null,
        signedBy: null,
        platformFee: {
            feeType: "PERCENTAGE",
            percentageRate: 22.0,
            fixedAmountPerOrder: null,
            minFeePerOrder: 12.0,
            maxFeePerOrder: 120.0,
            feeSlabs: null
        },
        deliveryFee: {
            feeType: "FIXED",
            payor: "CUSTOMER",
            percentageRate: null,
            fixedAmountPerOrder: 50.0,
            restaurantSharePercentage: null,
            customerSharePercentage: null,
            minFeePerOrder: null,
            maxFeePerOrder: null,
            distanceSlabs: null
        },
        paymentGatewayFee: {
            feeType: "PERCENTAGE",
            payor: "RESTAURANT",
            percentageRate: 2.0,
            fixedAmount: null,
            minFee: null,
            maxFee: null
        },
        penalties: null,
        validFrom: getTimestamp(),
        validUntil: null,
        autoRenewal: null,
        createdBy: null,
        createdAt: getTimestamp(),
        updatedBy: null,
        updatedAt: getTimestamp()
    },
    onboardingType: "SELF_SERVICE",
    onboardedBy: null,
    outlets: [],
    averageRating: 0.0,
    totalReviews: 0,
    totalOrders: 0,
    acceptsOrders: true,
    status: "PENDING",
    createdBy: "admin@foodai.com",
    createdAt: getTimestamp(),
    updatedAt: getTimestamp(),
    deleted: false,
    _class: "com.foodai.restaurant.domain.model.Restaurant"
};

db.restaurants.insertOne(dragonWok);
print("✓ Inserted: Dragon Wok (ID: " + dragonWok._id + ")");
print("");

// Print summary
print("========================================");
print("Mock Data Insertion Complete!");
print("========================================");
print("");
print("Total Restaurants Inserted: " + db.restaurants.countDocuments());
print("");
print("Restaurant Summary:");
print("  • Bella Italia (Italian, Mediterranean)");
print("  • Sattvik Bhavan (Indian, North Indian, South Indian)");
print("  • Dragon Wok (Chinese, Asian, Thai)");
print("");
print("All restaurants have:");
print("  - Status: PENDING");
print("  - Average Rating: 0.0");
print("  - Total Orders: 0");
print("  - No outlets (can be added later)");
print("");
print("You can now:");
print("  1. Start the application: mvn spring-boot:run -Dspring-boot.run.profiles=dev");
print("  2. Test GET API: curl 'http://localhost:8081/api/v1/restaurants?page=0&size=10'");
print("  3. View Swagger UI: http://localhost:8081/swagger-ui.html");
print("");
print("========================================");
