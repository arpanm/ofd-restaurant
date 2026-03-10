package com.foodai.order.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Cart domain entity.
 *
 * @author FoodAI Team
 */
@DisplayName("Cart Entity Tests")
class CartTest {

    private Cart cart;

    @BeforeEach
    void setUp() {
        cart = Cart.builder()
            .id("cart-123")
            .userId("user-123")
            .items(new ArrayList<>())
            .build();
    }

    @Nested
    @DisplayName("Add Item Tests")
    class AddItemTests {

        @Test
        @DisplayName("Should add new item to cart")
        void shouldAddNewItem() {
            CartItem item = createCartItem("menu-1", "rest-1", 2);
            
            cart.addItem(item);
            
            assertEquals(1, cart.getItems().size());
            assertEquals(2, cart.getItems().get(0).getQuantity());
        }

        @Test
        @DisplayName("Should increase quantity when adding same item")
        void shouldIncreaseQuantityForSameItem() {
            CartItem item1 = createCartItem("menu-1", "rest-1", 2);
            CartItem item2 = createCartItem("menu-1", "rest-1", 1);
            
            cart.addItem(item1);
            cart.addItem(item2);
            
            assertEquals(1, cart.getItems().size());
            assertEquals(3, cart.getItems().get(0).getQuantity());
        }

        @Test
        @DisplayName("Should add as separate item when customizations differ")
        void shouldAddSeparateItemWithDifferentCustomizations() {
            CartItem item1 = createCartItem("menu-1", "rest-1", 1);
            item1.setCustomizations(List.of(
                CartItemCustomization.builder()
                    .customizationId("size")
                    .optionId("large")
                    .build()
            ));
            
            CartItem item2 = createCartItem("menu-1", "rest-1", 1);
            item2.setCustomizations(List.of(
                CartItemCustomization.builder()
                    .customizationId("size")
                    .optionId("medium")
                    .build()
            ));
            
            cart.addItem(item1);
            cart.addItem(item2);
            
            assertEquals(2, cart.getItems().size());
        }
    }

    @Nested
    @DisplayName("Update Item Tests")
    class UpdateItemTests {

        @Test
        @DisplayName("Should update item quantity")
        void shouldUpdateItemQuantity() {
            CartItem item = createCartItem("menu-1", "rest-1", 2);
            cart.addItem(item);
            
            boolean result = cart.updateItemQuantity(item.getId(), 5);
            
            assertTrue(result);
            assertEquals(5, cart.getItems().get(0).getQuantity());
        }

        @Test
        @DisplayName("Should remove item when quantity is 0")
        void shouldRemoveItemWhenQuantityIsZero() {
            CartItem item = createCartItem("menu-1", "rest-1", 2);
            cart.addItem(item);
            
            boolean result = cart.updateItemQuantity(item.getId(), 0);
            
            assertTrue(result);
            assertTrue(cart.getItems().isEmpty());
        }

        @Test
        @DisplayName("Should return false when item not found")
        void shouldReturnFalseWhenItemNotFound() {
            boolean result = cart.updateItemQuantity("nonexistent", 5);
            
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("Remove Item Tests")
    class RemoveItemTests {

        @Test
        @DisplayName("Should remove item from cart")
        void shouldRemoveItem() {
            CartItem item = createCartItem("menu-1", "rest-1", 2);
            cart.addItem(item);
            
            boolean result = cart.removeItem(item.getId());
            
            assertTrue(result);
            assertTrue(cart.getItems().isEmpty());
        }

        @Test
        @DisplayName("Should return false when removing nonexistent item")
        void shouldReturnFalseWhenRemovingNonexistent() {
            boolean result = cart.removeItem("nonexistent");
            
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("Cart Calculations Tests")
    class CartCalculationsTests {

        @Test
        @DisplayName("Should calculate subtotal correctly")
        void shouldCalculateSubtotal() {
            CartItem item1 = createCartItem("menu-1", "rest-1", 2); // 299 * 2 = 598
            CartItem item2 = createCartItem("menu-2", "rest-2", 1); // 299 * 1 = 299
            
            cart.addItem(item1);
            cart.addItem(item2);
            
            assertEquals(new BigDecimal("897.00"), cart.getSubtotal());
        }

        @Test
        @DisplayName("Should calculate total item count")
        void shouldCalculateTotalItemCount() {
            CartItem item1 = createCartItem("menu-1", "rest-1", 2);
            CartItem item2 = createCartItem("menu-2", "rest-2", 3);
            
            cart.addItem(item1);
            cart.addItem(item2);
            
            assertEquals(5, cart.getTotalItemCount());
        }

        @Test
        @DisplayName("Should identify multi-restaurant cart")
        void shouldIdentifyMultiRestaurantCart() {
            CartItem item1 = createCartItem("menu-1", "rest-1", 1);
            CartItem item2 = createCartItem("menu-2", "rest-2", 1);
            
            cart.addItem(item1);
            cart.addItem(item2);
            
            assertTrue(cart.isMultiRestaurant());
            assertEquals(2, cart.getRestaurantIds().size());
        }

        @Test
        @DisplayName("Should not identify single restaurant cart as multi")
        void shouldNotIdentifySingleRestaurantAsMulti() {
            CartItem item1 = createCartItem("menu-1", "rest-1", 1);
            CartItem item2 = createCartItem("menu-2", "rest-1", 1);
            
            cart.addItem(item1);
            cart.addItem(item2);
            
            assertFalse(cart.isMultiRestaurant());
        }
    }

    @Nested
    @DisplayName("Clear Cart Tests")
    class ClearCartTests {

        @Test
        @DisplayName("Should clear all items")
        void shouldClearAllItems() {
            cart.addItem(createCartItem("menu-1", "rest-1", 2));
            cart.addItem(createCartItem("menu-2", "rest-2", 1));
            cart.setAppliedCouponCode("TEST50");
            cart.setCouponDiscount(new BigDecimal("50.00"));
            
            cart.clear();
            
            assertTrue(cart.isEmpty());
            assertNull(cart.getAppliedCouponCode());
            assertNull(cart.getCouponDiscount());
        }

        @Test
        @DisplayName("isEmpty should return true for empty cart")
        void shouldReturnTrueForEmptyCart() {
            assertTrue(cart.isEmpty());
        }

        @Test
        @DisplayName("isEmpty should return false for non-empty cart")
        void shouldReturnFalseForNonEmptyCart() {
            cart.addItem(createCartItem("menu-1", "rest-1", 1));
            
            assertFalse(cart.isEmpty());
        }
    }

    // Helper method
    private CartItem createCartItem(String menuItemId, String restaurantId, int quantity) {
        CartItem item = CartItem.builder()
            .menuItemId(menuItemId)
            .name("Test Item")
            .restaurantId(restaurantId)
            .restaurantName("Test Restaurant")
            .unitPrice(new BigDecimal("299.00"))
            .quantity(quantity)
            .customizations(new ArrayList<>())
            .build();
        item.calculateTotalPrice();
        return item;
    }
}

