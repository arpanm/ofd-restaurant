package com.foodai.restaurant.exception;

/**
 * Exception thrown when an outlet is not found.
 */
public class OutletNotFoundException extends RuntimeException {
    
    public OutletNotFoundException(String message) {
        super(message);
    }
    
    public OutletNotFoundException(String outletId, boolean byId) {
        super(String.format("Outlet with ID '%s' not found", outletId));
    }
}


