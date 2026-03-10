package com.foodai.menu.mapper;

import com.foodai.menu.domain.model.MenuItem;
import com.foodai.menu.domain.model.MenuItemStatus;
import com.foodai.menu.dto.request.CreateMenuItemRequest;
import com.foodai.menu.dto.request.UpdateMenuItemRequest;
import com.foodai.menu.dto.response.MenuItemDetailResponse;
import com.foodai.menu.dto.response.MenuItemResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for MenuItemMapper.
 *
 * @author FoodAI Team
 */
@SpringBootTest(classes = {MenuItemMapperImpl.class})
@DisplayName("MenuItem Mapper Tests")
class MenuItemMapperTest {

  @Autowired
  private MenuItemMapper mapper;

  @Test
  @DisplayName("Should map CreateMenuItemRequest to MenuItem entity")
  void shouldMapCreateRequestToEntity() {
    // Arrange
    CreateMenuItemRequest request = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Butter Chicken")
        .description("Rich and creamy curry")
        .category("Main Course")
        .basePrice(new BigDecimal("350.00"))
        .available(true)
        .vegetarian(false)
        .build();

    // Act
    MenuItem entity = mapper.toEntity(request);

    // Assert
    assertThat(entity).isNotNull();
    assertThat(entity.getRestaurantId()).isEqualTo("rest123");
    assertThat(entity.getName()).isEqualTo("Butter Chicken");
    assertThat(entity.getBasePrice()).isEqualByComparingTo(new BigDecimal("350.00"));
    assertThat(entity.isAvailable()).isTrue();
  }

  @Test
  @DisplayName("Should map MenuItem entity to MenuItemResponse")
  void shouldMapEntityToResponse() {
    // Arrange
    MenuItem entity = new MenuItem();
    entity.setId("item123");
    entity.setRestaurantId("rest123");
    entity.setName("Butter Chicken");
    entity.setBasePrice(new BigDecimal("350.00"));
    entity.setImages(List.of("image1.jpg", "image2.jpg"));
    entity.setStatus(MenuItemStatus.ACTIVE);

    // Act
    MenuItemResponse response = mapper.toResponse(entity);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo("item123");
    assertThat(response.getName()).isEqualTo("Butter Chicken");
    assertThat(response.getPrimaryImage()).isEqualTo("image1.jpg");
  }

  @Test
  @DisplayName("Should map MenuItem entity to MenuItemDetailResponse")
  void shouldMapEntityToDetailResponse() {
    // Arrange
    MenuItem entity = new MenuItem();
    entity.setId("item123");
    entity.setRestaurantId("rest123");
    entity.setName("Butter Chicken");
    entity.setDescription("Rich curry");
    entity.setBasePrice(new BigDecimal("350.00"));
    entity.setStatus(MenuItemStatus.ACTIVE);

    // Act
    MenuItemDetailResponse response = mapper.toDetailResponse(entity);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo("item123");
    assertThat(response.getName()).isEqualTo("Butter Chicken");
    assertThat(response.getDescription()).isEqualTo("Rich curry");
  }

  @Test
  @DisplayName("Should update entity from UpdateMenuItemRequest")
  void shouldUpdateEntityFromRequest() {
    // Arrange
    MenuItem entity = new MenuItem();
    entity.setId("item123");
    entity.setName("Old Name");
    entity.setBasePrice(new BigDecimal("300.00"));

    UpdateMenuItemRequest request = UpdateMenuItemRequest.builder()
        .name("New Name")
        .basePrice(new BigDecimal("400.00"))
        .build();

    // Act
    mapper.updateEntityFromRequest(request, entity);

    // Assert
    assertThat(entity.getName()).isEqualTo("New Name");
    assertThat(entity.getBasePrice()).isEqualByComparingTo(new BigDecimal("400.00"));
    assertThat(entity.getId()).isEqualTo("item123"); // ID should not change
  }

  @Test
  @DisplayName("Should map list of entities to list of responses")
  void shouldMapEntityListToResponseList() {
    // Arrange
    MenuItem entity1 = new MenuItem();
    entity1.setId("item1");
    entity1.setName("Item 1");

    MenuItem entity2 = new MenuItem();
    entity2.setId("item2");
    entity2.setName("Item 2");

    // Act
    List<MenuItemResponse> responses = mapper.toResponseList(List.of(entity1, entity2));

    // Assert
    assertThat(responses).hasSize(2);
    assertThat(responses.get(0).getId()).isEqualTo("item1");
    assertThat(responses.get(1).getId()).isEqualTo("item2");
  }
}

