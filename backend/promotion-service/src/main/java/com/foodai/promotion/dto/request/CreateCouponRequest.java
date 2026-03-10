package com.foodai.promotion.dto.request;

import com.foodai.promotion.domain.model.PromotionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new coupon.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new coupon")
public class CreateCouponRequest {

  @NotBlank(message = "Coupon code is required")
  @Size(min = 3, max = 20, message = "Code must be between 3 and 20 characters")
  @Schema(description = "Coupon code", example = "SAVE20")
  private String code;

  @Size(max = 100, message = "Name must not exceed 100 characters")
  @Schema(description = "Coupon name", example = "20% Off Coupon")
  private String name;

  @Size(max = 500, message = "Description must not exceed 500 characters")
  @Schema(description = "Coupon description")
  private String description;

  @NotNull(message = "Discount type is required")
  @Schema(description = "Type of discount", example = "PERCENTAGE")
  private PromotionType type;

  @Schema(description = "Discount value", example = "20")
  private BigDecimal value;

  @Positive(message = "Discount value must be positive")
  @Schema(description = "Discount value (alias)", example = "20")
  private BigDecimal discountValue;

  @Schema(description = "Minimum order value", example = "300")
  private BigDecimal minOrderValue;

  @Schema(description = "Maximum discount cap", example = "100")
  private BigDecimal maxDiscount;

  @Schema(description = "Valid from date")
  private Instant validFrom;

  @Schema(description = "Valid until date")
  private Instant validUntil;

  @Schema(description = "Total usage limit")
  private Integer totalLimit;

  @Schema(description = "Per user limit")
  private Integer perUserLimit;

  @Schema(description = "Restaurant ID (null for platform-wide)")
  private String restaurantId;

  @Schema(description = "Linked promotion ID")
  private String promotionId;

  @Schema(description = "First order only")
  private boolean firstOrderOnly;

  @Schema(description = "New users only")
  private boolean newUsersOnly;

  @Schema(description = "Applicable restaurant IDs")
  private Set<String> applicableRestaurants;

  @Schema(description = "Applicable category names")
  private Set<String> applicableCategories;
}

