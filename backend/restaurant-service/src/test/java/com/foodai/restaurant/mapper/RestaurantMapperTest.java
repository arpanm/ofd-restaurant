package com.foodai.restaurant.mapper;

import com.foodai.restaurant.domain.model.*;
import com.foodai.restaurant.dto.request.*;
import com.foodai.restaurant.dto.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for RestaurantMapper.
 */
@DisplayName("Restaurant Mapper Tests")
class RestaurantMapperTest {
    
    private RestaurantMapper mapper;
    
    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(RestaurantMapper.class);
    }
    
    @Test
    @DisplayName("Should map CreateRestaurantRequest to Restaurant entity")
    void shouldMapRequestToEntity() {
        // Given
        CreateRestaurantRequest request = CreateRestaurantRequest.builder()
            .name("Test Restaurant")
            .description("Test Description")
            .cuisineTypes(List.of("Italian"))
            .owners(List.of(OwnerDTO.builder()
                .ownerName("John Doe")
                .ownerEmail("john@example.com")
                .ownerPhone("9876543210")
                .ownershipPercentage(100.0)
                .role(OwnerRole.PRIMARY_OWNER)
                .isPrimaryContact(true)
                .build()))
            .contacts(List.of(ContactDTO.builder()
                .contactType(ContactType.PRIMARY)
                .name("John Doe")
                .phone("9876543210")
                .build()))
            .documents(List.of())
            .contract(ContractDTO.builder().build())
            .createdBy("user-1")
            .build();
        
        // When
        Restaurant restaurant = mapper.toEntity(request);
        
        // Then
        assertThat(restaurant).isNotNull();
        assertThat(restaurant.getId()).isNotNull();
        assertThat(restaurant.getName()).isEqualTo("Test Restaurant");
        assertThat(restaurant.getStatus()).isEqualTo(RestaurantStatus.PENDING);
        assertThat(restaurant.isDeleted()).isFalse();
    }
    
    @Test
    @DisplayName("Should map Restaurant entity to RestaurantResponse")
    void shouldMapEntityToResponse() {
        // Given
        Restaurant restaurant = Restaurant.builder()
            .id("restaurant-123")
            .name("Test Restaurant")
            .description("Test Description")
            .cuisineTypes(List.of("Italian"))
            .status(RestaurantStatus.ACTIVE)
            .acceptsOrders(true)
            .createdAt(Instant.now())
            .build();
        
        // When
        RestaurantResponse response = mapper.toResponse(restaurant);
        
        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("restaurant-123");
        assertThat(response.getName()).isEqualTo("Test Restaurant");
        assertThat(response.getStatus()).isEqualTo(RestaurantStatus.ACTIVE);
    }
    
    @Test
    @DisplayName("Should map AddressDTO to AddressVO")
    void shouldMapAddressDTOToVO() {
        // Given
        AddressDTO dto = AddressDTO.builder()
            .street("123 Main St")
            .city("Mumbai")
            .state("Maharashtra")
            .pincode("400001")
            .latitude(19.0760)
            .longitude(72.8777)
            .landmark("Near Station")
            .build();
        
        // When
        AddressVO vo = mapper.toAddressVO(dto);
        
        // Then
        assertThat(vo).isNotNull();
        assertThat(vo.getStreet()).isEqualTo("123 Main St");
        assertThat(vo.getCity()).isEqualTo("Mumbai");
        assertThat(vo.getPincode()).isEqualTo("400001");
        assertThat(vo.getLatitude()).isEqualTo(19.0760);
        assertThat(vo.getLongitude()).isEqualTo(72.8777);
    }
    
    @Test
    @DisplayName("Should map OperatingHoursDTO to OperatingHoursVO")
    void shouldMapOperatingHoursDTOToVO() {
        // Given
        OperatingHoursDTO dto = OperatingHoursDTO.builder()
            .dayOfWeek(DayOfWeek.MONDAY)
            .openTime(LocalTime.of(9, 0))
            .closeTime(LocalTime.of(22, 0))
            .isClosed(false)
            .build();
        
        // When
        OperatingHoursVO vo = mapper.toOperatingHoursVO(dto);
        
        // Then
        assertThat(vo).isNotNull();
        assertThat(vo.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(vo.getOpenTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(vo.getCloseTime()).isEqualTo(LocalTime.of(22, 0));
        assertThat(vo.getIsClosed()).isFalse();
    }
}


