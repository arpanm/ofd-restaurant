/**
 * Cart Slice - Cart state management
 */

import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { orderService } from '@/services';
import type {
  Cart,
  CartItem,
  AddToCartRequest,
  UpdateCartItemRequest,
  PaymentMethod,
} from '@/types/api.types';

interface CartState {
  cart: Cart | null;
  checkoutSummary: any | null;
  isLoading: boolean;
  isUpdating: boolean;
  error: string | null;
  isCartOpen: boolean;
}

const initialState: CartState = {
  cart: null,
  checkoutSummary: null,
  isLoading: false,
  isUpdating: false,
  error: null,
  isCartOpen: false,
};

// Async Thunks
export const fetchCart = createAsyncThunk(
  'cart/fetchCart',
  async (_, { rejectWithValue }) => {
    try {
      const response = await orderService.getCart();
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch cart');
    } catch (error: any) {
      // If no cart exists, return empty cart
      if (error.response?.status === 404) {
        return null;
      }
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch cart');
    }
  }
);

export const addToCart = createAsyncThunk(
  'cart/addToCart',
  async (data: AddToCartRequest, { rejectWithValue }) => {
    try {
      const response = await orderService.addToCart(data);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to add to cart');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to add to cart');
    }
  }
);

export const updateCartItem = createAsyncThunk(
  'cart/updateCartItem',
  async (
    { itemId, data }: { itemId: string; data: UpdateCartItemRequest },
    { rejectWithValue }
  ) => {
    try {
      const response = await orderService.updateCartItem(itemId, data);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to update cart item');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to update cart item');
    }
  }
);

export const removeFromCart = createAsyncThunk(
  'cart/removeFromCart',
  async (itemId: string, { rejectWithValue }) => {
    try {
      const response = await orderService.removeFromCart(itemId);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to remove from cart');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to remove from cart');
    }
  }
);

export const clearCart = createAsyncThunk(
  'cart/clearCart',
  async (_, { rejectWithValue }) => {
    try {
      await orderService.clearCart();
      return null;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to clear cart');
    }
  }
);

export const applyCoupon = createAsyncThunk(
  'cart/applyCoupon',
  async (couponCode: string, { rejectWithValue }) => {
    try {
      const response = await orderService.applyCoupon(couponCode);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to apply coupon');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to apply coupon');
    }
  }
);

export const removeCoupon = createAsyncThunk(
  'cart/removeCoupon',
  async (_, { rejectWithValue }) => {
    try {
      const response = await orderService.removeCoupon();
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to remove coupon');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to remove coupon');
    }
  }
);

export const setDeliveryAddress = createAsyncThunk(
  'cart/setDeliveryAddress',
  async (addressId: string, { rejectWithValue }) => {
    try {
      const response = await orderService.setDeliveryAddress(addressId);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to set address');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to set address');
    }
  }
);

export const setSpecialInstructions = createAsyncThunk(
  'cart/setSpecialInstructions',
  async (instructions: string, { rejectWithValue }) => {
    try {
      const response = await orderService.setSpecialInstructions(instructions);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to set instructions');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to set instructions');
    }
  }
);

export const validateCart = createAsyncThunk(
  'cart/validateCart',
  async (_, { rejectWithValue }) => {
    try {
      const response = await orderService.validateCart();
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Cart validation failed');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Cart validation failed');
    }
  }
);

export const getCheckoutSummary = createAsyncThunk(
  'cart/getCheckoutSummary',
  async (
    { paymentMethod, tipAmount }: { paymentMethod: PaymentMethod; tipAmount?: number },
    { rejectWithValue }
  ) => {
    try {
      const response = await orderService.getCheckoutSummary(paymentMethod, tipAmount);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to get checkout summary');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to get checkout summary');
    }
  }
);

