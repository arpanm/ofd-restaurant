package com.foodai.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for adding an item to the cart.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to add an item to the cart")
public class AddCartItemRequest {

    @NotBlank(message = "Menu item ID is required")
    @Schema(description = "Menu item ID", example = "item-123")
    private String menuItemId;

    @NotBlank(message = "Item name is required")
    @Schema(description = "Menu item name", example = "Chicken Biryani")
    private String name;

    @Schema(description = "Item description")
    private String description;

    @NotBlank(message = "Restaurant ID is required")
    @Schema(description = "Restaurant ID", example = "rest-456")
    private String restaurantId;

    @NotBlank(message = "Restaurant name is required")
    @Schema(description = "Restaurant name", example = "Biryani Blues")
    private String restaurantName;

    @Schema(description = "Item image URL")
    private String imageUrl;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Schema(description = "Unit price", example = "299.00")
    private BigDecimal unitPrice;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 99, message = "Quantity cannot exceed 99")
    @Schema(description = "Quantity to add", example = "2")
    private Integer quantity;

    @Schema(description = "Customizations for the item")
    private List<CartItemCustomizationDTO> customizations;

    @Size(max = 500, message = "Special instructions cannot exceed 500 characters")
    @Schema(description = "Special instructions for this item", example = "Less spicy please")
    private String specialInstructions;

    @Schema(description = "Whether item is vegetarian")
    private Boolean vegetarian;

    @Min(value = 0, message = "Spice level must be between 0 and 5")
    @Max(value = 5, message = "Spice level must be between 0 and 5")
    @Schema(description = "Spice level (0-5)", example = "3")
    private Integer spiceLevel;

    @Schema(description = "Preparation time in minutes", example = "25")
    private Integer preparationTime;
}

