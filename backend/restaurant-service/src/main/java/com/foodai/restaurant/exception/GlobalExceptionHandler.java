package com.foodai.restaurant.exception;

import jakarta.validation.ConstraintViolationException;
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

/**
 * Global exception handler for the restaurant service.
 * Provides centralized exception handling and consistent error responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * Handle RestaurantNotFoundException.
     */
    @ExceptionHandler(RestaurantNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleRestaurantNotFound(
        RestaurantNotFoundException ex, 
        WebRequest request
    ) {
        log.error("Restaurant not found: {}", ex.getMessage());
        ApiErrorResponse error = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Not Found")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Handle OutletNotFoundException.
     */
    @ExceptionHandler(OutletNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleOutletNotFound(
        OutletNotFoundException ex, 
        WebRequest request
    ) {
        log.error("Outlet not found: {}", ex.getMessage());
        ApiErrorResponse error = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Not Found")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    /**
     * Handle RestaurantAlreadyExistsException.
     */
    @ExceptionHandler(RestaurantAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleRestaurantAlreadyExists(
        RestaurantAlreadyExistsException ex, 
        WebRequest request
    ) {
        log.error("Restaurant already exists: {}", ex.getMessage());
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
     * Handle InvalidRestaurantStatusException.
     */
    @ExceptionHandler(InvalidRestaurantStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidRestaurantStatus(
        InvalidRestaurantStatusException ex, 
        WebRequest request
    ) {
        log.error("Invalid restaurant status: {}", ex.getMessage());
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
     * Handle InvalidOutletStatusException.
     */
    @ExceptionHandler(InvalidOutletStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidOutletStatus(
        InvalidOutletStatusException ex, 
        WebRequest request
    ) {
        log.error("Invalid outlet status: {}", ex.getMessage());
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
     * Handle DocumentVerificationException.
     */
    @ExceptionHandler(DocumentVerificationException.class)
    public ResponseEntity<ApiErrorResponse> handleDocumentVerification(
        DocumentVerificationException ex, 
        WebRequest request
    ) {
        log.error("Document verification failed: {}", ex.getMessage());
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
     * Handle OnboardingDraftNotFoundException.
     */
    @ExceptionHandler(OnboardingDraftNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleOnboardingDraftNotFound(
        OnboardingDraftNotFoundException ex,
        WebRequest request
    ) {
        log.error("Onboarding draft not found: {}", ex.getMessage());
        ApiErrorResponse error = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Not Found")
            .message(ex.getMessage())
            .path(request.getDescription(false).replace("uri=", ""))
            .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle ServiceabilityException.
     */
    @ExceptionHandler(ServiceabilityException.class)
    public ResponseEntity<ApiErrorResponse> handleServiceability(
        ServiceabilityException ex, 
        WebRequest request
    ) {
        log.error("Serviceability check failed: {}", ex.getMessage());
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
     * Handle validation errors from @Valid annotation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationErrors(
        MethodArgumentNotValidException ex,
        WebRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        log.error("Validation failed: {}", errors);
        ApiErrorResponse error = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Failed")
            .message("Invalid input data")
            .path(request.getDescription(false).replace("uri=", ""))
            .validationErrors(errors)
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handle constraint violation exceptions.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
        ConstraintViolationException ex,
        WebRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(propertyPath, message);
        });
        
        log.error("Constraint violation: {}", errors);
        ApiErrorResponse error = ApiErrorResponse.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Constraint Violation")
            .message("Constraint validation failed")
            .path(request.getDescription(false).replace("uri=", ""))
            .validationErrors(errors)
            .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    /**
     * Handle all other uncaught exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGlobalException(
        Exception ex, 
        WebRequest request
    ) {
        log.error("Unexpected error occurred", ex);
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


