package com.foodai.promotion.dto.response;

import com.foodai.promotion.domain.model.PromotionStatus;
import com.foodai.promotion.domain.model.PromotionType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Promotion.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Promotion response")
public class PromotionResponse {

  @Schema(description = "Promotion ID")
  private String id;

  @Schema(description = "Promotion name")
  private String name;

  @Schema(description = "Promotion description")
  private String description;

  @Schema(description = "Promotion type")
  private PromotionType type;

  @Schema(description = "Promotion status")
  private PromotionStatus status;

  @Schema(description = "Restaurant ID")
  private String restaurantId;

  @Schema(description = "Promo code")
  private String code;

  @Schema(description = "Discount value")
  private BigDecimal discountValue;

  @Schema(description = "Minimum order value")
  private BigDecimal minOrderValue;

  @Schema(description = "Maximum discount")
  private BigDecimal maxDiscount;

  @Schema(description = "Start date")
  private Instant startDate;

  @Schema(description = "End date")
  private Instant endDate;

  @Schema(description = "Start time")
  private LocalTime startTime;

  @Schema(description = "End time")
  private LocalTime endTime;

  @Schema(description = "Days of week")
  private Set<DayOfWeek> daysOfWeek;

  @Schema(description = "Total usage limit")
  private Integer totalUsageLimit;

  @Schema(description = "Per user limit")
  private Integer perUserLimit;

  @Schema(description = "Current usage count")
  private int currentUsage;

  @Schema(description = "Remaining usage")
  private Integer remainingUsage;

  @Schema(description = "Usage percentage")
  private double usagePercentage;

  @Schema(description = "Applicable restaurants")
  private Set<String> applicableRestaurants;

  @Schema(description = "Applicable menu items")
  private Set<String> applicableMenuItems;

  @Schema(description = "Applicable categories")
  private Set<String> applicableCategories;

  @Schema(description = "Terms and conditions")
  private String terms;

  @Schema(description = "Platform-wide flag")
  private boolean platformWide;

  @Schema(description = "Featured flag")
  private boolean featured;

  @Schema(description = "Display priority")
  private int priority;

  @Schema(description = "Banner URL")
  private String bannerUrl;

  @Schema(description = "Campaign ID")
  private String campaignId;

  @Schema(description = "Created at")
  private Instant createdAt;

  @Schema(description = "Updated at")
  private Instant updatedAt;
}

