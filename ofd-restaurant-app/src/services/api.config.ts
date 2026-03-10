/**
 * API Configuration and Axios Instance Setup
 * Centralized configuration for all backend service calls
 * 
 * All requests are routed through the Orchestration Service (API Gateway)
 * The gateway handles:
 * - Request routing to appropriate microservices
 * - Authentication/Authorization
 * - Rate limiting
 * - Request/Response transformation
 * - Circuit breaking
 */

import axios, { AxiosInstance, AxiosError, InternalAxiosRequestConfig, AxiosResponse } from 'axios';

// API Gateway URL - All requests go through orchestration service
const API_GATEWAY_URL = import.meta.env.VITE_API_GATEWAY_URL || 'http://localhost:8085/api/v1';

// Service path prefixes - These are routed by the API Gateway
export const SERVICE_PATHS = {
  user: '/user',           // Routes to user-service (8083)
  restaurant: '/restaurant', // Routes to restaurant-service (8081)
  menu: '/menu',           // Routes to menu-service (8082)
  order: '/order',         // Routes to order-service (8084)
  promotion: '/promotion', // Routes to promotion-service (8089)
  saga: '/sagas',          // Orchestration service direct
  checkout: '/checkout',   // Orchestration service direct
};

// Token storage keys
const ACCESS_TOKEN_KEY = 'foodai_access_token';
const REFRESH_TOKEN_KEY = 'foodai_refresh_token';

// Token management utilities
export const tokenManager = {
  getAccessToken: (): string | null => localStorage.getItem(ACCESS_TOKEN_KEY),
  setAccessToken: (token: string): void => localStorage.setItem(ACCESS_TOKEN_KEY, token),
  getRefreshToken: (): string | null => localStorage.getItem(REFRESH_TOKEN_KEY),
  setRefreshToken: (token: string): void => localStorage.setItem(REFRESH_TOKEN_KEY, token),
  clearTokens: (): void => {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
  },
  setTokens: (accessToken: string, refreshToken: string): void => {
    tokenManager.setAccessToken(accessToken);
    tokenManager.setRefreshToken(refreshToken);
  },
};

