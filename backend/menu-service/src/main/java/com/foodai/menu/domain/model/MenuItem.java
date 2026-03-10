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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Domain entity representing a Menu Item.
 *
 * <p>This is an aggregate root in the Menu bounded context.
 * A MenuItem represents a food/beverage item that can be ordered from a restaurant.
 *
 * <p>Business Rules:
 * <ul>
 *   <li>Name must be unique per restaurant</li>
 *   <li>Price must be positive</li>
 *   <li>Cannot be available if restaurant is inactive</li>
 *   <li>Customizations must have at least one option</li>
 *   <li>Preparation time must be realistic (5-120 minutes)</li>
 * </ul>
 *
 * @author FoodAI Team
 */
@Document(collection = "menu_items")
@CompoundIndex(name = "restaurant_name_idx", def = "{'restaurantId': 1, 'name': 1}", unique = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

  @Id
  private String id;

  @Indexed
  private String restaurantId;

  private String name;

  private String description;

  @Indexed
  private String category;

  private BigDecimal basePrice;

  private List<String> images;

  private boolean available;

  private Integer preparationTime; // in minutes

  private Integer spiceLevel; // 0-5

  // Dietary Information
  private boolean vegetarian;
  private boolean vegan;
  private boolean glutenFree;
  private boolean containsDairy;
  private boolean containsNuts;
  private boolean halal;
  private boolean jain;

  // Nutritional Information
  private NutritionalInfoVO nutritionalInfo;

  // Ingredients and allergens
  private List<String> ingredients;
  private Set<String> allergens;

  // Customization options
  private List<CustomizationVO> customizations;

  // Availability scheduling
  private AvailabilityScheduleVO availabilitySchedule;

  // Dynamic pricing configuration
  private DynamicPricingConfigVO dynamicPricingConfig;

  // Tags for search and filtering
  @Indexed
  private Set<String> tags;

  // Servings
  private Integer serves;

  // Item status
  private MenuItemStatus status;

  // Popularity and performance metrics
  private Integer totalOrders;
  private BigDecimal averageRating;
  private Integer reviewCount;

  // Audit fields
  @CreatedDate
  private Instant createdAt;

  @LastModifiedDate
  private Instant updatedAt;

  private boolean deleted;

  /**
   * Validates if the menu item is in a valid state.
   *
   * @throws IllegalStateException if entity is invalid
   */
  public void validate() {
    if (restaurantId == null || restaurantId.isBlank()) {
      throw new IllegalStateException("Restaurant ID cannot be empty");
    }

    if (name == null || name.isBlank()) {
      throw new IllegalStateException("Menu item name cannot be empty");
    }

    if (name.length() < 2 || name.length() > 100) {
      throw new IllegalStateException("Menu item name must be between 2 and 100 characters");
    }

    if (basePrice == null || basePrice.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalStateException("Price must be positive");
    }

    if (preparationTime != null && (preparationTime < 5 || preparationTime > 120)) {
      throw new IllegalStateException("Preparation time must be between 5 and 120 minutes");
    }

    if (spiceLevel != null && (spiceLevel < 0 || spiceLevel > 5)) {
      throw new IllegalStateException("Spice level must be between 0 and 5");
    }

    if (customizations != null) {
      customizations.forEach(c -> {
        if (c.getOptions() == null || c.getOptions().isEmpty()) {
          throw new IllegalStateException("Customization must have at least one option");
        }
      });
    }
  }

  /**
   * Calculates final price including customizations.
   *
   * @param selectedCustomizations list of selected customization option IDs
   * @return final calculated price
   */
  public BigDecimal calculateFinalPrice(List<String> selectedCustomizations) {
    BigDecimal finalPrice = this.basePrice;

    if (customizations != null && selectedCustomizations != null) {
      for (CustomizationVO customization : customizations) {
        for (CustomizationOptionVO option : customization.getOptions()) {
          if (selectedCustomizations.contains(option.getName())) {
            finalPrice = finalPrice.add(option.getAdditionalCost());
          }
        }
      }
    }

    return finalPrice;
  }

  /**
   * Checks if the item is currently available based on schedule.
   *
   * @return true if available now
   */
  public boolean isAvailableNow() {
    if (!available || deleted || status != MenuItemStatus.ACTIVE) {
      return false;
    }

    if (availabilitySchedule == null) {
      return true;
    }

    return availabilitySchedule.isAvailableNow();
  }

  /**
   * Updates availability status.
   *
   * @param available the new availability status
   */
  public void updateAvailability(boolean available) {
    this.available = available;
  }

  /**
   * Marks item as out of stock.
   */
  public void markOutOfStock() {
    this.available = false;
    this.status = MenuItemStatus.OUT_OF_STOCK;
  }

  /**
   * Marks item as in stock.
   */
  public void markInStock() {
    this.available = true;
    this.status = MenuItemStatus.ACTIVE;
  }

  /**
   * Soft deletes the menu item.
   */
  public void delete() {
    this.deleted = true;
    this.available = false;
    this.status = MenuItemStatus.INACTIVE;
  }

  /**
   * Records an order for this item.
   */
  public void recordOrder() {
    if (this.totalOrders == null) {
      this.totalOrders = 0;
    }
    this.totalOrders++;
  }

  /**
   * Updates the average rating.
   *
   * @param newRating the new rating to incorporate
   */
  public void updateRating(BigDecimal newRating) {
    if (this.reviewCount == null) {
      this.reviewCount = 0;
      this.averageRating = BigDecimal.ZERO;
    }

    BigDecimal totalRating = this.averageRating.multiply(new BigDecimal(this.reviewCount));
    totalRating = totalRating.add(newRating);
    this.reviewCount++;
    this.averageRating = totalRating.divide(new BigDecimal(this.reviewCount), 2, BigDecimal.ROUND_HALF_UP);
  }

  /**
   * Initializes default values for a new menu item.
   */
  public void initializeDefaults() {
    if (images == null) {
      images = new ArrayList<>();
    }
    if (tags == null) {
      tags = new HashSet<>();
    }
    if (allergens == null) {
      allergens = new HashSet<>();
    }
    if (ingredients == null) {
      ingredients = new ArrayList<>();
    }
    if (customizations == null) {
      customizations = new ArrayList<>();
    }
    if (totalOrders == null) {
      totalOrders = 0;
    }
    if (reviewCount == null) {
      reviewCount = 0;
    }
    if (averageRating == null) {
      averageRating = BigDecimal.ZERO;
    }
    if (status == null) {
      status = MenuItemStatus.ACTIVE;
    }

    // Auto-generate tags based on properties
    generateAutoTags();
  }

  /**
   * Automatically generates tags based on item properties.
   */
  private void generateAutoTags() {
    if (vegetarian) tags.add("vegetarian");
    if (vegan) tags.add("vegan");
    if (glutenFree) tags.add("gluten-free");
    if (halal) tags.add("halal");
    if (jain) tags.add("jain");
    if (spiceLevel != null && spiceLevel >= 3) tags.add("spicy");
    if (category != null) tags.add(category.toLowerCase().replace(" ", "-"));
  }
}

