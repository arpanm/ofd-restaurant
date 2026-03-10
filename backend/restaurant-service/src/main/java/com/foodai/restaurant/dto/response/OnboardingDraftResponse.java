package com.foodai.restaurant.dto.response;

import com.foodai.restaurant.domain.model.OnboardingDraftStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Response for GET onboarding draft (for prefill / resume).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingDraftResponse {

    private String draftId;
    private int currentStep;
    private OnboardingDraftStatus status;

    // Step 1
    private String name;
    private String description;
    private List<String> cuisineTypes;

    // Step 2
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String phone;
    private String email;

    // Step 3
    private String ownerName;
    private String ownerPhone;
    private String ownerEmail;
    private String fssaiNumber;
    private String gstNumber;
    private String panNumber;
    private String fssaiDocumentUrl;
    private String gstDocumentUrl;
    private String panDocumentUrl;
    private String cancelledChequeDocumentUrl;

    // Step 4
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String accountHolderName;

    // Step 5
    private Boolean contractSigned;
    private String contractSignedBy;
    private Instant contractSignedAt;
    private String signatureText;

    // Step 6
    private String menuFileUrl;

    private Boolean assistanceRequested;
    private Instant createdAt;
    private Instant updatedAt;
}
