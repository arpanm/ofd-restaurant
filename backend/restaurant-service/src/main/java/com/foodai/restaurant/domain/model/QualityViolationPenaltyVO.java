package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value Object for quality violation penalty configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QualityViolationPenaltyVO {
    /**
     * Whether quality violation penalties are enabled
     */
    private Boolean enabled;
    
    /**
     * Penalty per food quality complaint
     */
    private Double foodQualityComplaintPenalty;
    
    /**
     * Penalty per hygiene violation
     */
    private Double hygieneViolationPenalty;
    
    /**
     * Penalty per packaging issue
     */
    private Double packagingIssuePenalty;
    
    /**
     * Penalty for wrong item
     */
    private Double wrongItemPenalty;
    
    /**
     * Penalty for missing item
     */
    private Double missingItemPenalty;
    
    /**
     * Threshold count of quality complaints before suspension
     */
    private Integer qualityComplaintsThreshold;
    
    /**
     * Threshold period in days
     */
    private Integer qualityComplaintsDays;
}


