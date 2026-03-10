package com.foodai.menu.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Request DTO for searching menu items with filters.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Search request for menu items with filters")
public class MenuItemSearchRequest {

  @Schema(description = "Restaurant ID to search within", example = "rest123")
  private String restaurantId;

  @Schema(description = "Search text (searches in name and description)", example = "chicken")
  private String searchText;

  @Schema(description = "Category filter", example = "Main Course")
  private String category;

  @Schema(description = "Minimum price", example = "100")
  private BigDecimal minPrice;

  @Schema(description = "Maximum price", example = "500")
  private BigDecimal maxPrice;

  @Schema(description = "Vegetarian filter", example = "true")
  private Boolean vegetarian;

  @Schema(description = "Vegan filter", example = "false")
  private Boolean vegan;

  @Schema(description = "Gluten-free filter", example = "false")
  private Boolean glutenFree;

  @Schema(description = "Available items only", example = "true")
  private Boolean available;

  @Schema(description = "Tags to filter by", example = "[\"spicy\", \"popular\"]")
  private Set<String> tags;

  @Schema(description = "Minimum spice level (0-5)", example = "0", minimum = "0", maximum = "5")
  private Integer minSpiceLevel;

  @Schema(description = "Maximum spice level (0-5)", example = "5", minimum = "0", maximum = "5")
  private Integer maxSpiceLevel;

  @Schema(description = "Minimum rating", example = "4.0")
  private BigDecimal minRating;

  @Schema(description = "Maximum preparation time (minutes)", example = "30")
  private Integer maxPrepTime;

  @Schema(description = "Sort by field", example = "name", allowableValues = {"name", "basePrice", "totalOrders", "averageRating", "createdAt"})
  private String sortBy;

  @Schema(description = "Sort direction", example = "asc", allowableValues = {"asc", "desc"})
  private String sortDirection;

  @Schema(description = "Page number (0-indexed)", example = "0")
  private Integer page;

  @Schema(description = "Page size", example = "20")
  private Integer size;

  @AssertTrue(message = "Minimum price must be less than or equal to maximum price")
  private boolean isValidPriceRange() {
    if (minPrice != null && maxPrice != null) {
      return minPrice.compareTo(maxPrice) <= 0;
    }
    return true;
  }
}

