package com.foodai.promotion.dto.request;

import com.foodai.promotion.domain.model.SegmentCriteriaVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a customer segment.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a customer segment")
public class CreateSegmentRequest {

  @NotBlank(message = "Segment name is required")
  @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
  @Schema(description = "Segment name", example = "VIP Customers")
  private String name;

  @Size(max = 500, message = "Description must not exceed 500 characters")
  @Schema(description = "Segment description")
  private String description;

  @Schema(description = "Restaurant ID (null for platform-wide)")
  private String restaurantId;

  @Schema(description = "Segment criteria")
  private SegmentCriteriaVO criteria;

  @Schema(description = "Whether segment is dynamic (auto-calculated) or static")
  private boolean dynamic;

  @Schema(description = "Static user IDs for manual segments")
  private Set<String> staticUserIds;

  // Criteria fields
  @Schema(description = "Minimum order count")
  private Integer minOrderCount;

  @Schema(description = "Maximum order count")
  private Integer maxOrderCount;

  @Schema(description = "Minimum total spent", example = "5000")
  private BigDecimal minTotalSpent;

  @Schema(description = "Maximum total spent")
  private BigDecimal maxTotalSpent;

  @Schema(description = "Minimum average order value")
  private BigDecimal minAverageOrderValue;

  @Schema(description = "Maximum average order value")
  private BigDecimal maxAverageOrderValue;

  @Schema(description = "Days since last order (lapsed customers)")
  private Integer daysSinceLastOrder;

  @Schema(description = "Favorite categories")
  private Set<String> favoriteCategories;

  @Schema(description = "Favorite cuisines")
  private Set<String> favoriteCuisines;

  @Schema(description = "Minimum age")
  private Integer minAge;

  @Schema(description = "Maximum age")
  private Integer maxAge;

  @Schema(description = "Target cities")
  private Set<String> cities;

  @Schema(description = "Loyalty tiers (BRONZE, SILVER, GOLD, etc.)")
  private Set<String> loyaltyTiers;

  @Schema(description = "Dietary preferences")
  private Set<String> dietaryPreferences;
}

