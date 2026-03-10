package com.foodai.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for assigning a rider to an order.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to assign a rider to an order")
public class AssignRiderRequest {

    @NotBlank(message = "Rider ID is required")
    @Schema(description = "Rider ID", example = "rider-123")
    private String riderId;

    @NotBlank(message = "Rider name is required")
    @Schema(description = "Rider name", example = "Rajesh Kumar")
    private String riderName;

    @NotBlank(message = "Rider phone is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
    @Schema(description = "Rider phone number", example = "+919876543210")
    private String riderPhone;
}

