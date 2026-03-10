package com.foodai.menu.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

/**
 * Value Object representing the availability schedule for a menu item.
 *
 * <p>Allows items to be available only during specific time slots and days.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityScheduleVO {

  /**
   * Whether the item is always available.
   */
  private boolean alwaysAvailable;

  /**
   * Days of the week when the item is available.
   */
  private Set<DayOfWeek> availableDays;

  /**
   * Start time of availability (daily).
   */
  private LocalTime startTime;

  /**
   * End time of availability (daily).
   */
  private LocalTime endTime;

  /**
   * Specific date ranges for seasonal availability.
   */
  private LocalDateTime seasonalStartDate;

  private LocalDateTime seasonalEndDate;

  /**
   * Checks if the item is available at the current time.
   *
   * @return true if available now
   */
  public boolean isAvailableNow() {
    if (alwaysAvailable) {
      return true;
    }

    LocalDateTime now = LocalDateTime.now();

    // Check seasonal dates
    if (seasonalStartDate != null && now.isBefore(seasonalStartDate)) {
      return false;
    }
    if (seasonalEndDate != null && now.isAfter(seasonalEndDate)) {
      return false;
    }

    // Check day of week
    if (availableDays != null && !availableDays.isEmpty()) {
      DayOfWeek today = now.getDayOfWeek();
      if (!availableDays.contains(today)) {
        return false;
      }
    }

    // Check time of day
    if (startTime != null && endTime != null) {
      LocalTime currentTime = now.toLocalTime();
      return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime);
    }

    return true;
  }

  /**
   * Validates the schedule.
   *
   * @throws IllegalStateException if invalid
   */
  public void validate() {
    if (!alwaysAvailable) {
      if (startTime != null && endTime != null && startTime.isAfter(endTime)) {
        throw new IllegalStateException("Start time cannot be after end time");
      }

      if (seasonalStartDate != null && seasonalEndDate != null &&
          seasonalStartDate.isAfter(seasonalEndDate)) {
        throw new IllegalStateException("Seasonal start date cannot be after end date");
      }
    }
  }
}

