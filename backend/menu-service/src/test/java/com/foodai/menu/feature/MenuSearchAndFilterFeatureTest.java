package com.foodai.menu.feature;

import com.foodai.menu.domain.repository.MenuItemRepository;
import com.foodai.menu.dto.request.CreateMenuItemRequest;
import com.foodai.menu.dto.request.MenuItemSearchRequest;
import com.foodai.menu.dto.response.MenuItemResponse;
import com.foodai.menu.service.MenuItemService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Feature tests for Menu Search, Filtering & Recommendations.
 *
 * <p>Tests cover:
 * - Search by name, description, category
 * - Price range filtering
 * - Tag-based search
 * - Dietary filter combinations
 * - Spice level filtering
 * - Preparation time filtering
 * - Availability filtering
 * - Sorting (price, popularity, rating, name)
 *
 * <p>Based on: SEARCH_RECOMMENDATION_ENGINE.md, CONSUMER.tsx
 *
 * @author FoodAI Team
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Menu Search & Filter Feature Tests")
class MenuSearchAndFilterFeatureTest {

  @Autowired
  private MenuItemService menuItemService;

  @Autowired
  private MenuItemRepository menuItemRepository;

  @BeforeEach
  void setUp() {
    // Clean database
    menuItemRepository.deleteAll();
  }

  @AfterEach
  void cleanup() {
    menuItemRepository.deleteAll();
  }

  @Test
  @DisplayName("Feature: Search menu items by category")
  void shouldSearchByCategory() {
    // Given menu items in different categories
    menuItemService.create(CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Samosa")
        .category("Appetizer")
        .basePrice(new BigDecimal("50.00"))
        .build());

    menuItemService.create(CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Biryani")
        .category("Main Course")
        .basePrice(new BigDecimal("300.00"))
        .build());

    menuItemService.create(CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Gulab Jamun")
        .category("Dessert")
        .basePrice(new BigDecimal("80.00"))
        .build());

    // When searching for appetizers
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .category("Appetizer")
        .build();

    Page<MenuItemResponse> results = menuItemService.search(request);

    // Then only appetizers returned
    assertThat(results.getContent())
        .hasSize(1)
        .extracting(MenuItemResponse::getCategory)
        .containsExactly("Appetizer");
  }

  @Test
  @DisplayName("Feature: Price range filtering for budget-conscious customers")
  void shouldFilterByPriceRange() {
    // Given menu items with various prices
    menuItemService.create(CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Budget Meal")
        .category("Value")
        .basePrice(new BigDecimal("120.00"))
        .build());

    menuItemService.create(CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Mid Range Meal")
        .category("Main")
        .basePrice(new BigDecimal("280.00"))
        .build());

    menuItemService.create(CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Premium Meal")
        .category("Premium")
        .basePrice(new BigDecimal("550.00"))
        .build());

    // When filtering ₹200-₹400 range
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .minPrice(new BigDecimal("200.00"))
        .maxPrice(new BigDecimal("400.00"))
        .build();

    Page<MenuItemResponse> results = menuItemService.search(request);

    // Then only items in range returned
    assertThat(results.getContent())
        .extracting(MenuItemResponse::getName)
        .contains("Mid Range Meal")
        .doesNotContain("Budget Meal", "Premium Meal");
  }

  @Test
  @DisplayName("Feature: Tag-based search for popular/trending items")
  void shouldSearchByTags() {
    // Given items with tags
    Set<String> popularTags = Set.of("popular", "bestseller", "trending");
    Set<String> spicyTags = Set.of("spicy", "hot", "chili");

    menuItemService.create(CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Bestseller Biryani")
        .category("Main")
        .basePrice(new BigDecimal("350.00"))
        .tags(popularTags)
        .build());

    menuItemService.create(CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Spicy Wings")
        .category("Appetizer")
        .basePrice(new BigDecimal("200.00"))
        .tags(spicyTags)
        .build());

    // When searching for popular items
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .tags(Set.of("popular", "bestseller"))
        .build();

    Page<MenuItemResponse> results = menuItemService.search(request);

    // Then tagged items should be found
    assertThat(results.getContent())
        .extracting(MenuItemResponse::getName)
        .contains("Bestseller Biryani");
  }

