package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value Object representing a tiered fee slab based on order value.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeSlabVO {
    /**
     * Minimum order value for this slab
     */
    private Double minOrderValue;
    
    /**
     * Maximum order value for this slab
     */
    private Double maxOrderValue;
    
    /**
     * Percentage rate for this slab
     */
    private Double percentageRate;
    
    /**
     * Fixed amount for this slab
     */
    private Double fixedAmount;
}


