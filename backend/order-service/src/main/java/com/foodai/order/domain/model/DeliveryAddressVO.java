package com.foodai.order.domain.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Value object representing a delivery address.
 *
 * @author FoodAI Team
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryAddressVO {

    private String label;
    private String recipientName;
    private String contactPhone;
    private String flatNumber;
    private String building;
    private String street;
    private String landmark;
    private String area;
    private String city;
    private String state;
    private String pincode;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String deliveryInstructions;

    /**
     * Returns the full formatted address string.
     *
     * @return formatted address
     */
    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (flatNumber != null && !flatNumber.isBlank()) {
            sb.append(flatNumber).append(", ");
        }
        if (building != null && !building.isBlank()) {
            sb.append(building).append(", ");
        }
        if (street != null && !street.isBlank()) {
            sb.append(street).append(", ");
        }
        if (landmark != null && !landmark.isBlank()) {
            sb.append("Near ").append(landmark).append(", ");
        }
        if (area != null && !area.isBlank()) {
            sb.append(area).append(", ");
        }
        if (city != null && !city.isBlank()) {
            sb.append(city).append(", ");
        }
        if (state != null && !state.isBlank()) {
            sb.append(state).append(" - ");
        }
        if (pincode != null && !pincode.isBlank()) {
            sb.append(pincode);
        }
        return sb.toString().replaceAll(", $", "");
    }
}

