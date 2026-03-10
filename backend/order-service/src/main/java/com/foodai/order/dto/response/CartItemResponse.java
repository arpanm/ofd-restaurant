package com.foodai.order.dto.response;

import com.foodai.order.dto.request.CartItemCustomizationDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Response DTO for cart item.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Cart item response")
public class CartItemResponse {

    @Schema(description = "Cart item ID")
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

    @Schema(description = "Unit price")
    private BigDecimal unitPrice;

    @Schema(description = "Quantity")
    private Integer quantity;

    @Schema(description = "Total price")
    private BigDecimal totalPrice;

    @Schema(description = "Customizations")
    private List<CartItemCustomizationDTO> customizations;

    @Schema(description = "Special instructions")
    private String specialInstructions;

    @Schema(description = "Whether item is vegetarian")
    private Boolean vegetarian;

    @Schema(description = "Spice level (0-5)")
    private Integer spiceLevel;

    @Schema(description = "Preparation time in minutes")
    private Integer preparationTime;
}

