package com.foodai.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * Standard API response wrapper.
 *
 * @param <T> the type of data in the response
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standard API response")
public class ApiResponse<T> {

    @Schema(description = "Whether the request was successful")
    private boolean success;

    @Schema(description = "Response message")
    private String message;

    @Schema(description = "Response data")
    private T data;

    @Schema(description = "Response timestamp")
    @Builder.Default
    private Instant timestamp = Instant.now();

    @Schema(description = "Request ID for tracing")
    @Builder.Default
    private String requestId = UUID.randomUUID().toString();

    /**
     * Creates a successful response with data.
     *
     * @param data the response data
     * @param <T>  the type of data
     * @return ApiResponse with success status
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .build();
    }

    /**
     * Creates a successful response with message and data.
     *
     * @param message the response message
     * @param data    the response data
     * @param <T>     the type of data
     * @return ApiResponse with success status
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .build();
    }

    /**
     * Creates an error response.
     *
     * @param message the error message
     * @param <T>     the type of data
     * @return ApiResponse with error status
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .build();
    }
}

