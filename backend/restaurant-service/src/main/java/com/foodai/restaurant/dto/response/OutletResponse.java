package com.foodai.restaurant.dto.response;

import com.foodai.restaurant.domain.model.OutletStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Response DTO for Outlet with full details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutletResponse {
    private String outletId;
    private String outletName;
    private String outletCode;
    private AddressResponse address;
    private List<ContactResponse> contacts;
    private List<OperatingHoursResponse> operatingHours;
    private OutletStatus status;
    private String approvedBy;
    private Instant approvedAt;
    private String rejectionReason;
    private ServiceabilityResponse serviceabilityConfig;
    private TATConfigResponse tatConfig;
    private Boolean acceptsOrders;
    private Double minimumOrderValue;
    private Double deliveryFee;
    private Boolean selfDelivery;
    private Double averageRating;
    private Integer totalReviews;
    private Integer totalOrders;
    private Integer currentOrderQueue;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;
}


