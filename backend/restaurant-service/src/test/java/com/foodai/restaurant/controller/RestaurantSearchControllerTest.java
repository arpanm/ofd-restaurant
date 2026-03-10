package com.foodai.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodai.restaurant.domain.model.BudgetType;
import com.foodai.restaurant.domain.model.Restaurant;
import com.foodai.restaurant.domain.model.RestaurantStatus;
import com.foodai.restaurant.domain.repository.RestaurantRepository;
import com.foodai.restaurant.dto.request.SearchRestaurantRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for restaurant search endpoint.
 * Uses real MongoDB connection for end-to-end testing.
 * Kafka is mocked to avoid requiring a Kafka broker.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Restaurant Search Controller Integration Tests")
class RestaurantSearchControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @BeforeEach
    void setUp() {
        // Clean database before each test
        restaurantRepository.deleteAll();
        
        // Create test data
        Restaurant restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .description("A test restaurant")
                .cuisineTypes(List.of("Italian", "Chinese"))
                .averageCostForTwo(800.0)
                .averageRating(4.5)
                .totalReviews(100)
                .status(RestaurantStatus.APPROVED)
                .acceptsOrders(true)
                .vegetarianOnly(false)
                .build();
        
        restaurantRepository.save(restaurant);
    }
    
    @AfterEach
    void tearDown() {
        // Clean up after each test
        restaurantRepository.deleteAll();
    }
    
    @Test
    @DisplayName("Should search restaurants with valid request")
    void shouldSearchRestaurantsWithValidRequest() throws Exception {
        // Given
        SearchRestaurantRequest request = SearchRestaurantRequest.builder()
                .deliveryPincode("560001")
                .cuisineTypes(List.of("Italian"))
                .budgetType(BudgetType.MID_RANGE)
                .minRating(4.0)
                .build();
        
        // When & Then
        mockMvc.perform(post("/api/v1/restaurants/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }
    
    @Test
    @DisplayName("Should return 400 for invalid pincode")
    void shouldReturn400ForInvalidPincode() throws Exception {
        // Given
        SearchRestaurantRequest request = SearchRestaurantRequest.builder()
                .deliveryPincode("12345") // Invalid: only 5 digits
                .build();
        
        // When & Then
        mockMvc.perform(post("/api/v1/restaurants/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should return 400 for missing pincode")
    void shouldReturn400ForMissingPincode() throws Exception {
        // Given
        SearchRestaurantRequest request = SearchRestaurantRequest.builder()
                .cuisineTypes(List.of("Italian"))
                .build();
        
        // When & Then
        mockMvc.perform(post("/api/v1/restaurants/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should search with all filters")
    void shouldSearchWithAllFilters() throws Exception {
        // Given
        SearchRestaurantRequest request = SearchRestaurantRequest.builder()
                .deliveryPincode("560001")
                .cuisineTypes(List.of("Italian", "Chinese"))
                .budgetType(BudgetType.MID_RANGE)
                .minRating(4.0)
                .maxRating(5.0)
                .searchTerm("Test")
                .minPrice(300.0)
                .maxPrice(1000.0)
                .sortBy("rating")
                .sortDirection("desc")
                .page(0)
                .size(20)
                .returnOutlets(false)
                .build();
        
        // When & Then
        mockMvc.perform(post("/api/v1/restaurants/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    @DisplayName("Should search outlets when returnOutlets is true")
    void shouldSearchOutlets() throws Exception {
        // Given
        SearchRestaurantRequest request = SearchRestaurantRequest.builder()
                .deliveryPincode("560001")
                .returnOutlets(true)
                .build();
        
        // When & Then
        mockMvc.perform(post("/api/v1/restaurants/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
    
    @Test
    @DisplayName("Should return 400 for invalid rating range")
    void shouldReturn400ForInvalidRatingRange() throws Exception {
        // Given
        SearchRestaurantRequest request = SearchRestaurantRequest.builder()
                .deliveryPincode("560001")
                .minRating(6.0) // Invalid: > 5.0
                .build();
        
        // When & Then
        mockMvc.perform(post("/api/v1/restaurants/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @DisplayName("Should return 400 for invalid page size")
    void shouldReturn400ForInvalidPageSize() throws Exception {
        // Given
        SearchRestaurantRequest request = SearchRestaurantRequest.builder()
                .deliveryPincode("560001")
                .size(150) // Invalid: > 100
                .build();
        
        // When & Then
        mockMvc.perform(post("/api/v1/restaurants/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

