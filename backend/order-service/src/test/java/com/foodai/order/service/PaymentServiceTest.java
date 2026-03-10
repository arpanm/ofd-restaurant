package com.foodai.order.service;

import com.foodai.order.domain.model.*;
import com.foodai.order.domain.repository.PaymentRepository;
import com.foodai.order.domain.repository.RefundRepository;
import com.foodai.order.dto.request.InitiatePaymentRequest;
import com.foodai.order.dto.request.InitiateRefundRequest;
import com.foodai.order.dto.request.VerifyPaymentRequest;
import com.foodai.order.dto.response.PaymentInitiationResponse;
import com.foodai.order.dto.response.PaymentResponse;
import com.foodai.order.dto.response.RefundResponse;
import com.foodai.order.exception.PaymentFailedException;
import com.foodai.order.exception.PaymentNotFoundException;
import com.foodai.order.mapper.PaymentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PaymentService.
 *
 * @author FoodAI Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Payment Service Tests")
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RefundRepository refundRepository;

    @Mock
    private OrderService orderService;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    @Captor
    private ArgumentCaptor<Payment> paymentCaptor;

    @Captor
    private ArgumentCaptor<Refund> refundCaptor;

    private final String ORDER_ID = "order-123";
    private final String USER_ID = "user-123";
    private final String PAYMENT_ID = "payment-123";

    @Nested
    @DisplayName("Initiate Payment Tests")
    class InitiatePaymentTests {

        @Test
        @DisplayName("Should initiate payment successfully")
        void shouldInitiatePayment() {
            InitiatePaymentRequest request = InitiatePaymentRequest.builder()
                .orderId(ORDER_ID)
                .userId(USER_ID)
                .amount(new BigDecimal("599.00"))
                .paymentMethod(PaymentMethod.UPI)
                .build();

            when(paymentRepository.existsByOrderIdAndStatus(ORDER_ID, PaymentStatus.SUCCESS)).thenReturn(false);
            when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
                Payment payment = invocation.getArgument(0);
                payment.setId(PAYMENT_ID);
                return payment;
            });

            PaymentInitiationResponse response = paymentService.initiatePayment(request);

            assertThat(response).isNotNull();
            assertThat(response.getPaymentId()).isEqualTo(PAYMENT_ID);
            assertThat(response.getAmount()).isEqualTo(request.getAmount());
            assertThat(response.getCurrency()).isEqualTo("INR");
            assertThat(response.getGatewayOrderId()).startsWith("order_");

            verify(paymentRepository).save(paymentCaptor.capture());
            Payment savedPayment = paymentCaptor.getValue();
            assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(savedPayment.getAttempts()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should throw exception when order already has successful payment")
        void shouldThrowWhenOrderHasSuccessfulPayment() {
            InitiatePaymentRequest request = InitiatePaymentRequest.builder()
                .orderId(ORDER_ID)
                .userId(USER_ID)
                .amount(new BigDecimal("599.00"))
                .paymentMethod(PaymentMethod.UPI)
                .build();

            when(paymentRepository.existsByOrderIdAndStatus(ORDER_ID, PaymentStatus.SUCCESS)).thenReturn(true);

            assertThatThrownBy(() -> paymentService.initiatePayment(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already has a successful payment");
        }
    }

    @Nested
    @DisplayName("Verify Payment Tests")
    class VerifyPaymentTests {

        @Test
        @DisplayName("Should verify payment successfully")
        void shouldVerifyPayment() {
            VerifyPaymentRequest request = VerifyPaymentRequest.builder()
                .gatewayOrderId("order_ABC123")
                .paymentId("pay_XYZ789")
                .signature("valid_signature")
                .build();

            Payment payment = createPendingPayment();
            PaymentResponse expectedResponse = PaymentResponse.builder()
                .id(PAYMENT_ID)
                .status(PaymentStatus.SUCCESS)
                .build();

            when(paymentRepository.findByGatewayOrderId(request.getGatewayOrderId()))
                .thenReturn(Optional.of(payment));
            when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
            when(paymentMapper.toResponse(any(Payment.class))).thenReturn(expectedResponse);

            PaymentResponse response = paymentService.verifyPayment(request);

            assertThat(response.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
            verify(orderService).confirmOrder(ORDER_ID);
        }

        @Test
        @DisplayName("Should throw exception when payment not found")
        void shouldThrowWhenPaymentNotFound() {
            VerifyPaymentRequest request = VerifyPaymentRequest.builder()
                .gatewayOrderId("order_ABC123")
                .paymentId("pay_XYZ789")
                .signature("valid_signature")
                .build();

            when(paymentRepository.findByGatewayOrderId(request.getGatewayOrderId()))
                .thenReturn(Optional.empty());

            assertThatThrownBy(() -> paymentService.verifyPayment(request))
                .isInstanceOf(PaymentNotFoundException.class);
        }

        @Test
        @DisplayName("Should fail verification with invalid signature")
        void shouldFailWithInvalidSignature() {
            VerifyPaymentRequest request = VerifyPaymentRequest.builder()
                .gatewayOrderId("order_ABC123")
                .paymentId("pay_XYZ789")
                .signature("")  // Empty signature
                .build();

            Payment payment = createPendingPayment();

            when(paymentRepository.findByGatewayOrderId(request.getGatewayOrderId()))
                .thenReturn(Optional.of(payment));
            when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

            assertThatThrownBy(() -> paymentService.verifyPayment(request))
                .isInstanceOf(PaymentFailedException.class);
        }
    }

    @Nested
    @DisplayName("Get Payment Tests")
    class GetPaymentTests {

        @Test
        @DisplayName("Should get payment by ID")
        void shouldGetPaymentById() {
            Payment payment = createSuccessfulPayment();
            PaymentResponse expectedResponse = PaymentResponse.builder()
                .id(PAYMENT_ID)
                .orderId(ORDER_ID)
                .build();

            when(paymentRepository.findById(PAYMENT_ID)).thenReturn(Optional.of(payment));
            when(paymentMapper.toResponse(payment)).thenReturn(expectedResponse);

            PaymentResponse response = paymentService.getPayment(PAYMENT_ID);

            assertThat(response.getId()).isEqualTo(PAYMENT_ID);
        }

        @Test
        @DisplayName("Should throw when payment not found")
        void shouldThrowWhenNotFound() {
            when(paymentRepository.findById(PAYMENT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> paymentService.getPayment(PAYMENT_ID))
                .isInstanceOf(PaymentNotFoundException.class);
        }

        @Test
        @DisplayName("Should get payment by order ID")
        void shouldGetPaymentByOrderId() {
            Payment payment = createSuccessfulPayment();
            PaymentResponse expectedResponse = PaymentResponse.builder()
                .id(PAYMENT_ID)
                .orderId(ORDER_ID)
                .build();

            when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(payment));
            when(paymentMapper.toResponse(payment)).thenReturn(expectedResponse);

            PaymentResponse response = paymentService.getPaymentByOrderId(ORDER_ID);

            assertThat(response.getOrderId()).isEqualTo(ORDER_ID);
        }
    }

    @Nested
    @DisplayName("Get User Payments Tests")
    class GetUserPaymentsTests {

        @Test
        @DisplayName("Should get user payments with pagination")
        void shouldGetUserPayments() {
            Pageable pageable = PageRequest.of(0, 20);
            Payment payment = createSuccessfulPayment();
            Page<Payment> paymentPage = new PageImpl<>(List.of(payment), pageable, 1);
            PaymentResponse paymentResponse = PaymentResponse.builder()
                .id(PAYMENT_ID)
                .userId(USER_ID)
                .build();

            when(paymentRepository.findByUserIdOrderByCreatedAtDesc(USER_ID, pageable))
                .thenReturn(paymentPage);
            when(paymentMapper.toResponse(any(Payment.class))).thenReturn(paymentResponse);

            Page<PaymentResponse> result = paymentService.getUserPayments(USER_ID, pageable);

            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Initiate Refund Tests")
    class InitiateRefundTests {

        @Test
        @DisplayName("Should initiate full refund")
        void shouldInitiateFullRefund() {
            InitiateRefundRequest request = InitiateRefundRequest.builder()
                .orderId(ORDER_ID)
                .refundType(RefundType.FULL)
                .reason("Order cancelled")
                .initiatedBy("system")
                .build();

            Payment payment = createSuccessfulPayment();
            RefundResponse expectedResponse = RefundResponse.builder()
                .id("refund-123")
                .orderId(ORDER_ID)
                .amount(payment.getAmount())
                .status(RefundStatus.PROCESSED)
                .build();

            when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(payment));
            when(refundRepository.sumProcessedRefundsByPaymentId(PAYMENT_ID)).thenReturn(BigDecimal.ZERO);
            when(refundRepository.save(any(Refund.class))).thenAnswer(invocation -> {
                Refund refund = invocation.getArgument(0);
                refund.setId("refund-123");
                return refund;
            });
            when(paymentMapper.toRefundResponse(any(Refund.class))).thenReturn(expectedResponse);

            RefundResponse response = paymentService.initiateRefund(request);

            assertThat(response.getAmount()).isEqualTo(payment.getAmount());
            verify(refundRepository, times(2)).save(any(Refund.class));
        }

        @Test
        @DisplayName("Should initiate partial refund")
        void shouldInitiatePartialRefund() {
            BigDecimal refundAmount = new BigDecimal("200.00");
            InitiateRefundRequest request = InitiateRefundRequest.builder()
                .orderId(ORDER_ID)
                .refundType(RefundType.PARTIAL)
                .amount(refundAmount)
                .reason("Partial order issue")
                .initiatedBy("admin")
                .build();

            Payment payment = createSuccessfulPayment();
            RefundResponse expectedResponse = RefundResponse.builder()
                .id("refund-123")
                .orderId(ORDER_ID)
                .amount(refundAmount)
                .status(RefundStatus.PROCESSED)
                .build();

            when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(payment));
            when(refundRepository.save(any(Refund.class))).thenAnswer(invocation -> {
                Refund refund = invocation.getArgument(0);
                refund.setId("refund-123");
                return refund;
            });
            when(refundRepository.sumProcessedRefundsByPaymentId(PAYMENT_ID)).thenReturn(refundAmount);
            when(paymentMapper.toRefundResponse(any(Refund.class))).thenReturn(expectedResponse);

            RefundResponse response = paymentService.initiateRefund(request);

            assertThat(response).isNotNull();
            verify(paymentRepository).save(paymentCaptor.capture());
            Payment savedPayment = paymentCaptor.getValue();
            assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.PARTIALLY_REFUNDED);
        }

        @Test
        @DisplayName("Should throw when payment not found for refund")
        void shouldThrowWhenPaymentNotFoundForRefund() {
            InitiateRefundRequest request = InitiateRefundRequest.builder()
                .orderId(ORDER_ID)
                .refundType(RefundType.FULL)
                .reason("Order cancelled")
                .initiatedBy("system")
                .build();

            when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> paymentService.initiateRefund(request))
                .isInstanceOf(PaymentNotFoundException.class);
        }

        @Test
        @DisplayName("Should throw when payment is not successful")
        void shouldThrowWhenPaymentNotSuccessful() {
            InitiateRefundRequest request = InitiateRefundRequest.builder()
                .orderId(ORDER_ID)
                .refundType(RefundType.FULL)
                .reason("Order cancelled")
                .initiatedBy("system")
                .build();

            Payment payment = createPendingPayment();

            when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(payment));

            assertThatThrownBy(() -> paymentService.initiateRefund(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("not successful");
        }

        @Test
        @DisplayName("Should throw when no refundable amount")
        void shouldThrowWhenNoRefundableAmount() {
            InitiateRefundRequest request = InitiateRefundRequest.builder()
                .orderId(ORDER_ID)
                .refundType(RefundType.FULL)
                .reason("Order cancelled")
                .initiatedBy("system")
                .build();

            Payment payment = createSuccessfulPayment();

            when(paymentRepository.findByOrderId(ORDER_ID)).thenReturn(Optional.of(payment));
            when(refundRepository.sumProcessedRefundsByPaymentId(PAYMENT_ID))
                .thenReturn(payment.getAmount()); // Already fully refunded

            assertThatThrownBy(() -> paymentService.initiateRefund(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No refundable amount");
        }
    }

    @Nested
    @DisplayName("Get Refund Tests")
    class GetRefundTests {

        @Test
        @DisplayName("Should get refund by ID")
        void shouldGetRefundById() {
            Refund refund = createRefund();
            RefundResponse expectedResponse = RefundResponse.builder()
                .id("refund-123")
                .orderId(ORDER_ID)
                .build();

            when(refundRepository.findById("refund-123")).thenReturn(Optional.of(refund));
            when(paymentMapper.toRefundResponse(refund)).thenReturn(expectedResponse);

            RefundResponse response = paymentService.getRefund("refund-123");

            assertThat(response.getId()).isEqualTo("refund-123");
        }

        @Test
        @DisplayName("Should throw when refund not found")
        void shouldThrowWhenRefundNotFound() {
            when(refundRepository.findById("refund-123")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> paymentService.getRefund("refund-123"))
                .isInstanceOf(PaymentNotFoundException.class);
        }

        @Test
        @DisplayName("Should get refunds by order ID")
        void shouldGetRefundsByOrderId() {
            Refund refund = createRefund();
            RefundResponse expectedResponse = RefundResponse.builder()
                .id("refund-123")
                .orderId(ORDER_ID)
                .build();

            when(refundRepository.findByOrderIdOrderByCreatedAtDesc(ORDER_ID))
                .thenReturn(List.of(refund));
            when(paymentMapper.toRefundResponseList(anyList()))
                .thenReturn(List.of(expectedResponse));

            List<RefundResponse> responses = paymentService.getRefundsByOrderId(ORDER_ID);

            assertThat(responses).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Get User Refunds Tests")
    class GetUserRefundsTests {

        @Test
        @DisplayName("Should get user refunds with pagination")
        void shouldGetUserRefunds() {
            Pageable pageable = PageRequest.of(0, 20);
            Refund refund = createRefund();
            Page<Refund> refundPage = new PageImpl<>(List.of(refund), pageable, 1);
            RefundResponse refundResponse = RefundResponse.builder()
                .id("refund-123")
                .userId(USER_ID)
                .build();

            when(refundRepository.findByUserIdOrderByCreatedAtDesc(USER_ID, pageable))
                .thenReturn(refundPage);
            when(paymentMapper.toRefundResponse(any(Refund.class))).thenReturn(refundResponse);

            Page<RefundResponse> result = paymentService.getUserRefunds(USER_ID, pageable);

            assertThat(result.getContent()).hasSize(1);
        }
    }

    // Helper methods
    private Payment createPendingPayment() {
        return Payment.builder()
            .id(PAYMENT_ID)
            .orderId(ORDER_ID)
            .userId(USER_ID)
            .amount(new BigDecimal("599.00"))
            .paymentMethod(PaymentMethod.UPI)
            .status(PaymentStatus.PENDING)
            .gatewayOrderId("order_ABC123")
            .createdAt(Instant.now())
            .build();
    }

    private Payment createSuccessfulPayment() {
        return Payment.builder()
            .id(PAYMENT_ID)
            .orderId(ORDER_ID)
            .userId(USER_ID)
            .amount(new BigDecimal("599.00"))
            .paymentMethod(PaymentMethod.UPI)
            .status(PaymentStatus.SUCCESS)
            .gatewayOrderId("order_ABC123")
            .transactionId("pay_XYZ789")
            .createdAt(Instant.now())
            .completedAt(Instant.now())
            .build();
    }

    private Refund createRefund() {
        return Refund.builder()
            .id("refund-123")
            .orderId(ORDER_ID)
            .paymentId(PAYMENT_ID)
            .userId(USER_ID)
            .amount(new BigDecimal("299.00"))
            .originalAmount(new BigDecimal("599.00"))
            .refundType(RefundType.CANCELLATION)
            .status(RefundStatus.PROCESSED)
            .reason("Order cancelled")
            .initiatedBy("system")
            .createdAt(Instant.now())
            .build();
    }
}

