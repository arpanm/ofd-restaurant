package com.foodai.user.domain.model;

/**
 * Enumeration of feedback types.
 *
 * @author FoodAI Team
 */
public enum FeedbackType {
    /**
     * General review with ratings
     */
    REVIEW,

    /**
     * Complaint about order/service
     */
    COMPLAINT,

    /**
     * Feature suggestion
     */
    SUGGESTION,

    /**
     * Question/inquiry
     */
    INQUIRY,

    /**
     * Appreciation/compliment
     */
    APPRECIATION,

    /**
     * Bug report
     */
    BUG_REPORT,

    /**
     * Refund request
     */
    REFUND_REQUEST,

    /**
     * General support ticket
     */
    SUPPORT_TICKET
}

