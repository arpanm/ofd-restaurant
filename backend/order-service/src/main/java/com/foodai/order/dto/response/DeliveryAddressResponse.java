package com.foodai.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Response DTO for delivery address.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Delivery address response")
public class DeliveryAddressResponse {

    @Schema(description = "Address label")
    private String label;

    @Schema(description = "Recipient name")
    private String recipientName;

    @Schema(description = "Contact phone")
    private String contactPhone;

    @Schema(description = "Flat/house number")
    private String flatNumber;

    @Schema(description = "Building name")
    private String building;

    @Schema(description = "Street address")
    private String street;

    @Schema(description = "Landmark")
    private String landmark;

    @Schema(description = "Area/locality")
    private String area;

    @Schema(description = "City")
    private String city;

    @Schema(description = "State")
    private String state;

    @Schema(description = "Pincode")
    private String pincode;

    @Schema(description = "Latitude")
    private BigDecimal latitude;

    @Schema(description = "Longitude")
    private BigDecimal longitude;

    @Schema(description = "Delivery instructions")
    private String deliveryInstructions;

    @Schema(description = "Full formatted address")
    private String fullAddress;
}

