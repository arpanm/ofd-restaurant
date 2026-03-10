package com.foodai.orchestration.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Standard API response wrapper.
 *
 * @param <T> type of data
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard API response")
public class ApiResponse<T> {

    @Schema(description = "Whether the request was successful")
    private boolean success;

    @Schema(description = "Response data")
    private T data;

    @Schema(description = "Error details if failed")
    private ErrorDetails error;

    @Schema(description = "Response timestamp")
    @Builder.Default
    private Instant timestamp = Instant.now();

    @Schema(description = "Request path")
    private String path;

    @Schema(description = "Request ID for tracing")
    private String requestId;

    /**
     * Creates a successful response with data.
     *
     * @param data response data
     * @param <T> data type
     * @return successful response
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .timestamp(Instant.now())
            .build();
    }

    /**
     * Creates a successful response with data and request ID.
     *
     * @param data response data
     * @param requestId request ID
     * @param <T> data type
     * @return successful response
     */
    public static <T> ApiResponse<T> success(T data, String requestId) {
        return ApiResponse.<T>builder()
            .success(true)
            .data(data)
            .requestId(requestId)
            .timestamp(Instant.now())
            .build();
    }

    /**
     * Creates an error response.
     *
     * @param code error code
     * @param message error message
     * @param <T> data type
     * @return error response
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
            .success(false)
            .error(ErrorDetails.builder()
                .code(code)
                .message(message)
                .timestamp(Instant.now())
                .build())
            .timestamp(Instant.now())
            .build();
    }

    /**
     * Creates an error response with details.
     *
     * @param code error code
     * @param message error message
     * @param details validation details
     * @param <T> data type
     * @return error response
     */
    public static <T> ApiResponse<T> error(String code, String message, List<FieldError> details) {
        return ApiResponse.<T>builder()
            .success(false)
            .error(ErrorDetails.builder()
                .code(code)
                .message(message)
                .details(details)
                .timestamp(Instant.now())
                .build())
            .timestamp(Instant.now())
            .build();
    }

    /**
     * Error details structure.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Error details")
    public static class ErrorDetails {
        @Schema(description = "Error code")
        private String code;

        @Schema(description = "Error message")
        private String message;

        @Schema(description = "Field validation errors")
        private List<FieldError> details;

        @Schema(description = "Error timestamp")
        private Instant timestamp;

        @Schema(description = "Request path")
        private String path;

        @Schema(description = "Request ID")
        private String requestId;
    }

    /**
     * Field validation error.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Field validation error")
    public static class FieldError {
        @Schema(description = "Field name")
        private String field;

        @Schema(description = "Error message")
        private String message;

        @Schema(description = "Rejected value")
        private Object rejectedValue;

        @Schema(description = "Error code")
        private String code;
    }
}

