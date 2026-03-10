package com.foodai.promotion.exception;

import com.foodai.promotion.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Global exception handler for REST controllers.
 *
 * @author FoodAI Team
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * Handles PromotionNotFoundException.
   *
   * @param ex the exception
   * @param request the request
   * @return the error response
   */
  @ExceptionHandler(PromotionNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ApiResponse<Void>> handlePromotionNotFound(
      PromotionNotFoundException ex, WebRequest request) {
    log.warn("Promotion not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error("PROMOTION_NOT_FOUND", ex.getMessage()));
  }

  /**
   * Handles CouponNotFoundException.
   *
   * @param ex the exception
   * @param request the request
   * @return the error response
   */
  @ExceptionHandler(CouponNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ApiResponse<Void>> handleCouponNotFound(
      CouponNotFoundException ex, WebRequest request) {
    log.warn("Coupon not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error("COUPON_NOT_FOUND", ex.getMessage()));
  }

  /**
   * Handles CouponValidationException.
   *
   * @param ex the exception
   * @param request the request
   * @return the error response
   */
  @ExceptionHandler(CouponValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ApiResponse<Void>> handleCouponValidation(
      CouponValidationException ex, WebRequest request) {
    log.warn("Coupon validation failed: {} - {}", ex.getErrorCode(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage()));
  }

  /**
   * Handles CampaignNotFoundException.
   *
   * @param ex the exception
   * @param request the request
   * @return the error response
   */
  @ExceptionHandler(CampaignNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ApiResponse<Void>> handleCampaignNotFound(
      CampaignNotFoundException ex, WebRequest request) {
    log.warn("Campaign not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error("CAMPAIGN_NOT_FOUND", ex.getMessage()));
  }

  /**
   * Handles SegmentNotFoundException.
   *
   * @param ex the exception
   * @param request the request
   * @return the error response
   */
  @ExceptionHandler(SegmentNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ResponseEntity<ApiResponse<Void>> handleSegmentNotFound(
      SegmentNotFoundException ex, WebRequest request) {
    log.warn("Segment not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiResponse.error("SEGMENT_NOT_FOUND", ex.getMessage()));
  }

  /**
   * Handles validation exceptions.
   *
   * @param ex the exception
   * @param request the request
   * @return the error response
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ApiResponse<Void>> handleValidationException(
      MethodArgumentNotValidException ex, WebRequest request) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .findFirst()
            .orElse("Validation failed");
    log.warn("Validation failed: {}", message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error("VALIDATION_ERROR", message));
  }

  /**
   * Handles constraint violation exceptions.
   *
   * @param ex the exception
   * @param request the request
   * @return the error response
   */
  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(
      ConstraintViolationException ex, WebRequest request) {
    String message =
        ex.getConstraintViolations().stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .findFirst()
            .orElse("Constraint violation");
    log.warn("Constraint violation: {}", message);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error("CONSTRAINT_VIOLATION", message));
  }

  /**
   * Handles IllegalStateException.
   *
   * @param ex the exception
   * @param request the request
   * @return the error response
   */
  @ExceptionHandler(IllegalStateException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ApiResponse<Void>> handleIllegalState(
      IllegalStateException ex, WebRequest request) {
    log.warn("Illegal state: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ApiResponse.error("ILLEGAL_STATE", ex.getMessage()));
  }

  /**
   * Handles all other exceptions.
   *
   * @param ex the exception
   * @param request the request
   * @return the error response
   */
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ApiResponse<Void>> handleGenericException(
      Exception ex, WebRequest request) {
    log.error("Unexpected error", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiResponse.error("INTERNAL_ERROR", "An unexpected error occurred"));
  }
}

