package com.foodai.order.dto.response;

import com.foodai.order.domain.model.RefundStatus;
import com.foodai.order.domain.model.RefundType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Response DTO for refund.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Refund response")
public class RefundResponse {

    @Schema(description = "Refund ID")
    private String id;

    @Schema(description = "Payment ID")
    private String paymentId;

    @Schema(description = "Order ID")
    private String orderId;

    @Schema(description = "User ID")
    private String userId;

    @Schema(description = "Refund amount")
    private BigDecimal amount;

    @Schema(description = "Original payment amount")
    private BigDecimal originalAmount;

    @Schema(description = "Refund reason")
    private String reason;

    @Schema(description = "Refund type")
    private RefundType refundType;

    @Schema(description = "Gateway refund ID")
    private String gatewayRefundId;

    @Schema(description = "Refund status")
    private RefundStatus status;

    @Schema(description = "Who initiated the refund")
    private String initiatedBy;

    @Schema(description = "Failure reason if failed")
    private String failureReason;

    @Schema(description = "Additional notes")
    private String notes;

    @Schema(description = "Whether this is a full refund")
    private Boolean isFullRefund;

    @Schema(description = "Refund creation time")
    private Instant createdAt;

    @Schema(description = "Refund processing time")
    private Instant processedAt;
}

