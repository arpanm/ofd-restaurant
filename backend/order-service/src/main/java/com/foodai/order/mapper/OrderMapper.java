package com.foodai.order.mapper;

import com.foodai.order.domain.model.*;
import com.foodai.order.dto.request.DeliveryAddressDTO;
import com.foodai.order.dto.response.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * MapStruct mapper for Order and related DTOs.
 *
 * @author FoodAI Team
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    /**
     * Maps Order entity to OrderResponse.
     */
    @Mapping(target = "restaurantIds", expression = "java(order.getRestaurantIds())")
    @Mapping(target = "isMultiRestaurantOrder", expression = "java(order.isMultiRestaurantOrder())")
    @Mapping(target = "canCancel", expression = "java(order.canCancel())")
    @Mapping(target = "canRefund", expression = "java(order.canRefund())")
    OrderResponse toResponse(Order order);

    /**
     * Maps list of Orders to list of OrderResponses.
     */
    List<OrderResponse> toResponseList(List<Order> orders);

    /**
     * Maps OrderItem to OrderItemResponse.
     */
    OrderItemResponse toItemResponse(OrderItem item);

    /**
     * Maps list of OrderItems to list of OrderItemResponses.
     */
    List<OrderItemResponse> toItemResponseList(List<OrderItem> items);

    /**
     * Maps OrderItemCustomizationVO to OrderItemCustomizationResponse.
     */
    OrderItemCustomizationResponse toCustomizationResponse(OrderItemCustomizationVO vo);

    /**
     * Maps DeliveryAddressVO to DeliveryAddressResponse.
     */
    @Mapping(target = "fullAddress", expression = "java(address.getFullAddress())")
    DeliveryAddressResponse toAddressResponse(DeliveryAddressVO address);

    /**
     * Maps DeliveryAddressDTO to DeliveryAddressVO.
     */
    DeliveryAddressVO toAddressVO(DeliveryAddressDTO dto);

    /**
     * Maps OrderTotalVO to OrderTotalResponse.
     */
    OrderTotalResponse toTotalResponse(OrderTotalVO total);

    /**
     * Maps OrderTimelineEntryVO to OrderTimelineEntryResponse.
     */
    OrderTimelineEntryResponse toTimelineResponse(OrderTimelineEntryVO entry);

    /**
     * Maps list of timeline entries.
     */
    List<OrderTimelineEntryResponse> toTimelineResponseList(List<OrderTimelineEntryVO> entries);

    /**
     * Maps CartItem to OrderItem.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "customizations", source = "customizations")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "itemStatus", constant = "PENDING")
    OrderItem cartItemToOrderItem(CartItem cartItem);

    /**
     * Maps CartItemCustomization to OrderItemCustomizationVO.
     */
    OrderItemCustomizationVO toOrderItemCustomization(CartItemCustomization cartCustomization);

    /**
     * Maps list of CartItemCustomizations to OrderItemCustomizationVOs.
     */
    List<OrderItemCustomizationVO> toOrderItemCustomizationList(List<CartItemCustomization> customizations);
}

