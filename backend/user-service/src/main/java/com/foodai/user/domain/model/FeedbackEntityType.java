package com.foodai.user.domain.model;

/**
 * Enumeration of entity types that can receive feedback.
 *
 * @author FoodAI Team
 */
public enum FeedbackEntityType {
    /**
     * Restaurant feedback
     */
    RESTAURANT,

    /**
     * Menu item feedback
     */
    MENU_ITEM,

    /**
     * Delivery rider feedback
     */
    RIDER,

    /**
     * Order feedback
     */
    ORDER,

    /**
     * Platform/app feedback
     */
    PLATFORM
}

