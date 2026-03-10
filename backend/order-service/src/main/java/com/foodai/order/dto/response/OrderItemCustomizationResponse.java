package com.foodai.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for order item customization.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order item customization response")
public class OrderItemCustomizationResponse {

    @Schema(description = "Customization group ID")
    private String customizationId;

    @Schema(description = "Customization group name")
    private String customizationName;

    @Schema(description = "Selected option ID")
    private String optionId;

    @Schema(description = "Selected option name")
    private String optionName;

    @Schema(description = "Additional cost")
    private BigDecimal additionalCost;
}

