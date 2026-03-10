/**
 * Menu Slice - Menu state management
 */

import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { menuService } from '@/services';
import type {
  MenuItem,
  MenuCategory,
  CreateMenuItemRequest,
} from '@/types/api.types';

interface MenuState {
  // Consumer view
  categories: MenuCategory[];
  menuItems: MenuItem[];
  trendingItems: MenuItem[];
  recommendedItems: MenuItem[];
  currentItem: MenuItem | null;
  
  // Restaurant owner view
  editingItem: MenuItem | null;
  
  // Search results
  searchResults: MenuItem[];
  searchQuery: string;
  
  // Loading states
  isLoading: boolean;
  isSaving: boolean;
  error: string | null;
}

const initialState: MenuState = {
  categories: [],
  menuItems: [],
  trendingItems: [],
  recommendedItems: [],
  currentItem: null,
  editingItem: null,
  searchResults: [],
  searchQuery: '',
  isLoading: false,
  isSaving: false,
  error: null,
};

// Async Thunks
export const fetchCategories = createAsyncThunk(
  'menu/fetchCategories',
  async (restaurantId: string, { rejectWithValue }) => {
    try {
      const response = await menuService.getCategories(restaurantId);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch categories');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch categories');
    }
  }
);

export const fetchMenuItems = createAsyncThunk(
  'menu/fetchMenuItems',
  async (restaurantId: string, { rejectWithValue }) => {
    try {
      const response = await menuService.getMenuItems(restaurantId, 0, 100);
      if (response.success) {
        return response.data.content;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch menu items');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch menu items');
    }
  }
);

export const fetchMenuItemsByCategory = createAsyncThunk(
  'menu/fetchMenuItemsByCategory',
  async (
    { restaurantId, categoryId }: { restaurantId: string; categoryId: string },
    { rejectWithValue }
  ) => {
    try {
      const response = await menuService.getMenuItemsByCategory(restaurantId, categoryId);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch menu items');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch menu items');
    }
  }
);

export const fetchMenuItemById = createAsyncThunk(
  'menu/fetchMenuItemById',
  async (
    { restaurantId, itemId }: { restaurantId: string; itemId: string },
    { rejectWithValue }
  ) => {
    try {
      const response = await menuService.getMenuItemById(restaurantId, itemId);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch menu item');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch menu item');
    }
  }
);

export const fetchTrendingItems = createAsyncThunk(
  'menu/fetchTrendingItems',
  async (_, { rejectWithValue }) => {
    try {
      const response = await menuService.getTrendingItems(10);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch trending items');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch trending items');
    }
  }
);

export const fetchRecommendedItems = createAsyncThunk(
  'menu/fetchRecommendedItems',
  async (_, { rejectWithValue }) => {
    try {
      const response = await menuService.getRecommendedItems(10);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch recommended items');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch recommended items');
    }
  }
);

export const searchMenuItems = createAsyncThunk(
  'menu/searchMenuItems',
  async (
    { query, filters }: { query: string; filters?: any },
    { rejectWithValue }
  ) => {
    try {
      const response = await menuService.searchMenuItems(query, filters, 0, 50);
      if (response.success) {
        return response.data.content;
      }
      return rejectWithValue(response.error?.message || 'Search failed');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Search failed');
    }
  }
);

// Restaurant Owner Thunks
export const createCategory = createAsyncThunk(
  'menu/createCategory',
  async (
    { restaurantId, data }: { restaurantId: string; data: { name: string; description?: string } },
    { rejectWithValue }
  ) => {
    try {
      const response = await menuService.createCategory(restaurantId, data);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to create category');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to create category');
    }
  }
);

export const updateCategory = createAsyncThunk(
  'menu/updateCategory',
  async (
    { restaurantId, categoryId, data }: { restaurantId: string; categoryId: string; data: Partial<MenuCategory> },
    { rejectWithValue }
  ) => {
    try {
      const response = await menuService.updateCategory(restaurantId, categoryId, data);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to update category');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to update category');
    }
  }
);

export const deleteCategory = createAsyncThunk(
  'menu/deleteCategory',
  async (
    { restaurantId, categoryId }: { restaurantId: string; categoryId: string },
    { rejectWithValue }
  ) => {
    try {
      await menuService.deleteCategory(restaurantId, categoryId);
      return categoryId;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to delete category');
    }
  }
);

export const createMenuItem = createAsyncThunk(
  'menu/createMenuItem',
  async (data: CreateMenuItemRequest, { rejectWithValue }) => {
    try {
      const response = await menuService.createMenuItem(data);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to create menu item');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to create menu item');
    }
  }
);

export const updateMenuItem = createAsyncThunk(
  'menu/updateMenuItem',
  async (
    { restaurantId, itemId, data }: { restaurantId: string; itemId: string; data: Partial<CreateMenuItemRequest> },
    { rejectWithValue }
  ) => {
    try {
      const response = await menuService.updateMenuItem(restaurantId, itemId, data);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to update menu item');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to update menu item');
    }
  }
);

export const deleteMenuItem = createAsyncThunk(
  'menu/deleteMenuItem',
  async (
    { restaurantId, itemId }: { restaurantId: string; itemId: string },
    { rejectWithValue }
  ) => {
    try {
      await menuService.deleteMenuItem(restaurantId, itemId);
      return itemId;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to delete menu item');
    }
  }
);

