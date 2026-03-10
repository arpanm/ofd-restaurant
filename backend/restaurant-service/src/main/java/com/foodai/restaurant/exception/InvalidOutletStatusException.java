package com.foodai.restaurant.exception;

import com.foodai.restaurant.domain.model.OutletStatus;

/**
 * Exception thrown when an invalid outlet status transition is attempted.
 */
public class InvalidOutletStatusException extends RuntimeException {
    
    public InvalidOutletStatusException(String message) {
        super(message);
    }
    
    public InvalidOutletStatusException(OutletStatus currentStatus, OutletStatus newStatus) {
        super(String.format("Invalid outlet status transition from %s to %s", currentStatus, newStatus));
    }
}


