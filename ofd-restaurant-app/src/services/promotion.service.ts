/**
 * Promotion Service - API calls for promotions, coupons, and campaigns
 */

import { promotionApi } from './api.config';
import type {
  ApiResponse,
  PaginatedResponse,
  Promotion,
  PromotionStatus,
  PromotionType,
  Coupon,
  ValidateCouponRequest,
  CouponValidationResponse,
  ApplyCouponRequest,
} from '@/types/api.types';

// Additional types for promotion service
interface CreatePromotionRequest {
  name: string;
  description: string;
  type: PromotionType;
  discountValue: number;
  minOrderValue?: number;
  maxDiscount?: number;
  startDate: string;
  endDate: string;
  usageLimit?: number;
  restaurantId?: string;
  applicableCategories?: string[];
  applicableItems?: string[];
  terms?: string;
  bannerUrl?: string;
  platformWide?: boolean;
}

interface CreateCouponRequest {
  code: string;
  name: string;
  description: string;
  type: PromotionType;
  value: number;
  minOrderValue?: number;
  maxDiscount?: number;
  validFrom: string;
  validUntil: string;
  totalLimit?: number;
  perUserLimit?: number;
  restaurantId?: string;
  firstOrderOnly?: boolean;
  newUsersOnly?: boolean;
}

interface Campaign {
  id: string;
  name: string;
  description: string;
  type: string;
  status: string;
  targetSegmentIds: string[];
  linkedPromotionIds: string[];
  linkedCouponIds: string[];
  content: CampaignContent;
  schedule: CampaignSchedule;
  performance: CampaignPerformance;
  createdAt: string;
  updatedAt: string;
}

interface CampaignContent {
  title: string;
  body: string;
  imageUrl?: string;
  ctaText?: string;
  ctaLink?: string;
}

interface CampaignSchedule {
  startDate: string;
  endDate?: string;
  frequency: string;
  sendTime?: string;
}

interface CampaignPerformance {
  sent: number;
  delivered: number;
  opened: number;
  clicked: number;
  converted: number;
}

interface CustomerSegment {
  id: string;
  name: string;
  description: string;
  criteria: SegmentCriteria;
  customerCount: number;
  isDynamic: boolean;
  isActive: boolean;
  restaurantId?: string;
  createdAt: string;
  updatedAt: string;
}

interface SegmentCriteria {
  minOrders?: number;
  maxOrders?: number;
  minSpend?: number;
  maxSpend?: number;
  lastOrderDaysAgo?: number;
  cuisinePreferences?: string[];
  loyaltyTiers?: string[];
}

