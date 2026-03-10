package com.foodai.order.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents an item in the shopping cart.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    /**
     * Unique identifier for this cart item.
     */
    @Builder.Default
    private String id = UUID.randomUUID().toString();

    /**
     * Menu item ID.
     */
    private String menuItemId;

    /**
     * Menu item name.
     */
    private String name;

    /**
     * Menu item description.
     */
    private String description;

    /**
     * Restaurant ID.
     */
    private String restaurantId;

    /**
     * Restaurant name.
     */
    private String restaurantName;

    /**
     * Item image URL.
     */
    private String imageUrl;

    /**
     * Unit price.
     */
    private BigDecimal unitPrice;

    /**
     * Quantity in cart.
     */
    @Builder.Default
    private Integer quantity = 1;

    /**
     * Total price (unitPrice * quantity + customizations).
     */
    private BigDecimal totalPrice;

    /**
     * Customizations applied.
     */
    @Builder.Default
    private List<CartItemCustomization> customizations = new ArrayList<>();

    /**
     * Special instructions for this item.
     */
    private String specialInstructions;

    /**
     * Whether item is vegetarian.
     */
    private Boolean vegetarian;

    /**
     * Spice level (0-5).
     */
    private Integer spiceLevel;

    /**
     * Preparation time in minutes.
     */
    private Integer preparationTime;

    /**
     * Calculates and updates the total price.
     */
    public void calculateTotalPrice() {
        BigDecimal baseTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal customizationTotal = BigDecimal.ZERO;
        
        if (customizations != null) {
            for (CartItemCustomization customization : customizations) {
                if (customization.getAdditionalCost() != null) {
                    customizationTotal = customizationTotal.add(
                        customization.getAdditionalCost().multiply(BigDecimal.valueOf(quantity))
                    );
                }
            }
        }
        
        this.totalPrice = baseTotal.add(customizationTotal);
    }

    /**
     * Gets the customization cost per unit.
     *
     * @return customization cost
     */
    public BigDecimal getCustomizationCost() {
        if (customizations == null || customizations.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        return customizations.stream()
            .map(c -> c.getAdditionalCost() != null ? c.getAdditionalCost() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

