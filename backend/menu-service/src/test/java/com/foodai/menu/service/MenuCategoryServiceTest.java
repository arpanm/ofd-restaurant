package com.foodai.menu.service;

import com.foodai.menu.domain.model.MenuCategory;
import com.foodai.menu.domain.repository.MenuCategoryRepository;
import com.foodai.menu.dto.request.CreateMenuCategoryRequest;
import com.foodai.menu.dto.response.MenuCategoryResponse;
import com.foodai.menu.exception.DuplicateMenuCategoryException;
import com.foodai.menu.exception.MenuCategoryNotFoundException;
import com.foodai.menu.mapper.MenuCategoryMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MenuCategoryService.
 *
 * @author FoodAI Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MenuCategory Service Tests")
class MenuCategoryServiceTest {

  @Mock
  private MenuCategoryRepository repository;

  @Mock
  private MenuCategoryMapper mapper;

  @Mock
  private KafkaTemplate<String, Object> kafkaTemplate;

  @InjectMocks
  private MenuCategoryService service;

  private CreateMenuCategoryRequest createRequest;
  private MenuCategory category;
  private MenuCategoryResponse categoryResponse;

  @BeforeEach
  void setUp() {
    // Arrange - Setup test data
    createRequest = CreateMenuCategoryRequest.builder()
        .restaurantId("rest123")
        .name("Main Course")
        .description("Traditional main dishes")
        .displayOrder(1)
        .active(true)
        .build();

    category = new MenuCategory();
    category.setId("cat123");
    category.setRestaurantId("rest123");
    category.setName("Main Course");
    category.setDescription("Traditional main dishes");
    category.setDisplayOrder(1);
    category.setActive(true);
    category.setDeleted(false);

    categoryResponse = MenuCategoryResponse.builder()
        .id("cat123")
        .restaurantId("rest123")
        .name("Main Course")
        .description("Traditional main dishes")
        .displayOrder(1)
        .active(true)
        .build();
  }

  @Test
  @DisplayName("Should create menu category successfully")
  void shouldCreateMenuCategory_whenValidInput() {
    // Arrange
    when(repository.existsByRestaurantIdAndNameAndDeletedFalse(anyString(), anyString())).thenReturn(false);
    when(mapper.toEntity(any(CreateMenuCategoryRequest.class))).thenReturn(category);
    when(repository.save(any(MenuCategory.class))).thenReturn(category);
    when(mapper.toResponse(any(MenuCategory.class))).thenReturn(categoryResponse);
    when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(null);

    // Act
    MenuCategoryResponse response = service.create(createRequest);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getName()).isEqualTo("Main Course");
    assertThat(response.getRestaurantId()).isEqualTo("rest123");
    
    verify(repository).existsByRestaurantIdAndNameAndDeletedFalse("rest123", "Main Course");
    verify(repository).save(any(MenuCategory.class));
    verify(kafkaTemplate).send(eq("menu.category.created"), eq("cat123"), any());
  }

  @Test
  @DisplayName("Should throw exception when creating duplicate category")
  void shouldThrowException_whenCreatingDuplicateCategory() {
    // Arrange
    when(repository.existsByRestaurantIdAndNameAndDeletedFalse(anyString(), anyString())).thenReturn(true);

    // Act & Assert
    assertThatThrownBy(() -> service.create(createRequest))
        .isInstanceOf(DuplicateMenuCategoryException.class)
        .hasMessageContaining("Main Course")
        .hasMessageContaining("rest123");

    verify(repository, never()).save(any(MenuCategory.class));
  }

  @Test
  @DisplayName("Should find category by ID")
  void shouldFindCategoryById_whenExists() {
    // Arrange
    when(repository.findByIdAndDeletedFalse("cat123")).thenReturn(Optional.of(category));
    when(mapper.toResponse(any(MenuCategory.class))).thenReturn(categoryResponse);

    // Act
    MenuCategoryResponse response = service.findById("cat123");

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo("cat123");
    verify(repository).findByIdAndDeletedFalse("cat123");
  }

  @Test
  @DisplayName("Should throw exception when category not found")
  void shouldThrowException_whenCategoryNotFound() {
    // Arrange
    when(repository.findByIdAndDeletedFalse("nonexistent")).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> service.findById("nonexistent"))
        .isInstanceOf(MenuCategoryNotFoundException.class)
        .hasMessageContaining("nonexistent");
  }

  @Test
  @DisplayName("Should find categories by restaurant")
  void shouldFindCategoriesByRestaurant() {
    // Arrange
    when(repository.findByRestaurantIdAndDeletedFalse(anyString(), any(Sort.class)))
        .thenReturn(List.of(category));
    when(mapper.toResponseList(anyList())).thenReturn(List.of(categoryResponse));

    // Act
    List<MenuCategoryResponse> result = service.findByRestaurant("rest123");

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    verify(repository).findByRestaurantIdAndDeletedFalse(eq("rest123"), any(Sort.class));
  }

  @Test
  @DisplayName("Should find active categories by restaurant")
  void shouldFindActiveCategoriesByRestaurant() {
    // Arrange
    when(repository.findByRestaurantIdAndActiveTrueAndDeletedFalse(anyString(), any(Sort.class)))
        .thenReturn(List.of(category));
    when(mapper.toResponseList(anyList())).thenReturn(List.of(categoryResponse));

    // Act
    List<MenuCategoryResponse> result = service.findActiveByRestaurant("rest123");

    // Assert
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    verify(repository).findByRestaurantIdAndActiveTrueAndDeletedFalse(eq("rest123"), any(Sort.class));
  }

  @Test
  @DisplayName("Should update category successfully")
  void shouldUpdateCategory_whenValidInput() {
    // Arrange
    when(repository.findByIdAndDeletedFalse("cat123")).thenReturn(Optional.of(category));
    when(repository.save(any(MenuCategory.class))).thenReturn(category);
    when(mapper.toResponse(any(MenuCategory.class))).thenReturn(categoryResponse);
    when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(null);

    // Act
    MenuCategoryResponse response = service.update("cat123", createRequest);

    // Assert
    assertThat(response).isNotNull();
    verify(repository).findByIdAndDeletedFalse("cat123");
    verify(repository).save(category);
    verify(kafkaTemplate).send(eq("menu.category.updated"), eq("cat123"), any());
  }

  @Test
  @DisplayName("Should delete category (soft delete)")
  void shouldDeleteCategory() {
    // Arrange
    when(repository.findByIdAndDeletedFalse("cat123")).thenReturn(Optional.of(category));
    when(repository.save(any(MenuCategory.class))).thenReturn(category);
    when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(null);

    // Act
    service.delete("cat123");

    // Assert
    verify(repository).findByIdAndDeletedFalse("cat123");
    verify(repository).save(category);
    verify(kafkaTemplate).send(eq("menu.category.deleted"), eq("cat123"), isNull());
  }
}

