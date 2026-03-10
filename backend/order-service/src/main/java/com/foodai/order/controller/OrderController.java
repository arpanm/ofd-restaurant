package com.foodai.order.controller;

import com.foodai.order.domain.model.OrderStatus;
import com.foodai.order.dto.request.*;
import com.foodai.order.dto.response.ApiResponse;
import com.foodai.order.dto.response.OrderResponse;
import com.foodai.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for order operations (restaurant-facing: view orders, update status, cancel, assign rider).
 *
 * @author FoodAI Team
 */
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Management", description = "APIs for restaurant order management")
public class OrderController {

    private final OrderService orderService;

    /**
     * Gets an order by ID.
     */
    @GetMapping("/{orderId}")
    @Operation(summary = "Get Order", description = "Gets an order by its ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(
            @Parameter(description = "Order ID") @PathVariable String orderId) {
        log.info("Request to get order: {}", orderId);
        OrderResponse response = orderService.getOrder(orderId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Gets an order by order number.
     */
    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get Order by Number", description = "Gets an order by its order number")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByNumber(
            @Parameter(description = "Order Number") @PathVariable String orderNumber) {
        log.info("Request to get order by number: {}", orderNumber);
        OrderResponse response = orderService.getOrderByNumber(orderNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Gets orders for a restaurant.
     */
    @GetMapping("/restaurant/{restaurantId}")
    @Operation(summary = "Get Restaurant Orders", description = "Gets all orders for a restaurant")
    public ResponseEntity<ApiResponse<Page<OrderResponse>>> getRestaurantOrders(
            @Parameter(description = "Restaurant ID") @PathVariable String restaurantId,
            @Parameter(description = "Status filter") @RequestParam(required = false) OrderStatus status,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        log.info("Request to get orders for restaurant: {}", restaurantId);
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OrderResponse> response = orderService.getRestaurantOrders(restaurantId, status, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Updates order status.
     */
    @PatchMapping("/{orderId}/status")
    @Operation(summary = "Update Order Status", description = "Updates the status of an order")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Invalid status transition")
    })
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @Parameter(description = "Order ID") @PathVariable String orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        log.info("Request to update order {} status to {}", orderId, request.getStatus());
        OrderResponse response = orderService.updateOrderStatus(orderId, request);
        return ResponseEntity.ok(ApiResponse.success("Order status updated", response));
    }

    /**
     * Cancels an order.
     */
    @PostMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel Order", description = "Cancels an order")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Cannot cancel order")
    })
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @Parameter(description = "Order ID") @PathVariable String orderId,
            @Valid @RequestBody CancelOrderRequest request) {
        log.info("Request to cancel order: {}", orderId);
        OrderResponse response = orderService.cancelOrder(orderId, request);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled", response));
    }

    /**
     * Assigns a rider to an order.
     */
    @PostMapping("/{orderId}/assign-rider")
    @Operation(summary = "Assign Rider", description = "Assigns a delivery rider to an order")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Rider assigned successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Order not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Cannot assign rider")
    })
    public ResponseEntity<ApiResponse<OrderResponse>> assignRider(
            @Parameter(description = "Order ID") @PathVariable String orderId,
            @Valid @RequestBody AssignRiderRequest request) {
        log.info("Request to assign rider {} to order {}", request.getRiderId(), orderId);
        OrderResponse response = orderService.assignRider(orderId, request);
        return ResponseEntity.ok(ApiResponse.success("Rider assigned", response));
    }
}

