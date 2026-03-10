package com.foodai.promotion.domain.repository;

import com.foodai.promotion.domain.model.Campaign;
import com.foodai.promotion.domain.model.CampaignStatus;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Campaign entities.
 *
 * @author FoodAI Team
 */
@Repository
public interface CampaignRepository extends MongoRepository<Campaign, String> {

  /**
   * Finds a campaign by ID and not deleted.
   *
   * @param id the campaign ID
   * @return Optional containing the campaign
   */
  Optional<Campaign> findByIdAndDeletedFalse(String id);

  /**
   * Finds all non-deleted campaigns with pagination.
   *
   * @param pageable pagination info
   * @return page of campaigns
   */
  Page<Campaign> findByDeletedFalse(Pageable pageable);

  /**
   * Finds campaigns by restaurant ID.
   *
   * @param restaurantId the restaurant ID
   * @param pageable pagination info
   * @return page of campaigns
   */
  Page<Campaign> findByRestaurantIdAndDeletedFalse(String restaurantId, Pageable pageable);

  /**
   * Finds campaigns by status.
   *
   * @param status the campaign status
   * @param pageable pagination info
   * @return page of campaigns
   */
  Page<Campaign> findByStatusAndDeletedFalse(CampaignStatus status, Pageable pageable);

  /**
   * Finds campaigns by restaurant and status.
   *
   * @param restaurantId the restaurant ID
   * @param status the status
   * @param pageable pagination info
   * @return page of campaigns
   */
  Page<Campaign> findByRestaurantIdAndStatusAndDeletedFalse(
      String restaurantId, CampaignStatus status, Pageable pageable);

  /**
   * Finds active campaigns for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return list of active campaigns
   */
  List<Campaign> findByRestaurantIdAndStatusAndDeletedFalse(
      String restaurantId, CampaignStatus status);

  /**
   * Finds scheduled campaigns that should be activated.
   *
   * @param now current time
   * @return list of campaigns to activate
   */
  @Query(
      "{'deleted': false, 'status': 'SCHEDULED', "
          + "'schedule.startDate': {'$lte': ?0}}")
  List<Campaign> findScheduledCampaignsToActivate(Instant now);

  /**
   * Finds active campaigns that should be completed.
   *
   * @param now current time
   * @return list of campaigns to complete
   */
  @Query("{'deleted': false, 'status': 'ACTIVE', " + "'schedule.endDate': {'$lt': ?0}}")
  List<Campaign> findActiveCampaignsToComplete(Instant now);

  /**
   * Counts campaigns by restaurant and status.
   *
   * @param restaurantId the restaurant ID
   * @param status the status
   * @return count
   */
  long countByRestaurantIdAndStatusAndDeletedFalse(String restaurantId, CampaignStatus status);

  /**
   * Finds campaigns containing a segment.
   *
   * @param segmentId the segment ID
   * @return list of campaigns
   */
  @Query("{'deleted': false, 'segmentIds': ?0}")
  List<Campaign> findCampaignsUsingSegment(String segmentId);

  /**
   * Finds active campaigns for a restaurant at a given time.
   *
   * @param restaurantId the restaurant ID
   * @param now the current time
   * @return list of active campaigns
   */
  @Query(
      "{'deleted': false, 'restaurantId': ?0, 'status': 'ACTIVE', "
          + "'schedule.startTime': {'$lte': ?1}, 'schedule.endTime': {'$gte': ?1}}")
  List<Campaign> findActiveCampaigns(String restaurantId, Instant now);

  /**
   * Finds campaigns by status and schedule start time before given time.
   *
   * @param status the status
   * @param time the time
   * @return list of campaigns
   */
  @Query("{'deleted': false, 'status': ?0, 'schedule.startTime': {'$lte': ?1}}")
  List<Campaign> findByStatusAndScheduleStartTimeBefore(CampaignStatus status, Instant time);

  /**
   * Finds campaigns by status and schedule end time before given time.
   *
   * @param status the status
   * @param time the time
   * @return list of campaigns
   */
  @Query("{'deleted': false, 'status': ?0, 'schedule.endTime': {'$lt': ?1}}")
  List<Campaign> findByStatusAndScheduleEndTimeBefore(CampaignStatus status, Instant time);
}

