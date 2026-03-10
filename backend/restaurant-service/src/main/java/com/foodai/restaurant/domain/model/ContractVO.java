package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Value Object representing restaurant contract with comprehensive fee structures.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractVO {
    /**
     * Unique contract identifier
     */
    private String contractId;
    
    /**
     * URL to signed contract document
     */
    private String contractUrl;
    
    /**
     * Whether contract has been signed
     */
    private Boolean signed;
    
    /**
     * Timestamp when contract was signed
     */
    private Instant signedAt;
    
    /**
     * Owner who signed the contract
     */
    private String signedBy;
    
    /**
     * Platform fee structure
     */
    private PlatformFeeConfig platformFee;
    
    /**
     * Delivery fee structure
     */
    private DeliveryFeeConfig deliveryFee;
    
    /**
     * Payment gateway fee structure
     */
    private PaymentGatewayFeeConfig paymentGatewayFee;
    
    /**
     * Penalty configuration
     */
    private PenaltyConfig penalties;
    
    /**
     * Contract validity start date
     */
    private Instant validFrom;
    
    /**
     * Contract validity end date
     */
    private Instant validUntil;
    
    /**
     * Whether contract auto-renews
     */
    private Boolean autoRenewal;
    
    /**
     * User ID who created the contract
     */
    private String createdBy;
    
    /**
     * Contract creation timestamp
     */
    private Instant createdAt;
    
    /**
     * User ID who last updated the contract
     */
    private String updatedBy;
    
    /**
     * Contract last update timestamp
     */
    private Instant updatedAt;
}


