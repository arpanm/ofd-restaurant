package com.foodai.order.domain.repository;

import com.foodai.order.domain.model.Payment;
import com.foodai.order.domain.model.PaymentMethod;
import com.foodai.order.domain.model.PaymentStatus;
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
 * Repository interface for Payment entity.
 *
 * @author FoodAI Team
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {

    /**
     * Finds a payment by order ID.
     */
    Optional<Payment> findByOrderId(String orderId);

    /**
     * Finds all payments for an order (in case of retries).
     */
    List<Payment> findByOrderIdOrderByCreatedAtDesc(String orderId);

    /**
     * Finds payments by user ID.
     */
    Page<Payment> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    /**
     * Finds a payment by transaction ID.
     */
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * Finds a payment by gateway order ID.
     */
    Optional<Payment> findByGatewayOrderId(String gatewayOrderId);

    /**
     * Finds payments by status.
     */
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);

    /**
     * Finds payments by user ID and status.
     */
    List<Payment> findByUserIdAndStatus(String userId, PaymentStatus status);

    /**
     * Finds payments by payment method.
     */
    Page<Payment> findByPaymentMethod(PaymentMethod paymentMethod, Pageable pageable);

    /**
     * Finds payments created within a date range.
     */
    @Query("SELECT p FROM Payment p WHERE p.createdAt BETWEEN :startDate AND :endDate ORDER BY p.createdAt DESC")
    Page<Payment> findByDateRange(@Param("startDate") Instant startDate, 
                                  @Param("endDate") Instant endDate, 
                                  Pageable pageable);

    /**
     * Calculates total successful payments for a user.
     */
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.userId = :userId AND p.status = 'SUCCESS'")
    BigDecimal sumSuccessfulPaymentsByUserId(@Param("userId") String userId);

    /**
     * Counts payments by status.
     */
    long countByStatus(PaymentStatus status);

    /**
     * Finds pending payments older than specified time (for timeout handling).
     */
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' AND p.createdAt < :beforeTime")
    List<Payment> findPendingPaymentsOlderThan(@Param("beforeTime") Instant beforeTime);

    /**
     * Checks if a successful payment exists for an order.
     */
    boolean existsByOrderIdAndStatus(String orderId, PaymentStatus status);
}

