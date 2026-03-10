package com.foodai.restaurant.exception;

/**
 * Exception thrown when document verification fails.
 */
public class DocumentVerificationException extends RuntimeException {
    
    public DocumentVerificationException(String message) {
        super(message);
    }
    
    public DocumentVerificationException(String documentType, String reason) {
        super(String.format("Document verification failed for %s: %s", documentType, reason));
    }
}


