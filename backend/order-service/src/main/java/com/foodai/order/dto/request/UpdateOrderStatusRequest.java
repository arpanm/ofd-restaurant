package com.foodai.order.dto.request;

import com.foodai.order.domain.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating order status.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update order status")
public class UpdateOrderStatusRequest {

    @NotNull(message = "New status is required")
    @Schema(description = "New order status", example = "PREPARING")
    private OrderStatus status;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Schema(description = "Status change description")
    private String description;

    @NotBlank(message = "Updated by is required")
    @Schema(description = "Who is updating the status", example = "restaurant-123")
    private String updatedBy;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    @Schema(description = "Additional notes")
    private String notes;
}