export const promotionService = {
  // ============================================
  // Promotions (Consumer APIs)
  // ============================================

  /**
   * Get active promotions for consumer
   */
  getActivePromotions: async (
    restaurantId?: string,
    page = 0,
    size = 20
  ): Promise<ApiResponse<PaginatedResponse<Promotion>>> => {
    const response = await promotionApi.get('/promotions', {
      params: { status: 'ACTIVE', restaurantId, page, size },
    });
    return response.data;
  },

  /**
   * Get promotion by ID
   */
  getPromotionById: async (promotionId: string): Promise<ApiResponse<Promotion>> => {
    const response = await promotionApi.get(`/promotions/${promotionId}`);
    return response.data;
  },

  /**
   * Get promotion by code
   */
  getPromotionByCode: async (code: string): Promise<ApiResponse<Promotion>> => {
    const response = await promotionApi.get(`/promotions/code/${code}`);
    return response.data;
  },

  /**
   * Get featured promotions
   */
  getFeaturedPromotions: async (limit = 5): Promise<ApiResponse<Promotion[]>> => {
    const response = await promotionApi.get('/promotions/featured', { params: { limit } });
    return response.data;
  },

  /**
   * Get promotions for restaurant
   */
  getRestaurantPromotions: async (restaurantId: string): Promise<ApiResponse<Promotion[]>> => {
    const response = await promotionApi.get(`/promotions/restaurant/${restaurantId}/active`);
    return response.data;
  },

  // ============================================
  // Promotions (Restaurant Owner APIs)
  // ============================================

  /**
   * Get all promotions for restaurant owner
   */
  getMyPromotions: async (
    restaurantId: string,
    status?: PromotionStatus,
    page = 0,
    size = 20
  ): Promise<ApiResponse<PaginatedResponse<Promotion>>> => {
    const response = await promotionApi.get(`/promotions/restaurant/${restaurantId}`, {
      params: { status, page, size },
    });
    return response.data;
  },

  /**
   * Create new promotion
   */
  createPromotion: async (data: CreatePromotionRequest): Promise<ApiResponse<Promotion>> => {
    const response = await promotionApi.post('/promotions', data);
    return response.data;
  },

  /**
   * Update promotion
   */
  updatePromotion: async (
    promotionId: string,
    data: Partial<CreatePromotionRequest>
  ): Promise<ApiResponse<Promotion>> => {
    const response = await promotionApi.put(`/promotions/${promotionId}`, data);
    return response.data;
  },

  /**
   * Activate promotion
   */
  activatePromotion: async (promotionId: string): Promise<ApiResponse<Promotion>> => {
    const response = await promotionApi.put(`/promotions/${promotionId}/activate`);
    return response.data;
  },

  /**
   * Pause promotion
   */
  pausePromotion: async (promotionId: string): Promise<ApiResponse<Promotion>> => {
    const response = await promotionApi.put(`/promotions/${promotionId}/pause`);
    return response.data;
  },

  /**
   * Resume promotion
   */
  resumePromotion: async (promotionId: string): Promise<ApiResponse<Promotion>> => {
    const response = await promotionApi.put(`/promotions/${promotionId}/resume`);
    return response.data;
  },

  /**
   * End promotion
   */
  endPromotion: async (promotionId: string): Promise<ApiResponse<Promotion>> => {
    const response = await promotionApi.put(`/promotions/${promotionId}/end`);
    return response.data;
  },

  /**
   * Delete promotion
   */
  deletePromotion: async (promotionId: string): Promise<ApiResponse<void>> => {
    const response = await promotionApi.delete(`/promotions/${promotionId}`);
    return response.data;
  },

  // ============================================
  // Coupons (Consumer APIs)
  // ============================================

  /**
   * Get available coupons for user
   */
  getAvailableCoupons: async (restaurantId?: string): Promise<ApiResponse<Coupon[]>> => {
    const params = restaurantId 
      ? { restaurantId } 
      : undefined;
    const response = await promotionApi.get('/coupons/platform', { params });
    return response.data;
  },

  /**
   * Get coupon by code
   */
  getCouponByCode: async (code: string): Promise<ApiResponse<Coupon>> => {
    const response = await promotionApi.get(`/coupons/code/${code}`);
    return response.data;
  },

  /**
   * Validate coupon
   */
  validateCoupon: async (data: ValidateCouponRequest): Promise<ApiResponse<CouponValidationResponse>> => {
    const response = await promotionApi.post('/coupons/validate', data);
    return response.data;
  },

  /**
   * Apply coupon to order
   */
  applyCoupon: async (data: ApplyCouponRequest): Promise<ApiResponse<CouponValidationResponse>> => {
    const response = await promotionApi.post('/coupons/apply', data);
    return response.data;
  },

  // ============================================
  // Coupons (Restaurant Owner APIs)
  // ============================================

  /**
   * Get restaurant coupons
   */
  getRestaurantCoupons: async (
    restaurantId: string,
    page = 0,
    size = 20
  ): Promise<ApiResponse<PaginatedResponse<Coupon>>> => {
    const response = await promotionApi.get(`/coupons/restaurant/${restaurantId}`, {
      params: { page, size },
    });
    return response.data;
  },

  /**
   * Get coupon by ID
   */
  getCouponById: async (couponId: string): Promise<ApiResponse<Coupon>> => {
    const response = await promotionApi.get(`/coupons/${couponId}`);
    return response.data;
  },

  /**
   * Create coupon
   */
  createCoupon: async (data: CreateCouponRequest): Promise<ApiResponse<Coupon>> => {
    const response = await promotionApi.post('/coupons', data);
    return response.data;
  },

  /**
   * Update coupon
   */
  updateCoupon: async (
    couponId: string,
    data: Partial<CreateCouponRequest>
  ): Promise<ApiResponse<Coupon>> => {
    const response = await promotionApi.put(`/coupons/${couponId}`, data);
    return response.data;
  },

  /**
   * Activate coupon
   */
  activateCoupon: async (couponId: string): Promise<ApiResponse<Coupon>> => {
    const response = await promotionApi.put(`/coupons/${couponId}/activate`);
    return response.data;
  },

  /**
   * Deactivate coupon
   */
  deactivateCoupon: async (couponId: string): Promise<ApiResponse<Coupon>> => {
    const response = await promotionApi.put(`/coupons/${couponId}/deactivate`);
    return response.data;
  },

  /**
   * Delete coupon
   */
  deleteCoupon: async (couponId: string): Promise<ApiResponse<void>> => {
    const response = await promotionApi.delete(`/coupons/${couponId}`);
    return response.data;
  },

  // ============================================
  // Campaigns (Restaurant Owner APIs)
  // ============================================

  /**
   * Get campaigns
   */
  getCampaigns: async (
    restaurantId: string,
    status?: string,
    page = 0,
    size = 20
  ): Promise<ApiResponse<PaginatedResponse<Campaign>>> => {
    const response = await promotionApi.get(`/campaigns`, {
      params: { restaurantId, status, page, size },
    });
    return response.data;
  },

  /**
   * Get campaign by ID
   */
  getCampaignById: async (campaignId: string): Promise<ApiResponse<Campaign>> => {
    const response = await promotionApi.get(`/campaigns/${campaignId}`);
    return response.data;
  },

  /**
   * Create campaign
   */
  createCampaign: async (data: Partial<Campaign>): Promise<ApiResponse<Campaign>> => {
    const response = await promotionApi.post('/campaigns', data);
    return response.data;
  },

  /**
   * Update campaign
   */
  updateCampaign: async (
    campaignId: string,
    data: Partial<Campaign>
  ): Promise<ApiResponse<Campaign>> => {
    const response = await promotionApi.put(`/campaigns/${campaignId}`, data);
    return response.data;
  },

  /**
   * Activate campaign
   */
  activateCampaign: async (campaignId: string): Promise<ApiResponse<Campaign>> => {
    const response = await promotionApi.put(`/campaigns/${campaignId}/activate`);
    return response.data;
  },

  /**
   * Pause campaign
   */
  pauseCampaign: async (campaignId: string): Promise<ApiResponse<Campaign>> => {
    const response = await promotionApi.put(`/campaigns/${campaignId}/pause`);
    return response.data;
  },

  /**
   * Delete campaign
   */
  deleteCampaign: async (campaignId: string): Promise<ApiResponse<void>> => {
    const response = await promotionApi.delete(`/campaigns/${campaignId}`);
    return response.data;
  },

  /**
   * Get campaign performance
   */
  getCampaignPerformance: async (campaignId: string): Promise<ApiResponse<CampaignPerformance>> => {
    const response = await promotionApi.get(`/campaigns/${campaignId}/performance`);
    return response.data;
  },

  // ============================================
  // Customer Segments (Restaurant Owner APIs)
  // ============================================

  /**
   * Get customer segments
   */
  getSegments: async (
    restaurantId: string,
    page = 0,
    size = 20
  ): Promise<ApiResponse<PaginatedResponse<CustomerSegment>>> => {
    const response = await promotionApi.get('/segments', {
      params: { restaurantId, page, size },
    });
    return response.data;
  },

  /**
   * Get segment by ID
   */
  getSegmentById: async (segmentId: string): Promise<ApiResponse<CustomerSegment>> => {
    const response = await promotionApi.get(`/segments/${segmentId}`);
    return response.data;
  },

  /**
   * Create segment
   */
  createSegment: async (data: Partial<CustomerSegment>): Promise<ApiResponse<CustomerSegment>> => {
    const response = await promotionApi.post('/segments', data);
    return response.data;
  },

  /**
   * Update segment
   */
  updateSegment: async (
    segmentId: string,
    data: Partial<CustomerSegment>
  ): Promise<ApiResponse<CustomerSegment>> => {
    const response = await promotionApi.put(`/segments/${segmentId}`, data);
    return response.data;
  },

  /**
   * Delete segment
   */
  deleteSegment: async (segmentId: string): Promise<ApiResponse<void>> => {
    const response = await promotionApi.delete(`/segments/${segmentId}`);
    return response.data;
  },

  /**
   * Refresh segment customer count
   */
  refreshSegment: async (segmentId: string): Promise<ApiResponse<CustomerSegment>> => {
    const response = await promotionApi.post(`/segments/${segmentId}/refresh`);
    return response.data;
  },
};

export default promotionService;

