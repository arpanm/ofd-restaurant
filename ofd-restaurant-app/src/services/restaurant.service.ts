/**
 * Restaurant Service - API calls for restaurant management
 */

import { restaurantApi } from './api.config';
import type {
  ApiResponse,
  PaginatedResponse,
  PageRequest,
  Restaurant,
  RegisterRestaurantRequest,
  SubmitOnboardingRequest,
  SaveOnboardingDraftRequest,
  SubmitOnboardingByDraftRequest,
  OnboardingDraftResponse,
  RestaurantStatus,
  OperatingHours,
  RestaurantFeatures,
  SearchFilters,
} from '@/types/api.types';

export const restaurantService = {
  // ============================================
  // Restaurant Discovery (Consumer APIs)
  // ============================================

  /**
   * Get all restaurants with pagination
   */
  getRestaurants: async (params?: PageRequest & SearchFilters): Promise<ApiResponse<PaginatedResponse<Restaurant>>> => {
    const response = await restaurantApi.get('/restaurants', { params });
    return response.data;
  },

  /**
   * Get restaurant by ID
   */
  getRestaurantById: async (restaurantId: string): Promise<ApiResponse<Restaurant>> => {
    const response = await restaurantApi.get(`/restaurants/${restaurantId}`);
    return response.data;
  },

  /**
   * Search restaurants
   */
  searchRestaurants: async (
    query: string,
    filters?: SearchFilters,
    page = 0,
    size = 20
  ): Promise<ApiResponse<PaginatedResponse<Restaurant>>> => {
    const response = await restaurantApi.get('/restaurants/search', {
      params: { query, ...filters, page, size },
    });
    return response.data;
  },

  /**
   * Get nearby restaurants
   */
  getNearbyRestaurants: async (
    latitude: number,
    longitude: number,
    radiusKm = 5,
    page = 0,
    size = 20
  ): Promise<ApiResponse<PaginatedResponse<Restaurant>>> => {
    const response = await restaurantApi.get('/restaurants/nearby', {
      params: { latitude, longitude, radiusKm, page, size },
    });
    return response.data;
  },

  /**
   * Get restaurants by cuisine
   */
  getRestaurantsByCuisine: async (
    cuisine: string,
    page = 0,
    size = 20
  ): Promise<ApiResponse<PaginatedResponse<Restaurant>>> => {
    const response = await restaurantApi.get(`/restaurants/cuisine/${cuisine}`, {
      params: { page, size },
    });
    return response.data;
  },

  /**
   * Get featured restaurants
   */
  getFeaturedRestaurants: async (limit = 10): Promise<ApiResponse<Restaurant[]>> => {
    const response = await restaurantApi.get('/restaurants/featured', { params: { limit } });
    return response.data;
  },

  /**
   * Get popular restaurants
   */
  getPopularRestaurants: async (limit = 10): Promise<ApiResponse<Restaurant[]>> => {
    const response = await restaurantApi.get('/restaurants/popular', { params: { limit } });
    return response.data;
  },

  /**
   * Get new restaurants
   */
  getNewRestaurants: async (limit = 10): Promise<ApiResponse<Restaurant[]>> => {
    const response = await restaurantApi.get('/restaurants/new', { params: { limit } });
    return response.data;
  },

  /**
   * Get available cuisines
   */
  getCuisines: async (): Promise<ApiResponse<string[]>> => {
    const response = await restaurantApi.get('/restaurants/cuisines');
    return response.data;
  },

  // ============================================
  // Restaurant Onboarding & Management (Owner APIs)
  // ============================================

  /**
   * Register new restaurant
   */
  registerRestaurant: async (data: RegisterRestaurantRequest): Promise<ApiResponse<Restaurant>> => {
    const response = await restaurantApi.post('/restaurants', data);
    return response.data;
  },

  /**
   * Submit full onboarding flow (all steps). Creates restaurant + first outlet with default contract.
   */
  submitOnboarding: async (data: SubmitOnboardingRequest): Promise<ApiResponse<Restaurant>> => {
    const response = await restaurantApi.post('/restaurants/onboarding', data);
    return response.data;
  },

  /**
   * Create a new onboarding draft. Returns draftId to store (e.g. localStorage) for save/resume.
   */
  createOnboardingDraft: async (): Promise<ApiResponse<{ draftId: string }>> => {
    const response = await restaurantApi.post('/restaurants/onboarding/draft', {});
    return response.data;
  },

  /**
   * Get saved draft for prefill / resume.
   */
  getOnboardingDraft: async (draftId: string): Promise<ApiResponse<OnboardingDraftResponse>> => {
    const response = await restaurantApi.get(`/restaurants/onboarding/draft/${draftId}`);
    return response.data;
  },

  /**
   * Save draft (current step and form data). Call on Next or when leaving a step.
   */
  saveOnboardingDraft: async (
    draftId: string,
    data: SaveOnboardingDraftRequest
  ): Promise<ApiResponse<OnboardingDraftResponse>> => {
    const response = await restaurantApi.patch(`/restaurants/onboarding/draft/${draftId}`, data);
    return response.data;
  },

  /**
   * Submit onboarding from draft. Creates restaurant, owner user, sends verification email/SMS.
   */
  submitOnboardingByDraft: async (
    data: SubmitOnboardingByDraftRequest
  ): Promise<ApiResponse<Restaurant>> => {
    const response = await restaurantApi.post('/restaurants/onboarding/submit', data);
    return response.data;
  },

  /**
   * Get restaurants owned by current user
   */
  getMyRestaurants: async (): Promise<ApiResponse<Restaurant[]>> => {
    const response = await restaurantApi.get('/restaurants/my');
    return response.data;
  },

  /**
   * Update restaurant details
   */
  updateRestaurant: async (
    restaurantId: string,
    data: Partial<RegisterRestaurantRequest>
  ): Promise<ApiResponse<Restaurant>> => {
    const response = await restaurantApi.put(`/restaurants/${restaurantId}`, data);
    return response.data;
  },

  /**
   * Update restaurant status
   */
  updateRestaurantStatus: async (
    restaurantId: string,
    status: RestaurantStatus
  ): Promise<ApiResponse<Restaurant>> => {
    const response = await restaurantApi.put(`/restaurants/${restaurantId}/status`, { status });
    return response.data;
  },

  /**
   * Update operating hours
   */
  updateOperatingHours: async (
    restaurantId: string,
    hours: OperatingHours[]
  ): Promise<ApiResponse<Restaurant>> => {
    const response = await restaurantApi.put(`/restaurants/${restaurantId}/hours`, hours);
    return response.data;
  },

  /**
   * Update restaurant features
   */
  updateFeatures: async (
    restaurantId: string,
    features: Partial<RestaurantFeatures>
  ): Promise<ApiResponse<Restaurant>> => {
    const response = await restaurantApi.put(`/restaurants/${restaurantId}/features`, features);
    return response.data;
  },

  /**
   * Upload restaurant images
   */
  uploadImages: async (
    restaurantId: string,
    type: 'logo' | 'banner' | 'gallery',
    files: File[]
  ): Promise<ApiResponse<{ urls: string[] }>> => {
    const formData = new FormData();
    files.forEach((file) => formData.append('files', file));
    formData.append('type', type);
    const response = await restaurantApi.post(`/restaurants/${restaurantId}/images`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return response.data;
  },

  /**
   * Delete restaurant image
   */
  deleteImage: async (restaurantId: string, imageUrl: string): Promise<ApiResponse<void>> => {
    const response = await restaurantApi.delete(`/restaurants/${restaurantId}/images`, {
      data: { imageUrl },
    });
    return response.data;
  },

  // ============================================
  // Restaurant Analytics (Owner APIs)
  // ============================================

  /**
   * Get restaurant dashboard stats
   */
  getDashboardStats: async (restaurantId: string): Promise<ApiResponse<any>> => {
    const response = await restaurantApi.get(`/restaurants/${restaurantId}/stats`);
    return response.data;
  },

  /**
   * Get restaurant revenue
   */
  getRevenue: async (
    restaurantId: string,
    startDate: string,
    endDate: string
  ): Promise<ApiResponse<any>> => {
    const response = await restaurantApi.get(`/restaurants/${restaurantId}/revenue`, {
      params: { startDate, endDate },
    });
    return response.data;
  },

  /**
   * Get order analytics
   */
  getOrderAnalytics: async (
    restaurantId: string,
    startDate: string,
    endDate: string
  ): Promise<ApiResponse<any>> => {
    const response = await restaurantApi.get(`/restaurants/${restaurantId}/analytics/orders`, {
      params: { startDate, endDate },
    });
    return response.data;
  },

  /**
   * Get popular items analytics
   */
  getPopularItems: async (restaurantId: string, limit = 10): Promise<ApiResponse<any>> => {
    const response = await restaurantApi.get(`/restaurants/${restaurantId}/analytics/popular-items`, {
      params: { limit },
    });
    return response.data;
  },

  // ============================================
  // Reviews & Ratings
  // ============================================

  /**
   * Get restaurant reviews
   */
  getReviews: async (
    restaurantId: string,
    page = 0,
    size = 20
  ): Promise<ApiResponse<PaginatedResponse<any>>> => {
    const response = await restaurantApi.get(`/restaurants/${restaurantId}/reviews`, {
      params: { page, size },
    });
    return response.data;
  },

  /**
   * Reply to review (owner only)
   */
  replyToReview: async (
    restaurantId: string,
    reviewId: string,
    reply: string
  ): Promise<ApiResponse<void>> => {
    const response = await restaurantApi.post(`/restaurants/${restaurantId}/reviews/${reviewId}/reply`, {
      reply,
    });
    return response.data;
  },
};

export default restaurantService;

