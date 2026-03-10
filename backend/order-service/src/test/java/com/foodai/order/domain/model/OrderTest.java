package com.foodai.order.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Order domain entity.
 *
 * @author FoodAI Team
 */
@DisplayName("Order Entity Tests")
class OrderTest {

    private Order order;

    @BeforeEach
    void setUp() {
        order = Order.builder()
            .id("order-123")
            .orderNumber("ORD-20250115-001")
            .userId("user-123")
            .orderType(OrderType.SINGLE_RESTAURANT)
            .status(OrderStatus.CREATED)
            .paymentStatus(PaymentStatus.PENDING)
            .build();
    }

    @Nested
    @DisplayName("Status Transition Tests")
    class StatusTransitionTests {

        @Test
        @DisplayName("Should confirm order from CREATED status")
        void shouldConfirmOrder() {
            order.confirm();
            
            assertEquals(OrderStatus.CONFIRMED, order.getStatus());
            assertFalse(order.getTimeline().isEmpty());
        }

        @Test
        @DisplayName("Should throw when confirming non-CREATED order")
        void shouldThrowWhenConfirmingNonCreatedOrder() {
            order.setStatus(OrderStatus.CONFIRMED);
            
            assertThrows(IllegalStateException.class, () -> order.confirm());
        }

        @Test
        @DisplayName("Should start preparing from CONFIRMED status")
        void shouldStartPreparing() {
            order.setStatus(OrderStatus.CONFIRMED);
            
            order.startPreparing("restaurant-123");
            
            assertEquals(OrderStatus.PREPARING, order.getStatus());
        }

        @Test
        @DisplayName("Should mark ready from PREPARING status")
        void shouldMarkReady() {
            order.setStatus(OrderStatus.PREPARING);
            
            order.markReady("restaurant-123");
            
            assertEquals(OrderStatus.READY, order.getStatus());
        }

        @Test
        @DisplayName("Should mark picked up from READY status")
        void shouldMarkPickedUp() {
            order.setStatus(OrderStatus.READY);
            
            order.markPickedUp("rider-123", "Rajesh", "+919876543210");
            
            assertEquals(OrderStatus.PICKED_UP, order.getStatus());
            assertEquals("rider-123", order.getRiderId());
            assertEquals("Rajesh", order.getRiderName());
        }

        @Test
        @DisplayName("Should mark in transit from PICKED_UP status")
        void shouldMarkInTransit() {
            order.setStatus(OrderStatus.PICKED_UP);
            order.setRiderId("rider-123");
            
            order.markInTransit();
            
            assertEquals(OrderStatus.IN_TRANSIT, order.getStatus());
        }

        @Test
        @DisplayName("Should mark delivered from IN_TRANSIT status")
        void shouldMarkDelivered() {
            order.setStatus(OrderStatus.IN_TRANSIT);
            order.setRiderId("rider-123");
            
            order.markDelivered();
            
            assertEquals(OrderStatus.DELIVERED, order.getStatus());
            assertNotNull(order.getActualDeliveryTime());
        }
    }

    @Nested
    @DisplayName("Cancellation Tests")
    class CancellationTests {

        @Test
        @DisplayName("Should cancel order in CREATED status")
        void shouldCancelCreatedOrder() {
            order.cancel("Changed my mind", "user-123");
            
            assertEquals(OrderStatus.CANCELLED, order.getStatus());
            assertEquals("Changed my mind", order.getCancellationReason());
            assertEquals("user-123", order.getCancelledBy());
        }

        @Test
        @DisplayName("Should throw when cancelling delivered order")
        void shouldThrowWhenCancellingDeliveredOrder() {
            order.setStatus(OrderStatus.DELIVERED);
            
            assertThrows(IllegalStateException.class, 
                () -> order.cancel("Test", "user"));
        }

