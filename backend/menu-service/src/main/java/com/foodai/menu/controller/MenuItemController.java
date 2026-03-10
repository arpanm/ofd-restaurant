package com.foodai.menu.controller;

import com.foodai.menu.dto.request.CreateMenuItemRequest;
import com.foodai.menu.dto.request.MenuItemSearchRequest;
import com.foodai.menu.dto.request.UpdateMenuItemRequest;
import com.foodai.menu.dto.response.MenuItemDetailResponse;
import com.foodai.menu.dto.response.MenuItemResponse;
import com.foodai.menu.service.MenuItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for Menu Item operations.
 *
 * @author FoodAI Team
 */
@RestController
@RequestMapping("/api/v1/menu-items")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Menu Items", description = "Menu item management APIs")
public class MenuItemController {

  private final MenuItemService service;

  /**
   * Creates a new menu item.
   *
   * @param request the create request
   * @return the created menu item
   */
  @PostMapping
  @Operation(summary = "Create a new menu item", 
      description = "Creates a new menu item with all details including customizations and nutritional info")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Menu item created successfully",
          content = @Content(schema = @Schema(implementation = MenuItemResponse.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input"),
      @ApiResponse(responseCode = "409", description = "Duplicate menu item")
  })
  public ResponseEntity<MenuItemResponse> create(@Valid @RequestBody CreateMenuItemRequest request) {
    log.info("Received request to create menu item: {}", request.getName());
    MenuItemResponse response = service.create(request);
    
    URI location = ServletUriComponentsBuilder
        .fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(response.getId())
        .toUri();
    
    return ResponseEntity.created(location).body(response);
  }

