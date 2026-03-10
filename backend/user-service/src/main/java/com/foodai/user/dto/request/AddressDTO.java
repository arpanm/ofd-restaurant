package com.foodai.user.dto.request;

import com.foodai.user.domain.model.AddressType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for address data.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Address details")
public class AddressDTO {

    @Schema(description = "Address ID (for updates)")
    private String id;

    @Size(max = 50, message = "Label must be at most 50 characters")
    @Schema(description = "Address label", example = "Home")
    private String label;

    @Schema(description = "Address type")
    private AddressType type;

    @Size(max = 100, message = "Recipient name must be at most 100 characters")
    @Schema(description = "Recipient name", example = "John Doe")
    private String recipientName;

    @Pattern(regexp = "^\\+?[1-9]\\d{9,14}$", message = "Invalid phone number format")
    @Schema(description = "Contact phone", example = "+919876543210")
    private String contactPhone;

    @Size(max = 50, message = "Flat number must be at most 50 characters")
    @Schema(description = "Flat/apartment number", example = "A-101")
    private String flatNumber;

    @Size(max = 100, message = "Building name must be at most 100 characters")
    @Schema(description = "Building/apartment name", example = "Sunshine Apartments")
    private String building;

    @NotBlank(message = "Street address is required")
    @Size(max = 200, message = "Street must be at most 200 characters")
    @Schema(description = "Street address", example = "123 Main Street")
    private String street;

    @Size(max = 200, message = "Street2 must be at most 200 characters")
    @Schema(description = "Additional street info")
    private String street2;

    @Size(max = 100, message = "Landmark must be at most 100 characters")
    @Schema(description = "Landmark for navigation", example = "Near City Mall")
    private String landmark;

    @Size(max = 100, message = "Area must be at most 100 characters")
    @Schema(description = "Area/locality", example = "Bandra West")
    private String area;

    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must be at most 100 characters")
    @Schema(description = "City name", example = "Mumbai")
    private String city;

    @Size(max = 100, message = "State must be at most 100 characters")
    @Schema(description = "State name", example = "Maharashtra")
    private String state;

    @Schema(description = "Country name", example = "India")
    private String country;

    @NotBlank(message = "Pincode is required")
    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid pincode format")
    @Schema(description = "PIN code", example = "400050")
    private String pincode;

    @Schema(description = "Latitude", example = "19.0596")
    private Double latitude;

    @Schema(description = "Longitude", example = "72.8295")
    private Double longitude;

    @Schema(description = "Google Plus Code")
    private String plusCode;

    @Size(max = 500, message = "Delivery instructions must be at most 500 characters")
    @Schema(description = "Delivery instructions", example = "Ring bell twice")
    private String deliveryInstructions;

    @Schema(description = "Set as default address")
    private Boolean isDefault;
}

