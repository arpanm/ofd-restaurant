package com.foodai.promotion.service;

import com.foodai.promotion.domain.model.Coupon;
import com.foodai.promotion.domain.model.CouponUsage;
import com.foodai.promotion.domain.repository.CouponRepository;
import com.foodai.promotion.domain.repository.CouponUsageRepository;
import com.foodai.promotion.dto.request.ApplyCouponRequest;
import com.foodai.promotion.dto.request.CreateCouponRequest;
import com.foodai.promotion.dto.request.ValidateCouponRequest;
import com.foodai.promotion.dto.response.CouponResponse;
import com.foodai.promotion.dto.response.CouponValidationResponse;
import com.foodai.promotion.exception.CouponNotFoundException;
import com.foodai.promotion.exception.CouponValidationException;
import com.foodai.promotion.mapper.CouponMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing coupons.
 *
 * @author FoodAI Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CouponService {

  private final CouponRepository couponRepository;
  private final CouponUsageRepository couponUsageRepository;
  private final CouponMapper couponMapper;

  /**
   * Creates a new coupon.
   *
   * @param request the create request
   * @return the created coupon
   */
  @Transactional
  public CouponResponse createCoupon(CreateCouponRequest request) {
    log.info("Creating coupon: {}", request.getCode());

    // Validate unique code
    if (couponRepository.existsByCodeIgnoreCaseAndDeletedFalse(request.getCode())) {
      throw new IllegalStateException("Coupon code already exists: " + request.getCode());
    }

    Coupon coupon = couponMapper.toEntity(request);
    Coupon saved = couponRepository.save(coupon);

    log.info("Coupon created: id={}, code={}", saved.getId(), saved.getCode());
    return couponMapper.toResponse(saved);
  }

  /**
   * Gets a coupon by ID.
   *
   * @param id the coupon ID
   * @return the coupon
   */
  public CouponResponse getCoupon(String id) {
    log.debug("Getting coupon: {}", id);
    Coupon coupon =
        couponRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new CouponNotFoundException(id));
    return couponMapper.toResponse(coupon);
  }

  /**
   * Gets a coupon by code.
   *
   * @param code the coupon code
   * @return the coupon
   */
  public CouponResponse getCouponByCode(String code) {
    log.debug("Getting coupon by code: {}", code);
    Coupon coupon =
        couponRepository
            .findByCodeIgnoreCaseAndDeletedFalse(code)
            .orElseThrow(() -> new CouponNotFoundException(code));
    return couponMapper.toResponse(coupon);
  }

  /**
   * Gets all coupons with pagination.
   *
   * @param pageable pagination info
   * @return page of coupons
   */
  public Page<CouponResponse> getAllCoupons(Pageable pageable) {
    log.debug("Getting all coupons: page={}", pageable.getPageNumber());
    return couponRepository.findByDeletedFalse(pageable).map(couponMapper::toResponse);
  }

  /**
   * Gets coupons by restaurant.
   *
   * @param restaurantId the restaurant ID
   * @param pageable pagination info
   * @return page of coupons
   */
  public Page<CouponResponse> getCouponsByRestaurant(String restaurantId, Pageable pageable) {
    log.debug("Getting coupons for restaurant: {}", restaurantId);
    return couponRepository
        .findByRestaurantIdAndDeletedFalse(restaurantId, pageable)
        .map(couponMapper::toResponse);
  }

  /**
   * Gets valid coupons for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return list of valid coupons
   */
  public List<CouponResponse> getValidCoupons(String restaurantId) {
    log.debug("Getting valid coupons for restaurant: {}", restaurantId);
    return couponRepository.findValidCoupons(restaurantId, Instant.now()).stream()
        .map(couponMapper::toResponse)
        .collect(Collectors.toList());
  }

  /**
   * Gets platform-wide coupons.
   *
   * @return list of platform-wide coupons
   */
  public List<CouponResponse> getPlatformWideCoupons() {
    log.debug("Getting platform-wide coupons");
    return couponRepository.findPlatformWideCoupons(Instant.now()).stream()
        .map(couponMapper::toResponse)
        .collect(Collectors.toList());
  }

  /**
   * Validates a coupon.
   *
   * @param request the validation request
   * @return the validation response
   */
  public CouponValidationResponse validateCoupon(ValidateCouponRequest request) {
    log.debug("Validating coupon: {} for user: {}", request.getCode(), request.getUserId());

    Coupon coupon =
        couponRepository
            .findByCodeIgnoreCaseAndDeletedFalse(request.getCode())
            .orElse(null);

    if (coupon == null) {
      return CouponValidationResponse.invalid(
          CouponValidationException.CODE_INVALID, "Invalid coupon code");
    }

    // Validate coupon
    String validationError = validateCouponForUse(coupon, request);
    if (validationError != null) {
      return CouponValidationResponse.invalid(validationError, getErrorMessage(validationError));
    }

    // Calculate discount
    BigDecimal discount = coupon.calculateDiscount(request.getOrderValue());

    log.debug("Coupon valid: code={}, discount={}", request.getCode(), discount);
    return CouponValidationResponse.valid(
        couponMapper.toResponse(coupon), discount, request.getOrderValue());
  }

  /**
   * Applies a coupon to an order.
   *
   * @param request the apply request
   * @return the validation response
   */
  @Transactional
  public CouponValidationResponse applyCoupon(ApplyCouponRequest request) {
    log.info(
        "Applying coupon: {} for order: {}", request.getCode(), request.getOrderId());

    Coupon coupon =
        couponRepository
            .findByCodeIgnoreCaseAndDeletedFalse(request.getCode())
            .orElseThrow(() -> new CouponNotFoundException(request.getCode()));

    // Validate coupon
    ValidateCouponRequest validateRequest =
        ValidateCouponRequest.builder()
            .code(request.getCode())
            .userId(request.getUserId())
            .orderValue(request.getOrderValue())
            .restaurantId(request.getRestaurantId())
            .firstOrder(request.isFirstOrder())
            .newUser(request.isNewUser())
            .build();

    String validationError = validateCouponForUse(coupon, validateRequest);
    if (validationError != null) {
      throw new CouponValidationException(validationError, getErrorMessage(validationError));
    }

    // Calculate discount
    BigDecimal discount = coupon.calculateDiscount(request.getOrderValue());

    // Record usage
    coupon.recordUsage(request.getUserId());
    couponRepository.save(coupon);

    // Create usage record
    CouponUsage usage =
        CouponUsage.create(
            coupon,
            request.getUserId(),
            request.getOrderId(),
            request.getOrderValue(),
            discount);
    couponUsageRepository.save(usage);

    log.info(
        "Coupon applied: code={}, order={}, discount={}",
        request.getCode(),
        request.getOrderId(),
        discount);

    return CouponValidationResponse.valid(
        couponMapper.toResponse(coupon), discount, request.getOrderValue());
  }

  /**
   * Deactivates a coupon.
   *
   * @param id the coupon ID
   * @return the updated coupon
   */
  @Transactional
  public CouponResponse deactivateCoupon(String id) {
    log.info("Deactivating coupon: {}", id);
    Coupon coupon =
        couponRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new CouponNotFoundException(id));

    coupon.deactivate();
    Coupon saved = couponRepository.save(coupon);

    log.info("Coupon deactivated: {}", id);
    return couponMapper.toResponse(saved);
  }

  /**
   * Activates a coupon.
   *
   * @param id the coupon ID
   * @return the updated coupon
   */
  @Transactional
  public CouponResponse activateCoupon(String id) {
    log.info("Activating coupon: {}", id);
    Coupon coupon =
        couponRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new CouponNotFoundException(id));

    coupon.activate();
    Coupon saved = couponRepository.save(coupon);

    log.info("Coupon activated: {}", id);
    return couponMapper.toResponse(saved);
  }

  /**
   * Deletes a coupon (soft delete).
   *
   * @param id the coupon ID
   */
  @Transactional
  public void deleteCoupon(String id) {
    log.info("Deleting coupon: {}", id);
    Coupon coupon =
        couponRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new CouponNotFoundException(id));

    coupon.delete();
    couponRepository.save(coupon);

    log.info("Coupon deleted: {}", id);
  }

  /**
   * Validates a coupon for use.
   *
   * @param coupon the coupon
   * @param request the validation request
   * @return error code if invalid, null if valid
   */
  private String validateCouponForUse(Coupon coupon, ValidateCouponRequest request) {
    if (!coupon.isActive()) {
      return CouponValidationException.CODE_INACTIVE;
    }

    Instant now = Instant.now();
    if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
      return CouponValidationException.CODE_NOT_STARTED;
    }
    if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
      return CouponValidationException.CODE_EXPIRED;
    }

    if (coupon.getMinOrderValue() != null
        && request.getOrderValue().compareTo(coupon.getMinOrderValue()) < 0) {
      return CouponValidationException.CODE_MIN_ORDER_NOT_MET;
    }

    if (coupon.getTotalLimit() != null && coupon.getCurrentUsage() >= coupon.getTotalLimit()) {
      return CouponValidationException.CODE_USAGE_LIMIT_REACHED;
    }

    // Check per user limit
    long userUsage = couponUsageRepository.countByCouponIdAndUserId(coupon.getId(), request.getUserId());
    if (coupon.getPerUserLimit() != null && userUsage >= coupon.getPerUserLimit()) {
      return CouponValidationException.CODE_USER_LIMIT_REACHED;
    }

    if (coupon.isFirstOrderOnly() && !request.isFirstOrder()) {
      return CouponValidationException.CODE_FIRST_ORDER_ONLY;
    }

    if (coupon.isNewUsersOnly() && !request.isNewUser()) {
      return CouponValidationException.CODE_NEW_USER_ONLY;
    }

    return null;
  }

  /**
   * Gets error message for error code.
   *
   * @param errorCode the error code
   * @return the error message
   */
  private String getErrorMessage(String errorCode) {
    return switch (errorCode) {
      case CouponValidationException.CODE_INVALID -> "Invalid coupon code";
      case CouponValidationException.CODE_EXPIRED -> "Coupon has expired";
      case CouponValidationException.CODE_NOT_STARTED -> "Coupon is not yet active";
      case CouponValidationException.CODE_USAGE_LIMIT_REACHED -> "Coupon usage limit reached";
      case CouponValidationException.CODE_MIN_ORDER_NOT_MET -> "Minimum order value not met";
      case CouponValidationException.CODE_FIRST_ORDER_ONLY -> "Coupon valid for first order only";
      case CouponValidationException.CODE_NEW_USER_ONLY -> "Coupon valid for new users only";
      case CouponValidationException.CODE_USER_LIMIT_REACHED -> "You have reached the usage limit for this coupon";
      case CouponValidationException.CODE_INACTIVE -> "Coupon is not active";
      default -> "Coupon validation failed";
    };
  }
}

