package com.foodai.order.mapper;

import com.foodai.order.domain.model.Cart;
import com.foodai.order.domain.model.CartItem;
import com.foodai.order.domain.model.CartItemCustomization;
import com.foodai.order.dto.request.AddCartItemRequest;
import com.foodai.order.dto.request.CartItemCustomizationDTO;
import com.foodai.order.dto.response.CartItemResponse;
import com.foodai.order.dto.response.CartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * MapStruct mapper for Cart and related DTOs.
 *
 * @author FoodAI Team
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartMapper {

    /**
     * Maps Cart entity to CartResponse.
     */
    @Mapping(target = "subtotal", expression = "java(cart.getSubtotal())")
    @Mapping(target = "totalItemCount", expression = "java(cart.getTotalItemCount())")
    @Mapping(target = "restaurantIds", expression = "java(cart.getRestaurantIds())")
    @Mapping(target = "isMultiRestaurant", expression = "java(cart.isMultiRestaurant())")
    CartResponse toResponse(Cart cart);

    /**
     * Maps CartItem to CartItemResponse.
     */
    CartItemResponse toItemResponse(CartItem item);

    /**
     * Maps list of CartItems to list of CartItemResponses.
     */
    List<CartItemResponse> toItemResponseList(List<CartItem> items);

    /**
     * Maps AddCartItemRequest to CartItem.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    CartItem toCartItem(AddCartItemRequest request);

    /**
     * Maps CartItemCustomizationDTO to CartItemCustomization.
     */
    CartItemCustomization toCustomization(CartItemCustomizationDTO dto);

    /**
     * Maps CartItemCustomization to CartItemCustomizationDTO.
     */
    CartItemCustomizationDTO toCustomizationDTO(CartItemCustomization customization);

    /**
     * Maps list of customizations.
     */
    List<CartItemCustomization> toCustomizationList(List<CartItemCustomizationDTO> dtos);

    /**
     * Maps list of customizations to DTOs.
     */
    List<CartItemCustomizationDTO> toCustomizationDTOList(List<CartItemCustomization> customizations);
}

