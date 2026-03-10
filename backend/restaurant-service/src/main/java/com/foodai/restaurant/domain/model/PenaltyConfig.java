package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Comprehensive penalty configuration for a restaurant contract.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyConfig {
    /**
     * Rating-based penalties
     */
    private RatingPenaltyVO ratingPenalty;
    
    /**
     * Review-based penalties
     */
    private ReviewPenaltyVO reviewPenalty;
    
    /**
     * Cancellation penalties
     */
    private CancellationPenaltyVO cancellationPenalty;
    
    /**
     * Pricing rule violation penalties
     */
    private PricingViolationPenaltyVO pricingViolationPenalty;
    
    /**
     * Quality violation penalties
     */
    private QualityViolationPenaltyVO qualityViolationPenalty;
    
    /**
     * Delay penalties
     */
    private DelayPenaltyVO delayPenalty;
    
    /**
     * Other custom violations
     */
    private List<ViolationPenaltyVO> otherViolations;
}


