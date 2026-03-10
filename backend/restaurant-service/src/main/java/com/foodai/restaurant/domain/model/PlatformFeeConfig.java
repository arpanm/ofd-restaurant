package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Configuration for platform fee structure.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformFeeConfig {
    /**
     * Type of fee calculation
     */
    private FeeType feeType;
    
    /**
     * Percentage rate (e.g., 15% per order)
     */
    private Double percentageRate;
    
    /**
     * Fixed amount per order
     */
    private Double fixedAmountPerOrder;
    
    /**
     * Minimum fee per order
     */
    private Double minFeePerOrder;
    
    /**
     * Maximum fee per order (cap)
     */
    private Double maxFeePerOrder;
    
    /**
     * Tiered fee slabs (optional)
     */
    private List<FeeSlabVO> feeSlabs;
}


