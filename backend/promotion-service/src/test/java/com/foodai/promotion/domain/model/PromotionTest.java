package com.foodai.promotion.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for Promotion domain model.
 *
 * @author FoodAI Team
 */
class PromotionTest {

  private Promotion promotion;

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

    ScheduleVO schedule = new ScheduleVO();
    schedule.setStartDate(Instant.now().minus(1, ChronoUnit.DAYS));
    schedule.setEndDate(Instant.now().plus(7, ChronoUnit.DAYS));
    promotion.setSchedule(schedule);
  }

  @Nested
  @DisplayName("Activation Tests")
  class ActivationTests {

    @Test
    @DisplayName("Should activate a draft promotion")
    void shouldActivateDraftPromotion() {
      promotion.activate();
      assertThat(promotion.getStatus()).isEqualTo(PromotionStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should activate a scheduled promotion")
    void shouldActivateScheduledPromotion() {
      promotion.setStatus(PromotionStatus.SCHEDULED);
      promotion.activate();
      assertThat(promotion.getStatus()).isEqualTo(PromotionStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should throw when activating ended promotion")
    void shouldThrowWhenActivatingEndedPromotion() {
      promotion.setStatus(PromotionStatus.ENDED);
      assertThatThrownBy(() -> promotion.activate())
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Cannot activate");
    }
  }

  @Nested
  @DisplayName("Pause Tests")
  class PauseTests {

    @Test
    @DisplayName("Should pause an active promotion")
    void shouldPauseActivePromotion() {
      promotion.setStatus(PromotionStatus.ACTIVE);
      promotion.pause();
      assertThat(promotion.getStatus()).isEqualTo(PromotionStatus.PAUSED);
    }

    @Test
    @DisplayName("Should throw when pausing non-active promotion")
    void shouldThrowWhenPausingNonActivePromotion() {
      assertThatThrownBy(() -> promotion.pause())
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("pause");
    }
  }

  @Nested
  @DisplayName("Resume Tests")
  class ResumeTests {

    @Test
    @DisplayName("Should resume a paused promotion")
    void shouldResumePausedPromotion() {
      promotion.setStatus(PromotionStatus.PAUSED);
      promotion.resume();
      assertThat(promotion.getStatus()).isEqualTo(PromotionStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should throw when resuming non-paused promotion")
    void shouldThrowWhenResumingNonPausedPromotion() {
      assertThatThrownBy(() -> promotion.resume())
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("resume");
    }
  }

  @Nested
  @DisplayName("End Tests")
  class EndTests {

    @Test
    @DisplayName("Should end an active promotion")
    void shouldEndActivePromotion() {
      promotion.setStatus(PromotionStatus.ACTIVE);
      promotion.end();
      assertThat(promotion.getStatus()).isEqualTo(PromotionStatus.ENDED);
    }

    @Test
    @DisplayName("Should end a paused promotion")
    void shouldEndPausedPromotion() {
      promotion.setStatus(PromotionStatus.PAUSED);
      promotion.end();
      assertThat(promotion.getStatus()).isEqualTo(PromotionStatus.ENDED);
    }
  }

  @Nested
  @DisplayName("Validation Tests")
  class ValidationTests {

    @Test
    @DisplayName("Should validate successfully with valid data")
    void shouldValidateSuccessfully() {
      promotion.validate(); // Should not throw
    }

    @Test
    @DisplayName("Should throw when name is missing")
    void shouldThrowWhenNameIsMissing() {
      promotion.setName(null);
      assertThatThrownBy(() -> promotion.validate())
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("name");
    }

    @Test
    @DisplayName("Should throw when type is missing")
    void shouldThrowWhenTypeIsMissing() {
      promotion.setType(null);
      assertThatThrownBy(() -> promotion.validate())
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("type");
    }

    @Test
    @DisplayName("Should throw when discount is missing")
    void shouldThrowWhenDiscountIsMissing() {
      promotion.setDiscount(null);
      assertThatThrownBy(() -> promotion.validate())
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Discount");
    }

    @Test
    @DisplayName("Should throw when schedule is missing")
    void shouldThrowWhenScheduleIsMissing() {
      promotion.setSchedule(null);
      assertThatThrownBy(() -> promotion.validate())
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("Schedule");
    }
  }

  @Nested
  @DisplayName("Soft Delete Tests")
  class SoftDeleteTests {

    @Test
    @DisplayName("Should soft delete promotion")
    void shouldSoftDeletePromotion() {
      promotion.delete();
      assertThat(promotion.isDeleted()).isTrue();
    }
  }

  @Nested
  @DisplayName("Can Be Used Tests")
  class CanBeUsedTests {

    @Test
    @DisplayName("Should return true when promotion is active and valid")
    void shouldReturnTrueWhenActiveAndValid() {
      promotion.setStatus(PromotionStatus.ACTIVE);
      assertThat(promotion.canBeUsed()).isTrue();
    }

    @Test
    @DisplayName("Should return false when promotion is deleted")
    void shouldReturnFalseWhenDeleted() {
      promotion.setStatus(PromotionStatus.ACTIVE);
      promotion.delete();
      assertThat(promotion.canBeUsed()).isFalse();
    }

    @Test
    @DisplayName("Should return false when promotion is not active")
    void shouldReturnFalseWhenNotActive() {
      promotion.setStatus(PromotionStatus.DRAFT);
      assertThat(promotion.canBeUsed()).isFalse();
    }
  }
}
