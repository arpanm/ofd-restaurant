package com.foodai.order.mapper;

import com.foodai.order.domain.model.Cart;
import com.foodai.order.domain.model.CartItem;
import com.foodai.order.domain.model.CartItemCustomization;
import com.foodai.order.dto.request.AddCartItemRequest;
import com.foodai.order.dto.request.CartItemCustomizationDTO;
import com.foodai.order.dto.response.CartItemResponse;
import com.foodai.order.dto.response.CartResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for CartMapper.
 *
 * @author FoodAI Team
 */
@DisplayName("Cart Mapper Tests")
class CartMapperTest {

    private CartMapper cartMapper;

    @BeforeEach
    void setUp() {
        cartMapper = Mappers.getMapper(CartMapper.class);
    }

    @Nested
    @DisplayName("toResponse Tests")
    class ToResponseTests {

        @Test
        @DisplayName("Should map Cart to CartResponse with all fields")
        void shouldMapCartToResponse() {
            Cart cart = createFullCart();

            CartResponse response = cartMapper.toResponse(cart);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(cart.getId());
            assertThat(response.getUserId()).isEqualTo(cart.getUserId());
            assertThat(response.getItems()).hasSize(1);
            assertThat(response.getSubtotal()).isEqualTo(cart.getSubtotal());
            assertThat(response.getTotalItemCount()).isEqualTo(cart.getTotalItemCount());
        }

        @Test
        @DisplayName("Should handle null cart")
        void shouldHandleNullCart() {
            CartResponse response = cartMapper.toResponse(null);
            assertThat(response).isNull();
        }

