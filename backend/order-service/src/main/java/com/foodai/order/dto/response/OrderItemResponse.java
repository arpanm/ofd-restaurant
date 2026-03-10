package com.foodai.order.dto.response;

import com.foodai.order.domain.model.OrderItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO for order item.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order item response")
public class OrderItemResponse {

    @Schema(description = "Order item ID")
    private String id;

    @Schema(description = "Menu item ID")
    private String menuItemId;

    @Schema(description = "Item name")
    private String name;

    @Schema(description = "Item description")
    private String description;

    @Schema(description = "Restaurant ID")
    private String restaurantId;

    @Schema(description = "Restaurant name")
    private String restaurantName;

    @Schema(description = "Item image URL")
    private String imageUrl;

    @Schema(description = "Unit price at time of order")
    private BigDecimal unitPrice;

    @Schema(description = "Quantity ordered")
    private Integer quantity;

    @Schema(description = "Total price for this item")
    private BigDecimal totalPrice;

    @Schema(description = "Customizations applied")
    private List<OrderItemCustomizationResponse> customizations;

    @Schema(description = "Special instructions for this item")
    private String specialInstructions;

    @Schema(description = "Whether item is vegetarian")
    private Boolean vegetarian;

    @Schema(description = "Spice level (0-5)")
    private Integer spiceLevel;

    @Schema(description = "Item status")
    private OrderItemStatus itemStatus;
}

