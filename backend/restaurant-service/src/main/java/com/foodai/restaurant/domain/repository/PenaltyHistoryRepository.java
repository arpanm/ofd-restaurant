package com.foodai.restaurant.domain.repository;

import com.foodai.restaurant.domain.model.PenaltyHistory;
import com.foodai.restaurant.domain.model.PenaltyStatus;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Repository for Penalty history tracking.
 * Tracks all penalties, disputes, waivers, and resolutions.
 */
@Repository
public interface PenaltyHistoryRepository extends MongoRepository<PenaltyHistory, String> {
    
    /**
     * Find all penalties for a restaurant.
     */
    List<PenaltyHistory> findByRestaurantIdOrderByAppliedAtDesc(String restaurantId);
    
    /**
     * Find penalties for a specific outlet.
     */
    List<PenaltyHistory> findByOutletIdOrderByAppliedAtDesc(String outletId);
    
    /**
     * Find penalties by status.
     */
    List<PenaltyHistory> findByStatusOrderByAppliedAtDesc(PenaltyStatus status);
    
    /**
     * Find penalties for a restaurant with a specific status.
     */
    List<PenaltyHistory> findByRestaurantIdAndStatus(String restaurantId, PenaltyStatus status);
    
    /**
     * Find penalties within a date range.
     */
    @Query("{'restaurantId': ?0, 'appliedAt': {$gte: ?1, $lte: ?2}}")
    List<PenaltyHistory> findByRestaurantIdAndDateRange(
        String restaurantId, 
        Instant fromDate, 
        Instant toDate
    );
    
    /**
     * Find penalties by type.
     */
    List<PenaltyHistory> findByPenaltyTypeOrderByAppliedAtDesc(String penaltyType);
    
    /**
     * Get penalty summary by type for a restaurant within date range.
     * Returns aggregated data grouped by penalty type.
     */
    @Query(value = "{ 'restaurantId': ?0, 'status': 'APPLIED', 'appliedAt': {$gte: ?1, $lte: ?2} }")
    List<PenaltyHistory> findAppliedPenaltiesInRange(
        String restaurantId, 
        Instant fromDate, 
        Instant toDate
    );
}

