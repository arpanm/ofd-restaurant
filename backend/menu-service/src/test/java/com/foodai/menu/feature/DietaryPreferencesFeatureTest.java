package com.foodai.menu.feature;

import com.foodai.menu.domain.model.MenuItem;
import com.foodai.menu.domain.model.NutritionalInfoVO;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Feature tests for Dietary Preferences & Nutritional Information.
 *
 * <p>Tests cover:
 * - Vegetarian/Vegan/Gluten-free filtering
 * - Allergen tracking and warnings
 * - Nutritional information display
 * - Dietary restriction search
 * - Calorie-based filtering (diet planner)
 * - Halal/Jain certifications
 *
 * <p>Based on: DIET_PLANNER.md, CONSUMER.tsx features
 *
 * @author FoodAI Team
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Dietary Preferences Feature Tests")
class DietaryPreferencesFeatureTest {

  @Autowired
  private MenuItemService menuItemService;

  @Autowired
  private MenuItemRepository menuItemRepository;

  @AfterEach
  void cleanup() {
    menuItemRepository.deleteAll();
  }

  @Test
  @DisplayName("Feature: Filter vegetarian menu items for dietary restrictions")
  void shouldFilterVegetarianItems() {
    // Given multiple menu items with different dietary properties
    CreateMenuItemRequest vegItem = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Paneer Tikka")
        .category("Appetizer")
        .basePrice(new BigDecimal("180.00"))
        .vegetarian(true)
        .vegan(false)
        .build();

    CreateMenuItemRequest nonVegItem = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Chicken Tikka")
        .category("Appetizer")
        .basePrice(new BigDecimal("220.00"))
        .vegetarian(false)
        .vegan(false)
        .build();

    menuItemService.create(vegItem);
    menuItemService.create(nonVegItem);

    // When searching for vegetarian items only
    MenuItemSearchRequest searchRequest = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .vegetarian(true)
        .build();

    Page<MenuItemResponse> results = menuItemService.search(searchRequest);

    // Then only vegetarian items should be returned
    assertThat(results.getContent())
        .hasSize(1)
        .extracting(MenuItemResponse::getName)
        .containsExactly("Paneer Tikka");
  }

  @Test
  @DisplayName("Feature: Vegan menu items exclude all animal products")
  void shouldIdentifyVeganItems() {
    // Given various menu items
    CreateMenuItemRequest veganItem = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Vegan Buddha Bowl")
        .category("Main Course")
        .basePrice(new BigDecimal("280.00"))
        .vegetarian(true)
        .vegan(true)
        .containsDairy(false)
        .build();

    CreateMenuItemRequest vegetarianWithDairy = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Paneer Butter Masala")
        .category("Main Course")
        .basePrice(new BigDecimal("300.00"))
        .vegetarian(true)
        .vegan(false)
        .containsDairy(true)
        .build();

    menuItemService.create(veganItem);
    menuItemService.create(vegetarianWithDairy);

    // When customer searches for vegan options
    MenuItemSearchRequest searchRequest = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .vegan(true)
        .build();

    Page<MenuItemResponse> results = menuItemService.search(searchRequest);

    // Then only truly vegan items (no dairy) should be returned
    assertThat(results.getContent())
        .hasSize(1)
        .extracting(MenuItemResponse::getName)
        .containsExactly("Vegan Buddha Bowl");
  }

  @Test
  @DisplayName("Feature: Gluten-free filtering for celiac customers")
  void shouldFilterGlutenFreeItems() {
    // Given items with gluten-free attribute
    CreateMenuItemRequest glutenFree = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Grilled Chicken Salad")
        .category("Salad")
        .basePrice(new BigDecimal("220.00"))
        .glutenFree(true)
        .build();

    CreateMenuItemRequest containsGluten = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Pasta Alfredo")
        .category("Main Course")
        .basePrice(new BigDecimal("300.00"))
        .glutenFree(false)
        .build();

    menuItemService.create(glutenFree);
    menuItemService.create(containsGluten);

    // When filtering for gluten-free
    MenuItemSearchRequest searchRequest = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .glutenFree(true)
        .build();

    Page<MenuItemResponse> results = menuItemService.search(searchRequest);

    // Then only gluten-free items returned
    assertThat(results.getContent())
        .extracting(MenuItemResponse::getName)
        .contains("Grilled Chicken Salad")
        .doesNotContain("Pasta Alfredo");
  }

