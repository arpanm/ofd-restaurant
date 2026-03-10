package com.foodai.promotion.exception;

/**
 * Exception thrown when coupon validation fails.
 *
 * @author FoodAI Team
 */
public class CouponValidationException extends RuntimeException {

  private final String errorCode;

  /**
   * Creates a new exception with error code and message.
   *
   * @param errorCode the error code
   * @param message the message
   */
  public CouponValidationException(String errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  /**
   * Gets the error code.
   *
   * @return the error code
   */
  public String getErrorCode() {
    return errorCode;
  }

  /** Common error codes for coupon validation. */
  public static final String CODE_INVALID = "INVALID_CODE";
  public static final String CODE_EXPIRED = "COUPON_EXPIRED";
  public static final String CODE_NOT_STARTED = "COUPON_NOT_STARTED";
  public static final String CODE_USAGE_LIMIT_REACHED = "USAGE_LIMIT_REACHED";
  public static final String CODE_MIN_ORDER_NOT_MET = "MIN_ORDER_NOT_MET";
  public static final String CODE_FIRST_ORDER_ONLY = "FIRST_ORDER_ONLY";
  public static final String CODE_NEW_USER_ONLY = "NEW_USER_ONLY";
  public static final String CODE_USER_LIMIT_REACHED = "USER_LIMIT_REACHED";
  public static final String CODE_NOT_APPLICABLE = "NOT_APPLICABLE";
  public static final String CODE_INACTIVE = "COUPON_INACTIVE";
}

