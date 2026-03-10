package com.foodai.order.dto.request;

import com.foodai.order.domain.model.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for initiating a payment.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to initiate payment")
public class InitiatePaymentRequest {

    @NotBlank(message = "Order ID is required")
    @Schema(description = "Order ID", example = "order-123")
    private String orderId;

    @NotBlank(message = "User ID is required")
    @Schema(description = "User ID", example = "user-123")
    private String userId;

    @NotNull(message = "Amount is required")
    @Schema(description = "Payment amount", example = "599.00")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    @Schema(description = "Payment method", example = "UPI")
    private PaymentMethod paymentMethod;

    @Schema(description = "IP address of the payer")
    private String ipAddress;

    @Schema(description = "User agent of the payer")
    private String userAgent;
}

