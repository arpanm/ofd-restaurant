package com.foodai.order.domain.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Value object representing a customization applied to an order item.
 *
 * @author FoodAI Team
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemCustomizationVO {

    /**
     * Customization group ID (e.g., "size", "toppings").
     */
    private String customizationId;

    /**
     * Customization group name.
     */
    private String customizationName;

    /**
     * Selected option ID.
     */
    private String optionId;

    /**
     * Selected option name (e.g., "Extra Cheese", "No Onions").
     */
    private String optionName;

    /**
     * Additional cost for this customization.
     */
    private BigDecimal additionalCost;
}

