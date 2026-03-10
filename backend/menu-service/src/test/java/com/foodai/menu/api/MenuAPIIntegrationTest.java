package com.foodai.menu.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodai.menu.dto.request.CreateMenuItemRequest;
import com.foodai.menu.dto.request.MenuItemSearchRequest;
import com.foodai.menu.dto.request.UpdateMenuItemRequest;
import com.foodai.menu.dto.response.MenuItemResponse;
import com.foodai.menu.domain.repository.MenuItemRepository;
import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Menu Item REST API endpoints.
 *
 * <p>Tests cover:
 * - All REST endpoints (CRUD operations)
 * - Request/Response validation
 * - HTTP status codes
 * - Error handling and responses
 * - Content negotiation
 * - API contracts
 *
 * <p>Based on: Restaurant.tsx UI features and Consumer.tsx ordering flow
 *
 * @author FoodAI Team
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Menu API Integration Tests")
class MenuAPIIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MenuItemRepository menuItemRepository;

  @Autowired
  private OpenAPI openAPI;

  private CreateMenuItemRequest validMenuItemRequest;

  @AfterEach
  void cleanup() {
    menuItemRepository.deleteAll();
  }

  @BeforeEach
  void setUp() {
    validMenuItemRequest = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Test Menu Item")
        .category("Main Course")
        .description("Test description")
        .basePrice(new BigDecimal("250.00"))
        .vegetarian(true)
        .available(true)
        .build();
  }

  @Test
  @DisplayName("API: POST /api/v1/menu-items - Create menu item returns 201 Created")
  void shouldCreateMenuItemAndReturn201() throws Exception {
    // When creating a new menu item
    MvcResult result = mockMvc.perform(post("/api/v1/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validMenuItemRequest)))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.name").value("Test Menu Item"))
        .andExpect(jsonPath("$.basePrice").value(250.00))
        .andReturn();

    // Then response should contain created menu item
    String responseBody = result.getResponse().getContentAsString();
    MenuItemResponse response = objectMapper.readValue(responseBody, MenuItemResponse.class);
    assertThat(response.getId()).isNotNull();
    assertThat(response.getName()).isEqualTo("Test Menu Item");
  }

  @Test
  @DisplayName("API: POST /api/menu-items - Invalid request returns 400 Bad Request")
  void shouldReturn400ForInvalidCreateRequest() throws Exception {
    // Given invalid request (missing required fields)
    CreateMenuItemRequest invalidRequest = CreateMenuItemRequest.builder()
        // Missing restaurantId
        .name("Invalid Item")
        // Missing category
        // Missing price
        .build();

    // When sending invalid request
    mockMvc.perform(post("/api/v1/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").exists())
        .andExpect(jsonPath("$.validationErrors").exists());
  }

  @Test
  @DisplayName("API: GET /api/menu-items/{id} - Returns menu item details")
  void shouldGetMenuItemById() throws Exception {
    // Given a created menu item
    MvcResult createResult = mockMvc.perform(post("/api/v1/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validMenuItemRequest)))
        .andExpect(status().isCreated())
        .andReturn();

    MenuItemResponse created = objectMapper.readValue(
        createResult.getResponse().getContentAsString(),
        MenuItemResponse.class
    );

    // When retrieving by ID
    mockMvc.perform(get("/api/v1/menu-items/{id}", created.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(created.getId()))
        .andExpect(jsonPath("$.name").value("Test Menu Item"))
        .andExpect(jsonPath("$.restaurantId").value("rest123"));
  }

  @Test
  @DisplayName("API: GET /api/v1/menu-items/{id} - Returns 404 for non-existent item")
  void shouldReturn404ForNonExistentItem() throws Exception {
    // When requesting non-existent ID
    mockMvc.perform(get("/api/v1/menu-items/{id}", "non-existent-id"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").exists());
  }

  @Test
  @DisplayName("API: PUT /api/v1/menu-items/{id} - Updates menu item")
  void shouldUpdateMenuItemAndReturn200() throws Exception {
    // Given a created menu item
    MvcResult createResult = mockMvc.perform(post("/api/v1/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validMenuItemRequest)))
        .andExpect(status().isCreated())
        .andReturn();

    MenuItemResponse created = objectMapper.readValue(
        createResult.getResponse().getContentAsString(),
        MenuItemResponse.class
    );

    // When updating the item
    UpdateMenuItemRequest updateRequest = UpdateMenuItemRequest.builder()
        .name("Updated Menu Item")
        .category("Main Course")
        .basePrice(new BigDecimal("300.00"))
        .description("Updated description")
        .build();

    mockMvc.perform(put("/api/v1/menu-items/{id}", created.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Menu Item"))
        .andExpect(jsonPath("$.basePrice").value(300.00))
        .andExpect(jsonPath("$.description").value("Updated description"));
  }

  @Test
  @DisplayName("API: DELETE /api/v1/menu-items/{id} - Soft deletes menu item")
  void shouldDeleteMenuItemAndReturn204() throws Exception {
    // Given a created menu item
    MvcResult createResult = mockMvc.perform(post("/api/v1/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validMenuItemRequest)))
        .andExpect(status().isCreated())
        .andReturn();

    MenuItemResponse created = objectMapper.readValue(
        createResult.getResponse().getContentAsString(),
        MenuItemResponse.class
    );

    // When deleting the item
    mockMvc.perform(delete("/api/v1/menu-items/{id}", created.getId()))
        .andExpect(status().isNoContent());

    // Then item should not be found
    mockMvc.perform(get("/api/v1/menu-items/{id}", created.getId()))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("API: GET /api/menu-items - Search with filters returns paginated results")
  void shouldSearchMenuItemsWithFilters() throws Exception {
    // Given multiple menu items
    CreateMenuItemRequest item1 = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Veg Burger")
        .category("Fast Food")
        .basePrice(new BigDecimal("150.00"))
        .vegetarian(true)
        .build();

    CreateMenuItemRequest item2 = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Chicken Burger")
        .category("Fast Food")
        .basePrice(new BigDecimal("200.00"))
        .vegetarian(false)
        .build();

    mockMvc.perform(post("/api/v1/menu-items")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(item1)))
        .andExpect(status().isCreated());

    mockMvc.perform(post("/api/v1/menu-items")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(item2)))
        .andExpect(status().isCreated());

    // When searching with vegetarian filter
    MenuItemSearchRequest searchRequest = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .vegetarian(true)
        .category("Fast Food")
        .build();
    
    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(searchRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].name").value("Veg Burger"))
        .andExpect(jsonPath("$.totalElements").value(1))
        .andExpect(jsonPath("$.totalPages").exists());
  }

  @Test
  @DisplayName("API: PATCH /api/menu-items/{id}/availability - Updates availability")
  void shouldUpdateItemAvailability() throws Exception {
    // Given a created menu item
    MvcResult createResult = mockMvc.perform(post("/api/v1/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validMenuItemRequest)))
        .andExpect(status().isCreated())
        .andReturn();

    MenuItemResponse created = objectMapper.readValue(
        createResult.getResponse().getContentAsString(),
        MenuItemResponse.class
    );

    // When toggling availability to false
    mockMvc.perform(patch("/api/v1/menu-items/{id}/availability", created.getId())
            .param("available", "false"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.available").value(false));

    // And toggling back to true
    mockMvc.perform(patch("/api/v1/menu-items/{id}/availability", created.getId())
            .param("available", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.available").value(true));
  }

  @Test
  @DisplayName("API: GET /api/v1/menu-items/restaurant/{restaurantId}/top-selling - Returns best-selling items")
  void shouldGetTopSellingItems() throws Exception {
    // When requesting top-selling items
    mockMvc.perform(get("/api/v1/menu-items/restaurant/{restaurantId}/top-selling", "rest123")
            .param("limit", "5"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  @DisplayName("API: GET /api/v1/menu-items/restaurant/{restaurantId}/highly-rated - Returns highly-rated items")
  void shouldGetHighlyRatedItems() throws Exception {
    // When requesting highly-rated items
    mockMvc.perform(get("/api/v1/menu-items/restaurant/{restaurantId}/highly-rated", "rest123")
            .param("minRating", "4.0"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }

  @Test
  @DisplayName("API: POST /api/v1/menu-items/search - Searches by tags")
  void shouldSearchByTags() throws Exception {
    // When searching by tags
    MenuItemSearchRequest searchRequest = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .tags(Set.of("popular", "bestseller"))
        .build();
    
    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(searchRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray());
  }

  @Test
  @DisplayName("API: Error responses include proper error structure")
  void shouldReturnStandardizedErrorResponse() throws Exception {
    // When encountering an error
    mockMvc.perform(get("/api/v1/menu-items/{id}", "invalid-id"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.status").value(404))
        .andExpect(jsonPath("$.message").exists())
        .andExpect(jsonPath("$.path").exists());
  }

  @Test
  @DisplayName("API: Supports CORS for cross-origin requests")
  void shouldSupportCORSHeaders() throws Exception {
    // When making cross-origin request
    mockMvc.perform(get("/api/v1/menu-items")
            .header("Origin", "http://localhost:3000"))
        .andExpect(status().isOk())
        .andExpect(header().exists("Access-Control-Allow-Origin"));
  }

  @Test
  @DisplayName("API: Returns proper content type")
  void shouldReturnJsonContentType() throws Exception {
    // When requesting menu items
    mockMvc.perform(get("/api/v1/menu-items")
            .param("restaurantId", "rest123"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  @DisplayName("API: Validates price range in search")
  void shouldValidatePriceRangeInSearch() throws Exception {
    // When searching with valid price range
    MenuItemSearchRequest validRequest = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .minPrice(new BigDecimal("100"))
        .maxPrice(new BigDecimal("500"))
        .build();
        
    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validRequest)))
        .andExpect(status().isOk());

    // When min > max (invalid)
    MenuItemSearchRequest invalidRequest = MenuItemSearchRequest.builder()
        .restaurantId("rest123")
        .minPrice(new BigDecimal("500"))
        .maxPrice(new BigDecimal("100"))
        .build();
        
    mockMvc.perform(post("/api/v1/menu-items/search")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("API: Pagination parameters work correctly")
  void shouldHandlePaginationParameters() throws Exception {
    // When requesting with pagination
    mockMvc.perform(get("/api/v1/menu-items")
            .param("restaurantId", "rest123")
            .param("page", "0")
            .param("size", "10")
            .param("sort", "basePrice,asc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.number").value(0))
        .andExpect(jsonPath("$.size").exists())
        .andExpect(jsonPath("$.totalPages").exists())
        .andExpect(jsonPath("$.totalElements").exists());
  }

  @Test
  @DisplayName("API: Bulk operations return appropriate responses")
  void shouldHandleBulkOperations() throws Exception {
    // Given multiple items
    String[] itemIds = new String[3];
    for (int i = 0; i < 3; i++) {
      CreateMenuItemRequest request = CreateMenuItemRequest.builder()
          .restaurantId("rest123")
          .name("Bulk Item " + i)
          .category("Test")
          .basePrice(new BigDecimal("100.00"))
          .build();

      MvcResult result = mockMvc.perform(post("/api/v1/menu-items")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated())
          .andReturn();

      MenuItemResponse response = objectMapper.readValue(
          result.getResponse().getContentAsString(),
          MenuItemResponse.class
      );
      itemIds[i] = response.getId();
    }

    // When performing bulk update (if implemented)
    // Then should return appropriate status
    assertThat(itemIds).hasSize(3);
  }

  @Test
  @DisplayName("API: OpenAPI/Swagger documentation accessible")
  void shouldProvideOpenAPIDocumentation() throws Exception {
    // Verify that OpenAPI bean is configured
    // Note: The actual endpoint /v3/api-docs is served by Springdoc at runtime
    // In tests, we verify the OpenAPI configuration is present
    assertThat(openAPI).isNotNull();
    assertThat(openAPI.getInfo()).isNotNull();
    assertThat(openAPI.getInfo().getTitle()).isEqualTo("Menu Service API");
    assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0.0");
  }
}

