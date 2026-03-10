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

/**
 * History tracking for Penalties applied to restaurants.
 * Tracks all penalties, disputes, waivers, and resolutions.
 */
@Document(collection = "penalty_history")
@CompoundIndexes({
    @CompoundIndex(name = "penalty_restaurant_time", def = "{'restaurantId': 1, 'appliedAt': -1}"),
    @CompoundIndex(name = "penalty_status", def = "{'status': 1, 'appliedAt': -1}"),
    @CompoundIndex(name = "penalty_type_time", def = "{'penaltyType': 1, 'appliedAt': -1}"),
    @CompoundIndex(name = "penalty_restaurant_status", def = "{'restaurantId': 1, 'status': 1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyHistory {
    /**
     * Unique penalty entry ID
     */
    @Id
    private String id;
    
    /**
     * Reference to restaurant
     */
    @Indexed
    private String restaurantId;
    
    /**
     * Reference to outlet (if outlet-specific penalty)
     */
    @Indexed
    private String outletId;
    
    /**
     * Reference to order (if order-related penalty)
     */
    @Indexed
    private String orderId;
    
    /**
     * Type of penalty (RATING, REVIEW, CANCELLATION, PRICING, QUALITY, DELAY, etc.)
     */
    @Indexed
    private String penaltyType;
    
    /**
     * Specific violation type
     */
    private String violationType;
    
    /**
     * Penalty amount charged
     */
    private Double penaltyAmount;
    
    /**
     * Description of penalty
     */
    private String description;
    
    /**
     * Status of penalty
     */
    @Indexed
    private PenaltyStatus status;
    
    /**
     * Operations team member who applied penalty
     */
    @Indexed
    private String appliedBy;
    
    /**
     * Timestamp when penalty was applied
     */
    @Indexed
    private Instant appliedAt;
    
    /**
     * User ID who waived the penalty (if waived)
     */
    private String waivedBy;
    
    /**
     * Timestamp when penalty was waived
     */
    private Instant waivedAt;
    
    /**
     * Reason for waiving penalty
     */
    private String waiverReason;
    
    /**
     * Reason provided by restaurant for dispute
     */
    private String disputeReason;
}


