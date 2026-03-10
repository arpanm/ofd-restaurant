package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value Object for cancellation penalty configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancellationPenaltyVO {
    /**
     * Whether cancellation penalties are enabled
     */
    private Boolean enabled;
    
    /**
     * Penalty amount per cancellation
     */
    private Double cancellationPenaltyAmount;
    
    /**
     * Number of free cancellations allowed per month
     */
    private Integer allowedCancellationsPerMonth;
    
    /**
     * Penalty for first violation
     */
    private Double firstViolationPenalty;
    
    /**
     * Penalty for second violation
     */
    private Double secondViolationPenalty;
    
    /**
     * Penalty for third violation
     */
    private Double thirdViolationPenalty;
    
    /**
     * Maximum cancellation rate percentage (e.g., 5%)
     */
    private Double maxCancellationRatePercentage;
    
    /**
     * Penalty for exceeding cancellation rate
     */
    private Double penaltyForExceedingRate;
    
    /**
     * Suspension cancellation rate threshold (e.g., > 10%)
     */
    private Double suspensionCancellationRate;
}


