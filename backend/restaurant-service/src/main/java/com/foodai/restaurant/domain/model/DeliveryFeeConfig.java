package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Configuration for delivery fee structure.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryFeeConfig {
    /**
     * Type of fee calculation
     */
    private FeeType feeType;
    
    /**
     * Who pays the delivery fee
     */
    private DeliveryFeePayor payor;
    
    /**
     * Percentage rate (if percentage-based)
     */
    private Double percentageRate;
    
    /**
     * Fixed amount per order
     */
    private Double fixedAmountPerOrder;
    
    /**
     * Restaurant share percentage (if split)
     */
    private Double restaurantSharePercentage;
    
    /**
     * Customer share percentage (if split)
     */
    private Double customerSharePercentage;
    
    /**
     * Minimum fee per order
     */
    private Double minFeePerOrder;
    
    /**
     * Maximum fee per order (cap)
     */
    private Double maxFeePerOrder;
    
    /**
     * Distance-based fee slabs
     */
    private List<DistanceFeeSlabVO> distanceSlabs;
}


