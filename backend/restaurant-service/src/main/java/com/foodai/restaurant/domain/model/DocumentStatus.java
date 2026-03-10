package com.foodai.restaurant.domain.model;

/**
 * Verification status of uploaded documents.
 */
public enum DocumentStatus {
    /**
     * Document uploaded but not yet verified
     */
    PENDING,
    
    /**
     * Document verified by operations team
     */
    VERIFIED,
    
    /**
     * Document rejected (invalid or unclear)
     */
    REJECTED
}


