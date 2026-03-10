/**
 * Cart Hook - Custom hook for cart management
 */

import { useEffect, useCallback } from 'react';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import {
  fetchCart,
  addToCart,
  updateCartItem,
  removeFromCart,
  clearCart,
  applyCoupon,
  removeCoupon,
  setDeliveryAddress,
  setSpecialInstructions,
  validateCart,
  getCheckoutSummary,
  clearError,
  openCart,
  closeCart,
  toggleCart,
  updateItemQuantityOptimistic,
} from '@/store/slices/cartSlice';
import type { AddToCartRequest, UpdateCartItemRequest, PaymentMethod } from '@/types/api.types';

export const useCart = () => {
  const dispatch = useAppDispatch();
  const {
    cart,
    checkoutSummary,
    isLoading,
    isUpdating,
    error,
    isCartOpen,
  } = useAppSelector((state) => state.cart);
  const { isAuthenticated } = useAppSelector((state) => state.auth);

  // Fetch cart when authenticated
  useEffect(() => {
    if (isAuthenticated) {
      dispatch(fetchCart());
    }
  }, [dispatch, isAuthenticated]);

  const handleAddToCart = useCallback(
    async (data: AddToCartRequest) => {
      const result = await dispatch(addToCart(data));
      return result;
    },
    [dispatch]
  );

  const handleUpdateCartItem = useCallback(
    async (itemId: string, data: UpdateCartItemRequest) => {
      // Optimistic update for quantity
      if (data.quantity !== undefined) {
        dispatch(updateItemQuantityOptimistic({ itemId, quantity: data.quantity }));
      }
      const result = await dispatch(updateCartItem({ itemId, data }));
      return result;
    },
    [dispatch]
  );

  const handleRemoveFromCart = useCallback(
    async (itemId: string) => {
      const result = await dispatch(removeFromCart(itemId));
      return result;
    },
    [dispatch]
  );

  const handleClearCart = useCallback(async () => {
    const result = await dispatch(clearCart());
    return result;
  }, [dispatch]);

  const handleApplyCoupon = useCallback(
    async (couponCode: string) => {
      const result = await dispatch(applyCoupon(couponCode));
      return result;
    },
    [dispatch]
  );

  const handleRemoveCoupon = useCallback(async () => {
    const result = await dispatch(removeCoupon());
    return result;
  }, [dispatch]);

  const handleSetDeliveryAddress = useCallback(
    async (addressId: string) => {
      const result = await dispatch(setDeliveryAddress(addressId));
      return result;
    },
    [dispatch]
  );

  const handleSetSpecialInstructions = useCallback(
    async (instructions: string) => {
      const result = await dispatch(setSpecialInstructions(instructions));
      return result;
    },
    [dispatch]
  );

  const handleValidateCart = useCallback(async () => {
    const result = await dispatch(validateCart());
    return result;
  }, [dispatch]);

  const handleGetCheckoutSummary = useCallback(
    async (paymentMethod: PaymentMethod, tipAmount?: number) => {
      const result = await dispatch(getCheckoutSummary({ paymentMethod, tipAmount }));
      return result;
    },
    [dispatch]
  );

  const handleOpenCart = useCallback(() => {
    dispatch(openCart());
  }, [dispatch]);

  const handleCloseCart = useCallback(() => {
    dispatch(closeCart());
  }, [dispatch]);

  const handleToggleCart = useCallback(() => {
    dispatch(toggleCart());
  }, [dispatch]);

  const handleClearError = useCallback(() => {
    dispatch(clearError());
  }, [dispatch]);

  // Computed values
  const itemCount = cart?.items?.reduce((total, item) => total + item.quantity, 0) || 0;
  const isEmpty = !cart || cart.items.length === 0;
  const hasMultipleRestaurants = cart?.isMultiRestaurant || false;

  const getItemsByRestaurant = useCallback(() => {
    if (!cart) return [];
    return cart.restaurantGroups || [];
  }, [cart]);

  return {
    cart,
    checkoutSummary,
    isLoading,
    isUpdating,
    error,
    isCartOpen,
    itemCount,
    isEmpty,
    hasMultipleRestaurants,
    addToCart: handleAddToCart,
    updateCartItem: handleUpdateCartItem,
    removeFromCart: handleRemoveFromCart,
    clearCart: handleClearCart,
    applyCoupon: handleApplyCoupon,
    removeCoupon: handleRemoveCoupon,
    setDeliveryAddress: handleSetDeliveryAddress,
    setSpecialInstructions: handleSetSpecialInstructions,
    validateCart: handleValidateCart,
    getCheckoutSummary: handleGetCheckoutSummary,
    openCart: handleOpenCart,
    closeCart: handleCloseCart,
    toggleCart: handleToggleCart,
    clearError: handleClearError,
    getItemsByRestaurant,
  };
};

export default useCart;

