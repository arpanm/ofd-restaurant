/**
 * Auth Hook - Custom hook for authentication
 */

import { useEffect, useCallback } from 'react';
import { useAppDispatch, useAppSelector } from '@/store/hooks';
import {
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
  clearError,
} from '@/store/slices/authSlice';
import { tokenManager } from '@/services';
import type { LoginRequest, RegisterUserRequest, UpdateUserRequest, Address } from '@/types/api.types';

export const useAuth = () => {
  const dispatch = useAppDispatch();
  const {
    user,
    isAuthenticated,
    isLoading,
    error,
    addresses,
    preferences,
    loyaltyProfile,
    favoriteRestaurants,
    favoriteItems,
  } = useAppSelector((state) => state.auth);

  // Check if user is authenticated on mount
  useEffect(() => {
    const token = tokenManager.getAccessToken();
    if (token && !user) {
      dispatch(getCurrentUser());
    }
  }, [dispatch, user]);

  // Load user data when authenticated
  useEffect(() => {
    if (isAuthenticated && user) {
      dispatch(fetchAddresses());
      dispatch(fetchLoyaltyProfile());
      dispatch(fetchFavorites());
    }
  }, [dispatch, isAuthenticated, user]);

  const handleLogin = useCallback(
    async (credentials: LoginRequest) => {
      const result = await dispatch(login(credentials));
      return result;
    },
    [dispatch]
  );

  const handleRegister = useCallback(
    async (data: RegisterUserRequest) => {
      const result = await dispatch(register(data));
      return result;
    },
    [dispatch]
  );

  const handleLogout = useCallback(async () => {
    await dispatch(logout());
  }, [dispatch]);

  const handleUpdateProfile = useCallback(
    async (data: UpdateUserRequest) => {
      const result = await dispatch(updateProfile(data));
      return result;
    },
    [dispatch]
  );

  const handleAddAddress = useCallback(
    async (address: Omit<Address, 'id'>) => {
      const result = await dispatch(addAddress(address));
      return result;
    },
    [dispatch]
  );

  const handleUpdateAddress = useCallback(
    async (addressId: string, data: Partial<Address>) => {
      const result = await dispatch(updateAddress({ addressId, data }));
      return result;
    },
    [dispatch]
  );

  const handleDeleteAddress = useCallback(
    async (addressId: string) => {
      const result = await dispatch(deleteAddress(addressId));
      return result;
    },
    [dispatch]
  );

  const handleSetDefaultAddress = useCallback(
    async (addressId: string) => {
      const result = await dispatch(setDefaultAddress(addressId));
      return result;
    },
    [dispatch]
  );

  const handleToggleFavoriteRestaurant = useCallback(
    async (restaurantId: string) => {
      const result = await dispatch(toggleFavoriteRestaurant(restaurantId));
      return result;
    },
    [dispatch]
  );

  const handleToggleFavoriteItem = useCallback(
    async (itemId: string) => {
      const result = await dispatch(toggleFavoriteItem(itemId));
      return result;
    },
    [dispatch]
  );

  const handleClearError = useCallback(() => {
    dispatch(clearError());
  }, [dispatch]);

  const isRestaurantFavorite = useCallback(
    (restaurantId: string) => favoriteRestaurants.includes(restaurantId),
    [favoriteRestaurants]
  );

  const isItemFavorite = useCallback(
    (itemId: string) => favoriteItems.includes(itemId),
    [favoriteItems]
  );

  const getDefaultAddress = useCallback(() => {
    return addresses.find((a) => a.isDefault) || addresses[0] || null;
  }, [addresses]);

  return {
    user,
    isAuthenticated,
    isLoading,
    error,
    addresses,
    preferences,
    loyaltyProfile,
    favoriteRestaurants,
    favoriteItems,
    login: handleLogin,
    register: handleRegister,
    logout: handleLogout,
    updateProfile: handleUpdateProfile,
    addAddress: handleAddAddress,
    updateAddress: handleUpdateAddress,
    deleteAddress: handleDeleteAddress,
    setDefaultAddress: handleSetDefaultAddress,
    toggleFavoriteRestaurant: handleToggleFavoriteRestaurant,
    toggleFavoriteItem: handleToggleFavoriteItem,
    clearError: handleClearError,
    isRestaurantFavorite,
    isItemFavorite,
    getDefaultAddress,
  };
};

export default useAuth;

