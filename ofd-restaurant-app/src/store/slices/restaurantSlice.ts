/**
 * Restaurant Slice - Restaurant state management
 */

import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { restaurantService } from '@/services';
import type {
  Restaurant,
  PaginatedResponse,
  RegisterRestaurantRequest,
  SearchFilters,
} from '@/types/api.types';

interface RestaurantState {
  // Consumer view
  restaurants: Restaurant[];
  featuredRestaurants: Restaurant[];
  popularRestaurants: Restaurant[];
  nearbyRestaurants: Restaurant[];
  currentRestaurant: Restaurant | null;
  
  // Restaurant owner view
  myRestaurants: Restaurant[];
  selectedRestaurant: Restaurant | null;
  dashboardStats: any | null;
  
  // Pagination
  totalElements: number;
  totalPages: number;
  currentPage: number;
  
  // Filters
  cuisines: string[];
  activeFilters: SearchFilters;
  searchQuery: string;
  
  // Loading states
  isLoading: boolean;
  isLoadingMore: boolean;
  error: string | null;
}

const initialState: RestaurantState = {
  restaurants: [],
  featuredRestaurants: [],
  popularRestaurants: [],
  nearbyRestaurants: [],
  currentRestaurant: null,
  myRestaurants: [],
  selectedRestaurant: null,
  dashboardStats: null,
  totalElements: 0,
  totalPages: 0,
  currentPage: 0,
  cuisines: [],
  activeFilters: {},
  searchQuery: '',
  isLoading: false,
  isLoadingMore: false,
  error: null,
};

// Async Thunks
export const fetchRestaurants = createAsyncThunk(
  'restaurant/fetchRestaurants',
  async (params: { page?: number; filters?: SearchFilters } = {}, { rejectWithValue }) => {
    try {
      const response = await restaurantService.getRestaurants({
        page: params.page || 0,
        size: 20,
        ...params.filters,
      });
      if (response.success) {
        return { data: response.data, isLoadMore: (params.page || 0) > 0 };
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch restaurants');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch restaurants');
    }
  }
);

export const searchRestaurants = createAsyncThunk(
  'restaurant/searchRestaurants',
  async (
    { query, filters, page = 0 }: { query: string; filters?: SearchFilters; page?: number },
    { rejectWithValue }
  ) => {
    try {
      const response = await restaurantService.searchRestaurants(query, filters, page, 20);
      if (response.success) {
        return { data: response.data, isLoadMore: page > 0 };
      }
      return rejectWithValue(response.error?.message || 'Search failed');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Search failed');
    }
  }
);

export const fetchNearbyRestaurants = createAsyncThunk(
  'restaurant/fetchNearbyRestaurants',
  async (
    { latitude, longitude, radiusKm = 5 }: { latitude: number; longitude: number; radiusKm?: number },
    { rejectWithValue }
  ) => {
    try {
      const response = await restaurantService.getNearbyRestaurants(latitude, longitude, radiusKm, 0, 20);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch nearby restaurants');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch nearby restaurants');
    }
  }
);

export const fetchFeaturedRestaurants = createAsyncThunk(
  'restaurant/fetchFeaturedRestaurants',
  async (_, { rejectWithValue }) => {
    try {
      const response = await restaurantService.getFeaturedRestaurants(10);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch featured restaurants');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch featured restaurants');
    }
  }
);

export const fetchPopularRestaurants = createAsyncThunk(
  'restaurant/fetchPopularRestaurants',
  async (_, { rejectWithValue }) => {
    try {
      const response = await restaurantService.getPopularRestaurants(10);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch popular restaurants');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch popular restaurants');
    }
  }
);

export const fetchRestaurantById = createAsyncThunk(
  'restaurant/fetchRestaurantById',
  async (restaurantId: string, { rejectWithValue }) => {
    try {
      const response = await restaurantService.getRestaurantById(restaurantId);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch restaurant');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch restaurant');
    }
  }
);

export const fetchCuisines = createAsyncThunk(
  'restaurant/fetchCuisines',
  async (_, { rejectWithValue }) => {
    try {
      const response = await restaurantService.getCuisines();
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch cuisines');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch cuisines');
    }
  }
);

// Restaurant Owner Thunks
export const fetchMyRestaurants = createAsyncThunk(
  'restaurant/fetchMyRestaurants',
  async (_, { rejectWithValue }) => {
    try {
      const response = await restaurantService.getMyRestaurants();
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch my restaurants');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch my restaurants');
    }
  }
);

export const registerRestaurant = createAsyncThunk(
  'restaurant/registerRestaurant',
  async (data: RegisterRestaurantRequest, { rejectWithValue }) => {
    try {
      const response = await restaurantService.registerRestaurant(data);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to register restaurant');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to register restaurant');
    }
  }
);

export const updateRestaurant = createAsyncThunk(
  'restaurant/updateRestaurant',
  async (
    { restaurantId, data }: { restaurantId: string; data: Partial<RegisterRestaurantRequest> },
    { rejectWithValue }
  ) => {
    try {
      const response = await restaurantService.updateRestaurant(restaurantId, data);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to update restaurant');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to update restaurant');
    }
  }
);

