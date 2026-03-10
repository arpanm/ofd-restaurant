package com.foodai.restaurant.domain.repository;

import com.foodai.restaurant.domain.model.RestaurantHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Repository for Restaurant history tracking.
 */
@Repository
public interface RestaurantHistoryRepository extends MongoRepository<RestaurantHistory, String> {
    
    /**
     * Find all history entries for a restaurant, ordered by change time (most recent first).
     */
    List<RestaurantHistory> findByRestaurantIdOrderByChangedAtDesc(String restaurantId);
    
    /**
     * Find history entries for a specific field of a restaurant.
     */
    List<RestaurantHistory> findByRestaurantIdAndFieldNameOrderByChangedAtDesc(
        String restaurantId, 
        String fieldName
    );
    
    /**
     * Find all changes made by a specific user.
     */
    List<RestaurantHistory> findByChangedByOrderByChangedAtDesc(String changedBy);
    
    /**
     * Find history entries within a date range.
     */
    @Query("{'restaurantId': ?0, 'changedAt': {$gte: ?1, $lte: ?2}}")
    List<RestaurantHistory> findByRestaurantIdAndDateRange(
        String restaurantId, 
        Instant fromDate, 
        Instant toDate
    );
    
    /**
     * Find recent changes (last N entries).
     */
    List<RestaurantHistory> findTop10ByRestaurantIdOrderByChangedAtDesc(String restaurantId);
}


