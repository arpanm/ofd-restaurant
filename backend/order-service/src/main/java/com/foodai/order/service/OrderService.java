package com.foodai.order.service;

import com.foodai.order.domain.model.*;
import com.foodai.order.domain.repository.OrderRepository;
import com.foodai.order.dto.request.*;
import com.foodai.order.dto.response.OrderResponse;
import com.foodai.order.exception.InvalidOrderStateException;
import com.foodai.order.exception.OrderNotFoundException;
import com.foodai.order.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 * Service for order management operations.
 *
 * @author FoodAI Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final OrderMapper orderMapper;
    
    // Simple order number counter (in production, use database sequence)
    private static final AtomicLong orderCounter = new AtomicLong(1);
    private static final BigDecimal DELIVERY_FEE = new BigDecimal("40.00");
    private static final BigDecimal PLATFORM_FEE = new BigDecimal("5.00");
    private static final BigDecimal GST_PERCENTAGE = new BigDecimal("0.05");

    /**
     * Creates a new order from the user's cart.
     *
     * @param request the create order request
     * @return the created order response
     */
    public OrderResponse createOrder(CreateOrderRequest request) {
        log.info("Creating order", kv("userId", request.getUserId()), kv("orderType", request.getOrderType()));
        
        // Get cart and validate
        Cart cart = cartService.getCartEntity(request.getUserId());
        if (cart.isEmpty()) {
            throw new IllegalStateException("Cannot create order from empty cart");
        }
        
        // Generate order number
        String orderNumber = generateOrderNumber();
        
        // Create order
        Order order = Order.builder()
            .orderNumber(orderNumber)
            .userId(request.getUserId())
            .orderType(request.getOrderType())
            .status(OrderStatus.CREATED)
            .deliveryAddress(orderMapper.toAddressVO(request.getDeliveryAddress()))
            .paymentMethod(request.getPaymentMethod())
            .paymentStatus(PaymentStatus.PENDING)
            .specialInstructions(request.getSpecialInstructions())
            .deliveryInstructions(request.getDeliveryInstructions())
            .scheduledDeliveryTime(request.getScheduledDeliveryTime())
            .dietPlanId(request.getDietPlanId())
            .partyPlanId(request.getPartyPlanId())
            .chatSessionId(request.getChatSessionId())
            .reorderFromOrderId(request.getReorderFromOrderId())
            .build();
        
        // Add items from cart
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = orderMapper.cartItemToOrderItem(cartItem);
            order.addItem(orderItem);
        }
        
        // Calculate totals
        OrderTotalVO orderTotal = calculateOrderTotal(
            cart.getSubtotal(),
            request.getCouponDiscount(),
            request.getCouponCode(),
            request.getTip(),
            request.getLoyaltyPointsToRedeem()
        );
        order.setOrderTotal(orderTotal);
        
        // Estimate delivery time
        order.setEstimatedDeliveryTime(calculateEstimatedDeliveryTime(cart, request.getScheduledDeliveryTime()));
        
        // Save order
        Order savedOrder = orderRepository.save(order);
        log.info("Order created", kv("orderId", savedOrder.getId()), kv("orderNumber", orderNumber));
        
        // Clear cart after order creation
        cartService.deleteCart(request.getUserId());
        
        return orderMapper.toResponse(savedOrder);
    }

    /**
     * Gets an order by ID.
     *
     * @param orderId the order ID
     * @return the order response
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrder(String orderId) {
        log.debug("Getting order", kv("orderId", orderId));
        Order order = findOrderById(orderId);
        return orderMapper.toResponse(order);
    }

    /**
     * Gets an order by order number.
     *
     * @param orderNumber the order number
     * @return the order response
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber) {
        log.debug("Getting order by number", kv("orderNumber", orderNumber));
        Order order = orderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new OrderNotFoundException(orderNumber, true));
        return orderMapper.toResponse(order);
    }

    /**
     * Gets all orders for a user with pagination.
     *
     * @param userId the user ID
     * @param pageable pagination info
     * @return page of order responses
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrders(String userId, Pageable pageable) {
        log.debug("Getting orders for user", kv("userId", userId));
        return orderRepository.findByUserIdAndDeletedFalseOrderByCreatedAtDesc(userId, pageable)
            .map(orderMapper::toResponse);
    }

    /**
     * Gets active orders for a user.
     *
     * @param userId the user ID
     * @return list of active order responses
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getActiveOrders(String userId) {
        log.debug("Getting active orders for user", kv("userId", userId));
        List<Order> orders = orderRepository.findActiveOrdersByUserId(userId);
        return orderMapper.toResponseList(orders);
    }

    /**
     * Gets past orders for a user.
     *
     * @param userId the user ID
     * @param pageable pagination info
     * @return page of past order responses
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getPastOrders(String userId, Pageable pageable) {
        log.debug("Getting past orders for user", kv("userId", userId));
        return orderRepository.findPastOrdersByUserId(userId, pageable)
            .map(orderMapper::toResponse);
    }

    /**
     * Gets orders for a restaurant.
     *
     * @param restaurantId the restaurant ID
     * @param status optional status filter
     * @param pageable pagination info
     * @return page of order responses
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getRestaurantOrders(String restaurantId, OrderStatus status, Pageable pageable) {
        log.debug("Getting orders for restaurant", kv("restaurantId", restaurantId));
        if (status != null) {
            return orderRepository.findByRestaurantIdAndStatus(restaurantId, status, pageable)
                .map(orderMapper::toResponse);
        }
        return orderRepository.findByRestaurantId(restaurantId, pageable)
            .map(orderMapper::toResponse);
    }

    /**
     * Updates the status of an order.
     *
     * @param orderId the order ID
     * @param request the status update request
     * @return the updated order response
     */
    public OrderResponse updateOrderStatus(String orderId, UpdateOrderStatusRequest request) {
        log.info("Updating order status", kv("orderId", orderId), kv("newStatus", request.getStatus()));
        
        Order order = findOrderById(orderId);
        order.updateStatus(request.getStatus(), request.getDescription(), request.getUpdatedBy());
        
        Order savedOrder = orderRepository.save(order);
        log.info("Order status updated", kv("orderId", orderId), kv("status", request.getStatus()));
        
        // TODO: Send notifications, publish events
        
        return orderMapper.toResponse(savedOrder);
    }

    /**
     * Confirms an order after payment.
     *
     * @param orderId the order ID
     * @return the updated order response
     */
    public OrderResponse confirmOrder(String orderId) {
        log.info("Confirming order", kv("orderId", orderId));
        
        Order order = findOrderById(orderId);
        order.confirm();
        order.setPaymentStatus(PaymentStatus.SUCCESS);
        
        // Calculate loyalty points (1 point per ₹10 spent)
        int pointsEarned = order.getOrderTotal().getTotal()
            .divide(BigDecimal.TEN, 0, java.math.RoundingMode.DOWN)
            .intValue();
        order.setLoyaltyPointsEarned(pointsEarned);
        
        Order savedOrder = orderRepository.save(order);
        log.info("Order confirmed", kv("orderId", orderId), kv("pointsEarned", pointsEarned));
        
        // TODO: Publish order.confirmed event
        
        return orderMapper.toResponse(savedOrder);
    }

    /**
     * Cancels an order.
     *
     * @param orderId the order ID
     * @param request the cancellation request
     * @return the updated order response
     */
    public OrderResponse cancelOrder(String orderId, CancelOrderRequest request) {
        log.info("Cancelling order", kv("orderId", orderId), kv("reason", request.getReason()));
        
        Order order = findOrderById(orderId);
        
        if (!order.canCancel()) {
            throw new InvalidOrderStateException("Order cannot be cancelled in current state: " + order.getStatus());
        }
        
        order.cancel(request.getReason(), request.getCancelledBy());
        
        Order savedOrder = orderRepository.save(order);
        log.info("Order cancelled", kv("orderId", orderId));
        
        // TODO: Trigger refund if paid, publish order.cancelled event
        
        return orderMapper.toResponse(savedOrder);
    }

    /**
     * Assigns a rider to an order.
     *
     * @param orderId the order ID
     * @param request the rider assignment request
     * @return the updated order response
     */
    public OrderResponse assignRider(String orderId, AssignRiderRequest request) {
        log.info("Assigning rider to order", kv("orderId", orderId), kv("riderId", request.getRiderId()));
        
        Order order = findOrderById(orderId);
        
        if (order.getStatus() != OrderStatus.READY) {
            throw new InvalidOrderStateException("Can only assign rider to orders in READY status");
        }
        
        order.markPickedUp(request.getRiderId(), request.getRiderName(), request.getRiderPhone());
        
        Order savedOrder = orderRepository.save(order);
        log.info("Rider assigned to order", kv("orderId", orderId), kv("riderId", request.getRiderId()));
        
        return orderMapper.toResponse(savedOrder);
    }

    /**
     * Rates an order.
     *
     * @param orderId the order ID
     * @param request the rating request
     * @return the updated order response
     */
    public OrderResponse rateOrder(String orderId, RateOrderRequest request) {
        log.info("Rating order", kv("orderId", orderId), kv("rating", request.getRating()));
        
        Order order = findOrderById(orderId);
        
        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new InvalidOrderStateException("Can only rate delivered orders");
        }
        
        if (order.getReviewed()) {
            throw new InvalidOrderStateException("Order has already been reviewed");
        }
        
        order.setCustomerRating(request.getRating());
        order.setCustomerReview(request.getReview());
        order.setReviewed(true);
        
        Order savedOrder = orderRepository.save(order);
        log.info("Order rated", kv("orderId", orderId), kv("rating", request.getRating()));
        
        return orderMapper.toResponse(savedOrder);
    }

    /**
     * Creates a reorder from a previous order.
     *
     * @param originalOrderId the original order ID
     * @param userId the user ID
     * @return cart response with reordered items
     */
    public OrderResponse reorder(String originalOrderId, String userId) {
        log.info("Creating reorder", kv("originalOrderId", originalOrderId), kv("userId", userId));
        
        Order originalOrder = findOrderById(originalOrderId);
        
        if (!originalOrder.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Order does not belong to user");
        }
        
        // Add items to cart
        for (OrderItem item : originalOrder.getItems()) {
            AddCartItemRequest cartRequest = AddCartItemRequest.builder()
                .menuItemId(item.getMenuItemId())
                .name(item.getName())
                .description(item.getDescription())
                .restaurantId(item.getRestaurantId())
                .restaurantName(item.getRestaurantName())
                .imageUrl(item.getImageUrl())
                .unitPrice(item.getUnitPrice())
                .quantity(item.getQuantity())
                .specialInstructions(item.getSpecialInstructions())
                .vegetarian(item.getVegetarian())
                .spiceLevel(item.getSpiceLevel())
                .build();
            
            cartService.addItem(userId, cartRequest);
        }
        
        log.info("Reorder items added to cart", kv("originalOrderId", originalOrderId));
        
        // Return original order for reference
        return orderMapper.toResponse(originalOrder);
    }

    // Private helper methods

    private Order findOrderById(String orderId) {
        return orderRepository.findByIdAndDeletedFalse(orderId)
            .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
    }

    private String generateOrderNumber() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        long counter = orderCounter.getAndIncrement();
        return String.format("ORD-%s-%03d", date, counter);
    }

    private OrderTotalVO calculateOrderTotal(BigDecimal subtotal, BigDecimal couponDiscount, 
            String couponCode, BigDecimal tip, Integer loyaltyPoints) {
        
        BigDecimal gst = subtotal.multiply(GST_PERCENTAGE);
        BigDecimal discount = couponDiscount != null ? couponDiscount : BigDecimal.ZERO;
        BigDecimal loyaltyValue = loyaltyPoints != null ? BigDecimal.valueOf(loyaltyPoints) : BigDecimal.ZERO;
        BigDecimal tipAmount = tip != null ? tip : BigDecimal.ZERO;
        
        OrderTotalVO total = OrderTotalVO.builder()
            .subtotal(subtotal)
            .deliveryFee(DELIVERY_FEE)
            .platformFee(PLATFORM_FEE)
            .packagingCharges(BigDecimal.ZERO)
            .gst(gst)
            .gstPercentage(GST_PERCENTAGE.multiply(BigDecimal.valueOf(100)))
            .discount(discount)
            .couponCode(couponCode)
            .loyaltyPointsValue(loyaltyValue)
            .tip(tipAmount)
            .currency("INR")
            .build();
        
        total.calculateTotal();
        return total;
    }

    private Instant calculateEstimatedDeliveryTime(Cart cart, Instant scheduledTime) {
        if (scheduledTime != null) {
            return scheduledTime;
        }
        
        // Calculate based on max preparation time + 20 min delivery buffer
        int maxPrepTime = cart.getItems().stream()
            .mapToInt(item -> item.getPreparationTime() != null ? item.getPreparationTime() : 20)
            .max()
            .orElse(20);
        
        return Instant.now().plusSeconds((maxPrepTime + 20) * 60L);
    }
}

