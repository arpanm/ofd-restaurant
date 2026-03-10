package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Entity representing a restaurant outlet.
 * Multiple outlets can belong to a single restaurant (chain/brand).
 * This is NOT an aggregate root - it's part of the Restaurant aggregate.
 */
@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class RestaurantOutlet {
    /**
     * Unique outlet identifier
     */
    private String outletId;
    
    /**
     * Outlet name (e.g., "Main Branch", "MG Road Branch")
     */
    private String outletName;
    
    /**
     * Outlet code (e.g., "RES001-OUT001")
     */
    private String outletCode;
    
    /**
     * Physical address of this outlet
     */
    private AddressVO address;
    
    /**
     * Multiple contact details for this outlet
     */
    private List<ContactVO> contacts;
    
    /**
     * Operating hours for each day
     */
    private List<OperatingHoursVO> operatingHours;
    
    /**
     * Current status of the outlet
     */
    private OutletStatus status;
    
    /**
     * User ID who approved this outlet
     */
    private String approvedBy;
    
    /**
     * Approval timestamp
     */
    private Instant approvedAt;
    
    /**
     * Rejection reason (if rejected)
     */
    private String rejectionReason;
    
    /**
     * Serviceability configuration
     */
    private ServiceabilityConfig serviceabilityConfig;
    
    /**
     * TAT (Turn Around Time) configuration
     */
    private TATConfig tatConfig;
    
    /**
     * Whether outlet is accepting orders
     */
    private Boolean acceptsOrders;
    
    /**
     * Minimum order value for this outlet
     */
    private Double minimumOrderValue;
    
    /**
     * Delivery fee charged by this outlet
     */
    private Double deliveryFee;
    
    /**
     * Whether outlet has self-delivery
     */
    private Boolean selfDelivery;
    
    /**
     * Average rating for this outlet
     */
    private Double averageRating;
    
    /**
     * Total reviews for this outlet
     */
    private Integer totalReviews;
    
    /**
     * Total orders fulfilled by this outlet
     */
    private Integer totalOrders;
    
    /**
     * Current order queue size
     */
    private Integer currentOrderQueue;
    
    /**
     * User ID who created this outlet
     */
    private String createdBy;
    
    /**
     * Creation timestamp
     */
    private Instant createdAt;
    
    /**
     * User ID who last updated this outlet
     */
    private String updatedBy;
    
    /**
     * Last update timestamp
     */
    private Instant updatedAt;
    
    /**
     * Soft delete flag
     */
    private boolean deleted;
    
    /**
     * User ID who deleted this outlet
     */
    private String deletedBy;
    
    /**
     * Deletion timestamp
     */
    private Instant deletedAt;
}

