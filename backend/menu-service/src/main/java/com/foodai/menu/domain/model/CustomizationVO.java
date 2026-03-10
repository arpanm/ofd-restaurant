package com.foodai.menu.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Value Object representing a customization group for a menu item.
 *
 * <p>Examples: "Spice Level", "Add-ons", "Size", "Toppings"
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomizationVO {

  /**
   * Name of the customization group.
   * Example: "Spice Level", "Size", "Add-ons"
   */
  private String name;

  /**
   * Available options for this customization.
   */
  private List<CustomizationOptionVO> options;

  /**
   * Whether this customization is required.
   */
  private boolean required;

  /**
   * Whether multiple options can be selected.
   */
  private boolean multiSelect;

  /**
   * Minimum number of selections (for multi-select).
   */
  private Integer minSelections;

  /**
   * Maximum number of selections (for multi-select).
   */
  private Integer maxSelections;

  /**
   * Validates the customization.
   *
   * @throws IllegalStateException if invalid
   */
  public void validate() {
    if (name == null || name.isBlank()) {
      throw new IllegalStateException("Customization name cannot be empty");
    }

    if (options == null || options.isEmpty()) {
      throw new IllegalStateException("Customization must have at least one option");
    }

    if (multiSelect) {
      if (minSelections != null && minSelections < 0) {
        throw new IllegalStateException("Minimum selections cannot be negative");
      }
      if (maxSelections != null && maxSelections < 1) {
        throw new IllegalStateException("Maximum selections must be at least 1");
      }
      if (minSelections != null && maxSelections != null && minSelections > maxSelections) {
        throw new IllegalStateException("Minimum selections cannot exceed maximum selections");
      }
    }

    options.forEach(CustomizationOptionVO::validate);
  }
}

