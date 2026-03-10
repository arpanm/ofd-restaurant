package com.foodai.promotion.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for coupon validation.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Coupon validation response")
public class CouponValidationResponse {

  @Schema(description = "Whether coupon is valid")
  private boolean valid;

  @Schema(description = "Error message if invalid")
  private String errorMessage;

  @Schema(description = "Error code if invalid")
  private String errorCode;

  @Schema(description = "Coupon details if valid")
  private CouponResponse coupon;

  @Schema(description = "Calculated discount amount")
  private BigDecimal discountAmount;

  @Schema(description = "Final amount after discount")
  private BigDecimal finalAmount;

  /**
   * Creates a valid response.
   *
   * @param coupon the coupon
   * @param discountAmount the discount amount
   * @param orderValue the order value
   * @return validation response
   */
  public static CouponValidationResponse valid(
      CouponResponse coupon, BigDecimal discountAmount, BigDecimal orderValue) {
    return CouponValidationResponse.builder()
        .valid(true)
        .coupon(coupon)
        .discountAmount(discountAmount)
        .finalAmount(orderValue.subtract(discountAmount))
        .build();
  }

  /**
   * Creates an invalid response.
   *
   * @param errorCode the error code
   * @param errorMessage the error message
   * @return validation response
   */
  public static CouponValidationResponse invalid(String errorCode, String errorMessage) {
    return CouponValidationResponse.builder()
        .valid(false)
        .errorCode(errorCode)
        .errorMessage(errorMessage)
        .build();
  }
}

