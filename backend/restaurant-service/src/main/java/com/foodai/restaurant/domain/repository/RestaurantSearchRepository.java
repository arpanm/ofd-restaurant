package com.foodai.restaurant.domain.repository;

import com.foodai.restaurant.domain.model.RestaurantOutlet;
import com.foodai.restaurant.domain.model.Restaurant;
import com.foodai.restaurant.dto.request.SearchRestaurantRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom repository interface for advanced restaurant search operations.
 * Provides dynamic query building based on multiple search filters.
 */
public interface RestaurantSearchRepository {
    
    /**
     * Search for restaurants based on multiple criteria.
     * 
     * @param request Search criteria including filters and pagination
     * @param pageable Pagination information
     * @return Page of restaurants matching the criteria
     */
    Page<Restaurant> searchRestaurants(SearchRestaurantRequest request, Pageable pageable);
    
    /**
     * Search for individual outlets based on multiple criteria.
     * 
     * @param request Search criteria including filters and pagination
     * @param pageable Pagination information
     * @return Page of outlets matching the criteria
     */
    Page<RestaurantOutlet> searchOutlets(SearchRestaurantRequest request, Pageable pageable);
}

