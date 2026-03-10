package com.foodai.user.dto.request;

import com.foodai.user.domain.model.DietPlanType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for creating a diet plan.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a diet plan")
public class CreateDietPlanRequest {

    @NotNull(message = "User ID is required")
    @Schema(description = "User ID", required = true)
    private String userId;

    @NotBlank(message = "Plan name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    @Schema(description = "Plan name", example = "My Weight Loss Plan")
    private String name;

    @Size(max = 500, message = "Description must be at most 500 characters")
    @Schema(description = "Plan description")
    private String description;

    @NotNull(message = "Plan type is required")
    @Schema(description = "Type of diet plan")
    private DietPlanType planType;

    @Min(value = 1, message = "Duration must be at least 1 day")
    @Schema(description = "Duration in days", example = "30")
    private Integer durationDays;

    @NotNull(message = "Start date is required")
    @Future(message = "Start date must be in the future")
    @Schema(description = "Start date", example = "2025-02-01")
    private LocalDate startDate;

    @Schema(description = "Daily calorie target", example = "2000")
    private Integer dailyCalorieTarget;

    @Schema(description = "Daily protein target (grams)", example = "60")
    private Integer dailyProteinTarget;

    @Schema(description = "Daily carb target (grams)", example = "250")
    private Integer dailyCarbTarget;

    @Schema(description = "Daily fat target (grams)", example = "65")
    private Integer dailyFatTarget;

    @Schema(description = "Health goals", example = "[\"weight_loss\"]")
    private List<String> healthGoals;

    @Schema(description = "Dietary restrictions", example = "[\"vegetarian\"]")
    private List<String> restrictions;

    @Schema(description = "Preferred cuisines", example = "[\"Indian\", \"Mediterranean\"]")
    private List<String> preferredCuisines;

    @Schema(description = "Budget per meal (INR)", example = "200.0")
    private Double budgetPerMeal;

    @Schema(description = "Total budget (INR)", example = "6000.0")
    private Double totalBudget;

    @Schema(description = "Enable auto-ordering")
    private Boolean autoOrderEnabled;

    @Schema(description = "Auto-order lead time (minutes)", example = "60")
    private Integer autoOrderLeadTime;

    @Schema(description = "Generate AI-powered plan")
    private Boolean generateWithAI;

    @Schema(description = "Reminder settings")
    private DietReminderSettingsDTO reminderSettings;
}

