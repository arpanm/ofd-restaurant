package com.foodai.menu.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodai.menu.domain.model.CustomizationOptionVO;
import com.foodai.menu.domain.model.CustomizationVO;
import com.foodai.menu.domain.model.MenuItem;
import com.foodai.menu.domain.repository.MenuItemRepository;
import com.foodai.menu.dto.request.CreateMenuItemRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Menu Item Customization features.
 * 
 * <p>Tests food customization requirements from FOOD_CUSTOMIZATION_INSTRUCTIONS.md:
 * - Item-level special instructions
 * - Customization options (spice level, add-ons, size)
 * - Price calculations with customizations
 * - Multi-level customization groups
 * - Required vs optional customizations
 *
 * @author FoodAI Team
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Disabled("Requires Docker - enable when Docker environment is available")
@DisplayName("Menu Item Customization Integration Tests")
class MenuItemCustomizationIntegrationTest {

  @Container
  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.3");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MenuItemRepository repository;

  @BeforeEach
  void setUp() {
    repository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
  }

  @Test
  @DisplayName("Should create menu item with spice level customization")
  void shouldCreateItemWithSpiceCustomization() throws Exception {
    // Arrange
    CustomizationOptionVO mild = new CustomizationOptionVO("Mild", null, BigDecimal.ZERO, true);
    CustomizationOptionVO medium = new CustomizationOptionVO("Medium", null, BigDecimal.ZERO, true);
    CustomizationOptionVO extraSpicy = new CustomizationOptionVO("Extra Spicy", "For brave souls", new BigDecimal("20"), true);

    CustomizationVO spiceCustomization = new CustomizationVO(
        "Spice Level",
        List.of(mild, medium, extraSpicy),
        true,  // required
        false, // not multi-select
        1,     // min selections
        1      // max selections
    );

    CreateMenuItemRequest request = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Butter Chicken")
        .description("Rich and creamy curry")
        .category("Main Course")
        .basePrice(new BigDecimal("350"))
        .customizations(List.of(spiceCustomization))
        .available(true)
        .build();

    // Act & Assert
    String response = mockMvc.perform(post("/api/v1/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name", is("Butter Chicken")))
        .andReturn()
        .getResponse()
        .getContentAsString();

    String itemId = objectMapper.readTree(response).get("id").asText();

    // Verify customizations are saved
    MenuItem saved = repository.findById(itemId).orElseThrow();
    assertThat(saved.getCustomizations()).hasSize(1);
    assertThat(saved.getCustomizations().get(0).getName()).isEqualTo("Spice Level");
    assertThat(saved.getCustomizations().get(0).getOptions()).hasSize(3);
  }

  @Test
  @DisplayName("Should create menu item with multiple customization groups")
  void shouldCreateItemWithMultipleCustomizations() throws Exception {
    // Arrange - Size options
    CustomizationOptionVO small = new CustomizationOptionVO("Small", "Regular portion", BigDecimal.ZERO, true);
    CustomizationOptionVO large = new CustomizationOptionVO("Large", "Extra portion", new BigDecimal("50"), true);

    CustomizationVO sizeCustomization = new CustomizationVO(
        "Size",
        List.of(small, large),
        true,  // required
        false, // not multi-select
        1, 1
    );

    // Arrange - Add-ons options
    CustomizationOptionVO extraCheese = new CustomizationOptionVO("Extra Cheese", null, new BigDecimal("30"), true);
    CustomizationOptionVO extraSauce = new CustomizationOptionVO("Extra Sauce", null, new BigDecimal("20"), true);
    CustomizationOptionVO extraVeggies = new CustomizationOptionVO("Extra Vegetables", null, new BigDecimal("25"), true);

    CustomizationVO addOnsCustomization = new CustomizationVO(
        "Add-ons",
        List.of(extraCheese, extraSauce, extraVeggies),
        false, // not required
        true,  // multi-select
        0,     // min selections
        3      // max selections
    );

    CreateMenuItemRequest request = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Margherita Pizza")
        .category("Pizza")
        .basePrice(new BigDecimal("299"))
        .customizations(List.of(sizeCustomization, addOnsCustomization))
        .available(true)
        .build();

    // Act & Assert
    String response = mockMvc.perform(post("/api/v1/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    String itemId = objectMapper.readTree(response).get("id").asText();

    // Verify
    MenuItem saved = repository.findById(itemId).orElseThrow();
    assertThat(saved.getCustomizations()).hasSize(2);
    assertThat(saved.getCustomizations().get(0).getName()).isEqualTo("Size");
    assertThat(saved.getCustomizations().get(1).getName()).isEqualTo("Add-ons");
    assertThat(saved.getCustomizations().get(1).isMultiSelect()).isTrue();
  }

  @Test
  @DisplayName("Should calculate final price with customizations")
  void shouldCalculateFinalPriceWithCustomizations() {
    // Arrange
    CustomizationOptionVO extraSpicy = new CustomizationOptionVO("Extra Spicy", null, new BigDecimal("20"), true);
    CustomizationVO spiceCustomization = new CustomizationVO(
        "Spice Level",
        List.of(extraSpicy),
        true, false, 1, 1
    );

    MenuItem item = new MenuItem();
    item.setBasePrice(new BigDecimal("350"));
    item.setCustomizations(List.of(spiceCustomization));

    // Act
    List<String> selected = new ArrayList<>();
    selected.add("Extra Spicy");
    BigDecimal finalPrice = item.calculateFinalPrice(selected);

    // Assert
    assertThat(finalPrice).isEqualByComparingTo(new BigDecimal("370")); // 350 + 20
  }

  @Test
  @DisplayName("Should calculate final price with multiple customizations")
  void shouldCalculateFinalPriceWithMultipleCustomizations() {
    // Arrange
    CustomizationOptionVO large = new CustomizationOptionVO("Large", null, new BigDecimal("50"), true);
    CustomizationVO sizeCustomization = new CustomizationVO("Size", List.of(large), true, false, 1, 1);

    CustomizationOptionVO extraCheese = new CustomizationOptionVO("Extra Cheese", null, new BigDecimal("30"), true);
    CustomizationOptionVO extraSauce = new CustomizationOptionVO("Extra Sauce", null, new BigDecimal("20"), true);
    CustomizationVO addOnsCustomization = new CustomizationVO("Add-ons", List.of(extraCheese, extraSauce), false, true, 0, 3);

    MenuItem item = new MenuItem();
    item.setBasePrice(new BigDecimal("299"));
    item.setCustomizations(List.of(sizeCustomization, addOnsCustomization));

    // Act - Select Large size + Extra Cheese + Extra Sauce
    List<String> selected = List.of("Large", "Extra Cheese", "Extra Sauce");
    BigDecimal finalPrice = item.calculateFinalPrice(selected);

    // Assert
    assertThat(finalPrice).isEqualByComparingTo(new BigDecimal("399")); // 299 + 50 + 30 + 20
  }
}

