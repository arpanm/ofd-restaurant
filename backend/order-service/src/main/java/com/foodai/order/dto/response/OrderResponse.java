package com.foodai.order.dto.response;

import com.foodai.order.domain.model.OrderStatus;
import com.foodai.order.domain.model.OrderType;
import com.foodai.order.domain.model.PaymentMethod;
import com.foodai.order.domain.model.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Response DTO for order.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order response")
public class OrderResponse {

    @Schema(description = "Order ID")
    private String id;

    @Schema(description = "Order number", example = "ORD-20250115-001")
    private String orderNumber;

    @Schema(description = "User ID")
    private String userId;

    @Schema(description = "Order type")
    private OrderType orderType;

    @Schema(description = "Order status")
    private OrderStatus status;

    @Schema(description = "Order items")
    private List<OrderItemResponse> items;

    @Schema(description = "Delivery address")
    private DeliveryAddressResponse deliveryAddress;

    @Schema(description = "Order total breakdown")
    private OrderTotalResponse orderTotal;

    @Schema(description = "Payment method")
    private PaymentMethod paymentMethod;

    @Schema(description = "Payment status")
    private PaymentStatus paymentStatus;

    @Schema(description = "Order timeline")
    private List<OrderTimelineEntryResponse> timeline;

    @Schema(description = "Special instructions")
    private String specialInstructions;

    @Schema(description = "Delivery instructions")
    private String deliveryInstructions;

    @Schema(description = "Scheduled delivery time")
    private Instant scheduledDeliveryTime;

    @Schema(description = "Estimated delivery time")
    private Instant estimatedDeliveryTime;

    @Schema(description = "Actual delivery time")
    private Instant actualDeliveryTime;

    @Schema(description = "Rider ID")
    private String riderId;

    @Schema(description = "Rider name")
    private String riderName;

    @Schema(description = "Rider phone")
    private String riderPhone;

    @Schema(description = "Diet plan ID")
    private String dietPlanId;

    @Schema(description = "Party plan ID")
    private String partyPlanId;

    @Schema(description = "Cancellation reason")
    private String cancellationReason;

    @Schema(description = "Loyalty points earned")
    private Integer loyaltyPointsEarned;

    @Schema(description = "Customer rating")
    private Integer customerRating;

    @Schema(description = "Customer review")
    private String customerReview;

    @Schema(description = "Whether order has been reviewed")
    private Boolean reviewed;

    @Schema(description = "Distinct restaurant IDs")
    private List<String> restaurantIds;

    @Schema(description = "Whether this is a multi-restaurant order")
    private Boolean isMultiRestaurantOrder;

    @Schema(description = "Whether order can be cancelled")
    private Boolean canCancel;

    @Schema(description = "Whether order can be refunded")
    private Boolean canRefund;

    @Schema(description = "Order creation time")
    private Instant createdAt;

    @Schema(description = "Order last update time")
    private Instant updatedAt;
}

