package com.foodai.promotion.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.foodai.promotion.domain.model.PromotionStatus;
import com.foodai.promotion.domain.model.PromotionType;
import com.foodai.promotion.dto.request.CreatePromotionRequest;
import com.foodai.promotion.dto.response.PromotionResponse;
import com.foodai.promotion.exception.GlobalExceptionHandler;
import com.foodai.promotion.service.PromotionService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * Unit tests for PromotionController.
 *
 * @author FoodAI Team
 */
@ExtendWith(MockitoExtension.class)
class PromotionControllerTest {

  private MockMvc mockMvc;

  private ObjectMapper objectMapper;

  @Mock private PromotionService promotionService;

  @InjectMocks private PromotionController promotionController;

  private PromotionResponse promotionResponse;
  private CreatePromotionRequest createRequest;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    mockMvc =
        MockMvcBuilders.standaloneSetup(promotionController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

    promotionResponse =
        PromotionResponse.builder()
            .id("promo-123")
            .name("Test Promotion")
            .code("TEST20")
            .type(PromotionType.PERCENTAGE)
            .status(PromotionStatus.DRAFT)
            .build();

    createRequest =
        CreatePromotionRequest.builder()
            .name("Test Promotion")
            .code("TEST20")
            .type(PromotionType.PERCENTAGE)
            .restaurantId("restaurant-123")
            .discountValue(new BigDecimal("20"))
            .startDate(Instant.now().minus(1, ChronoUnit.DAYS))
            .endDate(Instant.now().plus(7, ChronoUnit.DAYS))
            .build();
  }

  @Nested
  @DisplayName("Create Promotion Endpoint Tests")
  class CreatePromotionEndpointTests {

    @Test
    @DisplayName("Should create promotion successfully")
    void shouldCreatePromotionSuccessfully() throws Exception {
      when(promotionService.createPromotion(any(CreatePromotionRequest.class)))
          .thenReturn(promotionResponse);

      mockMvc
          .perform(
              post("/api/v1/promotions")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(createRequest)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.id").value("promo-123"))
          .andExpect(jsonPath("$.data.code").value("TEST20"));
    }
  }

  @Nested
  @DisplayName("Get Promotion Endpoint Tests")
  class GetPromotionEndpointTests {

    @Test
    @DisplayName("Should get promotion by ID")
    void shouldGetPromotionById() throws Exception {
      when(promotionService.getPromotion("promo-123")).thenReturn(promotionResponse);

      mockMvc
          .perform(get("/api/v1/promotions/promo-123"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.id").value("promo-123"));
    }

    @Test
    @DisplayName("Should get promotion by code")
    void shouldGetPromotionByCode() throws Exception {
      when(promotionService.getPromotionByCode("TEST20")).thenReturn(promotionResponse);

      mockMvc
          .perform(get("/api/v1/promotions/code/TEST20"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.code").value("TEST20"));
    }
  }

  @Nested
  @DisplayName("Get All Promotions Endpoint Tests")
  class GetAllPromotionsEndpointTests {

    @Test
    @DisplayName("Should get all promotions with pagination")
    void shouldGetAllPromotionsWithPagination() throws Exception {
      Pageable pageable = PageRequest.of(0, 20);
      when(promotionService.getAllPromotions(any(Pageable.class)))
          .thenReturn(new PageImpl<>(List.of(promotionResponse), pageable, 1));

      mockMvc
          .perform(get("/api/v1/promotions"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.content[0].id").value("promo-123"));
    }

    @Test
    @DisplayName("Should get promotions by restaurant")
    void shouldGetPromotionsByRestaurant() throws Exception {
      Pageable pageable = PageRequest.of(0, 20);
      when(promotionService.getPromotionsByRestaurant(any(String.class), any(Pageable.class)))
          .thenReturn(new PageImpl<>(List.of(promotionResponse), pageable, 1));

      mockMvc
          .perform(get("/api/v1/promotions/restaurant/restaurant-123"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.content[0].id").value("promo-123"));
    }

    @Test
    @DisplayName("Should get active promotions for restaurant")
    void shouldGetActivePromotionsForRestaurant() throws Exception {
      when(promotionService.getActivePromotionsForRestaurant("restaurant-123"))
          .thenReturn(List.of(promotionResponse));

      mockMvc
          .perform(get("/api/v1/promotions/restaurant/restaurant-123/active"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data[0].id").value("promo-123"));
    }
  }

  @Nested
  @DisplayName("Status Change Endpoint Tests")
  class StatusChangeEndpointTests {

    @Test
    @DisplayName("Should activate promotion")
    void shouldActivatePromotion() throws Exception {
      when(promotionService.activatePromotion("promo-123")).thenReturn(promotionResponse);

      mockMvc
          .perform(put("/api/v1/promotions/promo-123/activate"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should pause promotion")
    void shouldPausePromotion() throws Exception {
      when(promotionService.pausePromotion("promo-123")).thenReturn(promotionResponse);

      mockMvc
          .perform(put("/api/v1/promotions/promo-123/pause"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should resume promotion")
    void shouldResumePromotion() throws Exception {
      when(promotionService.resumePromotion("promo-123")).thenReturn(promotionResponse);

      mockMvc
          .perform(put("/api/v1/promotions/promo-123/resume"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should end promotion")
    void shouldEndPromotion() throws Exception {
      when(promotionService.endPromotion("promo-123")).thenReturn(promotionResponse);

      mockMvc
          .perform(put("/api/v1/promotions/promo-123/end"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true));
    }
  }

  @Nested
  @DisplayName("Delete Promotion Endpoint Tests")
  class DeletePromotionEndpointTests {

    @Test
    @DisplayName("Should delete promotion")
    void shouldDeletePromotion() throws Exception {
      doNothing().when(promotionService).deletePromotion("promo-123");

      mockMvc
          .perform(delete("/api/v1/promotions/promo-123"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true));
    }
  }
}
