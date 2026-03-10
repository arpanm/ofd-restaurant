package com.foodai.restaurant.domain.model;

/**
 * Action to be taken for penalty violations.
 */
public enum PenaltyAction {
    /**
     * Issue warning to restaurant
     */
    WARNING,
    
    /**
     * Apply monetary fine
     */
    FINE,
    
    /**
     * Temporary suspension of restaurant/outlet
     */
    SUSPENSION,
    
    /**
     * Terminate contract
     */
    TERMINATION
}


