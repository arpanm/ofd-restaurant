package com.foodai.menu.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

/**
 * Value Object representing a time slot for pricing.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceTimeSlotVO {

  /**
   * Days this slot applies to.
   */
  private Set<DayOfWeek> days;

  /**
   * Start time of the slot.
   */
  private LocalTime startTime;

  /**
   * End time of the slot.
   */
  private LocalTime endTime;

  /**
   * Price multiplier for this slot.
   */
  private BigDecimal multiplier;

  /**
   * Validates the time slot.
   *
   * @throws IllegalStateException if invalid
   */
  public void validate() {
    if (startTime == null || endTime == null) {
      throw new IllegalStateException("Start time and end time are required");
    }

    if (startTime.isAfter(endTime)) {
      throw new IllegalStateException("Start time cannot be after end time");
    }

    if (multiplier == null || multiplier.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalStateException("Multiplier must be positive");
    }
  }
}

