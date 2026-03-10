package com.foodai.promotion.controller;

import com.foodai.promotion.dto.request.CreateSegmentRequest;
import com.foodai.promotion.dto.response.ApiResponse;
import com.foodai.promotion.dto.response.CustomerSegmentResponse;
import com.foodai.promotion.service.CustomerSegmentService;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing customer segments.
 *
 * @author FoodAI Team
 */
@RestController
@RequestMapping("/api/v1/segments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Segments", description = "Customer segment management endpoints")
public class CustomerSegmentController {

  private final CustomerSegmentService segmentService;

  /**
   * Creates a new customer segment.
   *
   * @param request the create request
   * @return the created segment
   */
  @PostMapping
  @Operation(summary = "Create a new customer segment")
  public ResponseEntity<ApiResponse<CustomerSegmentResponse>> createSegment(
      @Valid @RequestBody CreateSegmentRequest request) {
    log.info("REST request to create segment: {}", request.getName());
    CustomerSegmentResponse response = segmentService.createSegment(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(response, "Segment created successfully"));
  }

  /**
   * Gets a segment by ID.
   *
   * @param id the segment ID
   * @return the segment
   */
  @GetMapping("/{id}")
  @Operation(summary = "Get segment by ID")
  public ResponseEntity<ApiResponse<CustomerSegmentResponse>> getSegment(
      @Parameter(description = "Segment ID") @PathVariable String id) {
    log.debug("REST request to get segment: {}", id);
    CustomerSegmentResponse response = segmentService.getSegment(id);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Gets all segments.
   *
   * @param pageable pagination info
   * @return page of segments
   */
  @GetMapping
  @Operation(summary = "Get all segments")
  public ResponseEntity<ApiResponse<Page<CustomerSegmentResponse>>> getAllSegments(
      @PageableDefault(size = 20) Pageable pageable) {
    log.debug("REST request to get all segments");
    Page<CustomerSegmentResponse> page = segmentService.getAllSegments(pageable);
    return ResponseEntity.ok(ApiResponse.success(page));
  }

  /**
   * Gets segments by restaurant.
   *
   * @param restaurantId the restaurant ID
   * @param pageable pagination info
   * @return page of segments
   */
  @GetMapping("/restaurant/{restaurantId}")
  @Operation(summary = "Get segments by restaurant")
  public ResponseEntity<ApiResponse<Page<CustomerSegmentResponse>>> getSegmentsByRestaurant(
      @Parameter(description = "Restaurant ID") @PathVariable String restaurantId,
      @PageableDefault(size = 20) Pageable pageable) {
    log.debug("REST request to get segments for restaurant: {}", restaurantId);
    Page<CustomerSegmentResponse> page =
        segmentService.getSegmentsByRestaurant(restaurantId, pageable);
    return ResponseEntity.ok(ApiResponse.success(page));
  }

  /**
   * Gets active segments.
   *
   * @return list of active segments
   */
  @GetMapping("/active")
  @Operation(summary = "Get active segments")
  public ResponseEntity<ApiResponse<List<CustomerSegmentResponse>>> getActiveSegments() {
    log.debug("REST request to get active segments");
    List<CustomerSegmentResponse> segments = segmentService.getActiveSegments();
    return ResponseEntity.ok(ApiResponse.success(segments));
  }

  /**
   * Gets active segments for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return list of active segments
   */
  @GetMapping("/restaurant/{restaurantId}/active")
  @Operation(summary = "Get active segments for restaurant")
  public ResponseEntity<ApiResponse<List<CustomerSegmentResponse>>> getActiveSegmentsForRestaurant(
      @Parameter(description = "Restaurant ID") @PathVariable String restaurantId) {
    log.debug("REST request to get active segments for restaurant: {}", restaurantId);
    List<CustomerSegmentResponse> segments =
        segmentService.getActiveSegmentsForRestaurant(restaurantId);
    return ResponseEntity.ok(ApiResponse.success(segments));
  }

  /**
   * Updates a segment.
   *
   * @param id the segment ID
   * @param request the update request
   * @return the updated segment
   */
  @PutMapping("/{id}")
  @Operation(summary = "Update a segment")
  public ResponseEntity<ApiResponse<CustomerSegmentResponse>> updateSegment(
      @Parameter(description = "Segment ID") @PathVariable String id,
      @Valid @RequestBody CreateSegmentRequest request) {
    log.info("REST request to update segment: {}", id);
    CustomerSegmentResponse response = segmentService.updateSegment(id, request);
    return ResponseEntity.ok(ApiResponse.success(response, "Segment updated"));
  }

  /**
   * Activates a segment.
   *
   * @param id the segment ID
   * @return the updated segment
   */
  @PutMapping("/{id}/activate")
  @Operation(summary = "Activate a segment")
  public ResponseEntity<ApiResponse<CustomerSegmentResponse>> activateSegment(
      @Parameter(description = "Segment ID") @PathVariable String id) {
    log.info("REST request to activate segment: {}", id);
    CustomerSegmentResponse response = segmentService.activateSegment(id);
    return ResponseEntity.ok(ApiResponse.success(response, "Segment activated"));
  }

  /**
   * Deactivates a segment.
   *
   * @param id the segment ID
   * @return the updated segment
   */
  @PutMapping("/{id}/deactivate")
  @Operation(summary = "Deactivate a segment")
  public ResponseEntity<ApiResponse<CustomerSegmentResponse>> deactivateSegment(
      @Parameter(description = "Segment ID") @PathVariable String id) {
    log.info("REST request to deactivate segment: {}", id);
    CustomerSegmentResponse response = segmentService.deactivateSegment(id);
    return ResponseEntity.ok(ApiResponse.success(response, "Segment deactivated"));
  }

  /**
   * Deletes a segment.
   *
   * @param id the segment ID
   * @return no content
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a segment")
  public ResponseEntity<ApiResponse<Void>> deleteSegment(
      @Parameter(description = "Segment ID") @PathVariable String id) {
    log.info("REST request to delete segment: {}", id);
    segmentService.deleteSegment(id);
    return ResponseEntity.ok(ApiResponse.success(null, "Segment deleted"));
  }

  /**
   * Gets segments for a user.
   *
   * @param userId the user ID
   * @return list of segment IDs
   */
  @GetMapping("/user/{userId}")
  @Operation(summary = "Get segments for a user")
  public ResponseEntity<ApiResponse<List<String>>> getUserSegments(
      @Parameter(description = "User ID") @PathVariable String userId) {
    log.debug("REST request to get segments for user: {}", userId);
    List<String> segmentIds = segmentService.calculateUserSegments(userId);
    return ResponseEntity.ok(ApiResponse.success(segmentIds));
  }

  /**
   * Gets user count for a segment.
   *
   * @param id the segment ID
   * @return the user count
   */
  @GetMapping("/{id}/user-count")
  @Operation(summary = "Get user count for a segment")
  public ResponseEntity<ApiResponse<Integer>> getSegmentUserCount(
      @Parameter(description = "Segment ID") @PathVariable String id) {
    log.debug("REST request to get user count for segment: {}", id);
    int count = segmentService.getSegmentUserCount(id);
    return ResponseEntity.ok(ApiResponse.success(count));
  }

  /**
   * Adds a user to a segment.
   *
   * @param id the segment ID
   * @param userId the user ID
   * @return the updated segment
   */
  @PostMapping("/{id}/users/{userId}")
  @Operation(summary = "Add a user to a segment")
  public ResponseEntity<ApiResponse<CustomerSegmentResponse>> addUserToSegment(
      @Parameter(description = "Segment ID") @PathVariable String id,
      @Parameter(description = "User ID") @PathVariable String userId) {
    log.info("REST request to add user {} to segment {}", userId, id);
    CustomerSegmentResponse response = segmentService.addUserToSegment(id, userId);
    return ResponseEntity.ok(ApiResponse.success(response, "User added to segment"));
  }

  /**
   * Removes a user from a segment.
   *
   * @param id the segment ID
   * @param userId the user ID
   * @return the updated segment
   */
  @DeleteMapping("/{id}/users/{userId}")
  @Operation(summary = "Remove a user from a segment")
  public ResponseEntity<ApiResponse<CustomerSegmentResponse>> removeUserFromSegment(
      @Parameter(description = "Segment ID") @PathVariable String id,
      @Parameter(description = "User ID") @PathVariable String userId) {
    log.info("REST request to remove user {} from segment {}", userId, id);
    CustomerSegmentResponse response = segmentService.removeUserFromSegment(id, userId);
    return ResponseEntity.ok(ApiResponse.success(response, "User removed from segment"));
  }
}

