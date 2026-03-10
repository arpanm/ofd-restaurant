package com.foodai.order.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Generic API response wrapper.
 *
 * @param <T> the type of data
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Generic API response")
public class ApiResponse<T> {

    @Schema(description = "Whether the request was successful")
    private boolean success;

    @Schema(description = "Response message")
    private String message;

    @Schema(description = "Response data")
    private T data;

    @Schema(description = "Error details if any")
    private Object errors;

    @Schema(description = "Response timestamp")
    @Builder.Default
    private Instant timestamp = Instant.now();

    /**
     * Creates a success response with data.
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .message("Success")
            .data(data)
            .build();
    }

    /**
     * Creates a success response with message and data.
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
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .build();
    }

    /**
     * Creates an error response with details.
     */
    public static <T> ApiResponse<T> error(String message, Object errors) {
        return ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .errors(errors)
            .build();
    }
}

