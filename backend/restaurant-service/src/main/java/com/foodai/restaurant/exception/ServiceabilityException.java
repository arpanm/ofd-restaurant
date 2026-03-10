package com.foodai.restaurant.exception;

/**
 * Exception thrown when serviceability configuration is invalid or checks fail.
 */
public class ServiceabilityException extends RuntimeException {
    
    public ServiceabilityException(String message) {
        super(message);
    }
    
    public ServiceabilityException(String pincode, String reason) {
        super(String.format("Serviceability check failed for pincode %s: %s", pincode, reason));
    }
}


