package com.foodai.restaurant.domain.repository;

import com.foodai.restaurant.domain.model.OutletHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Outlet history tracking.
 */
@Repository
public interface OutletHistoryRepository extends MongoRepository<OutletHistory, String> {
    
    /**
     * Find all history entries for an outlet, ordered by change time.
     */
    List<OutletHistory> findByOutletIdOrderByChangedAtDesc(String outletId);
    
    /**
     * Find history entries for a specific field of an outlet.
     */
    List<OutletHistory> findByOutletIdAndFieldNameOrderByChangedAtDesc(
        String outletId, 
        String fieldName
    );
    
    /**
     * Find all outlet history for a restaurant.
     */
    List<OutletHistory> findByRestaurantIdOrderByChangedAtDesc(String restaurantId);
    
    /**
     * Find history for a specific outlet within a restaurant.
     */
    List<OutletHistory> findByRestaurantIdAndOutletIdOrderByChangedAtDesc(
        String restaurantId, 
        String outletId
    );
    
    /**
     * Find recent changes (last 10).
     */
    List<OutletHistory> findTop10ByOutletIdOrderByChangedAtDesc(String outletId);
}


