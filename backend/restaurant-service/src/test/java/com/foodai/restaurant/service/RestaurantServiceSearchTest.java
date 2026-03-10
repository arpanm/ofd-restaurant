package com.foodai.restaurant.service;

import com.foodai.restaurant.domain.model.BudgetType;
import com.foodai.restaurant.domain.model.Restaurant;
import com.foodai.restaurant.domain.model.RestaurantOutlet;
import com.foodai.restaurant.domain.repository.RestaurantSearchRepository;
import com.foodai.restaurant.dto.request.SearchRestaurantRequest;
import com.foodai.restaurant.dto.response.OutletSearchResponse;
import com.foodai.restaurant.dto.response.RestaurantSearchResponse;
import com.foodai.restaurant.mapper.SearchMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Restaurant search functionality.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RestaurantService Search Tests")
class RestaurantServiceSearchTest {
    
    @Mock
    private RestaurantSearchRepository restaurantSearchRepository;
    
    @Mock
    private SearchMapper searchMapper;
    
    @InjectMocks
    private RestaurantService restaurantService;
    
    private SearchRestaurantRequest validRequest;
    
    @BeforeEach
    void setUp() {
        validRequest = SearchRestaurantRequest.builder()
                .deliveryPincode("560001")
                .cuisineTypes(List.of("Italian"))
                .budgetType(BudgetType.MID_RANGE)
                .minRating(4.0)
                .page(0)
                .size(20)
                .sortBy("rating")
                .sortDirection("desc")
                .returnOutlets(false)
                .build();
    }
    
    @Test
    @DisplayName("Should search restaurants successfully")
    void shouldSearchRestaurantsSuccessfully() {
        // Given
        Restaurant restaurant = mock(Restaurant.class);
        Page<Restaurant> restaurantPage = new PageImpl<>(List.of(restaurant));
        RestaurantSearchResponse response = mock(RestaurantSearchResponse.class);
        
        when(restaurantSearchRepository.searchRestaurants(any(), any(Pageable.class)))
                .thenReturn(restaurantPage);
        when(searchMapper.toRestaurantSearchResponse(any(), eq("560001")))
                .thenReturn(response);
        
        // When
        Page<?> result = restaurantService.searchRestaurants(validRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(restaurantSearchRepository).searchRestaurants(any(), any(Pageable.class));
        verify(searchMapper).toRestaurantSearchResponse(restaurant, "560001");
    }
    
    @Test
    @DisplayName("Should search outlets successfully")
    void shouldSearchOutletsSuccessfully() {
        // Given
        SearchRestaurantRequest outletRequest = validRequest.toBuilder()
                .returnOutlets(true)
                .build();
        
        RestaurantOutlet outlet = mock(RestaurantOutlet.class);
        Page<RestaurantOutlet> outletPage = new PageImpl<>(List.of(outlet));
        OutletSearchResponse response = mock(OutletSearchResponse.class);
        
        when(restaurantSearchRepository.searchOutlets(any(), any(Pageable.class)))
                .thenReturn(outletPage);
        when(searchMapper.toOutletSearchResponse(any(), eq("560001")))
                .thenReturn(response);
        
        // When
        Page<?> result = restaurantService.searchRestaurants(outletRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        verify(restaurantSearchRepository).searchOutlets(any(), any(Pageable.class));
        verify(searchMapper).toOutletSearchResponse(outlet, "560001");
    }
    
    @Test
    @DisplayName("Should throw exception for invalid rating range")
    void shouldThrowExceptionForInvalidRatingRange() {
        // Given
        SearchRestaurantRequest invalidRequest = validRequest.toBuilder()
                .minRating(5.0)
                .maxRating(3.0)
                .build();
        
        // When & Then
        assertThatThrownBy(() -> restaurantService.searchRestaurants(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid rating range");
    }
    
    @Test
    @DisplayName("Should throw exception for invalid price range")
    void shouldThrowExceptionForInvalidPriceRange() {
        // Given
        SearchRestaurantRequest invalidRequest = validRequest.toBuilder()
                .minPrice(1000.0)
                .maxPrice(500.0)
                .build();
        
        // When & Then
        assertThatThrownBy(() -> restaurantService.searchRestaurants(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid price range");
    }
    
    @Test
    @DisplayName("Should handle empty search results")
    void shouldHandleEmptySearchResults() {
        // Given
        Page<Restaurant> emptyPage = new PageImpl<>(List.of());
        
        when(restaurantSearchRepository.searchRestaurants(any(), any(Pageable.class)))
                .thenReturn(emptyPage);
        
        // When
        Page<?> result = restaurantService.searchRestaurants(validRequest);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();
    }
    
    @Test
    @DisplayName("Should search with all filters applied")
    void shouldSearchWithAllFilters() {
        // Given
        SearchRestaurantRequest fullRequest = SearchRestaurantRequest.builder()
                .deliveryPincode("560001")
                .cuisineTypes(List.of("Italian", "Chinese"))
                .budgetType(BudgetType.MID_RANGE)
                .minRating(4.0)
                .maxRating(5.0)
                .searchTerm("pizza")
                .vegetarianOnly(true)
                .currentlyOpen(true)
                .minPrice(300.0)
                .maxPrice(1000.0)
                .sortBy("rating")
                .sortDirection("desc")
                .page(0)
                .size(10)
                .returnOutlets(false)
                .build();
        
        Page<Restaurant> restaurantPage = new PageImpl<>(List.of(mock(Restaurant.class)));
        
        when(restaurantSearchRepository.searchRestaurants(any(), any(Pageable.class)))
                .thenReturn(restaurantPage);
        when(searchMapper.toRestaurantSearchResponse(any(), any()))
                .thenReturn(mock(RestaurantSearchResponse.class));
        
        // When
        Page<?> result = restaurantService.searchRestaurants(fullRequest);
        
        // Then
        assertThat(result).isNotNull();
        verify(restaurantSearchRepository).searchRestaurants(eq(fullRequest), any(Pageable.class));
    }
}

