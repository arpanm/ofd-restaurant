package com.foodai.order.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Entity representing a payment transaction.
 *
 * @author FoodAI Team
 */
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_order_id", columnList = "orderId"),
    @Index(name = "idx_payment_user_id", columnList = "userId"),
    @Index(name = "idx_payment_status", columnList = "status"),
    @Index(name = "idx_payment_transaction_id", columnList = "transactionId")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Order ID this payment is for.
     */
    @Column(nullable = false)
    private String orderId;

    /**
     * User ID who made the payment.
     */
    @Column(nullable = false)
    private String userId;

    /**
     * Payment amount.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * Currency code.
     */
    @Column(length = 3)
    @Builder.Default
    private String currency = "INR";

    /**
     * Payment provider (razorpay, stripe, etc.).
     */
    @Column(length = 50)
    @Builder.Default
    private String paymentProvider = "razorpay";

    /**
     * Payment gateway order ID.
     */
    private String gatewayOrderId;

    /**
     * Payment gateway transaction/payment ID.
     */
    private String transactionId;

    /**
     * Payment gateway signature (for verification).
     */
    private String gatewaySignature;

    /**
     * Payment method used.
     */
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    /**
     * Payment method details (JSON).
     * e.g., {"upi_id": "user@upi", "card_last4": "4242", "bank_name": "HDFC"}
     */
    @Column(columnDefinition = "TEXT")
    private String paymentMethodDetails;

    /**
     * Payment status.
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    /**
     * Failure reason if payment failed.
     */
    @Column(length = 500)
    private String failureReason;

    /**
     * Number of payment attempts.
     */
    @Builder.Default
    private Integer attempts = 0;

    /**
     * IP address of the payer.
     */
    private String ipAddress;

    /**
     * User agent of the payer.
     */
    @Column(length = 500)
    private String userAgent;

    /**
     * Metadata (JSON format).
     */
    @Column(columnDefinition = "TEXT")
    private String metadata;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    /**
     * Timestamp when payment was completed.
     */
    private Instant completedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    /**
     * Marks payment as successful.
     *
     * @param transactionId the transaction ID from gateway
     */
    public void markSuccess(String transactionId) {
        this.transactionId = transactionId;
        this.status = PaymentStatus.SUCCESS;
        this.completedAt = Instant.now();
    }

    /**
     * Marks payment as failed.
     *
     * @param reason failure reason
     */
    public void markFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
    }

    /**
     * Marks payment as refunded.
     */
    public void markRefunded() {
        this.status = PaymentStatus.REFUNDED;
    }

    /**
     * Marks payment as partially refunded.
     */
    public void markPartiallyRefunded() {
        this.status = PaymentStatus.PARTIALLY_REFUNDED;
    }

    /**
     * Increments payment attempt counter.
     */
    public void incrementAttempts() {
        this.attempts++;
    }
}

