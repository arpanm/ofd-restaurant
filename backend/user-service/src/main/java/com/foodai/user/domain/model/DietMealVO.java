package com.foodai.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Value Object representing a scheduled meal in a diet plan.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietMealVO {

    /**
     * Unique meal identifier
     */
    private String id;

    /**
     * Meal type (breakfast, lunch, dinner, snack)
     */
    private MealType mealType;

    /**
     * Scheduled date
     */
    private LocalDate scheduledDate;

    /**
     * Scheduled time
     */
    private LocalTime scheduledTime;

    /**
     * Meal name/title
     */
    private String name;

    /**
     * Meal description
     */
    private String description;

    /**
     * Suggested menu item IDs
     */
    @Builder.Default
    private List<String> suggestedMenuItemIds = new ArrayList<>();

    /**
     * Suggested restaurant IDs
     */
    @Builder.Default
    private List<String> suggestedRestaurantIds = new ArrayList<>();

    /**
     * Selected menu item ID
     */
    private String selectedMenuItemId;

    /**
     * Selected restaurant ID
     */
    private String selectedRestaurantId;

    /**
     * Target calories for this meal
     */
    private Integer targetCalories;

    /**
     * Target protein (grams)
     */
    private Integer targetProtein;

    /**
     * Target carbs (grams)
     */
    private Integer targetCarbs;

    /**
     * Target fat (grams)
     */
    private Integer targetFat;

    /**
     * Actual calories consumed
     */
    private Integer actualCalories;

    /**
     * Budget for this meal (in INR)
     */
    private Double budget;

    /**
     * Actual cost (in INR)
     */
    private Double actualCost;

    /**
     * Order ID if this meal was ordered
     */
    private String orderId;

    /**
     * Meal status
     */
    @Builder.Default
    private DietMealStatus status = DietMealStatus.SCHEDULED;

    /**
     * When the meal was completed
     */
    private Instant completedAt;

    /**
     * Skip reason if meal was skipped
     */
    private String skipReason;

    /**
     * Custom instructions for this meal
     */
    private String customInstructions;

    /**
     * Notes about the meal
     */
    private String notes;

    /**
     * Whether reminder was sent
     */
    @Builder.Default
    private boolean reminderSent = false;

    /**
     * Whether auto-order was triggered
     */
    @Builder.Default
    private boolean autoOrderTriggered = false;
}

