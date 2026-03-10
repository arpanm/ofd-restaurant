package com.foodai.user.domain.model;

/**
 * Enumeration of compensation statuses.
 *
 * @author FoodAI Team
 */
public enum CompensationStatus {
    /**
     * Compensation has been offered to user
     */
    OFFERED,

    /**
     * User accepted the compensation
     */
    ACCEPTED,

    /**
     * User rejected the compensation
     */
    REJECTED,

    /**
     * Compensation is being processed
     */
    PROCESSING,

    /**
     * Compensation has been completed
     */
    COMPLETED,

    /**
     * Compensation failed to process
     */
    FAILED,

    /**
     * Compensation offer expired
     */
    EXPIRED
}

