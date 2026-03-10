package com.foodai.restaurant.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for contract information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractDTO {
    
    private String contractId;
    
    private String contractUrl;

    /** Full name of signatory (e.g. for digital signature during onboarding). */
    private String signedBy;

    private java.time.Instant signedAt;

    private Boolean signed;

    @NotNull(message = "Platform fee configuration is required")
    @Valid
    private PlatformFeeDTO platformFee;
    
    @NotNull(message = "Delivery fee configuration is required")
    @Valid
    private DeliveryFeeDTO deliveryFee;
    
    @NotNull(message = "Payment gateway fee configuration is required")
    @Valid
    private PaymentGatewayFeeDTO paymentGatewayFee;
    
    @Valid
    private PenaltyConfigDTO penalties;
    
    private Boolean autoRenewal;
}


