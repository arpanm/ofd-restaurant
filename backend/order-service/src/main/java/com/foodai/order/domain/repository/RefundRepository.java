package com.foodai.order.domain.repository;

import com.foodai.order.domain.model.Refund;
import com.foodai.order.domain.model.RefundStatus;
import com.foodai.order.domain.model.RefundType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Refund entity.
 *
 * @author FoodAI Team
 */
@Repository
public interface RefundRepository extends JpaRepository<Refund, String> {

    /**
     * Finds refunds by payment ID.
     */
    List<Refund> findByPaymentIdOrderByCreatedAtDesc(String paymentId);

    /**
     * Finds refunds by order ID.
     */
    List<Refund> findByOrderIdOrderByCreatedAtDesc(String orderId);

    /**
     * Finds refunds by user ID.
     */
    Page<Refund> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    /**
     * Finds a refund by gateway refund ID.
     */
    Optional<Refund> findByGatewayRefundId(String gatewayRefundId);

    /**
     * Finds refunds by status.
     */
    Page<Refund> findByStatus(RefundStatus status, Pageable pageable);

    /**
     * Finds refunds by type.
     */
    Page<Refund> findByRefundType(RefundType refundType, Pageable pageable);

    /**
     * Finds pending refunds.
     */
    List<Refund> findByStatus(RefundStatus status);

    /**
     * Calculates total refund amount for a payment.
     */
    @Query("SELECT SUM(r.amount) FROM Refund r WHERE r.paymentId = :paymentId AND r.status = 'PROCESSED'")
    BigDecimal sumProcessedRefundsByPaymentId(@Param("paymentId") String paymentId);

    /**
     * Calculates total refund amount for a user.
     */
    @Query("SELECT SUM(r.amount) FROM Refund r WHERE r.userId = :userId AND r.status = 'PROCESSED'")
    BigDecimal sumProcessedRefundsByUserId(@Param("userId") String userId);

    /**
     * Finds refunds created within a date range.
     */
    @Query("SELECT r FROM Refund r WHERE r.createdAt BETWEEN :startDate AND :endDate ORDER BY r.createdAt DESC")
    Page<Refund> findByDateRange(@Param("startDate") Instant startDate, 
                                 @Param("endDate") Instant endDate, 
                                 Pageable pageable);

    /**
     * Counts refunds by status.
     */
    long countByStatus(RefundStatus status);

    /**
     * Checks if a refund exists for an order.
     */
    boolean existsByOrderId(String orderId);

    /**
     * Finds pending refunds older than specified time (for retry/escalation).
     */
    @Query("SELECT r FROM Refund r WHERE r.status = 'PENDING' AND r.createdAt < :beforeTime")
    List<Refund> findPendingRefundsOlderThan(@Param("beforeTime") Instant beforeTime);
}

