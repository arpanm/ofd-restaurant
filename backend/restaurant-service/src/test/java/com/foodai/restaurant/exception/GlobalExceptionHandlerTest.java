package com.foodai.restaurant.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for GlobalExceptionHandler.
 */
@DisplayName("Global Exception Handler Tests")
class GlobalExceptionHandlerTest {
    
    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
    private final WebRequest webRequest = new ServletWebRequest(new MockHttpServletRequest());
    
    @Test
    @DisplayName("Should handle RestaurantNotFoundException")
    void shouldHandleRestaurantNotFoundException() {
        // Given
        RestaurantNotFoundException exception = new RestaurantNotFoundException("restaurant-123", true);
        
        // When
        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleRestaurantNotFound(exception, webRequest);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getMessage()).contains("restaurant-123");
    }
    
    @Test
    @DisplayName("Should handle OutletNotFoundException")
    void shouldHandleOutletNotFoundException() {
        // Given
        OutletNotFoundException exception = new OutletNotFoundException("outlet-123", true);
        
        // When
        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleOutletNotFound(exception, webRequest);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).contains("outlet-123");
    }
    
    @Test
    @DisplayName("Should handle RestaurantAlreadyExistsException")
    void shouldHandleRestaurantAlreadyExistsException() {
        // Given
        RestaurantAlreadyExistsException exception = new RestaurantAlreadyExistsException("Test Restaurant");
        
        // When
        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleRestaurantAlreadyExists(exception, webRequest);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
    }
    
    @Test
    @DisplayName("Should handle InvalidRestaurantStatusException")
    void shouldHandleInvalidRestaurantStatusException() {
        // Given
        InvalidRestaurantStatusException exception = new InvalidRestaurantStatusException(
            "Invalid status transition"
        );
        
        // When
        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleInvalidRestaurantStatus(exception, webRequest);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
    }
    
    @Test
    @DisplayName("Should handle generic exceptions")
    void shouldHandleGenericException() {
        // Given
        Exception exception = new RuntimeException("Unexpected error");
        
        // When
        ResponseEntity<ApiErrorResponse> response = exceptionHandler.handleGlobalException(exception, webRequest);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
    }
}