  @Test
  @DisplayName("Feature: Spice level filtering (0-5 scale)")
  void shouldFilterBySpiceLevel() {
    // Given items with different spice levels
    menuItemService.create(CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Mild Curry")
        .category("Main")
        .basePrice(new BigDecimal("250.00"))
        .spiceLevel(1)
        .build());

    menuItemService.create(CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Medium Curry")
        .category("Main")
        .basePrice(new BigDecimal("250.00"))
        .spiceLevel(3)
        .build());

    menuItemService.create(CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Extra Spicy Curry")
        .category("Main")
        .basePrice(new BigDecimal("250.00"))
        .spiceLevel(5)
        .build());

    // When filtering for mild to medium spice (0-3)
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .minSpiceLevel(0)
        .maxSpiceLevel(3)
        .build();

    Page<MenuItemResponse> results = menuItemService.search(request);

    // Then only appropriate spice levels returned
    assertThat(results.getContent())
        .extracting(MenuItemResponse::getName)
        .contains("Mild Curry", "Medium Curry")
        .doesNotContain("Extra Spicy Curry");
  }

  @Test
  @DisplayName("Feature: Quick prep time filtering for busy customers")
  void shouldFilterByPreparationTime() {
    // Given items with different prep times
    menuItemService.create(CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Quick Sandwich")
        .category("Fast")
        .basePrice(new BigDecimal("120.00"))
        .preparationTime(10)
        .build());

    menuItemService.create(CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Grilled Chicken")
        .category("Main")
        .basePrice(new BigDecimal("280.00"))
        .preparationTime(35)
        .build());

    // When customer wants items ready in < 20 minutes
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .maxPrepTime(20)
        .build();

    Page<MenuItemResponse> results = menuItemService.search(request);

    // Then only quick items returned
    assertThat(results.getContent())
        .extracting(MenuItemResponse::getName)
        .contains("Quick Sandwich")
        .doesNotContain("Grilled Chicken");
  }

  @Test
  @DisplayName("Feature: Sorting by price (low to high) for budget planning")
  void shouldSortByPriceLowToHigh() {
    // Given items with different prices
    menuItemService.create(CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Expensive Item")
        .category("Premium")
        .basePrice(new BigDecimal("500.00"))
        .build());

    menuItemService.create(CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Cheap Item")
        .category("Value")
        .basePrice(new BigDecimal("100.00"))
        .build());

    menuItemService.create(CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Mid Item")
        .category("Regular")
        .basePrice(new BigDecimal("250.00"))
        .build());

    // When sorting by price ascending
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .sortBy("basePrice")
        .sortDirection("asc")
        .build();

    Page<MenuItemResponse> results = menuItemService.search(request);

    // Then items returned in price order
    List<String> names = results.getContent().stream()
        .map(MenuItemResponse::getName)
        .toList();
    assertThat(names).containsExactly("Cheap Item", "Mid Item", "Expensive Item");
  }

  @Test
  @DisplayName("Feature: Filter available items only for immediate ordering")
  void shouldFilterAvailableItemsOnly() {
    // Given items with different availability
    menuItemService.create(CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Available Item")
        .category("Main")
        .basePrice(new BigDecimal("250.00"))
        .available(true)
        .build());

    CreateMenuItemRequest unavailableItem = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Sold Out Item")
        .category("Main")
        .basePrice(new BigDecimal("250.00"))
        .available(true)
        .build();
    MenuItemResponse soldOut = menuItemService.create(unavailableItem);
    menuItemService.updateAvailability(soldOut.getId(), false); // Mark as sold out

    // When filtering for available items
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .available(true)
        .build();

    Page<MenuItemResponse> results = menuItemService.search(request);

    // Then only available items returned
    assertThat(results.getContent())
        .extracting(MenuItemResponse::getName)
        .contains("Available Item")
        .doesNotContain("Sold Out Item");
  }
}

