package com.foodai.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for order total breakdown.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order total breakdown response")
public class OrderTotalResponse {

    @Schema(description = "Subtotal before taxes and fees")
    private BigDecimal subtotal;

    @Schema(description = "Delivery fee")
    private BigDecimal deliveryFee;

    @Schema(description = "Platform fee")
    private BigDecimal platformFee;

    @Schema(description = "Packaging charges")
    private BigDecimal packagingCharges;

    @Schema(description = "GST amount")
    private BigDecimal gst;

    @Schema(description = "GST percentage")
    private BigDecimal gstPercentage;

    @Schema(description = "Discount amount")
    private BigDecimal discount;

    @Schema(description = "Applied coupon code")
    private String couponCode;

    @Schema(description = "Loyalty points value used")
    private BigDecimal loyaltyPointsValue;

    @Schema(description = "Tip amount")
    private BigDecimal tip;

    @Schema(description = "Final total amount")
    private BigDecimal total;

    @Schema(description = "Currency code")
    private String currency;
}

