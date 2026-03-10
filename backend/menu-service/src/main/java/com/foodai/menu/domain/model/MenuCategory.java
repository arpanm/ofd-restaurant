package com.foodai.menu.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Domain entity representing a Menu Category.
 *
 * <p>Categories are used to organize menu items (e.g., Appetizers, Main Course, Desserts, Beverages).
 *
 * @author FoodAI Team
 */
@Document(collection = "menu_categories")
@CompoundIndex(name = "restaurant_name_idx", def = "{'restaurantId': 1, 'name': 1}", unique = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuCategory {

  @Id
  private String id;

  @Indexed
  private String restaurantId;

  private String name;

  private String description;

  private String iconUrl;

  private Integer displayOrder;

  private boolean active;

  @CreatedDate
  private Instant createdAt;

  @LastModifiedDate
  private Instant updatedAt;

  private boolean deleted;

  /**
   * Validates the category.
   *
   * @throws IllegalStateException if invalid
   */
  public void validate() {
    if (restaurantId == null || restaurantId.isBlank()) {
      throw new IllegalStateException("Restaurant ID cannot be empty");
    }

    if (name == null || name.isBlank()) {
      throw new IllegalStateException("Category name cannot be empty");
    }

    if (name.length() < 2 || name.length() > 50) {
      throw new IllegalStateException("Category name must be between 2 and 50 characters");
    }
  }

  /**
   * Soft deletes the category.
   */
  public void delete() {
    this.deleted = true;
    this.active = false;
  }
}

