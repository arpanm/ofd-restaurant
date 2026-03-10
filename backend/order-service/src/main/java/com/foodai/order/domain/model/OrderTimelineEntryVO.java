package com.foodai.order.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Value object representing a single entry in the order timeline.
 *
 * @author FoodAI Team
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTimelineEntryVO {

    /**
     * Status at this point in the timeline.
     */
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /**
     * Human-readable description of the status.
     */
    private String description;

    /**
     * Timestamp when this status was set.
     */
    private Instant timestamp;

    /**
     * Who/what triggered this status change.
     */
    private String updatedBy;

    /**
     * Additional notes or details.
     */
    private String notes;
}

