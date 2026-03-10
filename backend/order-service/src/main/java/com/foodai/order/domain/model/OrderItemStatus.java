package com.foodai.order.domain.model;

/**
 * Enumeration representing the status of an individual order item.
 *
 * @author FoodAI Team
 */
public enum OrderItemStatus {
    /**
     * Item is pending preparation.
     */
    PENDING,

    /**
     * Item is being prepared.
     */
    PREPARING,

    /**
     * Item is ready.
     */
    READY,

    /**
     * Item is unavailable.
     */
    UNAVAILABLE,

    /**
     * Item was cancelled.
     */
    CANCELLED
}

