package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value Object representing a distance-based fee slab.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistanceFeeSlabVO {
    /**
     * Minimum distance in kilometers
     */
    private Double minDistanceKm;
    
    /**
     * Maximum distance in kilometers
     */
    private Double maxDistanceKm;
    
    /**
     * Fee amount for this distance range
     */
    private Double feeAmount;
}


