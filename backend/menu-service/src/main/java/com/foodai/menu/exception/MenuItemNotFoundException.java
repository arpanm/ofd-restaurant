package com.foodai.menu.exception;

/**
 * Exception thrown when a requested Menu Item is not found.
 *
 * @author FoodAI Team
 */
public class MenuItemNotFoundException extends RuntimeException {

  private final String menuItemId;

  public MenuItemNotFoundException(String menuItemId) {
    super("Menu item not found with id: " + menuItemId);
    this.menuItemId = menuItemId;
  }

  public MenuItemNotFoundException(String menuItemId, String message) {
    super(message);
    this.menuItemId = menuItemId;
  }

  public String getMenuItemId() {
    return menuItemId;
  }
}

