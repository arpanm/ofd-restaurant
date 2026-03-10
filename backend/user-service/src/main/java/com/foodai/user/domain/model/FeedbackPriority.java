package com.foodai.user.domain.model;

/**
 * Enumeration of feedback/ticket priority levels.
 *
 * @author FoodAI Team
 */
public enum FeedbackPriority {
    /**
     * Low priority - can be addressed within a week
     */
    LOW,

    /**
     * Medium priority - should be addressed within 48 hours
     */
    MEDIUM,

    /**
     * High priority - should be addressed within 24 hours
     */
    HIGH,

    /**
     * Critical priority - requires immediate attention
     */
    CRITICAL
}