        @Test
        @DisplayName("canCancel should return true for cancellable statuses")
        void shouldReturnTrueForCancellableStatuses() {
            assertTrue(order.canCancel());
            
            order.setStatus(OrderStatus.CONFIRMED);
            assertTrue(order.canCancel());
            
            order.setStatus(OrderStatus.PREPARING);
            assertTrue(order.canCancel());
        }

        @Test
        @DisplayName("canCancel should return false for non-cancellable statuses")
        void shouldReturnFalseForNonCancellableStatuses() {
            order.setStatus(OrderStatus.DELIVERED);
            assertFalse(order.canCancel());
            
            order.setStatus(OrderStatus.CANCELLED);
            assertFalse(order.canCancel());
        }
    }

    @Nested
    @DisplayName("Item Management Tests")
    class ItemManagementTests {

        @Test
        @DisplayName("Should add item to order")
        void shouldAddItem() {
            OrderItem item = OrderItem.builder()
                .menuItemId("menu-123")
                .name("Chicken Biryani")
                .restaurantId("rest-123")
                .unitPrice(new BigDecimal("299.00"))
                .quantity(2)
                .build();
            
            order.addItem(item);
            
            assertEquals(1, order.getItems().size());
            assertEquals(order, item.getOrder());
        }

        @Test
        @DisplayName("Should remove item from order")
        void shouldRemoveItem() {
            OrderItem item = OrderItem.builder()
                .menuItemId("menu-123")
                .name("Chicken Biryani")
                .build();
            order.addItem(item);
            
            order.removeItem(item);
            
            assertTrue(order.getItems().isEmpty());
            assertNull(item.getOrder());
        }

        @Test
        @DisplayName("Should identify multi-restaurant order")
        void shouldIdentifyMultiRestaurantOrder() {
            OrderItem item1 = OrderItem.builder()
                .menuItemId("menu-1")
                .restaurantId("rest-1")
                .unitPrice(BigDecimal.valueOf(100))
                .quantity(1)
                .totalPrice(BigDecimal.valueOf(100))
                .build();
            OrderItem item2 = OrderItem.builder()
                .menuItemId("menu-2")
                .restaurantId("rest-2")
                .unitPrice(BigDecimal.valueOf(200))
                .quantity(1)
                .totalPrice(BigDecimal.valueOf(200))
                .build();
            
            order.addItem(item1);
            order.addItem(item2);
            
            assertTrue(order.isMultiRestaurantOrder());
            assertEquals(2, order.getRestaurantIds().size());
        }
    }

    @Nested
    @DisplayName("Total Calculation Tests")
    class TotalCalculationTests {

        @Test
        @DisplayName("Should calculate total from items")
        void shouldCalculateTotal() {
            OrderItem item1 = OrderItem.builder()
                .menuItemId("menu-1")
                .unitPrice(new BigDecimal("299.00"))
                .quantity(2)
                .totalPrice(new BigDecimal("598.00"))
                .build();
            OrderItem item2 = OrderItem.builder()
                .menuItemId("menu-2")
                .unitPrice(new BigDecimal("199.00"))
                .quantity(1)
                .totalPrice(new BigDecimal("199.00"))
                .build();
            
            order.addItem(item1);
            order.addItem(item2);
            order.calculateTotal();
            
            assertNotNull(order.getOrderTotal());
            assertEquals(new BigDecimal("797.00"), order.getOrderTotal().getSubtotal());
        }
    }

    @Nested
    @DisplayName("Refund Eligibility Tests")
    class RefundEligibilityTests {

        @Test
        @DisplayName("canRefund should return true for paid cancelled order")
        void shouldAllowRefundForPaidCancelledOrder() {
            order.setPaymentStatus(PaymentStatus.SUCCESS);
            order.setStatus(OrderStatus.CANCELLED);
            
            assertTrue(order.canRefund());
        }

        @Test
        @DisplayName("canRefund should return false for pending payment")
        void shouldNotAllowRefundForPendingPayment() {
            order.setPaymentStatus(PaymentStatus.PENDING);
            order.setStatus(OrderStatus.CANCELLED);
            
            assertFalse(order.canRefund());
        }
    }
}

