package com.foodai.order.domain.model;

/**
 * Enumeration representing the payment status of an order.
 *
 * @author FoodAI Team
 */
public enum PaymentStatus {
    /**
     * Payment has not been initiated yet.
     */
    PENDING,

    /**
     * Payment is being processed by the gateway.
     */
    PROCESSING,

    /**
     * Payment was successful.
     */
    SUCCESS,

    /**
     * Payment failed.
     */
    FAILED,

    /**
     * Full refund has been processed.
     */
    REFUNDED,

    /**
     * Partial refund has been processed.
     */
    PARTIALLY_REFUNDED,

    /**
     * Payment was cancelled by user.
     */
    CANCELLED,

    /**
     * Cash on delivery - payment to be collected.
     */
    COD_PENDING,

    /**
     * Cash on delivery - payment collected.
     */
    COD_COLLECTED
}

