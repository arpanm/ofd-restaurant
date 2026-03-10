package com.foodai.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for cancelling an order.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to cancel an order")
public class CancelOrderRequest {

    @NotBlank(message = "Cancellation reason is required")
    @Size(min = 10, max = 500, message = "Reason must be between 10 and 500 characters")
    @Schema(description = "Reason for cancellation", example = "Changed my mind about the order")
    private String reason;

    @NotBlank(message = "Cancelled by is required")
    @Schema(description = "Who is cancelling the order", example = "user-123")
    private String cancelledBy;
}

