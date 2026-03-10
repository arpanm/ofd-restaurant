package com.foodai.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Diet Plan Entity.
 * Manages user's diet plans including scheduled meals and nutritional tracking.
 *
 * <p>Covers features from:
 * <ul>
 *   <li>DIET_PLANNER.md - Diet plan creation, meal scheduling</li>
 *   <li>DIET_PLANNER_ORDER_TRACKING.md - Plan order tracking</li>
 * </ul>
 *
 * @author FoodAI Team
 */
@Document(collection = "diet_plans")
@CompoundIndexes({
    @CompoundIndex(name = "user_status_idx", def = "{'userId': 1, 'status': 1}"),
    @CompoundIndex(name = "user_dates_idx", def = "{'userId': 1, 'startDate': 1, 'endDate': 1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietPlan {

    /**
     * Unique diet plan identifier
     */
    @Id
    private String id;

    /**
     * User ID this plan belongs to
     */
    @Indexed
    private String userId;

    /**
     * Plan name (e.g., "Weight Loss Plan", "Muscle Building")
     */
    private String name;

    /**
     * Plan description
     */
    private String description;

    /**
     * Plan type
     */
    private DietPlanType planType;

    /**
     * Plan duration in days
     */
    private Integer durationDays;

    /**
     * Plan start date
     */
    private LocalDate startDate;

    /**
     * Plan end date
     */
    private LocalDate endDate;

    /**
     * Daily calorie target
     */
    private Integer dailyCalorieTarget;

    /**
     * Daily protein target (grams)
     */
    private Integer dailyProteinTarget;

    /**
     * Daily carb target (grams)
     */
    private Integer dailyCarbTarget;

    /**
     * Daily fat target (grams)
     */
    private Integer dailyFatTarget;

    /**
     * Scheduled meals in this plan
     */
    @Builder.Default
    private List<DietMealVO> meals = new ArrayList<>();

    /**
     * Health goals for this plan
     */
    @Builder.Default
    private List<String> healthGoals = new ArrayList<>();

    /**
     * Dietary restrictions to consider
     */
    @Builder.Default
    private List<String> restrictions = new ArrayList<>();

    /**
     * Preferred cuisines for meal suggestions
     */
    @Builder.Default
    private List<String> preferredCuisines = new ArrayList<>();

    /**
     * Budget per meal (in INR)
     */
    private Double budgetPerMeal;

    /**
     * Total budget for the plan (in INR)
     */
    private Double totalBudget;

    /**
     * Actual amount spent (in INR)
     */
    @Builder.Default
    private Double actualSpent = 0.0;

    /**
     * Number of meals completed
     */
    @Builder.Default
    private Integer mealsCompleted = 0;

    /**
     * Number of meals skipped
     */
    @Builder.Default
    private Integer mealsSkipped = 0;

    /**
     * Plan completion percentage
     */
    @Builder.Default
    private Integer completionPercentage = 0;

    /**
     * Whether this is an AI-generated plan
     */
    @Builder.Default
    private boolean aiGenerated = false;

    /**
     * Whether auto-ordering is enabled
     */
    @Builder.Default
    private boolean autoOrderEnabled = false;

    /**
     * Minutes before meal time to auto-order
     */
    @Builder.Default
    private Integer autoOrderLeadTime = 60;

    /**
     * Plan status
     */
    @Indexed
    @Builder.Default
    private DietPlanStatus status = DietPlanStatus.DRAFT;

    /**
     * Reminder settings
     */
    private DietReminderSettingsVO reminderSettings;

    /**
     * Creation timestamp
     */
    @CreatedDate
    private Instant createdAt;

    /**
     * Last update timestamp
     */
    @LastModifiedDate
    private Instant updatedAt;

    /**
     * Soft delete flag
     */
    @Builder.Default
    private boolean deleted = false;

    // ==================== Domain Methods ====================

    /**
     * Adds a meal to the plan.
     *
     * @param meal the meal to add
     */
    public void addMeal(DietMealVO meal) {
        if (this.meals == null) {
            this.meals = new ArrayList<>();
        }
        this.meals.add(meal);
    }

    /**
     * Marks a meal as completed.
     *
     * @param mealId the meal ID
     * @param orderId the order ID (if ordered)
     * @param actualCalories actual calories consumed
     */
    public void completeMeal(String mealId, String orderId, Integer actualCalories) {
        if (this.meals == null) return;
        
        this.meals.stream()
            .filter(m -> m.getId().equals(mealId))
            .findFirst()
            .ifPresent(meal -> {
                meal.setStatus(DietMealStatus.COMPLETED);
                meal.setOrderId(orderId);
                meal.setActualCalories(actualCalories);
                meal.setCompletedAt(Instant.now());
            });
        
        this.mealsCompleted++;
        updateCompletionPercentage();
    }

    /**
     * Marks a meal as skipped.
     *
     * @param mealId the meal ID
     * @param reason the skip reason
     */
    public void skipMeal(String mealId, String reason) {
        if (this.meals == null) return;
        
        this.meals.stream()
            .filter(m -> m.getId().equals(mealId))
            .findFirst()
            .ifPresent(meal -> {
                meal.setStatus(DietMealStatus.SKIPPED);
                meal.setSkipReason(reason);
            });
        
        this.mealsSkipped++;
        updateCompletionPercentage();
    }

    /**
     * Updates the completion percentage.
     */
    private void updateCompletionPercentage() {
        if (this.meals == null || this.meals.isEmpty()) {
            this.completionPercentage = 0;
            return;
        }
        int totalMeals = this.meals.size();
        this.completionPercentage = (this.mealsCompleted * 100) / totalMeals;
    }

    /**
     * Activates the plan.
     */
    public void activate() {
        this.status = DietPlanStatus.ACTIVE;
    }

    /**
     * Pauses the plan.
     */
    public void pause() {
        this.status = DietPlanStatus.PAUSED;
    }

    /**
     * Completes the plan.
     */
    public void complete() {
        this.status = DietPlanStatus.COMPLETED;
    }

    /**
     * Cancels the plan.
     */
    public void cancel() {
        this.status = DietPlanStatus.CANCELLED;
    }

    /**
     * Gets today's meals.
     *
     * @return list of today's meals
     */
    public List<DietMealVO> getTodaysMeals() {
        LocalDate today = LocalDate.now();
        return this.meals.stream()
            .filter(m -> m.getScheduledDate().equals(today))
            .toList();
    }
}

