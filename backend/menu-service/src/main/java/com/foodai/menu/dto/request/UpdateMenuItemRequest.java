package com.foodai.menu.dto.request;

import com.foodai.menu.domain.model.AvailabilityScheduleVO;
import com.foodai.menu.domain.model.CustomizationVO;
import com.foodai.menu.domain.model.DynamicPricingConfigVO;
import com.foodai.menu.domain.model.MenuItemStatus;
import com.foodai.menu.domain.model.NutritionalInfoVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Request DTO for updating an existing Menu Item.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update a menu item")
public class UpdateMenuItemRequest {

  @NotBlank(message = "Name is required")
  @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
  @Schema(description = "Name of the menu item", example = "Butter Chicken")
  private String name;

  @Size(max = 500, message = "Description cannot exceed 500 characters")
  @Schema(description = "Detailed description", example = "Rich and creamy North Indian curry")
  private String description;

  @NotBlank(message = "Category is required")
  @Size(min = 2, max = 50)
  @Schema(description = "Category", example = "Main Course")
  private String category;

  @NotNull(message = "Price is required")
  @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
  @Digits(integer = 6, fraction = 2)
  @Schema(description = "Base price", example = "350.00")
  private BigDecimal basePrice;

  @Schema(description = "Image URLs")
  private List<String> images;

  @Schema(description = "Availability status", example = "true")
  private Boolean available;

  @Min(value = 5, message = "Preparation time must be at least 5 minutes")
  @Max(value = 120, message = "Preparation time cannot exceed 120 minutes")
  @Schema(description = "Preparation time in minutes", example = "25")
  private Integer preparationTime;

  @Min(0)
  @Max(5)
  @Schema(description = "Spice level (0-5)", example = "3")
  private Integer spiceLevel;

  @Schema(description = "Vegetarian flag")
  private Boolean vegetarian;

  @Schema(description = "Vegan flag")
  private Boolean vegan;

  @Schema(description = "Gluten-free flag")
  private Boolean glutenFree;

  @Schema(description = "Contains dairy flag")
  private Boolean containsDairy;

  @Schema(description = "Contains nuts flag")
  private Boolean containsNuts;

  @Schema(description = "Halal certified flag")
  private Boolean halal;

  @Schema(description = "Jain-friendly flag")
  private Boolean jain;

  @Valid
  @Schema(description = "Nutritional information")
  private NutritionalInfoVO nutritionalInfo;

  @Schema(description = "Ingredients list")
  private List<String> ingredients;

  @Schema(description = "Allergens set")
  private Set<String> allergens;

  @Valid
  @Schema(description = "Customization options")
  private List<CustomizationVO> customizations;

  @Valid
  @Schema(description = "Availability schedule")
  private AvailabilityScheduleVO availabilitySchedule;

  @Valid
  @Schema(description = "Dynamic pricing config")
  private DynamicPricingConfigVO dynamicPricingConfig;

  @Schema(description = "Tags for search")
  private Set<String> tags;

  @Min(1)
  @Schema(description = "Number of servings", example = "2")
  private Integer serves;

  @Schema(description = "Item status")
  private MenuItemStatus status;
}

