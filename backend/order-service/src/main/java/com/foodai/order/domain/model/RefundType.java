package com.foodai.order.domain.model;

/**
 * Enumeration representing the type of refund.
 *
 * @author FoodAI Team
 */
public enum RefundType {
    /**
     * Full refund of the order amount.
     */
    FULL,

    /**
     * Partial refund for specific items.
     */
    PARTIAL,

    /**
     * Refund due to order cancellation.
     */
    CANCELLATION,

    /**
     * Refund due to quality issues.
     */
    QUALITY_ISSUE,

    /**
     * Refund due to missing items.
     */
    MISSING_ITEMS,

    /**
     * Refund due to wrong items delivered.
     */
    WRONG_ITEMS,

    /**
     * Refund due to late delivery.
     */
    LATE_DELIVERY,

    /**
     * Refund due to delivery failure.
     */
    DELIVERY_FAILED,

    /**
     * Goodwill refund for customer satisfaction.
     */
    GOODWILL
}

