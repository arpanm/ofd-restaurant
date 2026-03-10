package com.foodai.menu.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Standard API error response structure.
 *
 * <p>Provides consistent error format across all API endpoints.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "API error response")
public class ApiErrorResponse {

  @Schema(description = "Error timestamp")
  private Instant timestamp;

  @Schema(description = "HTTP status code", example = "404")
  private int status;

  @Schema(description = "Error type", example = "Not Found")
  private String error;

  @Schema(description = "Error message", example = "Menu item not found with id: 123")
  private String message;

  @Schema(description = "Request path", example = "/api/v1/menu-items/123")
  private String path;

  @Schema(description = "Field-level validation errors")
  private Map<String, String> validationErrors;

  @Schema(description = "Trace ID for error tracking")
  private String traceId;
}

