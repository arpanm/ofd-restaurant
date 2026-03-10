package com.foodai.restaurant.exception;

public class OnboardingDraftNotFoundException extends RuntimeException {

    public OnboardingDraftNotFoundException(String draftId) {
        super("Onboarding draft not found: " + draftId);
    }
}
