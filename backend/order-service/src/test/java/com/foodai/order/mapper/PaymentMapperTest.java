package com.foodai.order.mapper;

import com.foodai.order.domain.model.Payment;
import com.foodai.order.domain.model.PaymentMethod;
import com.foodai.order.domain.model.PaymentStatus;
import com.foodai.order.domain.model.Refund;
import com.foodai.order.domain.model.RefundStatus;
import com.foodai.order.domain.model.RefundType;
import com.foodai.order.dto.response.PaymentResponse;
import com.foodai.order.dto.response.RefundResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for PaymentMapper.
 *
 * @author FoodAI Team
 */
@DisplayName("Payment Mapper Tests")
class PaymentMapperTest {

    private PaymentMapper paymentMapper;

    @BeforeEach
    void setUp() {
        paymentMapper = Mappers.getMapper(PaymentMapper.class);
    }

    @Nested
    @DisplayName("toResponse Tests")
    class ToResponseTests {

        @Test
        @DisplayName("Should map Payment to PaymentResponse with all fields")
        void shouldMapPaymentToResponse() {
            Payment payment = createFullPayment();

            PaymentResponse response = paymentMapper.toResponse(payment);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(payment.getId());
            assertThat(response.getOrderId()).isEqualTo(payment.getOrderId());
            assertThat(response.getUserId()).isEqualTo(payment.getUserId());
            assertThat(response.getAmount()).isEqualTo(payment.getAmount());
            assertThat(response.getStatus()).isEqualTo(payment.getStatus());
            assertThat(response.getPaymentMethod()).isEqualTo(payment.getPaymentMethod());
            assertThat(response.getTransactionId()).isEqualTo(payment.getTransactionId());
        }

        @Test
        @DisplayName("Should handle null payment")
        void shouldHandleNullPayment() {
            PaymentResponse response = paymentMapper.toResponse(null);
            assertThat(response).isNull();
        }

        @Test
        @DisplayName("Should map payment with minimal fields")
        void shouldMapPaymentWithMinimalFields() {
            Payment payment = Payment.builder()
                .id("payment-123")
                .orderId("order-123")
                .amount(new BigDecimal("500.00"))
                .status(PaymentStatus.PENDING)
                .build();

            PaymentResponse response = paymentMapper.toResponse(payment);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(payment.getId());
            assertThat(response.getStatus()).isEqualTo(PaymentStatus.PENDING);
        }
    }

    @Nested
    @DisplayName("toResponseList Tests")
    class ToResponseListTests {

        @Test
        @DisplayName("Should map list of Payments to responses")
        void shouldMapPaymentList() {
            List<Payment> payments = List.of(createFullPayment(), createFullPayment());

            List<PaymentResponse> responses = paymentMapper.toResponseList(payments);

            assertThat(responses).hasSize(2);
        }

        @Test
        @DisplayName("Should handle null list")
        void shouldHandleNullList() {
            List<PaymentResponse> responses = paymentMapper.toResponseList(null);
            assertThat(responses).isNull();
        }

        @Test
        @DisplayName("Should handle empty list")
        void shouldHandleEmptyList() {
            List<PaymentResponse> responses = paymentMapper.toResponseList(Collections.emptyList());
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toRefundResponse Tests")
    class ToRefundResponseTests {

        @Test
        @DisplayName("Should map Refund to RefundResponse with all fields")
        void shouldMapRefundToResponse() {
            Refund refund = createFullRefund();

            RefundResponse response = paymentMapper.toRefundResponse(refund);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(refund.getId());
            assertThat(response.getOrderId()).isEqualTo(refund.getOrderId());
            assertThat(response.getPaymentId()).isEqualTo(refund.getPaymentId());
            assertThat(response.getAmount()).isEqualTo(refund.getAmount());
            assertThat(response.getStatus()).isEqualTo(refund.getStatus());
            assertThat(response.getRefundType()).isEqualTo(refund.getRefundType());
            assertThat(response.getReason()).isEqualTo(refund.getReason());
            assertThat(response.getIsFullRefund()).isEqualTo(refund.isFullRefund());
        }

        @Test
        @DisplayName("Should handle null refund")
        void shouldHandleNullRefund() {
            RefundResponse response = paymentMapper.toRefundResponse(null);
            assertThat(response).isNull();
        }

        @Test
        @DisplayName("Should correctly map full refund flag")
        void shouldMapFullRefundFlag() {
            Refund fullRefund = Refund.builder()
                .id("refund-123")
                .orderId("order-123")
                .originalAmount(new BigDecimal("500.00"))
                .amount(new BigDecimal("500.00"))
                .status(RefundStatus.PROCESSED)
                .build();

            RefundResponse response = paymentMapper.toRefundResponse(fullRefund);

            assertThat(response.getIsFullRefund()).isTrue();
        }

        @Test
        @DisplayName("Should correctly map partial refund flag")
        void shouldMapPartialRefundFlag() {
            Refund partialRefund = Refund.builder()
                .id("refund-123")
                .orderId("order-123")
                .originalAmount(new BigDecimal("500.00"))
                .amount(new BigDecimal("200.00"))
                .status(RefundStatus.PROCESSED)
                .build();

            RefundResponse response = paymentMapper.toRefundResponse(partialRefund);

            assertThat(response.getIsFullRefund()).isFalse();
        }
    }

    @Nested
    @DisplayName("toRefundResponseList Tests")
    class ToRefundResponseListTests {

        @Test
        @DisplayName("Should map list of Refunds to responses")
        void shouldMapRefundList() {
            List<Refund> refunds = List.of(createFullRefund(), createFullRefund());

            List<RefundResponse> responses = paymentMapper.toRefundResponseList(refunds);

            assertThat(responses).hasSize(2);
        }

        @Test
        @DisplayName("Should handle null list")
        void shouldHandleNullList() {
            List<RefundResponse> responses = paymentMapper.toRefundResponseList(null);
            assertThat(responses).isNull();
        }

        @Test
        @DisplayName("Should handle empty list")
        void shouldHandleEmptyList() {
            List<RefundResponse> responses = paymentMapper.toRefundResponseList(Collections.emptyList());
            assertThat(responses).isEmpty();
        }
    }

    // Helper methods
    private Payment createFullPayment() {
        return Payment.builder()
            .id("payment-123")
            .orderId("order-123")
            .userId("user-123")
            .amount(new BigDecimal("599.00"))
            .currency("INR")
            .paymentMethod(PaymentMethod.UPI)
            .status(PaymentStatus.SUCCESS)
            .gatewayOrderId("order_ABC123")
            .transactionId("pay_XYZ789")
            .createdAt(Instant.now())
            .completedAt(Instant.now())
            .build();
    }

    private Refund createFullRefund() {
        return Refund.builder()
            .id("refund-123")
            .orderId("order-123")
            .paymentId("payment-123")
            .userId("user-123")
            .refundType(RefundType.CANCELLATION)
            .amount(new BigDecimal("299.00"))
            .originalAmount(new BigDecimal("599.00"))
            .reason("Order was cancelled before preparation")
            .status(RefundStatus.PROCESSED)
            .gatewayRefundId("rfnd_ABC123")
            .initiatedBy("system")
            .createdAt(Instant.now())
            .processedAt(Instant.now())
            .build();
    }
}
