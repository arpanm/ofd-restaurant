package com.foodai.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Value Object representing a user's dietary preferences and restrictions.
 * Used for personalized recommendations and filtering.
 *
 * <p>Covers features from:
 * <ul>
 *   <li>DIET_PLANNER.md - Dietary restrictions and goals</li>
 *   <li>AI_PERSONALIZATION_FEATURES.md - Personalized food suggestions</li>
 *   <li>SEARCH_RECOMMENDATION_ENGINE.md - Diet-based filtering</li>
 *   <li>FOOD_CUSTOMIZATION_INSTRUCTIONS.md - Default customizations</li>
 * </ul>
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietaryPreferencesVO {

    /**
     * Whether user is vegetarian
     */
    @Builder.Default
    private boolean vegetarian = false;

    /**
     * Whether user is vegan
     */
    @Builder.Default
    private boolean vegan = false;

    /**
     * Whether user requires gluten-free options
     */
    @Builder.Default
    private boolean glutenFree = false;

    /**
     * Whether user avoids dairy
     */
    @Builder.Default
    private boolean dairyFree = false;

    /**
     * Whether user follows Jain diet (no onion, garlic, root vegetables)
     */
    @Builder.Default
    private boolean jain = false;

    /**
     * Whether user requires halal food
     */
    @Builder.Default
    private boolean halal = false;

    /**
     * Whether user requires kosher food
     */
    @Builder.Default
    private boolean kosher = false;

    /**
     * Whether user follows keto diet
     */
    @Builder.Default
    private boolean keto = false;

    /**
     * Whether user follows low-carb diet
     */
    @Builder.Default
    private boolean lowCarb = false;

    /**
     * Whether user follows paleo diet
     */
    @Builder.Default
    private boolean paleo = false;

    /**
     * List of food allergies (e.g., "peanuts", "shellfish", "eggs")
     */
    @Builder.Default
    private List<String> allergies = new ArrayList<>();

    /**
     * List of ingredients to avoid (e.g., "msg", "artificial sweeteners")
     */
    @Builder.Default
    private List<String> avoidIngredients = new ArrayList<>();

    /**
     * Preferred spice level (0-5, where 0 is no spice and 5 is extremely spicy)
     */
    @Builder.Default
    private Integer spicePreference = 2;

    /**
     * Daily calorie target (for diet planning)
     */
    private Integer dailyCalorieTarget;

    /**
     * Daily protein target in grams
     */
    private Integer dailyProteinTarget;

    /**
     * Daily carb target in grams
     */
    private Integer dailyCarbTarget;

    /**
     * Daily fat target in grams
     */
    private Integer dailyFatTarget;

    /**
     * Health goals (e.g., "weight_loss", "muscle_gain", "maintenance")
     */
    @Builder.Default
    private List<String> healthGoals = new ArrayList<>();

    /**
     * Medical conditions affecting diet (e.g., "diabetes", "hypertension")
     */
    @Builder.Default
    private List<String> medicalConditions = new ArrayList<>();

    /**
     * Default food customization instructions (e.g., "less oil", "extra spicy")
     */
    @Builder.Default
    private List<String> defaultCustomizations = new ArrayList<>();

    /**
     * Whether to show calorie information by default
     */
    @Builder.Default
    private boolean showCalories = true;

    /**
     * Whether to show nutritional information by default
     */
    @Builder.Default
    private boolean showNutrition = true;

    /**
     * Checks if user has any dietary restrictions.
     *
     * @return true if user has restrictions
     */
    public boolean hasRestrictions() {
        return vegetarian || vegan || glutenFree || dairyFree || jain ||
               halal || kosher || keto || lowCarb || paleo ||
               (allergies != null && !allergies.isEmpty()) ||
               (avoidIngredients != null && !avoidIngredients.isEmpty());
    }

    /**
     * Gets list of active dietary flags for filtering.
     *
     * @return list of active dietary flags
     */
    public List<String> getActiveDietaryFlags() {
        List<String> flags = new ArrayList<>();
        if (vegetarian) flags.add("vegetarian");
        if (vegan) flags.add("vegan");
        if (glutenFree) flags.add("gluten_free");
        if (dairyFree) flags.add("dairy_free");
        if (jain) flags.add("jain");
        if (halal) flags.add("halal");
        if (kosher) flags.add("kosher");
        if (keto) flags.add("keto");
        if (lowCarb) flags.add("low_carb");
        if (paleo) flags.add("paleo");
        return flags;
    }
}

