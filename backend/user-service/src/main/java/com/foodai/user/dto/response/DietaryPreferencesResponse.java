package com.foodai.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response DTO for Dietary Preferences.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dietary preferences response")
public class DietaryPreferencesResponse {

    @Schema(description = "Is vegetarian")
    private boolean vegetarian;

    @Schema(description = "Is vegan")
    private boolean vegan;

    @Schema(description = "Requires gluten-free")
    private boolean glutenFree;

    @Schema(description = "Avoids dairy")
    private boolean dairyFree;

    @Schema(description = "Follows Jain diet")
    private boolean jain;

    @Schema(description = "Requires halal")
    private boolean halal;

    @Schema(description = "Requires kosher")
    private boolean kosher;

    @Schema(description = "Follows keto diet")
    private boolean keto;

    @Schema(description = "Follows low-carb diet")
    private boolean lowCarb;

    @Schema(description = "Follows paleo diet")
    private boolean paleo;

    @Schema(description = "Food allergies")
    private List<String> allergies;

    @Schema(description = "Ingredients to avoid")
    private List<String> avoidIngredients;

    @Schema(description = "Spice preference (0-5)")
    private Integer spicePreference;

    @Schema(description = "Daily calorie target")
    private Integer dailyCalorieTarget;

    @Schema(description = "Health goals")
    private List<String> healthGoals;

    @Schema(description = "Default customizations")
    private List<String> defaultCustomizations;

    @Schema(description = "Has any restrictions")
    private boolean hasRestrictions;

    @Schema(description = "Active dietary flags")
    private List<String> activeDietaryFlags;
}

