package com.foodai.menu.dto.response;

import com.foodai.menu.domain.model.MenuItemStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

/**
 * Response DTO for Menu Item (Summary view).
 *
 * <p>This is a lightweight response used for list views and search results.
 * For detailed information, use {@link MenuItemDetailResponse}.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Menu item summary response")
public class MenuItemResponse {

  @Schema(description = "Unique identifier", example = "507f1f77bcf86cd799439011")
  private String id;

  @Schema(description = "Restaurant ID", example = "rest123")
  private String restaurantId;

  @Schema(description = "Item name", example = "Butter Chicken")
  private String name;

  @Schema(description = "Brief description", example = "Rich and creamy North Indian curry")
  private String description;

  @Schema(description = "Category", example = "Main Course")
  private String category;

  @Schema(description = "Base price", example = "350.00")
  private BigDecimal basePrice;

  @Schema(description = "Primary image URL", example = "https://images.foodai.com/item1.jpg")
  private String primaryImage;

  @Schema(description = "Current availability status", example = "true")
  private boolean available;

  @Schema(description = "Preparation time in minutes", example = "25")
  private Integer preparationTime;

  @Schema(description = "Spice level (0-5)", example = "3")
  private Integer spiceLevel;

  @Schema(description = "Whether the item is vegetarian", example = "false")
  private boolean vegetarian;

  @Schema(description = "Whether the item is vegan", example = "false")
  private boolean vegan;

  @Schema(description = "Whether the item is gluten-free", example = "false")
  private boolean glutenFree;

  @Schema(description = "Tags", example = "[\"spicy\", \"popular\", \"north-indian\"]")
  private Set<String> tags;

  @Schema(description = "Item status", example = "ACTIVE")
  private MenuItemStatus status;

  @Schema(description = "Total orders", example = "450")
  private Integer totalOrders;

  @Schema(description = "Average rating (0-5)", example = "4.5")
  private BigDecimal averageRating;

  @Schema(description = "Number of reviews", example = "89")
  private Integer reviewCount;

  @Schema(description = "Creation timestamp")
  private Instant createdAt;

  @Schema(description = "Last update timestamp")
  private Instant updatedAt;

  /**
   * Checks if the item is popular (high order count).
   *
   * @return true if popular
   */
  public boolean isPopular() {
    return totalOrders != null && totalOrders >= 100;
  }

  /**
   * Checks if the item is highly rated.
   *
   * @return true if rating >= 4.0
   */
  public boolean isHighlyRated() {
    return averageRating != null && averageRating.compareTo(new BigDecimal("4.0")) >= 0;
  }
}

