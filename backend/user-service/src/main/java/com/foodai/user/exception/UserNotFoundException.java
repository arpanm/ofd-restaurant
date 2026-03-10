package com.foodai.user.exception;

/**
 * Exception thrown when a user is not found.
 *
 * @author FoodAI Team
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String id) {
        super("User not found with id: " + id);
    }

    public UserNotFoundException(String field, String value) {
        super("User not found with " + field + ": " + value);
    }
}

