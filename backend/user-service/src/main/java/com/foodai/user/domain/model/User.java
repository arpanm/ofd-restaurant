package com.foodai.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * User Aggregate Root.
 * Represents a consumer/customer in the FoodAI platform.
 * This is the main entity for user management including profile, addresses, and preferences.
 *
 * <p>Covers features from:
 * <ul>
 *   <li>USER_ONBOARDING.md - User registration, profile setup</li>
 *   <li>CONSUMER_INTERFACE.md - User preferences, saved items</li>
 *   <li>CHECKOUT_FLOW.md - Saved payment methods, addresses</li>
 *   <li>AI_PERSONALIZATION_FEATURES.md - Personalization settings</li>
 * </ul>
 *
 * @author FoodAI Team
 */
@Document(collection = "users")
@CompoundIndexes({
    @CompoundIndex(name = "email_deleted_idx", def = "{'email': 1, 'deleted': 1}"),
    @CompoundIndex(name = "phone_deleted_idx", def = "{'phone': 1, 'deleted': 1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Unique user identifier
     */
    @Id
    private String id;

    /**
     * User's email address (unique, required for registration)
     */
    @Indexed(unique = true, sparse = true)
    private String email;

    /**
     * User's phone number with country code (unique, required)
     */
    @Indexed(unique = true)
    private String phone;

    /**
     * User's first name
     */
    private String firstName;

    /**
     * User's last name
     */
    private String lastName;

    /**
     * Display name (can be nickname or full name)
     */
    private String displayName;

    /**
     * Profile picture URL
     */
    private String avatarUrl;

    /**
     * Date of birth for personalization and offers
     */
    private LocalDate dateOfBirth;

    /**
     * Gender for personalization (optional)
     */
    private Gender gender;

    /**
     * User's saved addresses (home, work, etc.)
     */
    @Builder.Default
    private List<AddressVO> addresses = new ArrayList<>();

    /**
     * Dietary preferences (vegetarian, vegan, allergies, etc.)
     */
    private DietaryPreferencesVO dietaryPreferences;

    /**
     * Cuisine preferences
     */
    @Builder.Default
    private List<String> cuisinePreferences = new ArrayList<>();

    /**
     * Notification preferences
     */
    private NotificationPreferencesVO notificationPreferences;

    /**
     * AI personalization settings
     */
    private PersonalizationSettingsVO personalizationSettings;

    /**
     * Saved payment methods
     */
    @Builder.Default
    private List<PaymentMethodVO> paymentMethods = new ArrayList<>();

    /**
     * Default address ID for quick checkout
     */
    private String defaultAddressId;

    /**
     * Default payment method ID for quick checkout
     */
    private String defaultPaymentMethodId;

    /**
     * Favorite restaurant IDs
     */
    @Builder.Default
    private List<String> favoriteRestaurantIds = new ArrayList<>();

    /**
     * Favorite menu item IDs
     */
    @Builder.Default
    private List<String> favoriteMenuItemIds = new ArrayList<>();

    /**
     * Recent search terms for autocomplete
     */
    @Builder.Default
    private List<String> recentSearches = new ArrayList<>();

    /**
     * Language preference
     */
    @Builder.Default
    private String languagePreference = "en";

    /**
     * Timezone for order scheduling
     */
    @Builder.Default
    private String timezone = "Asia/Kolkata";

    /**
     * Referral code for this user
     */
    private String referralCode;

    /**
     * User who referred this user
     */
    private String referredBy;

    /**
     * Account status
     */
    @Indexed
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * Email verification status
     */
    @Builder.Default
    private boolean emailVerified = false;

    /**
     * Phone verification status
     */
    @Builder.Default
    private boolean phoneVerified = false;

    /**
     * Last login timestamp
     */
    private Instant lastLoginAt;

    /**
     * Total orders placed
     */
    @Builder.Default
    private Integer totalOrders = 0;

    /**
     * Total amount spent (in INR)
     */
    @Builder.Default
    private Double totalSpent = 0.0;

    /**
     * Average order value (in INR)
     */
    @Builder.Default
    private Double averageOrderValue = 0.0;

    /**
     * User ID who created this account (for admin-created accounts)
     */
    private String createdBy;

    /**
     * Creation timestamp
     */
    @CreatedDate
    private Instant createdAt;

    /**
     * User ID who last updated this account
     */
    private String updatedBy;

    /**
     * Last update timestamp
     */
    @LastModifiedDate
    private Instant updatedAt;

    /**
     * Soft delete flag
     */
    @Indexed
    @Builder.Default
    private boolean deleted = false;

    /**
     * User ID who deleted this account
     */
    private String deletedBy;

    /**
     * Deletion timestamp
     */
    private Instant deletedAt;

    // ==================== Domain Methods ====================

    /**
     * Validates if the user entity is in a valid state.
     *
     * @throws IllegalStateException if user is invalid
     */
    public void validate() {
        if (phone == null || phone.isBlank()) {
            throw new IllegalStateException("Phone number is required");
        }
        if (firstName == null || firstName.isBlank()) {
            throw new IllegalStateException("First name is required");
        }
    }

    /**
     * Adds a new address to the user's address list.
     *
     * @param address the address to add
     */
    public void addAddress(AddressVO address) {
        if (this.addresses == null) {
            this.addresses = new ArrayList<>();
        }
        // Set as default if first address
        if (this.addresses.isEmpty()) {
            address.setDefault(true);
            this.defaultAddressId = address.getId();
        }
        this.addresses.add(address);
    }

    /**
     * Removes an address from the user's address list.
     *
     * @param addressId the ID of the address to remove
     * @return true if address was removed
     */
    public boolean removeAddress(String addressId) {
        if (this.addresses == null) {
            return false;
        }
        boolean removed = this.addresses.removeIf(addr -> addr.getId().equals(addressId));
        if (removed && addressId.equals(this.defaultAddressId)) {
            // Set new default if we removed the default address
            if (!this.addresses.isEmpty()) {
                AddressVO newDefault = this.addresses.get(0);
                newDefault.setDefault(true);
                this.defaultAddressId = newDefault.getId();
            } else {
                this.defaultAddressId = null;
            }
        }
        return removed;
    }

    /**
     * Sets a specific address as default.
     *
     * @param addressId the ID of the address to set as default
     */
    public void setDefaultAddress(String addressId) {
        if (this.addresses == null) {
            return;
        }
        this.addresses.forEach(addr -> addr.setDefault(addr.getId().equals(addressId)));
        this.defaultAddressId = addressId;
    }

    /**
     * Adds a restaurant to favorites.
     *
     * @param restaurantId the restaurant ID to add
     */
    public void addFavoriteRestaurant(String restaurantId) {
        if (this.favoriteRestaurantIds == null) {
            this.favoriteRestaurantIds = new ArrayList<>();
        }
        if (!this.favoriteRestaurantIds.contains(restaurantId)) {
            this.favoriteRestaurantIds.add(restaurantId);
        }
    }

    /**
     * Removes a restaurant from favorites.
     *
     * @param restaurantId the restaurant ID to remove
     */
    public void removeFavoriteRestaurant(String restaurantId) {
        if (this.favoriteRestaurantIds != null) {
            this.favoriteRestaurantIds.remove(restaurantId);
        }
    }

    /**
     * Records an order for analytics.
     *
     * @param orderAmount the order amount in INR
     */
    public void recordOrder(Double orderAmount) {
        this.totalOrders = (this.totalOrders == null ? 0 : this.totalOrders) + 1;
        this.totalSpent = (this.totalSpent == null ? 0.0 : this.totalSpent) + orderAmount;
        this.averageOrderValue = this.totalSpent / this.totalOrders;
    }

    /**
     * Soft deletes the user.
     *
     * @param deletedBy the user ID who is deleting
     */
    public void delete(String deletedBy) {
        this.deleted = true;
        this.deletedBy = deletedBy;
        this.deletedAt = Instant.now();
        this.status = UserStatus.DELETED;
    }

    /**
     * Gets the user's full name.
     *
     * @return full name
     */
    public String getFullName() {
        if (lastName != null && !lastName.isBlank()) {
            return firstName + " " + lastName;
        }
        return firstName;
    }
}

