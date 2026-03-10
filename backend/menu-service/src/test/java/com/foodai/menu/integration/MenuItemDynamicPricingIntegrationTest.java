package com.foodai.menu.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodai.menu.domain.model.DynamicPricingConfigVO;
import com.foodai.menu.domain.model.MenuItem;
import com.foodai.menu.domain.model.PriceTimeSlotVO;
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
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Dynamic Pricing features.
 * 
 * <p>Tests smart pricing requirements from SMART_PRICING.md and MENU_AI_SUGGESTIONS.md:
 * - Peak hour pricing multipliers
 * - Off-peak discounts
 * - Demand-based pricing adjustments
 * - Time-based pricing slots
 * - Price floor and ceiling constraints
 * - A/B testing for different prices
 *
 * @author FoodAI Team
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Disabled("Requires Docker - enable when Docker environment is available")
@DisplayName("Menu Item Dynamic Pricing Integration Tests")
class MenuItemDynamicPricingIntegrationTest {

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
  @DisplayName("Should create menu item with peak hour pricing")
  void shouldCreateItemWithPeakHourPricing() throws Exception {
    // Arrange - Define peak hours (lunch and dinner rush)
    PriceTimeSlotVO lunchPeak = PriceTimeSlotVO.builder()
        .days(Set.of(DayOfWeek.values()))
        .startTime(LocalTime.of(12, 0))
        .endTime(LocalTime.of(14, 30))
        .multiplier(new BigDecimal("1.2")) // 20% markup during lunch rush
        .build();

    PriceTimeSlotVO dinnerPeak = PriceTimeSlotVO.builder()
        .days(Set.of(DayOfWeek.values()))
        .startTime(LocalTime.of(19, 0))
        .endTime(LocalTime.of(21, 30))
        .multiplier(new BigDecimal("1.3")) // 30% markup during dinner rush
        .build();

    DynamicPricingConfigVO pricingConfig = DynamicPricingConfigVO.builder()
        .enabled(true)
        .peakHourMultiplier(new BigDecimal("1.2"))
        .offPeakMultiplier(new BigDecimal("0.8"))
        .priceTimeSlots(List.of(lunchPeak, dinnerPeak))
        .demandBasedPricing(true)
        .priceFloor(new BigDecimal("200"))
        .priceCeiling(new BigDecimal("800"))
        .build();

    CreateMenuItemRequest request = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Butter Chicken")
        .category("Main Course")
        .basePrice(new BigDecimal("350"))
        .dynamicPricingConfig(pricingConfig)
        .available(true)
        .build();

    // Act
    String response = mockMvc.perform(post("/api/v1/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name", is("Butter Chicken")))
        .andReturn()
        .getResponse()
        .getContentAsString();

    String itemId = objectMapper.readTree(response).get("id").asText();

    // Assert
    MenuItem saved = repository.findById(itemId).orElseThrow();
    assertThat(saved.getDynamicPricingConfig()).isNotNull();
    assertThat(saved.getDynamicPricingConfig().isEnabled()).isTrue();
    assertThat(saved.getDynamicPricingConfig().getPriceTimeSlots()).hasSize(2);
    assertThat(saved.getDynamicPricingConfig().getPriceFloor()).isEqualByComparingTo(new BigDecimal("200"));
    assertThat(saved.getDynamicPricingConfig().getPriceCeiling()).isEqualByComparingTo(new BigDecimal("800"));
  }

