package com.foodai.promotion.exception;

/**
 * Exception thrown when a segment is not found.
 *
 * @author FoodAI Team
 */
public class SegmentNotFoundException extends RuntimeException {

  private static final String DEFAULT_MESSAGE = "Segment not found with id: ";

  /**
   * Creates a new exception with segment ID.
   *
   * @param segmentId the segment ID
   */
  public SegmentNotFoundException(String segmentId) {
    super(DEFAULT_MESSAGE + segmentId);
  }

  /**
   * Creates a new exception with message and cause.
   *
   * @param message the message
   * @param cause the cause
   */
  public SegmentNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}

