package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Additional serviceability rules for outlet.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceabilityRules {
    /**
     * Check inventory before accepting order
     */
    private Boolean checkInventory;
    
    /**
     * Check kitchen capacity before accepting order
     */
    private Boolean checkCapacity;
    
    /**
     * Maximum orders this outlet can handle per hour
     */
    private Integer maxOrdersPerHour;
    
    /**
     * Pincodes explicitly excluded from service
     */
    private List<String> excludedPincodes;
}


