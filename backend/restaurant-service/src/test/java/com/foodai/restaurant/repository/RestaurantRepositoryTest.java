package com.foodai.restaurant.repository;

import com.foodai.restaurant.domain.model.*;
import com.foodai.restaurant.domain.repository.RestaurantRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Repository tests for RestaurantRepository using real MongoDB.
 * Uses the MongoDB instance configured in application-test.yml (localhost:27017).
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Restaurant Repository Tests")
class RestaurantRepositoryTest {
    
    @Autowired
    private RestaurantRepository restaurantRepository;
    
    private Restaurant testRestaurant;
    
    @BeforeEach
    void setUp() {
        restaurantRepository.deleteAll();
        
        testRestaurant = Restaurant.builder()
            .name("Test Restaurant")
            .description("Test Description")
            .cuisineTypes(List.of("Italian", "Indian"))
            .status(RestaurantStatus.PENDING)
            .acceptsOrders(true)
            .outlets(new ArrayList<>())
            .deleted(false)
            .createdAt(Instant.now())
            .build();
    }
    
    @AfterEach
    void tearDown() {
        // Clean up after each test
        restaurantRepository.deleteAll();
    }
    
    @Test
    @DisplayName("Should save and retrieve restaurant")
    void shouldSaveAndRetrieveRestaurant() {
        // When
        Restaurant saved = restaurantRepository.save(testRestaurant);
        
        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        
        Optional<Restaurant> found = restaurantRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Restaurant");
    }
    
    @Test
    @DisplayName("Should find restaurant by ID and not deleted")
    void shouldFindByIdAndDeletedFalse() {
        // Given
        Restaurant saved = restaurantRepository.save(testRestaurant);
        
        // When
        Optional<Restaurant> found = restaurantRepository.findByIdAndDeletedFalse(saved.getId());
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Restaurant");
    }
    
    @Test
    @DisplayName("Should not find deleted restaurant")
    void shouldNotFindDeletedRestaurant() {
        // Given
        testRestaurant.setDeleted(true);
        Restaurant saved = restaurantRepository.save(testRestaurant);
        
        // When
        Optional<Restaurant> found = restaurantRepository.findByIdAndDeletedFalse(saved.getId());
        
        // Then
        assertThat(found).isEmpty();
    }
    
    @Test
    @DisplayName("Should find restaurant by name ignoring case")
    void shouldFindByNameIgnoreCase() {
        // Given
        restaurantRepository.save(testRestaurant);
        
        // When
        Optional<Restaurant> found = restaurantRepository.findByNameIgnoreCase("test restaurant");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Restaurant");
    }
    
    @Test
    @DisplayName("Should find restaurants by cuisine type")
    void shouldFindByCuisineType() {
        // Given
        restaurantRepository.save(testRestaurant);
        
        // When
        List<Restaurant> found = restaurantRepository.findByCuisineType("Italian");
        
        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getCuisineTypes()).contains("Italian");
    }
    
    @Test
    @DisplayName("Should search restaurants by name or cuisine")
    void shouldSearchByNameOrCuisine() {
        // Given
        restaurantRepository.save(testRestaurant);
        
        Restaurant another = Restaurant.builder()
            .name("Pizza Place")
            .cuisineTypes(List.of("Mexican"))
            .status(RestaurantStatus.ACTIVE)
            .deleted(false)
            .createdAt(Instant.now())
            .build();
        restaurantRepository.save(another);
        
        // When - Search by name
        var resultsByName = restaurantRepository.searchByNameOrCuisine("Test", null);
        
        // Then
        assertThat(resultsByName.getContent()).hasSize(1);
        assertThat(resultsByName.getContent().get(0).getName()).contains("Test");
    }
    
    @Test
    @DisplayName("Should handle outlets in restaurant")
    void shouldHandleOutlets() {
        // Given
        RestaurantOutlet outlet = RestaurantOutlet.builder()
            .outletId("outlet-1")
            .outletName("Main Branch")
            .status(OutletStatus.ACTIVE)
            .deleted(false)
            .build();
        
        testRestaurant.getOutlets().add(outlet);
        Restaurant saved = restaurantRepository.save(testRestaurant);
        
        // When
        Optional<Restaurant> found = restaurantRepository.findById(saved.getId());
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getOutlets()).hasSize(1);
        assertThat(found.get().getOutlets().get(0).getOutletId()).isEqualTo("outlet-1");
    }
    
    @Test
    @DisplayName("Should find restaurant by outlet ID")
    void shouldFindByOutletId() {
        // Given
        RestaurantOutlet outlet = RestaurantOutlet.builder()
            .outletId("outlet-123")
            .outletName("Main Branch")
            .status(OutletStatus.ACTIVE)
            .deleted(false)
            .build();
        
        testRestaurant.getOutlets().add(outlet);
        restaurantRepository.save(testRestaurant);
        
        // When
        Optional<Restaurant> found = restaurantRepository.findByOutletId("outlet-123");
        
        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getOutlets()).hasSize(1);
    }
}


