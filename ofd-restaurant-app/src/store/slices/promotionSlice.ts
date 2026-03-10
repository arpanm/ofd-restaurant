/**
 * Promotion Slice - Promotion and coupon state management
 */

import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { promotionService } from '@/services';
import type {
  Promotion,
  PromotionStatus,
  Coupon,
  ValidateCouponRequest,
  CouponValidationResponse,
} from '@/types/api.types';

interface PromotionState {
  // Consumer view
  activePromotions: Promotion[];
  featuredPromotions: Promotion[];
  availableCoupons: Coupon[];
  validatedCoupon: CouponValidationResponse | null;
  
  // Restaurant owner view
  myPromotions: Promotion[];
  myCoupons: Coupon[];
  campaigns: any[];
  segments: any[];
  
  // Pagination
  totalElements: number;
  totalPages: number;
  currentPage: number;
  
  // Loading states
  isLoading: boolean;
  isSaving: boolean;
  isValidating: boolean;
  error: string | null;
}

const initialState: PromotionState = {
  activePromotions: [],
  featuredPromotions: [],
  availableCoupons: [],
  validatedCoupon: null,
  myPromotions: [],
  myCoupons: [],
  campaigns: [],
  segments: [],
  totalElements: 0,
  totalPages: 0,
  currentPage: 0,
  isLoading: false,
  isSaving: false,
  isValidating: false,
  error: null,
};

// Consumer Thunks
export const fetchActivePromotions = createAsyncThunk(
  'promotion/fetchActivePromotions',
  async (restaurantId?: string, { rejectWithValue }) => {
    try {
      const response = await promotionService.getActivePromotions(restaurantId, 0, 50);
      if (response.success) {
        return response.data.content;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch promotions');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch promotions');
    }
  }
);

export const fetchFeaturedPromotions = createAsyncThunk(
  'promotion/fetchFeaturedPromotions',
  async (_, { rejectWithValue }) => {
    try {
      const response = await promotionService.getFeaturedPromotions(5);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch featured promotions');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch featured promotions');
    }
  }
);

export const fetchAvailableCoupons = createAsyncThunk(
  'promotion/fetchAvailableCoupons',
  async (restaurantId?: string, { rejectWithValue }) => {
    try {
      const response = await promotionService.getAvailableCoupons(restaurantId);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch coupons');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch coupons');
    }
  }
);

export const validateCoupon = createAsyncThunk(
  'promotion/validateCoupon',
  async (data: ValidateCouponRequest, { rejectWithValue }) => {
    try {
      const response = await promotionService.validateCoupon(data);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Coupon validation failed');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Coupon validation failed');
    }
  }
);

// Restaurant Owner Thunks
export const fetchMyPromotions = createAsyncThunk(
  'promotion/fetchMyPromotions',
  async (
    { restaurantId, status, page = 0 }: { restaurantId: string; status?: PromotionStatus; page?: number },
    { rejectWithValue }
  ) => {
    try {
      const response = await promotionService.getMyPromotions(restaurantId, status, page, 20);
      if (response.success) {
        return { data: response.data, isLoadMore: page > 0 };
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch promotions');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch promotions');
    }
  }
);

export const createPromotion = createAsyncThunk(
  'promotion/createPromotion',
  async (data: any, { rejectWithValue }) => {
    try {
      const response = await promotionService.createPromotion(data);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to create promotion');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to create promotion');
    }
  }
);

export const updatePromotion = createAsyncThunk(
  'promotion/updatePromotion',
  async ({ promotionId, data }: { promotionId: string; data: any }, { rejectWithValue }) => {
    try {
      const response = await promotionService.updatePromotion(promotionId, data);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to update promotion');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to update promotion');
    }
  }
);

export const activatePromotion = createAsyncThunk(
  'promotion/activatePromotion',
  async (promotionId: string, { rejectWithValue }) => {
    try {
      const response = await promotionService.activatePromotion(promotionId);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to activate promotion');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to activate promotion');
    }
  }
);

