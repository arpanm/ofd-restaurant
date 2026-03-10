package com.foodai.order.dto.request;

import com.foodai.order.domain.model.OrderType;
import com.foodai.order.domain.model.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Request DTO for creating an order from cart.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create an order")
public class CreateOrderRequest {

    @NotBlank(message = "User ID is required")
    @Schema(description = "User ID placing the order", example = "user-123")
    private String userId;

    @NotNull(message = "Order type is required")
    @Schema(description = "Type of order", example = "SINGLE_RESTAURANT")
    private OrderType orderType;

    @NotNull(message = "Delivery address is required")
    @Valid
    @Schema(description = "Delivery address")
    private DeliveryAddressDTO deliveryAddress;

    @NotNull(message = "Payment method is required")
    @Schema(description = "Payment method", example = "UPI")
    private PaymentMethod paymentMethod;

    @Size(max = 1000, message = "Special instructions cannot exceed 1000 characters")
    @Schema(description = "Special instructions for the order")
    private String specialInstructions;

    @Size(max = 500, message = "Delivery instructions cannot exceed 500 characters")
    @Schema(description = "Delivery instructions")
    private String deliveryInstructions;

    @Schema(description = "Scheduled delivery time (null for ASAP)")
    private Instant scheduledDeliveryTime;

    @Schema(description = "Applied coupon code")
    private String couponCode;

    @Schema(description = "Coupon discount amount")
    private BigDecimal couponDiscount;

    @Schema(description = "Tip amount for delivery partner", example = "20.00")
    private BigDecimal tip;

    @Schema(description = "Loyalty points to redeem", example = "100")
    private Integer loyaltyPointsToRedeem;

    @Schema(description = "Diet plan ID if ordering from diet planner")
    private String dietPlanId;

    @Schema(description = "Party plan ID if ordering for a party")
    private String partyPlanId;

    @Schema(description = "AI chat session ID if ordering via chat")
    private String chatSessionId;

    @Schema(description = "Original order ID if this is a reorder")
    private String reorderFromOrderId;
}

