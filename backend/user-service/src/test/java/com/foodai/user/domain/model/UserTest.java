package com.foodai.user.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for User domain entity.
 */
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
            .id("user-123")
            .phone("+919876543210")
            .email("test@example.com")
            .firstName("John")
            .lastName("Doe")
            .status(UserStatus.ACTIVE)
            .addresses(new ArrayList<>())
            .favoriteRestaurantIds(new ArrayList<>())
            .favoriteMenuItemIds(new ArrayList<>())
            .totalOrders(0)
            .totalSpent(0.0)
            .averageOrderValue(0.0)
            .build();
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should pass validation with valid data")
        void shouldPassValidation_withValidData() {
            // Act & Assert - no exception thrown
            user.validate();
        }

        @Test
        @DisplayName("Should fail validation when phone is null")
        void shouldFailValidation_whenPhoneIsNull() {
            // Arrange
            user.setPhone(null);

            // Act & Assert
            assertThatThrownBy(() -> user.validate())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Phone number is required");
        }

        @Test
        @DisplayName("Should fail validation when first name is blank")
        void shouldFailValidation_whenFirstNameIsBlank() {
            // Arrange
            user.setFirstName("");

            // Act & Assert
            assertThatThrownBy(() -> user.validate())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("First name is required");
        }
    }

    @Nested
    @DisplayName("Address Management Tests")
    class AddressManagementTests {

        @Test
        @DisplayName("Should add address and set as default if first")
        void shouldAddAddress_andSetAsDefaultIfFirst() {
            // Arrange
            AddressVO address = AddressVO.builder()
                .id("addr-1")
                .label("Home")
                .city("Mumbai")
                .build();

            // Act
            user.addAddress(address);

            // Assert
            assertThat(user.getAddresses()).hasSize(1);
            assertThat(user.getAddresses().get(0).isDefault()).isTrue();
            assertThat(user.getDefaultAddressId()).isEqualTo("addr-1");
        }

        @Test
        @DisplayName("Should not set second address as default")
        void shouldNotSetSecondAddressAsDefault() {
            // Arrange
            AddressVO address1 = AddressVO.builder()
                .id("addr-1")
                .label("Home")
                .build();
            AddressVO address2 = AddressVO.builder()
                .id("addr-2")
                .label("Work")
                .build();

            // Act
            user.addAddress(address1);
            user.addAddress(address2);

            // Assert
            assertThat(user.getAddresses()).hasSize(2);
            assertThat(user.getAddresses().get(0).isDefault()).isTrue();
            assertThat(user.getAddresses().get(1).isDefault()).isFalse();
            assertThat(user.getDefaultAddressId()).isEqualTo("addr-1");
        }

        @Test
        @DisplayName("Should remove address")
        void shouldRemoveAddress() {
            // Arrange
            AddressVO address1 = AddressVO.builder().id("addr-1").build();
            AddressVO address2 = AddressVO.builder().id("addr-2").build();
            user.addAddress(address1);
            user.addAddress(address2);

            // Act
            boolean removed = user.removeAddress("addr-2");

            // Assert
            assertThat(removed).isTrue();
            assertThat(user.getAddresses()).hasSize(1);
        }

        @Test
        @DisplayName("Should update default when removing default address")
        void shouldUpdateDefault_whenRemovingDefaultAddress() {
            // Arrange
            AddressVO address1 = AddressVO.builder().id("addr-1").build();
            AddressVO address2 = AddressVO.builder().id("addr-2").build();
            user.addAddress(address1);
            user.addAddress(address2);

            // Act
            user.removeAddress("addr-1");

            // Assert
            assertThat(user.getAddresses()).hasSize(1);
            assertThat(user.getAddresses().get(0).isDefault()).isTrue();
            assertThat(user.getDefaultAddressId()).isEqualTo("addr-2");
        }

        @Test
        @DisplayName("Should set default address")
        void shouldSetDefaultAddress() {
            // Arrange
            AddressVO address1 = AddressVO.builder().id("addr-1").build();
            AddressVO address2 = AddressVO.builder().id("addr-2").build();
            user.addAddress(address1);
            user.addAddress(address2);

            // Act
            user.setDefaultAddress("addr-2");

            // Assert
            assertThat(user.getAddresses().get(0).isDefault()).isFalse();
            assertThat(user.getAddresses().get(1).isDefault()).isTrue();
            assertThat(user.getDefaultAddressId()).isEqualTo("addr-2");
        }
    }

    @Nested
    @DisplayName("Favorites Tests")
    class FavoritesTests {

        @Test
        @DisplayName("Should add favorite restaurant")
        void shouldAddFavoriteRestaurant() {
            // Act
            user.addFavoriteRestaurant("restaurant-1");

            // Assert
            assertThat(user.getFavoriteRestaurantIds()).contains("restaurant-1");
        }

        @Test
        @DisplayName("Should not add duplicate favorite restaurant")
        void shouldNotAddDuplicateFavoriteRestaurant() {
            // Act
            user.addFavoriteRestaurant("restaurant-1");
            user.addFavoriteRestaurant("restaurant-1");

            // Assert
            assertThat(user.getFavoriteRestaurantIds()).hasSize(1);
        }

        @Test
        @DisplayName("Should remove favorite restaurant")
        void shouldRemoveFavoriteRestaurant() {
            // Arrange
            user.addFavoriteRestaurant("restaurant-1");

            // Act
            user.removeFavoriteRestaurant("restaurant-1");

            // Assert
            assertThat(user.getFavoriteRestaurantIds()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Order Recording Tests")
    class OrderRecordingTests {

        @Test
        @DisplayName("Should record order and update statistics")
        void shouldRecordOrderAndUpdateStatistics() {
            // Act
            user.recordOrder(500.0);
            user.recordOrder(300.0);

            // Assert
            assertThat(user.getTotalOrders()).isEqualTo(2);
            assertThat(user.getTotalSpent()).isEqualTo(800.0);
            assertThat(user.getAverageOrderValue()).isEqualTo(400.0);
        }
    }

    @Nested
    @DisplayName("Delete Tests")
    class DeleteTests {

        @Test
        @DisplayName("Should soft delete user")
        void shouldSoftDeleteUser() {
            // Act
            user.delete("admin");

            // Assert
            assertThat(user.isDeleted()).isTrue();
            assertThat(user.getDeletedBy()).isEqualTo("admin");
            assertThat(user.getDeletedAt()).isNotNull();
            assertThat(user.getStatus()).isEqualTo(UserStatus.DELETED);
        }
    }

    @Nested
    @DisplayName("Full Name Tests")
    class FullNameTests {

        @Test
        @DisplayName("Should return full name with first and last name")
        void shouldReturnFullName_withFirstAndLastName() {
            // Assert
            assertThat(user.getFullName()).isEqualTo("John Doe");
        }

        @Test
        @DisplayName("Should return only first name when last name is null")
        void shouldReturnOnlyFirstName_whenLastNameIsNull() {
            // Arrange
            user.setLastName(null);

            // Assert
            assertThat(user.getFullName()).isEqualTo("John");
        }
    }
}

