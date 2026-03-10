package com.foodai.restaurant.exception;

/**
 * Exception thrown when attempting to create a restaurant that already exists.
 */
public class RestaurantAlreadyExistsException extends RuntimeException {
    
    public RestaurantAlreadyExistsException(String message) {
        super(message);
    }
}

