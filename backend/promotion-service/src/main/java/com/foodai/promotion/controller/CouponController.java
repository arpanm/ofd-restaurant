package com.foodai.promotion.controller;

import com.foodai.promotion.dto.request.ApplyCouponRequest;
import com.foodai.promotion.dto.request.CreateCouponRequest;
import com.foodai.promotion.dto.request.ValidateCouponRequest;
import com.foodai.promotion.dto.response.ApiResponse;
import com.foodai.promotion.dto.response.CouponResponse;
import com.foodai.promotion.dto.response.CouponValidationResponse;
import com.foodai.promotion.service.CouponService;
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
 * REST controller for managing coupons.
 *
 * @author FoodAI Team
 */
@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Coupons", description = "Coupon management endpoints")
public class CouponController {

  private final CouponService couponService;

  /**
   * Creates a new coupon.
   *
   * @param request the create request
   * @return the created coupon
   */
  @PostMapping
  @Operation(summary = "Create a new coupon")
  public ResponseEntity<ApiResponse<CouponResponse>> createCoupon(
      @Valid @RequestBody CreateCouponRequest request) {
    log.info("REST request to create coupon: {}", request.getCode());
    CouponResponse response = couponService.createCoupon(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(response, "Coupon created successfully"));
  }

  /**
   * Gets a coupon by ID.
   *
   * @param id the coupon ID
   * @return the coupon
   */
  @GetMapping("/{id}")
  @Operation(summary = "Get coupon by ID")
  public ResponseEntity<ApiResponse<CouponResponse>> getCoupon(
      @Parameter(description = "Coupon ID") @PathVariable String id) {
    log.debug("REST request to get coupon: {}", id);
    CouponResponse response = couponService.getCoupon(id);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Gets a coupon by code.
   *
   * @param code the coupon code
   * @return the coupon
   */
  @GetMapping("/code/{code}")
  @Operation(summary = "Get coupon by code")
  public ResponseEntity<ApiResponse<CouponResponse>> getCouponByCode(
      @Parameter(description = "Coupon code") @PathVariable String code) {
    log.debug("REST request to get coupon by code: {}", code);
    CouponResponse response = couponService.getCouponByCode(code);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Gets all coupons.
   *
   * @param pageable pagination info
   * @return page of coupons
   */
  @GetMapping
  @Operation(summary = "Get all coupons")
  public ResponseEntity<ApiResponse<Page<CouponResponse>>> getAllCoupons(
      @PageableDefault(size = 20) Pageable pageable) {
    log.debug("REST request to get all coupons");
    Page<CouponResponse> page = couponService.getAllCoupons(pageable);
    return ResponseEntity.ok(ApiResponse.success(page));
  }

  /**
   * Gets coupons by restaurant.
   *
   * @param restaurantId the restaurant ID
   * @param pageable pagination info
   * @return page of coupons
   */
  @GetMapping("/restaurant/{restaurantId}")
  @Operation(summary = "Get coupons by restaurant")
  public ResponseEntity<ApiResponse<Page<CouponResponse>>> getCouponsByRestaurant(
      @Parameter(description = "Restaurant ID") @PathVariable String restaurantId,
      @PageableDefault(size = 20) Pageable pageable) {
    log.debug("REST request to get coupons for restaurant: {}", restaurantId);
    Page<CouponResponse> page = couponService.getCouponsByRestaurant(restaurantId, pageable);
    return ResponseEntity.ok(ApiResponse.success(page));
  }

  /**
   * Gets valid coupons for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return list of valid coupons
   */
  @GetMapping("/restaurant/{restaurantId}/valid")
  @Operation(summary = "Get valid coupons for restaurant")
  public ResponseEntity<ApiResponse<List<CouponResponse>>> getValidCoupons(
      @Parameter(description = "Restaurant ID") @PathVariable String restaurantId) {
    log.debug("REST request to get valid coupons for restaurant: {}", restaurantId);
    List<CouponResponse> coupons = couponService.getValidCoupons(restaurantId);
    return ResponseEntity.ok(ApiResponse.success(coupons));
  }

  /**
   * Gets platform-wide coupons.
   *
   * @return list of platform-wide coupons
   */
  @GetMapping("/platform")
  @Operation(summary = "Get platform-wide coupons")
  public ResponseEntity<ApiResponse<List<CouponResponse>>> getPlatformWideCoupons() {
    log.debug("REST request to get platform-wide coupons");
    List<CouponResponse> coupons = couponService.getPlatformWideCoupons();
    return ResponseEntity.ok(ApiResponse.success(coupons));
  }

  /**
   * Validates a coupon.
   *
   * @param request the validation request
   * @return the validation response
   */
  @PostMapping("/validate")
  @Operation(summary = "Validate a coupon")
  public ResponseEntity<ApiResponse<CouponValidationResponse>> validateCoupon(
      @Valid @RequestBody ValidateCouponRequest request) {
    log.debug("REST request to validate coupon: {}", request.getCode());
    CouponValidationResponse response = couponService.validateCoupon(request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Applies a coupon to an order.
   *
   * @param request the apply request
   * @return the validation response
   */
  @PostMapping("/apply")
  @Operation(summary = "Apply a coupon to an order")
  public ResponseEntity<ApiResponse<CouponValidationResponse>> applyCoupon(
      @Valid @RequestBody ApplyCouponRequest request) {
    log.info("REST request to apply coupon: {} to order: {}", request.getCode(), request.getOrderId());
    CouponValidationResponse response = couponService.applyCoupon(request);
    return ResponseEntity.ok(ApiResponse.success(response, "Coupon applied successfully"));
  }

  /**
   * Activates a coupon.
   *
   * @param id the coupon ID
   * @return the updated coupon
   */
  @PutMapping("/{id}/activate")
  @Operation(summary = "Activate a coupon")
  public ResponseEntity<ApiResponse<CouponResponse>> activateCoupon(
      @Parameter(description = "Coupon ID") @PathVariable String id) {
    log.info("REST request to activate coupon: {}", id);
    CouponResponse response = couponService.activateCoupon(id);
    return ResponseEntity.ok(ApiResponse.success(response, "Coupon activated"));
  }

  /**
   * Deactivates a coupon.
   *
   * @param id the coupon ID
   * @return the updated coupon
   */
  @PutMapping("/{id}/deactivate")
  @Operation(summary = "Deactivate a coupon")
  public ResponseEntity<ApiResponse<CouponResponse>> deactivateCoupon(
      @Parameter(description = "Coupon ID") @PathVariable String id) {
    log.info("REST request to deactivate coupon: {}", id);
    CouponResponse response = couponService.deactivateCoupon(id);
    return ResponseEntity.ok(ApiResponse.success(response, "Coupon deactivated"));
  }

  /**
   * Deletes a coupon.
   *
   * @param id the coupon ID
   * @return no content
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a coupon")
  public ResponseEntity<ApiResponse<Void>> deleteCoupon(
      @Parameter(description = "Coupon ID") @PathVariable String id) {
    log.info("REST request to delete coupon: {}", id);
    couponService.deleteCoupon(id);
    return ResponseEntity.ok(ApiResponse.success(null, "Coupon deleted"));
  }
}