export const pausePromotion = createAsyncThunk(
  'promotion/pausePromotion',
  async (promotionId: string, { rejectWithValue }) => {
    try {
      const response = await promotionService.pausePromotion(promotionId);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to pause promotion');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to pause promotion');
    }
  }
);

export const deletePromotion = createAsyncThunk(
  'promotion/deletePromotion',
  async (promotionId: string, { rejectWithValue }) => {
    try {
      await promotionService.deletePromotion(promotionId);
      return promotionId;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to delete promotion');
    }
  }
);

// Coupon Management
export const fetchMyCoupons = createAsyncThunk(
  'promotion/fetchMyCoupons',
  async ({ restaurantId, page = 0 }: { restaurantId: string; page?: number }, { rejectWithValue }) => {
    try {
      const response = await promotionService.getRestaurantCoupons(restaurantId, page, 20);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch coupons');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch coupons');
    }
  }
);

export const createCoupon = createAsyncThunk(
  'promotion/createCoupon',
  async (data: any, { rejectWithValue }) => {
    try {
      const response = await promotionService.createCoupon(data);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to create coupon');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to create coupon');
    }
  }
);

export const updateCoupon = createAsyncThunk(
  'promotion/updateCoupon',
  async ({ couponId, data }: { couponId: string; data: any }, { rejectWithValue }) => {
    try {
      const response = await promotionService.updateCoupon(couponId, data);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to update coupon');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to update coupon');
    }
  }
);

export const deleteCoupon = createAsyncThunk(
  'promotion/deleteCoupon',
  async (couponId: string, { rejectWithValue }) => {
    try {
      await promotionService.deleteCoupon(couponId);
      return couponId;
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to delete coupon');
    }
  }
);

// Campaign Management
export const fetchCampaigns = createAsyncThunk(
  'promotion/fetchCampaigns',
  async ({ restaurantId, page = 0 }: { restaurantId: string; page?: number }, { rejectWithValue }) => {
    try {
      const response = await promotionService.getCampaigns(restaurantId, undefined, page, 20);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch campaigns');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch campaigns');
    }
  }
);

export const createCampaign = createAsyncThunk(
  'promotion/createCampaign',
  async (data: any, { rejectWithValue }) => {
    try {
      const response = await promotionService.createCampaign(data);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to create campaign');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to create campaign');
    }
  }
);

// Segment Management
export const fetchSegments = createAsyncThunk(
  'promotion/fetchSegments',
  async ({ restaurantId, page = 0 }: { restaurantId: string; page?: number }, { rejectWithValue }) => {
    try {
      const response = await promotionService.getSegments(restaurantId, page, 20);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to fetch segments');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to fetch segments');
    }
  }
);

export const createSegment = createAsyncThunk(
  'promotion/createSegment',
  async (data: any, { rejectWithValue }) => {
    try {
      const response = await promotionService.createSegment(data);
      if (response.success) {
        return response.data;
      }
      return rejectWithValue(response.error?.message || 'Failed to create segment');
    } catch (error: any) {
      return rejectWithValue(error.response?.data?.error?.message || 'Failed to create segment');
    }
  }
);

