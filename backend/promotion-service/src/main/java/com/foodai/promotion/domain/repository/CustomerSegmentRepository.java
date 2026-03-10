package com.foodai.promotion.domain.repository;

import com.foodai.promotion.domain.model.CustomerSegment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for CustomerSegment entities.
 *
 * @author FoodAI Team
 */
@Repository
public interface CustomerSegmentRepository extends MongoRepository<CustomerSegment, String> {

  /**
   * Finds a segment by ID and not deleted.
   *
   * @param id the segment ID
   * @return Optional containing the segment
   */
  Optional<CustomerSegment> findByIdAndDeletedFalse(String id);

  /**
   * Finds all non-deleted segments with pagination.
   *
   * @param pageable pagination info
   * @return page of segments
   */
  Page<CustomerSegment> findByDeletedFalse(Pageable pageable);

  /**
   * Finds segments by restaurant ID.
   *
   * @param restaurantId the restaurant ID
   * @param pageable pagination info
   * @return page of segments
   */
  Page<CustomerSegment> findByRestaurantIdAndDeletedFalse(String restaurantId, Pageable pageable);

  /**
   * Finds active segments by restaurant ID.
   *
   * @param restaurantId the restaurant ID
   * @return list of active segments
   */
  List<CustomerSegment> findByRestaurantIdAndActiveAndDeletedFalse(
      String restaurantId, boolean active);

  /**
   * Finds platform-wide segments (no restaurant).
   *
   * @return list of platform segments
   */
  List<CustomerSegment> findByRestaurantIdIsNullAndActiveAndDeletedFalse(boolean active);

  /**
   * Finds AI-suggested segments.
   *
   * @param restaurantId the restaurant ID
   * @return list of AI-suggested segments
   */
  List<CustomerSegment> findByRestaurantIdAndAiSuggestedAndDeletedFalse(
      String restaurantId, boolean aiSuggested);

  /**
   * Finds custom segments by restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return list of custom segments
   */
  List<CustomerSegment> findByRestaurantIdAndCustomAndDeletedFalse(
      String restaurantId, boolean custom);

  /**
   * Checks if a segment name exists for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @param name the segment name
   * @return true if exists
   */
  boolean existsByRestaurantIdAndNameIgnoreCaseAndDeletedFalse(String restaurantId, String name);

  /**
   * Counts segments by restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return count
   */
  long countByRestaurantIdAndDeletedFalse(String restaurantId);

  /**
   * Finds all active segments.
   *
   * @return list of active segments
   */
  List<CustomerSegment> findByActiveTrue();

  /**
   * Finds active segments by restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return list of active segments
   */
  List<CustomerSegment> findByRestaurantIdAndActiveTrue(String restaurantId);

  /**
   * Finds segments containing a specific user ID in staticUserIds.
   *
   * @param userId the user ID
   * @return list of segments
   */
  List<CustomerSegment> findByStaticUserIdsContaining(String userId);
}

