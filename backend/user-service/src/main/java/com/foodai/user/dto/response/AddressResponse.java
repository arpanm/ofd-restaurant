package com.foodai.user.dto.response;

import com.foodai.user.domain.model.AddressType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for Address.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Address response")
public class AddressResponse {

    @Schema(description = "Address ID")
    private String id;

    @Schema(description = "Address label")
    private String label;

    @Schema(description = "Address type")
    private AddressType type;

    @Schema(description = "Recipient name")
    private String recipientName;

    @Schema(description = "Contact phone")
    private String contactPhone;

    @Schema(description = "Flat number")
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

    @Schema(description = "Country")
    private String country;

    @Schema(description = "Pincode")
    private String pincode;

    @Schema(description = "Latitude")
    private Double latitude;

    @Schema(description = "Longitude")
    private Double longitude;

    @Schema(description = "Delivery instructions")
    private String deliveryInstructions;

    @Schema(description = "Formatted address")
    private String formattedAddress;

    @Schema(description = "Is default address")
    private boolean isDefault;

    @Schema(description = "Is verified")
    private boolean verified;

    @Schema(description = "Created at")
    private Instant createdAt;
}