  @Test
  @DisplayName("Feature: Allergen tracking prevents dangerous orders")
  void shouldTrackAllergensForSafety() {
    // Given menu items with allergen information
    Set<String> nutsAllergen = new HashSet<>();
    nutsAllergen.add("peanuts");
    nutsAllergen.add("cashews");

    CreateMenuItemRequest itemWithNuts = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Kung Pao Chicken")
        .category("Chinese")
        .basePrice(new BigDecimal("320.00"))
        .containsNuts(true)
        .allergens(nutsAllergen)
        .build();

    MenuItemResponse response = menuItemService.create(itemWithNuts);

    // When retrieving the item
    MenuItem savedItem = menuItemRepository.findById(response.getId()).orElseThrow();

    // Then allergen information should be available
    assertThat(savedItem.isContainsNuts()).isTrue();
    assertThat(savedItem.getAllergens()).contains("peanuts", "cashews");
  }

  @Test
  @DisplayName("Feature: Nutritional information for diet planner integration")
  void shouldProvideDetailedNutritionalInfo() {
    // Given a menu item with complete nutritional data
    NutritionalInfoVO nutrition = NutritionalInfoVO.builder()
        .calories(450)
        .protein(new BigDecimal("28.00"))
        .carbohydrates(new BigDecimal("35.00"))
        .fat(new BigDecimal("22.00"))
        .fiber(new BigDecimal("5.00"))
        .sugar(new BigDecimal("8.00"))
        .sodium(Integer.valueOf(890))
        .build();

    CreateMenuItemRequest healthyItem = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Grilled Salmon with Quinoa")
        .category("Healthy Options")
        .basePrice(new BigDecimal("450.00"))
        .nutritionalInfo(nutrition)
        .build();

    MenuItemResponse response = menuItemService.create(healthyItem);

    // When customer uses diet planner
    MenuItem savedItem = menuItemRepository.findById(response.getId()).orElseThrow();

    // Then nutritional data should match diet plan requirements
    assertThat(savedItem.getNutritionalInfo()).isNotNull();
    assertThat(savedItem.getNutritionalInfo().getCalories()).isEqualTo(Integer.valueOf(450));
    assertThat(savedItem.getNutritionalInfo().getProtein()).isEqualByComparingTo(new BigDecimal("28.00"));
  }

  @Test
  @DisplayName("Feature: Calorie filtering for diet-conscious customers")
  void shouldFilterByCalorieRange() {
    // Given menu items with different calorie counts
    CreateMenuItemRequest lowCalItem = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Garden Salad")
        .category("Salad")
        .basePrice(new BigDecimal("150.00"))
        .nutritionalInfo(NutritionalInfoVO.builder().calories(180).build())
        .build();

    CreateMenuItemRequest highCalItem = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Cheese Burger")
        .category("Fast Food")
        .basePrice(new BigDecimal("250.00"))
        .nutritionalInfo(NutritionalInfoVO.builder().calories(850).build())
        .build();

    menuItemService.create(lowCalItem);
    menuItemService.create(highCalItem);

    // When customer sets calorie limit (e.g., 500 cal/meal for diet plan)
    // Note: Current search doesn't support calorie filtering yet
    // This test documents the requirement for future implementation

    MenuItem salad = menuItemRepository.findByRestaurantIdAndDeletedFalse("rest123", null)
        .getContent().stream()
        .filter(item -> item.getNutritionalInfo() != null)
        .filter(item -> item.getNutritionalInfo().getCalories() <= 500)
        .findFirst()
        .orElseThrow();

    assertThat(salad.getName()).isEqualTo("Garden Salad");
    assertThat(salad.getNutritionalInfo().getCalories()).isLessThanOrEqualTo(500);
  }

