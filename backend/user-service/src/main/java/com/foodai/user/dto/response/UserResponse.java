package com.foodai.user.dto.response;

import com.foodai.user.domain.model.Gender;
import com.foodai.user.domain.model.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Response DTO for User.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User response")
public class UserResponse {

    @Schema(description = "User ID")
    private String id;

    @Schema(description = "Email address")
    private String email;

    @Schema(description = "Phone number")
    private String phone;

    @Schema(description = "First name")
    private String firstName;

    @Schema(description = "Last name")
    private String lastName;

    @Schema(description = "Display name")
    private String displayName;

    @Schema(description = "Full name")
    private String fullName;

    @Schema(description = "Avatar URL")
    private String avatarUrl;

    @Schema(description = "Date of birth")
    private LocalDate dateOfBirth;

    @Schema(description = "Gender")
    private Gender gender;

    @Schema(description = "Saved addresses")
    private List<AddressResponse> addresses;

    @Schema(description = "Dietary preferences")
    private DietaryPreferencesResponse dietaryPreferences;

    @Schema(description = "Cuisine preferences")
    private List<String> cuisinePreferences;

    @Schema(description = "Notification preferences")
    private NotificationPreferencesResponse notificationPreferences;

    @Schema(description = "Personalization settings")
    private PersonalizationSettingsResponse personalizationSettings;

    @Schema(description = "Default address ID")
    private String defaultAddressId;

    @Schema(description = "Default payment method ID")
    private String defaultPaymentMethodId;

    @Schema(description = "Favorite restaurant IDs")
    private List<String> favoriteRestaurantIds;

    @Schema(description = "Favorite menu item IDs")
    private List<String> favoriteMenuItemIds;

    @Schema(description = "Language preference")
    private String languagePreference;

    @Schema(description = "Timezone")
    private String timezone;

    @Schema(description = "Referral code")
    private String referralCode;

    @Schema(description = "Account status")
    private UserStatus status;

    @Schema(description = "Email verified")
    private boolean emailVerified;

    @Schema(description = "Phone verified")
    private boolean phoneVerified;

    @Schema(description = "Last login timestamp")
    private Instant lastLoginAt;

    @Schema(description = "Total orders")
    private Integer totalOrders;

    @Schema(description = "Total spent (INR)")
    private Double totalSpent;

    @Schema(description = "Average order value (INR)")
    private Double averageOrderValue;

    @Schema(description = "Created at")
    private Instant createdAt;

    @Schema(description = "Updated at")
    private Instant updatedAt;
}

