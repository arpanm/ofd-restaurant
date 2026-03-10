package com.foodai.restaurant.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * DTO for operating hours.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperatingHoursDTO {
    
    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;
    
    private LocalTime openTime;
    
    private LocalTime closeTime;
    
    @NotNull(message = "Closed flag is required")
    private Boolean isClosed;
}