  @Test
  @DisplayName("Feature: Halal certification for Muslim customers")
  void shouldSupportHalalCertification() {
    // Given menu items with Halal certification
    CreateMenuItemRequest halalItem = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Halal Chicken Biryani")
        .category("Biryani")
        .basePrice(new BigDecimal("350.00"))
        .halal(true)
        .build();

    MenuItemResponse response = menuItemService.create(halalItem);
    MenuItem savedItem = menuItemRepository.findById(response.getId()).orElseThrow();

    // Then halal status should be tracked
    assertThat(savedItem.isHalal()).isTrue();
  }

  @Test
  @DisplayName("Feature: Jain-friendly food identification")
  void shouldSupportJainDietaryRequirements() {
    // Given items marked as Jain-friendly (no root vegetables)
    CreateMenuItemRequest jainItem = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Jain Thali")
        .category("Thali")
        .basePrice(new BigDecimal("280.00"))
        .vegetarian(true)
        .jain(true)
        .build();

    MenuItemResponse response = menuItemService.create(jainItem);
    MenuItem savedItem = menuItemRepository.findById(response.getId()).orElseThrow();

    // Then Jain status should be available for filtering
    assertThat(savedItem.isJain()).isTrue();
    assertThat(savedItem.isVegetarian()).isTrue();
  }

  @Test
  @DisplayName("Feature: Multiple dietary filters combined (Vegan + Gluten-Free + Low Calorie)")
  void shouldCombineMultipleDietaryFilters() {
    // Given a menu item that meets multiple dietary requirements
    CreateMenuItemRequest healthyVeganItem = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Quinoa Buddha Bowl")
        .category("Healthy")
        .basePrice(new BigDecimal("280.00"))
        .vegetarian(true)
        .vegan(true)
        .glutenFree(true)
        .containsDairy(false)
        .containsNuts(false)
        .nutritionalInfo(NutritionalInfoVO.builder()
            .calories(380)
            .protein(new BigDecimal("15.00"))
            .carbohydrates(new BigDecimal("45.00"))
            .fat(new BigDecimal("12.00"))
            .build())
        .build();

    MenuItemResponse response = menuItemService.create(healthyVeganItem);
    MenuItem savedItem = menuItemRepository.findById(response.getId()).orElseThrow();

    // When searching with multiple dietary filters
    MenuItemSearchRequest searchRequest = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .vegan(true)
        .glutenFree(true)
        .build();

    Page<MenuItemResponse> results = menuItemService.search(searchRequest);

    // Then item meets all criteria
    assertThat(savedItem.isVegan()).isTrue();
    assertThat(savedItem.isGlutenFree()).isTrue();
    assertThat(savedItem.getNutritionalInfo().getCalories()).isLessThan(500);
    assertThat(results.getContent()).isNotEmpty();
  }

  @Test
  @DisplayName("Feature: Ingredients list for allergen awareness")
  void shouldTrackIngredientsList() {
    // Given a menu item with complete ingredients list
    List<String> ingredients = List.of(
        "Chicken breast",
        "Yogurt",
        "Spices (turmeric, cumin, coriander)",
        "Onions",
        "Tomatoes",
        "Cream",
        "Butter"
    );

    CreateMenuItemRequest request = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Butter Chicken")
        .category("Main Course")
        .basePrice(new BigDecimal("350.00"))
        .ingredients(ingredients)
        .containsDairy(true)
        .build();

    MenuItemResponse response = menuItemService.create(request);
    MenuItem savedItem = menuItemRepository.findById(response.getId()).orElseThrow();

    // Then ingredients should be available for customer review
    assertThat(savedItem.getIngredients()).hasSize(7);
    assertThat(savedItem.getIngredients()).contains("Chicken breast", "Cream", "Butter");
    assertThat(savedItem.isContainsDairy()).isTrue();
  }
}

