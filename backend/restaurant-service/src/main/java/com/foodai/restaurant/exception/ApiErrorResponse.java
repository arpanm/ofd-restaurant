package com.foodai.restaurant.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Standardized API error response format.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    /**
     * Timestamp when error occurred
     */
    private Instant timestamp;
    
    /**
     * HTTP status code
     */
    private int status;
    
    /**
     * Error type/category
     */
    private String error;
    
    /**
     * Error message
     */
    private String message;
    
    /**
     * Request path that caused the error
     */
    private String path;
    
    /**
     * Validation errors (field-level errors)
     */
    private Map<String, String> validationErrors;
}


