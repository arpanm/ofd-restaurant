package com.foodai.menu.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static net.logstash.logback.argument.StructuredArguments.kv;

/**
 * Global exception handler for the Menu Service.
 *
 * <p>Provides consistent error responses across all controllers.
 *
 * @author FoodAI Team
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * Handles MenuItemNotFoundException.
   *
   * @param ex the exception
   * @param request the web request
   * @return error response
   */
  @ExceptionHandler(MenuItemNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleMenuItemNotFound(
      MenuItemNotFoundException ex, WebRequest request) {
    log.warn("Menu item not found", kv("menuItemId", ex.getMenuItemId()));

    ApiErrorResponse error = ApiErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
        .message(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(MenuCategoryNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleMenuCategoryNotFound(
      MenuCategoryNotFoundException ex, WebRequest request) {
    log.warn("Menu category not found", kv("message", ex.getMessage()));

    ApiErrorResponse error = ApiErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.NOT_FOUND.value())
        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
        .message(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(DuplicateMenuItemException.class)
  public ResponseEntity<ApiErrorResponse> handleDuplicateMenuItem(
      DuplicateMenuItemException ex, WebRequest request) {
    log.warn("Duplicate menu item", kv("message", ex.getMessage()));

    ApiErrorResponse error = ApiErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.CONFLICT.value())
        .error(HttpStatus.CONFLICT.getReasonPhrase())
        .message(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  @ExceptionHandler(DuplicateMenuCategoryException.class)
  public ResponseEntity<ApiErrorResponse> handleDuplicateMenuCategory(
      DuplicateMenuCategoryException ex, WebRequest request) {
    log.warn("Duplicate menu category", kv("message", ex.getMessage()));

    ApiErrorResponse error = ApiErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.CONFLICT.value())
        .error(HttpStatus.CONFLICT.getReasonPhrase())
        .message(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  /**
   * Handles validation errors from @Valid annotations.
   *
   * @param ex the exception
   * @param request the web request
   * @return error response with field-level errors
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidationErrors(
      MethodArgumentNotValidException ex, WebRequest request) {
    
    Map<String, String> fieldErrors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      fieldErrors.put(fieldName, errorMessage);
    });

    log.warn("Validation failed", kv("errors", fieldErrors));

    ApiErrorResponse error = ApiErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Validation Failed")
        .message("Input validation failed")
        .path(request.getDescription(false).replace("uri=", ""))
        .validationErrors(fieldErrors)
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  /**
   * Handles IllegalArgumentException.
   *
   * @param ex the exception
   * @param request the web request
   * @return error response
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiErrorResponse> handleIllegalArgument(
      IllegalArgumentException ex, WebRequest request) {
    log.warn("Illegal argument", kv("message", ex.getMessage()));

    ApiErrorResponse error = ApiErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.BAD_REQUEST.value())
        .error("Bad Request")
        .message(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }

  /**
   * Handles IllegalStateException.
   *
   * @param ex the exception
   * @param request the web request
   * @return error response
   */
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ApiErrorResponse> handleIllegalState(
      IllegalStateException ex, WebRequest request) {
    log.warn("Illegal state", kv("message", ex.getMessage()));

    ApiErrorResponse error = ApiErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.CONFLICT.value())
        .error("Conflict")
        .message(ex.getMessage())
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  /**
   * Handles all other uncaught exceptions.
   *
   * @param ex the exception
   * @param request the web request
   * @return error response
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleGenericException(
      Exception ex, WebRequest request) {
    log.error("Unexpected error occurred", ex, kv("message", ex.getMessage()));

    ApiErrorResponse error = ApiErrorResponse.builder()
        .timestamp(Instant.now())
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .error("Internal Server Error")
        .message("An unexpected error occurred. Please try again later.")
        .path(request.getDescription(false).replace("uri=", ""))
        .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }
}

