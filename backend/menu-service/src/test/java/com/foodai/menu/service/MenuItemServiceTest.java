package com.foodai.menu.service;

import com.foodai.menu.domain.model.MenuItem;
import com.foodai.menu.domain.model.MenuItemStatus;
import com.foodai.menu.domain.repository.MenuItemRepository;
import com.foodai.menu.dto.request.CreateMenuItemRequest;
import com.foodai.menu.dto.request.UpdateMenuItemRequest;
import com.foodai.menu.dto.response.MenuItemResponse;
import com.foodai.menu.exception.DuplicateMenuItemException;
import com.foodai.menu.exception.MenuItemNotFoundException;
import com.foodai.menu.mapper.MenuItemMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MenuItemService.
 *
 * @author FoodAI Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MenuItem Service Tests")
class MenuItemServiceTest {

  @Mock
  private MenuItemRepository repository;

  @Mock
  private MenuItemMapper mapper;

  @Mock
  private KafkaTemplate<String, Object> kafkaTemplate;

  @InjectMocks
  private MenuItemService service;

  private CreateMenuItemRequest createRequest;
  private UpdateMenuItemRequest updateRequest;
  private MenuItem menuItem;
  private MenuItemResponse menuItemResponse;

  @BeforeEach
  void setUp() {
    // Arrange - Setup test data
    createRequest = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Butter Chicken")
        .description("Rich and creamy curry")
        .category("Main Course")
        .basePrice(new BigDecimal("350.00"))
        .available(true)
        .vegetarian(false)
        .build();

    updateRequest = UpdateMenuItemRequest.builder()
        .name("Updated Butter Chicken")
        .basePrice(new BigDecimal("375.00"))
        .build();

    menuItem = new MenuItem();
    menuItem.setId("item123");
    menuItem.setRestaurantId("rest123");
    menuItem.setName("Butter Chicken");
    menuItem.setBasePrice(new BigDecimal("350.00"));
    menuItem.setStatus(MenuItemStatus.ACTIVE);
    menuItem.setDeleted(false);

    menuItemResponse = MenuItemResponse.builder()
        .id("item123")
        .restaurantId("rest123")
        .name("Butter Chicken")
        .basePrice(new BigDecimal("350.00"))
        .build();
  }

  @Test
  @DisplayName("Should create menu item successfully")
  void shouldCreateMenuItem_whenValidInput() {
    // Arrange
    when(repository.existsByRestaurantIdAndNameAndDeletedFalse(anyString(), anyString())).thenReturn(false);
    when(mapper.toEntity(any(CreateMenuItemRequest.class))).thenReturn(menuItem);
    when(repository.save(any(MenuItem.class))).thenReturn(menuItem);
    when(mapper.toResponse(any(MenuItem.class))).thenReturn(menuItemResponse);
    when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(null);

    // Act
    MenuItemResponse response = service.create(createRequest);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getName()).isEqualTo("Butter Chicken");
    assertThat(response.getRestaurantId()).isEqualTo("rest123");
    
