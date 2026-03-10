package com.foodai.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating a cart item.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update a cart item")
public class UpdateCartItemRequest {

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Max(value = 99, message = "Quantity cannot exceed 99")
    @Schema(description = "New quantity (0 to remove)", example = "3")
    private Integer quantity;

    @Size(max = 500, message = "Special instructions cannot exceed 500 characters")
    @Schema(description = "Updated special instructions")
    private String specialInstructions;
}

