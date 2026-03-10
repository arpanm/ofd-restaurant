package com.foodai.restaurant.dto.request;

import com.foodai.restaurant.domain.model.BudgetType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for searching restaurants with multiple filters.
 * 
 * Search Criteria:
 * - Delivery pincode (mandatory) - determines serviceability
 * - Cuisine types (optional) - filter by food types
 * - Budget type (optional) - filter by price range
 * - Rating range (optional) - filter by customer ratings
 * - Search term (optional) - search in restaurant/outlet names
 * - Return outlets flag - determines response type
 * 
 * Supports pagination for large result sets.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class SearchRestaurantRequest {
    
    /**
     * Delivery pincode - MANDATORY for serviceability check.
     * Must be a valid 6-digit Indian pincode.
     */
    @NotBlank(message = "Delivery pincode is required")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Pincode must be a valid 6-digit number")
    private String deliveryPincode;
    
    /**
     * Optional list of cuisine types to filter by.
     * Examples: "Italian", "Chinese", "Indian", "Continental"
     */
    private List<String> cuisineTypes;
    
    /**
     * Optional budget type filter.
     * Filters restaurants based on average cost for two people.
     */
    private BudgetType budgetType;
    
    /**
     * Minimum rating filter (0.0 to 5.0).
     * Only restaurants with rating >= this value will be returned.
     */
    @Min(value = 0, message = "Minimum rating must be between 0 and 5")
    @Max(value = 5, message = "Minimum rating must be between 0 and 5")
    private Double minRating;
    
    /**
     * Maximum rating filter (0.0 to 5.0).
     * Only restaurants with rating <= this value will be returned.
     */
    @Min(value = 0, message = "Maximum rating must be between 0 and 5")
    @Max(value = 5, message = "Maximum rating must be between 0 and 5")
    private Double maxRating;
    
    /**
     * Optional search term to filter by name.
     * Searches in restaurant names, outlet names, and descriptions.
     */
    private String searchTerm;
    
    /**
     * Flag to determine response type.
     * - true: Returns individual outlets with their locations
     * - false: Returns restaurants grouped (default)
     */
    @Builder.Default
    private Boolean returnOutlets = false;
    
    /**
     * Optional filter for vegetarian-only restaurants.
     */
    private Boolean vegetarianOnly;
    
    /**
     * Optional filter for currently open restaurants.
     * Checks against current time and outlet operating hours.
     */
    private Boolean currentlyOpen;
    
    /**
     * Minimum price filter (cost for two).
     */
    @Min(value = 0, message = "Minimum price must be non-negative")
    private Double minPrice;
    
    /**
     * Maximum price filter (cost for two).
     */
    @Min(value = 0, message = "Maximum price must be non-negative")
    private Double maxPrice;
    
    /**
     * Sort by field.
     * Supported values: "rating", "price", "distance", "popularity"
     */
    @Builder.Default
    private String sortBy = "rating";
    
    /**
     * Sort direction.
     * Supported values: "asc", "desc"
     */
    @Builder.Default
    private String sortDirection = "desc";
    
    /**
     * Page number for pagination (0-indexed).
     */
    @Builder.Default
    @Min(value = 0, message = "Page number must be non-negative")
    private Integer page = 0;
    
    /**
     * Page size for pagination.
     */
    @Builder.Default
    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size cannot exceed 100")
    private Integer size = 20;
    
    /**
     * Validates rating range consistency.
     */
    public boolean isValidRatingRange() {
        if (minRating != null && maxRating != null) {
            return minRating <= maxRating;
        }
        return true;
    }
    
    /**
     * Validates price range consistency.
     */
    public boolean isValidPriceRange() {
        if (minPrice != null && maxPrice != null) {
            return minPrice <= maxPrice;
        }
        return true;
    }
}

