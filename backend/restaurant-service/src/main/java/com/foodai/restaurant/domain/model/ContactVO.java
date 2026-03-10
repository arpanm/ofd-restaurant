package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Value Object representing a contact person.
 * Used for brand-level and outlet-level contacts.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactVO {
    /**
     * Unique contact identifier
     */
    private String contactId;
    
    /**
     * Type of contact
     */
    private ContactType contactType;
    
    /**
     * Full name of contact person
     */
    private String name;
    
    /**
     * Primary phone number
     */
    private String phone;
    
    /**
     * Alternate phone number
     */
    private String alternatePhone;
    
    /**
     * Email address
     */
    private String email;
    
    /**
     * Designation/role (e.g., Manager, Owner, Support Executive)
     */
    private String designation;
    
    /**
     * Whether this contact is currently active
     */
    private Boolean isActive;
    
    /**
     * Timestamp when contact was added
     */
    private Instant addedAt;
    
    /**
     * User ID who added this contact
     */
    private String addedBy;
}


