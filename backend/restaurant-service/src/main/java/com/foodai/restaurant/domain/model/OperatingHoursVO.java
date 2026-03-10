package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Value Object representing operating hours for a specific day.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperatingHoursVO {
    /**
     * Day of the week
     */
    private DayOfWeek dayOfWeek;
    
    /**
     * Opening time
     */
    private LocalTime openTime;
    
    /**
     * Closing time
     */
    private LocalTime closeTime;
    
    /**
     * Whether outlet is closed on this day
     */
    private Boolean isClosed;
}


