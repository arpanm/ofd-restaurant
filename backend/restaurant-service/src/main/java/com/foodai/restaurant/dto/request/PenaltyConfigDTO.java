package com.foodai.restaurant.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for penalty configuration.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyConfigDTO {
    private Boolean ratingPenaltyEnabled;
    private Double criticalRatingThreshold;
    private Double warningRatingThreshold;
    
    private Boolean cancellationPenaltyEnabled;
    private Double cancellationPenaltyAmount;
    private Integer allowedCancellationsPerMonth;
    
    private Boolean delayPenaltyEnabled;
    private Double penaltyPerMinuteDelay;
    private Integer allowedDelayMinutes;
}


