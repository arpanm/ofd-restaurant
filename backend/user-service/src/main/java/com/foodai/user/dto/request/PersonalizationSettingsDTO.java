package com.foodai.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for AI personalization settings.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI personalization settings")
public class PersonalizationSettingsDTO {

    @Schema(description = "Enable AI recommendations")
    private Boolean aiRecommendationsEnabled;

    @Schema(description = "Enable AI chat assistant")
    private Boolean aiChatEnabled;

    @Schema(description = "Enable voice ordering")
    private Boolean voiceOrderingEnabled;

    @Schema(description = "Allow learning from order history")
    private Boolean learningFromHistoryEnabled;

    @Schema(description = "Enable smart reorder suggestions")
    private Boolean smartReorderEnabled;

    @Schema(description = "Order history weight (0.0-1.0)", example = "0.7")
    private Double orderHistoryWeight;

    @Schema(description = "Dietary weight (0.0-1.0)", example = "0.8")
    private Double dietaryWeight;

    @Schema(description = "Show personalized banners")
    private Boolean showPersonalizedBanners;

    @Schema(description = "Show suggestions")
    private Boolean showSuggestions;

    @Schema(description = "Show quick reorder")
    private Boolean showQuickReorder;

    @Schema(description = "Share data for improvement")
    private Boolean shareDataForImprovement;

    @Schema(description = "Location-based personalization")
    private Boolean locationBasedPersonalization;

    @Schema(description = "Chat language", example = "en")
    private String chatLanguage;

    @Schema(description = "Chat style (concise, detailed, friendly)", example = "friendly")
    private String chatStyle;

    @Schema(description = "Chat auto-order enabled")
    private Boolean chatAutoOrderEnabled;

    @Schema(description = "Chat order budget limit", example = "500.0")
    private Double chatOrderBudgetLimit;
}

