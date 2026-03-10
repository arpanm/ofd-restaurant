package com.foodai.order.mapper;

import com.foodai.order.domain.model.Payment;
import com.foodai.order.domain.model.Refund;
import com.foodai.order.dto.response.PaymentResponse;
import com.foodai.order.dto.response.RefundResponse;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-10T15:29:35+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class PaymentMapperImpl implements PaymentMapper {

    @Override
    public PaymentResponse toResponse(Payment payment) {
        if ( payment == null ) {
            return null;
        }

        PaymentResponse.PaymentResponseBuilder paymentResponse = PaymentResponse.builder();

        paymentResponse.amount( payment.getAmount() );
        paymentResponse.attempts( payment.getAttempts() );
        paymentResponse.completedAt( payment.getCompletedAt() );
        paymentResponse.createdAt( payment.getCreatedAt() );
        paymentResponse.currency( payment.getCurrency() );
        paymentResponse.failureReason( payment.getFailureReason() );
        paymentResponse.gatewayOrderId( payment.getGatewayOrderId() );
        paymentResponse.id( payment.getId() );
        paymentResponse.orderId( payment.getOrderId() );
        paymentResponse.paymentMethod( payment.getPaymentMethod() );
        paymentResponse.paymentProvider( payment.getPaymentProvider() );
        paymentResponse.status( payment.getStatus() );
        paymentResponse.transactionId( payment.getTransactionId() );
        paymentResponse.userId( payment.getUserId() );

        return paymentResponse.build();
    }

    @Override
    public List<PaymentResponse> toResponseList(List<Payment> payments) {
        if ( payments == null ) {
            return null;
        }

        List<PaymentResponse> list = new ArrayList<PaymentResponse>( payments.size() );
        for ( Payment payment : payments ) {
            list.add( toResponse( payment ) );
        }

        return list;
    }

    @Override
    public RefundResponse toRefundResponse(Refund refund) {
        if ( refund == null ) {
            return null;
        }

        RefundResponse.RefundResponseBuilder refundResponse = RefundResponse.builder();

        refundResponse.amount( refund.getAmount() );
        refundResponse.createdAt( refund.getCreatedAt() );
        refundResponse.failureReason( refund.getFailureReason() );
        refundResponse.gatewayRefundId( refund.getGatewayRefundId() );
        refundResponse.id( refund.getId() );
        refundResponse.initiatedBy( refund.getInitiatedBy() );
        refundResponse.notes( refund.getNotes() );
        refundResponse.orderId( refund.getOrderId() );
        refundResponse.originalAmount( refund.getOriginalAmount() );
        refundResponse.paymentId( refund.getPaymentId() );
        refundResponse.processedAt( refund.getProcessedAt() );
        refundResponse.reason( refund.getReason() );
        refundResponse.refundType( refund.getRefundType() );
        refundResponse.status( refund.getStatus() );
        refundResponse.userId( refund.getUserId() );

        refundResponse.isFullRefund( refund.isFullRefund() );

        return refundResponse.build();
    }

    @Override
    public List<RefundResponse> toRefundResponseList(List<Refund> refunds) {
        if ( refunds == null ) {
            return null;
        }

        List<RefundResponse> list = new ArrayList<RefundResponse>( refunds.size() );
        for ( Refund refund : refunds ) {
            list.add( toRefundResponse( refund ) );
        }

        return list;
    }
}
