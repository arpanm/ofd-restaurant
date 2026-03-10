package com.foodai.menu.dto.request;

import com.foodai.menu.domain.model.AvailabilityScheduleVO;
import com.foodai.menu.domain.model.CustomizationVO;
import com.foodai.menu.domain.model.DynamicPricingConfigVO;
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
 * Request DTO for creating a new Menu Item.
 *
 * <p>This DTO includes all required and optional fields for creating a menu item,
 * with comprehensive validation annotations.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new menu item")
public class CreateMenuItemRequest {

  @NotBlank(message = "Restaurant ID is required")
  @Schema(description = "ID of the restaurant this item belongs to", example = "rest123", required = true)
  private String restaurantId;

  @NotBlank(message = "Menu item name is required")
  @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
  @Schema(description = "Name of the menu item", example = "Butter Chicken", required = true)
  private String name;

  @Size(max = 500, message = "Description cannot exceed 500 characters")
  @Schema(description = "Detailed description of the item", example = "Rich and creamy North Indian curry with tender chicken pieces")
  private String description;

  @NotBlank(message = "Category is required")
  @Size(min = 2, max = 50)
  @Schema(description = "Category of the menu item", example = "Main Course", required = true)
  private String category;

  @NotNull(message = "Price is required")
  @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
  @Digits(integer = 6, fraction = 2, message = "Price must be a valid monetary amount")
  @Schema(description = "Base price of the item", example = "350.00", required = true)
  private BigDecimal basePrice;

  @Schema(description = "URLs of item images", example = "[\"https://images.foodai.com/item1.jpg\"]")
  private List<@NotBlank String> images;

  @Schema(description = "Whether the item is currently available", example = "true", defaultValue = "true")
  private boolean available = true;

  @Min(value = 5, message = "Preparation time must be at least 5 minutes")
  @Max(value = 120, message = "Preparation time cannot exceed 120 minutes")
  @Schema(description = "Preparation time in minutes", example = "25")
  private Integer preparationTime;

  @Min(value = 0, message = "Spice level must be between 0 and 5")
  @Max(value = 5, message = "Spice level must be between 0 and 5")
  @Schema(description = "Spice level (0-5)", example = "3", minimum = "0", maximum = "5")
  private Integer spiceLevel;

  // Dietary Information
  @Schema(description = "Whether the item is vegetarian", example = "false")
  private boolean vegetarian;

  @Schema(description = "Whether the item is vegan", example = "false")
  private boolean vegan;

  @Schema(description = "Whether the item is gluten-free", example = "false")
  private boolean glutenFree;

  @Schema(description = "Whether the item contains dairy", example = "true")
  private boolean containsDairy;

  @Schema(description = "Whether the item contains nuts", example = "false")
  private boolean containsNuts;

  @Schema(description = "Whether the item is halal certified", example = "true")
  private boolean halal;

  @Schema(description = "Whether the item is Jain-friendly", example = "false")
  private boolean jain;

  // Nutritional Information
  @Valid
  @Schema(description = "Nutritional information for the item")
  private NutritionalInfoVO nutritionalInfo;

  // Ingredients and Allergens
  @Schema(description = "List of ingredients", example = "[\"Chicken\", \"Cream\", \"Tomatoes\", \"Spices\"]")
  private List<@NotBlank String> ingredients;

  @Schema(description = "Set of allergens", example = "[\"dairy\", \"nuts\"]")
  private Set<@NotBlank String> allergens;

  // Customizations
  @Valid
  @Schema(description = "Available customization options")
  private List<CustomizationVO> customizations;

  // Availability Schedule
  @Valid
  @Schema(description = "Availability schedule for the item")
  private AvailabilityScheduleVO availabilitySchedule;

  // Dynamic Pricing
  @Valid
  @Schema(description = "Dynamic pricing configuration")
  private DynamicPricingConfigVO dynamicPricingConfig;

  // Tags
  @Schema(description = "Tags for search and filtering", example = "[\"spicy\", \"popular\", \"north-indian\"]")
  private Set<@NotBlank String> tags;

  // Servings
  @Min(value = 1, message = "Serves must be at least 1")
  @Schema(description = "Number of servings", example = "2")
  private Integer serves;

  /**
   * Validates the request.
   *
   * @throws IllegalArgumentException if validation fails
   */
  public void validate() {
    if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Price must be positive");
    }

    if (preparationTime != null && (preparationTime < 5 || preparationTime > 120)) {
      throw new IllegalArgumentException("Preparation time must be between 5 and 120 minutes");
    }

    if (customizations != null) {
      customizations.forEach(c -> {
        if (c.getOptions() == null || c.getOptions().isEmpty()) {
          throw new IllegalArgumentException("Customization must have at least one option");
        }
      });
    }
  }
}

