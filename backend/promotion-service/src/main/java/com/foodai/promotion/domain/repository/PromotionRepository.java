package com.foodai.promotion.domain.repository;

import com.foodai.promotion.domain.model.Promotion;
import com.foodai.promotion.domain.model.PromotionStatus;
import com.foodai.promotion.domain.model.PromotionType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Promotion entities.
 *
 * @author FoodAI Team
 */
@Repository
public interface PromotionRepository extends MongoRepository<Promotion, String> {

  /**
   * Finds a non-deleted promotion by ID.
   *
   * @param id the promotion ID
   * @return Optional containing the promotion if found
   */
  Optional<Promotion> findByIdAndDeletedFalse(String id);

  /**
   * Finds a promotion by code.
   *
   * @param code the promotion code
   * @return Optional containing the promotion if found
   */
  Optional<Promotion> findByCodeAndDeletedFalse(String code);

  /**
   * Finds all non-deleted promotions with pagination.
   *
   * @param pageable pagination info
   * @return page of promotions
   */
  Page<Promotion> findByDeletedFalse(Pageable pageable);

  /**
   * Finds promotions by restaurant ID.
   *
   * @param restaurantId the restaurant ID
   * @param pageable pagination info
   * @return page of promotions
   */
  Page<Promotion> findByRestaurantIdAndDeletedFalse(String restaurantId, Pageable pageable);

  /**
   * Finds promotions by status.
   *
   * @param status the promotion status
   * @param pageable pagination info
   * @return page of promotions
   */
  Page<Promotion> findByStatusAndDeletedFalse(PromotionStatus status, Pageable pageable);

  /**
   * Finds promotions by restaurant ID and status.
   *
   * @param restaurantId the restaurant ID
   * @param status the promotion status
   * @param pageable pagination info
   * @return page of promotions
   */
  Page<Promotion> findByRestaurantIdAndStatusAndDeletedFalse(
      String restaurantId, PromotionStatus status, Pageable pageable);

  /**
   * Finds promotions by type.
   *
   * @param type the promotion type
   * @param pageable pagination info
   * @return page of promotions
   */
  Page<Promotion> findByTypeAndDeletedFalse(PromotionType type, Pageable pageable);

  /**
   * Finds active promotions that should be shown to customers.
   *
   * @param restaurantId the restaurant ID (nullable)
   * @param now current time
   * @return list of active promotions
   */
  @Query(
      "{'deleted': false, 'status': 'ACTIVE', "
          + "'$or': [{'restaurantId': ?0}, {'platformWide': true}], "
          + "'schedule.startDate': {'$lte': ?1}, "
          + "'$or': [{'schedule.endDate': null}, {'schedule.endDate': {'$gte': ?1}}]}")
  List<Promotion> findActivePromotionsForRestaurant(String restaurantId, Instant now);

  /**
   * Finds platform-wide promotions.
   *
   * @param status the status
   * @param pageable pagination info
   * @return page of promotions
   */
  Page<Promotion> findByPlatformWideTrueAndStatusAndDeletedFalse(
      PromotionStatus status, Pageable pageable);

  /**
   * Finds featured promotions.
   *
   * @return list of featured promotions
   */
  List<Promotion> findByFeaturedTrueAndStatusAndDeletedFalse(PromotionStatus status);

  /**
   * Finds promotions by campaign ID.
   *
   * @param campaignId the campaign ID
   * @return list of promotions
   */
  List<Promotion> findByCampaignIdAndDeletedFalse(String campaignId);

  /**
   * Finds promotions that need status update based on schedule.
   *
   * @param status current status
   * @param now current time
   * @return list of promotions needing update
   */
  @Query(
      "{'deleted': false, 'status': ?0, "
          + "'$or': [{'schedule.endDate': {'$lt': ?1}}, "
          + "{'schedule.startDate': {'$lte': ?1}, 'status': 'SCHEDULED'}]}")
  List<Promotion> findPromotionsNeedingStatusUpdate(PromotionStatus status, Instant now);

  /**
   * Counts promotions by restaurant and status.
   *
   * @param restaurantId the restaurant ID
   * @param status the status
   * @return count
   */
  long countByRestaurantIdAndStatusAndDeletedFalse(String restaurantId, PromotionStatus status);

  /**
   * Checks if a promotion code exists.
   *
   * @param code the promotion code
   * @return true if exists
   */
  boolean existsByCodeAndDeletedFalse(String code);
}

