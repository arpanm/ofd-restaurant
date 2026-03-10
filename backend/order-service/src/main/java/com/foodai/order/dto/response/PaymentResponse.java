package com.foodai.order.dto.response;

import com.foodai.order.domain.model.PaymentMethod;
import com.foodai.order.domain.model.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Response DTO for payment.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payment response")
public class PaymentResponse {

    @Schema(description = "Payment ID")
    private String id;

    @Schema(description = "Order ID")
    private String orderId;

    @Schema(description = "User ID")
    private String userId;

    @Schema(description = "Payment amount")
    private BigDecimal amount;

    @Schema(description = "Currency")
    private String currency;

    @Schema(description = "Payment provider")
    private String paymentProvider;

    @Schema(description = "Gateway order ID")
    private String gatewayOrderId;

    @Schema(description = "Transaction ID")
    private String transactionId;

    @Schema(description = "Payment method")
    private PaymentMethod paymentMethod;

    @Schema(description = "Payment status")
    private PaymentStatus status;

    @Schema(description = "Failure reason if failed")
    private String failureReason;

    @Schema(description = "Number of attempts")
    private Integer attempts;

    @Schema(description = "Payment creation time")
    private Instant createdAt;

    @Schema(description = "Payment completion time")
    private Instant completedAt;
}