        @Test
        @DisplayName("Should handle cart with empty items")
        void shouldHandleEmptyCart() {
            Cart cart = Cart.builder()
                .id("cart-123")
                .userId("user-123")
                .items(new ArrayList<>())
                .build();

            CartResponse response = cartMapper.toResponse(cart);

            assertThat(response).isNotNull();
            assertThat(response.getItems()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toItemResponse Tests")
    class ToItemResponseTests {

        @Test
        @DisplayName("Should map CartItem to CartItemResponse")
        void shouldMapCartItemToResponse() {
            CartItem item = createCartItem();

            CartItemResponse response = cartMapper.toItemResponse(item);

            assertThat(response).isNotNull();
            assertThat(response.getMenuItemId()).isEqualTo(item.getMenuItemId());
            assertThat(response.getQuantity()).isEqualTo(item.getQuantity());
            assertThat(response.getUnitPrice()).isEqualTo(item.getUnitPrice());
        }

        @Test
        @DisplayName("Should handle null item")
        void shouldHandleNullItem() {
            CartItemResponse response = cartMapper.toItemResponse(null);
            assertThat(response).isNull();
        }
    }

    @Nested
    @DisplayName("toItemResponseList Tests")
    class ToItemResponseListTests {

        @Test
        @DisplayName("Should map list of CartItems to responses")
        void shouldMapCartItemList() {
            List<CartItem> items = List.of(createCartItem(), createCartItem());

            List<CartItemResponse> responses = cartMapper.toItemResponseList(items);

            assertThat(responses).hasSize(2);
        }

        @Test
        @DisplayName("Should handle null list")
        void shouldHandleNullList() {
            List<CartItemResponse> responses = cartMapper.toItemResponseList(null);
            assertThat(responses).isNull();
        }

        @Test
        @DisplayName("Should handle empty list")
        void shouldHandleEmptyList() {
            List<CartItemResponse> responses = cartMapper.toItemResponseList(Collections.emptyList());
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toCartItem Tests")
    class ToCartItemTests {

        @Test
        @DisplayName("Should map AddCartItemRequest to CartItem")
        void shouldMapRequestToCartItem() {
            AddCartItemRequest request = AddCartItemRequest.builder()
                .menuItemId("menu-123")
                .name("Butter Chicken")
                .restaurantId("restaurant-123")
                .quantity(2)
                .unitPrice(new BigDecimal("350.00"))
                .customizations(List.of(createCustomizationDTO()))
                .build();

            CartItem item = cartMapper.toCartItem(request);

            assertThat(item).isNotNull();
            assertThat(item.getMenuItemId()).isEqualTo(request.getMenuItemId());
            assertThat(item.getQuantity()).isEqualTo(request.getQuantity());
            assertThat(item.getUnitPrice()).isEqualTo(request.getUnitPrice());
        }

        @Test
        @DisplayName("Should handle null request")
        void shouldHandleNullRequest() {
            CartItem item = cartMapper.toCartItem(null);
            assertThat(item).isNull();
        }
    }

    @Nested
    @DisplayName("Customization Mapping Tests")
    class CustomizationMappingTests {

        @Test
        @DisplayName("Should map customization DTO to domain")
        void shouldMapCustomizationDTOToDomain() {
            CartItemCustomizationDTO dto = createCustomizationDTO();

            CartItemCustomization customization = cartMapper.toCustomization(dto);

            assertThat(customization).isNotNull();
            assertThat(customization.getCustomizationId()).isEqualTo(dto.getCustomizationId());
            assertThat(customization.getOptionId()).isEqualTo(dto.getOptionId());
        }

        @Test
        @DisplayName("Should map customization domain to DTO")
        void shouldMapCustomizationToDTO() {
            CartItemCustomization customization = CartItemCustomization.builder()
                .customizationId("size")
                .customizationName("Size")
                .optionId("large")
                .optionName("Large")
                .additionalCost(new BigDecimal("50.00"))
                .build();

            CartItemCustomizationDTO dto = cartMapper.toCustomizationDTO(customization);

            assertThat(dto).isNotNull();
            assertThat(dto.getCustomizationId()).isEqualTo(customization.getCustomizationId());
            assertThat(dto.getOptionId()).isEqualTo(customization.getOptionId());
        }

        @Test
        @DisplayName("Should map list of customization DTOs to domain")
        void shouldMapCustomizationDTOList() {
            List<CartItemCustomizationDTO> dtos = List.of(createCustomizationDTO());

            List<CartItemCustomization> customizations = cartMapper.toCustomizationList(dtos);

            assertThat(customizations).hasSize(1);
        }

        @Test
        @DisplayName("Should map list of customizations to DTOs")
        void shouldMapCustomizationListToDTOs() {
            CartItemCustomization customization = CartItemCustomization.builder()
                .customizationId("spice")
                .customizationName("Spice Level")
                .optionId("medium")
                .optionName("Medium")
                .additionalCost(BigDecimal.ZERO)
                .build();
            List<CartItemCustomization> customizations = List.of(customization);

            List<CartItemCustomizationDTO> dtos = cartMapper.toCustomizationDTOList(customizations);

            assertThat(dtos).hasSize(1);
        }

        @Test
        @DisplayName("Should handle null customization lists")
        void shouldHandleNullCustomizationLists() {
            assertThat(cartMapper.toCustomizationList(null)).isNull();
            assertThat(cartMapper.toCustomizationDTOList(null)).isNull();
        }
    }

    // Helper methods
    private Cart createFullCart() {
        CartItem item = createCartItem();
        List<CartItem> items = new ArrayList<>();
        items.add(item);

        return Cart.builder()
            .id("cart-123")
            .userId("user-123")
            .items(items)
            .appliedCouponCode("SAVE20")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }

    private CartItem createCartItem() {
        CartItem item = CartItem.builder()
            .id(UUID.randomUUID().toString())
            .menuItemId("menu-123")
            .name("Butter Chicken")
            .restaurantId("restaurant-123")
            .quantity(2)
            .unitPrice(new BigDecimal("350.00"))
            .customizations(List.of(CartItemCustomization.builder()
                .customizationId("spice")
                .customizationName("Spice Level")
                .optionId("medium")
                .optionName("Medium")
                .additionalCost(BigDecimal.ZERO)
                .build()))
            .build();
        item.calculateTotalPrice();
        return item;
    }

    private CartItemCustomizationDTO createCustomizationDTO() {
        return CartItemCustomizationDTO.builder()
            .customizationId("spice")
            .customizationName("Spice Level")
            .optionId("medium")
            .optionName("Medium")
            .additionalCost(BigDecimal.ZERO)
            .build();
    }
}
