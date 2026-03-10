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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 * Service for cart management operations.
 *
 * @author FoodAI Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    /**
     * Gets the cart for a user, creating one if it doesn't exist.
     *
     * @param userId the user ID
     * @return cart response
     */
    @Transactional(readOnly = true)
    public CartResponse getCart(String userId) {
        log.debug("Getting cart for user", kv("userId", userId));
        Cart cart = getOrCreateCart(userId);
        return cartMapper.toResponse(cart);
    }

    /**
     * Adds an item to the user's cart.
     *
     * @param userId the user ID
     * @param request the add item request
     * @return updated cart response
     */
    public CartResponse addItem(String userId, AddCartItemRequest request) {
        log.info("Adding item to cart", kv("userId", userId), kv("menuItemId", request.getMenuItemId()));
        
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartMapper.toCartItem(request);
        cart.addItem(item);
        
        Cart savedCart = cartRepository.save(cart);
        log.info("Item added to cart", kv("userId", userId), kv("itemId", item.getId()));
        
        return cartMapper.toResponse(savedCart);
    }

    /**
     * Updates an item in the cart.
     *
     * @param userId the user ID
     * @param itemId the cart item ID
     * @param request the update request
     * @return updated cart response
     */
    public CartResponse updateItem(String userId, String itemId, UpdateCartItemRequest request) {
        log.info("Updating cart item", kv("userId", userId), kv("itemId", itemId));
        
        Cart cart = findCartByUserId(userId);
        boolean updated = cart.updateItemQuantity(itemId, request.getQuantity());
        
        if (!updated) {
            throw new IllegalArgumentException("Item not found in cart: " + itemId);
        }
        
        // Update special instructions if provided
        if (request.getSpecialInstructions() != null) {
            cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .ifPresent(item -> item.setSpecialInstructions(request.getSpecialInstructions()));
        }
        
        Cart savedCart = cartRepository.save(cart);
        log.info("Cart item updated", kv("userId", userId), kv("itemId", itemId));
        
        return cartMapper.toResponse(savedCart);
    }

    /**
     * Removes an item from the cart.
     *
     * @param userId the user ID
     * @param itemId the cart item ID
     * @return updated cart response
     */
    public CartResponse removeItem(String userId, String itemId) {
        log.info("Removing item from cart", kv("userId", userId), kv("itemId", itemId));
        
        Cart cart = findCartByUserId(userId);
        boolean removed = cart.removeItem(itemId);
        
        if (!removed) {
            throw new IllegalArgumentException("Item not found in cart: " + itemId);
        }
        
        Cart savedCart = cartRepository.save(cart);
        log.info("Item removed from cart", kv("userId", userId), kv("itemId", itemId));
        
        return cartMapper.toResponse(savedCart);
    }

    /**
     * Clears all items from the cart.
     *
     * @param userId the user ID
     * @return empty cart response
     */
    public CartResponse clearCart(String userId) {
        log.info("Clearing cart", kv("userId", userId));
        
        Cart cart = findCartByUserId(userId);
        cart.clear();
        
        Cart savedCart = cartRepository.save(cart);
        log.info("Cart cleared", kv("userId", userId));
        
        return cartMapper.toResponse(savedCart);
    }

    /**
     * Applies a coupon to the cart.
     *
     * @param userId the user ID
     * @param request the coupon request
     * @return updated cart response
     */
    public CartResponse applyCoupon(String userId, ApplyCouponRequest request) {
        log.info("Applying coupon", kv("userId", userId), kv("couponCode", request.getCouponCode()));
        
        Cart cart = findCartByUserId(userId);
        
        // TODO: Validate coupon with promotion service
        // For now, simulate a simple discount
        BigDecimal discount = calculateCouponDiscount(request.getCouponCode(), cart.getSubtotal());
        
        cart.setAppliedCouponCode(request.getCouponCode());
        cart.setCouponDiscount(discount);
        
        Cart savedCart = cartRepository.save(cart);
        log.info("Coupon applied", kv("userId", userId), kv("discount", discount));
        
        return cartMapper.toResponse(savedCart);
    }

    /**
     * Removes the applied coupon from the cart.
     *
     * @param userId the user ID
     * @return updated cart response
     */
    public CartResponse removeCoupon(String userId) {
        log.info("Removing coupon", kv("userId", userId));
        
        Cart cart = findCartByUserId(userId);
        cart.setAppliedCouponCode(null);
        cart.setCouponDiscount(null);
        
        Cart savedCart = cartRepository.save(cart);
        log.info("Coupon removed", kv("userId", userId));
        
        return cartMapper.toResponse(savedCart);
    }

    /**
     * Updates cart settings (address, payment method, tip, etc.).
     *
     * @param userId the user ID
     * @param addressId selected address ID
     * @param tip tip amount
     * @param scheduledTime scheduled delivery time
     * @return updated cart response
     */
    public CartResponse updateCartSettings(String userId, String addressId, BigDecimal tip, Instant scheduledTime) {
        log.info("Updating cart settings", kv("userId", userId));
        
        Cart cart = findCartByUserId(userId);
        
        if (addressId != null) {
            cart.setSelectedAddressId(addressId);
        }
        if (tip != null) {
            cart.setTip(tip);
        }
        if (scheduledTime != null) {
            cart.setScheduledDeliveryTime(scheduledTime);
        }
        
        Cart savedCart = cartRepository.save(cart);
        log.info("Cart settings updated", kv("userId", userId));
        
        return cartMapper.toResponse(savedCart);
    }

    /**
     * Gets the cart entity for order creation.
     *
     * @param userId the user ID
     * @return the cart entity
     */
    public Cart getCartEntity(String userId) {
        return findCartByUserId(userId);
    }

    /**
     * Deletes the cart after order creation.
     *
     * @param userId the user ID
     */
    public void deleteCart(String userId) {
        cartRepository.deleteByUserId(userId);
        log.info("Cart deleted after order creation", kv("userId", userId));
    }

    // Private helper methods

    private Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId)
            .orElseGet(() -> {
                Cart newCart = Cart.builder()
                    .userId(userId)
                    .build();
                return cartRepository.save(newCart);
            });
    }

    private Cart findCartByUserId(String userId) {
        return cartRepository.findByUserId(userId)
            .orElseThrow(() -> new CartNotFoundException(userId, true));
    }

    private BigDecimal calculateCouponDiscount(String couponCode, BigDecimal subtotal) {
        // TODO: Integrate with promotion service
        // Simulated discount logic for now
        return switch (couponCode.toUpperCase()) {
            case "FIRST50" -> BigDecimal.valueOf(50);
            case "SAVE20" -> subtotal.multiply(BigDecimal.valueOf(0.20)).min(BigDecimal.valueOf(100));
            case "FOODIE100" -> BigDecimal.valueOf(100);
            default -> BigDecimal.ZERO;
        };
    }
}

