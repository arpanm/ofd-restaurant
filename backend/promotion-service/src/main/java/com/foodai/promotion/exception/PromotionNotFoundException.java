package com.foodai.promotion.exception;

/**
 * Exception thrown when a promotion is not found.
 *
 * @author FoodAI Team
 */
public class PromotionNotFoundException extends RuntimeException {

  private static final String DEFAULT_MESSAGE = "Promotion not found with id: ";

  /**
   * Creates a new exception with promotion ID.
   *
   * @param promotionId the promotion ID
   */
  public PromotionNotFoundException(String promotionId) {
    super(DEFAULT_MESSAGE + promotionId);
  }

  /**
   * Creates a new exception with message and cause.
   *
   * @param message the message
   * @param cause the cause
   */
  public PromotionNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}

