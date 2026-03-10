package com.foodai.order.domain.model;

/**
 * Enumeration representing the status of a refund.
 *
 * @author FoodAI Team
 */
public enum RefundStatus {
    /**
     * Refund has been initiated but not processed.
     */
    PENDING,

    /**
     * Refund is being processed by the payment gateway.
     */
    PROCESSING,

    /**
     * Refund has been successfully processed.
     */
    PROCESSED,

    /**
     * Refund processing failed.
     */
    FAILED,

    /**
     * Refund was rejected/declined.
     */
    REJECTED
}

