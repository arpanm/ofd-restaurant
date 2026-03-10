package com.foodai.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Value Object representing a user's delivery address.
 * Supports multiple address types (home, work, other) with geolocation.
 *
 * <p>Covers features from:
 * <ul>
 *   <li>USER_ONBOARDING.md - Address management</li>
 *   <li>CHECKOUT_FLOW.md - Saved addresses for quick checkout</li>
 *   <li>MAP_BASED_ORDER_TRACKING.md - Delivery location</li>
 * </ul>
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressVO {

    /**
     * Unique address identifier
     */
    private String id;

    /**
     * Address label (e.g., "Home", "Work", "Mom's Place")
     */
    private String label;

    /**
     * Address type
     */
    @Builder.Default
    private AddressType type = AddressType.OTHER;

    /**
     * Full name of the recipient
     */
    private String recipientName;

    /**
     * Contact phone for this address
     */
    private String contactPhone;

    /**
     * Building/flat number
     */
    private String flatNumber;

    /**
     * Building/apartment name
     */
    private String building;

    /**
     * Street address line 1
     */
    private String street;

    /**
     * Street address line 2
     */
    private String street2;

    /**
     * Landmark for easier navigation
     */
    private String landmark;

    /**
     * Area/locality name
     */
    private String area;

    /**
     * City name
     */
    private String city;

    /**
     * State/province name
     */
    private String state;

    /**
     * Country name
     */
    @Builder.Default
    private String country = "India";

    /**
     * Postal/PIN code
     */
    private String pincode;

    /**
     * Latitude for geolocation
     */
    private Double latitude;

    /**
     * Longitude for geolocation
     */
    private Double longitude;

    /**
     * Plus code for precise location (Google Plus Codes)
     */
    private String plusCode;

    /**
     * Delivery instructions (e.g., "Ring bell twice")
     */
    private String deliveryInstructions;

    /**
     * Whether this is the default address
     */
    @Builder.Default
    private boolean isDefault = false;

    /**
     * Whether address is verified
     */
    @Builder.Default
    private boolean verified = false;

    /**
     * Creation timestamp
     */
    private Instant createdAt;

    /**
     * Last update timestamp
     */
    private Instant updatedAt;

    /**
     * Gets formatted single-line address.
     *
     * @return formatted address string
     */
    public String getFormattedAddress() {
        StringBuilder sb = new StringBuilder();
        if (flatNumber != null && !flatNumber.isBlank()) {
            sb.append(flatNumber);
        }
        if (building != null && !building.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(building);
        }
        if (street != null && !street.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(street);
        }
        if (area != null && !area.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(area);
        }
        if (city != null && !city.isBlank()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        if (pincode != null && !pincode.isBlank()) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(pincode);
        }
        return sb.toString();
    }

    /**
     * Checks if address has valid geolocation.
     *
     * @return true if latitude and longitude are set
     */
    public boolean hasGeolocation() {
        return latitude != null && longitude != null;
    }
}

