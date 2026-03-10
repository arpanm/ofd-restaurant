package com.foodai.restaurant.service;

import com.foodai.restaurant.domain.model.*;
import com.foodai.restaurant.domain.repository.RestaurantRepository;
import com.foodai.restaurant.dto.request.*;
import com.foodai.restaurant.dto.response.RestaurantResponse;
import com.foodai.restaurant.exception.RestaurantAlreadyExistsException;
import com.foodai.restaurant.exception.RestaurantNotFoundException;
import com.foodai.restaurant.mapper.RestaurantMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RestaurantService.
 * Tests business logic with mocked dependencies.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Restaurant Service Tests")
class RestaurantServiceTest {
    
    @Mock
    private RestaurantRepository restaurantRepository;
    
    @Mock
    private RestaurantMapper restaurantMapper;
    
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @InjectMocks
    private RestaurantService restaurantService;
    
    private CreateRestaurantRequest createRequest;
    private Restaurant restaurant;
    private RestaurantResponse restaurantResponse;
    
    @BeforeEach
    void setUp() {
        // Setup test data
        createRequest = CreateRestaurantRequest.builder()
            .name("Test Restaurant")
            .description("Test Description")
            .cuisineTypes(List.of("Italian", "Indian"))
            .owners(List.of(
                OwnerDTO.builder()
                    .ownerName("John Doe")
                    .ownerEmail("john@example.com")
                    .ownerPhone("9876543210")
                    .ownershipPercentage(100.0)
                    .role(OwnerRole.PRIMARY_OWNER)
                    .isPrimaryContact(true)
                    .build()
            ))
            .contacts(List.of(
                ContactDTO.builder()
                    .contactType(ContactType.PRIMARY)
                    .name("John Doe")
                    .phone("9876543210")
                    .email("john@example.com")
                    .isActive(true)
                    .build()
            ))
            .documents(new ArrayList<>())
            .contract(ContractDTO.builder()
                .platformFee(PlatformFeeDTO.builder()
                    .feeType(FeeType.PERCENTAGE)
                    .percentageRate(15.0)
                    .build())
                .deliveryFee(DeliveryFeeDTO.builder()
                    .feeType(FeeType.FIXED)
                    .fixedAmountPerOrder(20.0)
                    .payor(DeliveryFeePayor.CUSTOMER)
                    .build())
                .paymentGatewayFee(PaymentGatewayFeeDTO.builder()
                    .feeType(FeeType.PERCENTAGE)
                    .percentageRate(2.0)
                    .payor(PaymentFeePayor.RESTAURANT)
                    .build())
                .build())
            .createdBy("test-user")
            .build();
        
        restaurant = Restaurant.builder()
            .id("restaurant-123")
            .name("Test Restaurant")
            .description("Test Description")
            .cuisineTypes(List.of("Italian", "Indian"))
            .status(RestaurantStatus.PENDING)
            .acceptsOrders(true)
            .owners(List.of(
                OwnerVO.builder()
                    .ownerName("John Doe")
                    .ownerEmail("john@example.com")
                    .ownerPhone("9876543210")
                    .ownershipPercentage(100.0)
                    .role(OwnerRole.PRIMARY_OWNER)
                    .isPrimaryContact(true)
                    .build()
            ))
            .contacts(List.of(
                ContactVO.builder()
                    .contactType(ContactType.PRIMARY)
                    .name("John Doe")
                    .phone("9876543210")
                    .email("john@example.com")
                    .isActive(true)
                    .build()
            ))
            .documents(new ArrayList<>())
            .outlets(new ArrayList<>())
            .deleted(false)
            .createdAt(Instant.now())
            .build();
        
        restaurantResponse = RestaurantResponse.builder()
            .id("restaurant-123")
            .name("Test Restaurant")
            .description("Test Description")
            .cuisineTypes(List.of("Italian", "Indian"))
            .status(RestaurantStatus.PENDING)
            .build();
    }
    
