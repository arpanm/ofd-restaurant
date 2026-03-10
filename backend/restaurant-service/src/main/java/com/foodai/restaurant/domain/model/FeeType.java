package com.foodai.restaurant.domain.model;

/**
 * Type of fee calculation method.
 */
public enum FeeType {
    /**
     * Percentage-based fee (e.g., 15% of order value)
     */
    PERCENTAGE,
    
    /**
     * Fixed amount per order (e.g., ₹20 per order)
     */
    FIXED,
    
    /**
     * Hybrid model (combination of percentage and fixed)
     */
    HYBRID
}


