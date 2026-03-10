package com.foodai.promotion.domain.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value object representing discount details.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountVO {

  /** Type of discount (percentage or fixed). */
  private PromotionType type;

  /** Discount value (percentage or fixed amount). */
  private BigDecimal value;

  /** Minimum order value to qualify for discount. */
  private BigDecimal minOrderValue;

  /** Maximum discount cap (for percentage discounts). */
  private BigDecimal maxDiscount;

  /**
   * Calculates the discount amount for a given subtotal.
   *
   * @param subtotal the order subtotal
   * @return the calculated discount amount
   */
  public BigDecimal calculateDiscount(BigDecimal subtotal) {
    if (minOrderValue != null && subtotal.compareTo(minOrderValue) < 0) {
      return BigDecimal.ZERO;
    }

    BigDecimal discount;
    if (type == PromotionType.PERCENTAGE) {
      discount = subtotal.multiply(value).divide(BigDecimal.valueOf(100));
      if (maxDiscount != null && discount.compareTo(maxDiscount) > 0) {
        discount = maxDiscount;
      }
    } else {
      discount = value;
    }

    return discount.min(subtotal);
  }
}

