package com.foodai.menu.controller;

import com.foodai.menu.dto.request.CreateMenuCategoryRequest;
import com.foodai.menu.dto.response.MenuCategoryResponse;
import com.foodai.menu.service.MenuCategoryService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Menu Category operations.
 *
 * @author FoodAI Team
 */
@RestController
@RequestMapping("/api/v1/menu-categories")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Menu Categories", description = "Menu category management APIs")
public class MenuCategoryController {

  private final MenuCategoryService service;

  /**
   * Creates a new menu category.
   *
   * @param request the create request
   * @return the created category
   */
  @PostMapping
  @Operation(summary = "Create a new menu category", 
      description = "Creates a new category for organizing menu items")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Category created successfully",
          content = @Content(schema = @Schema(implementation = MenuCategoryResponse.class))),
      @ApiResponse(responseCode = "400", description = "Invalid input"),
      @ApiResponse(responseCode = "409", description = "Duplicate category")
  })
  public ResponseEntity<MenuCategoryResponse> create(@Valid @RequestBody CreateMenuCategoryRequest request) {
    log.info("Received request to create menu category: {}", request.getName());
    MenuCategoryResponse response = service.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Retrieves a category by its ID.
   *
   * @param id the category ID
   * @return the category details
   */
  @GetMapping("/{id}")
  @Operation(summary = "Get category by ID", description = "Retrieves a specific menu category")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Category found",
          content = @Content(schema = @Schema(implementation = MenuCategoryResponse.class))),
      @ApiResponse(responseCode = "404", description = "Category not found")
  })
  public ResponseEntity<MenuCategoryResponse> getById(
      @Parameter(description = "Category ID") @PathVariable String id) {
    log.debug("Fetching category with id: {}", id);
    MenuCategoryResponse response = service.findById(id);
    return ResponseEntity.ok(response);
  }

  /**
   * Retrieves all categories for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return list of categories
   */
  @GetMapping("/restaurant/{restaurantId}")
  @Operation(summary = "Get categories by restaurant", 
      description = "Retrieves all categories for a specific restaurant")
  @ApiResponse(responseCode = "200", description = "Success")
  public ResponseEntity<List<MenuCategoryResponse>> getByRestaurant(
      @Parameter(description = "Restaurant ID") @PathVariable String restaurantId) {
    log.debug("Fetching categories for restaurant: {}", restaurantId);
    List<MenuCategoryResponse> response = service.findByRestaurant(restaurantId);
    return ResponseEntity.ok(response);
  }

  /**
   * Retrieves active categories for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return list of active categories
   */
  @GetMapping("/restaurant/{restaurantId}/active")
  @Operation(summary = "Get active categories", 
      description = "Retrieves only active categories for a restaurant")
  @ApiResponse(responseCode = "200", description = "Success")
  public ResponseEntity<List<MenuCategoryResponse>> getActiveByRestaurant(
      @Parameter(description = "Restaurant ID") @PathVariable String restaurantId) {
    log.debug("Fetching active categories for restaurant: {}", restaurantId);
    List<MenuCategoryResponse> response = service.findActiveByRestaurant(restaurantId);
    return ResponseEntity.ok(response);
  }

  /**
   * Updates an existing category.
   *
   * @param id the category ID
   * @param request the update request
   * @return the updated category
   */
  @PutMapping("/{id}")
  @Operation(summary = "Update category", description = "Updates an existing menu category")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Category updated successfully"),
      @ApiResponse(responseCode = "404", description = "Category not found"),
      @ApiResponse(responseCode = "400", description = "Invalid input")
  })
  public ResponseEntity<MenuCategoryResponse> update(
      @Parameter(description = "Category ID") @PathVariable String id,
      @Valid @RequestBody CreateMenuCategoryRequest request) {
    log.info("Updating category: {}", id);
    MenuCategoryResponse response = service.update(id, request);
    return ResponseEntity.ok(response);
  }

  /**
   * Deletes a category (soft delete).
   *
   * @param id the category ID
   * @return no content
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete category", description = "Soft deletes a menu category")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
      @ApiResponse(responseCode = "404", description = "Category not found")
  })
  public ResponseEntity<Void> delete(@Parameter(description = "Category ID") @PathVariable String id) {
    log.info("Deleting category: {}", id);
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}

