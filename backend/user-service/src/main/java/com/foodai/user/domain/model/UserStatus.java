package com.foodai.user.domain.model;

/**
 * Enumeration of possible user account statuses.
 *
 * @author FoodAI Team
 */
public enum UserStatus {
    /**
     * User account is active and fully functional
     */
    ACTIVE,

    /**
     * User account is pending verification (email or phone)
     */
    PENDING_VERIFICATION,

    /**
     * User account is suspended due to policy violations
     */
    SUSPENDED,

    /**
     * User account is temporarily blocked
     */
    BLOCKED,

    /**
     * User account is deactivated by user request
     */
    DEACTIVATED,

    /**
     * User account is deleted (soft delete)
     */
    DELETED
}

