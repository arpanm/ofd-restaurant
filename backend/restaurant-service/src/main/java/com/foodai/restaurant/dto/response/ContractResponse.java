package com.foodai.restaurant.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for Contract information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractResponse {
    private String contractId;
    private String contractUrl;
    private Boolean signed;
    private Instant signedAt;
    private String signedBy;
    private Object platformFee;
    private Object deliveryFee;
    private Object paymentGatewayFee;
    private Object penalties;
    private Instant validFrom;
    private Instant validUntil;
    private Boolean autoRenewal;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;
}


