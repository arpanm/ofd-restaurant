package com.foodai.restaurant.domain.model;

/**
 * Status enum for Restaurant Outlet (Outlet Level).
 * Each outlet can have independent status.
 */
public enum OutletStatus {
    /**
     * Outlet has been created but not yet submitted for approval
     */
    NOT_ONBOARDED,
    
    /**
     * Outlet is pending approval from operations team
     */
    PENDING_APPROVAL,
    
    /**
     * Outlet has been approved but not yet activated
     */
    APPROVED,
    
    /**
     * Outlet is active and accepting orders
     */
    ACTIVE,
    
    /**
     * Outlet is temporarily suspended
     */
    SUSPENDED,
    
    /**
     * Outlet registration was rejected
     */
    REJECTED
}


