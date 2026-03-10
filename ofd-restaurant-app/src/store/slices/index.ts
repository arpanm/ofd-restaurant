/**
 * Redux Slices Index - Export all slices and actions
 */

// Auth Slice
export { default as authReducer } from './authSlice';
export {
  login,
  register,
  logout,
  getCurrentUser,
  updateProfile,
  fetchAddresses,
  addAddress,
  updateAddress,
  deleteAddress,
  setDefaultAddress,
  fetchLoyaltyProfile,
  fetchFavorites,
  toggleFavoriteRestaurant,
  toggleFavoriteItem,
  clearError as clearAuthError,
  setUser,
  clearAuth,
} from './authSlice';

// Restaurant Slice
export { default as restaurantReducer } from './restaurantSlice';
export {
  fetchRestaurants,
  searchRestaurants,
  fetchNearbyRestaurants,
  fetchFeaturedRestaurants,
  fetchPopularRestaurants,
  fetchRestaurantById,
  fetchCuisines,
  fetchMyRestaurants,
  registerRestaurant,
  updateRestaurant,
  fetchDashboardStats,
  clearError as clearRestaurantError,
  setActiveFilters,
  setSearchQuery as setRestaurantSearchQuery,
  clearCurrentRestaurant,
  setSelectedRestaurant,
  clearRestaurants,
} from './restaurantSlice';

// Menu Slice
export { default as menuReducer } from './menuSlice';
export {
  fetchCategories,
  fetchMenuItems,
  fetchMenuItemsByCategory,
  fetchMenuItemById,
  fetchTrendingItems,
  fetchRecommendedItems,
  searchMenuItems,
  createCategory,
  updateCategory,
  deleteCategory,
  createMenuItem,
  updateMenuItem,
  deleteMenuItem,
  updateItemAvailability,
  updateItemPrice,
  clearError as clearMenuError,
  setSearchQuery as setMenuSearchQuery,
  clearSearchResults,
  setCurrentItem,
  setEditingItem,
  clearMenu,
} from './menuSlice';

// Cart Slice
export { default as cartReducer } from './cartSlice';
export {
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
  clearError as clearCartError,
  openCart,
  closeCart,
  toggleCart,
  resetCart,
  updateItemQuantityOptimistic,
} from './cartSlice';

// Order Slice
export { default as orderReducer } from './orderSlice';
export {
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
  clearError as clearOrderError,
  setActiveOrder,
  clearCurrentOrder,
  resetCheckout,
  updateOrderInList,
  handleOrderUpdate,
} from './orderSlice';

// Promotion Slice
export { default as promotionReducer } from './promotionSlice';
export {
  fetchActivePromotions,
  fetchFeaturedPromotions,
  fetchAvailableCoupons,
  validateCoupon,
  fetchMyPromotions,
  createPromotion,
  updatePromotion,
  activatePromotion,
  pausePromotion,
  deletePromotion,
  fetchMyCoupons,
  createCoupon,
  updateCoupon,
  deleteCoupon,
  fetchCampaigns,
  createCampaign,
  fetchSegments,
  createSegment,
  clearError as clearPromotionError,
  clearValidatedCoupon,
} from './promotionSlice';

// UI Slice
export { default as uiReducer } from './uiSlice';
export {
  setGlobalLoading,
  addToast,
  removeToast,
  clearToasts,
  openModal,
  closeModal,
  closeTopModal,
  closeAllModals,
  toggleSidebar,
  setSidebarOpen,
  toggleMobileMenu,
  setMobileMenuOpen,
  setTheme,
  setUserLocation,
  clearUserLocation,
  setLocationPermission,
  toggleSearch,
  setSearchOpen,
  addToSearchHistory,
  clearSearchHistory,
  toggleFilters,
  setFiltersOpen,
  initializeUI,
} from './uiSlice';