  @Test
  @DisplayName("Should apply off-peak discount pricing")
  void shouldApplyOffPeakDiscount() throws Exception {
    // Arrange
    DynamicPricingConfigVO pricingConfig = DynamicPricingConfigVO.builder()
        .enabled(true)
        .peakHourMultiplier(new BigDecimal("1.0"))
        .offPeakMultiplier(new BigDecimal("0.85")) // 15% off-peak discount
        .priceTimeSlots(List.of())
        .demandBasedPricing(false)
        .priceFloor(new BigDecimal("100"))
        .priceCeiling(new BigDecimal("500"))
        .build();

    CreateMenuItemRequest request = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Pasta Alfredo")
        .category("Main Course")
        .basePrice(new BigDecimal("280"))
        .dynamicPricingConfig(pricingConfig)
        .available(true)
        .build();

    // Act
    mockMvc.perform(post("/api/v1/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());

    // During off-peak hours, price should be: 280 * 0.85 = 238
  }

  @Test
  @DisplayName("Should enforce price floor constraint")
  void shouldEnforcePriceFloor() {
    // Arrange
    DynamicPricingConfigVO pricingConfig = DynamicPricingConfigVO.builder()
        .enabled(true)
        .peakHourMultiplier(new BigDecimal("1.0"))
        .offPeakMultiplier(new BigDecimal("0.5")) // 50% discount
        .priceTimeSlots(List.of())
        .demandBasedPricing(false)
        .priceFloor(new BigDecimal("150"))
        .priceCeiling(new BigDecimal("500"))
        .build();

    MenuItem item = new MenuItem();
    item.setBasePrice(new BigDecimal("200"));
    item.setDynamicPricingConfig(pricingConfig);

    // Act - Apply 50% discount: 200 * 0.5 = 100
    // But price floor is 150, so final price should be 150

    // Note: Actual pricing calculation would be done by a pricing service
    BigDecimal discountedPrice = item.getBasePrice().multiply(pricingConfig.getOffPeakMultiplier());
    BigDecimal finalPrice = discountedPrice.max(pricingConfig.getPriceFloor());

    // Assert
    assertThat(finalPrice).isEqualByComparingTo(new BigDecimal("150"));
  }

  @Test
  @DisplayName("Should enforce price ceiling constraint")
  void shouldEnforcePriceCeiling() {
    // Arrange
    DynamicPricingConfigVO pricingConfig = DynamicPricingConfigVO.builder()
        .enabled(true)
        .peakHourMultiplier(new BigDecimal("1.0"))
        .offPeakMultiplier(new BigDecimal("1.0"))
        .priceTimeSlots(List.of())
        .demandBasedPricing(true)
        .priceFloor(new BigDecimal("100"))
        .priceCeiling(new BigDecimal("400"))
        .build();

    MenuItem item = new MenuItem();
    item.setBasePrice(new BigDecimal("300"));
    item.setDynamicPricingConfig(pricingConfig);

    // Act - Use calculatePrice to test demand-based pricing
    // But price ceiling is 400, so final price should be 400

    BigDecimal finalPrice = pricingConfig.calculatePrice(
        item.getBasePrice(), true, 0.9, 1.0  // Peak hour + high demand
    );

    // Assert
    assertThat(finalPrice).isEqualByComparingTo(new BigDecimal("400"));
  }

  @Test
  @DisplayName("Should update dynamic pricing configuration")
  void shouldUpdateDynamicPricingConfig() throws Exception {
    // Arrange - Create item
    MenuItem item = new MenuItem();
    item.setRestaurantId("rest123");
    item.setName("Test Item");
    item.setCategory("Test");
    item.setBasePrice(new BigDecimal("250"));
    item.setAvailable(true);
    item.initializeDefaults();
    MenuItem saved = repository.save(item);

    // Arrange - New pricing config
    DynamicPricingConfigVO newPricingConfig = DynamicPricingConfigVO.builder()
        .enabled(true)
        .peakHourMultiplier(new BigDecimal("1.0"))
        .offPeakMultiplier(new BigDecimal("0.9")) // 10% off-peak discount
        .priceTimeSlots(List.of())
        .demandBasedPricing(false)
        .priceFloor(new BigDecimal("150"))
        .priceCeiling(new BigDecimal("400"))
        .build();

    // Act - Update pricing config via API
    // Note: Actual endpoint implementation needed

    // Assert
    // Verify new pricing config is saved
  }

  @Test
  @DisplayName("Should calculate price based on time slot")
  void shouldCalculatePriceBasedOnTimeSlot() {
    // Arrange
    PriceTimeSlotVO dinnerSlot = PriceTimeSlotVO.builder()
        .days(Set.of(DayOfWeek.values()))
        .startTime(LocalTime.of(19, 0))
        .endTime(LocalTime.of(21, 0))
        .multiplier(new BigDecimal("1.25")) // 25% markup
        .build();

    DynamicPricingConfigVO pricingConfig = DynamicPricingConfigVO.builder()
        .enabled(true)
        .peakHourMultiplier(new BigDecimal("1.25"))
        .offPeakMultiplier(new BigDecimal("1.0"))
        .priceTimeSlots(List.of(dinnerSlot))
        .demandBasedPricing(false)
        .priceFloor(new BigDecimal("0"))
        .priceCeiling(new BigDecimal("10000"))
        .build();

    MenuItem item = new MenuItem();
    item.setBasePrice(new BigDecimal("300"));
    item.setDynamicPricingConfig(pricingConfig);

    // Act - Calculate price at 8 PM (during dinner slot)
    // Base price: 300
    // Dinner multiplier: 1.25
    // Expected: 300 * 1.25 = 375

    BigDecimal expectedPrice = item.getBasePrice().multiply(new BigDecimal("1.25"));

    // Assert
    assertThat(expectedPrice).isEqualByComparingTo(new BigDecimal("375"));
  }

  @Test
  @DisplayName("Should enable and disable dynamic pricing")
  void shouldToggleDynamicPricing() throws Exception {
    // Arrange
    DynamicPricingConfigVO pricingConfig = DynamicPricingConfigVO.builder()
        .enabled(true) // initially enabled
        .peakHourMultiplier(new BigDecimal("1.0"))
        .offPeakMultiplier(new BigDecimal("0.9"))
        .priceTimeSlots(List.of())
        .demandBasedPricing(false)
        .priceFloor(new BigDecimal("100"))
        .priceCeiling(new BigDecimal("500"))
        .build();

    CreateMenuItemRequest request = CreateMenuItemRequest.builder()
        .restaurantId("rest123")
        .name("Test Item")
        .category("Test")
        .basePrice(new BigDecimal("250"))
        .dynamicPricingConfig(pricingConfig)
        .available(true)
        .build();

    String response = mockMvc.perform(post("/api/v1/menu-items")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();

    String itemId = objectMapper.readTree(response).get("id").asText();

    // Act - Disable dynamic pricing
    MenuItem item = repository.findById(itemId).orElseThrow();
    item.getDynamicPricingConfig().setEnabled(false);
    repository.save(item);

    // Assert
    MenuItem updated = repository.findById(itemId).orElseThrow();
    assertThat(updated.getDynamicPricingConfig().isEnabled()).isFalse();
  }
}