    verify(repository).existsByRestaurantIdAndNameAndDeletedFalse("rest123", "Butter Chicken");
    verify(repository).save(any(MenuItem.class));
    verify(kafkaTemplate).send(eq("menu.item.created"), eq("item123"), any());
  }

  @Test
  @DisplayName("Should throw exception when creating duplicate menu item")
  void shouldThrowException_whenCreatingDuplicateMenuItem() {
    // Arrange
    when(repository.existsByRestaurantIdAndNameAndDeletedFalse(anyString(), anyString())).thenReturn(true);

    // Act & Assert
    assertThatThrownBy(() -> service.create(createRequest))
        .isInstanceOf(DuplicateMenuItemException.class)
        .hasMessageContaining("Butter Chicken")
        .hasMessageContaining("rest123");

    verify(repository, never()).save(any(MenuItem.class));
  }

  @Test
  @DisplayName("Should find menu item by ID")
  void shouldFindMenuItemById_whenExists() {
    // Arrange
    when(repository.findByIdAndDeletedFalse("item123")).thenReturn(Optional.of(menuItem));
    when(mapper.toDetailResponse(any(MenuItem.class))).thenReturn(mock(com.foodai.menu.dto.response.MenuItemDetailResponse.class));

    // Act
    var response = service.findById("item123");

    // Assert
    assertThat(response).isNotNull();
    verify(repository).findByIdAndDeletedFalse("item123");
  }

  @Test
  @DisplayName("Should throw exception when menu item not found")
  void shouldThrowException_whenMenuItemNotFound() {
    // Arrange
    when(repository.findByIdAndDeletedFalse("nonexistent")).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> service.findById("nonexistent"))
        .isInstanceOf(MenuItemNotFoundException.class)
        .hasMessageContaining("nonexistent");
  }

  @Test
  @DisplayName("Should find all menu items with pagination")
  void shouldFindAllMenuItems_withPagination() {
    // Arrange
    Pageable pageable = PageRequest.of(0, 10);
    Page<MenuItem> page = new PageImpl<>(List.of(menuItem));
    when(repository.findByDeletedFalse(pageable)).thenReturn(page);
    when(mapper.toResponse(any(MenuItem.class))).thenReturn(menuItemResponse);

    // Act
    Page<MenuItemResponse> result = service.findAll(pageable);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    verify(repository).findByDeletedFalse(pageable);
  }

  @Test
  @DisplayName("Should find menu items by restaurant")
  void shouldFindMenuItemsByRestaurant() {
    // Arrange
    Pageable pageable = PageRequest.of(0, 10);
    Page<MenuItem> page = new PageImpl<>(List.of(menuItem));
    when(repository.findByRestaurantIdAndDeletedFalse("rest123", pageable)).thenReturn(page);
    when(mapper.toResponse(any(MenuItem.class))).thenReturn(menuItemResponse);

    // Act
    Page<MenuItemResponse> result = service.findByRestaurant("rest123", pageable);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    verify(repository).findByRestaurantIdAndDeletedFalse("rest123", pageable);
  }

  @Test
  @DisplayName("Should update menu item successfully")
  void shouldUpdateMenuItem_whenValidInput() {
    // Arrange
    when(repository.findByIdAndDeletedFalse("item123")).thenReturn(Optional.of(menuItem));
    when(repository.save(any(MenuItem.class))).thenReturn(menuItem);
    when(mapper.toResponse(any(MenuItem.class))).thenReturn(menuItemResponse);
    when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(null);
    doNothing().when(mapper).updateEntityFromRequest(any(), any());

    // Act
    MenuItemResponse response = service.update("item123", updateRequest);

    // Assert
    assertThat(response).isNotNull();
    verify(repository).findByIdAndDeletedFalse("item123");
    verify(mapper).updateEntityFromRequest(updateRequest, menuItem);
    verify(repository).save(menuItem);
    verify(kafkaTemplate).send(eq("menu.item.updated"), eq("item123"), any());
  }

  @Test
  @DisplayName("Should update availability status")
  void shouldUpdateAvailability() {
    // Arrange
    when(repository.findByIdAndDeletedFalse("item123")).thenReturn(Optional.of(menuItem));
    when(repository.save(any(MenuItem.class))).thenReturn(menuItem);
    when(mapper.toResponse(any(MenuItem.class))).thenReturn(menuItemResponse);
    when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(null);

    // Act
    MenuItemResponse response = service.updateAvailability("item123", true);

    // Assert
    assertThat(response).isNotNull();
    verify(repository).findByIdAndDeletedFalse("item123");
    verify(repository).save(menuItem);
    verify(kafkaTemplate).send(eq("menu.item.availability.changed"), eq("item123"), any());
  }

  @Test
  @DisplayName("Should delete menu item (soft delete)")
  void shouldDeleteMenuItem() {
    // Arrange
    when(repository.findByIdAndDeletedFalse("item123")).thenReturn(Optional.of(menuItem));
    when(repository.save(any(MenuItem.class))).thenReturn(menuItem);
    when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(null);

    // Act
    service.delete("item123");

    // Assert
    verify(repository).findByIdAndDeletedFalse("item123");
    verify(repository).save(menuItem);
    verify(kafkaTemplate).send(eq("menu.item.deleted"), eq("item123"), isNull());
  }

  @Test
  @DisplayName("Should find top-selling items")
  void shouldFindTopSellingItems() {
    // Arrange
    when(repository.findTopSellingItems(anyString(), any(Pageable.class))).thenReturn(List.of(menuItem));
    when(mapper.toResponseList(anyList())).thenReturn(List.of(menuItemResponse));

    // Act
    List<MenuItemResponse> result = service.findTopSelling("rest123", 10);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    verify(repository).findTopSellingItems(eq("rest123"), any(Pageable.class));
  }

  @Test
  @DisplayName("Should record order for menu item")
  void shouldRecordOrder() {
    // Arrange
    when(repository.findByIdAndDeletedFalse("item123")).thenReturn(Optional.of(menuItem));
    when(repository.save(any(MenuItem.class))).thenReturn(menuItem);

    // Act
    service.recordOrder("item123");

    // Assert
    verify(repository).findByIdAndDeletedFalse("item123");
    verify(repository).save(menuItem);
  }

  @Test
  @DisplayName("Should update rating for menu item")
  void shouldUpdateRating() {
    // Arrange
    BigDecimal newRating = new BigDecimal("4.5");
    when(repository.findByIdAndDeletedFalse("item123")).thenReturn(Optional.of(menuItem));
    when(repository.save(any(MenuItem.class))).thenReturn(menuItem);

    // Act
    service.updateRating("item123", newRating);

    // Assert
    verify(repository).findByIdAndDeletedFalse("item123");
    verify(repository).save(menuItem);
  }

  @Test
  @DisplayName("Should count menu items by restaurant")
  void shouldCountByRestaurant() {
    // Arrange
    when(repository.countByRestaurantIdAndDeletedFalse("rest123")).thenReturn(5L);

    // Act
    long count = service.countByRestaurant("rest123");

    // Assert
    assertThat(count).isEqualTo(5L);
    verify(repository).countByRestaurantIdAndDeletedFalse("rest123");
  }
}

