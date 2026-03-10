package com.foodai.order.mapper;

import com.foodai.order.domain.model.*;
import com.foodai.order.dto.request.DeliveryAddressDTO;
import com.foodai.order.dto.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for OrderMapper.
 *
 * @author FoodAI Team
 */
@DisplayName("Order Mapper Tests")
class OrderMapperTest {

    private OrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        orderMapper = Mappers.getMapper(OrderMapper.class);
    }

    @Nested
    @DisplayName("toResponse Tests")
    class ToResponseTests {

        @Test
        @DisplayName("Should map Order to OrderResponse with all fields")
        void shouldMapOrderToResponse() {
            Order order = createFullOrder();

            OrderResponse response = orderMapper.toResponse(order);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(order.getId());
            assertThat(response.getOrderNumber()).isEqualTo(order.getOrderNumber());
            assertThat(response.getUserId()).isEqualTo(order.getUserId());
            assertThat(response.getStatus()).isEqualTo(order.getStatus());
            assertThat(response.getOrderType()).isEqualTo(order.getOrderType());
        }

        @Test
        @DisplayName("Should handle null order")
        void shouldHandleNullOrder() {
            OrderResponse response = orderMapper.toResponse(null);
            assertThat(response).isNull();
        }
    }

    @Nested
    @DisplayName("toResponseList Tests")
    class ToResponseListTests {

        @Test
        @DisplayName("Should map list of Orders to responses")
        void shouldMapOrderList() {
            List<Order> orders = List.of(createFullOrder(), createFullOrder());

            List<OrderResponse> responses = orderMapper.toResponseList(orders);

            assertThat(responses).hasSize(2);
        }

        @Test
        @DisplayName("Should handle null list")
        void shouldHandleNullList() {
            List<OrderResponse> responses = orderMapper.toResponseList(null);
            assertThat(responses).isNull();
        }

        @Test
        @DisplayName("Should handle empty list")
        void shouldHandleEmptyList() {
            List<OrderResponse> responses = orderMapper.toResponseList(Collections.emptyList());
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toItemResponse Tests")
    class ToItemResponseTests {

        @Test
        @DisplayName("Should map OrderItem to OrderItemResponse")
        void shouldMapOrderItemToResponse() {
            OrderItem item = createOrderItem();

            OrderItemResponse response = orderMapper.toItemResponse(item);

            assertThat(response).isNotNull();
            assertThat(response.getMenuItemId()).isEqualTo(item.getMenuItemId());
            assertThat(response.getName()).isEqualTo(item.getName());
            assertThat(response.getQuantity()).isEqualTo(item.getQuantity());
        }

        @Test
        @DisplayName("Should handle null item")
        void shouldHandleNullItem() {
            OrderItemResponse response = orderMapper.toItemResponse(null);
            assertThat(response).isNull();
        }
    }

    @Nested
    @DisplayName("toItemResponseList Tests")
    class ToItemResponseListTests {

        @Test
        @DisplayName("Should map list of OrderItems to responses")
        void shouldMapOrderItemList() {
            List<OrderItem> items = List.of(createOrderItem(), createOrderItem());

            List<OrderItemResponse> responses = orderMapper.toItemResponseList(items);

            assertThat(responses).hasSize(2);
        }

        @Test
        @DisplayName("Should handle null list")
        void shouldHandleNullList() {
            List<OrderItemResponse> responses = orderMapper.toItemResponseList(null);
            assertThat(responses).isNull();
        }
    }

    @Nested
    @DisplayName("Address Mapping Tests")
    class AddressMappingTests {

        @Test
        @DisplayName("Should map DeliveryAddressVO to DeliveryAddressResponse")
        void shouldMapAddressVOToResponse() {
            DeliveryAddressVO address = createDeliveryAddressVO();

            DeliveryAddressResponse response = orderMapper.toAddressResponse(address);

            assertThat(response).isNotNull();
            assertThat(response.getRecipientName()).isEqualTo(address.getRecipientName());
            assertThat(response.getStreet()).isEqualTo(address.getStreet());
            assertThat(response.getCity()).isEqualTo(address.getCity());
            assertThat(response.getFullAddress()).isNotNull();
        }

        @Test
        @DisplayName("Should map DeliveryAddressDTO to DeliveryAddressVO")
        void shouldMapAddressDTOToVO() {
            DeliveryAddressDTO dto = DeliveryAddressDTO.builder()
                .recipientName("John Doe")
                .contactPhone("+919876543210")
                .street("MG Road")
                .city("Bangalore")
                .state("Karnataka")
                .pincode("560001")
                .build();

            DeliveryAddressVO vo = orderMapper.toAddressVO(dto);

            assertThat(vo).isNotNull();
            assertThat(vo.getRecipientName()).isEqualTo(dto.getRecipientName());
            assertThat(vo.getStreet()).isEqualTo(dto.getStreet());
            assertThat(vo.getCity()).isEqualTo(dto.getCity());
        }

        @Test
        @DisplayName("Should handle null address")
        void shouldHandleNullAddress() {
            assertThat(orderMapper.toAddressResponse(null)).isNull();
            assertThat(orderMapper.toAddressVO(null)).isNull();
        }
    }

    @Nested
    @DisplayName("OrderTotal Mapping Tests")
    class OrderTotalMappingTests {

        @Test
        @DisplayName("Should map OrderTotalVO to OrderTotalResponse")
        void shouldMapOrderTotalVOToResponse() {
            OrderTotalVO total = OrderTotalVO.builder()
                .subtotal(new BigDecimal("500.00"))
                .gst(new BigDecimal("25.00"))
                .deliveryFee(new BigDecimal("40.00"))
                .discount(new BigDecimal("50.00"))
                .total(new BigDecimal("515.00"))
                .build();

            OrderTotalResponse response = orderMapper.toTotalResponse(total);

            assertThat(response).isNotNull();
            assertThat(response.getSubtotal()).isEqualTo(total.getSubtotal());
            assertThat(response.getTotal()).isEqualTo(total.getTotal());
        }

        @Test
        @DisplayName("Should handle null total")
        void shouldHandleNullTotal() {
            OrderTotalResponse response = orderMapper.toTotalResponse(null);
            assertThat(response).isNull();
        }
    }

    @Nested
    @DisplayName("Timeline Mapping Tests")
    class TimelineMappingTests {

        @Test
        @DisplayName("Should map OrderTimelineEntryVO to response")
        void shouldMapTimelineEntry() {
            OrderTimelineEntryVO entry = OrderTimelineEntryVO.builder()
                .status(OrderStatus.CONFIRMED)
                .timestamp(Instant.now())
                .description("Order confirmed by restaurant")
                .updatedBy("restaurant-123")
                .build();

            OrderTimelineEntryResponse response = orderMapper.toTimelineResponse(entry);

            assertThat(response).isNotNull();
            assertThat(response.getStatus()).isEqualTo(entry.getStatus());
            assertThat(response.getDescription()).isEqualTo(entry.getDescription());
        }

        @Test
        @DisplayName("Should map list of timeline entries")
        void shouldMapTimelineList() {
            OrderTimelineEntryVO entry = OrderTimelineEntryVO.builder()
                .status(OrderStatus.CONFIRMED)
                .timestamp(Instant.now())
                .build();
            List<OrderTimelineEntryVO> entries = List.of(entry);

            List<OrderTimelineEntryResponse> responses = orderMapper.toTimelineResponseList(entries);

            assertThat(responses).hasSize(1);
        }

        @Test
        @DisplayName("Should handle null timeline list")
        void shouldHandleNullTimeline() {
            assertThat(orderMapper.toTimelineResponse(null)).isNull();
            assertThat(orderMapper.toTimelineResponseList(null)).isNull();
        }
    }

    @Nested
    @DisplayName("Cart to Order Item Mapping Tests")
    class CartToOrderItemMappingTests {

        @Test
        @DisplayName("Should map CartItem to OrderItem")
        void shouldMapCartItemToOrderItem() {
            CartItem cartItem = CartItem.builder()
                .menuItemId("menu-123")
                .name("Butter Chicken")
                .restaurantId("restaurant-123")
                .quantity(2)
                .unitPrice(new BigDecimal("350.00"))
                .totalPrice(new BigDecimal("700.00"))
                .customizations(List.of(CartItemCustomization.builder()
                    .customizationId("spice")
                    .customizationName("Spice Level")
                    .optionId("medium")
                    .optionName("Medium")
                    .additionalCost(BigDecimal.ZERO)
                    .build()))
                .build();

            OrderItem orderItem = orderMapper.cartItemToOrderItem(cartItem);

            assertThat(orderItem).isNotNull();
            assertThat(orderItem.getMenuItemId()).isEqualTo(cartItem.getMenuItemId());
            assertThat(orderItem.getName()).isEqualTo(cartItem.getName());
            assertThat(orderItem.getQuantity()).isEqualTo(cartItem.getQuantity());
            assertThat(orderItem.getItemStatus()).isEqualTo(OrderItemStatus.PENDING);
        }

        @Test
        @DisplayName("Should handle null CartItem")
        void shouldHandleNullCartItem() {
            OrderItem orderItem = orderMapper.cartItemToOrderItem(null);
            assertThat(orderItem).isNull();
        }
    }

    @Nested
    @DisplayName("Customization Mapping Tests")
    class CustomizationMappingTests {

        @Test
        @DisplayName("Should map CartItemCustomization to OrderItemCustomizationVO")
        void shouldMapCustomization() {
            CartItemCustomization cartCustomization = CartItemCustomization.builder()
                .customizationId("spice")
                .customizationName("Spice Level")
                .optionId("medium")
                .optionName("Medium")
                .additionalCost(new BigDecimal("10.00"))
                .build();

            OrderItemCustomizationVO orderCustomization = orderMapper.toOrderItemCustomization(cartCustomization);

            assertThat(orderCustomization).isNotNull();
            assertThat(orderCustomization.getCustomizationId()).isEqualTo(cartCustomization.getCustomizationId());
            assertThat(orderCustomization.getOptionId()).isEqualTo(cartCustomization.getOptionId());
        }

        @Test
        @DisplayName("Should map list of customizations")
        void shouldMapCustomizationList() {
            CartItemCustomization customization = CartItemCustomization.builder()
                .customizationId("spice")
                .customizationName("Spice Level")
                .optionId("medium")
                .optionName("Medium")
                .additionalCost(BigDecimal.ZERO)
                .build();

            List<OrderItemCustomizationVO> result = orderMapper.toOrderItemCustomizationList(List.of(customization));

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Should handle null customization")
        void shouldHandleNullCustomization() {
            assertThat(orderMapper.toOrderItemCustomization(null)).isNull();
            assertThat(orderMapper.toOrderItemCustomizationList(null)).isNull();
        }

        @Test
        @DisplayName("Should map OrderItemCustomizationVO to response")
        void shouldMapCustomizationToResponse() {
            OrderItemCustomizationVO vo = OrderItemCustomizationVO.builder()
                .customizationId("spice")
                .customizationName("Spice Level")
                .optionId("medium")
                .optionName("Medium")
                .additionalCost(new BigDecimal("10.00"))
                .build();

            OrderItemCustomizationResponse response = orderMapper.toCustomizationResponse(vo);

            assertThat(response).isNotNull();
            assertThat(response.getCustomizationId()).isEqualTo(vo.getCustomizationId());
            assertThat(response.getOptionId()).isEqualTo(vo.getOptionId());
        }
    }

    // Helper methods
    private Order createFullOrder() {
        return Order.builder()
            .id("order-123")
            .orderNumber("ORD-20250115-001")
            .userId("user-123")
            .orderType(OrderType.SINGLE_RESTAURANT)
            .status(OrderStatus.CREATED)
            .items(new ArrayList<>())
            .deliveryAddress(createDeliveryAddressVO())
            .orderTotal(OrderTotalVO.builder()
                .subtotal(new BigDecimal("500.00"))
                .total(new BigDecimal("550.00"))
                .build())
            .paymentMethod(PaymentMethod.UPI)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }

    private OrderItem createOrderItem() {
        return OrderItem.builder()
            .id("item-123")
            .menuItemId("menu-123")
            .name("Butter Chicken")
            .restaurantId("restaurant-123")
            .quantity(2)
            .unitPrice(new BigDecimal("350.00"))
            .totalPrice(new BigDecimal("700.00"))
            .itemStatus(OrderItemStatus.PENDING)
            .build();
    }

    private DeliveryAddressVO createDeliveryAddressVO() {
        return DeliveryAddressVO.builder()
            .recipientName("John Doe")
            .contactPhone("+919876543210")
            .street("MG Road")
            .city("Bangalore")
            .state("Karnataka")
            .pincode("560001")
            .build();
    }
}
