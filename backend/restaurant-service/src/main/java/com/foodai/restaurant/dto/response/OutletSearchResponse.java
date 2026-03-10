package com.foodai.restaurant.dto.response;

import com.foodai.restaurant.domain.model.RestaurantStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for outlet search results.
 * Returned when searchRestaurantRequest.returnOutlets = true.
 * Provides detailed outlet-level information including location and delivery details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutletSearchResponse {
    
    /**
     * Unique identifier for the outlet.
     */
    private String outletId;
    
    /**
     * Outlet name/branch identifier.
     */
    private String outletName;
    
    /**
     * Parent restaurant ID.
     */
    private String restaurantId;
    
    /**
     * Parent restaurant name.
     */
    private String restaurantName;
    
    /**
     * Restaurant/outlet description.
     */
    private String description;
    
    /**
     * List of cuisine types offered.
     */
    private List<String> cuisineTypes;
    
    /**
     * Average cost for two people.
     */
    private Double averageCostForTwo;
    
    /**
     * Average rating (0.0 to 5.0).
     */
    private Double averageRating;
    
    /**
     * Total number of reviews.
     */
    private Integer totalReviews;
    
    /**
     * Restaurant status.
     */
    private RestaurantStatus status;
    
    /**
     * Whether outlet is currently accepting orders.
     */
    private Boolean acceptsOrders;
    
    /**
     * Outlet address.
     */
    private AddressDTO address;
    
    /**
     * Distance from delivery pincode (in kilometers).
     */
    private Double distanceKm;
    
    /**
     * Estimated delivery time (in minutes).
     */
    private Integer estimatedDeliveryMinutes;
    
    /**
     * Delivery fee for this location.
     */
    private Double deliveryFee;
    
    /**
     * Whether the outlet is currently open.
     */
    private Boolean currentlyOpen;
    
    /**
     * Current operating hours for today.
     */
    private String todayTimings;
    
    /**
     * Whether restaurant serves vegetarian food only.
     */
    private Boolean vegetarianOnly;
    
    /**
     * Minimum order value required.
     */
    private Double minimumOrderValue;
    
    /**
     * Image URL for the restaurant/outlet.
     */
    private String imageUrl;
    
    /**
     * Special offers or tags (e.g., "Free Delivery", "50% Off").
     */
    private List<String> tags;
    
    /**
     * Nested DTO for address information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDTO {
        private String street;
        private String area;
        private String city;
        private String state;
        private String pincode;
        private String landmark;
        private Double latitude;
        private Double longitude;
    }
}

