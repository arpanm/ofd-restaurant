/**
 * UI Slice - UI state management
 */

import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface Toast {
  id: string;
  type: 'success' | 'error' | 'warning' | 'info';
  message: string;
  duration?: number;
}

interface Modal {
  id: string;
  type: string;
  props?: any;
}

interface UIState {
  // Global loading
  isGlobalLoading: boolean;
  loadingMessage: string | null;
  
  // Toasts
  toasts: Toast[];
  
  // Modals
  activeModals: Modal[];
  
  // Navigation
  sidebarOpen: boolean;
  mobileMenuOpen: boolean;
  
  // Theme
  theme: 'light' | 'dark' | 'system';
  
  // Location
  userLocation: { latitude: number; longitude: number } | null;
  locationPermissionGranted: boolean;
  
  // Search
  isSearchOpen: boolean;
  searchHistory: string[];
  
  // Filters panel
  isFiltersOpen: boolean;
}

const initialState: UIState = {
  isGlobalLoading: false,
  loadingMessage: null,
  toasts: [],
  activeModals: [],
  sidebarOpen: true,
  mobileMenuOpen: false,
  theme: 'system',
  userLocation: null,
  locationPermissionGranted: false,
  isSearchOpen: false,
  searchHistory: [],
  isFiltersOpen: false,
};

const uiSlice = createSlice({
  name: 'ui',
  initialState,
  reducers: {
    // Loading
    setGlobalLoading: (state, action: PayloadAction<{ loading: boolean; message?: string }>) => {
      state.isGlobalLoading = action.payload.loading;
      state.loadingMessage = action.payload.message || null;
    },
    
    // Toasts
    addToast: (state, action: PayloadAction<Omit<Toast, 'id'>>) => {
      const toast: Toast = {
        ...action.payload,
        id: `toast-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`,
      };
      state.toasts.push(toast);
    },
    removeToast: (state, action: PayloadAction<string>) => {
      state.toasts = state.toasts.filter((t) => t.id !== action.payload);
    },
    clearToasts: (state) => {
      state.toasts = [];
    },
    
    // Modals
    openModal: (state, action: PayloadAction<{ type: string; props?: any }>) => {
      const modal: Modal = {
        id: `modal-${Date.now()}`,
        type: action.payload.type,
        props: action.payload.props,
      };
      state.activeModals.push(modal);
    },
    closeModal: (state, action: PayloadAction<string>) => {
      state.activeModals = state.activeModals.filter((m) => m.id !== action.payload);
    },
    closeTopModal: (state) => {
      state.activeModals.pop();
    },
    closeAllModals: (state) => {
      state.activeModals = [];
    },
    
    // Navigation
    toggleSidebar: (state) => {
      state.sidebarOpen = !state.sidebarOpen;
    },
    setSidebarOpen: (state, action: PayloadAction<boolean>) => {
      state.sidebarOpen = action.payload;
    },
    toggleMobileMenu: (state) => {
      state.mobileMenuOpen = !state.mobileMenuOpen;
    },
    setMobileMenuOpen: (state, action: PayloadAction<boolean>) => {
      state.mobileMenuOpen = action.payload;
    },
    
    // Theme
    setTheme: (state, action: PayloadAction<'light' | 'dark' | 'system'>) => {
      state.theme = action.payload;
      localStorage.setItem('theme', action.payload);
    },
    
    // Location
    setUserLocation: (state, action: PayloadAction<{ latitude: number; longitude: number }>) => {
      state.userLocation = action.payload;
      state.locationPermissionGranted = true;
    },
    clearUserLocation: (state) => {
      state.userLocation = null;
    },
    setLocationPermission: (state, action: PayloadAction<boolean>) => {
      state.locationPermissionGranted = action.payload;
    },
    
    // Search
    toggleSearch: (state) => {
      state.isSearchOpen = !state.isSearchOpen;
    },
    setSearchOpen: (state, action: PayloadAction<boolean>) => {
      state.isSearchOpen = action.payload;
    },
    addToSearchHistory: (state, action: PayloadAction<string>) => {
      // Keep only unique items and limit to 10
      const filtered = state.searchHistory.filter((h) => h !== action.payload);
      state.searchHistory = [action.payload, ...filtered].slice(0, 10);
      localStorage.setItem('searchHistory', JSON.stringify(state.searchHistory));
    },
    clearSearchHistory: (state) => {
      state.searchHistory = [];
      localStorage.removeItem('searchHistory');
    },
    
    // Filters
    toggleFilters: (state) => {
      state.isFiltersOpen = !state.isFiltersOpen;
    },
    setFiltersOpen: (state, action: PayloadAction<boolean>) => {
      state.isFiltersOpen = action.payload;
    },
    
    // Initialize from localStorage
    initializeUI: (state) => {
      const theme = localStorage.getItem('theme') as 'light' | 'dark' | 'system' | null;
      if (theme) {
        state.theme = theme;
      }
      
      const searchHistory = localStorage.getItem('searchHistory');
      if (searchHistory) {
        try {
          state.searchHistory = JSON.parse(searchHistory);
        } catch {
          state.searchHistory = [];
        }
      }
    },
  },
});

export const {
  setGlobalLoading,
  addToast,
  removeToast,
  clearToasts,
  openModal,
  closeModal,
  closeTopModal,
  closeAllModals,
  toggleSidebar,
  setSidebarOpen,
  toggleMobileMenu,
  setMobileMenuOpen,
  setTheme,
  setUserLocation,
  clearUserLocation,
  setLocationPermission,
  toggleSearch,
  setSearchOpen,
  addToSearchHistory,
  clearSearchHistory,
  toggleFilters,
  setFiltersOpen,
  initializeUI,
} = uiSlice.actions;

export default uiSlice.reducer;

