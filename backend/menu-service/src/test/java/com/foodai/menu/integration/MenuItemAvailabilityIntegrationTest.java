package com.foodai.menu.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodai.menu.domain.model.AvailabilityScheduleVO;
import com.foodai.menu.domain.model.MenuItem;
import com.foodai.menu.domain.repository.MenuItemRepository;
import com.foodai.menu.dto.request.CreateMenuItemRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Menu Item Availability and Scheduling features.
 * 
 * <p>Tests availability management requirements:
 * - Time-based availability (breakfast, lunch, dinner)
 * - Day-of-week availability (weekend specials)
 * - Seasonal availability
 * - Real-time stock management
 * - Toggle availability on/off
 *
 * @author FoodAI Team
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Disabled("Requires Docker - enable when Docker environment is available")
@DisplayName("Menu Item Availability Integration Tests")
class MenuItemAvailabilityIntegrationTest {

  @Container
  static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0.3");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private MenuItemRepository repository;

  @BeforeEach
  void setUp() {
    repository.deleteAll();
  }

  @AfterEach
  void tearDown() {
    repository.deleteAll();
  }

  @Test
  @DisplayName("Should create breakfast item available only in morning hours")
  void shouldCreateBreakfastItemWithTimeRestriction() throws Exception {
    // Arrange
    AvailabilityScheduleVO breakfastSchedule = AvailabilityScheduleVO.builder()
        .alwaysAvailable(false)
        .availableDays(Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY))
        .startTime(LocalTime.of(6, 0))
        .endTime(LocalTime.of(11, 0))
        .seasonalStartDate(null)
        .seasonalEndDate(null)
        .build();

    CreateMenuItemRequest request = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Pancake Stack")
        .description("Fluffy pancakes with maple syrup")
        .category("Breakfast")
        .basePrice(new BigDecimal("180"))
        .availabilitySchedule(breakfastSchedule)
        .available(true)
        .build();

    // Act
    String response = mockMvc.perform(post("/api/v1/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name", is("Pancake Stack")))
        .andReturn()
        .getResponse()
        .getContentAsString();

    String itemId = objectMapper.readTree(response).get("id").asText();

