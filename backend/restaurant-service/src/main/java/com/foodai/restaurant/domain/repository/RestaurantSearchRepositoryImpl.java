package com.foodai.restaurant.domain.repository;

import com.foodai.restaurant.domain.model.RestaurantOutlet;
import com.foodai.restaurant.domain.model.Restaurant;
import com.foodai.restaurant.domain.model.RestaurantStatus;
import com.foodai.restaurant.dto.request.SearchRestaurantRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of custom restaurant search repository.
 * Uses MongoDB queries with dynamic criteria building for flexible search.
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class RestaurantSearchRepositoryImpl implements RestaurantSearchRepository {
    
    private final MongoTemplate mongoTemplate;
    
    @Override
    public Page<Restaurant> searchRestaurants(SearchRestaurantRequest request, Pageable pageable) {
        log.debug("Searching restaurants with filters: pincode={}, cuisine={}, budget={}", 
                request.getDeliveryPincode(), request.getCuisineTypes(), request.getBudgetType());
        
        Query query = new Query();
        List<Criteria> criteria = buildCommonCriteria(request);
        
        // Add restaurant-specific criteria
        criteria.add(Criteria.where("outlets").exists(true).not().size(0));
        
        // Apply all criteria
        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        
        // Apply sorting
        query.with(pageable);
        
        // Execute query
        List<Restaurant> restaurants = mongoTemplate.find(query, Restaurant.class);
        long total = mongoTemplate.count(query.skip(-1).limit(-1), Restaurant.class);
        
        log.debug("Found {} restaurants out of {} total", restaurants.size(), total);
        return new PageImpl<>(restaurants, pageable, total);
    }
    
    @Override
    public Page<RestaurantOutlet> searchOutlets(SearchRestaurantRequest request, Pageable pageable) {
        log.debug("Searching outlets with filters: pincode={}, cuisine={}", 
                request.getDeliveryPincode(), request.getCuisineTypes());
        
        // Find restaurants first, then extract their outlets
        Query restaurantQuery = new Query();
        List<Criteria> criteria = buildCommonCriteria(request);
        
        if (!criteria.isEmpty()) {
            restaurantQuery.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }
        
        List<Restaurant> restaurants = mongoTemplate.find(restaurantQuery, Restaurant.class);
        
        // Flatten outlets from all matching restaurants
        List<RestaurantOutlet> allOutlets = new ArrayList<>();
        for (Restaurant restaurant : restaurants) {
            if (restaurant.getOutlets() != null) {
                List<RestaurantOutlet> filteredOutlets = restaurant.getOutlets().stream()
                        .filter(outlet -> matchesOutletCriteria(outlet, request))
                        .toList();
                allOutlets.addAll(filteredOutlets);
            }
        }
        
        // Apply pagination manually
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allOutlets.size());
        List<RestaurantOutlet> paginatedOutlets = start < allOutlets.size() ? 
                allOutlets.subList(start, end) : List.of();
        
        log.debug("Found {} outlets out of {} total", paginatedOutlets.size(), allOutlets.size());
        return new PageImpl<>(paginatedOutlets, pageable, allOutlets.size());
    }
    
    /**
     * Build common search criteria applicable to both restaurants and outlets.
     */
    private List<Criteria> buildCommonCriteria(SearchRestaurantRequest request) {
        List<Criteria> criteria = new ArrayList<>();
        
        // Mandatory: Only approved and active restaurants
        criteria.add(Criteria.where("status").is(RestaurantStatus.APPROVED));
        criteria.add(Criteria.where("acceptsOrders").is(true));
        criteria.add(Criteria.where("deleted").ne(true));
        
        // Mandatory: Must have at least one outlet servicing the delivery pincode
        criteria.add(Criteria.where("outlets.serviceabilityConfig.serviceablePincodes").in(request.getDeliveryPincode()));
        
        // Optional: Cuisine types filter
        if (request.getCuisineTypes() != null && !request.getCuisineTypes().isEmpty()) {
            criteria.add(Criteria.where("cuisineTypes").in(request.getCuisineTypes()));
        }
        
        // Optional: Budget type filter
        if (request.getBudgetType() != null) {
            double minPrice = request.getBudgetType().getMinPrice();
            Double maxPrice = request.getBudgetType().getMaxPrice();
            
            if (maxPrice != null) {
                criteria.add(Criteria.where("averageCostForTwo")
                        .gte(minPrice).lte(maxPrice));
            } else {
                criteria.add(Criteria.where("averageCostForTwo").gte(minPrice));
            }
        }
        
        // Optional: Custom price range
        if (request.getMinPrice() != null) {
            criteria.add(Criteria.where("averageCostForTwo").gte(request.getMinPrice()));
        }
        if (request.getMaxPrice() != null) {
            criteria.add(Criteria.where("averageCostForTwo").lte(request.getMaxPrice()));
        }
        
        // Optional: Rating range filter
        if (request.getMinRating() != null) {
            criteria.add(Criteria.where("averageRating").gte(request.getMinRating()));
        }
        if (request.getMaxRating() != null) {
            criteria.add(Criteria.where("averageRating").lte(request.getMaxRating()));
        }
        
        // Optional: Vegetarian only filter
        if (Boolean.TRUE.equals(request.getVegetarianOnly())) {
            criteria.add(Criteria.where("vegetarianOnly").is(true));
        }
        
        // Optional: Search term (name or description)
        if (request.getSearchTerm() != null && !request.getSearchTerm().isBlank()) {
            Criteria searchCriteria = new Criteria().orOperator(
                    Criteria.where("name").regex(request.getSearchTerm(), "i"),
                    Criteria.where("description").regex(request.getSearchTerm(), "i")
            );
            criteria.add(searchCriteria);
        }
        
        return criteria;
    }
    
    /**
     * Check if an outlet matches the search criteria.
     * Used for outlet-level filtering.
     */
    private boolean matchesOutletCriteria(RestaurantOutlet outlet, SearchRestaurantRequest request) {
        // Check if outlet services the delivery pincode
        boolean servicesPincode = outlet.getServiceabilityConfig() != null &&
                outlet.getServiceabilityConfig().getServiceablePincodes() != null &&
                outlet.getServiceabilityConfig().getServiceablePincodes().contains(request.getDeliveryPincode());
        
        if (!servicesPincode) {
            return false;
        }
        
        // Check if currently open (if filter is applied)
        if (Boolean.TRUE.equals(request.getCurrentlyOpen())) {
            if (outlet.getOperatingHours() == null) {
                return false;
            }
            
            DayOfWeek today = DayOfWeek.from(java.time.LocalDate.now());
            LocalTime now = LocalTime.now();
            
            boolean isOpen = outlet.getOperatingHours().stream()
                    .anyMatch(hours -> hours.getDayOfWeek() == today &&
                            !hours.getOpenTime().isAfter(now) &&
                            !hours.getCloseTime().isBefore(now));
            
            if (!isOpen) {
                return false;
            }
        }
        
        // Check search term in outlet name
        if (request.getSearchTerm() != null && !request.getSearchTerm().isBlank()) {
            String searchTerm = request.getSearchTerm().toLowerCase();
            String outletName = outlet.getOutletName() != null ? outlet.getOutletName().toLowerCase() : "";
            if (!outletName.contains(searchTerm)) {
                return false;
            }
        }
        
        return true;
    }
}

