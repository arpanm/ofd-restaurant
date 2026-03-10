package com.foodai.restaurant.domain.repository;

import com.foodai.restaurant.domain.model.ContractHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Contract history tracking.
 * Contract changes are critical and require approval.
 */
@Repository
public interface ContractHistoryRepository extends MongoRepository<ContractHistory, String> {
    
    /**
     * Find all contract history for a restaurant.
     */
    List<ContractHistory> findByRestaurantIdOrderByChangedAtDesc(String restaurantId);
    
    /**
     * Find history for a specific contract.
     */
    List<ContractHistory> findByContractIdOrderByChangedAtDesc(String contractId);
    
    /**
     * Find contract changes that require re-signing.
     */
    List<ContractHistory> findByRequiresResigningTrue();
    
    /**
     * Find unapproved contract changes.
     */
    List<ContractHistory> findByApprovedByIsNull();
    
    /**
     * Find changes made by a specific user.
     */
    List<ContractHistory> findByChangedByOrderByChangedAtDesc(String changedBy);
}


