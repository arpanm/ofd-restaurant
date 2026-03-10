package com.foodai.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for applying a coupon to the cart.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to apply a coupon code")
public class ApplyCouponRequest {

    @NotBlank(message = "Coupon code is required")
    @Pattern(regexp = "^[A-Z0-9]{4,20}$", message = "Invalid coupon code format")
    @Schema(description = "Coupon code to apply", example = "FIRST50")
    private String couponCode;
}

