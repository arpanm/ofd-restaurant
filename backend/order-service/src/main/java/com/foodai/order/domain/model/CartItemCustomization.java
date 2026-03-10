package com.foodai.order.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Represents a customization applied to a cart item.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemCustomization {

    /**
     * Customization group ID.
     */
    private String customizationId;

    /**
     * Customization group name (e.g., "Size", "Toppings").
     */
    private String customizationName;

    /**
     * Selected option ID.
     */
    private String optionId;

    /**
     * Selected option name (e.g., "Large", "Extra Cheese").
     */
    private String optionName;

    /**
     * Additional cost for this customization.
     */
    private BigDecimal additionalCost;
}

