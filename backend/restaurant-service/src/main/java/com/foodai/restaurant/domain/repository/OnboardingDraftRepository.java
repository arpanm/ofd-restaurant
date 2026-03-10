package com.foodai.restaurant.domain.repository;

import com.foodai.restaurant.domain.model.OnboardingDraft;
import com.foodai.restaurant.domain.model.OnboardingDraftStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OnboardingDraftRepository extends MongoRepository<OnboardingDraft, String> {

    Optional<OnboardingDraft> findByIdAndStatus(String id, OnboardingDraftStatus status);
}
