package com.foodai.restaurant.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Address information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
    private String street;
    private String city;
    private String state;
    private String pincode;
    private Double latitude;
    private Double longitude;
    private String landmark;
}


