package com.foodai.order.exception;

/**
 * Exception thrown when an order is not found.
 *
 * @author FoodAI Team
 */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(String id, boolean isOrderNumber) {
        super(isOrderNumber 
            ? "Order not found with order number: " + id 
            : "Order not found with id: " + id);
    }
}

