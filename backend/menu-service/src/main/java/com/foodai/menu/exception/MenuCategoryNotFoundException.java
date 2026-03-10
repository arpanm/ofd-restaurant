package com.foodai.menu.exception;

/**
 * Exception thrown when a menu category is not found.
 *
 * @author FoodAI Team
 */
public class MenuCategoryNotFoundException extends RuntimeException {

  public MenuCategoryNotFoundException(String id) {
    super(String.format("Menu category not found with id: %s", id));
  }
}

