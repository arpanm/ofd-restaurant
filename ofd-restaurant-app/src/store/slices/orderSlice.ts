/**
 * Order Slice - Order state management
 */

import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { orderService } from '@/services';
import type {
  Order,
  OrderStatus,
  CheckoutRequest,
  PaymentMethod,
  OrderRating,
} from '@/types/api.types';

interface OrderState {
  // Consumer view
  orders: Order[];
  currentOrder: Order | null;
  activeOrder: Order | null; // Currently tracking order
  trackingInfo: any | null;
  
  // Restaurant view
  restaurantOrders: Order[];
  pendingOrders: Order[];
  activeRestaurantOrders: Order[];
  
  // Checkout
  checkoutSagaId: string | null;
  checkoutOrderId: string | null;
  checkoutStatus: 'idle' | 'processing' | 'completed' | 'failed';
  
  // Pagination
  totalElements: number;
  totalPages: number;
  currentPage: number;
  
  // Loading states
  isLoading: boolean;
  isProcessing: boolean;
  error: string | null;
}

const initialState: OrderState = {
  orders: [],
  currentOrder: null,
  activeOrder: null,
  trackingInfo: null,
  restaurantOrders: [],
  pendingOrders: [],
  activeRestaurantOrders: [],
  checkoutSagaId: null,
  checkoutOrderId: null,
  checkoutStatus: 'idle',
  totalElements: 0,
  totalPages: 0,
  currentPage: 0,
  isLoading: false,
  isProcessing: false,
  error: null,
};

// Async Thunks - Consumer
export const fetchMyOrders = createAsyncThunk(
  'order/fetchMyOrders',
  async (
    { page = 0, status }: { page?: number; status?: OrderStatus } = {},
    { rejectWithValue }
  ) => {
    try {
      const response = await orderService.getMyOrders(page, 20, status);
      if (response.success) {
        return { data: response.data, isLoadMore: page > 0 };
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch orders');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch orders');
    }
  }
);

export const fetchOrderById = createAsyncThunk(
  'order/fetchOrderById',
  async (orderId: string, { rejectWithValue }) => {
    try {
      const response = await orderService.getOrderById(orderId);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch order');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch order');
    }
  }
);

export const initiateCheckout = createAsyncThunk(
  'order/initiateCheckout',
  async (data: CheckoutRequest, { rejectWithValue }) => {
    try {
      const response = await orderService.initiateCheckout(data);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Checkout failed');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Checkout failed');
    }
  }
);

export const getCheckoutStatus = createAsyncThunk(
  'order/getCheckoutStatus',
  async (sagaId: string, { rejectWithValue }) => {
    try {
      const response = await orderService.getCheckoutStatus(sagaId);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to get checkout status');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to get checkout status');
    }
  }
);

export const cancelOrder = createAsyncThunk(
  'order/cancelOrder',
  async ({ orderId, reason }: { orderId: string; reason: string }, { rejectWithValue }) => {
    try {
      const response = await orderService.cancelOrder(orderId, reason);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to cancel order');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to cancel order');
    }
  }
);

export const trackOrder = createAsyncThunk(
  'order/trackOrder',
  async (orderId: string, { rejectWithValue }) => {
    try {
      const response = await orderService.trackOrder(orderId);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to track order');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to track order');
    }
  }
);

export const rateOrder = createAsyncThunk(
  'order/rateOrder',
  async (
    { orderId, rating }: { orderId: string; rating: Omit<OrderRating, 'createdAt'> },
    { rejectWithValue }
  ) => {
    try {
      const response = await orderService.rateOrder(orderId, rating);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to rate order');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to rate order');
    }
  }
);

export const reorder = createAsyncThunk(
  'order/reorder',
  async (orderId: string, { rejectWithValue }) => {
    try {
      const response = await orderService.reorder(orderId);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to reorder');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to reorder');
    }
  }
);

// Async Thunks - Restaurant
export const fetchRestaurantOrders = createAsyncThunk(
  'order/fetchRestaurantOrders',
  async (
    { restaurantId, page = 0, status }: { restaurantId: string; page?: number; status?: OrderStatus },
    { rejectWithValue }
  ) => {
    try {
      const response = await orderService.getRestaurantOrders(restaurantId, page, 20, status);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch orders');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch orders');
    }
  }
);

