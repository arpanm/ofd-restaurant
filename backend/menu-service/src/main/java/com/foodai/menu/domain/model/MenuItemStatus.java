package com.foodai.menu.domain.model;

/**
 * Enumeration representing the status of a Menu Item.
 *
 * @author FoodAI Team
 */
public enum MenuItemStatus {
  /**
   * Item is active and can be ordered if available.
   */
  ACTIVE,

  /**
   * Item is temporarily inactive (hidden from customers).
   */
  INACTIVE,

  /**
   * Item is out of stock.
   */
  OUT_OF_STOCK,

  /**
   * Item is pending approval (new items).
   */
  PENDING_APPROVAL,

  /**
   * Item is discontinued.
   */
  DISCONTINUED
}

