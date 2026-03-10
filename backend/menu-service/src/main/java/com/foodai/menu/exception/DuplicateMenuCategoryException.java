package com.foodai.menu.exception;

/**
 * Exception thrown when attempting to create a duplicate menu category.
 *
 * @author FoodAI Team
 */
public class DuplicateMenuCategoryException extends RuntimeException {

  public DuplicateMenuCategoryException(String restaurantId, String name) {
    super(String.format("Menu category with name '%s' already exists for restaurant '%s'", 
        name, restaurantId));
  }
}

