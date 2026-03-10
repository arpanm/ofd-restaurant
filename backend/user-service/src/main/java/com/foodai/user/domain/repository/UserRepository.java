package com.foodai.user.domain.repository;

import com.foodai.user.domain.model.User;
import com.foodai.user.domain.model.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User aggregate.
 * Provides data access operations for User entities.
 *
 * @author FoodAI Team
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Finds a non-deleted user by ID.
     *
     * @param id the user ID
     * @return Optional containing the user if found
     */
    Optional<User> findByIdAndDeletedFalse(String id);

    /**
     * Finds all non-deleted users with pagination.
     *
     * @param pageable pagination information
     * @return page of users
     */
    Page<User> findByDeletedFalse(Pageable pageable);

    /**
     * Finds a user by email (case-insensitive).
     *
     * @param email the email address
     * @return Optional containing the user if found
     */
    Optional<User> findByEmailIgnoreCaseAndDeletedFalse(String email);

    /**
     * Finds a user by phone number.
     *
     * @param phone the phone number
     * @return Optional containing the user if found
     */
    Optional<User> findByPhoneAndDeletedFalse(String phone);

    /**
     * Finds a user by referral code.
     *
     * @param referralCode the referral code
     * @return Optional containing the user if found
     */
    Optional<User> findByReferralCodeAndDeletedFalse(String referralCode);

    /**
     * Finds users by status.
     *
     * @param status the user status
     * @param pageable pagination information
     * @return page of users with the given status
     */
    Page<User> findByStatusAndDeletedFalse(UserStatus status, Pageable pageable);

    /**
     * Finds users referred by a specific user.
     *
     * @param referredBy the referrer's referral code
     * @return list of referred users
     */
    List<User> findByReferredByAndDeletedFalse(String referredBy);

    /**
     * Checks if email exists.
     *
     * @param email the email address
     * @return true if email exists
     */
    boolean existsByEmailIgnoreCaseAndDeletedFalse(String email);

    /**
     * Checks if phone exists.
     *
     * @param phone the phone number
     * @return true if phone exists
     */
    boolean existsByPhoneAndDeletedFalse(String phone);

    /**
     * Searches users by name or email.
     *
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of matching users
     */
    @Query("{ 'deleted': false, $or: [ " +
           "{ 'firstName': { $regex: ?0, $options: 'i' } }, " +
           "{ 'lastName': { $regex: ?0, $options: 'i' } }, " +
           "{ 'email': { $regex: ?0, $options: 'i' } }, " +
           "{ 'phone': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<User> searchUsers(String searchTerm, Pageable pageable);

    /**
     * Finds users with specific cuisine preferences.
     *
     * @param cuisine the cuisine preference
     * @param pageable pagination information
     * @return page of users with the preference
     */
    @Query("{ 'deleted': false, 'cuisinePreferences': ?0 }")
    Page<User> findByCuisinePreference(String cuisine, Pageable pageable);

    /**
     * Finds users who are vegetarian.
     *
     * @param pageable pagination information
     * @return page of vegetarian users
     */
    @Query("{ 'deleted': false, 'dietaryPreferences.vegetarian': true }")
    Page<User> findVegetarianUsers(Pageable pageable);

    /**
     * Counts active users.
     *
     * @return count of active users
     */
    long countByStatusAndDeletedFalse(UserStatus status);
}

