package com.foodai.user.exception;

/**
 * Exception thrown when a user already exists.
 *
 * @author FoodAI Team
 */
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String field, String value) {
        super("User already exists with " + field + ": " + value);
    }
}

