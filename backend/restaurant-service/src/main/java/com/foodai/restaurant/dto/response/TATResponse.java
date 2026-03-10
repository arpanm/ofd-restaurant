package com.foodai.restaurant.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Response DTO for calculated TAT.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TATResponse {
    private Integer calculatedTATMinutes;
    private Map<String, Integer> breakdown;
    private Map<String, Object> factors;
}


