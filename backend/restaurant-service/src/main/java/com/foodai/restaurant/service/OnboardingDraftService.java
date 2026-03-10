package com.foodai.restaurant.service;

import com.foodai.restaurant.domain.model.OnboardingDraft;
import com.foodai.restaurant.domain.model.OnboardingDraftStatus;
import com.foodai.restaurant.domain.repository.OnboardingDraftRepository;
import com.foodai.restaurant.dto.request.SaveOnboardingDraftRequest;
import com.foodai.restaurant.dto.request.SubmitOnboardingByDraftRequest;
import com.foodai.restaurant.dto.request.SubmitOnboardingRequest;
import com.foodai.restaurant.dto.response.OnboardingDraftResponse;
import com.foodai.restaurant.dto.response.RestaurantResponse;
import com.foodai.restaurant.exception.OnboardingDraftNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class OnboardingDraftService {

    private final OnboardingDraftRepository draftRepository;
    private final RestaurantService restaurantService;

    /**
     * Create a new draft. Returns draft ID for frontend to store and use in subsequent save/submit.
     */
    @Transactional
    public String createDraft() {
        OnboardingDraft draft = OnboardingDraft.builder()
            .currentStep(1)
            .status(OnboardingDraftStatus.DRAFT)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
        OnboardingDraft saved = draftRepository.save(draft);
        log.info("Created onboarding draft: {}", saved.getId());
        return saved.getId();
    }

    /**
     * Get draft by ID for prefill / resume. Throws if not found or already submitted.
     */
    public OnboardingDraftResponse getDraft(String draftId) {
        OnboardingDraft draft = draftRepository.findByIdAndStatus(draftId, OnboardingDraftStatus.DRAFT)
            .orElseGet(() -> draftRepository.findById(draftId)
                .orElseThrow(() -> new OnboardingDraftNotFoundException(draftId)));
        if (draft.getStatus() == OnboardingDraftStatus.SUBMITTED) {
            throw new OnboardingDraftNotFoundException(draftId + " (already submitted)");
        }
        return toResponse(draft);
    }

    /**
     * Save draft (partial update). Merges non-null fields from request and updates current step.
     */
    @Transactional
    public OnboardingDraftResponse saveDraft(String draftId, SaveOnboardingDraftRequest request) {
        OnboardingDraft draft = draftRepository.findByIdAndStatus(draftId, OnboardingDraftStatus.DRAFT)
            .orElseThrow(() -> new OnboardingDraftNotFoundException(draftId));
        merge(draft, request);
        if (request.getStep() != null) {
            draft.setCurrentStep(Math.min(6, Math.max(1, request.getStep())));
        }
        draft.setUpdatedAt(Instant.now());
        draftRepository.save(draft);
        log.debug("Saved onboarding draft: {}, step: {}", draftId, draft.getCurrentStep());
        return toResponse(draft);
    }

    /**
     * Submit onboarding from draft: validate, create restaurant + outlet, mark draft submitted,
     * create owner user and trigger verification (email/SMS). Returns restaurant response.
     */
    @Transactional
    public RestaurantResponse submitFromDraft(SubmitOnboardingByDraftRequest request) {
        OnboardingDraft draft = draftRepository.findByIdAndStatus(request.getDraftId(), OnboardingDraftStatus.DRAFT)
            .orElseThrow(() -> new OnboardingDraftNotFoundException(request.getDraftId()));

        SubmitOnboardingRequest submitReq = toSubmitRequest(draft, request.getCreatedBy());
        RestaurantResponse response = restaurantService.submitOnboarding(submitReq);

        draft.setStatus(OnboardingDraftStatus.SUBMITTED);
        draft.setUpdatedAt(Instant.now());
        draftRepository.save(draft);

        restaurantService.afterOnboardingSubmitted(response.getId(), submitReq);
        return response;
    }

    private void merge(OnboardingDraft draft, SaveOnboardingDraftRequest req) {
        if (req.getName() != null) draft.setName(req.getName());
        if (req.getDescription() != null) draft.setDescription(req.getDescription());
        if (req.getCuisineTypes() != null) draft.setCuisineTypes(req.getCuisineTypes());
        if (req.getAddress() != null) draft.setAddress(req.getAddress());
        if (req.getCity() != null) draft.setCity(req.getCity());
        if (req.getState() != null) draft.setState(req.getState());
        if (req.getPincode() != null) draft.setPincode(req.getPincode());
        if (req.getPhone() != null) draft.setPhone(req.getPhone());
        if (req.getEmail() != null) draft.setEmail(req.getEmail());
        if (req.getOwnerName() != null) draft.setOwnerName(req.getOwnerName());
        if (req.getOwnerPhone() != null) draft.setOwnerPhone(req.getOwnerPhone());
        if (req.getOwnerEmail() != null) draft.setOwnerEmail(req.getOwnerEmail());
        if (req.getFssaiNumber() != null) draft.setFssaiNumber(req.getFssaiNumber());
        if (req.getGstNumber() != null) draft.setGstNumber(req.getGstNumber());
        if (req.getPanNumber() != null) draft.setPanNumber(req.getPanNumber());
        if (req.getFssaiDocumentUrl() != null) draft.setFssaiDocumentUrl(req.getFssaiDocumentUrl());
        if (req.getGstDocumentUrl() != null) draft.setGstDocumentUrl(req.getGstDocumentUrl());
        if (req.getPanDocumentUrl() != null) draft.setPanDocumentUrl(req.getPanDocumentUrl());
        if (req.getCancelledChequeDocumentUrl() != null) draft.setCancelledChequeDocumentUrl(req.getCancelledChequeDocumentUrl());
        if (req.getBankName() != null) draft.setBankName(req.getBankName());
        if (req.getAccountNumber() != null) draft.setAccountNumber(req.getAccountNumber());
        if (req.getIfscCode() != null) draft.setIfscCode(req.getIfscCode());
        if (req.getAccountHolderName() != null) draft.setAccountHolderName(req.getAccountHolderName());
        if (req.getContractSigned() != null) draft.setContractSigned(req.getContractSigned());
        if (req.getContractSignedBy() != null) draft.setContractSignedBy(req.getContractSignedBy());
        if (req.getContractSignedAt() != null) draft.setContractSignedAt(req.getContractSignedAt());
        if (req.getSignatureText() != null) draft.setSignatureText(req.getSignatureText());
        if (req.getMenuFileUrl() != null) draft.setMenuFileUrl(req.getMenuFileUrl());
        if (req.getAssistanceRequested() != null) draft.setAssistanceRequested(req.getAssistanceRequested());
    }

    private SubmitOnboardingRequest toSubmitRequest(OnboardingDraft draft, String createdBy) {
        String createdByVal = createdBy != null && !createdBy.isBlank() ? createdBy : "onboarding-user";
        return SubmitOnboardingRequest.builder()
            .name(require(draft.getName(), "name"))
            .description(require(draft.getDescription(), "description"))
            .cuisineTypes(draft.getCuisineTypes() != null && !draft.getCuisineTypes().isEmpty() ? draft.getCuisineTypes() : List.of("Other"))
            .address(require(draft.getAddress(), "address"))
            .city(require(draft.getCity(), "city"))
            .state(require(draft.getState(), "state"))
            .pincode(require(draft.getPincode(), "pincode"))
            .phone(require(draft.getPhone(), "phone"))
            .email(require(draft.getEmail(), "email"))
            .ownerName(require(draft.getOwnerName(), "ownerName"))
            .ownerPhone(require(draft.getOwnerPhone(), "ownerPhone"))
            .ownerEmail(draft.getOwnerEmail() != null ? draft.getOwnerEmail() : draft.getEmail())
            .fssaiNumber(require(draft.getFssaiNumber(), "fssaiNumber"))
            .gstNumber(draft.getGstNumber())
            .panNumber(draft.getPanNumber())
            .fssaiDocumentUrl(require(draft.getFssaiDocumentUrl(), "fssaiDocumentUrl"))
            .gstDocumentUrl(draft.getGstDocumentUrl())
            .panDocumentUrl(draft.getPanDocumentUrl())
            .cancelledChequeDocumentUrl(require(draft.getCancelledChequeDocumentUrl(), "cancelledChequeDocumentUrl"))
            .bankName(require(draft.getBankName(), "bankName"))
            .accountNumber(require(draft.getAccountNumber(), "accountNumber"))
            .ifscCode(require(draft.getIfscCode(), "ifscCode"))
            .accountHolderName(require(draft.getAccountHolderName(), "accountHolderName"))
            .contractSignedBy(require(draft.getContractSignedBy(), "contractSignedBy"))
            .contractSignedAt(draft.getContractSignedAt() != null ? draft.getContractSignedAt() : Instant.now())
            .menuFileUrl(draft.getMenuFileUrl())
            .createdBy(createdByVal)
            .build();
    }

    private static String require(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required field for submit: " + field);
        }
        return value.trim();
    }

    private static OnboardingDraftResponse toResponse(OnboardingDraft d) {
        return OnboardingDraftResponse.builder()
            .draftId(d.getId())
            .currentStep(d.getCurrentStep())
            .status(d.getStatus())
            .name(d.getName())
            .description(d.getDescription())
            .cuisineTypes(d.getCuisineTypes())
            .address(d.getAddress())
            .city(d.getCity())
            .state(d.getState())
            .pincode(d.getPincode())
            .phone(d.getPhone())
            .email(d.getEmail())
            .ownerName(d.getOwnerName())
            .ownerPhone(d.getOwnerPhone())
            .ownerEmail(d.getOwnerEmail())
            .fssaiNumber(d.getFssaiNumber())
            .gstNumber(d.getGstNumber())
            .panNumber(d.getPanNumber())
            .fssaiDocumentUrl(d.getFssaiDocumentUrl())
            .gstDocumentUrl(d.getGstDocumentUrl())
            .panDocumentUrl(d.getPanDocumentUrl())
            .cancelledChequeDocumentUrl(d.getCancelledChequeDocumentUrl())
            .bankName(d.getBankName())
            .accountNumber(d.getAccountNumber())
            .ifscCode(d.getIfscCode())
            .accountHolderName(d.getAccountHolderName())
            .contractSigned(d.getContractSigned())
            .contractSignedBy(d.getContractSignedBy())
            .contractSignedAt(d.getContractSignedAt())
            .signatureText(d.getSignatureText())
            .menuFileUrl(d.getMenuFileUrl())
            .assistanceRequested(d.getAssistanceRequested())
            .createdAt(d.getCreatedAt())
            .updatedAt(d.getUpdatedAt())
            .build();
    }
}
