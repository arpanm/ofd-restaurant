package com.foodai.order.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MongoDB document representing a user's shopping cart.
 *
 * <p>Carts support items from multiple restaurants (multi-restaurant ordering).
 *
 * @author FoodAI Team
 */
@Document(collection = "carts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    private String id;

    /**
     * User ID who owns this cart.
     */
    @Indexed(unique = true)
    private String userId;

    /**
     * Cart items from potentially multiple restaurants.
     */
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    /**
     * Applied coupon code.
     */
    private String appliedCouponCode;

    /**
     * Discount from applied coupon.
     */
    private BigDecimal couponDiscount;

    /**
     * Selected delivery address ID.
     */
    private String selectedAddressId;

    /**
     * Selected payment method.
     */
    private PaymentMethod selectedPaymentMethod;

    /**
     * Special instructions for the order.
     */
    private String specialInstructions;

    /**
     * Scheduled delivery time (null for ASAP).
     */
    private Instant scheduledDeliveryTime;

    /**
     * Source of cart (web, mobile, ai_chat, diet_planner, party_planner).
     */
    private String source;

    /**
     * Diet plan ID if adding items from diet planner.
     */
    private String dietPlanId;

    /**
     * Party plan ID if adding items for a party.
     */
    private String partyPlanId;

    /**
     * AI chat session ID if adding items from chat.
     */
    private String chatSessionId;

    /**
     * Tip amount for delivery partner.
     */
    @Builder.Default
    private BigDecimal tip = BigDecimal.ZERO;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    /**
     * Adds an item to the cart. If the same item with same customizations exists,
     * increases the quantity.
     *
     * @param newItem the item to add
     */
    public void addItem(CartItem newItem) {
        Optional<CartItem> existingItem = findMatchingItem(newItem);
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + newItem.getQuantity());
            item.calculateTotalPrice();
        } else {
            newItem.calculateTotalPrice();
            items.add(newItem);
        }
    }

    /**
     * Updates the quantity of an item.
     *
     * @param itemId the item ID
     * @param quantity new quantity
     * @return true if item was updated
     */
    public boolean updateItemQuantity(String itemId, int quantity) {
        for (CartItem item : items) {
            if (item.getId().equals(itemId)) {
                if (quantity <= 0) {
                    items.remove(item);
                } else {
                    item.setQuantity(quantity);
                    item.calculateTotalPrice();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Removes an item from the cart.
     *
     * @param itemId the item ID to remove
     * @return true if item was removed
     */
    public boolean removeItem(String itemId) {
        return items.removeIf(item -> item.getId().equals(itemId));
    }

    /**
     * Clears all items from the cart.
     */
    public void clear() {
        items.clear();
        appliedCouponCode = null;
        couponDiscount = null;
        tip = BigDecimal.ZERO;
    }

    /**
     * Calculates the subtotal of all items.
     *
     * @return subtotal amount
     */
    public BigDecimal getSubtotal() {
        return items.stream()
            .map(CartItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Gets the total number of items in the cart.
     *
     * @return total item count
     */
    public int getTotalItemCount() {
        return items.stream()
            .mapToInt(CartItem::getQuantity)
            .sum();
    }

    /**
     * Gets distinct restaurant IDs in the cart.
     *
     * @return list of restaurant IDs
     */
    public List<String> getRestaurantIds() {
        return items.stream()
            .map(CartItem::getRestaurantId)
            .distinct()
            .toList();
    }

    /**
     * Checks if cart has items from multiple restaurants.
     *
     * @return true if multi-restaurant cart
     */
    public boolean isMultiRestaurant() {
        return getRestaurantIds().size() > 1;
    }

    /**
     * Checks if the cart is empty.
     *
     * @return true if cart has no items
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Finds a matching item (same menuItemId and customizations).
     */
    private Optional<CartItem> findMatchingItem(CartItem newItem) {
        return items.stream()
            .filter(item -> item.getMenuItemId().equals(newItem.getMenuItemId())
                && customizationsMatch(item.getCustomizations(), newItem.getCustomizations()))
            .findFirst();
    }

    /**
     * Checks if two customization lists match.
     */
    private boolean customizationsMatch(List<CartItemCustomization> list1, List<CartItemCustomization> list2) {
        if (list1 == null && list2 == null) return true;
        if (list1 == null || list2 == null) return false;
        if (list1.size() != list2.size()) return false;
        
        for (CartItemCustomization c1 : list1) {
            boolean found = list2.stream()
                .anyMatch(c2 -> c2.getCustomizationId().equals(c1.getCustomizationId())
                    && c2.getOptionId().equals(c1.getOptionId()));
            if (!found) return false;
        }
        return true;
    }
}

