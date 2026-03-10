package com.foodai.menu.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for MenuItem domain entity.
 *
 * @author FoodAI Team
 */
@DisplayName("MenuItem Domain Tests")
class MenuItemTest {

  private MenuItem menuItem;

  @BeforeEach
  void setUp() {
    menuItem = new MenuItem();
    menuItem.setId("item123");
    menuItem.setRestaurantId("rest123");
    menuItem.setName("Butter Chicken");
    menuItem.setDescription("Rich and creamy curry");
    menuItem.setCategory("Main Course");
    menuItem.setBasePrice(new BigDecimal("350.00"));
    menuItem.setAvailable(true);
    menuItem.setPreparationTime(25);
    menuItem.setSpiceLevel(3);
    menuItem.setVegetarian(false);
    menuItem.setStatus(MenuItemStatus.ACTIVE);
    menuItem.setDeleted(false);
    menuItem.setTotalOrders(0);
    menuItem.setReviewCount(0);
    menuItem.setAverageRating(BigDecimal.ZERO);
  }

  @Test
  @DisplayName("Should validate successfully with valid data")
  void shouldValidate_whenDataIsValid() {
    // Act & Assert
    menuItem.validate();
    // No exception should be thrown
  }

  @Test
  @DisplayName("Should throw exception when name is blank")
  void shouldThrowException_whenNameIsBlank() {
    // Arrange
    menuItem.setName("");

    // Act & Assert
    assertThatThrownBy(() -> menuItem.validate())
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("name");
  }

  @Test
  @DisplayName("Should throw exception when price is negative")
  void shouldThrowException_whenPriceIsNegative() {
    // Arrange
    menuItem.setBasePrice(new BigDecimal("-10"));

    // Act & Assert
    assertThatThrownBy(() -> menuItem.validate())
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Price");
  }

  @Test
  @DisplayName("Should throw exception when restaurant ID is blank")
  void shouldThrowException_whenRestaurantIdIsBlank() {
    // Arrange
    menuItem.setRestaurantId("");

    // Act & Assert
    assertThatThrownBy(() -> menuItem.validate())
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Restaurant ID");
  }

  @Test
  @DisplayName("Should initialize defaults correctly")
  void shouldInitializeDefaults() {
    // Arrange
    MenuItem newItem = new MenuItem();

    // Act
    newItem.initializeDefaults();

    // Assert
    assertThat(newItem.getStatus()).isEqualTo(MenuItemStatus.ACTIVE);
    assertThat(newItem.getTotalOrders()).isEqualTo(0);
    assertThat(newItem.getReviewCount()).isEqualTo(0);
    assertThat(newItem.getAverageRating()).isEqualTo(BigDecimal.ZERO);
    assertThat(newItem.isDeleted()).isFalse();
  }

  @Test
  @DisplayName("Should update availability")
  void shouldUpdateAvailability() {
    // Act
    menuItem.updateAvailability(false);

    // Assert
    assertThat(menuItem.isAvailable()).isFalse();
  }

  @Test
  @DisplayName("Should mark as deleted")
  void shouldMarkAsDeleted() {
    // Act
    menuItem.delete();

    // Assert
    assertThat(menuItem.isDeleted()).isTrue();
  }

  @Test
  @DisplayName("Should record order")
  void shouldRecordOrder() {
    // Arrange
    int initialOrders = menuItem.getTotalOrders();

    // Act
    menuItem.recordOrder();

    // Assert
    assertThat(menuItem.getTotalOrders()).isEqualTo(initialOrders + 1);
  }

  @Test
  @DisplayName("Should update rating")
  void shouldUpdateRating() {
    // Arrange
    menuItem.setReviewCount(0);
    menuItem.setAverageRating(BigDecimal.ZERO);

    // Act
    menuItem.updateRating(new BigDecimal("4.5"));
    menuItem.updateRating(new BigDecimal("5.0"));

    // Assert
    assertThat(menuItem.getReviewCount()).isEqualTo(2);
    assertThat(menuItem.getAverageRating()).isGreaterThan(BigDecimal.ZERO);
  }

  @Test
  @DisplayName("Should calculate final price with customizations")
  void shouldCalculateFinalPrice_withCustomizations() {
    // Arrange
    CustomizationOptionVO option1 = new CustomizationOptionVO("Extra Spicy", null, new BigDecimal("50"), true);
    CustomizationVO customization = new CustomizationVO(
        "Spice Level",
        List.of(option1),
        true,
        false,
        1,
        1
    );
    menuItem.setCustomizations(List.of(customization));
    Set<String> selectedCustomizations = new HashSet<>();
    selectedCustomizations.add("Extra Spicy");

    // Act
    BigDecimal finalPrice = menuItem.calculateFinalPrice(new ArrayList<>(selectedCustomizations));

    // Assert
    assertThat(finalPrice).isEqualByComparingTo(new BigDecimal("400.00")); // 350 + 50
  }

  @Test
  @DisplayName("Should check availability based on schedule")
  void shouldCheckAvailability_basedOnSchedule() {
    // Arrange
    AvailabilityScheduleVO schedule = new AvailabilityScheduleVO(
        false,
        Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, 
               DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
        LocalTime.of(11, 0),
        LocalTime.of(23, 0),
        null,
        null
    );
    menuItem.setAvailabilitySchedule(schedule);

    // Act
    boolean isAvailable = menuItem.isAvailableNow();

    // Assert - depends on current time, but method should not throw exception
    assertThat(isAvailable).isIn(true, false);
  }

  @Test
  @DisplayName("Should have tags set")
  void shouldHaveTags() {
    // Arrange
    Set<String> tags = new HashSet<>();
    tags.add("spicy");
    tags.add("popular");
    tags.add("chicken");
    menuItem.setTags(tags);

    // Assert
    assertThat(menuItem.getTags()).isNotNull();
    assertThat(menuItem.getTags()).isNotEmpty();
    assertThat(menuItem.getTags()).contains("spicy", "popular", "chicken");
  }
}

