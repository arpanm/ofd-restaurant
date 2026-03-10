package com.foodai.order.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Main Order entity representing a customer order.
 *
 * <p>This is the aggregate root for the Order bounded context.
 *
 * @author FoodAI Team
 */
@Entity
@Table(name = "orders", indexes = {
    @Index(name = "idx_order_user_id", columnList = "userId"),
    @Index(name = "idx_order_status", columnList = "status"),
    @Index(name = "idx_order_number", columnList = "orderNumber", unique = true),
    @Index(name = "idx_order_created_at", columnList = "createdAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Human-readable order number (e.g., ORD-20250115-001).
     */
    @Column(nullable = false, unique = true, length = 30)
    private String orderNumber;

    /**
     * User ID who placed the order.
     */
    @Column(nullable = false)
    private String userId;

    /**
     * Type of order (single restaurant, multi-restaurant, AI chat, etc.).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderType orderType;

    /**
     * Current status of the order.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.CREATED;

    /**
     * Order items.
     */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Delivery address for this order.
     */
    @Embedded
    private DeliveryAddressVO deliveryAddress;

    /**
     * Order total breakdown.
     */
    @Embedded
    private OrderTotalVO orderTotal;

    /**
     * Payment method used.
     */
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    /**
     * Payment status.
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    /**
     * Payment gateway transaction ID.
     */
    private String paymentTransactionId;

    /**
     * Payment gateway order ID.
     */
    private String paymentGatewayOrderId;

    /**
     * Order timeline/history.
     */
    @ElementCollection
    @CollectionTable(name = "order_timeline", joinColumns = @JoinColumn(name = "order_id"))
    @OrderBy("timestamp DESC")
    @Builder.Default
    private List<OrderTimelineEntryVO> timeline = new ArrayList<>();

    /**
     * Special instructions for the entire order.
     */
    @Column(length = 1000)
    private String specialInstructions;

    /**
     * Delivery instructions.
     */
    @Column(length = 500)
    private String deliveryInstructions;

    /**
     * Scheduled delivery time (null for ASAP).
     */
    private Instant scheduledDeliveryTime;

    /**
     * Estimated delivery time.
     */
    private Instant estimatedDeliveryTime;

    /**
     * Actual delivery time.
     */
    private Instant actualDeliveryTime;

    /**
     * Assigned rider ID.
     */
    private String riderId;

    /**
     * Rider name.
     */
    private String riderName;

    /**
     * Rider phone number.
     */
    private String riderPhone;

    /**
     * Diet plan ID if this order is part of a diet plan.
     */
    private String dietPlanId;

    /**
     * Party plan ID if this order is for a party/event.
     */
    private String partyPlanId;

    /**
     * AI chat session ID if ordered via AI chat.
     */
    private String chatSessionId;

    /**
     * Reference to original order if this is a reorder.
     */
    private String reorderFromOrderId;

    /**
     * Cancellation reason if cancelled.
     */
    @Column(length = 500)
    private String cancellationReason;

    /**
     * Who initiated cancellation.
     */
    private String cancelledBy;

    /**
     * Loyalty points earned for this order.
     */
    private Integer loyaltyPointsEarned;

    /**
     * Loyalty points redeemed for this order.
     */
    private Integer loyaltyPointsRedeemed;

    /**
     * Customer rating for this order (1-5).
     */
    private Integer customerRating;

    /**
     * Customer review/feedback.
     */
    @Column(length = 1000)
    private String customerReview;

    /**
     * Whether the order has been reviewed.
     */
    @Builder.Default
    private Boolean reviewed = false;

    /**
     * Soft delete flag.
     */
    @Builder.Default
    private Boolean deleted = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        updatedAt = Instant.now();
        addTimelineEntry(OrderStatus.CREATED, "Order created", "SYSTEM");
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    /**
     * Adds an item to the order.
     *
     * @param item the order item to add
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    /**
     * Removes an item from the order.
     *
     * @param item the order item to remove
     */
    public void removeItem(OrderItem item) {
        items.remove(item);
        item.setOrder(null);
    }

    /**
     * Adds a timeline entry.
     *
     * @param status the order status
     * @param description description of the status change
     * @param updatedBy who made the change
     */
    public void addTimelineEntry(OrderStatus status, String description, String updatedBy) {
        OrderTimelineEntryVO entry = OrderTimelineEntryVO.builder()
            .status(status)
            .description(description)
            .timestamp(Instant.now())
            .updatedBy(updatedBy)
            .build();
        timeline.add(entry);
    }

    /**
     * Updates the order status and adds a timeline entry.
     *
     * @param newStatus the new status
     * @param description description of the status change
     * @param updatedBy who made the change
     */
    public void updateStatus(OrderStatus newStatus, String description, String updatedBy) {
        this.status = newStatus;
        addTimelineEntry(newStatus, description, updatedBy);
    }

    /**
     * Confirms the order after payment.
     */
    public void confirm() {
        if (this.status != OrderStatus.CREATED) {
            throw new IllegalStateException("Can only confirm orders in CREATED status");
        }
        this.status = OrderStatus.CONFIRMED;
        addTimelineEntry(OrderStatus.CONFIRMED, "Order confirmed", "SYSTEM");
    }

    /**
     * Marks order as preparing.
     *
     * @param restaurantId the restaurant that started preparation
     */
    public void startPreparing(String restaurantId) {
        if (this.status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("Can only start preparing orders in CONFIRMED status");
        }
        this.status = OrderStatus.PREPARING;
        addTimelineEntry(OrderStatus.PREPARING, "Restaurant started preparation", restaurantId);
    }

    /**
     * Marks order as ready for pickup.
     *
     * @param restaurantId the restaurant that prepared the order
     */
    public void markReady(String restaurantId) {
        if (this.status != OrderStatus.PREPARING) {
            throw new IllegalStateException("Can only mark ready orders in PREPARING status");
        }
        this.status = OrderStatus.READY;
        addTimelineEntry(OrderStatus.READY, "Order ready for pickup", restaurantId);
    }

    /**
     * Marks order as picked up by rider.
     *
     * @param riderId the rider who picked up the order
     * @param riderName rider's name
     * @param riderPhone rider's phone
     */
    public void markPickedUp(String riderId, String riderName, String riderPhone) {
        if (this.status != OrderStatus.READY) {
            throw new IllegalStateException("Can only pick up orders in READY status");
        }
        this.riderId = riderId;
        this.riderName = riderName;
        this.riderPhone = riderPhone;
        this.status = OrderStatus.PICKED_UP;
        addTimelineEntry(OrderStatus.PICKED_UP, "Order picked up by " + riderName, riderId);
    }

    /**
     * Marks order as in transit.
     */
    public void markInTransit() {
        if (this.status != OrderStatus.PICKED_UP) {
            throw new IllegalStateException("Can only mark in transit orders in PICKED_UP status");
        }
        this.status = OrderStatus.IN_TRANSIT;
        addTimelineEntry(OrderStatus.IN_TRANSIT, "Order is on the way", riderId);
    }

    /**
     * Marks order as delivered.
     */
    public void markDelivered() {
        if (this.status != OrderStatus.IN_TRANSIT && this.status != OrderStatus.PICKED_UP) {
            throw new IllegalStateException("Can only deliver orders in IN_TRANSIT or PICKED_UP status");
        }
        this.status = OrderStatus.DELIVERED;
        this.actualDeliveryTime = Instant.now();
        addTimelineEntry(OrderStatus.DELIVERED, "Order delivered", riderId != null ? riderId : "SYSTEM");
    }

    /**
     * Cancels the order.
     *
     * @param reason cancellation reason
     * @param cancelledBy who cancelled the order
     */
    public void cancel(String reason, String cancelledBy) {
        if (this.status == OrderStatus.DELIVERED || this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel delivered or already cancelled orders");
        }
        this.status = OrderStatus.CANCELLED;
        this.cancellationReason = reason;
        this.cancelledBy = cancelledBy;
        addTimelineEntry(OrderStatus.CANCELLED, "Order cancelled: " + reason, cancelledBy);
    }

    /**
     * Checks if the order can be cancelled.
     *
     * @return true if the order can be cancelled
     */
    public boolean canCancel() {
        return this.status != OrderStatus.DELIVERED 
            && this.status != OrderStatus.CANCELLED
            && this.status != OrderStatus.RETURNED;
    }

    /**
     * Checks if the order can be refunded.
     *
     * @return true if the order can be refunded
     */
    public boolean canRefund() {
        return this.paymentStatus == PaymentStatus.SUCCESS
            && (this.status == OrderStatus.CANCELLED 
                || this.status == OrderStatus.DELIVERED
                || this.status == OrderStatus.DELIVERY_FAILED);
    }

    /**
     * Calculates the total from items and updates orderTotal.
     */
    public void calculateTotal() {
        if (orderTotal == null) {
            orderTotal = new OrderTotalVO();
        }
        
        BigDecimal subtotal = items.stream()
            .map(OrderItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        orderTotal.setSubtotal(subtotal);
        orderTotal.calculateTotal();
    }

    /**
     * Gets distinct restaurant IDs in this order.
     *
     * @return list of restaurant IDs
     */
    public List<String> getRestaurantIds() {
        return items.stream()
            .map(OrderItem::getRestaurantId)
            .distinct()
            .toList();
    }

    /**
     * Checks if this is a multi-restaurant order.
     *
     * @return true if order contains items from multiple restaurants
     */
    public boolean isMultiRestaurantOrder() {
        return getRestaurantIds().size() > 1;
    }
}

