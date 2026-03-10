/**
 * Menu Service - API calls for menu management
 */

import { menuApi } from './api.config';
import type {
  ApiResponse,
  PaginatedResponse,
  MenuItem,
  MenuCategory,
  CreateMenuItemRequest,
  MenuCustomization,
  MenuAddon,
  MenuItemAvailability,
} from '@/types/api.types';

export const menuService = {
  // ============================================
  // Menu Categories
  // ============================================

  /**
   * Get all categories for a restaurant
   */
  getCategories: async (restaurantId: string): Promise<ApiResponse<MenuCategory[]>> => {
    const response = await menuApi.get(`/restaurants/${restaurantId}/categories`);
    return response.data;
  },

  /**
   * Get category by ID
   */
  getCategoryById: async (restaurantId: string, categoryId: string): Promise<ApiResponse<MenuCategory>> => {
    const response = await menuApi.get(`/restaurants/${restaurantId}/categories/${categoryId}`);
    return response.data;
  },

  /**
   * Create new category
   */
  createCategory: async (
    restaurantId: string,
    data: { name: string; description?: string; imageUrl?: string; displayOrder?: number }
  ): Promise<ApiResponse<MenuCategory>> => {
    const response = await menuApi.post(`/restaurants/${restaurantId}/categories`, data);
    return response.data;
  },

  /**
   * Update category
   */
  updateCategory: async (
    restaurantId: string,
    categoryId: string,
    data: Partial<MenuCategory>
  ): Promise<ApiResponse<MenuCategory>> => {
    const response = await menuApi.put(`/restaurants/${restaurantId}/categories/${categoryId}`, data);
    return response.data;
  },

  /**
   * Delete category
   */
  deleteCategory: async (restaurantId: string, categoryId: string): Promise<ApiResponse<void>> => {
    const response = await menuApi.delete(`/restaurants/${restaurantId}/categories/${categoryId}`);
    return response.data;
  },

  /**
   * Reorder categories
   */
  reorderCategories: async (
    restaurantId: string,
    categoryIds: string[]
  ): Promise<ApiResponse<MenuCategory[]>> => {
    const response = await menuApi.put(`/restaurants/${restaurantId}/categories/reorder`, { categoryIds });
    return response.data;
  },

  // ============================================
  // Menu Items
  // ============================================

  /**
   * Get all menu items for a restaurant
   */
  getMenuItems: async (
    restaurantId: string,
    page = 0,
    size = 50
  ): Promise<ApiResponse<PaginatedResponse<MenuItem>>> => {
    const response = await menuApi.get(`/restaurants/${restaurantId}/items`, {
      params: { page, size },
    });
    return response.data;
  },

  /**
   * Get menu items by category
   */
  getMenuItemsByCategory: async (
    restaurantId: string,
    categoryId: string
  ): Promise<ApiResponse<MenuItem[]>> => {
    const response = await menuApi.get(`/restaurants/${restaurantId}/categories/${categoryId}/items`);
    return response.data;
  },

  /**
   * Get menu item by ID
   */
  getMenuItemById: async (restaurantId: string, itemId: string): Promise<ApiResponse<MenuItem>> => {
    const response = await menuApi.get(`/restaurants/${restaurantId}/items/${itemId}`);
    return response.data;
  },

  /**
   * Create new menu item
   */
  createMenuItem: async (data: CreateMenuItemRequest): Promise<ApiResponse<MenuItem>> => {
    const response = await menuApi.post(`/restaurants/${data.restaurantId}/items`, data);
    return response.data;
  },

  /**
   * Update menu item
   */
  updateMenuItem: async (
    restaurantId: string,
    itemId: string,
    data: Partial<CreateMenuItemRequest>
  ): Promise<ApiResponse<MenuItem>> => {
    const response = await menuApi.put(`/restaurants/${restaurantId}/items/${itemId}`, data);
    return response.data;
  },

  /**
   * Delete menu item
   */
  deleteMenuItem: async (restaurantId: string, itemId: string): Promise<ApiResponse<void>> => {
    const response = await menuApi.delete(`/restaurants/${restaurantId}/items/${itemId}`);
    return response.data;
  },

  /**
   * Update item availability
   */
  updateItemAvailability: async (
    restaurantId: string,
    itemId: string,
    availability: MenuItemAvailability
  ): Promise<ApiResponse<MenuItem>> => {
    const response = await menuApi.put(
      `/restaurants/${restaurantId}/items/${itemId}/availability`,
      availability
    );
    return response.data;
  },

  /**
   * Bulk update availability
   */
  bulkUpdateAvailability: async (
    restaurantId: string,
    updates: { itemId: string; isAvailable: boolean }[]
  ): Promise<ApiResponse<void>> => {
    const response = await menuApi.put(`/restaurants/${restaurantId}/items/bulk-availability`, { updates });
    return response.data;
  },

  /**
   * Upload item images
   */
  uploadItemImages: async (
    restaurantId: string,
    itemId: string,
    files: File[]
  ): Promise<ApiResponse<{ urls: string[] }>> => {
    const formData = new FormData();
    files.forEach((file) => formData.append('files', file));
    const response = await menuApi.post(
      `/restaurants/${restaurantId}/items/${itemId}/images`,
      formData,
      { headers: { 'Content-Type': 'multipart/form-data' } }
    );
    return response.data;
  },

  // ============================================
  // Customizations & Addons
  // ============================================

  /**
   * Get item customizations
   */
  getItemCustomizations: async (
    restaurantId: string,
    itemId: string
  ): Promise<ApiResponse<MenuCustomization[]>> => {
    const response = await menuApi.get(`/restaurants/${restaurantId}/items/${itemId}/customizations`);
    return response.data;
  },

  /**
   * Add customization to item
   */
  addCustomization: async (
    restaurantId: string,
    itemId: string,
    customization: Omit<MenuCustomization, 'id'>
  ): Promise<ApiResponse<MenuCustomization>> => {
    const response = await menuApi.post(
      `/restaurants/${restaurantId}/items/${itemId}/customizations`,
      customization
    );
    return response.data;
  },

  /**
   * Update customization
   */
  updateCustomization: async (
    restaurantId: string,
    itemId: string,
    customizationId: string,
    data: Partial<MenuCustomization>
  ): Promise<ApiResponse<MenuCustomization>> => {
    const response = await menuApi.put(
      `/restaurants/${restaurantId}/items/${itemId}/customizations/${customizationId}`,
      data
    );
    return response.data;
  },

  /**
   * Delete customization
   */
  deleteCustomization: async (
    restaurantId: string,
    itemId: string,
    customizationId: string
  ): Promise<ApiResponse<void>> => {
    const response = await menuApi.delete(
      `/restaurants/${restaurantId}/items/${itemId}/customizations/${customizationId}`
    );
    return response.data;
  },

  /**
   * Get item addons
   */
  getItemAddons: async (restaurantId: string, itemId: string): Promise<ApiResponse<MenuAddon[]>> => {
    const response = await menuApi.get(`/restaurants/${restaurantId}/items/${itemId}/addons`);
    return response.data;
  },

  /**
   * Add addon to item
   */
  addAddon: async (
    restaurantId: string,
    itemId: string,
    addon: Omit<MenuAddon, 'id'>
  ): Promise<ApiResponse<MenuAddon>> => {
    const response = await menuApi.post(`/restaurants/${restaurantId}/items/${itemId}/addons`, addon);
    return response.data;
  },

  /**
   * Update addon
   */
  updateAddon: async (
    restaurantId: string,
    itemId: string,
    addonId: string,
    data: Partial<MenuAddon>
  ): Promise<ApiResponse<MenuAddon>> => {
    const response = await menuApi.put(
      `/restaurants/${restaurantId}/items/${itemId}/addons/${addonId}`,
      data
    );
    return response.data;
  },

  /**
   * Delete addon
   */
  deleteAddon: async (
    restaurantId: string,
    itemId: string,
    addonId: string
  ): Promise<ApiResponse<void>> => {
    const response = await menuApi.delete(`/restaurants/${restaurantId}/items/${itemId}/addons/${addonId}`);
    return response.data;
  },

  // ============================================
  // Pricing
  // ============================================

  /**
   * Update item price
   */
  updateItemPrice: async (
    restaurantId: string,
    itemId: string,
    basePrice: number,
    discountPercentage?: number
  ): Promise<ApiResponse<MenuItem>> => {
    const response = await menuApi.put(`/restaurants/${restaurantId}/items/${itemId}/price`, {
      basePrice,
      discountPercentage,
    });
    return response.data;
  },

  /**
   * Bulk update prices
   */
  bulkUpdatePrices: async (
    restaurantId: string,
    updates: { itemId: string; basePrice: number; discountPercentage?: number }[]
  ): Promise<ApiResponse<void>> => {
    const response = await menuApi.put(`/restaurants/${restaurantId}/items/bulk-price`, { updates });
    return response.data;
  },

  // ============================================
  // Search & Discovery
  // ============================================

  /**
   * Search menu items across restaurants
   */
  searchMenuItems: async (
    query: string,
    filters?: {
      cuisines?: string[];
      vegetarian?: boolean;
      vegan?: boolean;
      maxPrice?: number;
      minRating?: number;
    },
    page = 0,
    size = 20
  ): Promise<ApiResponse<PaginatedResponse<MenuItem>>> => {
    const response = await menuApi.get('/items/search', {
      params: { query, ...filters, page, size },
    });
    return response.data;
  },

  /**
   * Get trending items
   */
  getTrendingItems: async (limit = 10): Promise<ApiResponse<MenuItem[]>> => {
    const response = await menuApi.get('/items/trending', { params: { limit } });
    return response.data;
  },

  /**
   * Get recommended items for user
   */
  getRecommendedItems: async (limit = 10): Promise<ApiResponse<MenuItem[]>> => {
    const response = await menuApi.get('/items/recommended', { params: { limit } });
    return response.data;
  },

  /**
   * Get items similar to given item
   */
  getSimilarItems: async (itemId: string, limit = 5): Promise<ApiResponse<MenuItem[]>> => {
    const response = await menuApi.get(`/items/${itemId}/similar`, { params: { limit } });
    return response.data;
  },
};

export default menuService;

