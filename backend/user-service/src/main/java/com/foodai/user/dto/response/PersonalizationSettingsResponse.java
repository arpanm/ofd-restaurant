package com.foodai.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Personalization Settings.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI personalization settings response")
public class PersonalizationSettingsResponse {

    @Schema(description = "AI recommendations enabled")
    private boolean aiRecommendationsEnabled;

    @Schema(description = "AI chat enabled")
    private boolean aiChatEnabled;

    @Schema(description = "Voice ordering enabled")
    private boolean voiceOrderingEnabled;

    @Schema(description = "Learning from history enabled")
    private boolean learningFromHistoryEnabled;

    @Schema(description = "Smart reorder enabled")
    private boolean smartReorderEnabled;

    @Schema(description = "Order history weight")
    private Double orderHistoryWeight;

    @Schema(description = "Dietary weight")
    private Double dietaryWeight;

    @Schema(description = "Show personalized banners")
    private boolean showPersonalizedBanners;

    @Schema(description = "Show suggestions")
    private boolean showSuggestions;

    @Schema(description = "Show quick reorder")
    private boolean showQuickReorder;

    @Schema(description = "Location-based personalization")
    private boolean locationBasedPersonalization;

    @Schema(description = "Chat language")
    private String chatLanguage;

    @Schema(description = "Chat style")
    private String chatStyle;

    @Schema(description = "Chat auto-order enabled")
    private boolean chatAutoOrderEnabled;
}

