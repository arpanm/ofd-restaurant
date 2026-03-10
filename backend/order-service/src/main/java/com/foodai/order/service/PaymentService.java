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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 * Service for payment and refund operations.
 *
 * @author FoodAI Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final OrderService orderService;
    private final PaymentMapper paymentMapper;

    // TODO: In production, inject Razorpay client
    // private final RazorpayClient razorpayClient;
    private static final String GATEWAY_KEY_ID = "rzp_test_xxxxx"; // Replace with actual key

    /**
     * Initiates a payment for an order.
     *
     * @param request the payment initiation request
     * @return payment initiation response with gateway details
     */
    public PaymentInitiationResponse initiatePayment(InitiatePaymentRequest request) {
        log.info("Initiating payment", kv("orderId", request.getOrderId()), kv("amount", request.getAmount()));
        
        // Check for existing successful payment
        if (paymentRepository.existsByOrderIdAndStatus(request.getOrderId(), PaymentStatus.SUCCESS)) {
            throw new IllegalStateException("Order already has a successful payment");
        }
        
        // Create gateway order (simulated - in production use Razorpay SDK)
        String gatewayOrderId = "order_" + UUID.randomUUID().toString().substring(0, 12);
        
        // Create payment record
        Payment payment = Payment.builder()
            .orderId(request.getOrderId())
            .userId(request.getUserId())
            .amount(request.getAmount())
            .paymentMethod(request.getPaymentMethod())
            .gatewayOrderId(gatewayOrderId)
            .status(PaymentStatus.PENDING)
            .ipAddress(request.getIpAddress())
            .userAgent(request.getUserAgent())
            .build();
        
        payment.incrementAttempts();
        Payment savedPayment = paymentRepository.save(payment);
        
        log.info("Payment initiated", kv("paymentId", savedPayment.getId()), kv("gatewayOrderId", gatewayOrderId));
        
        return PaymentInitiationResponse.builder()
            .paymentId(savedPayment.getId())
            .gatewayOrderId(gatewayOrderId)
            .amount(request.getAmount())
            .currency("INR")
            .gatewayKeyId(GATEWAY_KEY_ID)
            .orderNumber(request.getOrderId()) // TODO: Get actual order number
            .build();
    }

    /**
     * Verifies a payment from the gateway.
     *
     * @param request the verification request
     * @return payment response
     */
    public PaymentResponse verifyPayment(VerifyPaymentRequest request) {
        log.info("Verifying payment", kv("gatewayOrderId", request.getGatewayOrderId()));
        
        Payment payment = paymentRepository.findByGatewayOrderId(request.getGatewayOrderId())
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found for gateway order: " + request.getGatewayOrderId()));
        
        // Verify signature (simulated - in production use Razorpay SDK)
        boolean isValid = verifyGatewaySignature(request);
        
        if (isValid) {
            payment.markSuccess(request.getPaymentId());
            Payment savedPayment = paymentRepository.save(payment);
            
            // Confirm the order
            orderService.confirmOrder(payment.getOrderId());
            
            log.info("Payment verified successfully", kv("paymentId", payment.getId()));
            return paymentMapper.toResponse(savedPayment);
        } else {
            payment.markFailed("Signature verification failed");
            paymentRepository.save(payment);
            
            log.warn("Payment verification failed", kv("paymentId", payment.getId()));
            throw new PaymentFailedException("Payment verification failed");
        }
    }

    /**
     * Gets a payment by ID.
     *
     * @param paymentId the payment ID
     * @return payment response
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPayment(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found: " + paymentId));
        return paymentMapper.toResponse(payment);
    }

    /**
     * Gets payment by order ID.
     *
     * @param orderId the order ID
     * @return payment response
     */
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(String orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order: " + orderId));
        return paymentMapper.toResponse(payment);
    }

    /**
     * Gets payments for a user.
     *
     * @param userId the user ID
     * @param pageable pagination info
     * @return page of payment responses
     */
    @Transactional(readOnly = true)
    public Page<PaymentResponse> getUserPayments(String userId, Pageable pageable) {
        return paymentRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
            .map(paymentMapper::toResponse);
    }

    /**
     * Initiates a refund.
     *
     * @param request the refund request
     * @return refund response
     */
    public RefundResponse initiateRefund(InitiateRefundRequest request) {
        log.info("Initiating refund", kv("orderId", request.getOrderId()), kv("type", request.getRefundType()));
        
        // Get the payment for this order
        Payment payment = paymentRepository.findByOrderId(request.getOrderId())
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found for order: " + request.getOrderId()));
        
        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new IllegalStateException("Cannot refund a payment that is not successful");
        }
        
        // Calculate refund amount
        BigDecimal refundAmount = request.getAmount();
        if (refundAmount == null || request.getRefundType() == RefundType.FULL) {
            // Full refund - check for existing refunds
            BigDecimal existingRefunds = refundRepository.sumProcessedRefundsByPaymentId(payment.getId());
            if (existingRefunds == null) {
                existingRefunds = BigDecimal.ZERO;
            }
            refundAmount = payment.getAmount().subtract(existingRefunds);
        }
        
        if (refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("No refundable amount remaining");
        }
        
        // Create refund record
        Refund refund = Refund.builder()
            .paymentId(payment.getId())
            .orderId(request.getOrderId())
            .userId(payment.getUserId())
            .amount(refundAmount)
            .originalAmount(payment.getAmount())
            .reason(request.getReason())
            .refundType(request.getRefundType())
            .status(RefundStatus.PENDING)
            .initiatedBy(request.getInitiatedBy())
            .notes(request.getNotes())
            .build();
        
        Refund savedRefund = refundRepository.save(refund);
        
        // Process refund through gateway (simulated)
        processRefundWithGateway(savedRefund, payment.getTransactionId());
        
        // Update payment status
        BigDecimal totalRefunded = refundRepository.sumProcessedRefundsByPaymentId(payment.getId());
        if (totalRefunded != null && totalRefunded.compareTo(payment.getAmount()) >= 0) {
            payment.markRefunded();
        } else {
            payment.markPartiallyRefunded();
        }
        paymentRepository.save(payment);
        
        log.info("Refund initiated", kv("refundId", savedRefund.getId()), kv("amount", refundAmount));
        
        return paymentMapper.toRefundResponse(savedRefund);
    }

    /**
     * Gets a refund by ID.
     *
     * @param refundId the refund ID
     * @return refund response
     */
    @Transactional(readOnly = true)
    public RefundResponse getRefund(String refundId) {
        Refund refund = refundRepository.findById(refundId)
            .orElseThrow(() -> new PaymentNotFoundException("Refund not found: " + refundId));
        return paymentMapper.toRefundResponse(refund);
    }

    /**
     * Gets refunds for an order.
     *
     * @param orderId the order ID
     * @return list of refund responses
     */
    @Transactional(readOnly = true)
    public List<RefundResponse> getRefundsByOrderId(String orderId) {
        List<Refund> refunds = refundRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
        return paymentMapper.toRefundResponseList(refunds);
    }

    /**
     * Gets refunds for a user.
     *
     * @param userId the user ID
     * @param pageable pagination info
     * @return page of refund responses
     */
    @Transactional(readOnly = true)
    public Page<RefundResponse> getUserRefunds(String userId, Pageable pageable) {
        return refundRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
            .map(paymentMapper::toRefundResponse);
    }

    // Private helper methods

    private boolean verifyGatewaySignature(VerifyPaymentRequest request) {
        // TODO: Implement actual Razorpay signature verification
        // In production:
        // String expectedSignature = Utils.getHash(
        //     request.getGatewayOrderId() + "|" + request.getPaymentId(),
        //     razorpaySecret
        // );
        // return expectedSignature.equals(request.getSignature());
        
        // Simulated verification - always return true for testing
        return request.getSignature() != null && !request.getSignature().isEmpty();
    }

    private void processRefundWithGateway(Refund refund, String transactionId) {
        // TODO: Implement actual Razorpay refund
        // In production:
        // JSONObject refundRequest = new JSONObject();
        // refundRequest.put("amount", refund.getAmount().multiply(100).intValue());
        // refundRequest.put("notes", Map.of("reason", refund.getReason()));
        // Refund gatewayRefund = razorpayClient.payments.refund(transactionId, refundRequest);
        
        // Simulated refund processing
        String gatewayRefundId = "rfnd_" + UUID.randomUUID().toString().substring(0, 12);
        refund.markProcessed(gatewayRefundId);
        refundRepository.save(refund);
    }
}

