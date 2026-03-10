package com.foodai.order.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Entity representing a refund transaction.
 *
 * @author FoodAI Team
 */
@Entity
@Table(name = "refunds", indexes = {
    @Index(name = "idx_refund_payment_id", columnList = "paymentId"),
    @Index(name = "idx_refund_order_id", columnList = "orderId"),
    @Index(name = "idx_refund_user_id", columnList = "userId"),
    @Index(name = "idx_refund_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Payment ID this refund is for.
     */
    @Column(nullable = false)
    private String paymentId;

    /**
     * Order ID this refund is for.
     */
    @Column(nullable = false)
    private String orderId;

    /**
     * User ID who will receive the refund.
     */
    @Column(nullable = false)
    private String userId;

    /**
     * Refund amount.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Original payment amount.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal originalAmount;

    /**
     * Reason for refund.
     */
    @Column(length = 500)
    private String reason;

    /**
     * Type of refund.
     */
    @Enumerated(EnumType.STRING)
    private RefundType refundType;

    /**
     * Payment gateway refund ID.
     */
    private String gatewayRefundId;

    /**
     * Refund status.
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RefundStatus status = RefundStatus.PENDING;

    /**
     * Who initiated the refund.
     */
    private String initiatedBy;

    /**
     * Failure reason if refund failed.
     */
    @Column(length = 500)
    private String failureReason;

    /**
     * Additional notes.
     */
    @Column(length = 1000)
    private String notes;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Timestamp when refund was processed.
     */
    private Instant processedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    /**
     * Marks refund as processed.
     *
     * @param gatewayRefundId the refund ID from gateway
     */
    public void markProcessed(String gatewayRefundId) {
        this.gatewayRefundId = gatewayRefundId;
        this.status = RefundStatus.PROCESSED;
        this.processedAt = Instant.now();
    }

    /**
     * Marks refund as failed.
     *
     * @param reason failure reason
     */
    public void markFailed(String reason) {
        this.status = RefundStatus.FAILED;
        this.failureReason = reason;
    }

    /**
     * Checks if this is a full refund.
     *
     * @return true if full refund
     */
    public boolean isFullRefund() {
        return amount.compareTo(originalAmount) == 0;
    }
}

