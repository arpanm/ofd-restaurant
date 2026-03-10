package com.foodai.restaurant.integration;

import com.foodai.restaurant.domain.model.*;
import com.foodai.restaurant.domain.repository.RestaurantRepository;
import com.foodai.restaurant.dto.request.*;
import com.foodai.restaurant.dto.response.ApiResponse;
import com.foodai.restaurant.dto.response.RestaurantResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Integration tests for Restaurant service using real MongoDB.
 * Uses the MongoDB instance configured in application-test.yml (localhost:27017).
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("Restaurant Integration Tests")
class RestaurantIntegrationTest {
    
    @LocalServerPort
    private int port;
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/v1/restaurants";
        
        // Clean database before each test
        restaurantRepository.deleteAll();
    }
    
    @AfterEach
    void tearDown() {
        // Clean up after each test
        restaurantRepository.deleteAll();
    }
    
    @Test
    @DisplayName("Should create, retrieve, update, and delete restaurant - Full CRUD flow")
    void shouldPerformFullCRUDFlow() {
        // Create restaurant request
        CreateRestaurantRequest request = CreateRestaurantRequest.builder()
            .name("Integration Test Restaurant")
            .description("Integration Test Description")
            .cuisineTypes(List.of("Italian", "Indian"))
            .owners(List.of(OwnerDTO.builder()
                .ownerName("John Doe")
                .ownerEmail("john@example.com")
                .ownerPhone("9876543210")
                .ownershipPercentage(100.0)
                .role(OwnerRole.PRIMARY_OWNER)
                .isPrimaryContact(true)
                .build()))
            .contacts(List.of(ContactDTO.builder()
                .contactType(ContactType.PRIMARY)
                .name("John Doe")
                .phone("9876543210")
                .email("john@example.com")
                .isActive(true)
                .build()))
            .documents(List.of(DocumentDTO.builder()
                .type(DocumentType.FSSAI)
                .url("https://example.com/fssai.pdf")
                .build()))
            .contract(ContractDTO.builder()
                .platformFee(PlatformFeeDTO.builder()
                    .feeType(FeeType.PERCENTAGE)
                    .percentageRate(15.0)
                    .build())
                .deliveryFee(DeliveryFeeDTO.builder()
                    .feeType(FeeType.FIXED)
                    .payor(DeliveryFeePayor.CUSTOMER)
                    .fixedAmountPerOrder(20.0)
                    .build())
                .paymentGatewayFee(PaymentGatewayFeeDTO.builder()
                    .feeType(FeeType.PERCENTAGE)
                    .payor(PaymentFeePayor.RESTAURANT)
                    .percentageRate(2.0)
                    .build())
                .build())
            .createdBy("test-user")
            .build();
        
        // 1. CREATE - Create a new restaurant
        String restaurantId = given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post()
        .then()
            .statusCode(201)
            .body("success", equalTo(true))
            .body("data.name", equalTo("Integration Test Restaurant"))
            .body("data.status", equalTo("PENDING"))
            .body("data.cuisineTypes", hasSize(2))
            .extract()
            .path("data.id");
        
        // 2. READ - Retrieve the created restaurant
        given()
        .when()
            .get("/" + restaurantId)
        .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("data.id", equalTo(restaurantId))
            .body("data.name", equalTo("Integration Test Restaurant"));
        
        // 3. UPDATE - Update the restaurant
        request.setDescription("Updated Description");
        
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .put("/" + restaurantId)
        .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("data.id", equalTo(restaurantId));
        
        // 4. DELETE - Soft delete the restaurant
        given()
            .queryParam("deletedBy", "test-user")
        .when()
            .delete("/" + restaurantId)
        .then()
            .statusCode(200)
            .body("success", equalTo(true));
    }
    
    @Test
    @DisplayName("Should return validation error when creating restaurant with invalid data")
    void shouldReturnValidationErrorWithInvalidData() {
        // Given - Invalid request (missing required fields)
        CreateRestaurantRequest invalidRequest = CreateRestaurantRequest.builder()
            .name("") // Empty name - should fail validation
            .build();
        
        // When & Then
        given()
            .contentType(ContentType.JSON)
            .body(invalidRequest)
        .when()
            .post()
        .then()
            .statusCode(400)
            .body("error", equalTo("Validation Failed"));
    }
    
    @Test
    @DisplayName("Should search restaurants by name")
    void shouldSearchRestaurantsByName() {
        // First create a restaurant
        CreateRestaurantRequest request = createValidRestaurantRequest("Searchable Restaurant");
        
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post()
        .then()
            .statusCode(201);
        
        // Now search for it
        given()
            .queryParam("query", "Searchable")
        .when()
            .get("/search")
        .then()
            .statusCode(200)
            .body("success", equalTo(true))
            .body("data.content", notNullValue());
    }
    
    @Test
    @DisplayName("Should return 404 when restaurant not found")
    void shouldReturn404WhenRestaurantNotFound() {
        given()
        .when()
            .get("/non-existent-id")
        .then()
            .statusCode(404)
            .body("error", equalTo("Not Found"));
    }
    
    @Test
    @DisplayName("Should handle ownership percentage validation")
    void shouldValidateOwnershipPercentage() {
        // Given - Ownership percentages don't sum to 100%
        CreateRestaurantRequest request = createValidRestaurantRequest("Ownership Test");
        request.getOwners().get(0).setOwnershipPercentage(60.0); // Not 100%
        
        // When & Then
        given()
            .contentType(ContentType.JSON)
            .body(request)
        .when()
            .post()
        .then()
            .statusCode(500) // Should throw IllegalArgumentException
            .body("error", equalTo("Internal Server Error"));
    }
    
    /**
     * Helper method to create a valid restaurant request.
     */
    private CreateRestaurantRequest createValidRestaurantRequest(String name) {
        return CreateRestaurantRequest.builder()
            .name(name)
            .description("Test Description")
            .cuisineTypes(List.of("Italian"))
            .owners(List.of(OwnerDTO.builder()
                .ownerName("Owner")
                .ownerEmail("owner@example.com")
                .ownerPhone("9876543210")
                .ownershipPercentage(100.0)
                .role(OwnerRole.PRIMARY_OWNER)
                .isPrimaryContact(true)
                .build()))
            .contacts(List.of(ContactDTO.builder()
                .contactType(ContactType.PRIMARY)
                .name("Contact")
                .phone("9876543210")
                .email("contact@example.com")
                .isActive(true)
                .build()))
            .documents(List.of(DocumentDTO.builder()
                .type(DocumentType.FSSAI)
                .url("https://example.com/doc.pdf")
                .build()))
            .contract(ContractDTO.builder()
                .platformFee(PlatformFeeDTO.builder()
                    .feeType(FeeType.PERCENTAGE)
                    .percentageRate(15.0)
                    .build())
                .deliveryFee(DeliveryFeeDTO.builder()
                    .feeType(FeeType.FIXED)
                    .payor(DeliveryFeePayor.CUSTOMER)
                    .fixedAmountPerOrder(20.0)
                    .build())
                .paymentGatewayFee(PaymentGatewayFeeDTO.builder()
                    .feeType(FeeType.PERCENTAGE)
                    .payor(PaymentFeePayor.RESTAURANT)
                    .percentageRate(2.0)
                    .build())
                .build())
            .createdBy("test-user")
            .build();
    }
}
