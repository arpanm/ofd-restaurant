package com.foodai.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.foodai.order.domain.model.OrderStatus;
import com.foodai.order.domain.model.OrderType;
import com.foodai.order.dto.request.*;
import com.foodai.order.dto.response.OrderResponse;
import com.foodai.order.exception.GlobalExceptionHandler;
import com.foodai.order.exception.InvalidOrderStateException;
import com.foodai.order.exception.OrderNotFoundException;
import com.foodai.order.service.OrderService;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for OrderController.
 *
 * @author FoodAI Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Order Controller Tests")
class OrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private ObjectMapper objectMapper;
    private OrderResponse orderResponse;
    private final String ORDER_ID = "order-123";
    private final String USER_ID = "user-123";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        orderResponse = OrderResponse.builder()
            .id(ORDER_ID)
            .orderNumber("ORD-20250115-001")
            .userId(USER_ID)
            .orderType(OrderType.SINGLE_RESTAURANT)
            .status(OrderStatus.CREATED)
            .items(new ArrayList<>())
            .canCancel(true)
            .canRefund(false)
            .build();
    }

    @Nested
    @DisplayName("GET /api/v1/orders/{orderId}")
    class GetOrderEndpointTests {

        @Test
        @DisplayName("Should return order by ID")
        void shouldReturnOrderById() throws Exception {
            when(orderService.getOrder(ORDER_ID)).thenReturn(orderResponse);

            mockMvc.perform(get("/api/v1/orders/{orderId}", ORDER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(ORDER_ID));

            verify(orderService).getOrder(ORDER_ID);
        }

        @Test
        @DisplayName("Should return 404 when order not found")
        void shouldReturn404WhenOrderNotFound() throws Exception {
            when(orderService.getOrder(ORDER_ID))
                .thenThrow(new OrderNotFoundException("Order not found"));

            mockMvc.perform(get("/api/v1/orders/{orderId}", ORDER_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/orders/number/{orderNumber}")
    class GetOrderByNumberEndpointTests {

        @Test
        @DisplayName("Should return order by order number")
        void shouldReturnOrderByNumber() throws Exception {
            String orderNumber = "ORD-20250115-001";
            when(orderService.getOrderByNumber(orderNumber)).thenReturn(orderResponse);

            mockMvc.perform(get("/api/v1/orders/number/{orderNumber}", orderNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderNumber").value(orderNumber));

            verify(orderService).getOrderByNumber(orderNumber);
        }
    }

    @Nested
    @DisplayName("PATCH /api/v1/orders/{orderId}/status")
    class UpdateStatusEndpointTests {

        @Test
        @DisplayName("Should update order status")
        void shouldUpdateOrderStatus() throws Exception {
            UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                .status(OrderStatus.PREPARING)
                .description("Restaurant started cooking")
                .updatedBy("restaurant-123")
                .build();

            when(orderService.updateOrderStatus(eq(ORDER_ID), any(UpdateOrderStatusRequest.class)))
                .thenReturn(orderResponse);

            mockMvc.perform(patch("/api/v1/orders/{orderId}/status", ORDER_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

            verify(orderService).updateOrderStatus(eq(ORDER_ID), any(UpdateOrderStatusRequest.class));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/orders/{orderId}/cancel")
    class CancelOrderEndpointTests {

        @Test
        @DisplayName("Should cancel order")
        void shouldCancelOrder() throws Exception {
            CancelOrderRequest request = CancelOrderRequest.builder()
                .reason("Changed my mind about the order")
                .cancelledBy(USER_ID)
                .build();

            when(orderService.cancelOrder(eq(ORDER_ID), any(CancelOrderRequest.class)))
                .thenReturn(orderResponse);

            mockMvc.perform(post("/api/v1/orders/{orderId}/cancel", ORDER_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Order cancelled"));

            verify(orderService).cancelOrder(eq(ORDER_ID), any(CancelOrderRequest.class));
        }

        @Test
        @DisplayName("Should return 409 when order cannot be cancelled")
        void shouldReturn409WhenOrderCannotBeCancelled() throws Exception {
            CancelOrderRequest request = CancelOrderRequest.builder()
                .reason("Changed my mind")
                .cancelledBy(USER_ID)
                .build();

            when(orderService.cancelOrder(eq(ORDER_ID), any(CancelOrderRequest.class)))
                .thenThrow(new InvalidOrderStateException("Order cannot be cancelled"));

            mockMvc.perform(post("/api/v1/orders/{orderId}/cancel", ORDER_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/orders/{orderId}/assign-rider")
    class AssignRiderEndpointTests {

        @Test
        @DisplayName("Should assign rider to order")
        void shouldAssignRider() throws Exception {
            AssignRiderRequest request = AssignRiderRequest.builder()
                .riderId("rider-123")
                .riderName("Rajesh Kumar")
                .riderPhone("+919876543210")
                .build();

            when(orderService.assignRider(eq(ORDER_ID), any(AssignRiderRequest.class)))
                .thenReturn(orderResponse);

            mockMvc.perform(post("/api/v1/orders/{orderId}/assign-rider", ORDER_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Rider assigned"));

            verify(orderService).assignRider(eq(ORDER_ID), any(AssignRiderRequest.class));
        }
    }

}

