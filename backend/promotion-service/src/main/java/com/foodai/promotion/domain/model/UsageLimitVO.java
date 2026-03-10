package com.foodai.promotion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value object representing usage limits for a promotion.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageLimitVO {

  /** Total number of times the promotion can be used. */
  private Integer totalLimit;

  /** Maximum uses per user. */
  private Integer perUserLimit;

  /** Maximum uses per day. */
  private Integer dailyLimit;

  /** Current total usage count. */
  private int currentUsage;

  /**
   * Checks if the total usage limit has been reached.
   *
   * @return true if limit reached, false otherwise
   */
  public boolean isTotalLimitReached() {
    return totalLimit != null && currentUsage >= totalLimit;
  }

  /**
   * Checks if the promotion can be used based on current usage.
   *
   * @param userUsageCount the number of times the user has used this promotion
   * @return true if can be used, false otherwise
   */
  public boolean canUse(int userUsageCount) {
    if (totalLimit != null && currentUsage >= totalLimit) {
      return false;
    }
    if (perUserLimit != null && userUsageCount >= perUserLimit) {
      return false;
    }
    return true;
  }

  /**
   * Increments the usage count.
   *
   * @return the new usage count
   */
  public int incrementUsage() {
    return ++currentUsage;
  }

  /**
   * Gets the remaining usage count.
   *
   * @return remaining usage, or null if unlimited
   */
  public Integer getRemainingUsage() {
    if (totalLimit == null) {
      return null;
    }
    return Math.max(0, totalLimit - currentUsage);
  }

  /**
   * Gets the usage percentage.
   *
   * @return usage percentage (0-100), or 0 if unlimited
   */
  public double getUsagePercentage() {
    if (totalLimit == null || totalLimit == 0) {
      return 0;
    }
    return (double) currentUsage / totalLimit * 100;
  }
}

