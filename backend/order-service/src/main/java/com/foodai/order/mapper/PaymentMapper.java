package com.foodai.order.mapper;

import com.foodai.order.domain.model.Payment;
import com.foodai.order.domain.model.Refund;
import com.foodai.order.dto.response.PaymentResponse;
import com.foodai.order.dto.response.RefundResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * MapStruct mapper for Payment and Refund entities.
 *
 * @author FoodAI Team
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    /**
     * Maps Payment entity to PaymentResponse.
     */
    PaymentResponse toResponse(Payment payment);

    /**
     * Maps list of Payments to list of PaymentResponses.
     */
    List<PaymentResponse> toResponseList(List<Payment> payments);

    /**
     * Maps Refund entity to RefundResponse.
     */
    @Mapping(target = "isFullRefund", expression = "java(refund.isFullRefund())")
    RefundResponse toRefundResponse(Refund refund);

    /**
     * Maps list of Refunds to list of RefundResponses.
     */
    List<RefundResponse> toRefundResponseList(List<Refund> refunds);
}

