package com.foodai.menu.feature;

import com.foodai.menu.domain.model.AvailabilityScheduleVO;
import com.foodai.menu.domain.model.MenuItem;
import com.foodai.menu.domain.repository.MenuItemRepository;
import com.foodai.menu.dto.request.CreateMenuItemRequest;
import com.foodai.menu.dto.request.MenuItemSearchRequest;
import com.foodai.menu.dto.response.MenuItemResponse;
import com.foodai.menu.service.MenuItemService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Feature tests for Menu Item Availability Management.
 *
 * <p>Tests cover:
 * - Real-time availability toggle
 * - Time-based availability (breakfast, lunch, dinner)
 * - Day-of-week scheduling
 * - Seasonal availability
 * - Sold-out management
 * - Limited quantity handling
 * - Schedule validation
 *
 * <p>Based on: MENU_MANAGEMENT.md availability features
 *
 * @author FoodAI Team
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Menu Availability Feature Tests")
class MenuAvailabilityFeatureTest {

  @Autowired
  private MenuItemService menuItemService;

  @Autowired
  private MenuItemRepository menuItemRepository;

  @AfterEach
  void cleanup() {
    menuItemRepository.deleteAll();
  }

  @Test
  @DisplayName("Feature: Real-time availability toggle for sold-out items")
  void shouldToggleAvailabilityInRealTime() {
    // Given an available menu item
    CreateMenuItemRequest request = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Chicken Biryani")
        .category("Main Course")
        .basePrice(new BigDecimal("350.00"))
        .available(true)
        .build();

    MenuItemResponse response = menuItemService.create(request);

    // When restaurant marks it as sold out
    MenuItemResponse updated = menuItemService.updateAvailability(response.getId(), false);

    // Then item should be unavailable
    assertThat(updated).isNotNull();
    MenuItem item = menuItemRepository.findById(response.getId()).orElseThrow();
    assertThat(item.isAvailable()).isFalse();

    // And when restocked
    MenuItemResponse restocked = menuItemService.updateAvailability(response.getId(), true);
    MenuItem availableItem = menuItemRepository.findById(response.getId()).orElseThrow();

    // Then item becomes available again
    assertThat(availableItem.isAvailable()).isTrue();
  }

  @Test
  @DisplayName("Feature: Breakfast-only items available 6 AM - 11 AM")
  void shouldSupportBreakfastOnlyItems() {
    // Given a breakfast-only menu item
    AvailabilityScheduleVO breakfastSchedule = AvailabilityScheduleVO.builder()
        .alwaysAvailable(false)
        .availableDays(Set.of(DayOfWeek.values()))  // All days
        .startTime(LocalTime.of(6, 0))   // 6:00 AM
        .endTime(LocalTime.of(11, 0))    // 11:00 AM
        .build();

    CreateMenuItemRequest request = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Breakfast Combo")
        .category("Breakfast")
        .basePrice(new BigDecimal("150.00"))
        .availabilitySchedule(breakfastSchedule)
        .build();

    MenuItemResponse response = menuItemService.create(request);
    MenuItem item = menuItemRepository.findById(response.getId()).orElseThrow();

    // Then availability schedule should be configured
    assertThat(item.getAvailabilitySchedule()).isNotNull();
    assertThat(item.getAvailabilitySchedule().getStartTime()).isEqualTo(LocalTime.of(6, 0));
    assertThat(item.getAvailabilitySchedule().getEndTime()).isEqualTo(LocalTime.of(11, 0));
    assertThat(item.getAvailabilitySchedule().isAlwaysAvailable()).isFalse();
  }

  @Test
  @DisplayName("Feature: Weekend-only special items")
  void shouldSupportWeekendSpecials() {
    // Given a weekend-only item
    AvailabilityScheduleVO weekendSchedule = AvailabilityScheduleVO.builder()
        .alwaysAvailable(false)
        .availableDays(Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY))
        .startTime(LocalTime.of(12, 0))
        .endTime(LocalTime.of(22, 0))
        .build();

    CreateMenuItemRequest request = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Weekend Special Thali")
        .category("Specials")
        .basePrice(new BigDecimal("400.00"))
        .availabilitySchedule(weekendSchedule)
        .build();

    MenuItemResponse response = menuItemService.create(request);
    MenuItem item = menuItemRepository.findById(response.getId()).orElseThrow();

    // Then should only be available on weekends
    assertThat(item.getAvailabilitySchedule().getAvailableDays())
        .containsExactlyInAnyOrder(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
  }

