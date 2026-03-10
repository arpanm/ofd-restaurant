package com.foodai.order.service;

import com.foodai.order.domain.model.*;
import com.foodai.order.domain.repository.OrderRepository;
import com.foodai.order.dto.request.*;
import com.foodai.order.dto.response.OrderResponse;
import com.foodai.order.exception.InvalidOrderStateException;
import com.foodai.order.exception.OrderNotFoundException;
import com.foodai.order.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderService.
 *
 * @author FoodAI Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Order Service Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderResponse orderResponse;
    private Cart cart;
    private final String USER_ID = "user-123";
    private final String ORDER_ID = "order-123";

    @BeforeEach
    void setUp() {
        order = Order.builder()
            .id(ORDER_ID)
            .orderNumber("ORD-20250115-001")
            .userId(USER_ID)
            .orderType(OrderType.SINGLE_RESTAURANT)
            .status(OrderStatus.CREATED)
            .paymentStatus(PaymentStatus.PENDING)
            .items(new ArrayList<>())
            .timeline(new ArrayList<>())
            .build();

        orderResponse = OrderResponse.builder()
            .id(ORDER_ID)
            .orderNumber("ORD-20250115-001")
            .userId(USER_ID)
            .orderType(OrderType.SINGLE_RESTAURANT)
            .status(OrderStatus.CREATED)
            .build();

        cart = Cart.builder()
            .id("cart-123")
            .userId(USER_ID)
            .items(new ArrayList<>())
            .build();
    }

    @Nested
    @DisplayName("Create Order Tests")
    class CreateOrderTests {

        @Test
        @DisplayName("Should create order from cart")
        void shouldCreateOrderFromCart() {
            CartItem cartItem = CartItem.builder()
                .menuItemId("menu-123")
                .name("Chicken Biryani")
                .restaurantId("rest-123")
                .restaurantName("Biryani Blues")
                .unitPrice(new BigDecimal("299.00"))
                .quantity(2)
                .totalPrice(new BigDecimal("598.00"))
                .build();
            cart.getItems().add(cartItem);

            OrderItem orderItem = OrderItem.builder()
                .menuItemId("menu-123")
                .name("Chicken Biryani")
                .unitPrice(new BigDecimal("299.00"))
                .quantity(2)
                .totalPrice(new BigDecimal("598.00"))
                .build();

            CreateOrderRequest request = CreateOrderRequest.builder()
                .userId(USER_ID)
                .orderType(OrderType.SINGLE_RESTAURANT)
                .deliveryAddress(createDeliveryAddress())
                .paymentMethod(PaymentMethod.UPI)
                .build();

            DeliveryAddressVO addressVO = DeliveryAddressVO.builder()
                .recipientName("John Doe")
                .city("Bangalore")
                .build();

            when(cartService.getCartEntity(USER_ID)).thenReturn(cart);
            when(orderMapper.toAddressVO(any())).thenReturn(addressVO);
            when(orderMapper.cartItemToOrderItem(any())).thenReturn(orderItem);
            when(orderRepository.save(any(Order.class))).thenReturn(order);
            when(orderMapper.toResponse(order)).thenReturn(orderResponse);

            OrderResponse result = orderService.createOrder(request);

            assertNotNull(result);
            verify(cartService).deleteCart(USER_ID);
            verify(orderRepository).save(any(Order.class));
        }

        @Test
        @DisplayName("Should throw when cart is empty")
        void shouldThrowWhenCartIsEmpty() {
            CreateOrderRequest request = CreateOrderRequest.builder()
                .userId(USER_ID)
                .orderType(OrderType.SINGLE_RESTAURANT)
                .deliveryAddress(createDeliveryAddress())
                .paymentMethod(PaymentMethod.UPI)
                .build();

            when(cartService.getCartEntity(USER_ID)).thenReturn(cart);

            assertThrows(IllegalStateException.class,
                () -> orderService.createOrder(request));
        }
    }

    @Nested
    @DisplayName("Get Order Tests")
    class GetOrderTests {

        @Test
        @DisplayName("Should get order by ID")
        void shouldGetOrderById() {
            when(orderRepository.findByIdAndDeletedFalse(ORDER_ID)).thenReturn(Optional.of(order));
            when(orderMapper.toResponse(order)).thenReturn(orderResponse);

            OrderResponse result = orderService.getOrder(ORDER_ID);

            assertNotNull(result);
            assertEquals(ORDER_ID, result.getId());
        }

        @Test
        @DisplayName("Should throw when order not found")
        void shouldThrowWhenOrderNotFound() {
            when(orderRepository.findByIdAndDeletedFalse(ORDER_ID)).thenReturn(Optional.empty());

            assertThrows(OrderNotFoundException.class,
                () -> orderService.getOrder(ORDER_ID));
        }

        @Test
        @DisplayName("Should get orders by user ID")
        void shouldGetOrdersByUserId() {
            Page<Order> orderPage = new PageImpl<>(List.of(order));
            Pageable pageable = PageRequest.of(0, 20);

            when(orderRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(USER_ID, pageable))
                .thenReturn(orderPage);
            when(orderMapper.toResponse(order)).thenReturn(orderResponse);

            Page<OrderResponse> result = orderService.getUserOrders(USER_ID, pageable);

            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("Update Status Tests")
    class UpdateStatusTests {

        @Test
        @DisplayName("Should update order status")
        void shouldUpdateOrderStatus() {
            UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                .status(OrderStatus.PREPARING)
                .description("Restaurant started cooking")
                .updatedBy("restaurant-123")
                .build();

            when(orderRepository.findByIdAndDeletedFalse(ORDER_ID)).thenReturn(Optional.of(order));
            when(orderRepository.save(order)).thenReturn(order);
            when(orderMapper.toResponse(order)).thenReturn(orderResponse);

            OrderResponse result = orderService.updateOrderStatus(ORDER_ID, request);

            assertNotNull(result);
            assertEquals(OrderStatus.PREPARING, order.getStatus());
        }
    }

    @Nested
    @DisplayName("Cancel Order Tests")
    class CancelOrderTests {

        @Test
        @DisplayName("Should cancel order")
        void shouldCancelOrder() {
            CancelOrderRequest request = CancelOrderRequest.builder()
                .reason("Changed my mind")
                .cancelledBy(USER_ID)
                .build();

            when(orderRepository.findByIdAndDeletedFalse(ORDER_ID)).thenReturn(Optional.of(order));
            when(orderRepository.save(order)).thenReturn(order);
            when(orderMapper.toResponse(order)).thenReturn(orderResponse);

            OrderResponse result = orderService.cancelOrder(ORDER_ID, request);

            assertNotNull(result);
            assertEquals(OrderStatus.CANCELLED, order.getStatus());
        }

        @Test
        @DisplayName("Should throw when cancelling delivered order")
        void shouldThrowWhenCancellingDeliveredOrder() {
            order.setStatus(OrderStatus.DELIVERED);
            CancelOrderRequest request = CancelOrderRequest.builder()
                .reason("Test")
                .cancelledBy(USER_ID)
                .build();

            when(orderRepository.findByIdAndDeletedFalse(ORDER_ID)).thenReturn(Optional.of(order));

            assertThrows(InvalidOrderStateException.class,
                () -> orderService.cancelOrder(ORDER_ID, request));
        }
    }

    @Nested
    @DisplayName("Confirm Order Tests")
    class ConfirmOrderTests {

        @Test
        @DisplayName("Should confirm order after payment")
        void shouldConfirmOrder() {
            order.setOrderTotal(OrderTotalVO.builder()
                .total(new BigDecimal("500.00"))
                .build());

            when(orderRepository.findByIdAndDeletedFalse(ORDER_ID)).thenReturn(Optional.of(order));
            when(orderRepository.save(order)).thenReturn(order);
            when(orderMapper.toResponse(order)).thenReturn(orderResponse);

            OrderResponse result = orderService.confirmOrder(ORDER_ID);

            assertNotNull(result);
            assertEquals(OrderStatus.CONFIRMED, order.getStatus());
            assertEquals(PaymentStatus.SUCCESS, order.getPaymentStatus());
        }
    }

    @Nested
    @DisplayName("Rate Order Tests")
    class RateOrderTests {

        @Test
        @DisplayName("Should rate delivered order")
        void shouldRateDeliveredOrder() {
            order.setStatus(OrderStatus.DELIVERED);
            order.setReviewed(false);

            RateOrderRequest request = RateOrderRequest.builder()
                .rating(5)
                .review("Excellent food!")
                .build();

            when(orderRepository.findByIdAndDeletedFalse(ORDER_ID)).thenReturn(Optional.of(order));
            when(orderRepository.save(order)).thenReturn(order);
            when(orderMapper.toResponse(order)).thenReturn(orderResponse);

            OrderResponse result = orderService.rateOrder(ORDER_ID, request);

            assertNotNull(result);
            assertEquals(5, order.getCustomerRating());
            assertTrue(order.getReviewed());
        }

        @Test
        @DisplayName("Should throw when rating non-delivered order")
        void shouldThrowWhenRatingNonDeliveredOrder() {
            RateOrderRequest request = RateOrderRequest.builder()
                .rating(5)
                .build();

            when(orderRepository.findByIdAndDeletedFalse(ORDER_ID)).thenReturn(Optional.of(order));

            assertThrows(InvalidOrderStateException.class,
                () -> orderService.rateOrder(ORDER_ID, request));
        }
    }

    // Helper method
    private DeliveryAddressDTO createDeliveryAddress() {
        return DeliveryAddressDTO.builder()
            .recipientName("John Doe")
            .contactPhone("+919876543210")
            .street("MG Road")
            .city("Bangalore")
            .state("Karnataka")
            .pincode("560001")
            .build();
    }
}

