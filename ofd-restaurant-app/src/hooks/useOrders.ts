/**
 * Orders Hook - Custom hook for order management
 */

import { useCallback } from 'react';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import {
  fetchMyOrders,
  fetchOrderById,
  initiateCheckout,
  getCheckoutStatus,
  cancelOrder,
  trackOrder,
  rateOrder,
  reorder,
  fetchRestaurantOrders,
  fetchPendingOrders,
  fetchActiveOrders,
  acceptOrder,
  rejectOrder,
  updateOrderStatus,
  markOrderReady,
  clearError,
  setActiveOrder,
  resetCheckout,
} from '@/store/slices/orderSlice';
import { resetCart } from '@/store/slices/cartSlice';
import type { OrderStatus, CheckoutRequest, OrderRating } from '@/types/api.types';

export const useOrders = () => {
  const dispatch = useAppDispatch();
  const {
    orders,
    currentOrder,
    activeOrder,
    trackingInfo,
    checkoutSagaId,
    checkoutOrderId,
    checkoutStatus,
    totalElements,
    totalPages,
    currentPage,
    isLoading,
    isProcessing,
    error,
  } = useAppSelector((state) => state.order);

  // Consumer actions
  const handleFetchMyOrders = useCallback(
    async (page = 0, status?: OrderStatus) => {
      const result = await dispatch(fetchMyOrders({ page, status }));
      return result;
    },
    [dispatch]
  );

  const handleFetchOrderById = useCallback(
    async (orderId: string) => {
      const result = await dispatch(fetchOrderById(orderId));
      return result;
    },
    [dispatch]
  );

  const handleInitiateCheckout = useCallback(
    async (data: CheckoutRequest) => {
      const result = await dispatch(initiateCheckout(data));
      if (result.meta.requestStatus === 'fulfilled') {
        // Clear cart after successful checkout
        dispatch(resetCart());
      }
      return result;
    },
    [dispatch]
  );

  const handleGetCheckoutStatus = useCallback(
    async (sagaId: string) => {
      const result = await dispatch(getCheckoutStatus(sagaId));
      return result;
    },
    [dispatch]
  );

  const handleCancelOrder = useCallback(
    async (orderId: string, reason: string) => {
      const result = await dispatch(cancelOrder({ orderId, reason }));
      return result;
    },
    [dispatch]
  );

  const handleTrackOrder = useCallback(
    async (orderId: string) => {
      const result = await dispatch(trackOrder(orderId));
      return result;
    },
    [dispatch]
  );

  const handleRateOrder = useCallback(
    async (orderId: string, rating: Omit<OrderRating, 'createdAt'>) => {
      const result = await dispatch(rateOrder({ orderId, rating }));
      return result;
    },
    [dispatch]
  );

  const handleReorder = useCallback(
    async (orderId: string) => {
      const result = await dispatch(reorder(orderId));
      return result;
    },
    [dispatch]
  );

  const handleSetActiveOrder = useCallback(
    (order: any) => {
      dispatch(setActiveOrder(order));
    },
    [dispatch]
  );

  const handleResetCheckout = useCallback(() => {
    dispatch(resetCheckout());
  }, [dispatch]);

  const handleClearError = useCallback(() => {
    dispatch(clearError());
  }, [dispatch]);

  // Computed values
  const hasMoreOrders = currentPage < totalPages - 1;
  const pendingOrdersCount = orders.filter(
    (o) => o.status === 'PENDING' || o.status === 'CONFIRMED' || o.status === 'PREPARING'
  ).length;

  return {
    orders,
    currentOrder,
    activeOrder,
    trackingInfo,
    checkoutSagaId,
    checkoutOrderId,
    checkoutStatus,
    totalElements,
    totalPages,
    currentPage,
    isLoading,
    isProcessing,
    error,
    hasMoreOrders,
    pendingOrdersCount,
    fetchMyOrders: handleFetchMyOrders,
    fetchOrderById: handleFetchOrderById,
    initiateCheckout: handleInitiateCheckout,
    getCheckoutStatus: handleGetCheckoutStatus,
    cancelOrder: handleCancelOrder,
    trackOrder: handleTrackOrder,
    rateOrder: handleRateOrder,
    reorder: handleReorder,
    setActiveOrder: handleSetActiveOrder,
    resetCheckout: handleResetCheckout,
    clearError: handleClearError,
  };
};

// Restaurant-specific hook
export const useRestaurantOrders = (restaurantId: string) => {
  const dispatch = useAppDispatch();
  const {
    restaurantOrders,
    pendingOrders,
    activeRestaurantOrders,
    isLoading,
    error,
  } = useAppSelector((state) => state.order);

  const handleFetchRestaurantOrders = useCallback(
    async (page = 0, status?: OrderStatus) => {
      const result = await dispatch(fetchRestaurantOrders({ restaurantId, page, status }));
      return result;
    },
    [dispatch, restaurantId]
  );

  const handleFetchPendingOrders = useCallback(async () => {
    const result = await dispatch(fetchPendingOrders(restaurantId));
    return result;
  }, [dispatch, restaurantId]);

  const handleFetchActiveOrders = useCallback(async () => {
    const result = await dispatch(fetchActiveOrders(restaurantId));
    return result;
  }, [dispatch, restaurantId]);

  const handleAcceptOrder = useCallback(
    async (orderId: string, estimatedPrepTime: number) => {
      const result = await dispatch(acceptOrder({ orderId, estimatedPrepTime }));
      return result;
    },
    [dispatch]
  );

  const handleRejectOrder = useCallback(
    async (orderId: string, reason: string) => {
      const result = await dispatch(rejectOrder({ orderId, reason }));
      return result;
    },
    [dispatch]
  );

  const handleUpdateOrderStatus = useCallback(
    async (orderId: string, status: OrderStatus, message?: string) => {
      const result = await dispatch(updateOrderStatus({ orderId, status, message }));
      return result;
    },
    [dispatch]
  );

  const handleMarkOrderReady = useCallback(
    async (orderId: string) => {
      const result = await dispatch(markOrderReady(orderId));
      return result;
    },
    [dispatch]
  );

  return {
    restaurantOrders,
    pendingOrders,
    activeOrders: activeRestaurantOrders,
    isLoading,
    error,
    fetchRestaurantOrders: handleFetchRestaurantOrders,
    fetchPendingOrders: handleFetchPendingOrders,
    fetchActiveOrders: handleFetchActiveOrders,
    acceptOrder: handleAcceptOrder,
    rejectOrder: handleRejectOrder,
    updateOrderStatus: handleUpdateOrderStatus,
    markOrderReady: handleMarkOrderReady,
  };
};

export default useOrders;

