package com.foodai.order.service;

import com.foodai.order.domain.model.Cart;
import com.foodai.order.domain.model.CartItem;
import com.foodai.order.domain.repository.CartRepository;
import com.foodai.order.dto.request.AddCartItemRequest;
import com.foodai.order.dto.request.ApplyCouponRequest;
import com.foodai.order.dto.request.UpdateCartItemRequest;
import com.foodai.order.dto.response.CartResponse;
import com.foodai.order.exception.CartNotFoundException;
import com.foodai.order.mapper.CartMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CartService.
 *
 * @author FoodAI Team
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Cart Service Tests")
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartService cartService;

    private Cart cart;
    private CartResponse cartResponse;
    private final String USER_ID = "user-123";

    @BeforeEach
    void setUp() {
        cart = Cart.builder()
            .id("cart-123")
            .userId(USER_ID)
            .items(new ArrayList<>())
            .build();

        cartResponse = CartResponse.builder()
            .id("cart-123")
            .userId(USER_ID)
            .items(new ArrayList<>())
            .subtotal(BigDecimal.ZERO)
            .totalItemCount(0)
            .build();
    }

    @Nested
    @DisplayName("Get Cart Tests")
    class GetCartTests {

        @Test
        @DisplayName("Should return existing cart")
        void shouldReturnExistingCart() {
            when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
            when(cartMapper.toResponse(cart)).thenReturn(cartResponse);

            CartResponse result = cartService.getCart(USER_ID);

            assertNotNull(result);
            assertEquals(USER_ID, result.getUserId());
            verify(cartRepository).findByUserId(USER_ID);
        }

        @Test
        @DisplayName("Should create new cart if not exists")
        void shouldCreateNewCartIfNotExists() {
            when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);
            when(cartMapper.toResponse(cart)).thenReturn(cartResponse);

            CartResponse result = cartService.getCart(USER_ID);

            assertNotNull(result);
            verify(cartRepository).save(any(Cart.class));
        }
    }

    @Nested
    @DisplayName("Add Item Tests")
    class AddItemTests {

        @Test
        @DisplayName("Should add item to cart")
        void shouldAddItemToCart() {
            AddCartItemRequest request = AddCartItemRequest.builder()
                .menuItemId("menu-123")
                .name("Chicken Biryani")
                .restaurantId("rest-123")
                .restaurantName("Biryani Blues")
                .unitPrice(new BigDecimal("299.00"))
                .quantity(2)
                .build();

            CartItem cartItem = CartItem.builder()
                .menuItemId("menu-123")
                .name("Chicken Biryani")
                .unitPrice(new BigDecimal("299.00"))
                .quantity(2)
                .customizations(new ArrayList<>())
                .build();

            when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
            when(cartMapper.toCartItem(request)).thenReturn(cartItem);
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);
            when(cartMapper.toResponse(cart)).thenReturn(cartResponse);

            CartResponse result = cartService.addItem(USER_ID, request);

            assertNotNull(result);
            verify(cartRepository).save(any(Cart.class));
        }
    }

    @Nested
    @DisplayName("Update Item Tests")
    class UpdateItemTests {

        @Test
        @DisplayName("Should update item quantity")
        void shouldUpdateItemQuantity() {
            String itemId = "item-123";
            CartItem existingItem = CartItem.builder()
                .id(itemId)
                .menuItemId("menu-123")
                .unitPrice(new BigDecimal("299.00"))
                .quantity(1)
                .build();
            existingItem.calculateTotalPrice();
            cart.getItems().add(existingItem);

            UpdateCartItemRequest request = UpdateCartItemRequest.builder()
                .quantity(3)
                .build();

            when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);
            when(cartMapper.toResponse(cart)).thenReturn(cartResponse);

            CartResponse result = cartService.updateItem(USER_ID, itemId, request);

            assertNotNull(result);
            assertEquals(3, cart.getItems().get(0).getQuantity());
        }

        @Test
        @DisplayName("Should throw when item not found")
        void shouldThrowWhenItemNotFound() {
            when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));

            UpdateCartItemRequest request = UpdateCartItemRequest.builder()
                .quantity(3)
                .build();

            assertThrows(IllegalArgumentException.class,
                () -> cartService.updateItem(USER_ID, "nonexistent", request));
        }

        @Test
        @DisplayName("Should throw when cart not found")
        void shouldThrowWhenCartNotFound() {
            when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

            UpdateCartItemRequest request = UpdateCartItemRequest.builder()
                .quantity(3)
                .build();

            assertThrows(CartNotFoundException.class,
                () -> cartService.updateItem(USER_ID, "item-123", request));
        }
    }

    @Nested
    @DisplayName("Remove Item Tests")
    class RemoveItemTests {

        @Test
        @DisplayName("Should remove item from cart")
        void shouldRemoveItemFromCart() {
            String itemId = "item-123";
            CartItem existingItem = CartItem.builder()
                .id(itemId)
                .menuItemId("menu-123")
                .build();
            cart.getItems().add(existingItem);

            when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);
            when(cartMapper.toResponse(cart)).thenReturn(cartResponse);

            CartResponse result = cartService.removeItem(USER_ID, itemId);

            assertNotNull(result);
            assertTrue(cart.getItems().isEmpty());
        }
    }

    @Nested
    @DisplayName("Coupon Tests")
    class CouponTests {

        @Test
        @DisplayName("Should apply coupon to cart")
        void shouldApplyCoupon() {
            CartItem item = CartItem.builder()
                .menuItemId("menu-123")
                .unitPrice(new BigDecimal("500.00"))
                .quantity(1)
                .totalPrice(new BigDecimal("500.00"))
                .build();
            cart.getItems().add(item);

            ApplyCouponRequest request = ApplyCouponRequest.builder()
                .couponCode("FIRST50")
                .build();

            when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);
            when(cartMapper.toResponse(cart)).thenReturn(cartResponse);

            CartResponse result = cartService.applyCoupon(USER_ID, request);

            assertNotNull(result);
            assertEquals("FIRST50", cart.getAppliedCouponCode());
            assertNotNull(cart.getCouponDiscount());
        }

        @Test
        @DisplayName("Should remove coupon from cart")
        void shouldRemoveCoupon() {
            cart.setAppliedCouponCode("TEST50");
            cart.setCouponDiscount(new BigDecimal("50.00"));

            when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);
            when(cartMapper.toResponse(cart)).thenReturn(cartResponse);

            CartResponse result = cartService.removeCoupon(USER_ID);

            assertNotNull(result);
            assertNull(cart.getAppliedCouponCode());
            assertNull(cart.getCouponDiscount());
        }
    }

    @Nested
    @DisplayName("Clear Cart Tests")
    class ClearCartTests {

        @Test
        @DisplayName("Should clear cart")
        void shouldClearCart() {
            CartItem item = CartItem.builder()
                .menuItemId("menu-123")
                .build();
            cart.getItems().add(item);

            when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
            when(cartRepository.save(any(Cart.class))).thenReturn(cart);
            when(cartMapper.toResponse(cart)).thenReturn(cartResponse);

            CartResponse result = cartService.clearCart(USER_ID);

            assertNotNull(result);
            assertTrue(cart.getItems().isEmpty());
        }
    }
}

