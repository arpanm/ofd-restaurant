package com.foodai.restaurant.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for TAT configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TATConfigDTO {
    
    @NotNull(message = "Base prep time is required")
    @Min(value = 1, message = "Base prep time must be at least 1 minute")
    private Integer basePrepTimeMinutes;
    
    @Min(value = 0, message = "TAT per order must be non-negative")
    private Integer tatPerOrderInQueue;
    
    @Min(value = 0, message = "Peak hour extra must be non-negative")
    private Integer peakHourExtraMinutes;
    
    private Boolean considerRiderAvailability;
    
    private Boolean considerBatching;
    
    private Boolean considerDistance;
    
    @NotNull(message = "Max TAT is required")
    @Min(value = 1, message = "Max TAT must be at least 1 minute")
    private Integer maxTATMinutes;
    
    @Min(value = 0, message = "Buffer time must be non-negative")
    private Integer bufferTimeMinutes;
}


