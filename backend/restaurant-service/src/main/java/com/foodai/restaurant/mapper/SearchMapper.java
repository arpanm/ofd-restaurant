package com.foodai.restaurant.mapper;

import com.foodai.restaurant.domain.model.RestaurantOutlet;
import com.foodai.restaurant.domain.model.Restaurant;
import com.foodai.restaurant.dto.response.OutletSearchResponse;
import com.foodai.restaurant.dto.response.RestaurantSearchResponse;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting domain models to search response DTOs.
 * Handles transformation of Restaurant/Outlet entities to search-specific response formats.
 */
@Component
public class SearchMapper {
    
    /**
     * Convert Restaurant to RestaurantSearchResponse.
     * Includes nearest outlet information and aggregated data.
     */
    public RestaurantSearchResponse toRestaurantSearchResponse(Restaurant restaurant, String deliveryPincode) {
        // Find nearest outlet (simplified - in production would calculate actual distance)
        RestaurantOutlet nearestOutlet = findNearestOutlet(restaurant, deliveryPincode);
        
        return RestaurantSearchResponse.builder()
                .restaurantId(restaurant.getId())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .cuisineTypes(restaurant.getCuisineTypes())
                .averageCostForTwo(restaurant.getAverageCostForTwo())
                .averageRating(restaurant.getAverageRating())
                .totalReviews(restaurant.getTotalReviews())
                .status(restaurant.getStatus())
                .acceptsOrders(restaurant.getAcceptsOrders())
                .outletCount(restaurant.getOutlets() != null ? restaurant.getOutlets().size() : 0)
                .nearestOutletDistanceKm(nearestOutlet != null ? calculateDistance(nearestOutlet, deliveryPincode) : null)
                .estimatedDeliveryMinutes(nearestOutlet != null ? estimateDeliveryTime(nearestOutlet, deliveryPincode) : null)
                .deliveryFee(nearestOutlet != null ? calculateDeliveryFee(nearestOutlet, deliveryPincode) : null)
                .anyOutletOpen(isAnyOutletOpen(restaurant))
                .vegetarianOnly(restaurant.getVegetarianOnly())
                .minimumOrderValue(nearestOutlet != null ? getMinimumOrderValue(nearestOutlet) : null)
                .imageUrl(restaurant.getCoverImage())
                .tags(generateTags(restaurant))
                .nearbyOutlets(mapNearbyOutlets(restaurant, deliveryPincode))
                .build();
    }
    
    /**
     * Convert Outlet to OutletSearchResponse.
     * Includes detailed outlet-specific information.
     * Note: Some fields like averageCostForTwo and vegetarianOnly come from parent restaurant.
     */
    public OutletSearchResponse toOutletSearchResponse(RestaurantOutlet outlet, String deliveryPincode) {
        return OutletSearchResponse.builder()
                .outletId(outlet.getOutletId())
                .outletName(outlet.getOutletName())
                // Note: restaurantId and restaurantName would come from parent restaurant
                .description(null) // OutletOutlet doesn't have description, would come from parent
                .averageCostForTwo(null) // Would come from parent restaurant
                .averageRating(outlet.getAverageRating())
                .totalReviews(outlet.getTotalReviews())
                .acceptsOrders(outlet.getAcceptsOrders())
                .address(mapAddress(outlet))
                .distanceKm(calculateDistance(outlet, deliveryPincode))
                .estimatedDeliveryMinutes(estimateDeliveryTime(outlet, deliveryPincode))
                .deliveryFee(outlet.getDeliveryFee() != null ? outlet.getDeliveryFee() : calculateDeliveryFee(outlet, deliveryPincode))
                .currentlyOpen(isOutletOpen(outlet))
                .todayTimings(getTodayTimings(outlet))
                .vegetarianOnly(null) // Would come from parent restaurant
                .minimumOrderValue(outlet.getMinimumOrderValue())
                .imageUrl(null) // RestaurantOutlet doesn't store images directly
                .tags(generateOutletTags(outlet))
                .build();
    }
    
    /**
     * Find the nearest outlet that services the delivery pincode.
     */
    private RestaurantOutlet findNearestOutlet(Restaurant restaurant, String deliveryPincode) {
        if (restaurant.getOutlets() == null || restaurant.getOutlets().isEmpty()) {
            return null;
        }
        
        return restaurant.getOutlets().stream()
                .filter(outlet -> servicesPincode(outlet, deliveryPincode))
                .findFirst() // In production, would sort by actual distance
                .orElse(null);
    }
    
    /**
     * Check if outlet services the given pincode.
     */
    private boolean servicesPincode(RestaurantOutlet outlet, String pincode) {
        if (outlet.getServiceabilityConfig() == null || 
                outlet.getServiceabilityConfig().getServiceablePincodes() == null) {
            return false;
        }
        return outlet.getServiceabilityConfig().getServiceablePincodes().contains(pincode);
    }
    
