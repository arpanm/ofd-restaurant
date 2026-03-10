package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value Object representing an address.
 * Used for outlet locations.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressVO {
    /**
     * Street address
     */
    private String street;
    
    /**
     * City name
     */
    private String city;
    
    /**
     * State name
     */
    private String state;
    
    /**
     * Postal code (6 digits)
     */
    private String pincode;
    
    /**
     * Latitude coordinate for geo-location
     */
    private Double latitude;
    
    /**
     * Longitude coordinate for geo-location
     */
    private Double longitude;
    
    /**
     * Nearby landmark for easy identification
     */
    private String landmark;
}


