package com.foodai.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for payment initiation.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payment initiation response")
public class PaymentInitiationResponse {

    @Schema(description = "Payment ID in our system")
    private String paymentId;

    @Schema(description = "Gateway order ID (e.g., Razorpay order_id)")
    private String gatewayOrderId;

    @Schema(description = "Payment amount")
    private BigDecimal amount;

    @Schema(description = "Currency")
    private String currency;

    @Schema(description = "Gateway key ID (for client-side)")
    private String gatewayKeyId;

    @Schema(description = "Order number for display")
    private String orderNumber;

    @Schema(description = "User name for prefill")
    private String userName;

    @Schema(description = "User email for prefill")
    private String userEmail;

    @Schema(description = "User phone for prefill")
    private String userPhone;
}

