package com.foodai.restaurant.domain.model;

/**
 * Type of change recorded in history tables.
 */
public enum ChangeType {
    /**
     * New entity created
     */
    CREATE,
    
    /**
     * Entity updated
     */
    UPDATE,
    
    /**
     * Entity deleted (soft delete)
     */
    DELETE,
    
    /**
     * Status change
     */
    STATUS_CHANGE,
    
    /**
     * Entity approved
     */
    APPROVAL,
    
    /**
     * Entity rejected
     */
    REJECTION,
    
    /**
     * Entity activated
     */
    ACTIVATION,
    
    /**
     * Entity suspended
     */
    SUSPENSION
}


