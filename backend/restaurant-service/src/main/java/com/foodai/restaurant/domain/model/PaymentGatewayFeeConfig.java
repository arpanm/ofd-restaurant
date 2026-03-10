package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Configuration for payment gateway fee structure.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentGatewayFeeConfig {
    /**
     * Type of fee calculation
     */
    private FeeType feeType;
    
    /**
     * Who bears the payment gateway fee
     */
    private PaymentFeePayor payor;
    
    /**
     * Percentage rate (e.g., 2%)
     */
    private Double percentageRate;
    
    /**
     * Fixed amount per transaction
     */
    private Double fixedAmount;
    
    /**
     * Minimum fee
     */
    private Double minFee;
    
    /**
     * Maximum fee (cap)
     */
    private Double maxFee;
}