export const fetchDashboardStats = createAsyncThunk(
  'restaurant/fetchDashboardStats',
  async (restaurantId: string, { rejectWithValue }) => {
    try {
      const response = await restaurantService.getDashboardStats(restaurantId);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch dashboard stats');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch dashboard stats');
    }
  }
);

const restaurantSlice = createSlice({
  name: 'restaurant',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    setActiveFilters: (state, action: PayloadAction<SearchFilters>) => {
      state.activeFilters = action.payload;
    },
    setSearchQuery: (state, action: PayloadAction<string>) => {
      state.searchQuery = action.payload;
    },
    clearCurrentRestaurant: (state) => {
      state.currentRestaurant = null;
    },
    setSelectedRestaurant: (state, action: PayloadAction<Restaurant | null>) => {
      state.selectedRestaurant = action.payload;
    },
    clearRestaurants: (state) => {
      state.restaurants = [];
      state.totalElements = 0;
      state.totalPages = 0;
      state.currentPage = 0;
    },
  },
  extraReducers: (builder) => {
    // Fetch Restaurants
    builder
      .addCase(fetchRestaurants.pending, (state, action) => {
        if (action.meta.arg?.page === 0 || !action.meta.arg?.page) {
          state.isLoading = true;
        } else {
          state.isLoadingMore = true;
        }
        state.error = null;
      })
      .addCase(fetchRestaurants.fulfilled, (state, action) => {
        state.isLoading = false;
        state.isLoadingMore = false;
        if (action.payload.isLoadMore) {
          state.restaurants = [...state.restaurants, ...action.payload.data.content];
        } else {
          state.restaurants = action.payload.data.content;
        }
        state.totalElements = action.payload.data.totalElements;
        state.totalPages = action.payload.data.totalPages;
        state.currentPage = action.payload.data.number;
      })
      .addCase(fetchRestaurants.rejected, (state, action) => {
        state.isLoading = false;
        state.isLoadingMore = false;
        state.error = action.payload as string;
      });

    // Search Restaurants
    builder
      .addCase(searchRestaurants.pending, (state, action) => {
        if (action.meta.arg.page === 0) {
          state.isLoading = true;
        } else {
          state.isLoadingMore = true;
        }
        state.error = null;
      })
      .addCase(searchRestaurants.fulfilled, (state, action) => {
        state.isLoading = false;
        state.isLoadingMore = false;
        if (action.payload.isLoadMore) {
          state.restaurants = [...state.restaurants, ...action.payload.data.content];
        } else {
          state.restaurants = action.payload.data.content;
        }
        state.totalElements = action.payload.data.totalElements;
        state.totalPages = action.payload.data.totalPages;
        state.currentPage = action.payload.data.number;
      })
      .addCase(searchRestaurants.rejected, (state, action) => {
        state.isLoading = false;
        state.isLoadingMore = false;
        state.error = action.payload as string;
      });

    // Nearby Restaurants
    builder
      .addCase(fetchNearbyRestaurants.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(fetchNearbyRestaurants.fulfilled, (state, action) => {
        state.isLoading = false;
        state.nearbyRestaurants = action.payload.content;
      })
      .addCase(fetchNearbyRestaurants.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // Featured Restaurants
    builder
      .addCase(fetchFeaturedRestaurants.fulfilled, (state, action) => {
        state.featuredRestaurants = action.payload;
      });

    // Popular Restaurants
    builder
      .addCase(fetchPopularRestaurants.fulfilled, (state, action) => {
        state.popularRestaurants = action.payload;
      });

    // Fetch Restaurant by ID
    builder
      .addCase(fetchRestaurantById.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchRestaurantById.fulfilled, (state, action) => {
        state.isLoading = false;
        state.currentRestaurant = action.payload;
      })
      .addCase(fetchRestaurantById.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // Cuisines
    builder
      .addCase(fetchCuisines.fulfilled, (state, action) => {
        state.cuisines = action.payload;
      });

    // My Restaurants
    builder
      .addCase(fetchMyRestaurants.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(fetchMyRestaurants.fulfilled, (state, action) => {
        state.isLoading = false;
        state.myRestaurants = action.payload;
        if (action.payload.length > 0 && !state.selectedRestaurant) {
          state.selectedRestaurant = action.payload[0];
        }
      })
      .addCase(fetchMyRestaurants.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // Register Restaurant
    builder
      .addCase(registerRestaurant.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(registerRestaurant.fulfilled, (state, action) => {
        state.isLoading = false;
        state.myRestaurants.push(action.payload);
        state.selectedRestaurant = action.payload;
      })
      .addCase(registerRestaurant.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // Update Restaurant
    builder
      .addCase(updateRestaurant.fulfilled, (state, action) => {
        const index = state.myRestaurants.findIndex((r) => r.id === action.payload.id);
        if (index !== -1) {
          state.myRestaurants[index] = action.payload;
        }
        if (state.selectedRestaurant?.id === action.payload.id) {
          state.selectedRestaurant = action.payload;
        }
      });

    // Dashboard Stats
    builder
      .addCase(fetchDashboardStats.fulfilled, (state, action) => {
        state.dashboardStats = action.payload;
      });
  },
});

export const {
  clearError,
  setActiveFilters,
  setSearchQuery,
  clearCurrentRestaurant,
  setSelectedRestaurant,
  clearRestaurants,
} = restaurantSlice.actions;

export default restaurantSlice.reducer;

