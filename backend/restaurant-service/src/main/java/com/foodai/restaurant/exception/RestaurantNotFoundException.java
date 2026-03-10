package com.foodai.restaurant.exception;

/**
 * Exception thrown when a restaurant is not found.
 */
public class RestaurantNotFoundException extends RuntimeException {
    
    public RestaurantNotFoundException(String message) {
        super(message);
    }
    
    public RestaurantNotFoundException(String restaurantId, boolean byId) {
        super(String.format("Restaurant with ID '%s' not found", restaurantId));
    }
}


