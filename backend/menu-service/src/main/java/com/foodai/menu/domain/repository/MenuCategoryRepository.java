package com.foodai.menu.domain.repository;

import com.foodai.menu.domain.model.MenuCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for MenuCategory aggregate.
 *
 * <p>Provides data access operations for MenuCategory entities.
 *
 * @author FoodAI Team
 */
@Repository
public interface MenuCategoryRepository extends MongoRepository<MenuCategory, String> {

  /**
   * Finds a non-deleted category by its ID.
   *
   * @param id the category ID
   * @return Optional containing the category if found and not deleted
   */
  Optional<MenuCategory> findByIdAndDeletedFalse(String id);

  /**
   * Finds all non-deleted categories with pagination.
   *
   * @param pageable pagination information
   * @return page of non-deleted categories
   */
  Page<MenuCategory> findByDeletedFalse(Pageable pageable);

  /**
   * Finds all non-deleted categories for a specific restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return list of categories
   */
  List<MenuCategory> findByRestaurantIdAndDeletedFalse(String restaurantId);

  /**
   * Finds all non-deleted categories for a restaurant with sorting.
   *
   * @param restaurantId the restaurant ID
   * @param sort sort order
   * @return list of categories
   */
  List<MenuCategory> findByRestaurantIdAndDeletedFalse(String restaurantId, Sort sort);

  /**
   * Finds active categories for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @param sort sorting information
   * @return list of active categories
   */
  List<MenuCategory> findByRestaurantIdAndActiveTrueAndDeletedFalse(String restaurantId, Sort sort);

  /**
   * Checks if a category exists with the given restaurant ID and name.
   *
   * @param restaurantId the restaurant ID
   * @param name the category name
   * @return true if exists
   */
  boolean existsByRestaurantIdAndNameAndDeletedFalse(String restaurantId, String name);

  /**
   * Counts non-deleted categories for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return count of categories
   */
  long countByRestaurantIdAndDeletedFalse(String restaurantId);
}

