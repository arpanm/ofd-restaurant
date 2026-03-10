package com.foodai.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for dietary preferences.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dietary preferences and restrictions")
public class DietaryPreferencesDTO {

    @Schema(description = "Is vegetarian")
    private Boolean vegetarian;

    @Schema(description = "Is vegan")
    private Boolean vegan;

    @Schema(description = "Requires gluten-free")
    private Boolean glutenFree;

    @Schema(description = "Avoids dairy")
    private Boolean dairyFree;

    @Schema(description = "Follows Jain diet")
    private Boolean jain;

    @Schema(description = "Requires halal")
    private Boolean halal;

    @Schema(description = "Requires kosher")
    private Boolean kosher;

    @Schema(description = "Follows keto diet")
    private Boolean keto;

    @Schema(description = "Follows low-carb diet")
    private Boolean lowCarb;

    @Schema(description = "Follows paleo diet")
    private Boolean paleo;

    @Schema(description = "Food allergies", example = "[\"peanuts\", \"shellfish\"]")
    private List<String> allergies;

    @Schema(description = "Ingredients to avoid", example = "[\"MSG\", \"artificial sweeteners\"]")
    private List<String> avoidIngredients;

    @Schema(description = "Spice preference (0-5)", example = "2")
    private Integer spicePreference;

    @Schema(description = "Daily calorie target", example = "2000")
    private Integer dailyCalorieTarget;

    @Schema(description = "Daily protein target in grams", example = "60")
    private Integer dailyProteinTarget;

    @Schema(description = "Health goals", example = "[\"weight_loss\", \"muscle_gain\"]")
    private List<String> healthGoals;

    @Schema(description = "Default food customizations", example = "[\"less oil\", \"extra spicy\"]")
    private List<String> defaultCustomizations;

    @Schema(description = "Show calorie information")
    private Boolean showCalories;

    @Schema(description = "Show nutritional information")
    private Boolean showNutrition;
}

