package com.foodai.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for cart item customization.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Customization applied to a cart item")
public class CartItemCustomizationDTO {

    @NotBlank(message = "Customization ID is required")
    @Schema(description = "Customization group ID", example = "size")
    private String customizationId;

    @Schema(description = "Customization group name", example = "Size")
    private String customizationName;

    @NotBlank(message = "Option ID is required")
    @Schema(description = "Selected option ID", example = "large")
    private String optionId;

    @Schema(description = "Selected option name", example = "Large")
    private String optionName;

    @Schema(description = "Additional cost for this customization", example = "50.00")
    private BigDecimal additionalCost;
}

