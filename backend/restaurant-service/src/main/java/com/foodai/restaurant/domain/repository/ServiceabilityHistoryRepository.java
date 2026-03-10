package com.foodai.restaurant.domain.repository;

import com.foodai.restaurant.domain.model.ServiceabilityHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Serviceability history tracking.
 * Tracks pincode additions/removals and radius changes.
 */
@Repository
public interface ServiceabilityHistoryRepository extends MongoRepository<ServiceabilityHistory, String> {
    
    /**
     * Find serviceability history for an outlet.
     */
    List<ServiceabilityHistory> findByOutletIdOrderByChangedAtDesc(String outletId);
    
    /**
     * Find serviceability history for all outlets of a restaurant.
     */
    List<ServiceabilityHistory> findByRestaurantIdOrderByChangedAtDesc(String restaurantId);
    
    /**
     * Find recent serviceability changes (last 10).
     */
    List<ServiceabilityHistory> findTop10ByOutletIdOrderByChangedAtDesc(String outletId);
    
    /**
     * Find changes by type.
     */
    List<ServiceabilityHistory> findByChangeTypeOrderByChangedAtDesc(String changeType);
}


