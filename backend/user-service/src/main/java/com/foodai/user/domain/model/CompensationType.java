package com.foodai.user.domain.model;

/**
 * Enumeration of compensation types.
 *
 * @author FoodAI Team
 */
public enum CompensationType {
    /**
     * Full refund to payment method
     */
    FULL_REFUND,

    /**
     * Partial refund to payment method
     */
    PARTIAL_REFUND,

    /**
     * Platform credits/wallet
     */
    CREDITS,

    /**
     * Coupon code for future use
     */
    COUPON,

    /**
     * Discount on next order
     */
    DISCOUNT,

    /**
     * Loyalty points
     */
    LOYALTY_POINTS,

    /**
     * Free delivery on next order
     */
    FREE_DELIVERY,

    /**
     * Replacement order
     */
    REPLACEMENT,

    /**
     * No compensation (apology only)
     */
    APOLOGY_ONLY
}