const promotionSlice = createSlice({
  name: 'promotion',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
    clearValidatedCoupon: (state) => {
      state.validatedCoupon = null;
    },
  },
  extraReducers: (builder) => {
    // Active Promotions
    builder
      .addCase(fetchActivePromotions.pending, (state) => {
        state.isLoading = true;
        state.error = null;
      })
      .addCase(fetchActivePromotions.fulfilled, (state, action) => {
        state.isLoading = false;
        state.activePromotions = action.payload;
      })
      .addCase(fetchActivePromotions.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // Featured Promotions
    builder
      .addCase(fetchFeaturedPromotions.fulfilled, (state, action) => {
        state.featuredPromotions = action.payload;
      });

    // Available Coupons
    builder
      .addCase(fetchAvailableCoupons.fulfilled, (state, action) => {
        state.availableCoupons = action.payload;
      });

    // Validate Coupon
    builder
      .addCase(validateCoupon.pending, (state) => {
        state.isValidating = true;
        state.error = null;
      })
      .addCase(validateCoupon.fulfilled, (state, action) => {
        state.isValidating = false;
        state.validatedCoupon = action.payload;
      })
      .addCase(validateCoupon.rejected, (state, action) => {
        state.isValidating = false;
        state.error = action.payload as string;
        state.validatedCoupon = null;
      });

    // My Promotions
    builder
      .addCase(fetchMyPromotions.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(fetchMyPromotions.fulfilled, (state, action) => {
        state.isLoading = false;
        if (action.payload.isLoadMore) {
          state.myPromotions = [...state.myPromotions, ...action.payload.data.content];
        } else {
          state.myPromotions = action.payload.data.content;
        }
        state.totalElements = action.payload.data.totalElements;
        state.totalPages = action.payload.data.totalPages;
        state.currentPage = action.payload.data.number;
      })
      .addCase(fetchMyPromotions.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload as string;
      });

    // Create Promotion
    builder
      .addCase(createPromotion.pending, (state) => {
        state.isSaving = true;
        state.error = null;
      })
      .addCase(createPromotion.fulfilled, (state, action) => {
        state.isSaving = false;
        state.myPromotions.unshift(action.payload);
      })
      .addCase(createPromotion.rejected, (state, action) => {
        state.isSaving = false;
        state.error = action.payload as string;
      });

    // Update Promotion
    builder
      .addCase(updatePromotion.fulfilled, (state, action) => {
        const index = state.myPromotions.findIndex((p) => p.id === action.payload.id);
        if (index !== -1) {
          state.myPromotions[index] = action.payload;
        }
      });

    // Activate/Pause Promotion
    builder
      .addCase(activatePromotion.fulfilled, (state, action) => {
        const index = state.myPromotions.findIndex((p) => p.id === action.payload.id);
        if (index !== -1) {
          state.myPromotions[index] = action.payload;
        }
      })
      .addCase(pausePromotion.fulfilled, (state, action) => {
        const index = state.myPromotions.findIndex((p) => p.id === action.payload.id);
        if (index !== -1) {
          state.myPromotions[index] = action.payload;
        }
      });

    // Delete Promotion
    builder
      .addCase(deletePromotion.fulfilled, (state, action) => {
        state.myPromotions = state.myPromotions.filter((p) => p.id !== action.payload);
      });

    // My Coupons
    builder
      .addCase(fetchMyCoupons.fulfilled, (state, action) => {
        state.myCoupons = action.payload.content;
      });

    // Create Coupon
    builder
      .addCase(createCoupon.pending, (state) => {
        state.isSaving = true;
      })
      .addCase(createCoupon.fulfilled, (state, action) => {
        state.isSaving = false;
        state.myCoupons.unshift(action.payload);
      })
      .addCase(createCoupon.rejected, (state, action) => {
        state.isSaving = false;
        state.error = action.payload as string;
      });

    // Update Coupon
    builder
      .addCase(updateCoupon.fulfilled, (state, action) => {
        const index = state.myCoupons.findIndex((c) => c.id === action.payload.id);
        if (index !== -1) {
          state.myCoupons[index] = action.payload;
        }
      });

    // Delete Coupon
    builder
      .addCase(deleteCoupon.fulfilled, (state, action) => {
        state.myCoupons = state.myCoupons.filter((c) => c.id !== action.payload);
      });

    // Campaigns
    builder
      .addCase(fetchCampaigns.fulfilled, (state, action) => {
        state.campaigns = action.payload.content;
      })
      .addCase(createCampaign.fulfilled, (state, action) => {
        state.campaigns.unshift(action.payload);
      });

    // Segments
    builder
      .addCase(fetchSegments.fulfilled, (state, action) => {
        state.segments = action.payload.content;
      })
      .addCase(createSegment.fulfilled, (state, action) => {
        state.segments.unshift(action.payload);
      });
  },
});

export const { clearError, clearValidatedCoupon } = promotionSlice.actions;
export default promotionSlice.reducer;

