package com.foodai.restaurant.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for Restaurant domain model.
 */
@DisplayName("Restaurant Domain Model Tests")
class RestaurantTest {
    
    @Test
    @DisplayName("Should create restaurant with builder pattern")
    void shouldCreateRestaurantWithBuilder() {
        // Given & When
        Restaurant restaurant = Restaurant.builder()
            .id("restaurant-123")
            .name("Test Restaurant")
            .description("Test Description")
            .cuisineTypes(List.of("Italian", "Indian"))
            .status(RestaurantStatus.PENDING)
            .acceptsOrders(true)
            .deleted(false)
            .createdAt(Instant.now())
            .build();
        
        // Then
        assertThat(restaurant).isNotNull();
        assertThat(restaurant.getId()).isEqualTo("restaurant-123");
        assertThat(restaurant.getName()).isEqualTo("Test Restaurant");
        assertThat(restaurant.getCuisineTypes()).hasSize(2);
        assertThat(restaurant.getStatus()).isEqualTo(RestaurantStatus.PENDING);
        assertThat(restaurant.isDeleted()).isFalse();
    }
    
    @Test
    @DisplayName("Should handle outlets list operations")
    void shouldHandleOutletsListOperations() {
        // Given
        Restaurant restaurant = Restaurant.builder()
            .outlets(new ArrayList<>())
            .build();
        
        RestaurantOutlet outlet = RestaurantOutlet.builder()
            .outletId("outlet-1")
            .outletName("Main Branch")
            .status(OutletStatus.PENDING_APPROVAL)
            .build();
        
        // When
        restaurant.getOutlets().add(outlet);
        
        // Then
        assertThat(restaurant.getOutlets()).hasSize(1);
        assertThat(restaurant.getOutlets().get(0).getOutletId()).isEqualTo("outlet-1");
    }
    
    @Test
    @DisplayName("Should handle multiple owners")
    void shouldHandleMultipleOwners() {
        // Given
        OwnerVO owner1 = OwnerVO.builder()
            .ownerId("owner-1")
            .ownerName("John Doe")
            .ownershipPercentage(60.0)
            .role(OwnerRole.PRIMARY_OWNER)
            .build();
        
        OwnerVO owner2 = OwnerVO.builder()
            .ownerId("owner-2")
            .ownerName("Jane Smith")
            .ownershipPercentage(40.0)
            .role(OwnerRole.CO_OWNER)
            .build();
        
        // When
        Restaurant restaurant = Restaurant.builder()
            .owners(List.of(owner1, owner2))
            .build();
        
        // Then
        assertThat(restaurant.getOwners()).hasSize(2);
        double totalOwnership = restaurant.getOwners().stream()
            .mapToDouble(OwnerVO::getOwnershipPercentage)
            .sum();
        assertThat(totalOwnership).isEqualTo(100.0);
    }
    
    @Test
    @DisplayName("Should set and get all audit fields")
    void shouldSetAndGetAuditFields() {
        // Given
        Instant now = Instant.now();
        
        // When
        Restaurant restaurant = Restaurant.builder()
            .createdBy("user-1")
            .createdAt(now)
            .updatedBy("user-2")
            .updatedAt(now)
            .deletedBy("user-3")
            .deletedAt(now)
            .deleted(true)
            .build();
        
        // Then
        assertThat(restaurant.getCreatedBy()).isEqualTo("user-1");
        assertThat(restaurant.getCreatedAt()).isEqualTo(now);
        assertThat(restaurant.getUpdatedBy()).isEqualTo("user-2");
        assertThat(restaurant.getUpdatedAt()).isEqualTo(now);
        assertThat(restaurant.getDeletedBy()).isEqualTo("user-3");
        assertThat(restaurant.getDeletedAt()).isEqualTo(now);
        assertThat(restaurant.isDeleted()).isTrue();
    }
}


