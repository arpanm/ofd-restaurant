package com.foodai.restaurant.dto.response;

import com.foodai.restaurant.domain.model.OnboardingType;
import com.foodai.restaurant.domain.model.RestaurantStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Response DTO for Restaurant with full details.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponse {
    private String id;
    private String name;
    private String description;
    private String logo;
    private String coverImage;
    private String fssaiLicenseNumber;
    private String gstNumber;
    private String panNumber;
    private BankAccountResponse bankAccount;
    private List<OwnerResponse> owners;
    private List<ContactResponse> contacts;
    private List<String> cuisineTypes;
    private List<DocumentResponse> documents;
    private ContractResponse contract;
    private OnboardingType onboardingType;
    private String onboardedBy;
    private List<OutletResponse> outlets;
    private Double averageRating;
    private Integer totalReviews;
    private Integer totalOrders;
    private Boolean acceptsOrders;
    private RestaurantStatus status;
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;
}


