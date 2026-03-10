package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Turn Around Time (TAT) configuration for an outlet.
 * Controls how TAT is calculated for orders.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TATConfig {
    /**
     * Base food preparation time in minutes
     */
    private Integer basePrepTimeMinutes;
    
    /**
     * Additional minutes per order in queue
     */
    private Integer tatPerOrderInQueue;
    
    /**
     * Extra minutes to add during peak hours
     */
    private Integer peakHourExtraMinutes;
    
    /**
     * Peak hours definition
     */
    private List<TimeRangeVO> peakHours;
    
    /**
     * Factor in rider availability
     */
    private Boolean considerRiderAvailability;
    
    /**
     * Factor in order batching potential
     */
    private Boolean considerBatching;
    
    /**
     * Factor in delivery distance
     */
    private Boolean considerDistance;
    
    /**
     * Maximum TAT promised to customer
     */
    private Integer maxTATMinutes;
    
    /**
     * Buffer time for safety margin
     */
    private Integer bufferTimeMinutes;
}


