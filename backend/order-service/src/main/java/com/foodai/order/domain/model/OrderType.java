package com.foodai.order.domain.model;

/**
 * Enumeration representing the type/source of an order.
 *
 * @author FoodAI Team
 */
public enum OrderType {
    /**
     * Standard order from a single restaurant.
     */
    SINGLE_RESTAURANT,

    /**
     * Order containing items from multiple restaurants.
     */
    MULTI_RESTAURANT,

    /**
     * Order placed through AI chat assistant.
     */
    AI_CHAT,

    /**
     * Order as part of a diet plan subscription.
     */
    DIET_PLAN,

    /**
     * Order for party/event catering.
     */
    PARTY_PLANNER,

    /**
     * Repeat/reorder of a previous order.
     */
    REORDER,

    /**
     * Scheduled/future order.
     */
    SCHEDULED
}

