package com.foodai.order.mapper;

import com.foodai.order.domain.model.Cart;
import com.foodai.order.domain.model.CartItem;
import com.foodai.order.domain.model.CartItemCustomization;
import com.foodai.order.dto.request.AddCartItemRequest;
import com.foodai.order.dto.request.CartItemCustomizationDTO;
import com.foodai.order.dto.response.CartItemResponse;
import com.foodai.order.dto.response.CartResponse;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-10T15:29:35+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class CartMapperImpl implements CartMapper {

    @Override
    public CartResponse toResponse(Cart cart) {
        if ( cart == null ) {
            return null;
        }

        CartResponse.CartResponseBuilder cartResponse = CartResponse.builder();

        cartResponse.appliedCouponCode( cart.getAppliedCouponCode() );
        cartResponse.couponDiscount( cart.getCouponDiscount() );
        cartResponse.createdAt( cart.getCreatedAt() );
        cartResponse.id( cart.getId() );
        cartResponse.items( toItemResponseList( cart.getItems() ) );
        cartResponse.scheduledDeliveryTime( cart.getScheduledDeliveryTime() );
        cartResponse.selectedAddressId( cart.getSelectedAddressId() );
        cartResponse.selectedPaymentMethod( cart.getSelectedPaymentMethod() );
        cartResponse.specialInstructions( cart.getSpecialInstructions() );
        cartResponse.tip( cart.getTip() );
        cartResponse.updatedAt( cart.getUpdatedAt() );
        cartResponse.userId( cart.getUserId() );

        cartResponse.subtotal( cart.getSubtotal() );
        cartResponse.totalItemCount( cart.getTotalItemCount() );
        cartResponse.restaurantIds( cart.getRestaurantIds() );
        cartResponse.isMultiRestaurant( cart.isMultiRestaurant() );

        return cartResponse.build();
    }

    @Override
    public CartItemResponse toItemResponse(CartItem item) {
        if ( item == null ) {
            return null;
        }

        CartItemResponse.CartItemResponseBuilder cartItemResponse = CartItemResponse.builder();

        cartItemResponse.customizations( toCustomizationDTOList( item.getCustomizations() ) );
        cartItemResponse.description( item.getDescription() );
        cartItemResponse.id( item.getId() );
        cartItemResponse.imageUrl( item.getImageUrl() );
        cartItemResponse.menuItemId( item.getMenuItemId() );
        cartItemResponse.name( item.getName() );
        cartItemResponse.preparationTime( item.getPreparationTime() );
        cartItemResponse.quantity( item.getQuantity() );
        cartItemResponse.restaurantId( item.getRestaurantId() );
        cartItemResponse.restaurantName( item.getRestaurantName() );
        cartItemResponse.specialInstructions( item.getSpecialInstructions() );
        cartItemResponse.spiceLevel( item.getSpiceLevel() );
        cartItemResponse.totalPrice( item.getTotalPrice() );
        cartItemResponse.unitPrice( item.getUnitPrice() );
        cartItemResponse.vegetarian( item.getVegetarian() );

        return cartItemResponse.build();
    }

    @Override
    public List<CartItemResponse> toItemResponseList(List<CartItem> items) {
        if ( items == null ) {
            return null;
        }

        List<CartItemResponse> list = new ArrayList<CartItemResponse>( items.size() );
        for ( CartItem cartItem : items ) {
            list.add( toItemResponse( cartItem ) );
        }

        return list;
    }

    @Override
    public CartItem toCartItem(AddCartItemRequest request) {
        if ( request == null ) {
            return null;
        }

        CartItem.CartItemBuilder cartItem = CartItem.builder();

        cartItem.customizations( toCustomizationList( request.getCustomizations() ) );
        cartItem.description( request.getDescription() );
        cartItem.imageUrl( request.getImageUrl() );
        cartItem.menuItemId( request.getMenuItemId() );
        cartItem.name( request.getName() );
        cartItem.preparationTime( request.getPreparationTime() );
        cartItem.quantity( request.getQuantity() );
        cartItem.restaurantId( request.getRestaurantId() );
        cartItem.restaurantName( request.getRestaurantName() );
        cartItem.specialInstructions( request.getSpecialInstructions() );
        cartItem.spiceLevel( request.getSpiceLevel() );
        cartItem.unitPrice( request.getUnitPrice() );
        cartItem.vegetarian( request.getVegetarian() );

        return cartItem.build();
    }

    @Override
    public CartItemCustomization toCustomization(CartItemCustomizationDTO dto) {
        if ( dto == null ) {
            return null;
        }

        CartItemCustomization.CartItemCustomizationBuilder cartItemCustomization = CartItemCustomization.builder();

        cartItemCustomization.additionalCost( dto.getAdditionalCost() );
        cartItemCustomization.customizationId( dto.getCustomizationId() );
        cartItemCustomization.customizationName( dto.getCustomizationName() );
        cartItemCustomization.optionId( dto.getOptionId() );
        cartItemCustomization.optionName( dto.getOptionName() );

        return cartItemCustomization.build();
    }

    @Override
    public CartItemCustomizationDTO toCustomizationDTO(CartItemCustomization customization) {
        if ( customization == null ) {
            return null;
        }

        CartItemCustomizationDTO.CartItemCustomizationDTOBuilder cartItemCustomizationDTO = CartItemCustomizationDTO.builder();

        cartItemCustomizationDTO.additionalCost( customization.getAdditionalCost() );
        cartItemCustomizationDTO.customizationId( customization.getCustomizationId() );
        cartItemCustomizationDTO.customizationName( customization.getCustomizationName() );
        cartItemCustomizationDTO.optionId( customization.getOptionId() );
        cartItemCustomizationDTO.optionName( customization.getOptionName() );

        return cartItemCustomizationDTO.build();
    }

    @Override
    public List<CartItemCustomization> toCustomizationList(List<CartItemCustomizationDTO> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<CartItemCustomization> list = new ArrayList<CartItemCustomization>( dtos.size() );
        for ( CartItemCustomizationDTO cartItemCustomizationDTO : dtos ) {
            list.add( toCustomization( cartItemCustomizationDTO ) );
        }

        return list;
    }

    @Override
    public List<CartItemCustomizationDTO> toCustomizationDTOList(List<CartItemCustomization> customizations) {
        if ( customizations == null ) {
            return null;
        }

        List<CartItemCustomizationDTO> list = new ArrayList<CartItemCustomizationDTO>( customizations.size() );
        for ( CartItemCustomization cartItemCustomization : customizations ) {
            list.add( toCustomizationDTO( cartItemCustomization ) );
        }

        return list;
    }
}
