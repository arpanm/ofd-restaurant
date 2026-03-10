package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value Object for rating-based penalty configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingPenaltyVO {
    /**
     * Whether rating penalties are enabled
     */
    private Boolean enabled;
    
    /**
     * Critical rating threshold (e.g., < 3.0)
     */
    private Double criticalRatingThreshold;
    
    /**
     * Warning rating threshold (e.g., < 3.5)
     */
    private Double warningRatingThreshold;
    
    /**
     * Penalty percentage for critical rating
     */
    private Double criticalPenaltyPercentage;
    
    /**
     * Fixed penalty amount for critical rating
     */
    private Double criticalPenaltyFixed;
    
    /**
     * Penalty percentage for warning rating
     */
    private Double warningPenaltyPercentage;
    
    /**
     * Fixed penalty amount for warning rating
     */
    private Double warningPenaltyFixed;
    
    /**
     * Suspension rating threshold (e.g., < 2.5)
     */
    private Double suspensionRatingThreshold;
    
    /**
     * Consecutive days below threshold before suspension
     */
    private Integer continuousPoorRatingDays;
}


