package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * Restaurant Aggregate Root.
 * Represents a restaurant brand/chain that can have multiple outlets.
 * This is the main entity for restaurant management.
 */
@Document(collection = "restaurants")
@CompoundIndexes({
    @CompoundIndex(name = "owner_idx", def = "{'owners.ownerId': 1}")
    // Note: Compound indexes spanning multiple array fields (outlets, cuisineTypes) 
    // are not supported by MongoDB. Use separate simple indexes instead.
})
@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class Restaurant {
    /**
     * Unique restaurant identifier
     */
    @Id
    private String id;
    
    /**
     * Restaurant brand name
     */
    @Indexed
    private String name;
    
    /**
     * Restaurant description
     */
    private String description;
    
    /**
     * Logo URL
     */
    private String logo;
    
    /**
     * Cover image URL
     */
    private String coverImage;
    
    /**
     * Multiple owners/partners (can have multiple with ownership percentages)
     */
    private List<OwnerVO> owners;
    
    /**
     * Brand-level contact details (primary, secondary, emergency contacts)
     */
    private List<ContactVO> contacts;
    
    /**
     * Cuisine types offered (common across all outlets)
     */
    @Indexed
    private List<String> cuisineTypes;
    
    /**
     * FSSAI license number (Food Safety and Standards Authority of India).
     */
    private String fssaiLicenseNumber;

    /**
     * GST registration number (optional).
     */
    private String gstNumber;

    /**
     * PAN number (optional).
     */
    private String panNumber;

    /**
     * Payout bank account details.
     */
    private BankAccountVO bankAccount;

    /**
     * Documents for verification (brand level)
     */
    private List<DocumentVO> documents;

    /**
     * Contract with fee structures and penalties
     */
    private ContractVO contract;
    
    /**
     * Onboarding type
     */
    private OnboardingType onboardingType;
    
    /**
     * User ID of operations agent (if assisted onboarding)
     */
    private String onboardedBy;
    
    /**
     * Multiple outlets for this restaurant
     */
    private List<RestaurantOutlet> outlets;
    
    /**
     * Average rating (aggregated from all outlets)
     */
    private Double averageRating;
    
    /**
     * Total reviews (aggregated from all outlets)
     */
    private Integer totalReviews;
    
    /**
     * Total orders (aggregated from all outlets)
     */
    private Integer totalOrders;
    
    /**
     * Average cost for two people (in INR)
     */
    private Double averageCostForTwo;
    
    /**
     * Whether the restaurant serves only vegetarian food
     */
    private Boolean vegetarianOnly;
    
    /**
     * Master switch to accept orders (affects all outlets)
     */
    private Boolean acceptsOrders;
    
    /**
     * Restaurant brand status
     */
    @Indexed
    private RestaurantStatus status;
    
    /**
     * User ID who created this restaurant
     */
    private String createdBy;
    
    /**
     * Creation timestamp
     */
    @CreatedDate
    private Instant createdAt;
    
    /**
     * User ID who last updated this restaurant
     */
    private String updatedBy;
    
    /**
     * Last update timestamp
     */
    @LastModifiedDate
    private Instant updatedAt;
    
    /**
     * Soft delete flag
     */
    @Indexed
    private boolean deleted;
    
    /**
     * User ID who deleted this restaurant
     */
    private String deletedBy;
    
    /**
     * Deletion timestamp
     */
    private Instant deletedAt;
}

