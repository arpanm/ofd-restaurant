package com.foodai.restaurant.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for creating a new outlet.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOutletRequest {
    
    @NotBlank(message = "Outlet name is required")
    private String outletName;
    
    @NotBlank(message = "Outlet code is required")
    private String outletCode;
    
    @NotNull(message = "Address is required")
    @Valid
    private AddressDTO address;
    
    @NotEmpty(message = "At least one contact is required")
    @Valid
    private List<ContactDTO> contacts;
    
    @NotEmpty(message = "Operating hours must be specified")
    @Valid
    private List<OperatingHoursDTO> operatingHours;
    
    @NotNull(message = "Serviceability config is required")
    @Valid
    private ServiceabilityConfigDTO serviceabilityConfig;
    
    @NotNull(message = "TAT config is required")
    @Valid
    private TATConfigDTO tatConfig;
    
    @Min(value = 0, message = "Minimum order value must be non-negative")
    private Double minimumOrderValue;
    
    @Min(value = 0, message = "Delivery fee must be non-negative")
    private Double deliveryFee;
    
    private Boolean selfDelivery;
    
    @NotBlank(message = "Created by user ID is required")
    private String createdBy;
}


