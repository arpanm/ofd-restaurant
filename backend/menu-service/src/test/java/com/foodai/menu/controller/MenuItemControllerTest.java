package com.foodai.menu.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodai.menu.dto.request.CreateMenuItemRequest;
import com.foodai.menu.dto.request.UpdateMenuItemRequest;
import com.foodai.menu.dto.response.MenuItemDetailResponse;
import com.foodai.menu.dto.response.MenuItemResponse;
import com.foodai.menu.exception.MenuItemNotFoundException;
import com.foodai.menu.service.MenuItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for MenuItemController.
 *
 * @author FoodAI Team
 */
@WebMvcTest(MenuItemController.class)
@DisplayName("MenuItem Controller Tests")
class MenuItemControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private MenuItemService service;

  private CreateMenuItemRequest createRequest;
  private MenuItemResponse response;
  private MenuItemDetailResponse detailResponse;

  @BeforeEach
  void setUp() {
    createRequest = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Butter Chicken")
        .description("Rich and creamy curry")
        .category("Main Course")
        .basePrice(new BigDecimal("350.00"))
        .available(true)
        .vegetarian(false)
        .build();

    response = MenuItemResponse.builder()
        .id("item123")
        .restaurantId("rest123")
        .name("Butter Chicken")
        .basePrice(new BigDecimal("350.00"))
        .build();

    detailResponse = MenuItemDetailResponse.builder()
        .id("item123")
        .restaurantId("rest123")
        .name("Butter Chicken")
        .description("Rich and creamy curry")
        .basePrice(new BigDecimal("350.00"))
        .build();
  }

  @Test
  @DisplayName("POST /api/v1/menu-items - Should create menu item")
  void shouldCreateMenuItem() throws Exception {
    // Arrange
    when(service.create(any(CreateMenuItemRequest.class))).thenReturn(response);

    // Act & Assert
    mockMvc.perform(post("/api/v1/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("item123"))
        .andExpect(jsonPath("$.name").value("Butter Chicken"));
  }

  @Test
  @DisplayName("GET /api/v1/menu-items/{id} - Should return menu item")
  void shouldGetMenuItemById() throws Exception {
    // Arrange
    when(service.findById("item123")).thenReturn(detailResponse);

    // Act & Assert
    mockMvc.perform(get("/api/v1/menu-items/item123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("item123"))
        .andExpect(jsonPath("$.name").value("Butter Chicken"));
  }

  @Test
  @DisplayName("GET /api/v1/menu-items/{id} - Should return 404 when not found")
  void shouldReturn404_whenMenuItemNotFound() throws Exception {
    // Arrange
    when(service.findById("nonexistent")).thenThrow(new MenuItemNotFoundException("nonexistent"));

    // Act & Assert
    mockMvc.perform(get("/api/v1/menu-items/nonexistent"))
        .andExpect(status().isNotFound());
  }

  @Test
  @DisplayName("GET /api/v1/menu-items - Should return all menu items")
  void shouldGetAllMenuItems() throws Exception {
    // Arrange
    var page = new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1);
    when(service.findAll(any())).thenReturn(page);

    // Act & Assert
    mockMvc.perform(get("/api/v1/menu-items")
            .param("page", "0")
            .param("size", "20"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content[0].id").value("item123"));
  }

  @Test
  @DisplayName("GET /api/v1/menu-items/restaurant/{restaurantId} - Should return restaurant menu items")
  void shouldGetMenuItemsByRestaurant() throws Exception {
    // Arrange
    var page = new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1);
    when(service.findByRestaurant(anyString(), any())).thenReturn(page);

    // Act & Assert
    mockMvc.perform(get("/api/v1/menu-items/restaurant/rest123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").isArray());
  }

  @Test
  @DisplayName("PUT /api/v1/menu-items/{id} - Should update menu item")
  void shouldUpdateMenuItem() throws Exception {
    // Arrange
    UpdateMenuItemRequest updateRequest = UpdateMenuItemRequest.builder()
        .name("Updated Butter Chicken")
        .category("Main Course")
        .basePrice(new BigDecimal("375.00"))
        .build();
    when(service.update(anyString(), any(UpdateMenuItemRequest.class))).thenReturn(response);

    // Act & Assert
    mockMvc.perform(put("/api/v1/menu-items/item123")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("item123"));
  }

  @Test
  @DisplayName("PATCH /api/v1/menu-items/{id}/availability - Should update availability")
  void shouldUpdateAvailability() throws Exception {
    // Arrange
    when(service.updateAvailability("item123", true)).thenReturn(response);

    // Act & Assert
    mockMvc.perform(patch("/api/v1/menu-items/item123/availability")
            .param("available", "true"))
        .andExpect(status().isOk());
  }

  @Test
  @DisplayName("DELETE /api/v1/menu-items/{id} - Should delete menu item")
  void shouldDeleteMenuItem() throws Exception {
    // Act & Assert
    mockMvc.perform(delete("/api/v1/menu-items/item123"))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("GET /api/v1/menu-items/restaurant/{restaurantId}/top-selling - Should return top-selling items")
  void shouldGetTopSellingItems() throws Exception {
    // Arrange
    when(service.findTopSelling("rest123", 10)).thenReturn(List.of(response));

    // Act & Assert
    mockMvc.perform(get("/api/v1/menu-items/restaurant/rest123/top-selling")
            .param("limit", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
  }
}

