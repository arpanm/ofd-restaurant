package com.foodai.restaurant.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Standardized API response wrapper.
 * All controller endpoints should return this wrapper for consistency.
 *
 * @param <T> Type of the data being returned
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    /**
     * Indicates if the request was successful
     */
    private boolean success;
    
    /**
     * Human-readable message
     */
    private String message;
    
    /**
     * Response data (null if error)
     */
    private T data;
    
    /**
     * Timestamp of the response
     */
    private Instant timestamp;
    
    /**
     * Create a successful response with data.
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .message("Success")
            .data(data)
            .timestamp(Instant.now())
            .build();
    }
    
    /**
     * Create a successful response with data and custom message.
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .timestamp(Instant.now())
            .build();
    }
    
    /**
     * Create an error response with message.
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .data(null)
            .timestamp(Instant.now())
            .build();
    }
}


