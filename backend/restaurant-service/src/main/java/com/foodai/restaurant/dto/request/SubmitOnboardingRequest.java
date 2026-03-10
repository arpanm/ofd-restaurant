package com.foodai.restaurant.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for the restaurant onboarding workflow (all steps).
 * Maps 1:1 from the UI onboarding flow. Application layer converts this to
 * CreateRestaurantRequest + CreateOutletRequest and applies default contract.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitOnboardingRequest {

    // Step 1: Basic info
    @NotBlank(message = "Restaurant name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Description is required")
    @Size(max = 500)
    private String description;

    @NotEmpty(message = "At least one cuisine type is required")
    private List<String> cuisineTypes;

    // Step 2: Contact / address
    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Pincode must be a valid 6-digit code")
    private String pincode;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone must be a valid 10-digit Indian mobile number")
    private String phone;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    // Step 3: Legal / owner
    @NotBlank(message = "Owner name is required")
    private String ownerName;

    @NotBlank(message = "Owner phone is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Owner phone must be a valid 10-digit Indian mobile number")
    private String ownerPhone;

    @Email
    private String ownerEmail;

    @NotBlank(message = "FSSAI number is required")
    private String fssaiNumber;

    private String gstNumber;
    private String panNumber;

    /** Document URLs from upload (or placeholder). FSSAI required. */
    @NotBlank(message = "FSSAI document URL is required")
    private String fssaiDocumentUrl;

    private String gstDocumentUrl;
    private String panDocumentUrl;

    @NotBlank(message = "Cancelled cheque document URL is required")
    private String cancelledChequeDocumentUrl;

    // Step 4: Banking (already have cancelledChequeDocumentUrl above)
    @NotBlank(message = "Bank name is required")
    private String bankName;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotBlank(message = "IFSC code is required")
    private String ifscCode;

    @NotBlank(message = "Account holder name is required")
    private String accountHolderName;

    // Step 5: Contract
    @NotBlank(message = "Contract must be signed (signedBy required)")
    private String contractSignedBy;

    private java.time.Instant contractSignedAt;

    // Step 6 (optional): menu file URL
    private String menuFileUrl;

    @NotBlank(message = "Created by user ID is required")
    private String createdBy;
}
