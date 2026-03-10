package com.foodai.restaurant.domain.model;

/**
 * Who pays the delivery fee.
 */
public enum DeliveryFeePayor {
    /**
     * Restaurant pays full delivery fee
     */
    RESTAURANT,
    
    /**
     * Customer pays full delivery fee
     */
    CUSTOMER,
    
    /**
     * Delivery fee split between restaurant and customer
     */
    SPLIT,
    
    /**
     * Platform absorbs delivery fee
     */
    PLATFORM
}


