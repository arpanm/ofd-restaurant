package com.foodai.promotion.domain.repository;

import com.foodai.promotion.domain.model.CouponUsage;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for CouponUsage entities.
 *
 * @author FoodAI Team
 */
@Repository
public interface CouponUsageRepository extends MongoRepository<CouponUsage, String> {

  /**
   * Finds usages by coupon ID.
   *
   * @param couponId the coupon ID
   * @param pageable pagination info
   * @return page of usages
   */
  Page<CouponUsage> findByCouponId(String couponId, Pageable pageable);

  /**
   * Finds usages by user ID.
   *
   * @param userId the user ID
   * @param pageable pagination info
   * @return page of usages
   */
  Page<CouponUsage> findByUserId(String userId, Pageable pageable);

  /**
   * Finds usages by coupon and user.
   *
   * @param couponId the coupon ID
   * @param userId the user ID
   * @return list of usages
   */
  List<CouponUsage> findByCouponIdAndUserId(String couponId, String userId);

  /**
   * Counts usages by coupon and user.
   *
   * @param couponId the coupon ID
   * @param userId the user ID
   * @return count
   */
  long countByCouponIdAndUserId(String couponId, String userId);

  /**
   * Counts usages by coupon.
   *
   * @param couponId the coupon ID
   * @return count
   */
  long countByCouponId(String couponId);

  /**
   * Finds usages within a date range.
   *
   * @param couponId the coupon ID
   * @param start start date
   * @param end end date
   * @return list of usages
   */
  List<CouponUsage> findByCouponIdAndUsedAtBetween(String couponId, Instant start, Instant end);

  /**
   * Finds usages by order ID.
   *
   * @param orderId the order ID
   * @return list of usages
   */
  List<CouponUsage> findByOrderId(String orderId);

  /**
   * Checks if a coupon was used on an order.
   *
   * @param couponId the coupon ID
   * @param orderId the order ID
   * @return true if used
   */
  boolean existsByCouponIdAndOrderId(String couponId, String orderId);

  /**
   * Gets total discount by coupon.
   *
   * @param couponId the coupon ID
   * @return aggregation result
   */
  @Aggregation(
      pipeline = {
        "{ $match: { couponId: ?0 } }",
        "{ $group: { _id: null, totalDiscount: { $sum: '$discountAmount' }, count: { $sum: 1 } } }"
      })
  List<CouponUsageStats> getCouponStats(String couponId);

  /**
   * Coupon usage statistics.
   *
   * @author FoodAI Team
   */
  interface CouponUsageStats {
    java.math.BigDecimal getTotalDiscount();

    int getCount();
  }
}

