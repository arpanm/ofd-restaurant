package com.foodai.order.domain.model;

/**
 * Enumeration representing the status of an order throughout its lifecycle.
 *
 * <p>Order status flow:
 * CREATED → CONFIRMED → PREPARING → READY → PICKED_UP → IN_TRANSIT → DELIVERED
 * At any point before DELIVERED, an order can be CANCELLED.
 *
 * @author FoodAI Team
 */
public enum OrderStatus {
    /**
     * Order has been created but payment is pending.
     */
    CREATED,

    /**
     * Payment received and order confirmed.
     */
    CONFIRMED,

    /**
     * Restaurant has started preparing the order.
     */
    PREPARING,

    /**
     * Order is ready for pickup by delivery partner.
     */
    READY,

    /**
     * Delivery partner has picked up the order.
     */
    PICKED_UP,

    /**
     * Order is on the way to the customer.
     */
    IN_TRANSIT,

    /**
     * Order has been delivered to the customer.
     */
    DELIVERED,

    /**
     * Order has been cancelled.
     */
    CANCELLED,

    /**
     * Order delivery failed.
     */
    DELIVERY_FAILED,

    /**
     * Order is being returned/refunded.
     */
    RETURN_INITIATED,

    /**
     * Return completed and refund processed.
     */
    RETURNED
}

