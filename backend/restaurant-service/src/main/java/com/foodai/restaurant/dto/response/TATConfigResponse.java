package com.foodai.restaurant.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for TAT configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TATConfigResponse {
    private Integer basePrepTimeMinutes;
    private Integer tatPerOrderInQueue;
    private Integer peakHourExtraMinutes;
    private Boolean considerRiderAvailability;
    private Boolean considerBatching;
    private Boolean considerDistance;
    private Integer maxTATMinutes;
    private Integer bufferTimeMinutes;
}


