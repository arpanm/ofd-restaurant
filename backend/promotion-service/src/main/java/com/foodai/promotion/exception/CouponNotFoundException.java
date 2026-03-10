package com.foodai.promotion.exception;

/**
 * Exception thrown when a coupon is not found.
 *
 * @author FoodAI Team
 */
public class CouponNotFoundException extends RuntimeException {

  private static final String DEFAULT_MESSAGE = "Coupon not found: ";

  /**
   * Creates a new exception with coupon code/ID.
   *
   * @param identifier the coupon code or ID
   */
  public CouponNotFoundException(String identifier) {
    super(DEFAULT_MESSAGE + identifier);
  }

  /**
   * Creates a new exception with message and cause.
   *
   * @param message the message
   * @param cause the cause
   */
  public CouponNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}

