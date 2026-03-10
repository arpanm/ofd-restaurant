/**
 * API Types and Interfaces for FoodAI Platform
 * Defines contracts between frontend and backend services
 */

// ============================================
// Common Types
// ============================================

export interface ApiResponse<T> {
  success: boolean;
  data: T;
  error?: ErrorDetails;
  timestamp: string;
}

export interface ErrorDetails {
  code: string;
  message: string;
  details?: unknown;
}

export interface PaginatedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

export interface PageRequest {
  page?: number;
  size?: number;
  sort?: string;
}

// ============================================
// User Service Types
// ============================================

export interface User {
  id: string;
  email: string;
  phone: string;
  firstName: string;
  lastName: string;
  displayName: string;
  profileImageUrl?: string;
  status: UserStatus;
  role: UserRole;
  preferences: UserPreferences;
  loyaltyProfile: LoyaltyProfile;
  addresses: Address[];
  createdAt: string;
  updatedAt: string;
}

export enum UserStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED',
  PENDING_VERIFICATION = 'PENDING_VERIFICATION',
}

export enum UserRole {
  CUSTOMER = 'CUSTOMER',
  RESTAURANT_OWNER = 'RESTAURANT_OWNER',
  RESTAURANT_STAFF = 'RESTAURANT_STAFF',
  DELIVERY_PARTNER = 'DELIVERY_PARTNER',
  ADMIN = 'ADMIN',
  SUPER_ADMIN = 'SUPER_ADMIN',
}

export interface UserPreferences {
  dietaryRestrictions: string[];
  cuisinePreferences: string[];
  spiceLevelPreference: SpiceLevel;
  notificationSettings: NotificationSettings;
  language: string;
  currency: string;
}

export enum SpiceLevel {
  NONE = 'NONE',
  MILD = 'MILD',
  MEDIUM = 'MEDIUM',
  HOT = 'HOT',
  EXTRA_HOT = 'EXTRA_HOT',
}

export interface NotificationSettings {
  orderUpdates: boolean;
  promotions: boolean;
  recommendations: boolean;
  email: boolean;
  sms: boolean;
  push: boolean;
}

export interface LoyaltyProfile {
  tier: LoyaltyTier;
  points: number;
  lifetimePoints: number;
  achievements: Achievement[];
  streaks: StreakInfo;
}

export enum LoyaltyTier {
  BRONZE = 'BRONZE',
  SILVER = 'SILVER',
  GOLD = 'GOLD',
  PLATINUM = 'PLATINUM',
}

export interface Achievement {
  id: string;
  name: string;
  description: string;
  iconUrl: string;
  unlockedAt: string;
}

export interface StreakInfo {
  currentStreak: number;
  longestStreak: number;
  lastOrderDate: string;
}

export interface Address {
  id: string;
  label: string;
  type: AddressType;
  street: string;
  apartment?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  latitude: number;
  longitude: number;
  isDefault: boolean;
  deliveryInstructions?: string;
}

export enum AddressType {
  HOME = 'HOME',
  WORK = 'WORK',
  OTHER = 'OTHER',
}

export interface RegisterUserRequest {
  email: string;
  phone: string;
  password: string;
  firstName: string;
  lastName: string;
  referralCode?: string;
}

export interface LoginRequest {
  email?: string;
  phone?: string;
  password: string;
}

