package com.foodai.menu.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Value Object representing nutritional information for a menu item.
 *
 * <p>All values are per serving.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionalInfoVO {

  /**
   * Total calories per serving.
   */
  private Integer calories;

  /**
   * Protein content in grams.
   */
  private BigDecimal protein;

  /**
   * Carbohydrates content in grams.
   */
  private BigDecimal carbohydrates;

  /**
   * Fat content in grams.
   */
  private BigDecimal fat;

  /**
   * Fiber content in grams.
   */
  private BigDecimal fiber;

  /**
   * Sugar content in grams.
   */
  private BigDecimal sugar;

  /**
   * Sodium content in milligrams.
   */
  private Integer sodium;

  /**
   * Saturated fat content in grams.
   */
  private BigDecimal saturatedFat;

  /**
   * Trans fat content in grams.
   */
  private BigDecimal transFat;

  /**
   * Cholesterol content in milligrams.
   */
  private Integer cholesterol;

  /**
   * Validates nutritional information.
   *
   * @throws IllegalArgumentException if values are negative
   */
  public void validate() {
    if (calories != null && calories < 0) {
      throw new IllegalArgumentException("Calories cannot be negative");
    }
    if (protein != null && protein.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Protein cannot be negative");
    }
    if (carbohydrates != null && carbohydrates.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Carbohydrates cannot be negative");
    }
    if (fat != null && fat.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalArgumentException("Fat cannot be negative");
    }
  }
}

