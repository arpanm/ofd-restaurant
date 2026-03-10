/**
 * Auth Slice - Authentication state management
 */

import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { userService, tokenManager } from '@/services';
import type {
  User,
  LoginRequest,
  RegisterUserRequest,
  UpdateUserRequest,
  Address,
  UserPreferences,
  LoyaltyProfile,
} from '@/types/api.types';

interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  addresses: Address[];
  preferences: UserPreferences | null;
  loyaltyProfile: LoyaltyProfile | null;
  favoriteRestaurants: string[];
  favoriteItems: string[];
}

const initialState: AuthState = {
  user: null,
  isAuthenticated: false,
  isLoading: false,
  error: null,
  addresses: [],
  preferences: null,
  loyaltyProfile: null,
  favoriteRestaurants: [],
  favoriteItems: [],
};

// Async Thunks
export const login = createAsyncThunk(
  'auth/login',
  async (credentials: LoginRequest, { rejectWithValue }) => {
    try {
      const response = await userService.login(credentials);
      if (response.success) {
        tokenManager.setTokens(response.data.accessToken, response.data.refreshToken);
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Login failed');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Login failed');
    }
  }
);

export const register = createAsyncThunk(
  'auth/register',
  async (data: RegisterUserRequest, { rejectWithValue }) => {
    try {
      const response = await userService.register(data);
      if (response.success) {
        tokenManager.setTokens(response.data.accessToken, response.data.refreshToken);
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Registration failed');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Registration failed');
    }
  }
);

export const logout = createAsyncThunk(
  'auth/logout',
  async (_, { rejectWithValue }) => {
    try {
      await userService.logout();
      tokenManager.clearTokens();
    } catch (error: any) {
      tokenManager.clearTokens();
      return rejectWithValue(error.response?.data?.error?.message || 'Logout failed');
    }
  }
);

export const getCurrentUser = createAsyncThunk(
  'auth/getCurrentUser',
  async (_, { rejectWithValue }) => {
    try {
      const response = await userService.getCurrentUser();
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to get user');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to get user');
    }
  }
);

export const updateProfile = createAsyncThunk(
  'auth/updateProfile',
  async (data: UpdateUserRequest, { rejectWithValue }) => {
    try {
      const response = await userService.updateProfile(data);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Update failed');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Update failed');
    }
  }
);

export const fetchAddresses = createAsyncThunk(
  'auth/fetchAddresses',
  async (_, { rejectWithValue }) => {
    try {
      const response = await userService.getAddresses();
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch addresses');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch addresses');
    }
  }
);

export const addAddress = createAsyncThunk(
  'auth/addAddress',
  async (address: Omit<Address, 'id'>, { rejectWithValue }) => {
    try {
      const response = await userService.addAddress(address);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to add address');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to add address');
    }
  }
);

export const updateAddress = createAsyncThunk(
  'auth/updateAddress',
  async ({ addressId, data }: { addressId: string; data: Partial<Address> }, { rejectWithValue }) => {
    try {
      const response = await userService.updateAddress(addressId, data);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to update address');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to update address');
    }
  }
);

export const deleteAddress = createAsyncThunk(
  'auth/deleteAddress',
  async (addressId: string, { rejectWithValue }) => {
    try {
      await userService.deleteAddress(addressId);
      return addressId;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to delete address');
    }
  }
);

export const setDefaultAddress = createAsyncThunk(
  'auth/setDefaultAddress',
  async (addressId: string, { rejectWithValue }) => {
    try {
      const response = await userService.setDefaultAddress(addressId);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to set default address');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to set default address');
    }
  }
);

export const fetchLoyaltyProfile = createAsyncThunk(
  'auth/fetchLoyaltyProfile',
  async (_, { rejectWithValue }) => {
    try {
      const response = await userService.getLoyaltyProfile();
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch loyalty profile');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch loyalty profile');
    }
  }
);

export const fetchFavorites = createAsyncThunk(
  'auth/fetchFavorites',
  async (_, { rejectWithValue }) => {
    try {
      const [restaurantsRes, itemsRes] = await Promise.all([
        userService.getFavoriteRestaurants(),
        userService.getFavoriteItems(),
      ]);
      return {
        restaurants: restaurantsRes.data,
        items: itemsRes.data,
      };
    } catch (error: any) {
      return rejectWithValue('Failed to fetch favorites');
    }
  }
);

