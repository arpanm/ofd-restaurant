package com.foodai.menu.feature;

import com.foodai.menu.domain.model.CustomizationOptionVO;
import com.foodai.menu.domain.model.CustomizationVO;
import com.foodai.menu.domain.model.MenuItem;
import com.foodai.menu.domain.repository.MenuItemRepository;
import com.foodai.menu.dto.request.CreateMenuItemRequest;
import com.foodai.menu.dto.response.MenuItemResponse;
import com.foodai.menu.service.MenuItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Feature tests for Menu Customization & Special Instructions.
 *
 * <p>Tests cover:
 * - Item-level customizations (spice level, add-ons, portion size)
 * - Customization groups (required vs optional)
 * - Multi-select customizations
 * - Price calculation with customizations
 * - Validation of customization rules
 *
 * <p>Based on: FOOD_CUSTOMIZATION_INSTRUCTIONS.md
 *
 * @author FoodAI Team
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Menu Customization Feature Tests")
class MenuCustomizationFeatureTest {

  @Autowired
  private MenuItemService menuItemService;

  @Autowired
  private MenuItemRepository menuItemRepository;

  @AfterEach
  void cleanup() {
    menuItemRepository.deleteAll();
  }

  @Test
  @DisplayName("Feature: Customer can customize spice level for menu items")
  void shouldAllowSpiceLevelCustomization() {
    // Given a menu item with spice level customization options
    CustomizationOptionVO mild = CustomizationOptionVO.builder()
        .name("Mild")
        .description("Light spice level")
        .additionalCost(BigDecimal.ZERO)
        .available(true)
        .build();

    CustomizationOptionVO medium = CustomizationOptionVO.builder()
        .name("Medium")
        .description("Moderate spice level")
        .additionalCost(BigDecimal.ZERO)
        .available(true)
        .build();

    CustomizationOptionVO extraSpicy = CustomizationOptionVO.builder()
        .name("Extra Spicy")
        .description("Very spicy, for spice lovers")
        .additionalCost(new BigDecimal("20.00"))
        .available(true)
        .build();

    CustomizationVO spiceLevelCustomization = CustomizationVO.builder()
        .name("Spice Level")
        .options(List.of(mild, medium, extraSpicy))
        .required(true)
        .multiSelect(false)
        .minSelections(1)
        .maxSelections(1)
        .build();

    CreateMenuItemRequest request = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Chicken Curry")
        .category("Main Course")
        .basePrice(new BigDecimal("280.00"))
        .customizations(List.of(spiceLevelCustomization))
        .build();

    // When creating the menu item
    MenuItemResponse response = menuItemService.create(request);

    // Then the customizations should be saved
    assertThat(response).isNotNull();
    MenuItem savedItem = menuItemRepository.findById(response.getId()).orElseThrow();
    assertThat(savedItem.getCustomizations()).hasSize(1);
    assertThat(savedItem.getCustomizations().get(0).getName()).isEqualTo("Spice Level");
    assertThat(savedItem.getCustomizations().get(0).getOptions()).hasSize(3);
    assertThat(savedItem.getCustomizations().get(0).isRequired()).isTrue();
  }

  @Test
  @DisplayName("Feature: Price calculation with multiple add-on customizations")
  void shouldCalculatePriceWithMultipleAddOns() {
    // Given a menu item with add-on customizations
    CustomizationOptionVO extraCheese = CustomizationOptionVO.builder()
        .name("Extra Cheese")
        .additionalCost(new BigDecimal("30.00"))
        .available(true)
        .build();

    CustomizationOptionVO jalapeños = CustomizationOptionVO.builder()
        .name("Jalapeños")
        .additionalCost(new BigDecimal("20.00"))
        .available(true)
        .build();

    CustomizationOptionVO bacon = CustomizationOptionVO.builder()
        .name("Bacon")
        .additionalCost(new BigDecimal("50.00"))
        .available(true)
        .build();

    CustomizationVO addOns = CustomizationVO.builder()
        .name("Add-Ons")
        .options(List.of(extraCheese, jalapeños, bacon))
        .required(false)
        .multiSelect(true)
        .minSelections(0)
        .maxSelections(3)
        .build();

    MenuItem item = new MenuItem();
    item.setRestaurantId("rest123");
    item.setName("Pizza");
    item.setBasePrice(new BigDecimal("350.00"));
    item.setCustomizations(List.of(addOns));

    // When customer selects multiple add-ons
    List<String> selectedCustomizations = new ArrayList<>();
    selectedCustomizations.add("Extra Cheese");  // +30
    selectedCustomizations.add("Jalapeños");     // +20

    // Then the final price should include all customizations
    BigDecimal finalPrice = item.calculateFinalPrice(selectedCustomizations);
    assertThat(finalPrice).isEqualByComparingTo(new BigDecimal("400.00")); // 350 + 30 + 20
  }