export const fetchPendingOrders = createAsyncThunk(
  'order/fetchPendingOrders',
  async (restaurantId: string, { rejectWithValue }) => {
    try {
      const response = await orderService.getPendingOrders(restaurantId);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch pending orders');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch pending orders');
    }
  }
);

export const fetchActiveOrders = createAsyncThunk(
  'order/fetchActiveOrders',
  async (restaurantId: string, { rejectWithValue }) => {
    try {
      const response = await orderService.getActiveOrders(restaurantId);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch active orders');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch active orders');
    }
  }
);

export const acceptOrder = createAsyncThunk(
  'order/acceptOrder',
  async (
    { orderId, estimatedPrepTime }: { orderId: string; estimatedPrepTime: number },
    { rejectWithValue }
  ) => {
    try {
      const response = await orderService.acceptOrder(orderId, estimatedPrepTime);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to accept order');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to accept order');
    }
  }
);

export const rejectOrder = createAsyncThunk(
  'order/rejectOrder',
  async ({ orderId, reason }: { orderId: string; reason: string }, { rejectWithValue }) => {
    try {
      const response = await orderService.rejectOrder(orderId, reason);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to reject order');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to reject order');
    }
  }
);

export const updateOrderStatus = createAsyncThunk(
  'order/updateOrderStatus',
  async (
    { orderId, status, message }: { orderId: string; status: OrderStatus; message?: string },
    { rejectWithValue }
  ) => {
    try {
      const response = await orderService.updateOrderStatus(orderId, status, message);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to update order status');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to update order status');
    }
  }
);

export const markOrderReady = createAsyncThunk(
  'order/markOrderReady',
  async (orderId: string, { rejectWithValue }) => {
    try {
      const response = await orderService.markReadyForPickup(orderId);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to mark order as ready');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to mark order as ready');
    }
  }
);

