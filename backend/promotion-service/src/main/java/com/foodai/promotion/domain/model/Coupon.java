package com.foodai.promotion.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Domain entity representing a Coupon.
 *
 * <p>Coupons are specific discount codes that customers can apply at checkout. They may be linked
 * to a promotion or be standalone.
 *
 * @author FoodAI Team
 */
@Document(collection = "coupons")
@CompoundIndex(name = "idx_code_active", def = "{'code': 1, 'active': 1}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

  /** Unique identifier. */
  @Id private String id;

  /** Coupon code (unique, uppercase). */
  @Indexed(unique = true)
  private String code;

  /** Display name. */
  private String name;

  /** Description for customers. */
  private String description;

  /** Type of discount. */
  private PromotionType type;

  /** Discount value (percentage or fixed amount). */
  private BigDecimal value;

  /** Discount value (alias for compatibility). */
  private BigDecimal discountValue;

  /** Discount type (alias for compatibility). */
  private PromotionType discountType;

  /** Minimum order value to use coupon. */
  private BigDecimal minOrderValue;

  /** Maximum discount cap. */
  private BigDecimal maxDiscount;

  /** Valid from date. */
  private Instant validFrom;

  /** Valid until date. */
  private Instant validUntil;

  /** Total usage limit. */
  private Integer totalLimit;

  /** Per user limit. */
  private Integer perUserLimit;

  /** Current usage count. */
  @Builder.Default private int currentUsage = 0;

  /** Usage count (alias). */
  @Builder.Default private int usageCount = 0;

  /** Usage limit. */
  private Integer usageLimit;

  /** User IDs who have used this coupon. */
  @Builder.Default private Set<String> usedByUsers = new HashSet<>();

  /** Linked promotion ID. */
  private String promotionId;

  /** Restaurant ID (null for platform-wide). */
  @Indexed private String restaurantId;

  /** Whether coupon is active. */
  @Indexed @Builder.Default private boolean active = true;

  /** Whether coupon is for first order only. */
  private boolean firstOrderOnly;

  /** Whether coupon is for new users only. */
  private boolean newUsersOnly;

  /** Applicable restaurant IDs (empty = all). */
  private Set<String> applicableRestaurants;

  /** Applicable category names (empty = all). */
  private Set<String> applicableCategories;

  /** Whether coupon is deleted. */
  private boolean deleted;

  /** Creation timestamp. */
  @CreatedDate private Instant createdAt;

  /** Last update timestamp. */
  @LastModifiedDate private Instant updatedAt;

  /** Created by user ID. */
  private String createdBy;

  /**
   * Validates the coupon for use.
   *
   * @param userId the user attempting to use the coupon
   * @param orderValue the order value
   * @param isFirstOrder whether this is the user's first order
   * @param isNewUser whether this is a new user
   * @return true if coupon can be used, false otherwise
   */
  public boolean canBeUsed(
      String userId, BigDecimal orderValue, boolean isFirstOrder, boolean isNewUser) {
    if (!active || deleted) {
      return false;
    }
    Instant now = Instant.now();
    if (validFrom != null && now.isBefore(validFrom)) {
      return false;
    }
    if (validUntil != null && now.isAfter(validUntil)) {
      return false;
    }
    if (minOrderValue != null && orderValue.compareTo(minOrderValue) < 0) {
      return false;
    }
    if (totalLimit != null && currentUsage >= totalLimit) {
      return false;
    }
    if (perUserLimit != null && getUserUsageCount(userId) >= perUserLimit) {
      return false;
    }
    if (firstOrderOnly && !isFirstOrder) {
      return false;
    }
    if (newUsersOnly && !isNewUser) {
      return false;
    }
    return true;
  }

  /**
   * Calculates the discount for a given order value.
   *
   * @param orderValue the order value
   * @return the discount amount
   */
  public BigDecimal calculateDiscount(BigDecimal orderValue) {
    if (minOrderValue != null && orderValue.compareTo(minOrderValue) < 0) {
      return BigDecimal.ZERO;
    }

    BigDecimal discountVal = value != null ? value : discountValue;
    PromotionType discountTyp = type != null ? type : discountType;
    
    if (discountVal == null) {
      return BigDecimal.ZERO;
    }

    BigDecimal discount;
    if (discountTyp == PromotionType.PERCENTAGE) {
      discount = orderValue.multiply(discountVal).divide(BigDecimal.valueOf(100));
      if (maxDiscount != null && discount.compareTo(maxDiscount) > 0) {
        discount = maxDiscount;
      }
    } else {
      discount = discountVal;
    }

    return discount.min(orderValue);
  }

  /**
   * Records usage of the coupon by a user.
   *
   * @param userId the user ID
   */
  public void recordUsage(String userId) {
    int limit = usageLimit != null ? usageLimit : (totalLimit != null ? totalLimit : Integer.MAX_VALUE);
    if (currentUsage >= limit) {
      throw new IllegalStateException("Coupon usage limit exceeded");
    }
    currentUsage++;
    usageCount++;
    if (usedByUsers == null) {
      usedByUsers = new HashSet<>();
    }
    usedByUsers.add(userId);
  }

  /**
   * Gets the usage count for a specific user.
   *
   * @param userId the user ID
   * @return the usage count
   */
  public int getUserUsageCount(String userId) {
    if (usedByUsers == null) {
      return 0;
    }
    return usedByUsers.contains(userId) ? 1 : 0;
  }

  /** Deactivates the coupon. */
  public void deactivate() {
    this.active = false;
  }

  /** Activates the coupon. */
  public void activate() {
    this.active = true;
  }

  /** Soft deletes the coupon. */
  public void delete() {
    this.deleted = true;
    this.active = false;
  }

  /**
   * Checks if the coupon has expired.
   *
   * @return true if expired, false otherwise
   */
  public boolean isExpired() {
    return validUntil != null && Instant.now().isAfter(validUntil);
  }

  /**
   * Gets the remaining usage count.
   *
   * @return remaining usage, or null if unlimited
   */
  public Integer getRemainingUsage() {
    if (totalLimit == null) {
      return null;
    }
    return Math.max(0, totalLimit - currentUsage);
  }
}

