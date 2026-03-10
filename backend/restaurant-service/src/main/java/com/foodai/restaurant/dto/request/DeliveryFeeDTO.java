package com.foodai.restaurant.dto.request;

import com.foodai.restaurant.domain.model.DeliveryFeePayor;
import com.foodai.restaurant.domain.model.FeeType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for delivery fee configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryFeeDTO {
    
    @NotNull(message = "Fee type is required")
    private FeeType feeType;
    
    @NotNull(message = "Payor is required")
    private DeliveryFeePayor payor;
    
    @Min(value = 0, message = "Percentage rate must be non-negative")
    private Double percentageRate;
    
    @Min(value = 0, message = "Fixed amount must be non-negative")
    private Double fixedAmountPerOrder;
    
    @Min(value = 0, message = "Restaurant share must be non-negative")
    private Double restaurantSharePercentage;
    
    @Min(value = 0, message = "Customer share must be non-negative")
    private Double customerSharePercentage;
    
    @Min(value = 0, message = "Min fee must be non-negative")
    private Double minFeePerOrder;
    
    @Min(value = 0, message = "Max fee must be non-negative")
    private Double maxFeePerOrder;
}


