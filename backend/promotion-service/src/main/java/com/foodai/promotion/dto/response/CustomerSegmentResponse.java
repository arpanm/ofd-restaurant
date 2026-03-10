package com.foodai.promotion.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for CustomerSegment.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customer segment response")
public class CustomerSegmentResponse {

  @Schema(description = "Segment ID")
  private String id;

  @Schema(description = "Segment name")
  private String name;

  @Schema(description = "Segment description")
  private String description;

  @Schema(description = "Restaurant ID")
  private String restaurantId;

  @Schema(description = "Customer count")
  private int customerCount;

  @Schema(description = "Custom segment flag")
  private boolean custom;

  @Schema(description = "AI suggested flag")
  private boolean aiSuggested;

  @Schema(description = "Active status")
  private boolean active;

  @Schema(description = "Criteria details")
  private CriteriaResponse criteria;

  @Schema(description = "Last refreshed")
  private Instant lastRefreshed;

  @Schema(description = "Created at")
  private Instant createdAt;

  @Schema(description = "Updated at")
  private Instant updatedAt;

  /**
   * Criteria response.
   *
   * @author FoodAI Team
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CriteriaResponse {
    @Schema(description = "Min order count")
    private Integer minOrderCount;

    @Schema(description = "Max order count")
    private Integer maxOrderCount;

    @Schema(description = "Min total spent")
    private BigDecimal minTotalSpent;

    @Schema(description = "Max total spent")
    private BigDecimal maxTotalSpent;

    @Schema(description = "Min AOV")
    private BigDecimal minAverageOrderValue;

    @Schema(description = "Max AOV")
    private BigDecimal maxAverageOrderValue;

    @Schema(description = "Days since last order")
    private Integer daysSinceLastOrder;

    @Schema(description = "Favorite categories")
    private Set<String> favoriteCategories;

    @Schema(description = "Favorite cuisines")
    private Set<String> favoriteCuisines;

    @Schema(description = "Min age")
    private Integer minAge;

    @Schema(description = "Max age")
    private Integer maxAge;

    @Schema(description = "Cities")
    private Set<String> cities;

    @Schema(description = "Loyalty tiers")
    private Set<String> loyaltyTiers;
  }
}