  @Test
  @DisplayName("Feature: Required customizations must have at least one option")
  void shouldEnforceRequiredCustomizationValidation() {
    // Given a required customization group with no options
    CustomizationVO invalidCustomization = CustomizationVO.builder()
        .name("Size")
        .options(List.of()) // Empty options
        .required(true)
        .build();

    // When validating the customization
    // Then it should throw exception
    assertThat(invalidCustomization.getOptions()).isEmpty();
    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalStateException.class,
        invalidCustomization::validate,
        "Customization must have at least one option"
    );
  }

  @Test
  @DisplayName("Feature: Multi-level customization groups (Portion Size + Add-Ons)")
  void shouldSupportMultiLevelCustomizations() {
    // Given a menu item with multiple customization groups
    CustomizationVO sizeCustomization = CustomizationVO.builder()
        .name("Portion Size")
        .options(List.of(
            CustomizationOptionVO.builder().name("Regular").additionalCost(BigDecimal.ZERO).available(true).build(),
            CustomizationOptionVO.builder().name("Large").additionalCost(new BigDecimal("50.00")).available(true).build()
        ))
        .required(true)
        .multiSelect(false)
        .build();

    CustomizationVO toppingsCustomization = CustomizationVO.builder()
        .name("Toppings")
        .options(List.of(
            CustomizationOptionVO.builder().name("Olives").additionalCost(new BigDecimal("20.00")).available(true).build(),
            CustomizationOptionVO.builder().name("Mushrooms").additionalCost(new BigDecimal("30.00")).available(true).build()
        ))
        .required(false)
        .multiSelect(true)
        .build();

    MenuItem item = new MenuItem();
    item.setBasePrice(new BigDecimal("300.00"));
    item.setCustomizations(List.of(sizeCustomization, toppingsCustomization));

    // When customer selects from both groups
    List<String> selections = List.of("Large", "Olives", "Mushrooms");

    // Then price should reflect all selections
    BigDecimal finalPrice = item.calculateFinalPrice(selections);
    assertThat(finalPrice).isEqualByComparingTo(new BigDecimal("400.00")); // 300 + 50 + 20 + 30
  }

  @Test
  @DisplayName("Feature: Item-level special instructions support")
  void shouldSupportSpecialInstructions() {
    // Given a menu item (instructions are at order level, not stored in MenuItem)
    // When creating a menu item
    CreateMenuItemRequest request = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Biryani")
        .category("Main Course")
        .basePrice(new BigDecimal("350.00"))
        .description("Hyderabadi Biryani with aromatic spices")
        .build();

    MenuItemResponse response = menuItemService.create(request);

    // Then the item should support customization via UI
    // Note: Special instructions like "Extra spicy, no onions" are handled
    // at cart/order level in the UI, not stored in menu item definition
    assertThat(response).isNotNull();
    assertThat(response.getName()).isEqualTo("Biryani");
  }

  @Test
  @DisplayName("Feature: Common customization templates (No Onions, Extra Cheese, Well Done)")
  void shouldSupportCommonCustomizationPatterns() {
    // Given common customization patterns
    CustomizationVO commonRequests = CustomizationVO.builder()
        .name("Special Requests")
        .options(List.of(
            CustomizationOptionVO.builder().name("No Onions").additionalCost(BigDecimal.ZERO).available(true).build(),
            CustomizationOptionVO.builder().name("No Garlic").additionalCost(BigDecimal.ZERO).available(true).build(),
            CustomizationOptionVO.builder().name("Extra Sauce").additionalCost(new BigDecimal("15.00")).available(true).build(),
            CustomizationOptionVO.builder().name("Less Salt").additionalCost(BigDecimal.ZERO).available(true).build(),
            CustomizationOptionVO.builder().name("Well Done").additionalCost(BigDecimal.ZERO).available(true).build()
        ))
        .required(false)
        .multiSelect(true)
        .minSelections(0)
        .maxSelections(5)
        .build();

    MenuItem item = new MenuItem();
    item.setBasePrice(new BigDecimal("250.00"));
    item.setCustomizations(List.of(commonRequests));

    // When customer selects multiple common requests
    List<String> selections = List.of("No Onions", "Extra Sauce", "Less Salt");

    // Then price should only include paid options
    BigDecimal finalPrice = item.calculateFinalPrice(selections);
    assertThat(finalPrice).isEqualByComparingTo(new BigDecimal("265.00")); // 250 + 15 (only Extra Sauce costs)
  }

  @Test
  @DisplayName("Feature: Dietary preference-based customizations")
  void shouldSupportDietaryPreferenceCustomizations() {
    // Given a menu item with dietary customization options
    CustomizationVO dietaryOptions = CustomizationVO.builder()
        .name("Make it")
        .options(List.of(
            CustomizationOptionVO.builder().name("Vegan").description("Use plant-based alternatives").additionalCost(new BigDecimal("30.00")).available(true).build(),
            CustomizationOptionVO.builder().name("Gluten-Free").description("Use gluten-free base").additionalCost(new BigDecimal("40.00")).available(true).build(),
            CustomizationOptionVO.builder().name("Keto-Friendly").description("Low carb option").additionalCost(new BigDecimal("50.00")).available(true).build()
        ))
        .required(false)
        .multiSelect(false)
        .build();

    CreateMenuItemRequest request = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Pasta")
        .category("Main Course")
        .basePrice(new BigDecimal("320.00"))
        .customizations(List.of(dietaryOptions))
        .vegetarian(false)
        .build();

    // When creating the item
    MenuItemResponse response = menuItemService.create(request);

    // Then dietary customizations should be available
    assertThat(response).isNotNull();
    MenuItem savedItem = menuItemRepository.findById(response.getId()).orElseThrow();
    assertThat(savedItem.getCustomizations().get(0).getOptions()).hasSize(3);
    
    // And customer can make it vegan
    List<String> veganSelection = List.of("Vegan");
    BigDecimal veganPrice = savedItem.calculateFinalPrice(veganSelection);
    assertThat(veganPrice).isEqualByComparingTo(new BigDecimal("350.00")); // 320 + 30
  }

  @Test
  @DisplayName("Feature: Portion size customization with pricing tiers")
  void shouldSupportPortionSizeCustomization() {
    // Given a menu item with portion size options
    CustomizationVO portionSize = CustomizationVO.builder()
        .name("Portion Size")
        .options(List.of(
            CustomizationOptionVO.builder().name("Regular").additionalCost(BigDecimal.ZERO).available(true).build(),
            CustomizationOptionVO.builder().name("Large").additionalCost(new BigDecimal("80.00")).available(true).build(),
            CustomizationOptionVO.builder().name("Extra Large").additionalCost(new BigDecimal("150.00")).available(true).build(),
            CustomizationOptionVO.builder().name("Family Pack").additionalCost(new BigDecimal("300.00")).available(true).build()
        ))
        .required(true)
        .multiSelect(false)
        .build();

    MenuItem item = new MenuItem();
    item.setBasePrice(new BigDecimal("250.00"));
    item.setCustomizations(List.of(portionSize));

    // When customer selects family pack
    List<String> selection = List.of("Family Pack");
    BigDecimal finalPrice = item.calculateFinalPrice(selection);

    // Then price should reflect the large portion
    assertThat(finalPrice).isEqualByComparingTo(new BigDecimal("550.00")); // 250 + 300
  }

  @Test
  @DisplayName("Feature: Validation prevents invalid customization configurations")
  void shouldValidateCustomizationConstraints() {
    // Given invalid customization configurations

    // Test 1: Multi-select with invalid min/max selections
    CustomizationVO invalidMultiSelect = CustomizationVO.builder()
        .name("Toppings")
        .options(List.of(
            CustomizationOptionVO.builder().name("Topping 1").additionalCost(BigDecimal.ZERO).available(true).build()
        ))
        .multiSelect(true)
        .minSelections(3)  // Invalid: min > max
        .maxSelections(2)  // Invalid: max < min
        .build();

    // When validating
    // Then should throw exception
    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalStateException.class,
        invalidMultiSelect::validate,
        "Minimum selections cannot exceed maximum selections"
    );

    // Test 2: Negative minimum selections
    CustomizationVO negativeMin = CustomizationVO.builder()
        .name("Options")
        .options(List.of(
            CustomizationOptionVO.builder().name("Option 1").additionalCost(BigDecimal.ZERO).available(true).build()
        ))
        .multiSelect(true)
        .minSelections(-1)  // Invalid: negative
        .build();

    org.junit.jupiter.api.Assertions.assertThrows(
        IllegalStateException.class,
        negativeMin::validate,
        "Minimum selections cannot be negative"
    );
  }

  @Test
  @DisplayName("Feature: Real-world customization scenario - Burger with full options")
  void shouldSupportComplexRealWorldCustomization() {
    // Given a burger with multiple customization groups
    CustomizationVO pattyType = CustomizationVO.builder()
        .name("Patty Type")
        .options(List.of(
            CustomizationOptionVO.builder().name("Beef").additionalCost(BigDecimal.ZERO).available(true).build(),
            CustomizationOptionVO.builder().name("Chicken").additionalCost(new BigDecimal("20.00")).available(true).build(),
            CustomizationOptionVO.builder().name("Veggie").additionalCost(new BigDecimal("10.00")).available(true).build()
        ))
        .required(true)
        .multiSelect(false)
        .build();

    CustomizationVO cheeseType = CustomizationVO.builder()
        .name("Cheese")
        .options(List.of(
            CustomizationOptionVO.builder().name("No Cheese").additionalCost(BigDecimal.ZERO).available(true).build(),
            CustomizationOptionVO.builder().name("Regular Cheese").additionalCost(new BigDecimal("25.00")).available(true).build(),
            CustomizationOptionVO.builder().name("Premium Cheese").additionalCost(new BigDecimal("50.00")).available(true).build()
        ))
        .required(true)
        .multiSelect(false)
        .build();

    CustomizationVO toppings = CustomizationVO.builder()
        .name("Toppings")
        .options(List.of(
            CustomizationOptionVO.builder().name("Lettuce").additionalCost(BigDecimal.ZERO).available(true).build(),
            CustomizationOptionVO.builder().name("Tomato").additionalCost(BigDecimal.ZERO).available(true).build(),
            CustomizationOptionVO.builder().name("Onion").additionalCost(BigDecimal.ZERO).available(true).build(),
            CustomizationOptionVO.builder().name("Pickles").additionalCost(BigDecimal.ZERO).available(true).build(),
            CustomizationOptionVO.builder().name("Avocado").additionalCost(new BigDecimal("40.00")).available(true).build()
        ))
        .required(false)
        .multiSelect(true)
        .minSelections(0)
        .maxSelections(5)
        .build();

    MenuItem burger = new MenuItem();
    burger.setBasePrice(new BigDecimal("180.00"));
    burger.setCustomizations(List.of(pattyType, cheeseType, toppings));

    // When customer customizes: Chicken, Premium Cheese, Lettuce, Tomato, Avocado
    List<String> selections = List.of("Chicken", "Premium Cheese", "Lettuce", "Tomato", "Avocado");
    BigDecimal finalPrice = burger.calculateFinalPrice(selections);

    // Then total should be: 180 (base) + 20 (chicken) + 50 (premium cheese) + 0 + 0 + 40 (avocado)
    assertThat(finalPrice).isEqualByComparingTo(new BigDecimal("290.00"));
  }

  @Test
  @DisplayName("Feature: Customization options can be marked unavailable")
  void shouldHandleUnavailableCustomizationOptions() {
    // Given a customization with some options unavailable
    CustomizationVO sauces = CustomizationVO.builder()
        .name("Sauce")
        .options(List.of(
            CustomizationOptionVO.builder().name("Ketchup").additionalCost(BigDecimal.ZERO).available(true).build(),
            CustomizationOptionVO.builder().name("Mayo").additionalCost(BigDecimal.ZERO).available(true).build(),
            CustomizationOptionVO.builder().name("Special Sauce").additionalCost(new BigDecimal("30.00")).available(false).build() // Out of stock
        ))
        .required(true)
        .multiSelect(false)
        .build();

    CreateMenuItemRequest request = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("French Fries")
        .category("Sides")
        .basePrice(new BigDecimal("100.00"))
        .customizations(List.of(sauces))
        .build();

    // When creating menu item with unavailable option
    MenuItemResponse response = menuItemService.create(request);

    // Then unavailable option should still be saved (for re-enabling later)
    MenuItem savedItem = menuItemRepository.findById(response.getId()).orElseThrow();
    assertThat(savedItem.getCustomizations().get(0).getOptions())
        .filteredOn(opt -> !opt.isAvailable())
        .hasSize(1)
        .extracting(CustomizationOptionVO::getName)
        .contains("Special Sauce");
  }
}

