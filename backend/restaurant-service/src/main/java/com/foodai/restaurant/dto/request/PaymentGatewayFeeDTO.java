package com.foodai.restaurant.dto.request;

import com.foodai.restaurant.domain.model.FeeType;
import com.foodai.restaurant.domain.model.PaymentFeePayor;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for payment gateway fee configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentGatewayFeeDTO {
    
    @NotNull(message = "Fee type is required")
    private FeeType feeType;
    
    @NotNull(message = "Payor is required")
    private PaymentFeePayor payor;
    
    @Min(value = 0, message = "Percentage rate must be non-negative")
    private Double percentageRate;
    
    @Min(value = 0, message = "Fixed amount must be non-negative")
    private Double fixedAmount;
    
    @Min(value = 0, message = "Min fee must be non-negative")
    private Double minFee;
    
    @Min(value = 0, message = "Max fee must be non-negative")
    private Double maxFee;
}


