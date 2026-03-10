package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value Object for review-based penalty configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewPenaltyVO {
    /**
     * Whether review penalties are enabled
     */
    private Boolean enabled;
    
    /**
     * Penalty per negative review
     */
    private Double negativeReviewPenaltyAmount;
    
    /**
     * Threshold count of negative reviews before action
     */
    private Integer negativeReviewThresholdCount;
    
    /**
     * Threshold period in days
     */
    private Integer negativeReviewThresholdDays;
    
    /**
     * Fixed penalty amount when threshold exceeded
     */
    private Double penaltyAmount;
    
    /**
     * Penalty as percentage of monthly revenue
     */
    private Double penaltyPercentage;
}


