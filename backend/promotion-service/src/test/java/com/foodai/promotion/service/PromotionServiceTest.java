package com.foodai.promotion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.foodai.promotion.domain.model.DiscountVO;
import com.foodai.promotion.domain.model.Promotion;
import com.foodai.promotion.domain.model.PromotionStatus;
import com.foodai.promotion.domain.model.PromotionType;
import com.foodai.promotion.domain.model.ScheduleVO;
import com.foodai.promotion.domain.repository.PromotionRepository;
import com.foodai.promotion.dto.request.CreatePromotionRequest;
import com.foodai.promotion.dto.response.PromotionResponse;
import com.foodai.promotion.exception.PromotionNotFoundException;
import com.foodai.promotion.mapper.PromotionMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Unit tests for PromotionService.
 *
 * @author FoodAI Team
 */
@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {

  @Mock private PromotionRepository promotionRepository;

  @Mock private PromotionMapper promotionMapper;

  @InjectMocks private PromotionService promotionService;

  private Promotion promotion;
  private PromotionResponse promotionResponse;
  private CreatePromotionRequest createRequest;

  @BeforeEach
  void setUp() {
    promotion = new Promotion();
    promotion.setId("promo-123");
    promotion.setName("Test Promotion");
    promotion.setCode("TEST20");
    promotion.setType(PromotionType.PERCENTAGE);
    promotion.setRestaurantId("restaurant-123");
    promotion.setStatus(PromotionStatus.DRAFT);

    DiscountVO discount = new DiscountVO();
    discount.setType(PromotionType.PERCENTAGE);
    discount.setValue(new BigDecimal("20"));
    promotion.setDiscount(discount);

    // Add schedule so validate() doesn't fail
    ScheduleVO schedule = new ScheduleVO();
    schedule.setStartDate(Instant.now().minus(1, ChronoUnit.DAYS));
    schedule.setEndDate(Instant.now().plus(7, ChronoUnit.DAYS));
    promotion.setSchedule(schedule);

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
            .build();
  }

  @Nested
  @DisplayName("Create Promotion Tests")
  class CreatePromotionTests {

    @Test
    @DisplayName("Should create promotion successfully")
    void shouldCreatePromotionSuccessfully() {
      when(promotionMapper.toEntity(any(CreatePromotionRequest.class))).thenReturn(promotion);
      when(promotionRepository.existsByCodeAndDeletedFalse(anyString())).thenReturn(false);
      when(promotionRepository.save(any(Promotion.class))).thenReturn(promotion);
      when(promotionMapper.toResponse(any(Promotion.class))).thenReturn(promotionResponse);

      PromotionResponse result = promotionService.createPromotion(createRequest);

      assertThat(result).isNotNull();
      assertThat(result.getCode()).isEqualTo("TEST20");
      verify(promotionRepository).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Should throw when code already exists")
    void shouldThrowWhenCodeAlreadyExists() {
      when(promotionMapper.toEntity(any(CreatePromotionRequest.class))).thenReturn(promotion);
      when(promotionRepository.existsByCodeAndDeletedFalse(anyString())).thenReturn(true);

      assertThatThrownBy(() -> promotionService.createPromotion(createRequest))
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("already exists");

      verify(promotionRepository, never()).save(any());
    }
  }

  @Nested
  @DisplayName("Get Promotion Tests")
  class GetPromotionTests {

    @Test
    @DisplayName("Should get promotion by ID")
    void shouldGetPromotionById() {
      when(promotionRepository.findByIdAndDeletedFalse("promo-123"))
          .thenReturn(Optional.of(promotion));
      when(promotionMapper.toResponse(promotion)).thenReturn(promotionResponse);

      PromotionResponse result = promotionService.getPromotion("promo-123");

      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo("promo-123");
    }

    @Test
    @DisplayName("Should throw when promotion not found")
    void shouldThrowWhenPromotionNotFound() {
      when(promotionRepository.findByIdAndDeletedFalse("invalid-id"))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> promotionService.getPromotion("invalid-id"))
          .isInstanceOf(PromotionNotFoundException.class);
    }
  }

  @Nested
  @DisplayName("Get All Promotions Tests")
  class GetAllPromotionsTests {

    @Test
    @DisplayName("Should get all promotions with pagination")
    void shouldGetAllPromotionsWithPagination() {
      Pageable pageable = PageRequest.of(0, 10);
      Page<Promotion> promotionPage = new PageImpl<>(List.of(promotion), pageable, 1);

      when(promotionRepository.findByDeletedFalse(pageable)).thenReturn(promotionPage);
      when(promotionMapper.toResponse(promotion)).thenReturn(promotionResponse);

      Page<PromotionResponse> result = promotionService.getAllPromotions(pageable);

      assertThat(result).isNotNull();
      assertThat(result.getTotalElements()).isEqualTo(1);
    }
  }

  @Nested
  @DisplayName("Status Change Tests")
  class StatusChangeTests {

    @Test
    @DisplayName("Should activate promotion")
    void shouldActivatePromotion() {
      when(promotionRepository.findByIdAndDeletedFalse("promo-123"))
          .thenReturn(Optional.of(promotion));
      when(promotionRepository.save(any(Promotion.class))).thenReturn(promotion);
      when(promotionMapper.toResponse(any(Promotion.class))).thenReturn(promotionResponse);

      PromotionResponse result = promotionService.activatePromotion("promo-123");

      assertThat(result).isNotNull();
      verify(promotionRepository).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Should pause promotion")
    void shouldPausePromotion() {
      promotion.setStatus(PromotionStatus.ACTIVE);
      when(promotionRepository.findByIdAndDeletedFalse("promo-123"))
          .thenReturn(Optional.of(promotion));
      when(promotionRepository.save(any(Promotion.class))).thenReturn(promotion);
      when(promotionMapper.toResponse(any(Promotion.class))).thenReturn(promotionResponse);

      PromotionResponse result = promotionService.pausePromotion("promo-123");

      assertThat(result).isNotNull();
      verify(promotionRepository).save(any(Promotion.class));
    }
  }

  @Nested
  @DisplayName("Delete Promotion Tests")
  class DeletePromotionTests {

    @Test
    @DisplayName("Should delete promotion")
    void shouldDeletePromotion() {
      when(promotionRepository.findByIdAndDeletedFalse("promo-123"))
          .thenReturn(Optional.of(promotion));
      when(promotionRepository.save(any(Promotion.class))).thenReturn(promotion);

      promotionService.deletePromotion("promo-123");

      verify(promotionRepository).save(any(Promotion.class));
    }

    @Test
    @DisplayName("Should throw when deleting non-existent promotion")
    void shouldThrowWhenDeletingNonExistentPromotion() {
      when(promotionRepository.findByIdAndDeletedFalse("invalid-id"))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> promotionService.deletePromotion("invalid-id"))
          .isInstanceOf(PromotionNotFoundException.class);
    }
  }
}
