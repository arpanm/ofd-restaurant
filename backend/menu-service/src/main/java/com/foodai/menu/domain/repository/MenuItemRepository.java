package com.foodai.menu.domain.repository;

import com.foodai.menu.domain.model.MenuItem;
import com.foodai.menu.domain.model.MenuItemStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for MenuItem aggregate.
 *
 * <p>Provides data access operations for MenuItem entities.
 *
 * @author FoodAI Team
 */
@Repository
public interface MenuItemRepository extends MongoRepository<MenuItem, String> {

  /**
   * Finds a non-deleted menu item by its ID.
   *
   * @param id the menu item ID
   * @return Optional containing the menu item if found and not deleted
   */
  Optional<MenuItem> findByIdAndDeletedFalse(String id);

  /**
   * Finds all non-deleted menu items with pagination.
   *
   * @param pageable pagination information
   * @return page of non-deleted menu items
   */
  Page<MenuItem> findByDeletedFalse(Pageable pageable);

  /**
   * Finds all non-deleted menu items for a specific restaurant.
   *
   * @param restaurantId the restaurant ID
   * @param pageable pagination information
   * @return page of menu items
   */
  Page<MenuItem> findByRestaurantIdAndDeletedFalse(String restaurantId, Pageable pageable);

  /**
   * Finds all non-deleted menu items for a restaurant and category.
   *
   * @param restaurantId the restaurant ID
   * @param category the category
   * @param pageable pagination information
   * @return page of menu items
   */
  Page<MenuItem> findByRestaurantIdAndCategoryAndDeletedFalse(
      String restaurantId, String category, Pageable pageable);

  /**
   * Finds available menu items for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @param pageable pagination information
   * @return page of available menu items
   */
  Page<MenuItem> findByRestaurantIdAndAvailableTrueAndDeletedFalse(
      String restaurantId, Pageable pageable);

  /**
   * Finds menu items by status.
   *
   * @param restaurantId the restaurant ID
   * @param status the status
   * @param pageable pagination information
   * @return page of menu items
   */
  Page<MenuItem> findByRestaurantIdAndStatusAndDeletedFalse(
      String restaurantId, MenuItemStatus status, Pageable pageable);

  /**
   * Finds vegetarian menu items.
   *
   * @param restaurantId the restaurant ID
   * @param vegetarian whether to find vegetarian items
   * @param pageable pagination information
   * @return page of vegetarian menu items
   */
  Page<MenuItem> findByRestaurantIdAndVegetarianAndDeletedFalse(
      String restaurantId, boolean vegetarian, Pageable pageable);

  /**
   * Searches menu items by name (case-insensitive).
   *
   * @param restaurantId the restaurant ID
   * @param nameRegex name pattern
   * @param pageable pagination information
   * @return page of matching menu items
   */
  @Query("{ 'restaurantId': ?0, 'name': { $regex: ?1, $options: 'i' }, 'deleted': false }")
  Page<MenuItem> searchByName(String restaurantId, String nameRegex, Pageable pageable);

  /**
   * Finds menu items by tags.
   *
   * @param restaurantId the restaurant ID
   * @param tag the tag to search for
   * @param pageable pagination information
   * @return page of menu items with the tag
   */
  Page<MenuItem> findByRestaurantIdAndTagsContainingAndDeletedFalse(
      String restaurantId, String tag, Pageable pageable);

  /**
   * Finds all menu items for a restaurant (including deleted, for admin).
   *
   * @param restaurantId the restaurant ID
   * @param pageable pagination information
   * @return page of all menu items
   */
  Page<MenuItem> findByRestaurantId(String restaurantId, Pageable pageable);

  /**
   * Counts non-deleted menu items for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return count of menu items
   */
  long countByRestaurantIdAndDeletedFalse(String restaurantId);

  /**
   * Counts available menu items for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return count of available menu items
   */
  long countByRestaurantIdAndAvailableTrueAndDeletedFalse(String restaurantId);

  /**
   * Checks if a menu item with given name exists for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @param name the menu item name
   * @return true if exists
   */
  boolean existsByRestaurantIdAndNameAndDeletedFalse(String restaurantId, String name);

  /**
   * Finds top-selling menu items for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return list of top menu items
   */
  @Query(value = "{ 'restaurantId': ?0, 'deleted': false }", sort = "{ 'totalOrders': -1 }")
  List<MenuItem> findTopSellingItems(String restaurantId, Pageable pageable);

  /**
   * Finds highly rated menu items.
   *
   * @param restaurantId the restaurant ID
   * @param minRating minimum average rating
   * @param pageable pagination information
   * @return page of highly rated items
   */
  @Query("{ 'restaurantId': ?0, 'averageRating': { $gte: ?1 }, 'deleted': false }")
  Page<MenuItem> findHighlyRatedItems(String restaurantId, double minRating, Pageable pageable);
}

