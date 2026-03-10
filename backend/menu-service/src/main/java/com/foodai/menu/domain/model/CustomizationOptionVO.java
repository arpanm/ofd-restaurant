package com.foodai.menu.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Value Object representing a customization option.
 *
 * <p>Examples: "Mild" (for Spice Level), "Extra Cheese" (for Add-ons), "Large" (for Size)
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomizationOptionVO {

  /**
   * Name of the option.
   * Example: "Mild", "Medium", "Extra Spicy", "Extra Cheese"
   */
  private String name;

  /**
   * Description of the option (optional).
   */
  private String description;

  /**
   * Additional cost for selecting this option.
   * Zero if no additional cost.
   */
  private BigDecimal additionalCost;

  /**
   * Whether this option is currently available.
   */
  private boolean available;

  /**
   * Validates the customization option.
   *
   * @throws IllegalStateException if invalid
   */
  public void validate() {
    if (name == null || name.isBlank()) {
      throw new IllegalStateException("Option name cannot be empty");
    }

    if (additionalCost == null) {
      additionalCost = BigDecimal.ZERO;
    }

    if (additionalCost.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalStateException("Additional cost cannot be negative");
    }
  }
}

