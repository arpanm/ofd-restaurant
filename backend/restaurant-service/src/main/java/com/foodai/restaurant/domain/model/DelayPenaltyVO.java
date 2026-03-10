package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value Object for delivery delay penalty configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DelayPenaltyVO {
    /**
     * Whether delay penalties are enabled
     */
    private Boolean enabled;
    
    /**
     * Buffer minutes beyond promised TAT
     */
    private Integer allowedDelayMinutes;
    
    /**
     * Penalty per minute of delay
     */
    private Double penaltyPerMinuteDelay;
    
    /**
     * Maximum penalty cap per order
     */
    private Double maxDelayPenaltyPerOrder;
    
    /**
     * Chronic delay threshold percentage (e.g., > 20% orders delayed)
     */
    private Double chronicDelayThresholdPercentage;
    
    /**
     * Monthly penalty for chronic delays
     */
    private Double chronicDelayPenalty;
}


