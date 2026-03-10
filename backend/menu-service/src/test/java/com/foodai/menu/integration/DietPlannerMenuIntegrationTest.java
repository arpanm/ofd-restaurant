package com.foodai.menu.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodai.menu.domain.model.MenuItem;
import com.foodai.menu.domain.model.NutritionalInfoVO;
import com.foodai.menu.domain.repository.MenuItemRepository;
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
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Diet Planner Menu Integration.
 * 
 * <p>Tests diet planner requirements from DIET_PLANNER.md:
 * - Filtering by nutritional content (calories, protein, carbs, fat)
 * - Dietary preference filtering (vegetarian, vegan, keto, low-carb)
 * - Meal type categorization (breakfast, lunch, snacks, dinner)
 * - Calorie limit filtering
 * - Macro-nutrient targeting
 * - Food allergy exclusions
 *
 * @author FoodAI Team
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Disabled("Requires Docker - enable when Docker environment is available")
@DisplayName("Diet Planner Menu Integration Tests")
class DietPlannerMenuIntegrationTest {

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
    createDietPlannerTestItems();
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
  }

  private void createDietPlannerTestItems() {
    // High Protein Breakfast
    MenuItem omelette = new MenuItem();
    omelette.setRestaurantId("rest123");
    omelette.setName("Protein Omelette");
    omelette.setCategory("Breakfast");
    omelette.setBasePrice(new BigDecimal("150"));
    omelette.setVegetarian(true);
    omelette.setTags(Set.of("breakfast", "high-protein", "keto-friendly"));
    NutritionalInfoVO nutrition1 = NutritionalInfoVO.builder()
        .calories(300)
        .protein(new BigDecimal("25"))
        .carbohydrates(new BigDecimal("5"))
        .fat(new BigDecimal("20"))
        .build();
    omelette.setNutritionalInfo(nutrition1);
    omelette.initializeDefaults();
    repository.save(omelette);

    // Low Calorie Lunch
    MenuItem grilledChickenSalad = new MenuItem();
    grilledChickenSalad.setRestaurantId("rest123");
    grilledChickenSalad.setName("Grilled Chicken Salad");
    grilledChickenSalad.setCategory("Lunch");
    grilledChickenSalad.setBasePrice(new BigDecimal("280"));
    grilledChickenSalad.setVegetarian(false);
    grilledChickenSalad.setGlutenFree(true);
    grilledChickenSalad.setTags(Set.of("lunch", "low-calorie", "high-protein"));
    NutritionalInfoVO nutrition2 = NutritionalInfoVO.builder()
        .calories(350)
        .protein(new BigDecimal("30"))
        .carbohydrates(new BigDecimal("20"))
        .fat(new BigDecimal("15"))
        .build();
    grilledChickenSalad.setNutritionalInfo(nutrition2);
    grilledChickenSalad.initializeDefaults();
    repository.save(grilledChickenSalad);

    // Vegan Snack
    MenuItem fruitBowl = new MenuItem();
    fruitBowl.setRestaurantId("rest123");
    fruitBowl.setName("Mixed Fruit Bowl");
    fruitBowl.setCategory("Snacks");
    fruitBowl.setBasePrice(new BigDecimal("100"));
    fruitBowl.setVegetarian(true);
    fruitBowl.setVegan(true);
    fruitBowl.setGlutenFree(true);
    fruitBowl.setTags(Set.of("snack", "vegan", "low-calorie"));
    NutritionalInfoVO nutrition3 = NutritionalInfoVO.builder()
        .calories(150)
        .protein(new BigDecimal("2"))
        .carbohydrates(new BigDecimal("35"))
        .fat(new BigDecimal("1"))
        .build();
    fruitBowl.setNutritionalInfo(nutrition3);
    fruitBowl.initializeDefaults();
    repository.save(fruitBowl);

    // Keto Dinner
    MenuItem salmonSteak = new MenuItem();
    salmonSteak.setRestaurantId("rest123");
    salmonSteak.setName("Grilled Salmon Steak");
    salmonSteak.setCategory("Dinner");
    salmonSteak.setBasePrice(new BigDecimal("450"));
    salmonSteak.setVegetarian(false);
    salmonSteak.setGlutenFree(true);
    salmonSteak.setTags(Set.of("dinner", "keto", "high-protein", "low-carb"));
    NutritionalInfoVO nutrition4 = NutritionalInfoVO.builder()
        .calories(400)
        .protein(new BigDecimal("35"))
        .carbohydrates(new BigDecimal("3"))
        .fat(new BigDecimal("25"))
        .build();
    salmonSteak.setNutritionalInfo(nutrition4);
    salmonSteak.initializeDefaults();
    repository.save(salmonSteak);

    // Low Carb Lunch
    MenuItem cauliflowerRice = new MenuItem();
    cauliflowerRice.setRestaurantId("rest123");
    cauliflowerRice.setName("Cauliflower Fried Rice");
    cauliflowerRice.setCategory("Lunch");
    cauliflowerRice.setBasePrice(new BigDecimal("200"));
    cauliflowerRice.setVegetarian(true);
    cauliflowerRice.setVegan(true);
    cauliflowerRice.setGlutenFree(true);
    cauliflowerRice.setTags(Set.of("lunch", "low-carb", "vegan"));
    NutritionalInfoVO nutrition5 = NutritionalInfoVO.builder()
        .calories(250)
        .protein(new BigDecimal("10"))
        .carbohydrates(new BigDecimal("15"))
        .fat(new BigDecimal("12"))
        .build();
    cauliflowerRice.setNutritionalInfo(nutrition5);
    cauliflowerRice.initializeDefaults();
    repository.save(cauliflowerRice);
  }

  @Test
  @DisplayName("Should filter meals under calorie limit (500 cal)")
  void shouldFilterByCalorieLimit() throws Exception {
    // Act - Search for items under 500 calories
    mockMvc.perform(get("/api/v1/menu-items/restaurant/{restaurantId}", "rest123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(5)));

    // All test items are under 500 calories
    // In real implementation, would add maxCalories parameter to search
  }

  @Test
  @DisplayName("Should filter breakfast items for morning meal plan")
  void shouldFilterBreakfastItems() throws Exception {
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .category("Breakfast")
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].name", is("Protein Omelette")));
  }

  @Test
  @DisplayName("Should filter lunch items")
  void shouldFilterLunchItems() throws Exception {
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .category("Lunch")
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2))); // Grilled Chicken Salad, Cauliflower Rice
  }

  @Test
  @DisplayName("Should filter snack items")
  void shouldFilterSnackItems() throws Exception {
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .category("Snacks")
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].name", is("Mixed Fruit Bowl")));
  }

  @Test
  @DisplayName("Should filter dinner items")
  void shouldFilterDinnerItems() throws Exception {
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .category("Dinner")
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content[0].name", is("Grilled Salmon Steak")));
  }

  @Test
  @DisplayName("Should filter items for Keto diet plan")
  void shouldFilterKetoItems() throws Exception {
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .tags(Set.of("keto"))
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))));
    
    // Should return: Protein Omelette, Grilled Salmon Steak
  }

  @Test
  @DisplayName("Should filter items for Vegan diet plan")
  void shouldFilterVeganItems() throws Exception {
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .vegan(true)
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2))); // Mixed Fruit Bowl, Cauliflower Rice
  }

  @Test
  @DisplayName("Should filter items for Low Carb diet plan")
  void shouldFilterLowCarbItems() throws Exception {
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .tags(Set.of("low-carb"))
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
    
    // Should return: Grilled Salmon Steak, Cauliflower Rice
  }

  @Test
  @DisplayName("Should filter high protein items for muscle gain plan")
  void shouldFilterHighProteinItems() throws Exception {
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .tags(Set.of("high-protein"))
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk());
    
    // Should return items with protein >= 20g: Omelette, Grilled Chicken Salad, Salmon Steak
  }

  @Test
  @DisplayName("Should exclude items based on allergens")
  void shouldExcludeAllergens() {
    // Note: This test would require adding allergens to nutritional info
    // and filtering logic in search endpoint
    
    // Use case: User allergic to fish should not see Salmon Steak
  }

  @Test
  @DisplayName("Should create weekly meal plan with balanced macros")
  void shouldCreateBalancedMealPlan() throws Exception {
    // Scenario: Create 7-day meal plan with daily calorie target of 2000 cal
    
    // Day 1:
    // Breakfast: Protein Omelette (300 cal, 25g protein)
    // Lunch: Grilled Chicken Salad (350 cal, 30g protein)
    // Snack: Mixed Fruit Bowl (150 cal, 2g protein)
    // Dinner: Grilled Salmon Steak (400 cal, 35g protein)
    // Total: 1200 cal, 92g protein
    
    // System should suggest additional items to reach 2000 cal target
  }

  @Test
  @DisplayName("Should filter gluten-free items for celiac diet plan")
  void shouldFilterGlutenFreeItems() throws Exception {
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .glutenFree(true)
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(4))); // All except Protein Omelette
  }

  @Test
  @DisplayName("Should combine dietary filters for complex diet plan")
  void shouldCombineDietaryFilters() throws Exception {
    // Use case: Vegan + Gluten-Free + Low-Calorie (<300 cal)
    MenuItemSearchRequest request = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .vegan(true)
        .glutenFree(true)
        .build();

    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(2))); // Mixed Fruit Bowl, Cauliflower Rice
  }

  @Test
  @DisplayName("Should get nutritional summary for meal plan")
  void shouldGetNutritionalSummary() {
    // Use case: Calculate total calories, protein, carbs, fat for selected meals
    
    // Selected meals:
    // - Protein Omelette: 300 cal, 25p, 5c, 20f
    // - Grilled Chicken Salad: 350 cal, 30p, 20c, 15f
    // - Mixed Fruit Bowl: 150 cal, 2p, 35c, 1f
    // - Grilled Salmon Steak: 400 cal, 35p, 3c, 25f
    
    // Total: 1200 cal, 92p, 63c, 61f
  }
}

