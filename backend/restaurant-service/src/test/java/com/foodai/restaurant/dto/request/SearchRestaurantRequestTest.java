package com.foodai.restaurant.dto.request;

import com.foodai.restaurant.domain.model.BudgetType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for SearchRestaurantRequest validation methods.
 */
@DisplayName("SearchRestaurantRequest Tests")
class SearchRestaurantRequestTest {
    
    @Test
    @DisplayName("Should validate valid rating range")
    void shouldValidateValidRatingRange() {
        // Given
        SearchRestaurantRequest request = SearchRestaurantRequest.builder()
                .deliveryPincode("560001")
                .minRating(3.0)
                .maxRating(5.0)
                .build();
        
        // When & Then
        assertThat(request.isValidRatingRange()).isTrue();
    }
    
    @Test
    @DisplayName("Should invalidate when minRating > maxRating")
    void shouldInvalidateWhenMinRatingGreaterThanMax() {
        // Given
        SearchRestaurantRequest request = SearchRestaurantRequest.builder()
                .deliveryPincode("560001")
                .minRating(4.5)
                .maxRating(3.0)
                .build();
        
        // When & Then
        assertThat(request.isValidRatingRange()).isFalse();
    }
    
    @Test
    @DisplayName("Should validate valid price range")
    void shouldValidateValidPriceRange() {
        // Given
        SearchRestaurantRequest request = SearchRestaurantRequest.builder()
                .deliveryPincode("560001")
                .minPrice(100.0)
                .maxPrice(500.0)
                .build();
        
        // When & Then
        assertThat(request.isValidPriceRange()).isTrue();
    }
    
    @Test
    @DisplayName("Should invalidate when minPrice > maxPrice")
    void shouldInvalidateWhenMinPriceGreaterThanMax() {
        // Given
        SearchRestaurantRequest request = SearchRestaurantRequest.builder()
                .deliveryPincode("560001")
                .minPrice(500.0)
                .maxPrice(100.0)
                .build();
        
        // When & Then
        assertThat(request.isValidPriceRange()).isFalse();
    }
    
    @Test
    @DisplayName("Should have default values for pagination")
    void shouldHaveDefaultPaginationValues() {
        // Given
        SearchRestaurantRequest request = SearchRestaurantRequest.builder()
                .deliveryPincode("560001")
                .build();
        
        // When & Then
        assertThat(request.getPage()).isEqualTo(0);
        assertThat(request.getSize()).isEqualTo(20);
        assertThat(request.getSortBy()).isEqualTo("rating");
        assertThat(request.getSortDirection()).isEqualTo("desc");
        assertThat(request.getReturnOutlets()).isFalse();
    }
    
    @Test
    @DisplayName("Should build complete search request")
    void shouldBuildCompleteSearchRequest() {
        // Given & When
        SearchRestaurantRequest request = SearchRestaurantRequest.builder()
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
                .page(1)
                .size(10)
                .returnOutlets(false)
                .build();
        
        // Then
        assertThat(request).isNotNull();
        assertThat(request.getDeliveryPincode()).isEqualTo("560001");
        assertThat(request.getCuisineTypes()).containsExactly("Italian", "Chinese");
        assertThat(request.getBudgetType()).isEqualTo(BudgetType.MID_RANGE);
        assertThat(request.getMinRating()).isEqualTo(4.0);
        assertThat(request.getSearchTerm()).isEqualTo("pizza");
        assertThat(request.getVegetarianOnly()).isTrue();
    }
}

