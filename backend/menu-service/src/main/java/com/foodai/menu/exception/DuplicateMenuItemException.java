package com.foodai.menu.exception;

/**
 * Exception thrown when attempting to create a duplicate menu item.
 *
 * @author FoodAI Team
 */
public class DuplicateMenuItemException extends RuntimeException {

  public DuplicateMenuItemException(String restaurantId, String name) {
    super(String.format("Menu item with name '%s' already exists for restaurant '%s'", 
        name, restaurantId));
  }
}

