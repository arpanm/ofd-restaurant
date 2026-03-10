package com.foodai.user.domain.model;

/**
 * Enumeration of feedback/ticket statuses.
 *
 * @author FoodAI Team
 */
public enum FeedbackStatus {
    /**
     * Feedback has been submitted
     */
    SUBMITTED,

    /**
     * Feedback is being reviewed
     */
    UNDER_REVIEW,

    /**
     * Feedback is assigned and in progress
     */
    IN_PROGRESS,

    /**
     * Waiting for user response
     */
    PENDING_USER_RESPONSE,

    /**
     * Waiting for restaurant response
     */
    PENDING_RESTAURANT_RESPONSE,

    /**
     * Feedback has been escalated
     */
    ESCALATED,

    /**
     * Feedback has been resolved
     */
    RESOLVED,

    /**
     * Feedback is closed
     */
    CLOSED,

    /**
     * Feedback was reopened
     */
    REOPENED
}

