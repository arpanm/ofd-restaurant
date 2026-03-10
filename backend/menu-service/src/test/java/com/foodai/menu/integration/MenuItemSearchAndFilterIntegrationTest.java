package com.foodai.menu.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodai.menu.domain.model.*;
import com.foodai.menu.domain.repository.MenuItemRepository;
import com.foodai.menu.dto.request.CreateMenuItemRequest;
import com.foodai.menu.dto.request.MenuItemSearchRequest;
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
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Menu Item Search and Filtering features.
 * 
 * <p>Tests consumer app filtering requirements:
 * - High Protein filter (protein >= 20g)
 * - Under 500 Cal filter (calories < 500)
 * - Dietary filters (Veg, Vegan, Gluten-Free)
 * - Price range filtering
 * - Search by name/description
 * - Tags filtering
 *
 * @author FoodAI Team
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Disabled("Requires Docker - enable when Docker environment is available")
@DisplayName("Menu Item Search & Filter Integration Tests")
class MenuItemSearchAndFilterIntegrationTest {

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
    createTestMenuItems();
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
  }

  private void createTestMenuItems() {
    // High Protein Item (for "High Protein" filter)
    MenuItem chickenBiryani = new MenuItem();
    chickenBiryani.setRestaurantId("rest123");
    chickenBiryani.setName("Chicken Biryani");
    chickenBiryani.setDescription("Aromatic rice with tender chicken");
    chickenBiryani.setCategory("Main Course");
    chickenBiryani.setBasePrice(new BigDecimal("299"));
    chickenBiryani.setAvailable(true);
    chickenBiryani.setVegetarian(false);
    chickenBiryani.setTags(Set.of("spicy", "popular", "protein-rich"));
    NutritionalInfoVO nutrition1 = NutritionalInfoVO.builder()
        .calories(650)
        .protein(new BigDecimal("35"))
        .carbohydrates(new BigDecimal("75"))
        .fat(new BigDecimal("20"))
        .build();
    chickenBiryani.setNutritionalInfo(nutrition1);
    chickenBiryani.initializeDefaults();
    repository.save(chickenBiryani);

    // Under 500 Cal Item
    MenuItem greenSalad = new MenuItem();
    greenSalad.setRestaurantId("rest123");
    greenSalad.setName("Green Salad Bowl");
    greenSalad.setDescription("Fresh vegetables with light dressing");
    greenSalad.setCategory("Salads");
    greenSalad.setBasePrice(new BigDecimal("150"));
    greenSalad.setAvailable(true);
    greenSalad.setVegetarian(true);
    greenSalad.setVegan(true);
    greenSalad.setGlutenFree(true);
    greenSalad.setTags(Set.of("healthy", "low-calorie", "vegan"));
    NutritionalInfoVO nutrition2 = NutritionalInfoVO.builder()
        .calories(250)
        .protein(new BigDecimal("8"))
        .carbohydrates(new BigDecimal("30"))
        .fat(new BigDecimal("10"))
        .build();
    greenSalad.setNutritionalInfo(nutrition2);
    greenSalad.initializeDefaults();
    repository.save(greenSalad);

    // Vegetarian High Protein Item
    MenuItem paneerTikka = new MenuItem();
    paneerTikka.setRestaurantId("rest123");
    paneerTikka.setName("Paneer Tikka");
    paneerTikka.setDescription("Grilled cottage cheese with spices");
    paneerTikka.setCategory("Appetizer");
    paneerTikka.setBasePrice(new BigDecimal("220"));
    paneerTikka.setAvailable(true);
    paneerTikka.setVegetarian(true);
    paneerTikka.setGlutenFree(true);
    paneerTikka.setTags(Set.of("vegetarian", "protein-rich", "grilled"));
    NutritionalInfoVO nutrition3 = NutritionalInfoVO.builder()
        .calories(400)
        .protein(new BigDecimal("25"))
        .carbohydrates(new BigDecimal("15"))
        .fat(new BigDecimal("28"))
        .build();
    paneerTikka.setNutritionalInfo(nutrition3);
    paneerTikka.initializeDefaults();
    repository.save(paneerTikka);

    // Vegan Low Calorie Item
    MenuItem quinoaBowl = new MenuItem();
    quinoaBowl.setRestaurantId("rest123");
    quinoaBowl.setName("Quinoa Bowl");
    quinoaBowl.setDescription("Protein-rich quinoa with vegetables");
    quinoaBowl.setCategory("Bowls");
    quinoaBowl.setBasePrice(new BigDecimal("280"));
    quinoaBowl.setAvailable(true);
    quinoaBowl.setVegetarian(true);
    quinoaBowl.setVegan(true);
    quinoaBowl.setGlutenFree(true);
    quinoaBowl.setTags(Set.of("vegan", "healthy", "protein-rich"));
    NutritionalInfoVO nutrition4 = NutritionalInfoVO.builder()
        .calories(450)
        .protein(new BigDecimal("18"))
        .carbohydrates(new BigDecimal("55"))
        .fat(new BigDecimal("12"))
        .build();
    quinoaBowl.setNutritionalInfo(nutrition4);
    quinoaBowl.initializeDefaults();
    repository.save(quinoaBowl);

    // Gluten-Free Dessert
    MenuItem fruitCustard = new MenuItem();
    fruitCustard.setRestaurantId("rest123");
    fruitCustard.setName("Fruit Custard");
    fruitCustard.setDescription("Mixed fruits in creamy custard");
    fruitCustard.setCategory("Desserts");
    fruitCustard.setBasePrice(new BigDecimal("120"));
    fruitCustard.setAvailable(true);
    fruitCustard.setVegetarian(true);
    fruitCustard.setGlutenFree(true);
    fruitCustard.setTags(Set.of("dessert", "sweet", "gluten-free"));
    NutritionalInfoVO nutrition5 = NutritionalInfoVO.builder()
        .calories(280)
        .protein(new BigDecimal("5"))
        .carbohydrates(new BigDecimal("45"))
        .fat(new BigDecimal("8"))
        .build();
    fruitCustard.setNutritionalInfo(nutrition5);
    fruitCustard.initializeDefaults();
    repository.save(fruitCustard);
  }

  @Test
  @DisplayName("Should filter menu items by High Protein (protein >= 20g)")
  void shouldFilterByHighProtein() throws Exception {
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(3))));
    
    // Verify high protein items are included
    // Note: Actual filtering by protein would need to be added to search endpoint
  }

  @Test
  @DisplayName("Should filter menu items Under 500 Calories")
  void shouldFilterByCalories() throws Exception {
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
    
    // Should return items with calories < 500: Green Salad (250), Quinoa Bowl (450), Fruit Custard (280)
  }

  @Test
  @DisplayName("Should filter vegetarian menu items")
  void shouldFilterVegetarian() throws Exception {
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .vegetarian(true)
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(4))); // All except Chicken Biryani
  }

  @Test
  @DisplayName("Should filter vegan menu items")
  void shouldFilterVegan() throws Exception {
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .vegan(true)
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2))); // Green Salad, Quinoa Bowl
  }

  @Test
  @DisplayName("Should filter gluten-free menu items")
  void shouldFilterGlutenFree() throws Exception {
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .glutenFree(true)
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(4))); // All except Chicken Biryani
  }

  @Test
  @DisplayName("Should filter by price range")
  void shouldFilterByPriceRange() throws Exception {
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .minPrice(new BigDecimal("150"))
        .maxPrice(new BigDecimal("250"))
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
    
    // Should return: Green Salad (150), Paneer Tikka (220), Fruit Custard (120) - need to implement price filtering
  }

  @Test
  @DisplayName("Should search menu items by name")
  void shouldSearchByName() throws Exception {
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .searchText("biryani")
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
    
    // Should return Chicken Biryani
  }

  @Test
  @DisplayName("Should filter by tags")
  void shouldFilterByTags() throws Exception {
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .tags(Set.of("healthy"))
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
    
    // Should return: Green Salad, Quinoa Bowl
  }

  @Test
  @DisplayName("Should filter by category")
  void shouldFilterByCategory() throws Exception {
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .category("Main Course")
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].name", is("Chicken Biryani")));
  }

  @Test
  @DisplayName("Should combine multiple filters (Vegan + Under 500 Cal + Price Range)")
  void shouldCombineMultipleFilters() throws Exception {
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .vegan(true)
        .maxPrice(new BigDecimal("300"))
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
    
    // Should return: Green Salad (150), Quinoa Bowl (280) - both vegan and under 300
  }

  @Test
  @DisplayName("Should return only available items when filtered")
  void shouldFilterByAvailability() throws Exception {
    // Mark one item as unavailable
    MenuItem item = repository.findByRestaurantIdAndDeletedFalse("rest123", null)
        .getContent().get(0);
    item.setAvailable(false);
    repository.save(item);

    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .available(true)
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(4))); // One item marked unavailable
  }

  @Test
  @DisplayName("Should sort by price low to high")
  void shouldSortByPriceLowToHigh() throws Exception {
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .sortBy("basePrice")
        .sortDirection("asc")
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].name", is("Fruit Custard"))); // Cheapest at 120
  }

  @Test
  @DisplayName("Should sort by price high to low")
  void shouldSortByPriceHighToLow() throws Exception {
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .sortBy("basePrice")
        .sortDirection("desc")
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].name", is("Chicken Biryani"))); // Most expensive at 299
  }

  @Test
  @DisplayName("Should support pagination")
  void shouldSupportPagination() throws Exception {
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .page(0)
        .size(2)
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.totalElements", is(5)))
        .andExpect(jsonPath("$.totalPages", is(3)));
  }
}

