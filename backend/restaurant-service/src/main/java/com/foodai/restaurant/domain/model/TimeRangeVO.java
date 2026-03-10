package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Value Object representing a time range (for peak hours, etc).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeRangeVO {
    /**
     * Start time of the range
     */
    private LocalTime startTime;
    
    /**
     * End time of the range
     */
    private LocalTime endTime;
}