    @Test
    @DisplayName("Should create restaurant successfully when valid input provided")
    void shouldCreateRestaurantSuccessfully_whenValidInput() {
        // Given
        when(restaurantRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(restaurantMapper.toEntity(any())).thenReturn(restaurant);
        when(restaurantRepository.save(any())).thenReturn(restaurant);
        when(restaurantMapper.toResponse(any())).thenReturn(restaurantResponse);
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(CompletableFuture.completedFuture(null));
        
        // When
        RestaurantResponse response = restaurantService.createRestaurant(createRequest);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Test Restaurant");
        
        verify(restaurantRepository).findByNameIgnoreCase("Test Restaurant");
        verify(restaurantRepository).save(any(Restaurant.class));
        verify(kafkaTemplate).send(eq("restaurant.registered"), anyString(), any());
    }
    
    @Test
    @DisplayName("Should throw exception when restaurant with same name already exists")
    void shouldThrowException_whenRestaurantAlreadyExists() {
        // Given
        when(restaurantRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.of(restaurant));
        
        // When & Then
        assertThatThrownBy(() -> restaurantService.createRestaurant(createRequest))
            .isInstanceOf(RestaurantAlreadyExistsException.class)
            .hasMessageContaining("Test Restaurant");
        
        verify(restaurantRepository, never()).save(any());
    }
    
    @Test
    @DisplayName("Should return restaurant when exists")
    void shouldReturnRestaurant_whenExists() {
        // Given
        when(restaurantRepository.findByIdAndDeletedFalse("restaurant-123"))
            .thenReturn(Optional.of(restaurant));
        when(restaurantMapper.toResponse(any())).thenReturn(restaurantResponse);
        
        // When
        RestaurantResponse response = restaurantService.getRestaurantById("restaurant-123");
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("restaurant-123");
        
        verify(restaurantRepository).findByIdAndDeletedFalse("restaurant-123");
    }
    
    @Test
    @DisplayName("Should throw exception when restaurant not found")
    void shouldThrowException_whenRestaurantNotFound() {
        // Given
        when(restaurantRepository.findByIdAndDeletedFalse("non-existent"))
            .thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> restaurantService.getRestaurantById("non-existent"))
            .isInstanceOf(RestaurantNotFoundException.class)
            .hasMessageContaining("non-existent");
    }
    
    @Test
    @DisplayName("Should soft delete restaurant successfully")
    void shouldSoftDeleteRestaurant_whenExists() {
        // Given
        when(restaurantRepository.findByIdAndDeletedFalse("restaurant-123"))
            .thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any())).thenReturn(restaurant);
        
        // When
        restaurantService.deleteRestaurant("restaurant-123", "admin-user");
        
        // Then
        verify(restaurantRepository).findByIdAndDeletedFalse("restaurant-123");
        verify(restaurantRepository).save(any(Restaurant.class));
        
        assertThat(restaurant.isDeleted()).isTrue();
        assertThat(restaurant.getDeletedBy()).isEqualTo("admin-user");
        assertThat(restaurant.getDeletedAt()).isNotNull();
    }
    
    @Test
    @DisplayName("Should approve restaurant successfully")
    void shouldApproveRestaurant_whenExists() {
        // Given
        when(restaurantRepository.findByIdAndDeletedFalse("restaurant-123"))
            .thenReturn(Optional.of(restaurant));
        when(restaurantRepository.save(any())).thenReturn(restaurant);
        when(restaurantMapper.toResponse(any())).thenReturn(restaurantResponse);
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(CompletableFuture.completedFuture(null));
        
        // When
        RestaurantResponse response = restaurantService.approveRestaurant("restaurant-123", "admin-user");
        
        // Then
        assertThat(response).isNotNull();
        verify(restaurantRepository).save(any(Restaurant.class));
        verify(kafkaTemplate).send(eq("restaurant.approved"), anyString(), any());
        
        assertThat(restaurant.getStatus()).isEqualTo(RestaurantStatus.APPROVED);
        assertThat(restaurant.getUpdatedBy()).isEqualTo("admin-user");
    }
    
    @Test
    @DisplayName("Should throw exception when ownership percentage does not sum to 100")
    void shouldThrowException_whenOwnershipPercentageInvalid() {
        // Given
        createRequest.setOwners(List.of(
            OwnerDTO.builder()
                .ownerName("Owner 1")
                .ownerEmail("owner1@example.com")
                .ownerPhone("9876543210")
                .ownershipPercentage(60.0)
                .role(OwnerRole.PRIMARY_OWNER)
                .isPrimaryContact(true)
                .build()
        ));
        
        restaurant.setOwners(List.of(
            OwnerVO.builder()
                .ownerName("Owner 1")
                .ownershipPercentage(60.0)
                .build()
        ));
        
        when(restaurantRepository.findByNameIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(restaurantMapper.toEntity(any())).thenReturn(restaurant);
        
        // When & Then
        assertThatThrownBy(() -> restaurantService.createRestaurant(createRequest))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Total ownership percentage must equal 100%");
        
        verify(restaurantRepository, never()).save(any());
    }
}

