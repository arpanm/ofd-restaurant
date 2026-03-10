package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value Object for pricing violation penalty configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingViolationPenaltyVO {
    /**
     * Whether pricing violation penalties are enabled
     */
    private Boolean enabled;
    
    /**
     * Penalty for price mismatch
     */
    private Double priceMismatchPenalty;
    
    /**
     * Unauthorized price increase threshold percentage (e.g., > 10%)
     */
    private Double unauthorizedIncreaseThreshold;
    
    /**
     * Penalty for unauthorized price increase
     */
    private Double unauthorizedIncreasePenalty;
    
    /**
     * Penalty for marking item unavailable with different price
     */
    private Double availabilityMismatchPenalty;
    
    /**
     * Penalty for charging above MRP
     */
    private Double mrpViolationPenalty;
}


