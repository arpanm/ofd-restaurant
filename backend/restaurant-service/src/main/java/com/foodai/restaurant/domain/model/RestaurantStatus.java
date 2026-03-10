package com.foodai.restaurant.domain.model;

/**
 * Status enum for Restaurant (Brand Level).
 * Represents the overall status of the restaurant brand.
 */
public enum RestaurantStatus {
    /**
     * Restaurant registration is pending approval
     */
    PENDING,
    
    /**
     * Restaurant brand has been approved by operations team
     */
    APPROVED,
    
    /**
     * Restaurant is active and can accept orders (at least one outlet is active)
     */
    ACTIVE,
    
    /**
     * Restaurant is temporarily suspended (all outlets suspended)
     */
    SUSPENDED,
    
    /**
     * Restaurant registration was rejected
     */
    REJECTED
}