const cartSlice = createSlice({
  name: 'cart',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    openCart: (state) => {
      state.isCartOpen = true;
    },
    closeCart: (state) => {
      state.isCartOpen = false;
    },
    toggleCart: (state) => {
      state.isCartOpen = !state.isCartOpen;
    },
    resetCart: (state) => {
      state.cart = null;
      state.checkoutSummary = null;
    },
    // Optimistic update for quantity change
    updateItemQuantityOptimistic: (
      state,
      action: PayloadAction<{ itemId: string; quantity: number }>
    ) => {
      if (state.cart) {
        const item = state.cart.items.find((i) => i.id === action.payload.itemId);
        if (item) {
          item.quantity = action.payload.quantity;
          item.totalPrice = item.unitPrice * action.payload.quantity;
          // Recalculate totals
          state.cart.subtotal = state.cart.items.reduce((sum, i) => sum + i.totalPrice, 0);
          state.cart.total = state.cart.subtotal + state.cart.deliveryFee + state.cart.taxes - state.cart.discount;
        }
      }
    },
  },
  extraReducers: (builder) => {
    // Fetch Cart
    builder
      .addCase(fetchCart.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchCart.fulfilled, (state, action) => {
        state.isLoading = false;
        state.cart = action.payload;
      })
      .addCase(fetchCart.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // Add to Cart
    builder
      .addCase(addToCart.pending, (state) => {
        state.isUpdating = true;
        state.error = null;
      })
      .addCase(addToCart.fulfilled, (state, action) => {
        state.isUpdating = false;
        state.cart = action.payload;
        state.isCartOpen = true; // Open cart after adding item
      })
      .addCase(addToCart.rejected, (state, action) => {
        state.isUpdating = false;
        state.error = action.payload as string;
      });

    // Update Cart Item
    builder
      .addCase(updateCartItem.pending, (state) => {
        state.isUpdating = true;
      })
      .addCase(updateCartItem.fulfilled, (state, action) => {
        state.isUpdating = false;
        state.cart = action.payload;
      })
      .addCase(updateCartItem.rejected, (state, action) => {
        state.isUpdating = false;
        state.error = action.payload as string;
      });

    // Remove from Cart
    builder
      .addCase(removeFromCart.pending, (state) => {
        state.isUpdating = true;
      })
      .addCase(removeFromCart.fulfilled, (state, action) => {
        state.isUpdating = false;
        state.cart = action.payload;
      })
      .addCase(removeFromCart.rejected, (state, action) => {
        state.isUpdating = false;
        state.error = action.payload as string;
      });

    // Clear Cart
    builder
      .addCase(clearCart.fulfilled, (state) => {
        state.cart = null;
        state.checkoutSummary = null;
      });

    // Apply Coupon
    builder
      .addCase(applyCoupon.pending, (state) => {
        state.isUpdating = true;
        state.error = null;
      })
      .addCase(applyCoupon.fulfilled, (state, action) => {
        state.isUpdating = false;
        state.cart = action.payload;
      })
      .addCase(applyCoupon.rejected, (state, action) => {
        state.isUpdating = false;
        state.error = action.payload as string;
      });

    // Remove Coupon
    builder
      .addCase(removeCoupon.fulfilled, (state, action) => {
        state.cart = action.payload;
      });

    // Set Delivery Address
    builder
      .addCase(setDeliveryAddress.fulfilled, (state, action) => {
        state.cart = action.payload;
      });

    // Set Special Instructions
    builder
      .addCase(setSpecialInstructions.fulfilled, (state, action) => {
        state.cart = action.payload;
      });

    // Validate Cart
    builder
      .addCase(validateCart.rejected, (state, action) => {
        state.error = action.payload as string;
      });

    // Checkout Summary
    builder
      .addCase(getCheckoutSummary.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(getCheckoutSummary.fulfilled, (state, action) => {
        state.isLoading = false;
        state.checkoutSummary = action.payload;
      })
      .addCase(getCheckoutSummary.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });
  },
});

export const {
  clearError,
  openCart,
  closeCart,
  toggleCart,
  resetCart,
  updateItemQuantityOptimistic,
} = cartSlice.actions;

export default cartSlice.reducer;

