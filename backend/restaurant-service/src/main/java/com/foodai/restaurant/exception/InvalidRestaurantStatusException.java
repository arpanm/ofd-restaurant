package com.foodai.restaurant.exception;

import com.foodai.restaurant.domain.model.RestaurantStatus;

/**
 * Exception thrown when an invalid restaurant status transition is attempted.
 */
public class InvalidRestaurantStatusException extends RuntimeException {
    
    public InvalidRestaurantStatusException(String message) {
        super(message);
    }
    
    public InvalidRestaurantStatusException(RestaurantStatus currentStatus, RestaurantStatus newStatus) {
        super(String.format("Invalid status transition from %s to %s", currentStatus, newStatus));
    }
}


