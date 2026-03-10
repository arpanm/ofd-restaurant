package com.foodai.order.mapper;

import com.foodai.order.domain.model.CartItem;
import com.foodai.order.domain.model.CartItemCustomization;
import com.foodai.order.domain.model.DeliveryAddressVO;
import com.foodai.order.domain.model.Order;
import com.foodai.order.domain.model.OrderItem;
import com.foodai.order.domain.model.OrderItemCustomizationVO;
import com.foodai.order.domain.model.OrderItemStatus;
import com.foodai.order.domain.model.OrderTimelineEntryVO;
import com.foodai.order.domain.model.OrderTotalVO;
import com.foodai.order.dto.request.DeliveryAddressDTO;
import com.foodai.order.dto.response.DeliveryAddressResponse;
import com.foodai.order.dto.response.OrderItemCustomizationResponse;
import com.foodai.order.dto.response.OrderItemResponse;
import com.foodai.order.dto.response.OrderResponse;
import com.foodai.order.dto.response.OrderTimelineEntryResponse;
import com.foodai.order.dto.response.OrderTotalResponse;
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
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderResponse toResponse(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderResponse.OrderResponseBuilder orderResponse = OrderResponse.builder();

        orderResponse.actualDeliveryTime( order.getActualDeliveryTime() );
        orderResponse.cancellationReason( order.getCancellationReason() );
        orderResponse.createdAt( order.getCreatedAt() );
        orderResponse.customerRating( order.getCustomerRating() );
        orderResponse.customerReview( order.getCustomerReview() );
        orderResponse.deliveryAddress( toAddressResponse( order.getDeliveryAddress() ) );
        orderResponse.deliveryInstructions( order.getDeliveryInstructions() );
        orderResponse.dietPlanId( order.getDietPlanId() );
        orderResponse.estimatedDeliveryTime( order.getEstimatedDeliveryTime() );
        orderResponse.id( order.getId() );
        orderResponse.items( toItemResponseList( order.getItems() ) );
        orderResponse.loyaltyPointsEarned( order.getLoyaltyPointsEarned() );
        orderResponse.orderNumber( order.getOrderNumber() );
        orderResponse.orderTotal( toTotalResponse( order.getOrderTotal() ) );
        orderResponse.orderType( order.getOrderType() );
        orderResponse.partyPlanId( order.getPartyPlanId() );
        orderResponse.paymentMethod( order.getPaymentMethod() );
        orderResponse.paymentStatus( order.getPaymentStatus() );
        orderResponse.reviewed( order.getReviewed() );
        orderResponse.riderId( order.getRiderId() );
        orderResponse.riderName( order.getRiderName() );
        orderResponse.riderPhone( order.getRiderPhone() );
        orderResponse.scheduledDeliveryTime( order.getScheduledDeliveryTime() );
        orderResponse.specialInstructions( order.getSpecialInstructions() );
        orderResponse.status( order.getStatus() );
        orderResponse.timeline( toTimelineResponseList( order.getTimeline() ) );
        orderResponse.updatedAt( order.getUpdatedAt() );
        orderResponse.userId( order.getUserId() );

        orderResponse.restaurantIds( order.getRestaurantIds() );
        orderResponse.isMultiRestaurantOrder( order.isMultiRestaurantOrder() );
        orderResponse.canCancel( order.canCancel() );
        orderResponse.canRefund( order.canRefund() );

        return orderResponse.build();
    }

    @Override
    public List<OrderResponse> toResponseList(List<Order> orders) {
        if ( orders == null ) {
            return null;
        }

        List<OrderResponse> list = new ArrayList<OrderResponse>( orders.size() );
        for ( Order order : orders ) {
            list.add( toResponse( order ) );
        }

        return list;
    }

    @Override
    public OrderItemResponse toItemResponse(OrderItem item) {
        if ( item == null ) {
            return null;
        }

        OrderItemResponse.OrderItemResponseBuilder orderItemResponse = OrderItemResponse.builder();

        orderItemResponse.customizations( orderItemCustomizationVOListToOrderItemCustomizationResponseList( item.getCustomizations() ) );
        orderItemResponse.description( item.getDescription() );
        orderItemResponse.id( item.getId() );
        orderItemResponse.imageUrl( item.getImageUrl() );
        orderItemResponse.itemStatus( item.getItemStatus() );
        orderItemResponse.menuItemId( item.getMenuItemId() );
        orderItemResponse.name( item.getName() );
        orderItemResponse.quantity( item.getQuantity() );
        orderItemResponse.restaurantId( item.getRestaurantId() );
        orderItemResponse.restaurantName( item.getRestaurantName() );
        orderItemResponse.specialInstructions( item.getSpecialInstructions() );
        orderItemResponse.spiceLevel( item.getSpiceLevel() );
        orderItemResponse.totalPrice( item.getTotalPrice() );
        orderItemResponse.unitPrice( item.getUnitPrice() );
        orderItemResponse.vegetarian( item.getVegetarian() );

        return orderItemResponse.build();
    }

    @Override
    public List<OrderItemResponse> toItemResponseList(List<OrderItem> items) {
        if ( items == null ) {
            return null;
        }

        List<OrderItemResponse> list = new ArrayList<OrderItemResponse>( items.size() );
        for ( OrderItem orderItem : items ) {
            list.add( toItemResponse( orderItem ) );
        }

        return list;
    }

    @Override
    public OrderItemCustomizationResponse toCustomizationResponse(OrderItemCustomizationVO vo) {
        if ( vo == null ) {
            return null;
        }

        OrderItemCustomizationResponse.OrderItemCustomizationResponseBuilder orderItemCustomizationResponse = OrderItemCustomizationResponse.builder();

        orderItemCustomizationResponse.additionalCost( vo.getAdditionalCost() );
        orderItemCustomizationResponse.customizationId( vo.getCustomizationId() );
        orderItemCustomizationResponse.customizationName( vo.getCustomizationName() );
        orderItemCustomizationResponse.optionId( vo.getOptionId() );
        orderItemCustomizationResponse.optionName( vo.getOptionName() );

        return orderItemCustomizationResponse.build();
    }

    @Override
    public DeliveryAddressResponse toAddressResponse(DeliveryAddressVO address) {
        if ( address == null ) {
            return null;
        }

        DeliveryAddressResponse.DeliveryAddressResponseBuilder deliveryAddressResponse = DeliveryAddressResponse.builder();

        deliveryAddressResponse.area( address.getArea() );
        deliveryAddressResponse.building( address.getBuilding() );
        deliveryAddressResponse.city( address.getCity() );
        deliveryAddressResponse.contactPhone( address.getContactPhone() );
        deliveryAddressResponse.deliveryInstructions( address.getDeliveryInstructions() );
        deliveryAddressResponse.flatNumber( address.getFlatNumber() );
        deliveryAddressResponse.label( address.getLabel() );
        deliveryAddressResponse.landmark( address.getLandmark() );
        deliveryAddressResponse.latitude( address.getLatitude() );
        deliveryAddressResponse.longitude( address.getLongitude() );
        deliveryAddressResponse.pincode( address.getPincode() );
        deliveryAddressResponse.recipientName( address.getRecipientName() );
        deliveryAddressResponse.state( address.getState() );
        deliveryAddressResponse.street( address.getStreet() );

        deliveryAddressResponse.fullAddress( address.getFullAddress() );

        return deliveryAddressResponse.build();
    }

    @Override
    public DeliveryAddressVO toAddressVO(DeliveryAddressDTO dto) {
        if ( dto == null ) {
            return null;
        }

        DeliveryAddressVO.DeliveryAddressVOBuilder deliveryAddressVO = DeliveryAddressVO.builder();

        deliveryAddressVO.area( dto.getArea() );
        deliveryAddressVO.building( dto.getBuilding() );
        deliveryAddressVO.city( dto.getCity() );
        deliveryAddressVO.contactPhone( dto.getContactPhone() );
        deliveryAddressVO.deliveryInstructions( dto.getDeliveryInstructions() );
        deliveryAddressVO.flatNumber( dto.getFlatNumber() );
        deliveryAddressVO.label( dto.getLabel() );
        deliveryAddressVO.landmark( dto.getLandmark() );
        deliveryAddressVO.latitude( dto.getLatitude() );
        deliveryAddressVO.longitude( dto.getLongitude() );
        deliveryAddressVO.pincode( dto.getPincode() );
        deliveryAddressVO.recipientName( dto.getRecipientName() );
        deliveryAddressVO.state( dto.getState() );
        deliveryAddressVO.street( dto.getStreet() );

        return deliveryAddressVO.build();
    }

    @Override
    public OrderTotalResponse toTotalResponse(OrderTotalVO total) {
        if ( total == null ) {
            return null;
        }

        OrderTotalResponse.OrderTotalResponseBuilder orderTotalResponse = OrderTotalResponse.builder();

        orderTotalResponse.couponCode( total.getCouponCode() );
        orderTotalResponse.currency( total.getCurrency() );
        orderTotalResponse.deliveryFee( total.getDeliveryFee() );
        orderTotalResponse.discount( total.getDiscount() );
        orderTotalResponse.gst( total.getGst() );
        orderTotalResponse.gstPercentage( total.getGstPercentage() );
        orderTotalResponse.loyaltyPointsValue( total.getLoyaltyPointsValue() );
        orderTotalResponse.packagingCharges( total.getPackagingCharges() );
        orderTotalResponse.platformFee( total.getPlatformFee() );
        orderTotalResponse.subtotal( total.getSubtotal() );
        orderTotalResponse.tip( total.getTip() );
        orderTotalResponse.total( total.getTotal() );

        return orderTotalResponse.build();
    }

    @Override
    public OrderTimelineEntryResponse toTimelineResponse(OrderTimelineEntryVO entry) {
        if ( entry == null ) {
            return null;
        }

        OrderTimelineEntryResponse.OrderTimelineEntryResponseBuilder orderTimelineEntryResponse = OrderTimelineEntryResponse.builder();

        orderTimelineEntryResponse.description( entry.getDescription() );
        orderTimelineEntryResponse.notes( entry.getNotes() );
        orderTimelineEntryResponse.status( entry.getStatus() );
        orderTimelineEntryResponse.timestamp( entry.getTimestamp() );
        orderTimelineEntryResponse.updatedBy( entry.getUpdatedBy() );

        return orderTimelineEntryResponse.build();
    }

    @Override
    public List<OrderTimelineEntryResponse> toTimelineResponseList(List<OrderTimelineEntryVO> entries) {
        if ( entries == null ) {
            return null;
        }

        List<OrderTimelineEntryResponse> list = new ArrayList<OrderTimelineEntryResponse>( entries.size() );
        for ( OrderTimelineEntryVO orderTimelineEntryVO : entries ) {
            list.add( toTimelineResponse( orderTimelineEntryVO ) );
        }

        return list;
    }

    @Override
    public OrderItem cartItemToOrderItem(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }

        OrderItem.OrderItemBuilder orderItem = OrderItem.builder();

        orderItem.customizations( toOrderItemCustomizationList( cartItem.getCustomizations() ) );
        orderItem.description( cartItem.getDescription() );
        orderItem.imageUrl( cartItem.getImageUrl() );
        orderItem.menuItemId( cartItem.getMenuItemId() );
        orderItem.name( cartItem.getName() );
        orderItem.quantity( cartItem.getQuantity() );
        orderItem.restaurantId( cartItem.getRestaurantId() );
        orderItem.restaurantName( cartItem.getRestaurantName() );
        orderItem.specialInstructions( cartItem.getSpecialInstructions() );
        orderItem.spiceLevel( cartItem.getSpiceLevel() );
        orderItem.totalPrice( cartItem.getTotalPrice() );
        orderItem.unitPrice( cartItem.getUnitPrice() );
        orderItem.vegetarian( cartItem.getVegetarian() );

        orderItem.itemStatus( OrderItemStatus.PENDING );

        return orderItem.build();
    }

    @Override
    public OrderItemCustomizationVO toOrderItemCustomization(CartItemCustomization cartCustomization) {
        if ( cartCustomization == null ) {
            return null;
        }

        OrderItemCustomizationVO.OrderItemCustomizationVOBuilder orderItemCustomizationVO = OrderItemCustomizationVO.builder();

        orderItemCustomizationVO.additionalCost( cartCustomization.getAdditionalCost() );
        orderItemCustomizationVO.customizationId( cartCustomization.getCustomizationId() );
        orderItemCustomizationVO.customizationName( cartCustomization.getCustomizationName() );
        orderItemCustomizationVO.optionId( cartCustomization.getOptionId() );
        orderItemCustomizationVO.optionName( cartCustomization.getOptionName() );

        return orderItemCustomizationVO.build();
    }

    @Override
    public List<OrderItemCustomizationVO> toOrderItemCustomizationList(List<CartItemCustomization> customizations) {
        if ( customizations == null ) {
            return null;
        }

        List<OrderItemCustomizationVO> list = new ArrayList<OrderItemCustomizationVO>( customizations.size() );
        for ( CartItemCustomization cartItemCustomization : customizations ) {
            list.add( toOrderItemCustomization( cartItemCustomization ) );
        }

        return list;
    }

    protected List<OrderItemCustomizationResponse> orderItemCustomizationVOListToOrderItemCustomizationResponseList(List<OrderItemCustomizationVO> list) {
        if ( list == null ) {
            return null;
        }

        List<OrderItemCustomizationResponse> list1 = new ArrayList<OrderItemCustomizationResponse>( list.size() );
        for ( OrderItemCustomizationVO orderItemCustomizationVO : list ) {
            list1.add( toCustomizationResponse( orderItemCustomizationVO ) );
        }

        return list1;
    }
}
