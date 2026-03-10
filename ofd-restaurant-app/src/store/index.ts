/**
 * Redux Store Configuration
 * Centralized state management for FoodAI Platform
 */

import { configureStore } from '@reduxjs/toolkit';
import authReducer from './slices/authSlice';
import restaurantReducer from './slices/restaurantSlice';
import menuReducer from './slices/menuSlice';
import cartReducer from './slices/cartSlice';
import orderReducer from './slices/orderSlice';
import promotionReducer from './slices/promotionSlice';
import uiReducer from './slices/uiSlice';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    restaurant: restaurantReducer,
    menu: menuReducer,
    cart: cartReducer,
    order: orderReducer,
    promotion: promotionReducer,
    ui: uiReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        // Ignore these paths in state for non-serializable values
        ignoredActions: ['persist/PERSIST', 'persist/REHYDRATE'],
      },
    }),
  devTools: import.meta.env.DEV,
});

// Infer types from the store
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export default store;

