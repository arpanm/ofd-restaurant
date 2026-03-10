package com.foodai.restaurant.domain.model;

/**
 * Types of documents required for restaurant registration.
 */
public enum DocumentType {
    /**
     * FSSAI (Food Safety and Standards Authority of India) License
     */
    FSSAI,
    
    /**
     * GST Registration Certificate
     */
    GST,
    
    /**
     * PAN Card
     */
    PAN,
    
    /**
     * Bank Account Proof (Cancelled cheque/Bank statement)
     */
    BANK_PROOF,

    /**
     * Menu file (e.g. Excel upload during onboarding)
     */
    MENU
}


