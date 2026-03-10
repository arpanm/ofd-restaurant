package com.foodai.restaurant.domain.model;

/**
 * Role of restaurant owner in the business.
 */
public enum OwnerRole {
    /**
     * Primary owner with majority stake and decision making authority
     */
    PRIMARY_OWNER,
    
    /**
     * Co-owner with significant stake
     */
    CO_OWNER,
    
    /**
     * Business partner
     */
    PARTNER,
    
    /**
     * Investor with ownership stake
     */
    INVESTOR
}


