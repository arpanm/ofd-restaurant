package com.foodai.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for verifying a payment.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to verify payment")
public class VerifyPaymentRequest {

    @NotBlank(message = "Gateway order ID is required")
    @Schema(description = "Payment gateway order ID", example = "order_ABC123")
    private String gatewayOrderId;

    @NotBlank(message = "Payment ID is required")
    @Schema(description = "Payment gateway payment ID", example = "pay_XYZ789")
    private String paymentId;

    @NotBlank(message = "Signature is required")
    @Schema(description = "Payment gateway signature for verification")
    private String signature;
}

