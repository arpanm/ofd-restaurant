package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

/**
 * Persisted onboarding draft. Stores progress by step so the user can resume.
 * All fields are optional to support partial saves.
 */
@Document(collection = "onboarding_drafts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingDraft {

    @Id
    private String id;

    /** Current step (1-6). */
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

    // Step 6
    private String menuFileUrl;

    /** Whether assistance was requested (UI state). */
    private Boolean assistanceRequested;

    /** Signature text (UI state for step 5). */
    private String signatureText;

    @CreatedDate
    private Instant createdAt;
    @LastModifiedDate
    private Instant updatedAt;
}
