package com.foodai.order.dto.response;

import com.foodai.order.domain.model.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Response DTO for cart.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cart response")
public class CartResponse {

    @Schema(description = "Cart ID")
    private String id;

    @Schema(description = "User ID")
    private String userId;

    @Schema(description = "Cart items")
    private List<CartItemResponse> items;

    @Schema(description = "Applied coupon code")
    private String appliedCouponCode;

    @Schema(description = "Coupon discount amount")
    private BigDecimal couponDiscount;

    @Schema(description = "Selected address ID")
    private String selectedAddressId;

    @Schema(description = "Selected payment method")
    private PaymentMethod selectedPaymentMethod;

    @Schema(description = "Special instructions")
    private String specialInstructions;

    @Schema(description = "Scheduled delivery time")
    private Instant scheduledDeliveryTime;

    @Schema(description = "Tip amount")
    private BigDecimal tip;

    @Schema(description = "Cart subtotal")
    private BigDecimal subtotal;

    @Schema(description = "Total item count")
    private Integer totalItemCount;

    @Schema(description = "Distinct restaurant IDs")
    private List<String> restaurantIds;

    @Schema(description = "Whether cart has items from multiple restaurants")
    private Boolean isMultiRestaurant;

    @Schema(description = "Cart creation time")
    private Instant createdAt;

    @Schema(description = "Cart last update time")
    private Instant updatedAt;
}

