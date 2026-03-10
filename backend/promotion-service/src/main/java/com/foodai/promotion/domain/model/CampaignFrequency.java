package com.foodai.promotion.domain.model;

/**
 * Enumeration of campaign frequency types.
 *
 * @author FoodAI Team
 */
public enum CampaignFrequency {
  /** Campaign runs only once. */
  ONCE,

  /** Campaign runs daily. */
  DAILY,

  /** Campaign runs weekly. */
  WEEKLY,

  /** Campaign runs monthly. */
  MONTHLY,

  /** Custom frequency defined by cron expression. */
  CUSTOM
}

