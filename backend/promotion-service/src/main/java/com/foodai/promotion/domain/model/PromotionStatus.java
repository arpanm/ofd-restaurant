package com.foodai.promotion.domain.model;

/**
 * Enumeration of promotion statuses.
 *
 * @author FoodAI Team
 */
public enum PromotionStatus {
  /** Promotion is a draft and not yet published. */
  DRAFT,

  /** Promotion is scheduled to start in the future. */
  SCHEDULED,

  /** Promotion is currently active and can be used. */
  ACTIVE,

  /** Promotion is temporarily paused. */
  PAUSED,

  /** Promotion has ended. */
  ENDED,

  /** Promotion has been expired. */
  EXPIRED,

  /** Promotion has been cancelled. */
  CANCELLED
}

