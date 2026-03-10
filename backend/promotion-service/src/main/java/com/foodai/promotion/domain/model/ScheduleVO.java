package com.foodai.promotion.domain.model;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value object representing promotion/campaign schedule.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleVO {

  /** Start date and time of the promotion. */
  private Instant startDate;

  /** End date and time of the promotion. */
  private Instant endDate;

  /** Start time (for daily/weekly schedules). */
  private LocalTime startTime;

  /** End time (for daily/weekly schedules). */
  private LocalTime endTime;

  /** Days of week when promotion is active (for weekly schedules). */
  private Set<DayOfWeek> daysOfWeek;

  /** Campaign frequency (for recurring promotions). */
  private CampaignFrequency frequency;

  /** Custom cron expression (for custom frequency). */
  private String cronExpression;

  /**
   * Checks if the promotion is currently active based on schedule.
   *
   * @param now the current time
   * @return true if promotion is active, false otherwise
   */
  public boolean isActiveNow(Instant now) {
    if (startDate != null && now.isBefore(startDate)) {
      return false;
    }
    if (endDate != null && now.isAfter(endDate)) {
      return false;
    }
    return true;
  }

  /**
   * Checks if the schedule has expired.
   *
   * @param now the current time
   * @return true if expired, false otherwise
   */
  public boolean isExpired(Instant now) {
    return endDate != null && now.isAfter(endDate);
  }

  /**
   * Checks if the schedule is scheduled for future.
   *
   * @param now the current time
   * @return true if scheduled for future, false otherwise
   */
  public boolean isScheduled(Instant now) {
    return startDate != null && now.isBefore(startDate);
  }
}

