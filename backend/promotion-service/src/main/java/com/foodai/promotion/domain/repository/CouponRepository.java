package com.foodai.promotion.domain.repository;

import com.foodai.promotion.domain.model.Coupon;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Coupon entities.
 *
 * @author FoodAI Team
 */
@Repository
public interface CouponRepository extends MongoRepository<Coupon, String> {

  /**
   * Finds a coupon by ID and not deleted.
   *
   * @param id the coupon ID
   * @return Optional containing the coupon
   */
  Optional<Coupon> findByIdAndDeletedFalse(String id);

  /**
   * Finds a coupon by code.
   *
   * @param code the coupon code
   * @return Optional containing the coupon
   */
  Optional<Coupon> findByCodeAndDeletedFalse(String code);

  /**
   * Finds a coupon by code, ignoring case.
   *
   * @param code the coupon code
   * @return Optional containing the coupon
   */
  Optional<Coupon> findByCodeIgnoreCaseAndDeletedFalse(String code);

  /**
   * Finds all non-deleted coupons with pagination.
   *
   * @param pageable pagination info
   * @return page of coupons
   */
  Page<Coupon> findByDeletedFalse(Pageable pageable);

  /**
   * Finds coupons by restaurant ID.
   *
   * @param restaurantId the restaurant ID
   * @param pageable pagination info
   * @return page of coupons
   */
  Page<Coupon> findByRestaurantIdAndDeletedFalse(String restaurantId, Pageable pageable);

  /**
   * Finds active coupons by restaurant ID.
   *
   * @param restaurantId the restaurant ID
   * @return list of active coupons
   */
  List<Coupon> findByRestaurantIdAndActiveAndDeletedFalse(String restaurantId, boolean active);

  /**
   * Finds valid coupons (active, not expired).
   *
   * @param restaurantId the restaurant ID
   * @param now current time
   * @return list of valid coupons
   */
  @Query(
      "{'deleted': false, 'active': true, "
          + "'$or': [{'restaurantId': ?0}, {'restaurantId': null}], "
          + "'$or': [{'validFrom': null}, {'validFrom': {'$lte': ?1}}], "
          + "'$or': [{'validUntil': null}, {'validUntil': {'$gte': ?1}}]}")
  List<Coupon> findValidCoupons(String restaurantId, Instant now);

  /**
   * Finds platform-wide active coupons.
   *
   * @param now current time
   * @return list of coupons
   */
  @Query(
      "{'deleted': false, 'active': true, 'restaurantId': null, "
          + "'$or': [{'validFrom': null}, {'validFrom': {'$lte': ?0}}], "
          + "'$or': [{'validUntil': null}, {'validUntil': {'$gte': ?0}}]}")
  List<Coupon> findPlatformWideCoupons(Instant now);

  /**
   * Finds coupons linked to a promotion.
   *
   * @param promotionId the promotion ID
   * @return list of coupons
   */
  List<Coupon> findByPromotionIdAndDeletedFalse(String promotionId);

  /**
   * Checks if a coupon code exists.
   *
   * @param code the coupon code
   * @return true if exists
   */
  boolean existsByCodeIgnoreCaseAndDeletedFalse(String code);

  /**
   * Finds expired coupons that need deactivation.
   *
   * @param now current time
   * @return list of expired coupons
   */
  @Query("{'deleted': false, 'active': true, 'validUntil': {'$lt': ?0}}")
  List<Coupon> findExpiredActiveCoupons(Instant now);

  /**
   * Counts coupons by restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return count
   */
  long countByRestaurantIdAndDeletedFalse(String restaurantId);

  /**
   * Counts active coupons by restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return count
   */
  long countByRestaurantIdAndActiveAndDeletedFalse(String restaurantId, boolean active);
}