export const toggleFavoriteRestaurant = createAsyncThunk(
  'auth/toggleFavoriteRestaurant',
  async (restaurantId: string, { getState, rejectWithValue }) => {
    try {
      const state = getState() as { auth: AuthState };
      const isFavorite = state.auth.favoriteRestaurants.includes(restaurantId);
      
      if (isFavorite) {
        await userService.removeFavoriteRestaurant(restaurantId);
      } else {
        await userService.addFavoriteRestaurant(restaurantId);
      }
      
      return { restaurantId, isFavorite: !isFavorite };
    } catch (error: any) {
      return rejectWithValue('Failed to update favorites');
    }
  }
);

export const toggleFavoriteItem = createAsyncThunk(
  'auth/toggleFavoriteItem',
  async (itemId: string, { getState, rejectWithValue }) => {
    try {
      const state = getState() as { auth: AuthState };
      const isFavorite = state.auth.favoriteItems.includes(itemId);
      
      if (isFavorite) {
        await userService.removeFavoriteItem(itemId);
      } else {
        await userService.addFavoriteItem(itemId);
      }
      
      return { itemId, isFavorite: !isFavorite };
    } catch (error: any) {
      return rejectWithValue('Failed to update favorites');
    }
  }
);

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    setUser: (state, action: PayloadAction<User>) => {
      state.user = action.payload;
      state.isAuthenticated = true;
    },
    clearAuth: (state) => {
      state.user = null;
      state.isAuthenticated = false;
      state.addresses = [];
      state.preferences = null;
      state.loyaltyProfile = null;
      state.favoriteRestaurants = [];
      state.favoriteItems = [];
      tokenManager.clearTokens();
    },
  },
  extraReducers: (builder) => {
    // Login
    builder
      .addCase(login.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(login.fulfilled, (state, action) => {
        state.isLoading = false;
        state.user = action.payload.user;
        state.isAuthenticated = true;
      })
      .addCase(login.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // Register
    builder
      .addCase(register.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(register.fulfilled, (state, action) => {
        state.isLoading = false;
        state.user = action.payload.user;
        state.isAuthenticated = true;
      })
      .addCase(register.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // Logout
    builder
      .addCase(logout.fulfilled, (state) => {
        state.user = null;
        state.isAuthenticated = false;
        state.addresses = [];
        state.preferences = null;
        state.loyaltyProfile = null;
        state.favoriteRestaurants = [];
        state.favoriteItems = [];
      });

    // Get Current User
    builder
      .addCase(getCurrentUser.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(getCurrentUser.fulfilled, (state, action) => {
        state.isLoading = false;
        state.user = action.payload;
        state.isAuthenticated = true;
      })
      .addCase(getCurrentUser.rejected, (state) => {
        state.isLoading = false;
        state.isAuthenticated = false;
      });

    // Update Profile
    builder
      .addCase(updateProfile.fulfilled, (state, action) => {
        state.user = action.payload;
      });

    // Addresses
    builder
      .addCase(fetchAddresses.fulfilled, (state, action) => {
        state.addresses = action.payload;
      })
      .addCase(addAddress.fulfilled, (state, action) => {
        state.addresses.push(action.payload);
      })
      .addCase(updateAddress.fulfilled, (state, action) => {
        const index = state.addresses.findIndex((a) => a.id === action.payload.id);
        if (index !== -1) {
          state.addresses[index] = action.payload;
        }
      })
      .addCase(deleteAddress.fulfilled, (state, action) => {
        state.addresses = state.addresses.filter((a) => a.id !== action.payload);
      })
      .addCase(setDefaultAddress.fulfilled, (state, action) => {
        state.addresses = state.addresses.map((a) => ({
          ...a,
          isDefault: a.id === action.payload.id,
        }));
      });

    // Loyalty
    builder
      .addCase(fetchLoyaltyProfile.fulfilled, (state, action) => {
        state.loyaltyProfile = action.payload;
      });

    // Favorites
    builder
      .addCase(fetchFavorites.fulfilled, (state, action) => {
        state.favoriteRestaurants = action.payload.restaurants;
        state.favoriteItems = action.payload.items;
      })
      .addCase(toggleFavoriteRestaurant.fulfilled, (state, action) => {
        if (action.payload.isFavorite) {
          state.favoriteRestaurants.push(action.payload.restaurantId);
        } else {
          state.favoriteRestaurants = state.favoriteRestaurants.filter(
            (id) => id !== action.payload.restaurantId
          );
        }
      })
      .addCase(toggleFavoriteItem.fulfilled, (state, action) => {
        if (action.payload.isFavorite) {
          state.favoriteItems.push(action.payload.itemId);
        } else {
          state.favoriteItems = state.favoriteItems.filter(
            (id) => id !== action.payload.itemId
          );
        }
      });
  },
});

export const { clearError, setUser, clearAuth } = authSlice.actions;
export default authSlice.reducer;

