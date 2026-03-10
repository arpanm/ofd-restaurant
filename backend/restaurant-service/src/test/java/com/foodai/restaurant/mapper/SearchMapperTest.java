package com.foodai.restaurant.mapper;

import com.foodai.restaurant.domain.model.*;
import com.foodai.restaurant.dto.response.OutletSearchResponse;
import com.foodai.restaurant.dto.response.RestaurantSearchResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for SearchMapper.
 */
@DisplayName("SearchMapper Tests")
class SearchMapperTest {
    
    private SearchMapper searchMapper;
    
    @BeforeEach
    void setUp() {
        searchMapper = new SearchMapper();
    }
    
    @Test
    @DisplayName("Should map Restaurant to RestaurantSearchResponse")
    void shouldMapRestaurantToSearchResponse() {
        // Given
        Restaurant restaurant = createTestRestaurant();
        String deliveryPincode = "560001";
        
        // When
        RestaurantSearchResponse response = searchMapper.toRestaurantSearchResponse(restaurant, deliveryPincode);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getRestaurantId()).isEqualTo("rest-1");
        assertThat(response.getName()).isEqualTo("Test Restaurant");
        assertThat(response.getAverageRating()).isEqualTo(4.5);
        assertThat(response.getTotalReviews()).isEqualTo(100);
        assertThat(response.getOutletCount()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("Should map RestaurantOutlet to OutletSearchResponse")
    void shouldMapOutletToSearchResponse() {
        // Given
        RestaurantOutlet outlet = createTestOutlet();
        String deliveryPincode = "560001";
        
        // When
        OutletSearchResponse response = searchMapper.toOutletSearchResponse(outlet, deliveryPincode);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getOutletId()).isEqualTo("outlet-1");
        assertThat(response.getOutletName()).isEqualTo("Main Branch");
        assertThat(response.getAverageRating()).isEqualTo(4.5);
        assertThat(response.getAcceptsOrders()).isTrue();
    }
    
    @Test
    @DisplayName("Should handle restaurant with no outlets")
    void shouldHandleRestaurantWithNoOutlets() {
        // Given
        Restaurant restaurant = createTestRestaurant();
        restaurant.setOutlets(null);
        
        // When
        RestaurantSearchResponse response = searchMapper.toRestaurantSearchResponse(restaurant, "560001");
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getOutletCount()).isEqualTo(0);
        assertThat(response.getNearbyOutlets()).isEmpty();
    }
    
    @Test
    @DisplayName("Should map nearby outlets correctly")
    void shouldMapNearbyOutlets() {
        // Given
        Restaurant restaurant = createTestRestaurant();
        
        // When
        RestaurantSearchResponse response = searchMapper.toRestaurantSearchResponse(restaurant, "560001");
        
        // Then
        assertThat(response.getNearbyOutlets()).isNotEmpty();
        assertThat(response.getNearbyOutlets()).hasSizeLessThanOrEqualTo(5); // Max 5 outlets
    }
    
    private Restaurant createTestRestaurant() {
        return Restaurant.builder()
                .id("rest-1")
                .name("Test Restaurant")
                .description("A test restaurant")
                .cuisineTypes(List.of("Italian", "Chinese"))
                .averageCostForTwo(800.0)
                .averageRating(4.5)
                .totalReviews(100)
                .status(RestaurantStatus.APPROVED)
                .acceptsOrders(true)
                .vegetarianOnly(false)
                .coverImage("http://example.com/image.jpg")
                .outlets(List.of(
                        createTestOutlet(),
                        createTestOutlet2()
                ))
                .build();
    }
    
    private RestaurantOutlet createTestOutlet() {
        return RestaurantOutlet.builder()
                .outletId("outlet-1")
                .outletName("Main Branch")
                .address(AddressVO.builder()
                        .street("123 Main St, Indiranagar")
                        .city("Bangalore")
                        .state("Karnataka")
                        .pincode("560001")
                        .latitude(12.9716)
                        .longitude(77.5946)
                        .build())
                .serviceabilityConfig(ServiceabilityConfig.builder()
                        .serviceablePincodes(List.of("560001", "560002", "560003"))
                        .build())
                .operatingHours(List.of(
                        OperatingHoursVO.builder()
                                .dayOfWeek(DayOfWeek.MONDAY)
                                .openTime(LocalTime.of(9, 0))
                                .closeTime(LocalTime.of(22, 0))
                                .build()
                ))
                .status(OutletStatus.ACTIVE)
                .acceptsOrders(true)
                .averageRating(4.5)
                .totalReviews(50)
                .minimumOrderValue(100.0)
                .deliveryFee(20.0)
                .build();
    }
    
    private RestaurantOutlet createTestOutlet2() {
        return RestaurantOutlet.builder()
                .outletId("outlet-2")
                .outletName("MG Road Branch")
                .address(AddressVO.builder()
                        .street("456 MG Road")
                        .city("Bangalore")
                        .state("Karnataka")
                        .pincode("560001")
                        .build())
                .serviceabilityConfig(ServiceabilityConfig.builder()
                        .serviceablePincodes(List.of("560001", "560004"))
                        .build())
                .status(OutletStatus.ACTIVE)
                .acceptsOrders(true)
                .averageRating(4.3)
                .totalReviews(30)
                .minimumOrderValue(150.0)
                .build();
    }
}

