package com.foodai.promotion.dto.response;

import com.foodai.promotion.domain.model.PromotionType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Coupon.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Coupon response")
public class CouponResponse {

  @Schema(description = "Coupon ID")
  private String id;

  @Schema(description = "Coupon code")
  private String code;

  @Schema(description = "Coupon name")
  private String name;

  @Schema(description = "Coupon description")
  private String description;

  @Schema(description = "Discount type")
  private PromotionType type;

  @Schema(description = "Discount value")
  private BigDecimal value;

  @Schema(description = "Discount value (alias)")
  private BigDecimal discountValue;

  @Schema(description = "Minimum order value")
  private BigDecimal minOrderValue;

  @Schema(description = "Maximum discount")
  private BigDecimal maxDiscount;

  @Schema(description = "Valid from")
  private Instant validFrom;

  @Schema(description = "Valid until")
  private Instant validUntil;

  @Schema(description = "Total limit")
  private Integer totalLimit;

  @Schema(description = "Per user limit")
  private Integer perUserLimit;

  @Schema(description = "Current usage")
  private int currentUsage;

  @Schema(description = "Remaining usage")
  private Integer remainingUsage;

  @Schema(description = "Restaurant ID")
  private String restaurantId;

  @Schema(description = "Linked promotion ID")
  private String promotionId;

  @Schema(description = "Active status")
  private boolean active;

  @Schema(description = "First order only")
  private boolean firstOrderOnly;

  @Schema(description = "New users only")
  private boolean newUsersOnly;

  @Schema(description = "Expired status")
  private boolean expired;

  @Schema(description = "Applicable restaurants")
  private Set<String> applicableRestaurants;

  @Schema(description = "Applicable categories")
  private Set<String> applicableCategories;

  @Schema(description = "Created at")
  private Instant createdAt;

  @Schema(description = "Updated at")
  private Instant updatedAt;
}

