package com.foodai.menu.service;

import com.foodai.menu.domain.model.MenuCategory;
import com.foodai.menu.domain.repository.MenuCategoryRepository;
import com.foodai.menu.dto.request.CreateMenuCategoryRequest;
import com.foodai.menu.dto.response.MenuCategoryResponse;
import com.foodai.menu.exception.DuplicateMenuCategoryException;
import com.foodai.menu.exception.MenuCategoryNotFoundException;
import com.foodai.menu.mapper.MenuCategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 * Application service for MenuCategory operations.
 *
 * @author FoodAI Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MenuCategoryService {

  private final MenuCategoryRepository repository;
  private final MenuCategoryMapper mapper;
  private final KafkaTemplate<String, Object> kafkaTemplate;

  /**
   * Creates a new menu category.
   *
   * @param request the create request
   * @return the created category response
   * @throws DuplicateMenuCategoryException if category with same name exists
   */
  @Transactional
  @CacheEvict(value = "menuCategories", key = "#request.restaurantId")
  public MenuCategoryResponse create(CreateMenuCategoryRequest request) {
    log.info("Creating new menu category",
        kv("restaurantId", request.getRestaurantId()),
        kv("name", request.getName()));

    // Check for duplicates
    if (repository.existsByRestaurantIdAndNameAndDeletedFalse(
        request.getRestaurantId(), request.getName())) {
      throw new DuplicateMenuCategoryException(request.getRestaurantId(), request.getName());
    }

    MenuCategory entity = mapper.toEntity(request);
    entity.validate();

    MenuCategory saved = repository.save(entity);

    log.info("Menu category created successfully", kv("id", saved.getId()));

    // Publish event
    kafkaTemplate.send("menu.category.created", saved.getId(), saved);

    return mapper.toResponse(saved);
  }

  /**
   * Finds a category by its ID.
   *
   * @param id the category ID
   * @return the category response
   * @throws MenuCategoryNotFoundException if not found
   */
  @Cacheable(value = "menuCategory", key = "#id")
  public MenuCategoryResponse findById(String id) {
    log.debug("Finding menu category by id", kv("id", id));

    MenuCategory entity = repository.findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new MenuCategoryNotFoundException(id));

    return mapper.toResponse(entity);
  }

  /**
   * Finds all categories for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return list of categories
   */
  @Cacheable(value = "menuCategories", key = "#restaurantId")
  public List<MenuCategoryResponse> findByRestaurant(String restaurantId) {
    log.debug("Finding categories by restaurant", kv("restaurantId", restaurantId));

    Sort sort = Sort.by(Sort.Direction.ASC, "displayOrder", "name");
    List<MenuCategory> categories = repository.findByRestaurantIdAndDeletedFalse(restaurantId, sort);

    return mapper.toResponseList(categories);
  }

  /**
   * Finds active categories for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return list of active categories
   */
  @Cacheable(value = "activeCategories", key = "#restaurantId")
  public List<MenuCategoryResponse> findActiveByRestaurant(String restaurantId) {
    log.debug("Finding active categories", kv("restaurantId", restaurantId));

    Sort sort = Sort.by(Sort.Direction.ASC, "displayOrder", "name");
    List<MenuCategory> categories = repository.findByRestaurantIdAndActiveTrueAndDeletedFalse(
        restaurantId, sort);

    return mapper.toResponseList(categories);
  }

  /**
   * Updates a category.
   *
   * @param id the category ID
   * @param request the update request
   * @return the updated category response
   * @throws MenuCategoryNotFoundException if not found
   */
  @Transactional
  @CacheEvict(value = {"menuCategory", "menuCategories", "activeCategories"}, allEntries = true)
  public MenuCategoryResponse update(String id, CreateMenuCategoryRequest request) {
    log.info("Updating menu category", kv("id", id));

    MenuCategory entity = repository.findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new MenuCategoryNotFoundException(id));

    // Update fields
    entity.setName(request.getName());
    entity.setDescription(request.getDescription());
    entity.setIconUrl(request.getIconUrl());
    entity.setDisplayOrder(request.getDisplayOrder());
    entity.setActive(request.isActive());
    entity.validate();

    MenuCategory updated = repository.save(entity);

    log.info("Menu category updated successfully", kv("id", updated.getId()));

    // Publish event
    kafkaTemplate.send("menu.category.updated", updated.getId(), updated);

    return mapper.toResponse(updated);
  }

  /**
   * Deletes a category (soft delete).
   *
   * @param id the category ID
   * @throws MenuCategoryNotFoundException if not found
   */
  @Transactional
  @CacheEvict(value = {"menuCategory", "menuCategories", "activeCategories"}, allEntries = true)
  public void delete(String id) {
    log.info("Deleting menu category", kv("id", id));

    MenuCategory entity = repository.findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new MenuCategoryNotFoundException(id));

    entity.delete();
    repository.save(entity);

    log.info("Menu category deleted successfully", kv("id", id));

    // Publish event
    kafkaTemplate.send("menu.category.deleted", id, null);
  }
}

