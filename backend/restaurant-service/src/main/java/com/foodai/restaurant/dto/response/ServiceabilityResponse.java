package com.foodai.restaurant.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for Serviceability configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceabilityResponse {
    private List<String> serviceablePincodes;
    private Double maxDeliveryRadius;
    private Boolean useRadiusBased;
    private Boolean checkInventory;
    private Boolean checkCapacity;
    private Integer maxOrdersPerHour;
    private List<String> excludedPincodes;
}


