package com.foodai.order.exception;

/**
 * Exception thrown when a payment is not found.
 *
 * @author FoodAI Team
 */
public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(String message) {
        super(message);
    }
}

