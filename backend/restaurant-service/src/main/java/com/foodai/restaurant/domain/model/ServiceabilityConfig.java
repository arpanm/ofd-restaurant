package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Serviceability configuration for an outlet.
 * Defines which areas the outlet can serve.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceabilityConfig {
    /**
     * List of pincodes this outlet can serve
     */
    private List<String> serviceablePincodes;
    
    /**
     * Maximum delivery radius in kilometers
     */
    private Double maxDeliveryRadius;
    
    /**
     * Use radius-based serviceability (true) or pincode-based (false)
     */
    private Boolean useRadiusBased;
    
    /**
     * Additional serviceability rules
     */
    private ServiceabilityRules rules;
}