export const updateItemAvailability = createAsyncThunk(
  'menu/updateItemAvailability',
  async (
    { restaurantId, itemId, isAvailable }: { restaurantId: string; itemId: string; isAvailable: boolean },
    { rejectWithValue }
  ) => {
    try {
      const response = await menuService.updateItemAvailability(restaurantId, itemId, { isAvailable, availableDays: [] });
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to update availability');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to update availability');
    }
  }
);

export const updateItemPrice = createAsyncThunk(
  'menu/updateItemPrice',
  async (
    { restaurantId, itemId, basePrice, discountPercentage }: { restaurantId: string; itemId: string; basePrice: number; discountPercentage?: number },
    { rejectWithValue }
  ) => {
    try {
      const response = await menuService.updateItemPrice(restaurantId, itemId, basePrice, discountPercentage);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to update price');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to update price');
    }
  }
);

const menuSlice = createSlice({
  name: 'menu',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    setSearchQuery: (state, action: PayloadAction<string>) => {
      state.searchQuery = action.payload;
    },
    clearSearchResults: (state) => {
      state.searchResults = [];
      state.searchQuery = '';
    },
    setCurrentItem: (state, action: PayloadAction<MenuItem | null>) => {
      state.currentItem = action.payload;
    },
    setEditingItem: (state, action: PayloadAction<MenuItem | null>) => {
      state.editingItem = action.payload;
    },
    clearMenu: (state) => {
      state.categories = [];
      state.menuItems = [];
    },
  },
  extraReducers: (builder) => {
    // Categories
    builder
      .addCase(fetchCategories.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchCategories.fulfilled, (state, action) => {
        state.isLoading = false;
        state.categories = action.payload;
      })
      .addCase(fetchCategories.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    builder
      .addCase(createCategory.pending, (state) => {
        state.isSaving = true;
      })
      .addCase(createCategory.fulfilled, (state, action) => {
        state.isSaving = false;
        state.categories.push(action.payload);
      })
      .addCase(createCategory.rejected, (state, action) => {
        state.isSaving = false;
        state.error = action.payload as string;
      });

    builder
      .addCase(updateCategory.fulfilled, (state, action) => {
        const index = state.categories.findIndex((c) => c.id === action.payload.id);
        if (index !== -1) {
          state.categories[index] = action.payload;
        }
      });

    builder
      .addCase(deleteCategory.fulfilled, (state, action) => {
        state.categories = state.categories.filter((c) => c.id !== action.payload);
      });

    // Menu Items
    builder
      .addCase(fetchMenuItems.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchMenuItems.fulfilled, (state, action) => {
        state.isLoading = false;
        state.menuItems = action.payload;
      })
      .addCase(fetchMenuItems.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    builder
      .addCase(fetchMenuItemsByCategory.fulfilled, (state, action) => {
        // Merge items by category
        const otherItems = state.menuItems.filter(
          (item) => !action.payload.some((newItem) => newItem.id === item.id)
        );
        state.menuItems = [...otherItems, ...action.payload];
      });

    builder
      .addCase(fetchMenuItemById.fulfilled, (state, action) => {
        state.currentItem = action.payload;
      });

    builder
      .addCase(createMenuItem.pending, (state) => {
        state.isSaving = true;
      })
      .addCase(createMenuItem.fulfilled, (state, action) => {
        state.isSaving = false;
        state.menuItems.push(action.payload);
      })
      .addCase(createMenuItem.rejected, (state, action) => {
        state.isSaving = false;
        state.error = action.payload as string;
      });

    builder
      .addCase(updateMenuItem.fulfilled, (state, action) => {
        const index = state.menuItems.findIndex((item) => item.id === action.payload.id);
        if (index !== -1) {
          state.menuItems[index] = action.payload;
        }
        if (state.currentItem?.id === action.payload.id) {
          state.currentItem = action.payload;
        }
      });

    builder
      .addCase(deleteMenuItem.fulfilled, (state, action) => {
        state.menuItems = state.menuItems.filter((item) => item.id !== action.payload);
      });

    builder
      .addCase(updateItemAvailability.fulfilled, (state, action) => {
        const index = state.menuItems.findIndex((item) => item.id === action.payload.id);
        if (index !== -1) {
          state.menuItems[index] = action.payload;
        }
      });

    builder
      .addCase(updateItemPrice.fulfilled, (state, action) => {
        const index = state.menuItems.findIndex((item) => item.id === action.payload.id);
        if (index !== -1) {
          state.menuItems[index] = action.payload;
        }
      });

    // Trending & Recommended
    builder
      .addCase(fetchTrendingItems.fulfilled, (state, action) => {
        state.trendingItems = action.payload;
      });

    builder
      .addCase(fetchRecommendedItems.fulfilled, (state, action) => {
        state.recommendedItems = action.payload;
      });

    // Search
    builder
      .addCase(searchMenuItems.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(searchMenuItems.fulfilled, (state, action) => {
        state.isLoading = false;
        state.searchResults = action.payload;
      })
      .addCase(searchMenuItems.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });
  },
});

export const {
  clearError,
  setSearchQuery,
  clearSearchResults,
  setCurrentItem,
  setEditingItem,
  clearMenu,
} = menuSlice.actions;

export default menuSlice.reducer;

