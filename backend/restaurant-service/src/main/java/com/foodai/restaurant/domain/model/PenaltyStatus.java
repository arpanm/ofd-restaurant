package com.foodai.restaurant.domain.model;

/**
 * Status of penalty in the system.
 */
public enum PenaltyStatus {
    /**
     * Penalty calculated but not yet applied
     */
    PENDING,
    
    /**
     * Penalty has been applied
     */
    APPLIED,
    
    /**
     * Penalty was waived by operations team
     */
    WAIVED,
    
    /**
     * Restaurant is disputing the penalty
     */
    DISPUTED,
    
    /**
     * Dispute has been resolved
     */
    RESOLVED
}