    /**
     * Calculate distance from outlet to delivery location.
     * Simplified implementation - in production would use actual coordinates.
     */
    private Double calculateDistance(RestaurantOutlet outlet, String deliveryPincode) {
        // TODO: Implement actual distance calculation using lat/long
        return 5.0; // Placeholder
    }
    
    /**
     * Estimate delivery time based on distance and other factors.
     */
    private Integer estimateDeliveryTime(RestaurantOutlet outlet, String deliveryPincode) {
        Double distance = calculateDistance(outlet, deliveryPincode);
        // Assume 30 minutes base + 5 minutes per km
        return 30 + (int) (distance * 5);
    }
    
    /**
     * Calculate delivery fee based on distance and service area configuration.
     */
    private Double calculateDeliveryFee(RestaurantOutlet outlet, String deliveryPincode) {
        // TODO: Implement delivery fee calculation based on outlet configuration
        // For now, return a fixed fee
        return 20.0;
    }
    
    /**
     * Check if any outlet of the restaurant is currently open.
     */
    private Boolean isAnyOutletOpen(Restaurant restaurant) {
        if (restaurant.getOutlets() == null) {
            return false;
        }
        return restaurant.getOutlets().stream().anyMatch(this::isOutletOpen);
    }
    
    /**
     * Check if outlet is currently open.
     */
    private Boolean isOutletOpen(RestaurantOutlet outlet) {
        if (outlet.getOperatingHours() == null) {
            return false;
        }
        
        DayOfWeek today = DayOfWeek.from(java.time.LocalDate.now());
        LocalTime now = LocalTime.now();
        
        return outlet.getOperatingHours().stream()
                .anyMatch(hours -> hours.getDayOfWeek() == today &&
                        !hours.getOpenTime().isAfter(now) &&
                        !hours.getCloseTime().isBefore(now));
    }
    
    /**
     * Get today's operating hours for display.
     */
    private String getTodayTimings(RestaurantOutlet outlet) {
        if (outlet.getOperatingHours() == null) {
            return "Hours not available";
        }
        
        DayOfWeek today = DayOfWeek.from(java.time.LocalDate.now());
        
        return outlet.getOperatingHours().stream()
                .filter(hours -> hours.getDayOfWeek() == today)
                .map(hours -> hours.getOpenTime() + " - " + hours.getCloseTime())
                .findFirst()
                .orElse("Closed today");
    }
    
    /**
     * Get minimum order value from outlet.
     */
    private Double getMinimumOrderValue(RestaurantOutlet outlet) {
        // Placeholder - would come from outlet configuration
        return 100.0;
    }
    
    /**
     * Generate promotional tags for restaurant.
     */
    private List<String> generateTags(Restaurant restaurant) {
        // TODO: Implement actual tag generation logic based on offers, ratings, etc.
        return List.of();
    }
    
    /**
     * Generate promotional tags for outlet.
     */
    private List<String> generateOutletTags(RestaurantOutlet outlet) {
        // TODO: Implement actual tag generation logic
        return List.of();
    }
    
    /**
     * Map nearby outlets for restaurant response.
     */
    private List<RestaurantSearchResponse.NearbyOutletDTO> mapNearbyOutlets(Restaurant restaurant, String deliveryPincode) {
        if (restaurant.getOutlets() == null) {
            return List.of();
        }
        
        return restaurant.getOutlets().stream()
                .filter(outlet -> servicesPincode(outlet, deliveryPincode))
                .limit(5) // Show max 5 nearby outlets
                .map(outlet -> RestaurantSearchResponse.NearbyOutletDTO.builder()
                        .outletId(outlet.getOutletId())
                        .outletName(outlet.getOutletName())
                        .area(outlet.getAddress() != null ? outlet.getAddress().getStreet() : null)
                        .city(outlet.getAddress() != null ? outlet.getAddress().getCity() : null)
                        .distanceKm(calculateDistance(outlet, deliveryPincode))
                        .currentlyOpen(isOutletOpen(outlet))
                        .build())
                .collect(Collectors.toList());
    }
    
    /**
     * Map outlet address to response DTO.
     */
    private OutletSearchResponse.AddressDTO mapAddress(RestaurantOutlet outlet) {
        if (outlet.getAddress() == null) {
            return null;
        }
        
        return OutletSearchResponse.AddressDTO.builder()
                .street(outlet.getAddress().getStreet())
                .area(outlet.getAddress().getStreet()) // AddressVO doesn't have separate area field
                .city(outlet.getAddress().getCity())
                .state(outlet.getAddress().getState())
                .pincode(outlet.getAddress().getPincode())
                .landmark(outlet.getAddress().getLandmark())
                .latitude(outlet.getAddress().getLatitude())
                .longitude(outlet.getAddress().getLongitude())
                .build();
    }
}

