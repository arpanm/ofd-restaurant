package com.foodai.restaurant.domain.repository;

import com.foodai.restaurant.domain.model.OutletStatus;
import com.foodai.restaurant.domain.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Restaurant aggregate.
 * Provides complex queries for restaurant and outlet operations.
 */
@Repository
public interface RestaurantRepository extends MongoRepository<Restaurant, String> {
    
    // ========== Restaurant (Brand Level) Queries ==========
    
    /**
     * Find restaurant by ID excluding soft-deleted records.
     */
    Optional<Restaurant> findByIdAndDeletedFalse(String id);
    
    /**
     * Find all non-deleted restaurants with pagination.
     */
    Page<Restaurant> findByDeletedFalse(Pageable pageable);
    
    /**
     * Find restaurants by owner ID.
     */
    @Query("{'owners.ownerId': ?0, 'deleted': false}")
    List<Restaurant> findByOwnerId(String ownerId);
    
    /**
     * Search restaurants by name or cuisine type (case-insensitive).
     */
    @Query("{ $and: [ " +
           "  { 'deleted': false }, " +
           "  { $or: [ " +
           "    { 'name': { $regex: ?0, $options: 'i' } }, " +
           "    { 'cuisineTypes': { $regex: ?0, $options: 'i' } } " +
           "  ] } " +
           "] }")
    Page<Restaurant> searchByNameOrCuisine(String searchTerm, Pageable pageable);
    
    /**
     * Find restaurants by cuisine type.
     */
    @Query("{'cuisineTypes': ?0, 'deleted': false}")
    List<Restaurant> findByCuisineType(String cuisineType);
    
    // ========== Outlet Queries (using MongoDB's array queries) ==========
    
    /**
     * Find restaurant containing a specific outlet.
     */
    @Query("{'outlets.outletId': ?0}")
    Optional<Restaurant> findByOutletId(String outletId);
    
    /**
     * Find restaurants with outlets in a specific status.
     */
    @Query("{'outlets.status': ?0, 'outlets.deleted': false, 'deleted': false}")
    List<Restaurant> findByOutletStatus(OutletStatus status);
    
    /**
     * Find restaurants with outlets serving a specific pincode.
     */
    @Query("{ 'outlets': { $elemMatch: { " +
           "  'serviceabilityConfig.serviceablePincodes': ?0, " +
           "  'deleted': false, " +
           "  'status': 'ACTIVE', " +
           "  'acceptsOrders': true " +
           "} }, 'deleted': false, 'acceptsOrders': true }")
    List<Restaurant> findRestaurantsServicingPincode(String pincode);
    
    /**
     * Find restaurants with outlets in a specific city.
     */
    @Query("{ 'outlets': { $elemMatch: { " +
           "  'address.city': ?0, " +
           "  'deleted': false, " +
           "  'status': 'ACTIVE' " +
           "} }, 'deleted': false }")
    List<Restaurant> findByOutletCity(String city);
    
    /**
     * Find restaurants with outlets in a specific pincode (address).
     */
    @Query("{ 'outlets': { $elemMatch: { " +
           "  'address.pincode': ?0, " +
           "  'deleted': false " +
           "} }, 'deleted': false }")
    List<Restaurant> findByOutletAddressPincode(String pincode);
    
    /**
     * Find outlets in a geographic bounding box (for radius-based serviceability).
     */
    @Query("{ 'outlets': { $elemMatch: { " +
           "  'address.latitude': { $gte: ?0, $lte: ?1 }, " +
           "  'address.longitude': { $gte: ?2, $lte: ?3 }, " +
           "  'deleted': false, " +
           "  'status': 'ACTIVE' " +
           "} }, 'deleted': false }")
    List<Restaurant> findOutletsInBoundingBox(
        Double minLat, Double maxLat, 
        Double minLon, Double maxLon
    );
    
    /**
     * Find restaurants by multiple cuisine types.
     */
    @Query("{'cuisineTypes': { $in: ?0 }, 'deleted': false}")
    List<Restaurant> findByCuisineTypesIn(List<String> cuisineTypes);
    
    /**
     * Count active outlets for a restaurant.
     */
    @Query(value = "{'_id': ?0}", count = true)
    long countById(String restaurantId);
    
    /**
     * Check if restaurant with given name already exists (case-insensitive).
     */
    @Query("{'name': { $regex: ?0, $options: 'i' }, 'deleted': false}")
    Optional<Restaurant> findByNameIgnoreCase(String name);
}


