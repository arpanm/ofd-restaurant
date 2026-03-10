package com.foodai.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value Object representing a user's AI personalization settings.
 * Controls how AI features interact with the user.
 *
 * <p>Covers features from:
 * <ul>
 *   <li>AI_PERSONALIZATION_FEATURES.md - Personalization preferences</li>
 *   <li>AI_CHAT_BUYING_JOURNEY.md - Chat assistant settings</li>
 *   <li>SEARCH_RECOMMENDATION_ENGINE.md - Recommendation preferences</li>
 * </ul>
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalizationSettingsVO {

    // ==================== AI Features ====================

    /**
     * Whether to enable AI-powered recommendations
     */
    @Builder.Default
    private boolean aiRecommendationsEnabled = true;

    /**
     * Whether to enable AI chat assistant
     */
    @Builder.Default
    private boolean aiChatEnabled = true;

    /**
     * Whether to enable voice ordering
     */
    @Builder.Default
    private boolean voiceOrderingEnabled = false;

    /**
     * Whether to allow AI to learn from order history
     */
    @Builder.Default
    private boolean learningFromHistoryEnabled = true;

    /**
     * Whether to allow AI to suggest reorders
     */
    @Builder.Default
    private boolean smartReorderEnabled = true;

    // ==================== Recommendation Settings ====================

    /**
     * How much to weight past orders in recommendations (0.0 to 1.0)
     */
    @Builder.Default
    private Double orderHistoryWeight = 0.7;

    /**
     * How much to weight dietary preferences (0.0 to 1.0)
     */
    @Builder.Default
    private Double dietaryWeight = 0.8;

    /**
     * How much to weight time-of-day context (0.0 to 1.0)
     */
    @Builder.Default
    private Double timeContextWeight = 0.5;

    /**
     * How much to weight weather context (0.0 to 1.0)
     */
    @Builder.Default
    private Double weatherContextWeight = 0.3;

    /**
     * How much to weight trending items (0.0 to 1.0)
     */
    @Builder.Default
    private Double trendingWeight = 0.4;

    // ==================== Display Settings ====================

    /**
     * Whether to show personalized banners
     */
    @Builder.Default
    private boolean showPersonalizedBanners = true;

    /**
     * Whether to show "You might also like" suggestions
     */
    @Builder.Default
    private boolean showSuggestions = true;

    /**
     * Whether to show "Order again" quick actions
     */
    @Builder.Default
    private boolean showQuickReorder = true;

    /**
     * Whether to show personalized offers
     */
    @Builder.Default
    private boolean showPersonalizedOffers = true;

    /**
     * Number of recommendations to show per section
     */
    @Builder.Default
    private Integer recommendationsPerSection = 5;

    // ==================== Privacy Settings ====================

    /**
     * Whether to share data for platform improvement
     */
    @Builder.Default
    private boolean shareDataForImprovement = true;

    /**
     * Whether to allow cross-platform personalization
     */
    @Builder.Default
    private boolean crossPlatformPersonalization = false;

    /**
     * Whether to allow location-based personalization
     */
    @Builder.Default
    private boolean locationBasedPersonalization = true;

    // ==================== Chat Assistant Settings ====================

    /**
     * Preferred language for AI chat
     */
    @Builder.Default
    private String chatLanguage = "en";

    /**
     * Chat response style (concise, detailed, friendly)
     */
    @Builder.Default
    private String chatStyle = "friendly";

    /**
     * Whether chat assistant can auto-complete orders
     */
    @Builder.Default
    private boolean chatAutoOrderEnabled = false;

    /**
     * Budget limit for chat-initiated orders (0 = no limit)
     */
    @Builder.Default
    private Double chatOrderBudgetLimit = 0.0;
}

