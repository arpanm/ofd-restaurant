package com.foodai.menu.dto.response;

import com.foodai.menu.domain.model.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Response DTO for Menu Item (Detailed view).
 *
 * <p>This includes all fields including customizations, nutrition, and availability schedule.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Detailed menu item response with all fields")
public class MenuItemDetailResponse {

  @Schema(description = "Unique identifier")
  private String id;

  @Schema(description = "Restaurant ID")
  private String restaurantId;

  @Schema(description = "Item name")
  private String name;

  @Schema(description = "Description")
  private String description;

  @Schema(description = "Category")
  private String category;

  @Schema(description = "Base price")
  private BigDecimal basePrice;

  @Schema(description = "All image URLs")
  private List<String> images;

  @Schema(description = "Availability status")
  private boolean available;

  @Schema(description = "Preparation time in minutes")
  private Integer preparationTime;

  @Schema(description = "Spice level (0-5)")
  private Integer spiceLevel;

  @Schema(description = "Vegetarian flag")
  private boolean vegetarian;

  @Schema(description = "Vegan flag")
  private boolean vegan;

  @Schema(description = "Gluten-free flag")
  private boolean glutenFree;

  @Schema(description = "Contains dairy flag")
  private boolean containsDairy;

  @Schema(description = "Contains nuts flag")
  private boolean containsNuts;

  @Schema(description = "Halal certified flag")
  private boolean halal;

  @Schema(description = "Jain-friendly flag")
  private boolean jain;

  @Schema(description = "Nutritional information")
  private NutritionalInfoVO nutritionalInfo;

  @Schema(description = "Ingredients list")
  private List<String> ingredients;

  @Schema(description = "Allergens set")
  private Set<String> allergens;

  @Schema(description = "Customization options")
  private List<CustomizationVO> customizations;

  @Schema(description = "Availability schedule")
  private AvailabilityScheduleVO availabilitySchedule;

  @Schema(description = "Dynamic pricing configuration")
  private DynamicPricingConfigVO dynamicPricingConfig;

  @Schema(description = "Tags")
  private Set<String> tags;

  @Schema(description = "Number of servings")
  private Integer serves;

  @Schema(description = "Item status")
  private MenuItemStatus status;

  @Schema(description = "Total orders")
  private Integer totalOrders;

  @Schema(description = "Average rating")
  private BigDecimal averageRating;

  @Schema(description = "Review count")
  private Integer reviewCount;

  @Schema(description = "Creation timestamp")
  private Instant createdAt;

  @Schema(description = "Last update timestamp")
  private Instant updatedAt;
}

