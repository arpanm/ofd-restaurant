package com.foodai.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for delivery address.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Delivery address details")
public class DeliveryAddressDTO {

    @Schema(description = "Address label", example = "Home")
    private String label;

    @NotBlank(message = "Recipient name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    @Schema(description = "Recipient name", example = "John Doe")
    private String recipientName;

    @NotBlank(message = "Contact phone is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
    @Schema(description = "Contact phone", example = "+919876543210")
    private String contactPhone;

    @Size(max = 50, message = "Flat number cannot exceed 50 characters")
    @Schema(description = "Flat/house number", example = "101")
    private String flatNumber;

    @Size(max = 100, message = "Building name cannot exceed 100 characters")
    @Schema(description = "Building/apartment name", example = "Sunshine Apartments")
    private String building;

    @NotBlank(message = "Street is required")
    @Size(max = 200, message = "Street cannot exceed 200 characters")
    @Schema(description = "Street address", example = "MG Road")
    private String street;

    @Size(max = 100, message = "Landmark cannot exceed 100 characters")
    @Schema(description = "Nearby landmark", example = "Near City Mall")
    private String landmark;

    @Size(max = 100, message = "Area cannot exceed 100 characters")
    @Schema(description = "Area/locality", example = "Koramangala")
    private String area;

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City cannot exceed 50 characters")
    @Schema(description = "City", example = "Bangalore")
    private String city;

    @Size(max = 50, message = "State cannot exceed 50 characters")
    @Schema(description = "State", example = "Karnataka")
    private String state;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Invalid pincode")
    @Schema(description = "Pincode", example = "560001")
    private String pincode;

    @Schema(description = "Latitude", example = "12.9716")
    private BigDecimal latitude;

    @Schema(description = "Longitude", example = "77.5946")
    private BigDecimal longitude;

    @Size(max = 500, message = "Delivery instructions cannot exceed 500 characters")
    @Schema(description = "Delivery instructions", example = "Leave at door")
    private String deliveryInstructions;
}