// Create the main API Gateway instance
const createApiGatewayInstance = (): AxiosInstance => {
  const instance = axios.create({
    baseURL: API_GATEWAY_URL,
    timeout: 30000,
    headers: {
      'Content-Type': 'application/json',
    },
  });

  // Request interceptor - Add auth token and request metadata
  instance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
      const token = tokenManager.getAccessToken();
      if (token && config.headers) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      // Add request ID for tracing
      config.headers['X-Request-ID'] = `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
      return config;
    },
    (error: AxiosError) => {
      return Promise.reject(error);
    }
  );

  // Response interceptor - Handle errors and token refresh
  instance.interceptors.response.use(
    (response: AxiosResponse) => response,
    async (error: AxiosError) => {
      const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

      // Handle 401 Unauthorized - Token expired
      if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;

        try {
          const refreshToken = tokenManager.getRefreshToken();
          if (refreshToken) {
            // Try to refresh the token via gateway
            const response = await axios.post(`${API_GATEWAY_URL}${SERVICE_PATHS.user}/auth/refresh`, {
              refreshToken,
            });
            
            const { accessToken, refreshToken: newRefreshToken } = response.data.data;
            tokenManager.setTokens(accessToken, newRefreshToken);

            // Retry the original request
            if (originalRequest.headers) {
              originalRequest.headers.Authorization = `Bearer ${accessToken}`;
            }
            return instance(originalRequest);
          }
        } catch (refreshError) {
          // Refresh failed - clear tokens and redirect to login
          tokenManager.clearTokens();
          window.dispatchEvent(new CustomEvent('auth:logout'));
          return Promise.reject(refreshError);
        }
      }

      // Handle other errors
      return Promise.reject(error);
    }
  );

  return instance;
};

// Create the main API gateway instance
const apiGateway = createApiGatewayInstance();

/**
 * Service-specific API wrappers
 * These provide convenient access to each service through the API Gateway
 */

// User Service API wrapper
export const userApi = {
  get: (url: string, config?: any) => apiGateway.get(`${SERVICE_PATHS.user}${url}`, config),
  post: (url: string, data?: any, config?: any) => apiGateway.post(`${SERVICE_PATHS.user}${url}`, data, config),
  put: (url: string, data?: any, config?: any) => apiGateway.put(`${SERVICE_PATHS.user}${url}`, data, config),
  patch: (url: string, data?: any, config?: any) => apiGateway.patch(`${SERVICE_PATHS.user}${url}`, data, config),
  delete: (url: string, config?: any) => apiGateway.delete(`${SERVICE_PATHS.user}${url}`, config),
};

// Restaurant Service API wrapper
export const restaurantApi = {
  get: (url: string, config?: any) => apiGateway.get(`${SERVICE_PATHS.restaurant}${url}`, config),
  post: (url: string, data?: any, config?: any) => apiGateway.post(`${SERVICE_PATHS.restaurant}${url}`, data, config),
  put: (url: string, data?: any, config?: any) => apiGateway.put(`${SERVICE_PATHS.restaurant}${url}`, data, config),
  patch: (url: string, data?: any, config?: any) => apiGateway.patch(`${SERVICE_PATHS.restaurant}${url}`, data, config),
  delete: (url: string, config?: any) => apiGateway.delete(`${SERVICE_PATHS.restaurant}${url}`, config),
};

// Menu Service API wrapper
export const menuApi = {
  get: (url: string, config?: any) => apiGateway.get(`${SERVICE_PATHS.menu}${url}`, config),
  post: (url: string, data?: any, config?: any) => apiGateway.post(`${SERVICE_PATHS.menu}${url}`, data, config),
  put: (url: string, data?: any, config?: any) => apiGateway.put(`${SERVICE_PATHS.menu}${url}`, data, config),
  patch: (url: string, data?: any, config?: any) => apiGateway.patch(`${SERVICE_PATHS.menu}${url}`, data, config),
  delete: (url: string, config?: any) => apiGateway.delete(`${SERVICE_PATHS.menu}${url}`, config),
};

// Order Service API wrapper
export const orderApi = {
  get: (url: string, config?: any) => apiGateway.get(`${SERVICE_PATHS.order}${url}`, config),
  post: (url: string, data?: any, config?: any) => apiGateway.post(`${SERVICE_PATHS.order}${url}`, data, config),
  put: (url: string, data?: any, config?: any) => apiGateway.put(`${SERVICE_PATHS.order}${url}`, data, config),
  patch: (url: string, data?: any, config?: any) => apiGateway.patch(`${SERVICE_PATHS.order}${url}`, data, config),
  delete: (url: string, config?: any) => apiGateway.delete(`${SERVICE_PATHS.order}${url}`, config),
};

// Orchestration Service API wrapper (direct calls to saga/checkout)
export const orchestrationApi = {
  get: (url: string, config?: any) => apiGateway.get(url, config),
  post: (url: string, data?: any, config?: any) => apiGateway.post(url, data, config),
  put: (url: string, data?: any, config?: any) => apiGateway.put(url, data, config),
  patch: (url: string, data?: any, config?: any) => apiGateway.patch(url, data, config),
  delete: (url: string, config?: any) => apiGateway.delete(url, config),
};

// Promotion Service API wrapper
export const promotionApi = {
  get: (url: string, config?: any) => apiGateway.get(`${SERVICE_PATHS.promotion}${url}`, config),
  post: (url: string, data?: any, config?: any) => apiGateway.post(`${SERVICE_PATHS.promotion}${url}`, data, config),
  put: (url: string, data?: any, config?: any) => apiGateway.put(`${SERVICE_PATHS.promotion}${url}`, data, config),
  patch: (url: string, data?: any, config?: any) => apiGateway.patch(`${SERVICE_PATHS.promotion}${url}`, data, config),
  delete: (url: string, config?: any) => apiGateway.delete(`${SERVICE_PATHS.promotion}${url}`, config),
};

// Export raw gateway for advanced use cases
export const gateway = apiGateway;

// Export all APIs as a single object for convenience
export const api = {
  user: userApi,
  restaurant: restaurantApi,
  menu: menuApi,
  order: orderApi,
  orchestration: orchestrationApi,
  promotion: promotionApi,
  gateway: apiGateway,
};

// WebSocket URL for real-time features (also through gateway)
export const getWebSocketUrl = (path: string): string => {
  const wsProtocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
  const wsHost = import.meta.env.VITE_WS_HOST || 'localhost:8085';
  return `${wsProtocol}//${wsHost}/ws${path}`;
};

export default api;
