package com.foodai.user.domain.model;

/**
 * Enumeration of achievement categories.
 *
 * @author FoodAI Team
 */
public enum AchievementCategory {
    /**
     * Order-related achievements (first order, 10th order, etc.)
     */
    ORDERS,

    /**
     * Spending-related achievements
     */
    SPENDING,

    /**
     * Streak-related achievements
     */
    STREAKS,

    /**
     * Referral-related achievements
     */
    REFERRALS,

    /**
     * Exploration achievements (trying new restaurants, cuisines)
     */
    EXPLORATION,

    /**
     * Community achievements (reviews, ratings)
     */
    COMMUNITY,

    /**
     * Special/seasonal achievements
     */
    SPECIAL,

    /**
     * Diet/health achievements
     */
    HEALTH
}

