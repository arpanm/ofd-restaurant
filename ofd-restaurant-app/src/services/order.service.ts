/**
 * Order Service - API calls for cart and order management
 */

import { orderApi, orchestrationApi } from './api.config';
import type {
  ApiResponse,
  PaginatedResponse,
  Cart,
  CartItem,
  AddToCartRequest,
  UpdateCartItemRequest,
  Order,
  OrderStatus,
  CreateOrderRequest,
  CheckoutRequest,
  PaymentMethod,
  OrderRating,
} from '@/types/api.types';

export const orderService = {
  // ============================================
  // Cart Management
  // ============================================

  /**
   * Get current user's cart
   */
  getCart: async (): Promise<ApiResponse<Cart>> => {
    const response = await orderApi.get('/cart');
    return response.data;
  },

  /**
   * Add item to cart
   */
  addToCart: async (data: AddToCartRequest): Promise<ApiResponse<Cart>> => {
    const response = await orderApi.post('/cart/items', data);
    return response.data;
  },

  /**
   * Update cart item
   */
  updateCartItem: async (itemId: string, data: UpdateCartItemRequest): Promise<ApiResponse<Cart>> => {
    const response = await orderApi.put(`/cart/items/${itemId}`, data);
    return response.data;
  },

  /**
   * Remove item from cart
   */
  removeFromCart: async (itemId: string): Promise<ApiResponse<Cart>> => {
    const response = await orderApi.delete(`/cart/items/${itemId}`);
    return response.data;
  },

  /**
   * Clear entire cart
   */
  clearCart: async (): Promise<ApiResponse<void>> => {
    const response = await orderApi.delete('/cart');
    return response.data;
  },

  /**
   * Apply coupon to cart
   */
  applyCoupon: async (couponCode: string): Promise<ApiResponse<Cart>> => {
    const response = await orderApi.post('/cart/coupon', { code: couponCode });
    return response.data;
  },

  /**
   * Remove coupon from cart
   */
  removeCoupon: async (): Promise<ApiResponse<Cart>> => {
    const response = await orderApi.delete('/cart/coupon');
    return response.data;
  },

  /**
   * Set delivery address for cart
   */
  setDeliveryAddress: async (addressId: string): Promise<ApiResponse<Cart>> => {
    const response = await orderApi.put('/cart/address', { addressId });
    return response.data;
  },

  /**
   * Add special instructions to cart
   */
  setSpecialInstructions: async (instructions: string): Promise<ApiResponse<Cart>> => {
    const response = await orderApi.put('/cart/instructions', { instructions });
    return response.data;
  },

  // ============================================
  // Checkout & Order Creation
  // ============================================

  /**
   * Validate cart before checkout
   */
  validateCart: async (): Promise<ApiResponse<{ valid: boolean; errors?: string[] }>> => {
    const response = await orderApi.post('/cart/validate');
    return response.data;
  },

  /**
   * Get checkout summary
   */
  getCheckoutSummary: async (
    paymentMethod: PaymentMethod,
    tipAmount?: number
  ): Promise<ApiResponse<any>> => {
    const response = await orderApi.post('/checkout/summary', { paymentMethod, tipAmount });
    return response.data;
  },

  /**
   * Initiate checkout (via Orchestration Service Saga)
   */
  initiateCheckout: async (data: CheckoutRequest): Promise<ApiResponse<{ sagaId: string; orderId: string }>> => {
    const response = await orchestrationApi.post('/checkout', data);
    return response.data;
  },

  /**
   * Create order directly
   */
  createOrder: async (data: CreateOrderRequest): Promise<ApiResponse<Order>> => {
    const response = await orderApi.post('/orders', data);
    return response.data;
  },

  /**
   * Get checkout saga status
   */
  getCheckoutStatus: async (sagaId: string): Promise<ApiResponse<any>> => {
    const response = await orchestrationApi.get(`/sagas/${sagaId}`);
    return response.data;
  },

  // ============================================
  // Order Management (Consumer)
  // ============================================

  /**
   * Get user's orders
   */
  getMyOrders: async (
    page = 0,
    size = 20,
    status?: OrderStatus
  ): Promise<ApiResponse<PaginatedResponse<Order>>> => {
    const response = await orderApi.get('/orders/my', { params: { page, size, status } });
    return response.data;
  },

  /**
   * Get order by ID
   */
  getOrderById: async (orderId: string): Promise<ApiResponse<Order>> => {
    const response = await orderApi.get(`/orders/${orderId}`);
    return response.data;
  },

  /**
   * Get order by order number
   */
  getOrderByNumber: async (orderNumber: string): Promise<ApiResponse<Order>> => {
    const response = await orderApi.get(`/orders/number/${orderNumber}`);
    return response.data;
  },

  /**
   * Cancel order
   */
  cancelOrder: async (orderId: string, reason: string): Promise<ApiResponse<Order>> => {
    const response = await orderApi.post(`/orders/${orderId}/cancel`, { reason });
    return response.data;
  },

  /**
   * Request refund
   */
  requestRefund: async (orderId: string, reason: string, amount?: number): Promise<ApiResponse<any>> => {
    const response = await orderApi.post(`/orders/${orderId}/refund`, { reason, amount });
    return response.data;
  },

  /**
   * Reorder (add previous order items to cart)
   */
  reorder: async (orderId: string): Promise<ApiResponse<Cart>> => {
    const response = await orderApi.post(`/orders/${orderId}/reorder`);
    return response.data;
  },

  // ============================================
  // Order Tracking
  // ============================================

  /**
   * Get real-time order tracking info
   */
  trackOrder: async (orderId: string): Promise<ApiResponse<any>> => {
    const response = await orderApi.get(`/orders/${orderId}/track`);
    return response.data;
  },

  /**
   * Get delivery partner location
   */
  getDeliveryPartnerLocation: async (orderId: string): Promise<ApiResponse<any>> => {
    const response = await orderApi.get(`/orders/${orderId}/delivery-location`);
    return response.data;
  },

  /**
   * Subscribe to order updates (returns WebSocket URL)
   */
  getOrderTrackingWebSocket: (orderId: string): string => {
    const wsUrl = import.meta.env.VITE_ORDER_WS_URL || 'ws://localhost:8084/ws';
    return `${wsUrl}/orders/${orderId}/track`;
  },

  // ============================================
  // Order Ratings & Reviews
  // ============================================

  /**
   * Rate order
   */
  rateOrder: async (orderId: string, rating: Omit<OrderRating, 'createdAt'>): Promise<ApiResponse<Order>> => {
    const response = await orderApi.post(`/orders/${orderId}/rate`, rating);
    return response.data;
  },

  /**
   * Update order rating
   */
  updateRating: async (orderId: string, rating: Partial<OrderRating>): Promise<ApiResponse<Order>> => {
    const response = await orderApi.put(`/orders/${orderId}/rate`, rating);
    return response.data;
  },

  // ============================================
  // Order Management (Restaurant)
  // ============================================

  /**
   * Get restaurant orders
   */
  getRestaurantOrders: async (
    restaurantId: string,
    page = 0,
    size = 20,
    status?: OrderStatus
  ): Promise<ApiResponse<PaginatedResponse<Order>>> => {
    const response = await orderApi.get(`/restaurants/${restaurantId}/orders`, {
      params: { page, size, status },
    });
    return response.data;
  },

  /**
   * Get pending orders for restaurant
   */
  getPendingOrders: async (restaurantId: string): Promise<ApiResponse<Order[]>> => {
    const response = await orderApi.get(`/restaurants/${restaurantId}/orders/pending`);
    return response.data;
  },

  /**
   * Get active orders for restaurant
   */
  getActiveOrders: async (restaurantId: string): Promise<ApiResponse<Order[]>> => {
    const response = await orderApi.get(`/restaurants/${restaurantId}/orders/active`);
    return response.data;
  },

  /**
   * Accept order
   */
  acceptOrder: async (
    orderId: string,
    estimatedPrepTime: number
  ): Promise<ApiResponse<Order>> => {
    const response = await orderApi.post(`/orders/${orderId}/accept`, { estimatedPrepTime });
    return response.data;
  },

  /**
   * Reject order
   */
  rejectOrder: async (orderId: string, reason: string): Promise<ApiResponse<Order>> => {
    const response = await orderApi.post(`/orders/${orderId}/reject`, { reason });
    return response.data;
  },

  /**
   * Update order status
   */
  updateOrderStatus: async (orderId: string, status: OrderStatus, message?: string): Promise<ApiResponse<Order>> => {
    const response = await orderApi.put(`/orders/${orderId}/status`, { status, message });
    return response.data;
  },

  /**
   * Mark order as ready for pickup
   */
  markReadyForPickup: async (orderId: string): Promise<ApiResponse<Order>> => {
    const response = await orderApi.post(`/orders/${orderId}/ready`);
    return response.data;
  },

  /**
   * Update item status within order
   */
  updateItemStatus: async (
    orderId: string,
    itemId: string,
    status: string
  ): Promise<ApiResponse<Order>> => {
    const response = await orderApi.put(`/orders/${orderId}/items/${itemId}/status`, { status });
    return response.data;
  },

  // ============================================
  // Payment Management
  // ============================================

  /**
   * Get payment details
   */
  getPaymentDetails: async (paymentId: string): Promise<ApiResponse<any>> => {
    const response = await orderApi.get(`/payments/${paymentId}`);
    return response.data;
  },

  /**
   * Get user payment history
   */
  getPaymentHistory: async (page = 0, size = 20): Promise<ApiResponse<PaginatedResponse<any>>> => {
    const response = await orderApi.get('/payments/my', { params: { page, size } });
    return response.data;
  },

  /**
   * Initiate payment
   */
  initiatePayment: async (
    orderId: string,
    method: PaymentMethod
  ): Promise<ApiResponse<{ paymentId: string; gatewayOrderId?: string }>> => {
    const response = await orderApi.post('/payments/initiate', { orderId, method });
    return response.data;
  },

  /**
   * Verify payment
   */
  verifyPayment: async (
    paymentId: string,
    gatewayResponse: any
  ): Promise<ApiResponse<{ success: boolean; orderId: string }>> => {
    const response = await orderApi.post('/payments/verify', { paymentId, gatewayResponse });
    return response.data;
  },

  /**
   * Get refund status
   */
  getRefundStatus: async (refundId: string): Promise<ApiResponse<any>> => {
    const response = await orderApi.get(`/refunds/${refundId}`);
    return response.data;
  },
};

export default orderService;