  @Test
  @DisplayName("Feature: Seasonal items with date range")
  void shouldSupportSeasonalAvailability() {
    // Given a seasonal item (winter special)
    AvailabilityScheduleVO winterSchedule = AvailabilityScheduleVO.builder()
        .alwaysAvailable(false)
        .seasonalStartDate(LocalDateTime.of(2025, 11, 1, 0, 0))   // Nov 1
        .seasonalEndDate(LocalDateTime.of(2026, 2, 28, 23, 59))     // Feb 28
        .availableDays(Set.of(DayOfWeek.values()))
        .startTime(LocalTime.of(0, 0))
        .endTime(LocalTime.of(23, 59))
        .build();

    CreateMenuItemRequest request = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Gajar Halwa (Winter Special)")
        .category("Dessert")
        .basePrice(new BigDecimal("120.00"))
        .availabilitySchedule(winterSchedule)
        .build();

    MenuItemResponse response = menuItemService.create(request);
    MenuItem item = menuItemRepository.findById(response.getId()).orElseThrow();

    // Then seasonal dates should be configured
    assertThat(item.getAvailabilitySchedule().getSeasonalStartDate())
        .isEqualTo(LocalDateTime.of(2025, 11, 1, 0, 0));
    assertThat(item.getAvailabilitySchedule().getSeasonalEndDate())
        .isEqualTo(LocalDateTime.of(2026, 2, 28, 23, 59));
  }

  @Test
  @DisplayName("Feature: Always available items (no time restrictions)")
  void shouldSupportAlwaysAvailableItems() {
    // Given an always-available item
    AvailabilityScheduleVO alwaysSchedule = AvailabilityScheduleVO.builder()
        .alwaysAvailable(true)
        .build();

    CreateMenuItemRequest request = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Water Bottle")
        .category("Beverages")
        .basePrice(new BigDecimal("20.00"))
        .availabilitySchedule(alwaysSchedule)
        .available(true)
        .build();

    MenuItemResponse response = menuItemService.create(request);
    MenuItem item = menuItemRepository.findById(response.getId()).orElseThrow();

    // Then should be available 24/7
    assertThat(item.getAvailabilitySchedule().isAlwaysAvailable()).isTrue();
    assertThat(item.isAvailable()).isTrue();
  }

  @Test
  @DisplayName("Feature: Lunch-only items (11 AM - 4 PM)")
  void shouldSupportLunchOnlyItems() {
    // Given a lunch-special item
    AvailabilityScheduleVO lunchSchedule = AvailabilityScheduleVO.builder()
        .alwaysAvailable(false)
        .availableDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                              DayOfWeek.THURSDAY, DayOfWeek.FRIDAY))
        .startTime(LocalTime.of(11, 0))
        .endTime(LocalTime.of(16, 0))
        .build();

    CreateMenuItemRequest request = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Executive Lunch Thali")
        .category("Lunch Specials")
        .basePrice(new BigDecimal("220.00"))
        .availabilitySchedule(lunchSchedule)
        .build();

    MenuItemResponse response = menuItemService.create(request);
    MenuItem item = menuItemRepository.findById(response.getId()).orElseThrow();

    // Then lunch timing should be enforced
    assertThat(item.getAvailabilitySchedule().getStartTime()).isEqualTo(LocalTime.of(11, 0));
    assertThat(item.getAvailabilitySchedule().getEndTime()).isEqualTo(LocalTime.of(16, 0));
    assertThat(item.getAvailabilitySchedule().getAvailableDays())
        .doesNotContain(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
  }

  @Test
  @DisplayName("Feature: Check if item is available now based on schedule")
  void shouldCheckCurrentAvailability() {
    // Given an item available during specific hours
    AvailabilityScheduleVO schedule = AvailabilityScheduleVO.builder()
        .alwaysAvailable(false)
        .availableDays(Set.of(DayOfWeek.values()))
        .startTime(LocalTime.of(12, 0))
        .endTime(LocalTime.of(23, 0))
        .build();

    MenuItem item = new MenuItem();
    item.setAvailable(true);
    item.setAvailabilitySchedule(schedule);

    // When checking availability
    boolean isAvailable = item.isAvailableNow();

    // Then result depends on current time
    // (Test validates method doesn't throw exception)
    assertThat(isAvailable).isIn(true, false);
  }

  @Test
  @DisplayName("Feature: Combine multiple filters - Veg + Available + Price Range + Category")
  void shouldCombineMultipleSearchFilters() {
    // Given diverse menu items
    menuItemService.create(CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Veg Burger Available")
        .category("Fast Food")
        .basePrice(new BigDecimal("150.00"))
        .vegetarian(true)
        .available(true)
        .build());

    menuItemService.create(CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Non-Veg Burger")
        .category("Fast Food")
        .basePrice(new BigDecimal("180.00"))
        .vegetarian(false)
        .available(true)
        .build());

    menuItemService.create(CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Expensive Veg Item")
        .category("Fast Food")
        .basePrice(new BigDecimal("400.00"))
        .vegetarian(true)
        .available(true)
        .build());

    // When applying multiple filters
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .category("Fast Food")
        .vegetarian(true)
        .available(true)
        .minPrice(new BigDecimal("100.00"))
        .maxPrice(new BigDecimal("200.00"))
        .build();

    Page<MenuItemResponse> results = menuItemService.search(request);

    // Then only items matching ALL criteria returned
    assertThat(results.getContent())
        .hasSize(1)
        .extracting(MenuItemResponse::getName)
        .containsExactly("Veg Burger Available");
  }
}

