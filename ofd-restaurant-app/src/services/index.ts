/**
 * Services Index - Export all API services
 */

export { default as api, tokenManager } from './api.config';
export { default as userService } from './user.service';
export { default as restaurantService } from './restaurant.service';
export { default as menuService } from './menu.service';
export { default as orderService } from './order.service';
export { default as promotionService } from './promotion.service';

// Re-export individual APIs for direct access
export {
  userApi,
  restaurantApi,
  menuApi,
  orderApi,
  orchestrationApi,
  promotionApi,
} from './api.config';