export interface AuthResponse {
  user: User;
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

export interface UpdateUserRequest {
  firstName?: string;
  lastName?: string;
  displayName?: string;
  profileImageUrl?: string;
  preferences?: Partial<UserPreferences>;
}

// ============================================
// Restaurant Service Types
// ============================================

export interface Restaurant {
  id: string;
  name: string;
  description: string;
  cuisine: string[];
  rating: number;
  totalReviews: number;
  priceRange: PriceRange;
  address: RestaurantAddress;
  contact: RestaurantContact;
  operatingHours: OperatingHours[];
  images: RestaurantImages;
  features: RestaurantFeatures;
  status: RestaurantStatus;
  ownerId: string;
  createdAt: string;
  updatedAt: string;
}

export enum PriceRange {
  BUDGET = 'BUDGET',
  MODERATE = 'MODERATE',
  EXPENSIVE = 'EXPENSIVE',
  PREMIUM = 'PREMIUM',
}

export interface RestaurantAddress {
  street: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  latitude: number;
  longitude: number;
}

export interface RestaurantContact {
  phone: string;
  email: string;
  website?: string;
}

export interface OperatingHours {
  dayOfWeek: string;
  openTime: string;
  closeTime: string;
  isClosed: boolean;
}

export interface RestaurantImages {
  logo?: string;
  banner?: string;
  gallery: string[];
}

export interface RestaurantFeatures {
  delivery: boolean;
  pickup: boolean;
  dineIn: boolean;
  tableReservation: boolean;
  acceptsOnlinePayment: boolean;
  hasParking: boolean;
  isVegetarian: boolean;
  isVegan: boolean;
  isHalal: boolean;
  hasOutdoorSeating: boolean;
}

export enum RestaurantStatus {
  PENDING = 'PENDING',
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED',
  CLOSED = 'CLOSED',
}

export interface RegisterRestaurantRequest {
  name: string;
  description: string;
  cuisine: string[];
  priceRange: PriceRange;
  address: RestaurantAddress;
  contact: RestaurantContact;
  operatingHours: OperatingHours[];
  features: RestaurantFeatures;
  documents: RestaurantDocuments;
}

export interface RestaurantDocuments {
  fssaiLicense: string;
  gstNumber: string;
  panNumber: string;
  bankDetails: BankDetails;
}

export interface BankDetails {
  accountNumber: string;
  ifscCode: string;
  accountHolderName: string;
  bankName: string;
}

/** Request payload for POST /restaurants/onboarding (all 6 steps in one). */
export interface SubmitOnboardingRequest {
  name: string;
  description: string;
  cuisineTypes: string[];
  address: string;
  city: string;
  state: string;
  pincode: string;
  phone: string;
  email: string;
  ownerName: string;
  ownerPhone: string;
  ownerEmail: string;
  fssaiNumber: string;
  gstNumber?: string;
  panNumber?: string;
  fssaiDocumentUrl: string;
  gstDocumentUrl?: string;
  panDocumentUrl?: string;
  cancelledChequeDocumentUrl: string;
  bankName: string;
  accountNumber: string;
  ifscCode: string;
  accountHolderName: string;
  contractSignedBy: string;
  contractSignedAt: string; // ISO instant
  menuFileUrl?: string;
  createdBy: string;
}

/** Onboarding draft: save/resume by step. */
export type OnboardingDraftStatus = 'DRAFT' | 'SUBMITTED';

export interface OnboardingDraftResponse {
  draftId: string;
  currentStep: number;
  status: OnboardingDraftStatus;
  name?: string;
  description?: string;
  cuisineTypes?: string[];
  address?: string;
  city?: string;
  state?: string;
  pincode?: string;
  phone?: string;
  email?: string;
  ownerName?: string;
  ownerPhone?: string;
  ownerEmail?: string;
  fssaiNumber?: string;
  gstNumber?: string;
  panNumber?: string;
  fssaiDocumentUrl?: string;
  gstDocumentUrl?: string;
  panDocumentUrl?: string;
  cancelledChequeDocumentUrl?: string;
  bankName?: string;
  accountNumber?: string;
  ifscCode?: string;
  accountHolderName?: string;
  contractSigned?: boolean;
  contractSignedBy?: string;
  contractSignedAt?: string;
  signatureText?: string;
  menuFileUrl?: string;
  assistanceRequested?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

/** Partial save request for PATCH /restaurants/onboarding/draft/:draftId */
export interface SaveOnboardingDraftRequest {
  step?: number;
  name?: string;
  description?: string;
  cuisineTypes?: string[];
  address?: string;
  city?: string;
  state?: string;
  pincode?: string;
  phone?: string;
  email?: string;
  ownerName?: string;
  ownerPhone?: string;
  ownerEmail?: string;
  fssaiNumber?: string;
  gstNumber?: string;
  panNumber?: string;
  fssaiDocumentUrl?: string;
  gstDocumentUrl?: string;
  panDocumentUrl?: string;
  cancelledChequeDocumentUrl?: string;
  bankName?: string;
  accountNumber?: string;
  ifscCode?: string;
  accountHolderName?: string;
  contractSigned?: boolean;
  contractSignedBy?: string;
  contractSignedAt?: string;
  signatureText?: string;
  menuFileUrl?: string;
  assistanceRequested?: boolean;
}

export interface SubmitOnboardingByDraftRequest {
  draftId: string;
  createdBy?: string;
}

// ============================================
// Menu Service Types
// ============================================

export interface MenuItem {
  id: string;
  restaurantId: string;
  name: string;
  description: string;
  categoryId: string;
  categoryName: string;
  basePrice: number;
  currentPrice: number;
  discountPercentage?: number;
  images: string[];
  tags: string[];
  dietaryInfo: DietaryInfo;
  nutritionInfo?: NutritionInfo;
  customizations: MenuCustomization[];
  addons: MenuAddon[];
  availability: MenuItemAvailability;
  preparationTime: number;
  isPopular: boolean;
  isNew: boolean;
  isFeatured: boolean;
  rating: number;
  totalOrders: number;
  createdAt: string;
  updatedAt: string;
}

export interface DietaryInfo {
  isVegetarian: boolean;
  isVegan: boolean;
  isGlutenFree: boolean;
  isHalal: boolean;
  isJain: boolean;
  allergens: string[];
  spiceLevel: SpiceLevel;
}

export interface NutritionInfo {
  calories: number;
  protein: number;
  carbohydrates: number;
  fat: number;
  fiber: number;
  sodium: number;
  servingSize: string;
}

export interface MenuCustomization {
  id: string;
  name: string;
  type: CustomizationType;
  required: boolean;
  minSelections: number;
  maxSelections: number;
  options: CustomizationOption[];
}

export enum CustomizationType {
  SINGLE = 'SINGLE',
  MULTIPLE = 'MULTIPLE',
}

export interface CustomizationOption {
  id: string;
  name: string;
  price: number;
  isDefault: boolean;
  isAvailable: boolean;
}

export interface MenuAddon {
  id: string;
  name: string;
  price: number;
  isAvailable: boolean;
  maxQuantity: number;
}

export interface MenuItemAvailability {
  isAvailable: boolean;
  availableDays: string[];
  startTime?: string;
  endTime?: string;
  stockQuantity?: number;
}

export interface MenuCategory {
  id: string;
  restaurantId: string;
  name: string;
  description?: string;
  imageUrl?: string;
  displayOrder: number;
  isActive: boolean;
  itemCount: number;
}

export interface CreateMenuItemRequest {
  restaurantId: string;
  name: string;
  description: string;
  categoryId: string;
  basePrice: number;
  images: string[];
  tags: string[];
  dietaryInfo: DietaryInfo;
  nutritionInfo?: NutritionInfo;
  customizations?: MenuCustomization[];
  addons?: MenuAddon[];
  preparationTime: number;
}

// ============================================
// Cart & Order Types
// ============================================

export interface Cart {
  id: string;
  userId: string;
  items: CartItem[];
  restaurantId?: string;
  restaurantName?: string;
  subtotal: number;
  deliveryFee: number;
  taxes: number;
  discount: number;
  total: number;
  appliedCouponCode?: string;
  deliveryAddress?: Address;
  specialInstructions?: string;
  isMultiRestaurant: boolean;
  restaurantGroups: RestaurantGroup[];
  updatedAt: string;
}

export interface CartItem {
  id: string;
  menuItemId: string;
  restaurantId: string;
  name: string;
  imageUrl: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  customizations: SelectedCustomization[];
  addons: SelectedAddon[];
  specialInstructions?: string;
}

export interface SelectedCustomization {
  customizationId: string;
  customizationName: string;
  optionId: string;
  optionName: string;
  price: number;
}

export interface SelectedAddon {
  addonId: string;
  addonName: string;
  quantity: number;
  price: number;
}

export interface RestaurantGroup {
  restaurantId: string;
  restaurantName: string;
  items: CartItem[];
  subtotal: number;
  deliveryFee: number;
}

export interface AddToCartRequest {
  menuItemId: string;
  restaurantId: string;
  quantity: number;
  customizations?: SelectedCustomization[];
  addons?: SelectedAddon[];
  specialInstructions?: string;
}

export interface UpdateCartItemRequest {
  quantity?: number;
  customizations?: SelectedCustomization[];
  addons?: SelectedAddon[];
  specialInstructions?: string;
}

export interface Order {
  id: string;
  orderNumber: string;
  userId: string;
  restaurantId: string;
  restaurantName: string;
  type: OrderType;
  status: OrderStatus;
  items: OrderItem[];
  deliveryAddress: Address;
  deliveryPartner?: DeliveryPartner;
  payment: PaymentInfo;
  totals: OrderTotals;
  timeline: OrderTimeline[];
  estimatedDeliveryTime?: string;
  actualDeliveryTime?: string;
  specialInstructions?: string;
  rating?: OrderRating;
  createdAt: string;
  updatedAt: string;
}

export enum OrderType {
  SINGLE_RESTAURANT = 'SINGLE_RESTAURANT',
  MULTI_RESTAURANT = 'MULTI_RESTAURANT',
  AI_CHAT_ORDER = 'AI_CHAT_ORDER',
  PARTY_ORDER = 'PARTY_ORDER',
  DIET_PLAN_ORDER = 'DIET_PLAN_ORDER',
}

export enum OrderStatus {
  PENDING = 'PENDING',
  CONFIRMED = 'CONFIRMED',
  PREPARING = 'PREPARING',
  READY_FOR_PICKUP = 'READY_FOR_PICKUP',
  OUT_FOR_DELIVERY = 'OUT_FOR_DELIVERY',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED',
  REFUNDED = 'REFUNDED',
}

export interface OrderItem {
  id: string;
  menuItemId: string;
  name: string;
  imageUrl: string;
  quantity: number;
  unitPrice: number;
  totalPrice: number;
  customizations: SelectedCustomization[];
  addons: SelectedAddon[];
  specialInstructions?: string;
  status: OrderItemStatus;
}

export enum OrderItemStatus {
  PENDING = 'PENDING',
  PREPARING = 'PREPARING',
  READY = 'READY',
  CANCELLED = 'CANCELLED',
}

export interface DeliveryPartner {
  id: string;
  name: string;
  phone: string;
  photoUrl?: string;
  vehicleType: string;
  vehicleNumber: string;
  currentLocation?: GeoLocation;
  rating: number;
}

export interface GeoLocation {
  latitude: number;
  longitude: number;
  timestamp: string;
}

export interface PaymentInfo {
  id: string;
  method: PaymentMethod;
  status: PaymentStatus;
  amount: number;
  transactionId?: string;
  gatewayResponse?: string;
  paidAt?: string;
}

export enum PaymentMethod {
  CARD = 'CARD',
  UPI = 'UPI',
  WALLET = 'WALLET',
  NETBANKING = 'NETBANKING',
  COD = 'COD',
}

export enum PaymentStatus {
  PENDING = 'PENDING',
  PROCESSING = 'PROCESSING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED',
  REFUNDED = 'REFUNDED',
}

export interface OrderTotals {
  subtotal: number;
  deliveryFee: number;
  platformFee: number;
  taxes: number;
  discount: number;
  tipAmount: number;
  total: number;
}

export interface OrderTimeline {
  status: OrderStatus;
  timestamp: string;
  message: string;
  updatedBy?: string;
}

export interface OrderRating {
  foodRating: number;
  deliveryRating: number;
  overallRating: number;
  review?: string;
  tags: string[];
  createdAt: string;
}

export interface CreateOrderRequest {
  cartId: string;
  deliveryAddressId: string;
  paymentMethod: PaymentMethod;
  tipAmount?: number;
  specialInstructions?: string;
  scheduledTime?: string;
}

export interface CheckoutRequest {
  userId: string;
  cartId: string;
  deliveryAddressId: string;
  paymentMethod: PaymentMethod;
  tipAmount?: number;
  specialInstructions?: string;
}

// ============================================
// Promotion Service Types
// ============================================

export interface Promotion {
  id: string;
  name: string;
  description: string;
  code?: string;
  type: PromotionType;
  status: PromotionStatus;
  discountValue: number;
  minOrderValue?: number;
  maxDiscount?: number;
  validFrom: string;
  validUntil: string;
  usageLimit?: number;
  currentUsage: number;
  restaurantId?: string;
  applicableCategories: string[];
  applicableItems: string[];
  terms: string;
  bannerUrl?: string;
}

export enum PromotionType {
  PERCENTAGE = 'PERCENTAGE',
  FIXED_AMOUNT = 'FIXED_AMOUNT',
  BOGO = 'BOGO',
  FREE_DELIVERY = 'FREE_DELIVERY',
  BUNDLE = 'BUNDLE',
  CASHBACK = 'CASHBACK',
}

export enum PromotionStatus {
  DRAFT = 'DRAFT',
  SCHEDULED = 'SCHEDULED',
  ACTIVE = 'ACTIVE',
  PAUSED = 'PAUSED',
  ENDED = 'ENDED',
  EXPIRED = 'EXPIRED',
  CANCELLED = 'CANCELLED',
}

export interface Coupon {
  id: string;
  code: string;
  name: string;
  description: string;
  type: PromotionType;
  discountValue: number;
  minOrderValue?: number;
  maxDiscount?: number;
  validFrom: string;
  validUntil: string;
  totalLimit?: number;
  perUserLimit?: number;
  currentUsage: number;
  isActive: boolean;
  restaurantId?: string;
}

export interface ValidateCouponRequest {
  code: string;
  userId: string;
  orderValue: number;
  restaurantId?: string;
}

export interface CouponValidationResponse {
  valid: boolean;
  coupon?: Coupon;
  discountAmount: number;
  message?: string;
  reason?: string;
}

export interface ApplyCouponRequest {
  code: string;
  orderId: string;
  userId: string;
}

// ============================================
// Search & Filter Types
// ============================================

export interface SearchRequest {
  query: string;
  filters?: SearchFilters;
  sort?: SearchSort;
  page?: number;
  size?: number;
}

export interface SearchFilters {
  cuisines?: string[];
  priceRange?: PriceRange[];
  rating?: number;
  deliveryTime?: number;
  dietary?: DietaryFilter;
  distance?: number;
  latitude?: number;
  longitude?: number;
}

export interface DietaryFilter {
  vegetarian?: boolean;
  vegan?: boolean;
  glutenFree?: boolean;
  halal?: boolean;
}

export interface SearchSort {
  field: SearchSortField;
  direction: 'asc' | 'desc';
}

export enum SearchSortField {
  RELEVANCE = 'RELEVANCE',
  RATING = 'RATING',
  DELIVERY_TIME = 'DELIVERY_TIME',
  DISTANCE = 'DISTANCE',
  PRICE_LOW = 'PRICE_LOW',
  PRICE_HIGH = 'PRICE_HIGH',
  POPULARITY = 'POPULARITY',
}

export interface SearchResponse {
  restaurants: Restaurant[];
  menuItems: MenuItem[];
  totalRestaurants: number;
  totalMenuItems: number;
  suggestions: string[];
}

// ============================================
// Real-time Tracking Types
// ============================================

export interface OrderTrackingUpdate {
  orderId: string;
  status: OrderStatus;
  timestamp: string;
  deliveryPartnerLocation?: GeoLocation;
  estimatedArrival?: string;
  message?: string;
}

export interface RestaurantOrderUpdate {
  orderId: string;
  status: OrderStatus;
  itemStatuses: { itemId: string; status: OrderItemStatus }[];
  timestamp: string;
  message?: string;
}

