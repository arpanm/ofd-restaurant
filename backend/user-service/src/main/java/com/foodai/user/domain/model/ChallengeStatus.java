package com.foodai.user.domain.model;

/**
 * Enumeration of challenge statuses.
 *
 * @author FoodAI Team
 */
public enum ChallengeStatus {
    /**
     * Challenge has not started yet
     */
    NOT_STARTED,

    /**
     * Challenge is in progress
     */
    IN_PROGRESS,

    /**
     * Challenge is completed successfully
     */
    COMPLETED,

    /**
     * Challenge expired without completion
     */
    EXPIRED,

    /**
     * Challenge was abandoned by user
     */
    ABANDONED
}

