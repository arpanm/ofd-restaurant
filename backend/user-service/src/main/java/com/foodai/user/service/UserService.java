package com.foodai.user.service;

import com.foodai.user.domain.model.*;
import com.foodai.user.domain.repository.UserRepository;
import com.foodai.user.dto.request.*;
import com.foodai.user.dto.response.AddressResponse;
import com.foodai.user.dto.response.UserResponse;
import com.foodai.user.exception.UserAlreadyExistsException;
import com.foodai.user.exception.UserNotFoundException;
import com.foodai.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for User operations.
 * Handles CRUD operations, business logic, and event publishing.
 *
 * @author FoodAI Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Creates a new user.
     *
     * @param request the create request
     * @return the created user response
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating new user with phone: {}", request.getPhone());

        // Check for existing user
        if (userRepository.existsByPhoneAndDeletedFalse(request.getPhone())) {
            throw new UserAlreadyExistsException("phone", request.getPhone());
        }
        if (request.getEmail() != null && 
            userRepository.existsByEmailIgnoreCaseAndDeletedFalse(request.getEmail())) {
            throw new UserAlreadyExistsException("email", request.getEmail());
        }

        // Map to entity
        User user = userMapper.toEntity(request);
        user.validate();

        // Add initial address if provided
        if (request.getAddress() != null) {
            AddressVO address = userMapper.toAddressVO(request.getAddress());
            user.addAddress(address);
        }

        // Save user
        User saved = userRepository.save(user);
        log.info("User created successfully with ID: {}", saved.getId());

        // Publish event
        publishUserEvent("user.registered", saved);

        return userMapper.toResponse(saved);
    }

    /**
     * Gets a user by ID.
     *
     * @param id the user ID
     * @return the user response
     */
    @Cacheable(value = "users", key = "#id")
    public UserResponse getUserById(String id) {
        log.debug("Fetching user with ID: {}", id);

        User user = userRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        return userMapper.toResponse(user);
    }

    /**
     * Gets a user by phone number.
     *
     * @param phone the phone number
     * @return the user response
     */
    public UserResponse getUserByPhone(String phone) {
        log.debug("Fetching user with phone: {}", phone);

        User user = userRepository.findByPhoneAndDeletedFalse(phone)
            .orElseThrow(() -> new UserNotFoundException("phone", phone));

        return userMapper.toResponse(user);
    }

    /**
     * Gets a user by email.
     *
     * @param email the email address
     * @return the user response
     */
    public UserResponse getUserByEmail(String email) {
        log.debug("Fetching user with email: {}", email);

        User user = userRepository.findByEmailIgnoreCaseAndDeletedFalse(email)
            .orElseThrow(() -> new UserNotFoundException("email", email));

        return userMapper.toResponse(user);
    }

    /**
     * Gets all users with pagination.
     *
     * @param pageable pagination information
     * @return page of users
     */
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.debug("Fetching all users, page: {}", pageable.getPageNumber());

        return userRepository.findByDeletedFalse(pageable)
            .map(userMapper::toResponse);
    }

    /**
     * Searches users by name or email.
     *
     * @param searchTerm the search term
     * @param pageable   pagination information
     * @return page of matching users
     */
    public Page<UserResponse> searchUsers(String searchTerm, Pageable pageable) {
        log.debug("Searching users with term: {}", searchTerm);

        return userRepository.searchUsers(searchTerm, pageable)
            .map(userMapper::toResponse);
    }

    /**
     * Updates a user.
     *
     * @param id      the user ID
     * @param request the update request
     * @return the updated user response
     */
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public UserResponse updateUser(String id, UpdateUserRequest request) {
        log.info("Updating user with ID: {}", id);

        User user = userRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        // Check email uniqueness if changed
        if (request.getEmail() != null && 
            !request.getEmail().equalsIgnoreCase(user.getEmail()) &&
            userRepository.existsByEmailIgnoreCaseAndDeletedFalse(request.getEmail())) {
            throw new UserAlreadyExistsException("email", request.getEmail());
        }

        // Update fields
        userMapper.updateEntityFromRequest(user, request);
        user.setUpdatedBy(request.getUpdatedBy());
        user.setUpdatedAt(Instant.now());

        // Save
        User updated = userRepository.save(user);
        log.info("User updated successfully: {}", id);

        // Publish event
        publishUserEvent("user.profile.updated", updated);

        return userMapper.toResponse(updated);
    }

    /**
     * Deletes a user (soft delete).
     *
     * @param id        the user ID
     * @param deletedBy the user who is deleting
     */
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(String id, String deletedBy) {
        log.info("Deleting user with ID: {}", id);

        User user = userRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new UserNotFoundException(id));

        user.delete(deletedBy);
        userRepository.save(user);

        log.info("User deleted successfully: {}", id);

        // Publish event
        publishUserEvent("user.deleted", user);
    }

    // ==================== Address Operations ====================

    /**
     * Adds an address to a user.
     *
     * @param userId  the user ID
     * @param request the address request
     * @return the added address response
     */
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public AddressResponse addAddress(String userId, AddressDTO request) {
        log.info("Adding address for user: {}", userId);

        User user = userRepository.findByIdAndDeletedFalse(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        AddressVO address = userMapper.toAddressVO(request);
        user.addAddress(address);

        userRepository.save(user);
        log.info("Address added successfully: {}", address.getId());

        return userMapper.toAddressResponse(address);
    }

    /**
     * Updates an address for a user.
     *
     * @param userId    the user ID
     * @param addressId the address ID
     * @param request   the address request
     * @return the updated address response
     */
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public AddressResponse updateAddress(String userId, String addressId, AddressDTO request) {
        log.info("Updating address {} for user: {}", addressId, userId);

        User user = userRepository.findByIdAndDeletedFalse(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        AddressVO existingAddress = user.getAddresses().stream()
            .filter(a -> a.getId().equals(addressId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Address not found: " + addressId));

        // Update address fields
        AddressVO updatedAddress = userMapper.toAddressVO(request);
        updatedAddress.setId(addressId);
        updatedAddress.setCreatedAt(existingAddress.getCreatedAt());

        // Replace in list
        user.getAddresses().removeIf(a -> a.getId().equals(addressId));
        user.getAddresses().add(updatedAddress);

        // Handle default setting
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            user.setDefaultAddress(addressId);
        }

        userRepository.save(user);
        log.info("Address updated successfully: {}", addressId);

        return userMapper.toAddressResponse(updatedAddress);
    }

    /**
     * Removes an address from a user.
     *
     * @param userId    the user ID
     * @param addressId the address ID
     */
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void removeAddress(String userId, String addressId) {
        log.info("Removing address {} from user: {}", addressId, userId);

        User user = userRepository.findByIdAndDeletedFalse(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        boolean removed = user.removeAddress(addressId);
        if (!removed) {
            throw new IllegalArgumentException("Address not found: " + addressId);
        }

        userRepository.save(user);
        log.info("Address removed successfully: {}", addressId);
    }

    /**
     * Gets all addresses for a user.
     *
     * @param userId the user ID
     * @return list of address responses
     */
    public List<AddressResponse> getUserAddresses(String userId) {
        log.debug("Fetching addresses for user: {}", userId);

        User user = userRepository.findByIdAndDeletedFalse(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        return userMapper.toAddressResponseList(user.getAddresses());
    }

    // ==================== Favorites Operations ====================

    /**
     * Adds a restaurant to favorites.
     *
     * @param userId       the user ID
     * @param restaurantId the restaurant ID
     */
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void addFavoriteRestaurant(String userId, String restaurantId) {
        log.info("Adding favorite restaurant {} for user: {}", restaurantId, userId);

        User user = userRepository.findByIdAndDeletedFalse(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        user.addFavoriteRestaurant(restaurantId);
        userRepository.save(user);

        log.info("Favorite restaurant added successfully");
    }

    /**
     * Removes a restaurant from favorites.
     *
     * @param userId       the user ID
     * @param restaurantId the restaurant ID
     */
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void removeFavoriteRestaurant(String userId, String restaurantId) {
        log.info("Removing favorite restaurant {} for user: {}", restaurantId, userId);

        User user = userRepository.findByIdAndDeletedFalse(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        user.removeFavoriteRestaurant(restaurantId);
        userRepository.save(user);

        log.info("Favorite restaurant removed successfully");
    }

    // ==================== Verification Operations ====================

    /**
     * Verifies user's email.
     *
     * @param userId the user ID
     */
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void verifyEmail(String userId) {
        log.info("Verifying email for user: {}", userId);

        User user = userRepository.findByIdAndDeletedFalse(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        user.setEmailVerified(true);
        updateStatusIfVerified(user);
        userRepository.save(user);

        log.info("Email verified successfully for user: {}", userId);
    }

    /**
     * Verifies user's phone.
     *
     * @param userId the user ID
     */
    @Transactional
    @CacheEvict(value = "users", key = "#userId")
    public void verifyPhone(String userId) {
        log.info("Verifying phone for user: {}", userId);

        User user = userRepository.findByIdAndDeletedFalse(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        user.setPhoneVerified(true);
        updateStatusIfVerified(user);
        userRepository.save(user);

        log.info("Phone verified successfully for user: {}", userId);
    }

    private void updateStatusIfVerified(User user) {
        if (user.isPhoneVerified() && user.getStatus() == UserStatus.PENDING_VERIFICATION) {
            user.setStatus(UserStatus.ACTIVE);
        }
    }

    // ==================== Event Publishing ====================

    private void publishUserEvent(String topic, User user) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("eventId", UUID.randomUUID().toString());
            event.put("eventType", topic);
            event.put("timestamp", Instant.now().toString());
            event.put("userId", user.getId());
            event.put("email", user.getEmail());
            event.put("phone", user.getPhone());
            event.put("status", user.getStatus());

            kafkaTemplate.send(topic, user.getId(), event);
            log.debug("Published event to {}: {}", topic, user.getId());
        } catch (Exception e) {
            log.error("Failed to publish event to {}", topic, e);
            // Don't fail the operation if event publishing fails
        }
    }
}

