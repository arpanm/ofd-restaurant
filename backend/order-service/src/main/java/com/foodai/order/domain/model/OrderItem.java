package com.foodai.order.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing an item within an order.
 *
 * @author FoodAI Team
 */
@Entity
@Table(name = "order_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * Reference to the menu item.
     */
    @Column(nullable = false)
    private String menuItemId;

    /**
     * Menu item name (snapshot at order time).
     */
    @Column(nullable = false)
    private String name;

    /**
     * Menu item description.
     */
    private String description;

    /**
     * Restaurant ID this item belongs to.
     */
    @Column(nullable = false)
    private String restaurantId;

    /**
     * Restaurant name (snapshot at order time).
     */
    private String restaurantName;

    /**
     * Item image URL.
     */
    private String imageUrl;

    /**
     * Unit price at the time of order.
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    /**
     * Quantity ordered.
     */
    @Column(nullable = false)
    private Integer quantity;

    /**
     * Total price for this item (unitPrice * quantity + customizations).
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    /**
     * Customizations applied to this item.
     */
    @ElementCollection
    @CollectionTable(name = "order_item_customizations", joinColumns = @JoinColumn(name = "order_item_id"))
    @Builder.Default
    private List<OrderItemCustomizationVO> customizations = new ArrayList<>();

    /**
     * Special instructions for this item.
     */
    @Column(length = 500)
    private String specialInstructions;

    /**
     * Whether item is vegetarian.
     */
    private Boolean vegetarian;

    /**
     * Spice level (0-5).
     */
    private Integer spiceLevel;

    /**
     * Status of this item (for partial order tracking).
     */
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderItemStatus itemStatus = OrderItemStatus.PENDING;

    /**
     * Reference to the parent order.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        calculateTotalPrice();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
        calculateTotalPrice();
    }

    /**
     * Calculates and updates the total price for this item.
     */
    public void calculateTotalPrice() {
        BigDecimal baseTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        BigDecimal customizationTotal = BigDecimal.ZERO;
        
        if (customizations != null) {
            for (OrderItemCustomizationVO customization : customizations) {
                if (customization.getAdditionalCost() != null) {
                    customizationTotal = customizationTotal.add(
                        customization.getAdditionalCost().multiply(BigDecimal.valueOf(quantity))
                    );
                }
            }
        }
        
        this.totalPrice = baseTotal.add(customizationTotal);
    }
}

