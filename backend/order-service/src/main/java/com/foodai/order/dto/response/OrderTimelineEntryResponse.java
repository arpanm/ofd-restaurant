package com.foodai.order.dto.response;

import com.foodai.order.domain.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for order timeline entry.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order timeline entry response")
public class OrderTimelineEntryResponse {

    @Schema(description = "Status at this point")
    private OrderStatus status;

    @Schema(description = "Status description")
    private String description;

    @Schema(description = "Timestamp")
    private Instant timestamp;

    @Schema(description = "Who updated the status")
    private String updatedBy;

    @Schema(description = "Additional notes")
    private String notes;
}

