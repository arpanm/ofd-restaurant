package com.foodai.restaurant.dto.request;

import com.foodai.restaurant.domain.model.FeeType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for platform fee configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlatformFeeDTO {
    
    @NotNull(message = "Fee type is required")
    private FeeType feeType;
    
    @Min(value = 0, message = "Percentage rate must be non-negative")
    private Double percentageRate;
    
    @Min(value = 0, message = "Fixed amount must be non-negative")
    private Double fixedAmountPerOrder;
    
    @Min(value = 0, message = "Min fee must be non-negative")
    private Double minFeePerOrder;
    
    @Min(value = 0, message = "Max fee must be non-negative")
    private Double maxFeePerOrder;
}


