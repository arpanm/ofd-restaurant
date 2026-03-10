package com.foodai.restaurant.controller;

import com.foodai.restaurant.dto.request.CreateOutletRequest;
import com.foodai.restaurant.dto.request.CreateRestaurantRequest;
import com.foodai.restaurant.dto.request.SearchRestaurantRequest;
import com.foodai.restaurant.dto.request.SubmitOnboardingRequest;
import com.foodai.restaurant.dto.response.ApiResponse;
import com.foodai.restaurant.dto.response.OutletResponse;
import com.foodai.restaurant.dto.response.RestaurantResponse;
import com.foodai.restaurant.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Restaurant operations.
 * Provides endpoints for restaurant CRUD operations and outlet management.
 */
@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Restaurant Management", description = "APIs for managing restaurants and outlets")
public class RestaurantController {
    
    private final RestaurantService restaurantService;
    
    /**
     * Create a new restaurant.
     */
    @PostMapping
    @Operation(summary = "Create new restaurant", description = "Create a new restaurant brand with owners and contacts")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Restaurant created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Restaurant already exists")
    })
    public ResponseEntity<ApiResponse<RestaurantResponse>> createRestaurant(
        @Valid @RequestBody CreateRestaurantRequest request
    ) {
        log.info("POST /api/v1/restaurants - Creating restaurant: {}", request.getName());
        
        RestaurantResponse response = restaurantService.createRestaurant(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Restaurant created successfully", response));
    }

    /**
     * Submit restaurant onboarding (workflow: all steps in one request).
     * Creates restaurant with default contract and first outlet; persists legal and bank details.
     */
    @PostMapping("/onboarding")
    @Operation(summary = "Submit onboarding", description = "Submit full onboarding flow and create restaurant + first outlet")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Onboarding completed, restaurant created"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Restaurant name already exists")
    })
    public ResponseEntity<ApiResponse<RestaurantResponse>> submitOnboarding(
        @Valid @RequestBody SubmitOnboardingRequest request
    ) {
        log.info("POST /api/v1/restaurants/onboarding - Submitting onboarding: {}", request.getName());
        RestaurantResponse response = restaurantService.submitOnboarding(request);
        restaurantService.afterOnboardingSubmitted(response.getId(), request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Onboarding completed. Check your email and phone for verification to log in.", response));
    }
    
    /**
     * Get restaurant by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get restaurant by ID", description = "Retrieve restaurant details by ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Restaurant found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    public ResponseEntity<ApiResponse<RestaurantResponse>> getRestaurantById(
        @Parameter(description = "Restaurant ID") @PathVariable String id
    ) {
        log.info("GET /api/v1/restaurants/{} - Fetching restaurant", id);
        
        RestaurantResponse response = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Get all restaurants with pagination.
     */
    @GetMapping
    @Operation(summary = "Get all restaurants", description = "Retrieve all restaurants with pagination")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Restaurants retrieved successfully")
    })
    public ResponseEntity<ApiResponse<Page<RestaurantResponse>>> getAllRestaurants(
        @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        log.info("GET /api/v1/restaurants - Fetching all restaurants, page: {}", pageable.getPageNumber());
        
        Page<RestaurantResponse> response = restaurantService.getAllRestaurants(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Search restaurants.
     */
    @GetMapping("/search")
    @Operation(summary = "Search restaurants", description = "Search restaurants by name or cuisine")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search completed successfully")
    })
    public ResponseEntity<ApiResponse<Page<RestaurantResponse>>> searchRestaurants(
        @Parameter(description = "Search term") @RequestParam String query,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        log.info("GET /api/v1/restaurants/search?query={}", query);
        
        Page<RestaurantResponse> response = restaurantService.searchRestaurants(query, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Get restaurants by owner.
     */
    @GetMapping("/by-owner/{ownerId}")
    @Operation(summary = "Get restaurants by owner", description = "Retrieve all restaurants owned by a specific owner")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Restaurants retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<RestaurantResponse>>> getRestaurantsByOwner(
        @Parameter(description = "Owner ID") @PathVariable String ownerId
    ) {
        log.info("GET /api/v1/restaurants/by-owner/{}", ownerId);
        
        List<RestaurantResponse> response = restaurantService.getRestaurantsByOwner(ownerId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Update restaurant.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update restaurant", description = "Update restaurant details")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Restaurant updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    public ResponseEntity<ApiResponse<RestaurantResponse>> updateRestaurant(
        @Parameter(description = "Restaurant ID") @PathVariable String id,
        @Valid @RequestBody CreateRestaurantRequest request
    ) {
        log.info("PUT /api/v1/restaurants/{} - Updating restaurant", id);
        
        RestaurantResponse response = restaurantService.updateRestaurant(id, request);
        return ResponseEntity.ok(ApiResponse.success("Restaurant updated successfully", response));
    }
    
    /**
     * Delete restaurant.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete restaurant", description = "Soft delete a restaurant")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Restaurant deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteRestaurant(
        @Parameter(description = "Restaurant ID") @PathVariable String id,
        @Parameter(description = "User ID who is deleting") @RequestParam String deletedBy
    ) {
        log.info("DELETE /api/v1/restaurants/{} - Deleting restaurant", id);
        
        restaurantService.deleteRestaurant(id, deletedBy);
        return ResponseEntity.ok(ApiResponse.success("Restaurant deleted successfully", null));
    }
    
    /**
     * Add outlet to restaurant.
     */
    @PostMapping("/{restaurantId}/outlets")
    @Operation(summary = "Add outlet", description = "Add a new outlet to a restaurant")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Outlet created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    public ResponseEntity<ApiResponse<OutletResponse>> addOutlet(
        @Parameter(description = "Restaurant ID") @PathVariable String restaurantId,
        @Valid @RequestBody CreateOutletRequest request
    ) {
        log.info("POST /api/v1/restaurants/{}/outlets - Adding outlet", restaurantId);
        
        OutletResponse response = restaurantService.addOutlet(restaurantId, request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Outlet created successfully", response));
    }
    
    /**
     * Get outlet by ID.
     */
    @GetMapping("/{restaurantId}/outlets/{outletId}")
    @Operation(summary = "Get outlet by ID", description = "Retrieve outlet details")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Outlet found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Outlet not found")
    })
    public ResponseEntity<ApiResponse<OutletResponse>> getOutletById(
        @Parameter(description = "Restaurant ID") @PathVariable String restaurantId,
        @Parameter(description = "Outlet ID") @PathVariable String outletId
    ) {
        log.info("GET /api/v1/restaurants/{}/outlets/{}", restaurantId, outletId);
        
        OutletResponse response = restaurantService.getOutletById(restaurantId, outletId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Get all outlets for a restaurant.
     */
    @GetMapping("/{restaurantId}/outlets")
    @Operation(summary = "Get all outlets", description = "Retrieve all outlets for a restaurant")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Outlets retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<OutletResponse>>> getOutletsByRestaurant(
        @Parameter(description = "Restaurant ID") @PathVariable String restaurantId
    ) {
        log.info("GET /api/v1/restaurants/{}/outlets", restaurantId);
        
        List<OutletResponse> response = restaurantService.getOutletsByRestaurant(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    /**
     * Approve restaurant.
     */
    @PostMapping("/{restaurantId}/approve")
    @Operation(summary = "Approve restaurant", description = "Approve a restaurant for operations")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Restaurant approved successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Restaurant not found")
    })
    public ResponseEntity<ApiResponse<RestaurantResponse>> approveRestaurant(
        @Parameter(description = "Restaurant ID") @PathVariable String restaurantId,
        @Parameter(description = "User ID who is approving") @RequestParam String approvedBy
    ) {
        log.info("POST /api/v1/restaurants/{}/approve - Approving restaurant", restaurantId);
        
        RestaurantResponse response = restaurantService.approveRestaurant(restaurantId, approvedBy);
        return ResponseEntity.ok(ApiResponse.success("Restaurant approved successfully", response));
    }
    
    /**
     * Advanced search for restaurants or outlets with multiple filters.
     * 
     * Query Parameters:
     * - deliveryPincode (required): 6-digit pincode for delivery location
     * - cuisineTypes: Comma-separated list of cuisines (e.g., "Italian,Chinese")
     * - budgetType: BUDGET, AFFORDABLE, MID_RANGE, PREMIUM, LUXURY
     * - minRating: Minimum rating filter (0-5)
     * - maxRating: Maximum rating filter (0-5)
     * - searchTerm: Search in restaurant/outlet names
     * - returnOutlets: true to return individual outlets, false for restaurants (default: false)
     * - vegetarianOnly: Filter for vegetarian-only restaurants
     * - currentlyOpen: Filter for currently open restaurants
     * - minPrice: Minimum price (cost for two)
     * - maxPrice: Maximum price (cost for two)
     * - sortBy: rating, price, distance, popularity (default: rating)
     * - sortDirection: asc, desc (default: desc)
     * - page: Page number (default: 0)
     * - size: Page size (default: 20, max: 100)
     */
    @PostMapping("/search")
    @Operation(
        summary = "Advanced restaurant search",
        description = "Search for restaurants or outlets with multiple filters including delivery pincode (required), cuisine, budget, ratings, and more"
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid search parameters")
    })
    public ResponseEntity<ApiResponse<Page<?>>> searchRestaurants(
        @Valid @RequestBody SearchRestaurantRequest request
    ) {
        log.info("POST /api/v1/restaurants/search - Searching with filters: pincode={}, cuisine={}, budget={}", 
                request.getDeliveryPincode(), request.getCuisineTypes(), request.getBudgetType());
        
        Page<?> results = restaurantService.searchRestaurants(request);
        
        String message = String.format("Found %d %s", 
                results.getTotalElements(),
                Boolean.TRUE.equals(request.getReturnOutlets()) ? "outlets" : "restaurants");
        
        return ResponseEntity.ok(ApiResponse.success(message, results));
    }
}


