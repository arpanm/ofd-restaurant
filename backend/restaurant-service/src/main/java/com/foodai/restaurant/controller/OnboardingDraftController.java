package com.foodai.restaurant.controller;

import com.foodai.restaurant.dto.request.SaveOnboardingDraftRequest;
import com.foodai.restaurant.dto.request.SubmitOnboardingByDraftRequest;
import com.foodai.restaurant.dto.response.ApiResponse;
import com.foodai.restaurant.dto.response.OnboardingDraftResponse;
import com.foodai.restaurant.dto.response.RestaurantResponse;
import com.foodai.restaurant.service.OnboardingDraftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/restaurants/onboarding")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Onboarding Draft", description = "Save and resume onboarding progress by step")
public class OnboardingDraftController {

    private final OnboardingDraftService draftService;

    @PostMapping("/draft")
    @Operation(summary = "Create draft", description = "Create a new onboarding draft; returns draftId for subsequent save/submit")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Draft created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<ApiResponse<Map<String, String>>> createDraft() {
        String draftId = draftService.createDraft();
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Draft created", Map.of("draftId", draftId)));
    }

    @GetMapping("/draft/{draftId}")
    @Operation(summary = "Get draft", description = "Get saved draft for prefill/resume")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Draft found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Draft not found or already submitted")
    })
    public ResponseEntity<ApiResponse<OnboardingDraftResponse>> getDraft(
        @Parameter(description = "Draft ID") @PathVariable String draftId
    ) {
        OnboardingDraftResponse response = draftService.getDraft(draftId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/draft/{draftId}")
    @Operation(summary = "Save draft", description = "Save current step and form data (partial update)")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Draft saved"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Draft not found")
    })
    public ResponseEntity<ApiResponse<OnboardingDraftResponse>> saveDraft(
        @Parameter(description = "Draft ID") @PathVariable String draftId,
        @RequestBody SaveOnboardingDraftRequest request
    ) {
        OnboardingDraftResponse response = draftService.saveDraft(draftId, request);
        return ResponseEntity.ok(ApiResponse.success("Draft saved", response));
    }

    @PostMapping("/submit")
    @Operation(summary = "Submit by draft", description = "Submit onboarding from draft: create restaurant, owner user, send verification email/SMS")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Onboarding completed"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or incomplete draft"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Draft not found")
    })
    public ResponseEntity<ApiResponse<RestaurantResponse>> submitByDraft(
        @Valid @RequestBody SubmitOnboardingByDraftRequest request
    ) {
        log.info("POST /api/v1/restaurants/onboarding/submit - Submitting draft: {}", request.getDraftId());
        RestaurantResponse response = draftService.submitFromDraft(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Onboarding completed. Check your email and phone for verification to log in.", response));
    }
}
