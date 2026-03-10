package com.foodai.promotion.domain.model;

import java.math.BigDecimal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value object representing customer segment criteria.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SegmentCriteriaVO {

  /** Minimum number of orders. */
  private Integer minOrderCount;

  /** Maximum number of orders. */
  private Integer maxOrderCount;

  /** Minimum total amount spent. */
  private BigDecimal minTotalSpent;

  /** Maximum total amount spent. */
  private BigDecimal maxTotalSpent;

  /** Minimum average order value. */
  private BigDecimal minAverageOrderValue;

  /** Maximum average order value. */
  private BigDecimal maxAverageOrderValue;

  /** Days since last order (recency). */
  private Integer daysSinceLastOrder;

  /** Favorite categories. */
  private Set<String> favoriteCategories;

  /** Favorite cuisines. */
  private Set<String> favoriteCuisines;

  /** Minimum age. */
  private Integer minAge;

  /** Maximum age. */
  private Integer maxAge;

  /** Location cities. */
  private Set<String> cities;

  /** Loyalty tier. */
  private Set<String> loyaltyTiers;

  /** Dietary preferences. */
  private Set<String> dietaryPreferences;

  /** User registration date range start. */
  private String registeredAfter;

  /** User registration date range end. */
  private String registeredBefore;

  /**
   * Checks if a user matches this segment criteria.
   *
   * @param orderCount user's order count
   * @param totalSpent user's total spent
   * @param daysSinceLast days since user's last order
   * @return true if user matches criteria, false otherwise
   */
  public boolean matchesUser(int orderCount, BigDecimal totalSpent, int daysSinceLast) {
    if (minOrderCount != null && orderCount < minOrderCount) {
      return false;
    }
    if (maxOrderCount != null && orderCount > maxOrderCount) {
      return false;
    }
    if (minTotalSpent != null && totalSpent.compareTo(minTotalSpent) < 0) {
      return false;
    }
    if (maxTotalSpent != null && totalSpent.compareTo(maxTotalSpent) > 0) {
      return false;
    }
    if (daysSinceLastOrder != null && daysSinceLast < daysSinceLastOrder) {
      return false;
    }
    return true;
  }
}