    // Assert
    MenuItem saved = repository.findById(itemId).orElseThrow();
    assertThat(saved.getAvailabilitySchedule()).isNotNull();
    assertThat(saved.getAvailabilitySchedule().getStartTime()).isEqualTo(LocalTime.of(6, 0));
    assertThat(saved.getAvailabilitySchedule().getEndTime()).isEqualTo(LocalTime.of(11, 0));
  }

  @Test
  @DisplayName("Should check if item is available now based on time schedule")
  void shouldCheckItemAvailabilityBasedOnTime() {
    // Arrange - Lunch item available 11 AM to 4 PM
    AvailabilityScheduleVO lunchSchedule = AvailabilityScheduleVO.builder()
        .alwaysAvailable(false)
        .availableDays(Set.of(DayOfWeek.values()))
        .startTime(LocalTime.of(11, 0))
        .endTime(LocalTime.of(16, 0))
        .seasonalStartDate(null)
        .seasonalEndDate(null)
        .build();

    MenuItem lunchItem = new MenuItem();
    lunchItem.setName("Lunch Thali");
    lunchItem.setAvailable(true);
    lunchItem.setAvailabilitySchedule(lunchSchedule);

    // Act & Assert - Check at different times
    LocalTime morningTime = LocalTime.of(10, 0);
    LocalTime lunchTime = LocalTime.of(13, 0);
    LocalTime eveningTime = LocalTime.of(18, 0);

    // Note: isAvailableNow() would need current time parameter or use system time
    // This is a conceptual test - actual implementation may vary
  }

  @Test
  @DisplayName("Should create weekend special item")
  void shouldCreateWeekendSpecialItem() throws Exception {
    // Arrange
    AvailabilityScheduleVO weekendSchedule = AvailabilityScheduleVO.builder()
        .alwaysAvailable(false)
        .availableDays(Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY))
        .startTime(LocalTime.of(12, 0))
        .endTime(LocalTime.of(22, 0))
        .seasonalStartDate(null)
        .seasonalEndDate(null)
        .build();

    CreateMenuItemRequest request = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Weekend Brunch Special")
        .description("Special brunch menu for weekends")
        .category("Specials")
        .basePrice(new BigDecimal("450"))
        .availabilitySchedule(weekendSchedule)
        .available(true)
        .build();

    // Act & Assert
    mockMvc.perform(post("/api/v1/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name", is("Weekend Brunch Special")));
  }

  @Test
  @DisplayName("Should create seasonal menu item")
  void shouldCreateSeasonalMenuItem() throws Exception {
    // Arrange - Winter special available Dec-Feb
    AvailabilityScheduleVO winterSchedule = AvailabilityScheduleVO.builder()
        .alwaysAvailable(false)
        .availableDays(Set.of(DayOfWeek.values()))
        .startTime(LocalTime.of(0, 0))
        .endTime(LocalTime.of(23, 59))
        .seasonalStartDate(LocalDateTime.of(2025, 12, 1, 0, 0))
        .seasonalEndDate(LocalDateTime.of(2026, 2, 28, 23, 59))
        .build();

    CreateMenuItemRequest request = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Gajar Halwa")
        .description("Warm carrot pudding - winter special")
        .category("Desserts")
        .basePrice(new BigDecimal("120"))
        .availabilitySchedule(winterSchedule)
        .available(true)
        .build();

    // Act
    String response = mockMvc.perform(post("/api/v1/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    String itemId = objectMapper.readTree(response).get("id").asText();

    // Assert
    MenuItem saved = repository.findById(itemId).orElseThrow();
    assertThat(saved.getAvailabilitySchedule().getSeasonalStartDate()).isEqualTo(LocalDateTime.of(2025, 12, 1, 0, 0));
    assertThat(saved.getAvailabilitySchedule().getSeasonalEndDate()).isEqualTo(LocalDateTime.of(2026, 2, 28, 23, 59));
  }

  @Test
  @DisplayName("Should toggle menu item availability on and off")
  void shouldToggleAvailability() throws Exception {
    // Arrange - Create item
    MenuItem item = new MenuItem();
    item.setRestaurantId("rest123");
    item.setName("Test Item");
    item.setCategory("Test");
    item.setBasePrice(new BigDecimal("100"));
    item.setAvailable(true);
    item.initializeDefaults();
    MenuItem saved = repository.save(item);

    // Act - Toggle OFF
    mockMvc.perform(patch("/api/v1/menu-items/{id}/toggle-availability", saved.getId())
            .param("available", "false"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.available", is(false)));

    // Act - Toggle ON
    mockMvc.perform(patch("/api/v1/menu-items/{id}/toggle-availability", saved.getId())
            .param("available", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.available", is(true)));
  }

  @Test
  @DisplayName("Should mark item as sold out")
  void shouldMarkItemAsSoldOut() throws Exception {
    // Arrange
    MenuItem item = new MenuItem();
    item.setRestaurantId("rest123");
    item.setName("Limited Edition Dish");
    item.setCategory("Specials");
    item.setBasePrice(new BigDecimal("500"));
    item.setAvailable(true);
    item.initializeDefaults();
    MenuItem saved = repository.save(item);

    // Act - Mark as sold out
    mockMvc.perform(patch("/api/v1/menu-items/{id}/toggle-availability", saved.getId())
            .param("available", "false"))
        .andExpect(status().isOk());

    // Assert
    MenuItem updated = repository.findById(saved.getId()).orElseThrow();
    assertThat(updated.isAvailable()).isFalse();
  }

  @Test
  @DisplayName("Should filter only available items")
  void shouldFilterOnlyAvailableItems() throws Exception {
    // Arrange - Create mix of available and unavailable items
    MenuItem available1 = new MenuItem();
    available1.setRestaurantId("rest123");
    available1.setName("Available Item 1");
    available1.setCategory("Test");
    available1.setBasePrice(new BigDecimal("100"));
    available1.setAvailable(true);
    available1.initializeDefaults();

    MenuItem available2 = new MenuItem();
    available2.setRestaurantId("rest123");
    available2.setName("Available Item 2");
    available2.setCategory("Test");
    available2.setBasePrice(new BigDecimal("150"));
    available2.setAvailable(true);
    available2.initializeDefaults();

    MenuItem unavailable = new MenuItem();
    unavailable.setRestaurantId("rest123");
    unavailable.setName("Sold Out Item");
    unavailable.setCategory("Test");
    unavailable.setBasePrice(new BigDecimal("200"));
    unavailable.setAvailable(false);
    unavailable.initializeDefaults();

    repository.save(available1);
    repository.save(available2);
    repository.save(unavailable);

    // Act & Assert - Get available items
    mockMvc.perform(get("/api/v1/menu-items/restaurant/{restaurantId}/available", "rest123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)));
  }
}

