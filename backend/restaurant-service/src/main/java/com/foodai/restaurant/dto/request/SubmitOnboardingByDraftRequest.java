package com.foodai.restaurant.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitOnboardingByDraftRequest {

    @NotBlank(message = "Draft ID is required")
    private String draftId;

    /** User ID for createdBy when creating restaurant (e.g. from auth). */
    private String createdBy;
}