  /**
   * Retrieves a menu item by its ID.
   *
   * @param id the menu item ID
   * @return the menu item details
   */
  @GetMapping("/{id}")
  @Operation(summary = "Get menu item by ID", description = "Retrieves detailed information about a specific menu item")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Menu item found",
          content = @Content(schema = @Schema(implementation = MenuItemDetailResponse.class))),
      @ApiResponse(responseCode = "404", description = "Menu item not found")
  })
  public ResponseEntity<MenuItemDetailResponse> getById(
      @Parameter(description = "Menu item ID") @PathVariable String id) {
    log.debug("Fetching menu item with id: {}", id);
    MenuItemDetailResponse response = service.findById(id);
    return ResponseEntity.ok(response);
  }

  /**
   * Retrieves all menu items with pagination.
   *
   * @param page page number (0-indexed)
   * @param size page size
   * @param sortBy field to sort by
   * @param sortDirection sort direction (asc/desc)
   * @return page of menu items
   */
  @GetMapping
  @Operation(summary = "Get all menu items", description = "Retrieves all menu items with pagination and sorting")
  @ApiResponse(responseCode = "200", description = "Success")
  public ResponseEntity<Page<MenuItemResponse>> getAll(
      @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
      @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
      @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDirection) {
    
    Sort.Direction direction = Sort.Direction.fromString(sortDirection);
    Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
    
    Page<MenuItemResponse> response = service.findAll(pageable);
    return ResponseEntity.ok(response);
  }

  /**
   * Retrieves menu items for a specific restaurant.
   *
   * @param restaurantId the restaurant ID
   * @param page page number
   * @param size page size
   * @return page of menu items
   */
  @GetMapping("/restaurant/{restaurantId}")
  @Operation(summary = "Get menu items by restaurant", 
      description = "Retrieves all menu items for a specific restaurant")
  @ApiResponse(responseCode = "200", description = "Success")
  public ResponseEntity<Page<MenuItemResponse>> getByRestaurant(
      @Parameter(description = "Restaurant ID") @PathVariable String restaurantId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
    Page<MenuItemResponse> response = service.findByRestaurant(restaurantId, pageable);
    return ResponseEntity.ok(response);
  }

  /**
   * Searches menu items with filters.
   *
   * @param request the search request
   * @return page of matching menu items
   */
  @PostMapping("/search")
  @Operation(summary = "Search menu items", 
      description = "Searches menu items with various filters like category, dietary preferences, price range, etc.")
  @ApiResponse(responseCode = "200", description = "Success")
  public ResponseEntity<Page<MenuItemResponse>> search(@Valid @RequestBody MenuItemSearchRequest request) {
    log.debug("Searching menu items with filters: {}", request);
    Page<MenuItemResponse> response = service.search(request);
    return ResponseEntity.ok(response);
  }

  /**
   * Updates an existing menu item.
   *
   * @param id the menu item ID
   * @param request the update request
   * @return the updated menu item
   */
  @PutMapping("/{id}")
  @Operation(summary = "Update menu item", description = "Updates an existing menu item")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Menu item updated successfully"),
      @ApiResponse(responseCode = "404", description = "Menu item not found"),
      @ApiResponse(responseCode = "400", description = "Invalid input")
  })
  public ResponseEntity<MenuItemResponse> update(
      @Parameter(description = "Menu item ID") @PathVariable String id,
      @Valid @RequestBody UpdateMenuItemRequest request) {
    log.info("Updating menu item: {}", id);
    MenuItemResponse response = service.update(id, request);
    return ResponseEntity.ok(response);
  }

  /**
   * Updates availability status of a menu item.
   *
   * @param id the menu item ID
   * @param available the new availability status
   * @return the updated menu item
   */
  @PatchMapping("/{id}/availability")
  @Operation(summary = "Update availability", description = "Updates the availability status of a menu item")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Availability updated successfully"),
      @ApiResponse(responseCode = "404", description = "Menu item not found")
  })
  public ResponseEntity<MenuItemResponse> updateAvailability(
      @Parameter(description = "Menu item ID") @PathVariable String id,
      @Parameter(description = "Availability status") @RequestParam boolean available) {
    log.info("Updating availability for menu item: {} to {}", id, available);
    MenuItemResponse response = service.updateAvailability(id, available);
    return ResponseEntity.ok(response);
  }

  /**
   * Deletes a menu item (soft delete).
   *
   * @param id the menu item ID
   * @return no content
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete menu item", description = "Soft deletes a menu item")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Menu item deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Menu item not found")
  })
  public ResponseEntity<Void> delete(@Parameter(description = "Menu item ID") @PathVariable String id) {
    log.info("Deleting menu item: {}", id);
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Retrieves top-selling menu items for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @param limit maximum number of items
   * @return list of top-selling items
   */
  @GetMapping("/restaurant/{restaurantId}/top-selling")
  @Operation(summary = "Get top-selling items", 
      description = "Retrieves the most popular menu items based on order count")
  @ApiResponse(responseCode = "200", description = "Success")
  public ResponseEntity<List<MenuItemResponse>> getTopSelling(
      @Parameter(description = "Restaurant ID") @PathVariable String restaurantId,
      @Parameter(description = "Maximum number of items") @RequestParam(defaultValue = "10") int limit) {
    log.debug("Fetching top {} selling items for restaurant: {}", limit, restaurantId);
    List<MenuItemResponse> response = service.findTopSelling(restaurantId, limit);
    return ResponseEntity.ok(response);
  }

  /**
   * Retrieves highly-rated menu items.
   *
   * @param restaurantId the restaurant ID
   * @param minRating minimum rating threshold
   * @param page page number
   * @param size page size
   * @return page of highly-rated items
   */
  @GetMapping("/restaurant/{restaurantId}/highly-rated")
  @Operation(summary = "Get highly-rated items", 
      description = "Retrieves menu items with rating above a threshold")
  @ApiResponse(responseCode = "200", description = "Success")
  public ResponseEntity<List<MenuItemResponse>> getHighlyRated(
      @Parameter(description = "Restaurant ID") @PathVariable String restaurantId,
      @Parameter(description = "Minimum rating") @RequestParam(defaultValue = "4.0") double minRating,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "averageRating"));
    Page<MenuItemResponse> pagedResponse = service.findHighlyRated(restaurantId, minRating, pageable);
    return ResponseEntity.ok(pagedResponse.getContent());
  }

  /**
   * Records an order for a menu item.
   *
   * @param id the menu item ID
   * @return no content
   */
  @PostMapping("/{id}/record-order")
  @Operation(summary = "Record order", description = "Increments the order count for a menu item")
  @ApiResponse(responseCode = "204", description = "Order recorded successfully")
  public ResponseEntity<Void> recordOrder(@Parameter(description = "Menu item ID") @PathVariable String id) {
    log.debug("Recording order for menu item: {}", id);
    service.recordOrder(id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Updates rating for a menu item.
   *
   * @param id the menu item ID
   * @param rating the new rating
   * @return no content
   */
  @PatchMapping("/{id}/rating")
  @Operation(summary = "Update rating", description = "Updates the rating for a menu item")
  @ApiResponse(responseCode = "204", description = "Rating updated successfully")
  public ResponseEntity<Void> updateRating(
      @Parameter(description = "Menu item ID") @PathVariable String id,
      @Parameter(description = "Rating value") @RequestParam BigDecimal rating) {
    log.debug("Updating rating for menu item: {} to {}", id, rating);
    service.updateRating(id, rating);
    return ResponseEntity.noContent().build();
  }
}

