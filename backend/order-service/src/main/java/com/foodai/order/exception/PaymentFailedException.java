package com.foodai.order.exception;

/**
 * Exception thrown when a payment fails.
 *
 * @author FoodAI Team
 */
public class PaymentFailedException extends RuntimeException {

    public PaymentFailedException(String message) {
        super(message);
    }
}

