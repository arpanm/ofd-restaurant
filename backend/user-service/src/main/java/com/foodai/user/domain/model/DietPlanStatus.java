package com.foodai.user.domain.model;

/**
 * Enumeration of diet plan statuses.
 *
 * @author FoodAI Team
 */
public enum DietPlanStatus {
    /**
     * Plan is being created/edited
     */
    DRAFT,

    /**
     * Plan is scheduled but not yet started
     */
    SCHEDULED,

    /**
     * Plan is currently active
     */
    ACTIVE,

    /**
     * Plan is temporarily paused
     */
    PAUSED,

    /**
     * Plan is completed successfully
     */
    COMPLETED,

    /**
     * Plan was cancelled
     */
    CANCELLED,

    /**
     * Plan expired without completion
     */
    EXPIRED
}

