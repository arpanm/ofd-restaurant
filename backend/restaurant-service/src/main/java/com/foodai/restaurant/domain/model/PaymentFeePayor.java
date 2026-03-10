package com.foodai.restaurant.domain.model;

/**
 * Who pays the payment gateway fee.
 */
public enum PaymentFeePayor {
    /**
     * Restaurant pays payment gateway fee
     */
    RESTAURANT,
    
    /**
     * Customer pays payment gateway fee
     */
    CUSTOMER,
    
    /**
     * Platform absorbs payment gateway fee
     */
    PLATFORM
}


