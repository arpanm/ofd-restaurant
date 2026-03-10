package com.foodai.promotion.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Domain entity tracking coupon usage by users.
 *
 * @author FoodAI Team
 */
@Document(collection = "coupon_usages")
@CompoundIndex(name = "idx_coupon_user", def = "{'couponId': 1, 'userId': 1}")
@CompoundIndex(name = "idx_user_date", def = "{'userId': 1, 'usedAt': -1}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponUsage {

  /** Unique identifier. */
  @Id private String id;

  /** Coupon ID that was used. */
  @Indexed private String couponId;

  /** Coupon code that was used. */
  private String couponCode;

  /** User ID who used the coupon. */
  @Indexed private String userId;

  /** Order ID where coupon was applied. */
  @Indexed private String orderId;

  /** Order value before discount. */
  private BigDecimal orderValue;

  /** Discount amount applied. */
  private BigDecimal discountAmount;

  /** Restaurant ID where coupon was used. */
  private String restaurantId;

  /** Timestamp when coupon was used. */
  @CreatedDate private Instant usedAt;

  /**
   * Creates a usage record.
   *
   * @param coupon the coupon used
   * @param userId the user ID
   * @param orderId the order ID
   * @param orderValue the order value
   * @param discountAmount the discount applied
   * @return a new CouponUsage
   */
  public static CouponUsage create(
      Coupon coupon,
      String userId,
      String orderId,
      BigDecimal orderValue,
      BigDecimal discountAmount) {
    return CouponUsage.builder()
        .couponId(coupon.getId())
        .couponCode(coupon.getCode())
        .userId(userId)
        .orderId(orderId)
        .orderValue(orderValue)
        .discountAmount(discountAmount)
        .restaurantId(coupon.getRestaurantId())
        .build();
  }
}

