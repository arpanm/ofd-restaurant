package com.foodai.restaurant.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Response DTO for Operating Hours.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperatingHoursResponse {
    private DayOfWeek dayOfWeek;
    private LocalTime openTime;
    private LocalTime closeTime;
    private Boolean isClosed;
}


