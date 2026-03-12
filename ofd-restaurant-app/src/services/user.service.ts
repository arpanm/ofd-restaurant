/**
 * User Service - API calls for user management
 */

import { userApi } from './api.config';
import type {
  ApiResponse,
  User,
  RegisterUserRequest,
  AuthRegisterRequest,
  LoginRequest,
  AuthResponse,
  UpdateUserRequest,
  Address,
  UserPreferences,
  LoyaltyProfile,
} from '@/types/api.types';

export const userService = {
  // ============================================
  // Authentication
  // ============================================
  
  /**
   * Register a new user (legacy, with password)
   */
  register: async (data: RegisterUserRequest): Promise<ApiResponse<AuthResponse>> => {
    const response = await userApi.post('/auth/register', data);
    return response.data;
  },

  /**
   * Register and get tokens (for onboarding flow; no password)
   */
  registerAuth: async (data: AuthRegisterRequest): Promise<ApiResponse<AuthResponse>> => {
    const response = await userApi.post('/auth/register', data);
    return response.data;
  },

  /**
   * Login user with email/phone and password
   */
  login: async (data: LoginRequest): Promise<ApiResponse<AuthResponse>> => {
    const response = await userApi.post('/auth/login', data);
    return response.data;
  },

  /**
   * Logout user
   */
  logout: async (): Promise<ApiResponse<void>> => {
    const response = await userApi.post('/auth/logout');
    return response.data;
  },

  /**
   * Refresh access token
   */
  refreshToken: async (refreshToken: string): Promise<ApiResponse<AuthResponse>> => {
    const response = await userApi.post('/auth/refresh', { refreshToken });
    return response.data;
  },

  /**
   * Send OTP for login (email or phone)
   */
  sendOtp: async (contact: string, type: 'EMAIL' | 'PHONE'): Promise<ApiResponse<{ message: string }>> => {
    const response = await userApi.post('/auth/send-otp', { contact: contact.trim(), type });
    return response.data;
  },

  /**
   * Verify OTP and return tokens and user
   */
  verifyOtp: async (contact: string, otp: string): Promise<ApiResponse<AuthResponse>> => {
    const response = await userApi.post('/auth/verify-otp', { contact: contact.trim(), otp });
    return response.data;
  },

  /**
   * Request password reset
   */
  forgotPassword: async (email: string): Promise<ApiResponse<{ message: string }>> => {
    const response = await userApi.post('/auth/forgot-password', { email });
    return response.data;
  },

  /**
   * Reset password with token
   */
  resetPassword: async (token: string, newPassword: string): Promise<ApiResponse<void>> => {
    const response = await userApi.post('/auth/reset-password', { token, newPassword });
    return response.data;
  },

  // ============================================
  // User Profile
  // ============================================

  /**
   * Get current user profile
   */
  getCurrentUser: async (): Promise<ApiResponse<User>> => {
    const response = await userApi.get('/users/me');
    return response.data;
  },

  /**
   * Get user by ID
   */
  getUserById: async (userId: string): Promise<ApiResponse<User>> => {
    const response = await userApi.get(`/users/${userId}`);
    return response.data;
  },

  /**
   * Get user by phone (e.g. for referral search). Returns null if not found.
   */
  getUserByPhone: async (phone: string): Promise<ApiResponse<User> | { data: null }> => {
    try {
      const response = await userApi.get(`/users/by-phone/${encodeURIComponent(phone)}`);
      return response.data;
    } catch {
      return { data: null };
    }
  },

  /**
   * Update user profile
   */
  updateProfile: async (data: UpdateUserRequest): Promise<ApiResponse<User>> => {
    const response = await userApi.put('/users/me', data);
    return response.data;
  },

  /**
   * Upload profile image
   */
  uploadProfileImage: async (file: File): Promise<ApiResponse<{ imageUrl: string }>> => {
    const formData = new FormData();
    formData.append('file', file);
    const response = await userApi.post('/users/me/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  },

  /**
   * Delete user account
   */
  deleteAccount: async (): Promise<ApiResponse<void>> => {
    const response = await userApi.delete('/users/me');
    return response.data;
  },

  // ============================================
  // User Addresses
  // ============================================

  /**
   * Get all addresses for current user
   */
  getAddresses: async (): Promise<ApiResponse<Address[]>> => {
    const response = await userApi.get('/users/me/addresses');
    return response.data;
  },

  /**
   * Add new address
   */
  addAddress: async (address: Omit<Address, 'id'>): Promise<ApiResponse<Address>> => {
    const response = await userApi.post('/users/me/addresses', address);
    return response.data;
  },

  /**
   * Update address
   */
  updateAddress: async (addressId: string, address: Partial<Address>): Promise<ApiResponse<Address>> => {
    const response = await userApi.put(`/users/me/addresses/${addressId}`, address);
    return response.data;
  },

  /**
   * Delete address
   */
  deleteAddress: async (addressId: string): Promise<ApiResponse<void>> => {
    const response = await userApi.delete(`/users/me/addresses/${addressId}`);
    return response.data;
  },

  /**
   * Set default address
   */
  setDefaultAddress: async (addressId: string): Promise<ApiResponse<Address>> => {
    const response = await userApi.put(`/users/me/addresses/${addressId}/default`);
    return response.data;
  },

  // ============================================
  // User Preferences
  // ============================================

  /**
   * Get user preferences
   */
  getPreferences: async (): Promise<ApiResponse<UserPreferences>> => {
    const response = await userApi.get('/users/me/preferences');
    return response.data;
  },

  /**
   * Update user preferences
   */
  updatePreferences: async (preferences: Partial<UserPreferences>): Promise<ApiResponse<UserPreferences>> => {
    const response = await userApi.put('/users/me/preferences', preferences);
    return response.data;
  },

  // ============================================
  // Loyalty & Rewards
  // ============================================

  /**
   * Get loyalty profile
   */
  getLoyaltyProfile: async (): Promise<ApiResponse<LoyaltyProfile>> => {
    const response = await userApi.get('/users/me/loyalty');
    return response.data;
  },

  /**
   * Get loyalty points history
   */
  getLoyaltyHistory: async (page = 0, size = 20): Promise<ApiResponse<any>> => {
    const response = await userApi.get('/users/me/loyalty/history', { params: { page, size } });
    return response.data;
  },

  /**
   * Redeem loyalty points
   */
  redeemPoints: async (points: number, rewardId: string): Promise<ApiResponse<any>> => {
    const response = await userApi.post('/users/me/loyalty/redeem', { points, rewardId });
    return response.data;
  },

  // ============================================
  // Favorites
  // ============================================

  /**
   * Get favorite restaurants
   */
  getFavoriteRestaurants: async (): Promise<ApiResponse<string[]>> => {
    const response = await userApi.get('/users/me/favorites/restaurants');
    return response.data;
  },

  /**
   * Add restaurant to favorites
   */
  addFavoriteRestaurant: async (restaurantId: string): Promise<ApiResponse<void>> => {
    const response = await userApi.post(`/users/me/favorites/restaurants/${restaurantId}`);
    return response.data;
  },

  /**
   * Remove restaurant from favorites
   */
  removeFavoriteRestaurant: async (restaurantId: string): Promise<ApiResponse<void>> => {
    const response = await userApi.delete(`/users/me/favorites/restaurants/${restaurantId}`);
    return response.data;
  },

  /**
   * Get favorite menu items
   */
  getFavoriteItems: async (): Promise<ApiResponse<string[]>> => {
    const response = await userApi.get('/users/me/favorites/items');
    return response.data;
  },

  /**
   * Add menu item to favorites
   */
  addFavoriteItem: async (itemId: string): Promise<ApiResponse<void>> => {
    const response = await userApi.post(`/users/me/favorites/items/${itemId}`);
    return response.data;
  },

  /**
   * Remove menu item from favorites
   */
  removeFavoriteItem: async (itemId: string): Promise<ApiResponse<void>> => {
    const response = await userApi.delete(`/users/me/favorites/items/${itemId}`);
    return response.data;
  },
};

export default userService;

