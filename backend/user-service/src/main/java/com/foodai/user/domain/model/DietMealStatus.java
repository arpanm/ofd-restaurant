package com.foodai.user.domain.model;

/**
 * Enumeration of diet meal statuses.
 *
 * @author FoodAI Team
 */
public enum DietMealStatus {
    /**
     * Meal is scheduled for future
     */
    SCHEDULED,

    /**
     * Meal is pending (today's meal)
     */
    PENDING,

    /**
     * Order has been placed
     */
    ORDERED,

    /**
     * Meal was completed/eaten
     */
    COMPLETED,

    /**
     * Meal was skipped
     */
    SKIPPED,

    /**
     * Meal was substituted with different item
     */
    SUBSTITUTED,

    /**
     * Meal time passed without action
     */
    MISSED
}

