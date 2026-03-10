package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * History tracking for Serviceability configuration changes.
 * Tracks pincode additions/removals and radius changes.
 */
@Document(collection = "serviceability_history")
@CompoundIndexes({
    @CompoundIndex(name = "serviceability_outlet_time", def = "{'outletId': 1, 'changedAt': -1}"),
    @CompoundIndex(name = "serviceability_restaurant_time", def = "{'restaurantId': 1, 'changedAt': -1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceabilityHistory {
    /**
     * Unique history entry ID
     */
    @Id
    private String id;
    
    /**
     * Reference to restaurant
     */
    @Indexed
    private String restaurantId;
    
    /**
     * Reference to outlet
     */
    @Indexed
    private String outletId;
    
    /**
     * Type of change (PINCODE_ADDED, PINCODE_REMOVED, RADIUS_CHANGED, RULES_UPDATED)
     */
    private String changeType;
    
    /**
     * Pincodes that were added
     */
    private List<String> addedPincodes;
    
    /**
     * Pincodes that were removed
     */
    private List<String> removedPincodes;
    
    /**
     * Old delivery radius
     */
    private Double oldRadius;
    
    /**
     * New delivery radius
     */
    private Double newRadius;
    
    /**
     * User ID who made the change
     */
    @Indexed
    private String changedBy;
    
    /**
     * Timestamp of change
     */
    @Indexed
    private Instant changedAt;
    
    /**
     * Reason for change
     */
    private String changeReason;
}


