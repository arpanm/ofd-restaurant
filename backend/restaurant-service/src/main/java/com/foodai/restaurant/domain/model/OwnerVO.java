package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Value Object representing a restaurant owner.
 * Supports multiple owners per restaurant.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerVO {
    /**
     * User ID of the owner
     */
    private String ownerId;
    
    /**
     * Full name of the owner
     */
    private String ownerName;
    
    /**
     * Email address of the owner
     */
    private String ownerEmail;
    
    /**
     * Phone number of the owner (10 digits)
     */
    private String ownerPhone;
    
    /**
     * Percentage ownership stake (must sum to 100% across all owners)
     */
    private Double ownershipPercentage;
    
    /**
     * Role of the owner in the business
     */
    private OwnerRole role;
    
    /**
     * Whether this owner is the primary contact
     */
    private Boolean isPrimaryContact;
    
    /**
     * Timestamp when owner was added
     */
    private Instant addedAt;
    
    /**
     * User ID who added this owner
     */
    private String addedBy;
}


