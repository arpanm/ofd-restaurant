package com.foodai.promotion.dto.request;

import com.foodai.promotion.domain.model.PromotionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
 * Request DTO for creating a new promotion.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new promotion")
public class CreatePromotionRequest {

  @NotBlank(message = "Promotion name is required")
  @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
  @Schema(description = "Promotion name", example = "Weekend Special 20% Off")
  private String name;

  @Size(max = 500, message = "Description must not exceed 500 characters")
  @Schema(description = "Promotion description", example = "Get 20% off on all orders this weekend")
  private String description;

  @NotNull(message = "Promotion type is required")
  @Schema(description = "Type of promotion", example = "PERCENTAGE")
  private PromotionType type;

  @Schema(description = "Restaurant ID (null for platform-wide)")
  private String restaurantId;

  @Size(max = 20, message = "Code must not exceed 20 characters")
  @Schema(description = "Promo code", example = "WEEKEND20")
  private String code;

  @Schema(description = "Discount value (percentage or fixed amount)", example = "20")
  @Positive(message = "Discount value must be positive")
  private BigDecimal discountValue;

  @Schema(description = "Minimum order value", example = "300")
  private BigDecimal minOrderValue;

  @Schema(description = "Maximum discount cap", example = "200")
  private BigDecimal maxDiscount;

  @NotNull(message = "Start date is required")
  @Schema(description = "Start date and time")
  private Instant startDate;

  @NotNull(message = "End date is required")
  @Schema(description = "End date and time")
  private Instant endDate;

  @Schema(description = "Start time for daily promotions")
  private LocalTime startTime;

  @Schema(description = "End time for daily promotions")
  private LocalTime endTime;

  @Schema(description = "Days of week when promotion is active")
  private Set<DayOfWeek> daysOfWeek;

  @Schema(description = "Total usage limit")
  private Integer totalUsageLimit;

  @Schema(description = "Per user usage limit")
  private Integer perUserLimit;

  @Schema(description = "Applicable restaurant IDs")
  private Set<String> applicableRestaurants;

  @Schema(description = "Applicable menu item IDs")
  private Set<String> applicableMenuItems;

  @Schema(description = "Applicable category names")
  private Set<String> applicableCategories;

  @Schema(description = "Terms and conditions")
  private String terms;

  @Schema(description = "Whether platform-wide promotion")
  private boolean platformWide;

  @Schema(description = "Whether featured promotion")
  private boolean featured;

  @Schema(description = "Display priority")
  private int priority;

  @Schema(description = "Banner image URL")
  private String bannerUrl;
}

