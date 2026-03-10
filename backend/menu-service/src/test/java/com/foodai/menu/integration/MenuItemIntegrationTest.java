package com.foodai.menu.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Menu Item APIs using Testcontainers.
 *
 * @author FoodAI Team
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Disabled("Requires Docker - enable when Docker environment is available")
@DisplayName("MenuItem Integration Tests")
class MenuItemIntegrationTest {

  @Container
  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.3")
      .withExposedPorts(27017);

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
  @DisplayName("Should create, retrieve, update, and delete menu item - Full lifecycle")
  void shouldPerformFullCRUDLifecycle() throws Exception {
    // Create
    CreateMenuItemRequest createRequest = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Butter Chicken")
        .description("Rich and creamy curry")
        .category("Main Course")
        .basePrice(new BigDecimal("350.00"))
        .available(true)
        .vegetarian(false)
        .preparationTime(25)
        .spiceLevel(3)
        .build();

    String createResponse = mockMvc.perform(post("/api/v1/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value("Butter Chicken"))
        .andReturn()
        .getResponse()
        .getContentAsString();

    String itemId = objectMapper.readTree(createResponse).get("id").asText();

    // Retrieve
    mockMvc.perform(get("/api/v1/menu-items/" + itemId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(itemId))
        .andExpect(jsonPath("$.name").value("Butter Chicken"));

    // Update availability
    mockMvc.perform(patch("/api/v1/menu-items/" + itemId + "/availability")
            .param("available", "false"))
        .andExpect(status().isOk());

    // Delete
    mockMvc.perform(delete("/api/v1/menu-items/" + itemId))
        .andExpect(status().isNoContent());

    // Verify deletion (soft delete)
    mockMvc.perform(get("/api/v1/menu-items/" + itemId))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("Should search menu items by restaurant")
  void shouldSearchMenuItemsByRestaurant() throws Exception {
    // Create test data
    MenuItem item1 = new MenuItem();
    item1.setRestaurantId("rest123");
    item1.setName("Butter Chicken");
    item1.setCategory("Main Course");
    item1.setBasePrice(new BigDecimal("350.00"));
    item1.initializeDefaults();
    repository.save(item1);

    MenuItem item2 = new MenuItem();
    item2.setRestaurantId("rest123");
    item2.setName("Paneer Tikka");
    item2.setCategory("Starter");
    item2.setBasePrice(new BigDecimal("250.00"));
    item2.setVegetarian(true);
    item2.initializeDefaults();
    repository.save(item2);

    // Search by restaurant
    mockMvc.perform(get("/api/v1/menu-items/restaurant/rest123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content.length()").value(2));
  }

  @Test
  @DisplayName("Should validate required fields")
  void shouldValidateRequiredFields() throws Exception {
    CreateMenuItemRequest invalidRequest = CreateMenuItemRequest.builder()
        .name("") // Invalid: blank name
        .basePrice(new BigDecimal("-100")) // Invalid: negative price
        .build();

    mockMvc.perform(post("/api/v1/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("Should prevent duplicate menu items")
  void shouldPreventDuplicateMenuItems() throws Exception {
    CreateMenuItemRequest request = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Butter Chicken")
        .category("Main Course")
        .basePrice(new BigDecimal("350.00"))
        .build();

    // First creation should succeed
    mockMvc.perform(post("/api/v1/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    // Duplicate should fail
    mockMvc.perform(post("/api/v1/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isConflict());
  }
}

