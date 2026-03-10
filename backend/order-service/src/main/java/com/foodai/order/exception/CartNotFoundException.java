package com.foodai.order.exception;

/**
 * Exception thrown when a cart is not found.
 *
 * @author FoodAI Team
 */
public class CartNotFoundException extends RuntimeException {

    public CartNotFoundException(String message) {
        super(message);
    }

    public CartNotFoundException(String userId, boolean isUserId) {
        super("Cart not found for user: " + userId);
    }
}

