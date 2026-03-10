package com.foodai.restaurant.dto.response;

import com.foodai.restaurant.domain.model.OwnerRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for Owner information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerResponse {
    private String ownerId;
    private String ownerName;
    private String ownerEmail;
    private String ownerPhone;
    private Double ownershipPercentage;
    private OwnerRole role;
    private Boolean isPrimaryContact;
    private Instant addedAt;
    private String addedBy;
}


