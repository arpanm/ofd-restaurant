package com.foodai.order.dto.request;

import com.foodai.order.domain.model.RefundType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for initiating a refund.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to initiate a refund")
public class InitiateRefundRequest {

    @NotBlank(message = "Order ID is required")
    @Schema(description = "Order ID", example = "order-123")
    private String orderId;

    @NotNull(message = "Refund type is required")
    @Schema(description = "Type of refund", example = "CANCELLATION")
    private RefundType refundType;

    @DecimalMin(value = "0.01", message = "Refund amount must be greater than 0")
    @Schema(description = "Refund amount (null for full refund)", example = "299.00")
    private BigDecimal amount;

    @NotBlank(message = "Reason is required")
    @Size(min = 10, max = 500, message = "Reason must be between 10 and 500 characters")
    @Schema(description = "Reason for refund", example = "Order was cancelled before preparation")
    private String reason;

    @NotBlank(message = "Initiated by is required")
    @Schema(description = "Who initiated the refund", example = "system")
    private String initiatedBy;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    @Schema(description = "Additional notes")
    private String notes;
}

