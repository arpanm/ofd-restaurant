package com.foodai.order.domain.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Value object representing the order total breakdown.
 *
 * @author FoodAI Team
 */
@Embeddable
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderTotalVO {

    /**
     * Sum of all item prices before taxes and fees.
     */
    private BigDecimal subtotal;

    /**
     * Delivery fee.
     */
    private BigDecimal deliveryFee;

    /**
     * Platform/service fee.
     */
    private BigDecimal platformFee;

    /**
     * Packaging charges.
     */
    private BigDecimal packagingCharges;

    /**
     * GST amount.
     */
    private BigDecimal gst;

    /**
     * GST percentage applied.
     */
    private BigDecimal gstPercentage;

    /**
     * Discount amount from coupon/promotion.
     */
    private BigDecimal discount;

    /**
     * Coupon code applied (if any).
     */
    private String couponCode;

    /**
     * Loyalty points used (in currency value).
     */
    private BigDecimal loyaltyPointsValue;

    /**
     * Tip amount for delivery partner.
     */
    private BigDecimal tip;

    /**
     * Final total amount to be paid.
     */
    private BigDecimal total;

    /**
     * Currency code (e.g., INR).
     */
    private String currency;

    /**
     * Calculates and updates the total amount.
     */
    public void calculateTotal() {
        BigDecimal calculatedTotal = subtotal != null ? subtotal : BigDecimal.ZERO;
        
        if (deliveryFee != null) {
            calculatedTotal = calculatedTotal.add(deliveryFee);
        }
        if (platformFee != null) {
            calculatedTotal = calculatedTotal.add(platformFee);
        }
        if (packagingCharges != null) {
            calculatedTotal = calculatedTotal.add(packagingCharges);
        }
        if (gst != null) {
            calculatedTotal = calculatedTotal.add(gst);
        }
        if (discount != null) {
            calculatedTotal = calculatedTotal.subtract(discount);
        }
        if (loyaltyPointsValue != null) {
            calculatedTotal = calculatedTotal.subtract(loyaltyPointsValue);
        }
        if (tip != null) {
            calculatedTotal = calculatedTotal.add(tip);
        }
        
        this.total = calculatedTotal.max(BigDecimal.ZERO);
    }
}

