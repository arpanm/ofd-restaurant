package com.foodai.restaurant.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for serviceability configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceabilityConfigDTO {
    
    @NotEmpty(message = "At least one serviceable pincode is required")
    private List<String> serviceablePincodes;
    
    @Min(value = 1, message = "Max delivery radius must be at least 1 km")
    private Double maxDeliveryRadius;
    
    @NotNull(message = "Use radius based flag is required")
    private Boolean useRadiusBased;
    
    private Boolean checkInventory;
    
    private Boolean checkCapacity;
    
    @Min(value = 1, message = "Max orders per hour must be at least 1")
    private Integer maxOrdersPerHour;
}


