package com.foodai.promotion.domain.model;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value object representing where a promotion applies.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicabilityVO {

  /** Restaurant IDs where promotion applies (empty = all). */
  private Set<String> restaurantIds;

  /** Menu item IDs where promotion applies (empty = all). */
  private Set<String> menuItemIds;

  /** Category names where promotion applies (empty = all). */
  private Set<String> categories;

  /** User segment IDs eligible for promotion (empty = all). */
  private Set<String> segmentIds;

  /** Specific user IDs eligible for promotion (empty = all). */
  private Set<String> userIds;

  /** Cuisine types where promotion applies (empty = all). */
  private Set<String> cuisineTypes;

  /** Whether promotion applies to first order only. */
  private boolean firstOrderOnly;

  /** Whether promotion applies to new users only. */
  private boolean newUsersOnly;

  /** Whether promotion applies to app orders only. */
  private boolean appOnly;

  /** Whether promotion applies to web orders only. */
  private boolean webOnly;

  /**
   * Checks if the promotion applies to a specific restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return true if applicable, false otherwise
   */
  public boolean appliesToRestaurant(String restaurantId) {
    return restaurantIds == null || restaurantIds.isEmpty() || restaurantIds.contains(restaurantId);
  }

  /**
   * Checks if the promotion applies to a specific menu item.
   *
   * @param menuItemId the menu item ID
   * @return true if applicable, false otherwise
   */
  public boolean appliesToMenuItem(String menuItemId) {
    return menuItemIds == null || menuItemIds.isEmpty() || menuItemIds.contains(menuItemId);
  }

  /**
   * Checks if the promotion applies to a specific category.
   *
   * @param category the category name
   * @return true if applicable, false otherwise
   */
  public boolean appliesToCategory(String category) {
    return categories == null || categories.isEmpty() || categories.contains(category);
  }

  /**
   * Checks if the promotion applies to a specific user.
   *
   * @param userId the user ID
   * @param userSegmentIds the user's segment IDs
   * @return true if applicable, false otherwise
   */
  public boolean appliesToUser(String userId, Set<String> userSegmentIds) {
    // Check specific user IDs
    if (userIds != null && !userIds.isEmpty() && !userIds.contains(userId)) {
      return false;
    }
    // Check segment IDs
    if (segmentIds != null && !segmentIds.isEmpty()) {
      if (userSegmentIds == null || userSegmentIds.isEmpty()) {
        return false;
      }
      return userSegmentIds.stream().anyMatch(segmentIds::contains);
    }
    return true;
  }
}

