package com.foodai.promotion.controller;

import com.foodai.promotion.domain.model.PromotionStatus;
import com.foodai.promotion.dto.request.CreatePromotionRequest;
import com.foodai.promotion.dto.response.ApiResponse;
import com.foodai.promotion.dto.response.PromotionResponse;
import com.foodai.promotion.service.PromotionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing promotions.
 *
 * @author FoodAI Team
 */
@RestController
@RequestMapping("/api/v1/promotions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Promotions", description = "Promotion management endpoints")
public class PromotionController {

  private final PromotionService promotionService;

  /**
   * Creates a new promotion.
   *
   * @param request the create request
   * @return the created promotion
   */
  @PostMapping
  @Operation(summary = "Create a new promotion")
  public ResponseEntity<ApiResponse<PromotionResponse>> createPromotion(
      @Valid @RequestBody CreatePromotionRequest request) {
    log.info("REST request to create promotion: {}", request.getName());
    PromotionResponse response = promotionService.createPromotion(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(response, "Promotion created successfully"));
  }

  /**
   * Gets a promotion by ID.
   *
   * @param id the promotion ID
   * @return the promotion
   */
  @GetMapping("/{id}")
  @Operation(summary = "Get promotion by ID")
  public ResponseEntity<ApiResponse<PromotionResponse>> getPromotion(
      @Parameter(description = "Promotion ID") @PathVariable String id) {
    log.debug("REST request to get promotion: {}", id);
    PromotionResponse response = promotionService.getPromotion(id);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Gets a promotion by code.
   *
   * @param code the promotion code
   * @return the promotion
   */
  @GetMapping("/code/{code}")
  @Operation(summary = "Get promotion by code")
  public ResponseEntity<ApiResponse<PromotionResponse>> getPromotionByCode(
      @Parameter(description = "Promotion code") @PathVariable String code) {
    log.debug("REST request to get promotion by code: {}", code);
    PromotionResponse response = promotionService.getPromotionByCode(code);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Gets all promotions.
   *
   * @param pageable pagination info
   * @return page of promotions
   */
  @GetMapping
  @Operation(summary = "Get all promotions")
  public ResponseEntity<ApiResponse<Page<PromotionResponse>>> getAllPromotions(
      @PageableDefault(size = 20) Pageable pageable) {
    log.debug("REST request to get all promotions");
    Page<PromotionResponse> page = promotionService.getAllPromotions(pageable);
    return ResponseEntity.ok(ApiResponse.success(page));
  }

  /**
   * Gets promotions by restaurant.
   *
   * @param restaurantId the restaurant ID
   * @param pageable pagination info
   * @return page of promotions
   */
  @GetMapping("/restaurant/{restaurantId}")
  @Operation(summary = "Get promotions by restaurant")
  public ResponseEntity<ApiResponse<Page<PromotionResponse>>> getPromotionsByRestaurant(
      @Parameter(description = "Restaurant ID") @PathVariable String restaurantId,
      @PageableDefault(size = 20) Pageable pageable) {
    log.debug("REST request to get promotions for restaurant: {}", restaurantId);
    Page<PromotionResponse> page =
        promotionService.getPromotionsByRestaurant(restaurantId, pageable);
    return ResponseEntity.ok(ApiResponse.success(page));
  }

  /**
   * Gets promotions by status.
   *
   * @param status the status
   * @param pageable pagination info
   * @return page of promotions
   */
  @GetMapping("/status/{status}")
  @Operation(summary = "Get promotions by status")
  public ResponseEntity<ApiResponse<Page<PromotionResponse>>> getPromotionsByStatus(
      @Parameter(description = "Promotion status") @PathVariable PromotionStatus status,
      @PageableDefault(size = 20) Pageable pageable) {
    log.debug("REST request to get promotions by status: {}", status);
    Page<PromotionResponse> page = promotionService.getPromotionsByStatus(status, pageable);
    return ResponseEntity.ok(ApiResponse.success(page));
  }

  /**
   * Gets promotions by restaurant and status.
   *
   * @param restaurantId the restaurant ID
   * @param status the status
   * @param pageable pagination info
   * @return page of promotions
   */
  @GetMapping("/restaurant/{restaurantId}/status/{status}")
  @Operation(summary = "Get promotions by restaurant and status")
  public ResponseEntity<ApiResponse<Page<PromotionResponse>>> getPromotionsByRestaurantAndStatus(
      @Parameter(description = "Restaurant ID") @PathVariable String restaurantId,
      @Parameter(description = "Promotion status") @PathVariable PromotionStatus status,
      @PageableDefault(size = 20) Pageable pageable) {
    log.debug("REST request to get promotions for restaurant {} with status {}", restaurantId, status);
    Page<PromotionResponse> page =
        promotionService.getPromotionsByRestaurantAndStatus(restaurantId, status, pageable);
    return ResponseEntity.ok(ApiResponse.success(page));
  }

  /**
   * Gets active promotions for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return list of active promotions
   */
  @GetMapping("/restaurant/{restaurantId}/active")
  @Operation(summary = "Get active promotions for restaurant")
  public ResponseEntity<ApiResponse<List<PromotionResponse>>> getActivePromotions(
      @Parameter(description = "Restaurant ID") @PathVariable String restaurantId) {
    log.debug("REST request to get active promotions for restaurant: {}", restaurantId);
    List<PromotionResponse> promotions =
        promotionService.getActivePromotionsForRestaurant(restaurantId);
    return ResponseEntity.ok(ApiResponse.success(promotions));
  }

  /**
   * Activates a promotion.
   *
   * @param id the promotion ID
   * @return the updated promotion
   */
  @PutMapping("/{id}/activate")
  @Operation(summary = "Activate a promotion")
  public ResponseEntity<ApiResponse<PromotionResponse>> activatePromotion(
      @Parameter(description = "Promotion ID") @PathVariable String id) {
    log.info("REST request to activate promotion: {}", id);
    PromotionResponse response = promotionService.activatePromotion(id);
    return ResponseEntity.ok(ApiResponse.success(response, "Promotion activated"));
  }

  /**
   * Pauses a promotion.
   *
   * @param id the promotion ID
   * @return the updated promotion
   */
  @PutMapping("/{id}/pause")
  @Operation(summary = "Pause a promotion")
  public ResponseEntity<ApiResponse<PromotionResponse>> pausePromotion(
      @Parameter(description = "Promotion ID") @PathVariable String id) {
    log.info("REST request to pause promotion: {}", id);
    PromotionResponse response = promotionService.pausePromotion(id);
    return ResponseEntity.ok(ApiResponse.success(response, "Promotion paused"));
  }

  /**
   * Resumes a paused promotion.
   *
   * @param id the promotion ID
   * @return the updated promotion
   */
  @PutMapping("/{id}/resume")
  @Operation(summary = "Resume a paused promotion")
  public ResponseEntity<ApiResponse<PromotionResponse>> resumePromotion(
      @Parameter(description = "Promotion ID") @PathVariable String id) {
    log.info("REST request to resume promotion: {}", id);
    PromotionResponse response = promotionService.resumePromotion(id);
    return ResponseEntity.ok(ApiResponse.success(response, "Promotion resumed"));
  }

  /**
   * Ends a promotion.
   *
   * @param id the promotion ID
   * @return the updated promotion
   */
  @PutMapping("/{id}/end")
  @Operation(summary = "End a promotion")
  public ResponseEntity<ApiResponse<PromotionResponse>> endPromotion(
      @Parameter(description = "Promotion ID") @PathVariable String id) {
    log.info("REST request to end promotion: {}", id);
    PromotionResponse response = promotionService.endPromotion(id);
    return ResponseEntity.ok(ApiResponse.success(response, "Promotion ended"));
  }

  /**
   * Deletes a promotion.
   *
   * @param id the promotion ID
   * @return no content
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a promotion")
  public ResponseEntity<ApiResponse<Void>> deletePromotion(
      @Parameter(description = "Promotion ID") @PathVariable String id) {
    log.info("REST request to delete promotion: {}", id);
    promotionService.deletePromotion(id);
    return ResponseEntity.ok(ApiResponse.success(null, "Promotion deleted"));
  }
}

