package com.foodai.promotion.domain.model;

/**
 * Enumeration of campaign statuses.
 *
 * @author FoodAI Team
 */
public enum CampaignStatus {
  /** Campaign is being created. */
  DRAFT,

  /** Campaign is scheduled to run. */
  SCHEDULED,

  /** Campaign is currently running. */
  ACTIVE,

  /** Campaign is temporarily paused. */
  PAUSED,

  /** Campaign has completed. */
  COMPLETED,

  /** Campaign has been cancelled. */
  CANCELLED,

  /** Campaign has been archived. */
  ARCHIVED
}

