package com.foodai.promotion.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  @Schema(description = "Success flag")
  private boolean success;

  @Schema(description = "Response data")
  private T data;

  @Schema(description = "Error details")
  private ErrorDetails error;

  @Schema(description = "Response timestamp")
  @Builder.Default
  private Instant timestamp = Instant.now();

  /**
   * Creates a success response with data.
   *
   * @param data the data
   * @param <T> the type
   * @return the response
   */
  public static <T> ApiResponse<T> success(T data) {
    return ApiResponse.<T>builder().success(true).data(data).build();
  }

  /**
   * Creates a success response with data and message.
   *
   * @param data the data
   * @param message the message (ignored in response, but used for logging)
   * @param <T> the type
   * @return the response
   */
  public static <T> ApiResponse<T> success(T data, String message) {
    return ApiResponse.<T>builder().success(true).data(data).build();
  }

  /**
   * Creates an error response.
   *
   * @param code the error code
   * @param message the error message
   * @param <T> the type
   * @return the response
   */
  public static <T> ApiResponse<T> error(String code, String message) {
    return ApiResponse.<T>builder()
        .success(false)
        .error(ErrorDetails.builder().code(code).message(message).build())
        .build();
  }

  /**
   * Error details.
   *
   * @author FoodAI Team
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ErrorDetails {
    @Schema(description = "Error code")
    private String code;

    @Schema(description = "Error message")
    private String message;

    @Schema(description = "Additional details")
    private Object details;
  }
}

