package com.foodai.user.controller;

import com.foodai.user.dto.request.AddressDTO;
import com.foodai.user.dto.request.CreateUserRequest;
import com.foodai.user.dto.request.UpdateUserRequest;
import com.foodai.user.dto.response.AddressResponse;
import com.foodai.user.dto.response.ApiResponse;
import com.foodai.user.dto.response.UserResponse;
import com.foodai.user.service.UserService;
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
 * REST controller for User operations.
 * Provides CRUD endpoints for managing users.
 *
 * @author FoodAI Team
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "APIs for managing users, profiles, and preferences")
public class UserController {

    private final UserService userService;

    // ==================== User CRUD ====================

    /**
     * Creates a new user.
     *
     * @param request the create request
     * @return the created user
     */
    @PostMapping
    @Operation(summary = "Create new user", description = "Create a new user account")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "User created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "User already exists")
    })
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        log.info("POST /api/v1/users - Creating user");

        UserResponse response = userService.createUser(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("User created successfully", response));
    }

    /**
     * Gets a user by ID.
     *
     * @param id the user ID
     * @return the user
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve user details by ID")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @Parameter(description = "User ID") @PathVariable String id) {
        log.info("GET /api/v1/users/{} - Fetching user", id);

        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Gets a user by phone number.
     *
     * @param phone the phone number
     * @return the user
     */
    @GetMapping("/by-phone/{phone}")
    @Operation(summary = "Get user by phone", description = "Retrieve user details by phone number")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByPhone(
            @Parameter(description = "Phone number") @PathVariable String phone) {
        log.info("GET /api/v1/users/by-phone/{}", phone);

        UserResponse response = userService.getUserByPhone(phone);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Gets a user by email.
     *
     * @param email the email address
     * @return the user
     */
    @GetMapping("/by-email")
    @Operation(summary = "Get user by email", description = "Retrieve user details by email")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(
            @Parameter(description = "Email address") @RequestParam String email) {
        log.info("GET /api/v1/users/by-email?email={}", email);

        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Gets all users with pagination.
     *
     * @param pageable pagination information
     * @return page of users
     */
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve all users with pagination")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("GET /api/v1/users - Fetching all users, page: {}", pageable.getPageNumber());

        Page<UserResponse> response = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Searches users.
     *
     * @param query    the search term
     * @param pageable pagination information
     * @return page of matching users
     */
    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by name, email, or phone")
    public ResponseEntity<ApiResponse<Page<UserResponse>>> searchUsers(
            @Parameter(description = "Search term") @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable) {
        log.info("GET /api/v1/users/search?query={}", query);

        Page<UserResponse> response = userService.searchUsers(query, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Updates a user.
     *
     * @param id      the user ID
     * @param request the update request
     * @return the updated user
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update user profile")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @Parameter(description = "User ID") @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("PUT /api/v1/users/{} - Updating user", id);

        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", response));
    }

    /**
     * Deletes a user.
     *
     * @param id        the user ID
     * @param deletedBy the user who is deleting
     * @return no content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Soft delete a user")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @Parameter(description = "User ID") @PathVariable String id,
            @Parameter(description = "User ID who is deleting") @RequestParam String deletedBy) {
        log.info("DELETE /api/v1/users/{} - Deleting user", id);

        userService.deleteUser(id, deletedBy);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    // ==================== Address Endpoints ====================

    /**
     * Gets all addresses for a user.
     *
     * @param userId the user ID
     * @return list of addresses
     */
    @GetMapping("/{userId}/addresses")
    @Operation(summary = "Get user addresses", description = "Get all saved addresses for a user")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getUserAddresses(
            @Parameter(description = "User ID") @PathVariable String userId) {
        log.info("GET /api/v1/users/{}/addresses", userId);

        List<AddressResponse> response = userService.getUserAddresses(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Adds an address for a user.
     *
     * @param userId  the user ID
     * @param request the address request
     * @return the added address
     */
    @PostMapping("/{userId}/addresses")
    @Operation(summary = "Add address", description = "Add a new address for a user")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @Parameter(description = "User ID") @PathVariable String userId,
            @Valid @RequestBody AddressDTO request) {
        log.info("POST /api/v1/users/{}/addresses - Adding address", userId);

        AddressResponse response = userService.addAddress(userId, request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success("Address added successfully", response));
    }

    /**
     * Updates an address for a user.
     *
     * @param userId    the user ID
     * @param addressId the address ID
     * @param request   the address request
     * @return the updated address
     */
    @PutMapping("/{userId}/addresses/{addressId}")
    @Operation(summary = "Update address", description = "Update an existing address")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @Parameter(description = "User ID") @PathVariable String userId,
            @Parameter(description = "Address ID") @PathVariable String addressId,
            @Valid @RequestBody AddressDTO request) {
        log.info("PUT /api/v1/users/{}/addresses/{} - Updating address", userId, addressId);

        AddressResponse response = userService.updateAddress(userId, addressId, request);
        return ResponseEntity.ok(ApiResponse.success("Address updated successfully", response));
    }

    /**
     * Removes an address from a user.
     *
     * @param userId    the user ID
     * @param addressId the address ID
     * @return no content
     */
    @DeleteMapping("/{userId}/addresses/{addressId}")
    @Operation(summary = "Remove address", description = "Remove an address from a user")
    public ResponseEntity<ApiResponse<Void>> removeAddress(
            @Parameter(description = "User ID") @PathVariable String userId,
            @Parameter(description = "Address ID") @PathVariable String addressId) {
        log.info("DELETE /api/v1/users/{}/addresses/{} - Removing address", userId, addressId);

        userService.removeAddress(userId, addressId);
        return ResponseEntity.ok(ApiResponse.success("Address removed successfully", null));
    }

    // ==================== Favorites Endpoints ====================

    /**
     * Adds a restaurant to favorites.
     *
     * @param userId       the user ID
     * @param restaurantId the restaurant ID
     * @return success response
     */
    @PostMapping("/{userId}/favorites/restaurants/{restaurantId}")
    @Operation(summary = "Add favorite restaurant", description = "Add a restaurant to user's favorites")
    public ResponseEntity<ApiResponse<Void>> addFavoriteRestaurant(
            @Parameter(description = "User ID") @PathVariable String userId,
            @Parameter(description = "Restaurant ID") @PathVariable String restaurantId) {
        log.info("POST /api/v1/users/{}/favorites/restaurants/{}", userId, restaurantId);

        userService.addFavoriteRestaurant(userId, restaurantId);
        return ResponseEntity.ok(ApiResponse.success("Restaurant added to favorites", null));
    }

    /**
     * Removes a restaurant from favorites.
     *
     * @param userId       the user ID
     * @param restaurantId the restaurant ID
     * @return success response
     */
    @DeleteMapping("/{userId}/favorites/restaurants/{restaurantId}")
    @Operation(summary = "Remove favorite restaurant", description = "Remove a restaurant from user's favorites")
    public ResponseEntity<ApiResponse<Void>> removeFavoriteRestaurant(
            @Parameter(description = "User ID") @PathVariable String userId,
            @Parameter(description = "Restaurant ID") @PathVariable String restaurantId) {
        log.info("DELETE /api/v1/users/{}/favorites/restaurants/{}", userId, restaurantId);

        userService.removeFavoriteRestaurant(userId, restaurantId);
        return ResponseEntity.ok(ApiResponse.success("Restaurant removed from favorites", null));
    }

    // ==================== Verification Endpoints ====================

    /**
     * Verifies user's email.
     *
     * @param userId the user ID
     * @return success response
     */
    @PostMapping("/{userId}/verify-email")
    @Operation(summary = "Verify email", description = "Mark user's email as verified")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @Parameter(description = "User ID") @PathVariable String userId) {
        log.info("POST /api/v1/users/{}/verify-email", userId);

        userService.verifyEmail(userId);
        return ResponseEntity.ok(ApiResponse.success("Email verified successfully", null));
    }

    /**
     * Verifies user's phone.
     *
     * @param userId the user ID
     * @return success response
     */
    @PostMapping("/{userId}/verify-phone")
    @Operation(summary = "Verify phone", description = "Mark user's phone as verified")
    public ResponseEntity<ApiResponse<Void>> verifyPhone(
            @Parameter(description = "User ID") @PathVariable String userId) {
        log.info("POST /api/v1/users/{}/verify-phone", userId);

        userService.verifyPhone(userId);
        return ResponseEntity.ok(ApiResponse.success("Phone verified successfully", null));
    }
}

