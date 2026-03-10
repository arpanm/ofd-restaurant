package com.foodai.restaurant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodai.restaurant.domain.model.*;
import com.foodai.restaurant.domain.repository.RestaurantRepository;
import com.foodai.restaurant.dto.request.*;
import com.foodai.restaurant.dto.response.*;
import com.foodai.restaurant.service.RestaurantService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller tests for RestaurantController using MockMvc.
 * Uses real MongoDB connection with mocked Kafka.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Restaurant Controller Tests")
class RestaurantControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    @MockBean
    private RestaurantService restaurantService;
    
    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    private CreateRestaurantRequest createRequest;
    private RestaurantResponse restaurantResponse;
    
    @BeforeEach
    void setUp() {
        // Clean database before each test
        restaurantRepository.deleteAll();
        createRequest = CreateRestaurantRequest.builder()
            .name("Test Restaurant")
            .description("Test Description")
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
            .createdBy("user-1")
            .build();
        
        restaurantResponse = RestaurantResponse.builder()
            .id("restaurant-123")
            .name("Test Restaurant")
            .description("Test Description")
            .cuisineTypes(List.of("Italian", "Indian"))
            .status(RestaurantStatus.PENDING)
            .acceptsOrders(true)
            .build();
    }
    
    @AfterEach
    void tearDown() {
        // Clean up after each test
        restaurantRepository.deleteAll();
    }
    
    @Test
    @DisplayName("POST /api/v1/restaurants - Should create restaurant successfully")
    void shouldCreateRestaurantSuccessfully() throws Exception {
        // Given
        when(restaurantService.createRestaurant(any(CreateRestaurantRequest.class)))
            .thenReturn(restaurantResponse);
        
        // When & Then
        mockMvc.perform(post("/api/v1/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value("restaurant-123"))
            .andExpect(jsonPath("$.data.name").value("Test Restaurant"));
        
        verify(restaurantService).createRestaurant(any(CreateRestaurantRequest.class));
    }
    
    @Test
    @DisplayName("POST /api/v1/restaurants - Should return 400 when name is blank")
    void shouldReturn400WhenNameIsBlank() throws Exception {
        // Given
        createRequest.setName("");
        
        // When & Then
        mockMvc.perform(post("/api/v1/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isBadRequest());
        
        verify(restaurantService, never()).createRestaurant(any());
    }
    
    @Test
    @DisplayName("GET /api/v1/restaurants/{id} - Should return restaurant when exists")
    void shouldReturnRestaurantWhenExists() throws Exception {
        // Given
        when(restaurantService.getRestaurantById("restaurant-123"))
            .thenReturn(restaurantResponse);
        
        // When & Then
        mockMvc.perform(get("/api/v1/restaurants/restaurant-123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value("restaurant-123"))
            .andExpect(jsonPath("$.data.name").value("Test Restaurant"));
        
        verify(restaurantService).getRestaurantById("restaurant-123");
    }
    
    @Test
    @DisplayName("GET /api/v1/restaurants - Should return paginated restaurants")
    void shouldReturnPaginatedRestaurants() throws Exception {
        // Given
        Page<RestaurantResponse> page = new PageImpl<>(
            List.of(restaurantResponse),
            PageRequest.of(0, 20),
            1
        );
        when(restaurantService.getAllRestaurants(any()))
            .thenReturn(page);
        
        // When & Then
        mockMvc.perform(get("/api/v1/restaurants")
                .param("page", "0")
                .param("size", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content").isArray())
            .andExpect(jsonPath("$.data.content[0].id").value("restaurant-123"));
        
        verify(restaurantService).getAllRestaurants(any());
    }
    
    @Test
    @DisplayName("GET /api/v1/restaurants/search - Should return search results")
    void shouldReturnSearchResults() throws Exception {
        // Given
        Page<RestaurantResponse> page = new PageImpl<>(
            List.of(restaurantResponse),
            PageRequest.of(0, 20),
            1
        );
        when(restaurantService.searchRestaurants(anyString(), any()))
            .thenReturn(page);
        
        // When & Then
        mockMvc.perform(get("/api/v1/restaurants/search")
                .param("query", "Test"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content").isArray());
        
        verify(restaurantService).searchRestaurants(eq("Test"), any());
    }
    
    @Test
    @DisplayName("PUT /api/v1/restaurants/{id} - Should update restaurant successfully")
    void shouldUpdateRestaurantSuccessfully() throws Exception {
        // Given
        when(restaurantService.updateRestaurant(anyString(), any(CreateRestaurantRequest.class)))
            .thenReturn(restaurantResponse);
        
        // When & Then
        mockMvc.perform(put("/api/v1/restaurants/restaurant-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value("restaurant-123"));
        
        verify(restaurantService).updateRestaurant(eq("restaurant-123"), any(CreateRestaurantRequest.class));
    }
    
    @Test
    @DisplayName("DELETE /api/v1/restaurants/{id} - Should delete restaurant successfully")
    void shouldDeleteRestaurantSuccessfully() throws Exception {
        // Given
        doNothing().when(restaurantService).deleteRestaurant(anyString(), anyString());
        
        // When & Then
        mockMvc.perform(delete("/api/v1/restaurants/restaurant-123")
                .param("deletedBy", "admin-user"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
        
        verify(restaurantService).deleteRestaurant("restaurant-123", "admin-user");
    }
    
    @Test
    @DisplayName("GET /api/v1/restaurants/by-owner/{ownerId} - Should return owner's restaurants")
    void shouldReturnOwnerRestaurants() throws Exception {
        // Given
        when(restaurantService.getRestaurantsByOwner("owner-123"))
            .thenReturn(List.of(restaurantResponse));
        
        // When & Then
        mockMvc.perform(get("/api/v1/restaurants/by-owner/owner-123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray())
            .andExpect(jsonPath("$.data[0].id").value("restaurant-123"));
        
        verify(restaurantService).getRestaurantsByOwner("owner-123");
    }
    
    @Test
    @DisplayName("POST /api/v1/restaurants/{id}/approve - Should approve restaurant")
    void shouldApproveRestaurant() throws Exception {
        // Given
        RestaurantResponse approvedResponse = RestaurantResponse.builder()
            .id("restaurant-123")
            .name("Test Restaurant")
            .status(RestaurantStatus.APPROVED)
            .build();
        
        when(restaurantService.approveRestaurant("restaurant-123", "admin-user"))
            .thenReturn(approvedResponse);
        
        // When & Then
        mockMvc.perform(post("/api/v1/restaurants/restaurant-123/approve")
                .param("approvedBy", "admin-user"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.status").value("APPROVED"));
        
        verify(restaurantService).approveRestaurant("restaurant-123", "admin-user");
    }
}


