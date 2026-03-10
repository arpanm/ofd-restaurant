package com.foodai.menu.service;

import com.foodai.menu.domain.model.MenuItem;
import com.foodai.menu.domain.model.MenuItemStatus;
import com.foodai.menu.domain.repository.MenuItemRepository;
import com.foodai.menu.dto.request.CreateMenuItemRequest;
import com.foodai.menu.dto.request.MenuItemSearchRequest;
import com.foodai.menu.dto.request.UpdateMenuItemRequest;
import com.foodai.menu.dto.response.MenuItemDetailResponse;
import com.foodai.menu.dto.response.MenuItemResponse;
import com.foodai.menu.exception.DuplicateMenuItemException;
import com.foodai.menu.exception.MenuItemNotFoundException;
import com.foodai.menu.mapper.MenuItemMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 * Application service for MenuItem operations.
 *
 * <p>Orchestrates domain operations and handles transactions.
 *
 * @author FoodAI Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MenuItemService {

  private final MenuItemRepository repository;
  private final MenuItemMapper mapper;
  private final KafkaTemplate<String, Object> kafkaTemplate;

  /**
   * Creates a new menu item.
   *
   * @param request the create request
   * @return the created menu item response
   * @throws DuplicateMenuItemException if item with same name exists
   */
  @Transactional
  @CacheEvict(value = "menuItems", key = "#request.restaurantId")
  public MenuItemResponse create(CreateMenuItemRequest request) {
    log.info("Creating new menu item", 
        kv("restaurantId", request.getRestaurantId()),
        kv("name", request.getName()));

    // Check for duplicates
    if (repository.existsByRestaurantIdAndNameAndDeletedFalse(
        request.getRestaurantId(), request.getName())) {
      throw new DuplicateMenuItemException(request.getRestaurantId(), request.getName());
    }

    MenuItem entity = mapper.toEntity(request);
    entity.validate();
    entity.initializeDefaults();

    MenuItem saved = repository.save(entity);

    log.info("Menu item created successfully", kv("id", saved.getId()));

    // Publish event
    kafkaTemplate.send("menu.item.created", saved.getId(), saved);

    return mapper.toResponse(saved);
  }

  /**
   * Finds a menu item by its ID.
   *
   * @param id the menu item ID
   * @return the menu item detail response
   * @throws MenuItemNotFoundException if not found
   */
  @Cacheable(value = "menuItem", key = "#id")
  public MenuItemDetailResponse findById(String id) {
    log.debug("Finding menu item by id", kv("id", id));

    MenuItem entity = repository.findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new MenuItemNotFoundException(id));

    return mapper.toDetailResponse(entity);
  }

  /**
   * Finds all menu items with pagination.
   *
   * @param pageable pagination information
   * @return page of menu item responses
   */
  public Page<MenuItemResponse> findAll(Pageable pageable) {
    log.debug("Finding all menu items",
        kv("page", pageable.getPageNumber()),
        kv("size", pageable.getPageSize()));

    return repository.findByDeletedFalse(pageable)
        .map(mapper::toResponse);
  }

  /**
   * Finds menu items by restaurant.
   *
   * @param restaurantId the restaurant ID
   * @param pageable pagination information
   * @return page of menu items
   */
  @Cacheable(value = "menuItems", key = "#restaurantId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
  public Page<MenuItemResponse> findByRestaurant(String restaurantId, Pageable pageable) {
    log.debug("Finding menu items by restaurant",
        kv("restaurantId", restaurantId),
        kv("page", pageable.getPageNumber()));

    return repository.findByRestaurantIdAndDeletedFalse(restaurantId, pageable)
        .map(mapper::toResponse);
  }

  /**
   * Searches menu items with filters.
   *
   * @param request the search request
   * @return page of matching menu items
   */
  public Page<MenuItemResponse> search(MenuItemSearchRequest request) {
    log.debug("Searching menu items", kv("request", request));

    String restaurantId = request.getRestaurantId();
    int page = request.getPage() != null ? request.getPage() : 0;
    int size = request.getSize() != null ? request.getSize() : 20;
    
    String sortBy = request.getSortBy() != null ? request.getSortBy() : "createdAt";
    String sortDirection = request.getSortDirection() != null ? request.getSortDirection() : "desc";
    Sort.Direction direction = Sort.Direction.fromString(sortDirection);
    
    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

    // Get all items for the restaurant first
    List<MenuItem> allItems = repository.findByRestaurantIdAndDeletedFalse(
        restaurantId, 
        PageRequest.of(0, Integer.MAX_VALUE)
    ).getContent();

    // Apply filters in application layer
    List<MenuItem> filtered = allItems.stream()
        .filter(item -> matchesFilters(item, request))
        .toList();

    // Apply sorting
    filtered = applySorting(filtered, sortBy, direction);

    // Apply pagination
    int start = Math.min(page * size, filtered.size());
    int end = Math.min(start + size, filtered.size());
    List<MenuItem> paginatedItems = filtered.subList(start, end);

    // Convert to responses
    List<MenuItemResponse> responses = mapper.toResponseList(paginatedItems);
    
    return new PageImpl<>(responses, pageable, filtered.size());
  }

  private boolean matchesFilters(MenuItem item, MenuItemSearchRequest request) {
    // Category filter
    if (request.getCategory() != null && !request.getCategory().isBlank()) {
      if (!request.getCategory().equalsIgnoreCase(item.getCategory())) {
        return false;
      }
    }

    // Price range filter
    if (request.getMinPrice() != null) {
      if (item.getBasePrice().compareTo(request.getMinPrice()) < 0) {
        return false;
      }
    }
    if (request.getMaxPrice() != null) {
      if (item.getBasePrice().compareTo(request.getMaxPrice()) > 0) {
        return false;
      }
    }

    // Dietary filters
    if (Boolean.TRUE.equals(request.getVegetarian()) && !Boolean.TRUE.equals(item.isVegetarian())) {
      return false;
    }
    if (Boolean.TRUE.equals(request.getVegan()) && !Boolean.TRUE.equals(item.isVegan())) {
      return false;
    }
    if (Boolean.TRUE.equals(request.getGlutenFree()) && !Boolean.TRUE.equals(item.isGlutenFree())) {
      return false;
    }

    // Availability filter
    if (Boolean.TRUE.equals(request.getAvailable()) && !Boolean.TRUE.equals(item.isAvailable())) {
      return false;
    }

    // Spice level filter
    if (request.getMinSpiceLevel() != null && item.getSpiceLevel() != null) {
      if (item.getSpiceLevel() < request.getMinSpiceLevel()) {
        return false;
      }
    }
    if (request.getMaxSpiceLevel() != null && item.getSpiceLevel() != null) {
      if (item.getSpiceLevel() > request.getMaxSpiceLevel()) {
        return false;
      }
    }

    // Preparation time filter
    if (request.getMaxPrepTime() != null && item.getPreparationTime() != null) {
      if (item.getPreparationTime() > request.getMaxPrepTime()) {
        return false;
      }
    }

    // Rating filter
    if (request.getMinRating() != null && item.getAverageRating() != null) {
      if (item.getAverageRating().compareTo(request.getMinRating()) < 0) {
        return false;
      }
    }

    // Tags filter
    if (request.getTags() != null && !request.getTags().isEmpty()) {
      if (item.getTags() == null || item.getTags().stream().noneMatch(request.getTags()::contains)) {
        return false;
      }
    }

    // Search text filter (name or description)
    if (request.getSearchText() != null && !request.getSearchText().isBlank()) {
      String searchLower = request.getSearchText().toLowerCase();
      boolean matchesName = item.getName() != null && 
          item.getName().toLowerCase().contains(searchLower);
      boolean matchesDesc = item.getDescription() != null && 
          item.getDescription().toLowerCase().contains(searchLower);
      if (!matchesName && !matchesDesc) {
        return false;
      }
    }

    return true;
  }

  private List<MenuItem> applySorting(List<MenuItem> items, String sortBy, Sort.Direction direction) {
    return items.stream()
        .sorted((a, b) -> {
          int comparison = 0;
          switch (sortBy) {
            case "name":
              comparison = a.getName().compareTo(b.getName());
              break;
            case "basePrice":
              comparison = a.getBasePrice().compareTo(b.getBasePrice());
              break;
            case "totalOrders":
              comparison = Integer.compare(a.getTotalOrders(), b.getTotalOrders());
              break;
            case "averageRating":
              comparison = a.getAverageRating().compareTo(b.getAverageRating());
              break;
            case "createdAt":
            default:
              if (a.getCreatedAt() != null && b.getCreatedAt() != null) {
                comparison = a.getCreatedAt().compareTo(b.getCreatedAt());
              }
              break;
          }
          return direction == Sort.Direction.ASC ? comparison : -comparison;
        })
        .toList();
  }

  /**
   * Updates an existing menu item.
   *
   * @param id the menu item ID
   * @param request the update request
   * @return the updated menu item response
   * @throws MenuItemNotFoundException if not found
   */
  @Transactional
  @CacheEvict(value = {"menuItem", "menuItems"}, allEntries = true)
  public MenuItemResponse update(String id, UpdateMenuItemRequest request) {
    log.info("Updating menu item", kv("id", id));

    MenuItem entity = repository.findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new MenuItemNotFoundException(id));

    mapper.updateEntityFromRequest(request, entity);
    entity.validate();

    MenuItem updated = repository.save(entity);

    log.info("Menu item updated successfully", kv("id", updated.getId()));

    // Publish event
    kafkaTemplate.send("menu.item.updated", updated.getId(), updated);

    return mapper.toResponse(updated);
  }

  /**
   * Updates availability status.
   *
   * @param id the menu item ID
   * @param available the new availability status
   * @return the updated menu item response
   * @throws MenuItemNotFoundException if not found
   */
  @Transactional
  @CacheEvict(value = {"menuItem", "menuItems"}, allEntries = true)
  public MenuItemResponse updateAvailability(String id, boolean available) {
    log.info("Updating menu item availability", kv("id", id), kv("available", available));

    MenuItem entity = repository.findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new MenuItemNotFoundException(id));

    entity.updateAvailability(available);
    MenuItem updated = repository.save(entity);

    log.info("Menu item availability updated", kv("id", id), kv("available", available));

    // Publish event
    kafkaTemplate.send("menu.item.availability.changed", updated.getId(), updated);

    return mapper.toResponse(updated);
  }

  /**
   * Deletes a menu item (soft delete).
   *
   * @param id the menu item ID
   * @throws MenuItemNotFoundException if not found
   */
  @Transactional
  @CacheEvict(value = {"menuItem", "menuItems"}, allEntries = true)
  public void delete(String id) {
    log.info("Deleting menu item", kv("id", id));

    MenuItem entity = repository.findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new MenuItemNotFoundException(id));

    String restaurantId = entity.getRestaurantId();
    entity.delete();
    repository.save(entity);

    log.info("Menu item deleted successfully", kv("id", id));

    // Publish event
    kafkaTemplate.send("menu.item.deleted", id, null);
  }

  /**
   * Finds top-selling menu items for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @param limit maximum number of items
   * @return list of top-selling items
   */
  @Cacheable(value = "topSellingItems", key = "#restaurantId + '_' + #limit")
  public List<MenuItemResponse> findTopSelling(String restaurantId, int limit) {
    log.debug("Finding top-selling items", kv("restaurantId", restaurantId), kv("limit", limit));

    Pageable pageable = PageRequest.of(0, limit);
    List<MenuItem> topItems = repository.findTopSellingItems(restaurantId, pageable);

    return mapper.toResponseList(topItems);
  }

  /**
   * Finds highly-rated menu items.
   *
   * @param restaurantId the restaurant ID
   * @param minRating minimum rating threshold
   * @param pageable pagination information
   * @return page of highly-rated items
   */
  public Page<MenuItemResponse> findHighlyRated(String restaurantId, double minRating, Pageable pageable) {
    log.debug("Finding highly-rated items",
        kv("restaurantId", restaurantId),
        kv("minRating", minRating));

    return repository.findHighlyRatedItems(restaurantId, minRating, pageable)
        .map(mapper::toResponse);
  }

  /**
   * Records an order for a menu item.
   *
   * @param id the menu item ID
   */
  @Transactional
  @CacheEvict(value = {"menuItem", "menuItems", "topSellingItems"}, allEntries = true)
  public void recordOrder(String id) {
    log.debug("Recording order for menu item", kv("id", id));

    MenuItem entity = repository.findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new MenuItemNotFoundException(id));

    entity.recordOrder();
    repository.save(entity);
  }

  /**
   * Updates rating for a menu item.
   *
   * @param id the menu item ID
   * @param rating the new rating
   */
  @Transactional
  @CacheEvict(value = {"menuItem", "menuItems"}, allEntries = true)
  public void updateRating(String id, BigDecimal rating) {
    log.debug("Updating menu item rating", kv("id", id), kv("rating", rating));

    MenuItem entity = repository.findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new MenuItemNotFoundException(id));

    entity.updateRating(rating);
    repository.save(entity);
  }

  /**
   * Counts menu items for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return count of menu items
   */
  public long countByRestaurant(String restaurantId) {
    return repository.countByRestaurantIdAndDeletedFalse(restaurantId);
  }

  /**
   * Checks if search request is a simple restaurant query.
   *
   * @param request the search request
   * @return true if simple query
   */
  private boolean isSimpleRestaurantQuery(MenuItemSearchRequest request) {
    return request.getCategory() == null &&
           request.getSearchText() == null &&
           request.getVegetarian() == null &&
           request.getVegan() == null &&
           request.getGlutenFree() == null &&
           request.getTags() == null &&
           request.getMinPrice() == null &&
           request.getMaxPrice() == null;
  }
}