const orderSlice = createSlice({
  name: 'order',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    setActiveOrder: (state, action: PayloadAction<Order | null>) => {
      state.activeOrder = action.payload;
    },
    clearCurrentOrder: (state) => {
      state.currentOrder = null;
    },
    resetCheckout: (state) => {
      state.checkoutSagaId = null;
      state.checkoutOrderId = null;
      state.checkoutStatus = 'idle';
    },
    updateOrderInList: (state, action: PayloadAction<Order>) => {
      const updateOrder = (orders: Order[]) => {
        const index = orders.findIndex((o) => o.id === action.payload.id);
        if (index !== -1) {
          orders[index] = action.payload;
        }
        return orders;
      };
      
      state.orders = updateOrder(state.orders);
      state.restaurantOrders = updateOrder(state.restaurantOrders);
      state.pendingOrders = updateOrder(state.pendingOrders);
      state.activeRestaurantOrders = updateOrder(state.activeRestaurantOrders);
      
      if (state.currentOrder?.id === action.payload.id) {
        state.currentOrder = action.payload;
      }
      if (state.activeOrder?.id === action.payload.id) {
        state.activeOrder = action.payload;
      }
    },
    // Real-time order update from WebSocket
    handleOrderUpdate: (state, action: PayloadAction<{ orderId: string; status: OrderStatus; trackingInfo?: any }>) => {
      const updateOrderStatus = (orders: Order[]) => {
        const order = orders.find((o) => o.id === action.payload.orderId);
        if (order) {
          order.status = action.payload.status;
        }
        return orders;
      };
      
      state.orders = updateOrderStatus(state.orders);
      state.restaurantOrders = updateOrderStatus(state.restaurantOrders);
      
      if (state.currentOrder?.id === action.payload.orderId) {
        state.currentOrder.status = action.payload.status;
      }
      if (state.activeOrder?.id === action.payload.orderId) {
        state.activeOrder.status = action.payload.status;
        if (action.payload.trackingInfo) {
          state.trackingInfo = action.payload.trackingInfo;
        }
      }
    },
  },
  extraReducers: (builder) => {
    // Fetch My Orders
    builder
      .addCase(fetchMyOrders.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchMyOrders.fulfilled, (state, action) => {
        state.isLoading = false;
        if (action.payload.isLoadMore) {
          state.orders = [...state.orders, ...action.payload.data.content];
        } else {
          state.orders = action.payload.data.content;
        }
        state.totalElements = action.payload.data.totalElements;
        state.totalPages = action.payload.data.totalPages;
        state.currentPage = action.payload.data.number;
      })
      .addCase(fetchMyOrders.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // Fetch Order by ID
    builder
      .addCase(fetchOrderById.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(fetchOrderById.fulfilled, (state, action) => {
        state.isLoading = false;
        state.currentOrder = action.payload;
      })
      .addCase(fetchOrderById.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // Initiate Checkout
    builder
      .addCase(initiateCheckout.pending, (state) => {
        state.isProcessing = true;
        state.checkoutStatus = 'processing';
        state.error = null;
      })
      .addCase(initiateCheckout.fulfilled, (state, action) => {
        state.isProcessing = false;
        state.checkoutSagaId = action.payload.sagaId;
        state.checkoutOrderId = action.payload.orderId;
        state.checkoutStatus = 'completed';
      })
      .addCase(initiateCheckout.rejected, (state, action) => {
        state.isProcessing = false;
        state.checkoutStatus = 'failed';
        state.error = action.payload as string;
      });

    // Cancel Order
    builder
      .addCase(cancelOrder.fulfilled, (state, action) => {
        const updateOrder = (orders: Order[]) => {
          const index = orders.findIndex((o) => o.id === action.payload.id);
          if (index !== -1) {
            orders[index] = action.payload;
          }
          return orders;
        };
        state.orders = updateOrder(state.orders);
        if (state.currentOrder?.id === action.payload.id) {
          state.currentOrder = action.payload;
        }
      });

    // Track Order
    builder
      .addCase(trackOrder.fulfilled, (state, action) => {
        state.trackingInfo = action.payload;
      });

    // Rate Order
    builder
      .addCase(rateOrder.fulfilled, (state, action) => {
        if (state.currentOrder?.id === action.payload.id) {
          state.currentOrder = action.payload;
        }
      });

    // Restaurant Orders
    builder
      .addCase(fetchRestaurantOrders.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(fetchRestaurantOrders.fulfilled, (state, action) => {
        state.isLoading = false;
        state.restaurantOrders = action.payload.content;
        state.totalElements = action.payload.totalElements;
        state.totalPages = action.payload.totalPages;
      })
      .addCase(fetchRestaurantOrders.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // Pending Orders
    builder
      .addCase(fetchPendingOrders.fulfilled, (state, action) => {
        state.pendingOrders = action.payload;
      });

    // Active Orders
    builder
      .addCase(fetchActiveOrders.fulfilled, (state, action) => {
        state.activeRestaurantOrders = action.payload;
      });

    // Accept Order
    builder
      .addCase(acceptOrder.fulfilled, (state, action) => {
        // Move from pending to active
        state.pendingOrders = state.pendingOrders.filter((o) => o.id !== action.payload.id);
        state.activeRestaurantOrders.push(action.payload);
      });

    // Reject Order
    builder
      .addCase(rejectOrder.fulfilled, (state, action) => {
        state.pendingOrders = state.pendingOrders.filter((o) => o.id !== action.payload.id);
      });

    // Update Order Status
    builder
      .addCase(updateOrderStatus.fulfilled, (state, action) => {
        const updateOrder = (orders: Order[]) => {
          const index = orders.findIndex((o) => o.id === action.payload.id);
          if (index !== -1) {
            orders[index] = action.payload;
          }
          return orders;
        };
        state.restaurantOrders = updateOrder(state.restaurantOrders);
        state.activeRestaurantOrders = updateOrder(state.activeRestaurantOrders);
      });

    // Mark Order Ready
    builder
      .addCase(markOrderReady.fulfilled, (state, action) => {
        const updateOrder = (orders: Order[]) => {
          const index = orders.findIndex((o) => o.id === action.payload.id);
          if (index !== -1) {
            orders[index] = action.payload;
          }
          return orders;
        };
        state.activeRestaurantOrders = updateOrder(state.activeRestaurantOrders);
      });
  },
});

export const {
  clearError,
  setActiveOrder,
  clearCurrentOrder,
  resetCheckout,
  updateOrderInList,
  handleOrderUpdate,
} = orderSlice.actions;

export default orderSlice.reducer;

