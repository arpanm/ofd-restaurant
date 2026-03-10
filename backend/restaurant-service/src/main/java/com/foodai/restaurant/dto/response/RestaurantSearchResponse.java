package com.foodai.restaurant.dto.response;

import com.foodai.restaurant.domain.model.RestaurantStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for restaurant search results.
 * Returned when searchRestaurantRequest.returnOutlets = false (default).
 * Provides restaurant-level aggregated information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantSearchResponse {
    
    /**
     * Unique identifier for the restaurant.
     */
    private String restaurantId;
    
    /**
     * Restaurant name.
     */
    private String name;
    
    /**
     * Restaurant description.
     */
    private String description;
    
    /**
     * List of cuisine types offered.
     */
    private List<String> cuisineTypes;
    
    /**
     * Average cost for two people across all outlets.
     */
    private Double averageCostForTwo;
    
    /**
     * Average rating across all outlets (0.0 to 5.0).
     */
    private Double averageRating;
    
    /**
     * Total number of reviews across all outlets.
     */
    private Integer totalReviews;
    
    /**
     * Restaurant status.
     */
    private RestaurantStatus status;
    
    /**
     * Whether restaurant is currently accepting orders.
     */
    private Boolean acceptsOrders;
    
    /**
     * Number of outlets in serviceable area.
     */
    private Integer outletCount;
    
    /**
     * Distance to nearest outlet (in kilometers).
     */
    private Double nearestOutletDistanceKm;
    
    /**
     * Estimated delivery time from nearest outlet (in minutes).
     */
    private Integer estimatedDeliveryMinutes;
    
    /**
     * Delivery fee from nearest outlet.
     */
    private Double deliveryFee;
    
    /**
     * Whether any outlet is currently open.
     */
    private Boolean anyOutletOpen;
    
    /**
     * Whether restaurant serves vegetarian food only.
     */
    private Boolean vegetarianOnly;
    
    /**
     * Minimum order value required.
     */
    private Double minimumOrderValue;
    
    /**
     * Image URL for the restaurant.
     */
    private String imageUrl;
    
    /**
     * Special offers or tags (e.g., "Free Delivery", "50% Off").
     */
    private List<String> tags;
    
    /**
     * List of nearby serviceable outlets.
     * Only includes basic outlet information (not full details).
     */
    private List<NearbyOutletDTO> nearbyOutlets;
    
    /**
     * Nested DTO for nearby outlet basic information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NearbyOutletDTO {
        private String outletId;
        private String outletName;
        private String area;
        private String city;
        private Double distanceKm;
        private Boolean currentlyOpen;
    }
}

