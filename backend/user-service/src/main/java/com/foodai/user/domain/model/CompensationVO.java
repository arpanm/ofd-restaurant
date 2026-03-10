package com.foodai.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Value Object representing compensation offered for feedback resolution.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompensationVO {

    /**
     * Type of compensation
     */
    private CompensationType type;

    /**
     * Compensation amount (for refund/credits)
     */
    private Double amount;

    /**
     * Coupon code (for coupon compensation)
     */
    private String couponCode;

    /**
     * Discount percentage (for discount compensation)
     */
    private Integer discountPercentage;

    /**
     * Loyalty points awarded
     */
    private Long loyaltyPoints;

    /**
     * Description of compensation
     */
    private String description;

    /**
     * Compensation status
     */
    @Builder.Default
    private CompensationStatus status = CompensationStatus.OFFERED;

    /**
     * When compensation was offered
     */
    private Instant offeredAt;

    /**
     * When compensation was accepted/rejected
     */
    private Instant respondedAt;

    /**
     * When compensation was processed
     */
    private Instant processedAt;
}

