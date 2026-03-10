package com.foodai.promotion.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for validating a coupon.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to validate a coupon")
public class ValidateCouponRequest {

  @NotBlank(message = "Coupon code is required")
  @Schema(description = "Coupon code to validate", example = "SAVE20")
  private String code;

  @NotBlank(message = "User ID is required")
  @Schema(description = "User ID")
  private String userId;

  @NotNull(message = "Order value is required")
  @Positive(message = "Order value must be positive")
  @Schema(description = "Order subtotal", example = "500")
  private BigDecimal orderValue;

  @Schema(description = "Restaurant ID")
  private String restaurantId;

  @Schema(description = "Whether this is user's first order")
  private boolean firstOrder;

  @Schema(description = "Whether this is a new user")
  private boolean newUser;
}

