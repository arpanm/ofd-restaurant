package com.foodai.order.exception;

/**
 * Exception thrown when an order operation is invalid due to current state.
 *
 * @author FoodAI Team
 */
public class InvalidOrderStateException extends RuntimeException {

    public InvalidOrderStateException(String message) {
        super(message);
    }
}

