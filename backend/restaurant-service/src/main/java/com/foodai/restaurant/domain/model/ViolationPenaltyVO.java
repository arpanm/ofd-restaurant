package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value Object for other violation penalties.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViolationPenaltyVO {
    /**
     * Type of violation (e.g., FSSAI_VIOLATION, UNAUTHORIZED_OPERATION)
     */
    private String violationType;
    
    /**
     * Description of the violation
     */
    private String description;
    
    /**
     * Penalty amount
     */
    private Double penaltyAmount;
    
    /**
     * Action to be taken
     */
    private PenaltyAction action;
    
    /**
     * Number of days for suspension (if applicable)
     */
    private Integer suspensionDays;
}


