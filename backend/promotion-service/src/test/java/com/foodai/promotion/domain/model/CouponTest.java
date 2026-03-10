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
 * Unit tests for Coupon domain model.
 *
 * @author FoodAI Team
 */
class CouponTest {

  private Coupon coupon;

  @BeforeEach
  void setUp() {
    coupon = new Coupon();
    coupon.setId("coupon-123");
    coupon.setCode("SAVE10");
    coupon.setDescription("Save 10% on your order");
    coupon.setType(PromotionType.PERCENTAGE);
    coupon.setValue(new BigDecimal("10"));
    coupon.setRestaurantId("restaurant-123");
    coupon.setActive(true);
    coupon.setUsageCount(0);
  }

  @Nested
  @DisplayName("Discount Calculation Tests")
  class DiscountCalculationTests {

    @Test
    @DisplayName("Should calculate percentage discount")
    void shouldCalculatePercentageDiscount() {
      BigDecimal orderValue = new BigDecimal("100");
      BigDecimal discount = coupon.calculateDiscount(orderValue);
      assertThat(discount).isEqualByComparingTo(new BigDecimal("10"));
    }

    @Test
    @DisplayName("Should calculate flat discount")
    void shouldCalculateFlatDiscount() {
      coupon.setType(PromotionType.FIXED_AMOUNT);
      coupon.setValue(new BigDecimal("25"));

      BigDecimal orderValue = new BigDecimal("100");
      BigDecimal discount = coupon.calculateDiscount(orderValue);
      assertThat(discount).isEqualByComparingTo(new BigDecimal("25"));
    }

    @Test
    @DisplayName("Should cap flat discount at order value")
    void shouldCapFlatDiscountAtOrderValue() {
      coupon.setType(PromotionType.FIXED_AMOUNT);
      coupon.setValue(new BigDecimal("150"));

      BigDecimal orderValue = new BigDecimal("100");
      BigDecimal discount = coupon.calculateDiscount(orderValue);
      assertThat(discount).isEqualByComparingTo(new BigDecimal("100"));
    }

    @Test
    @DisplayName("Should apply max discount cap")
    void shouldApplyMaxDiscountCap() {
      coupon.setValue(new BigDecimal("50"));
      coupon.setMaxDiscount(new BigDecimal("25"));

      BigDecimal orderValue = new BigDecimal("100");
      BigDecimal discount = coupon.calculateDiscount(orderValue);
      assertThat(discount).isEqualByComparingTo(new BigDecimal("25"));
    }
  }

  @Nested
  @DisplayName("Usage Tests")
  class UsageTests {

    @Test
    @DisplayName("Should record usage")
    void shouldRecordUsage() {
      coupon.recordUsage("user-123");
      assertThat(coupon.getUsageCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw when usage limit exceeded")
    void shouldThrowWhenUsageLimitExceeded() {
      coupon.setUsageLimit(10);
      coupon.setCurrentUsage(10);
      assertThatThrownBy(() -> coupon.recordUsage("user-123"))
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("limit");
    }
  }

  @Nested
  @DisplayName("Activation Tests")
  class ActivationTests {

    @Test
    @DisplayName("Should activate coupon")
    void shouldActivateCoupon() {
      coupon.setActive(false);
      coupon.activate();
      assertThat(coupon.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should deactivate coupon")
    void shouldDeactivateCoupon() {
      coupon.deactivate();
      assertThat(coupon.isActive()).isFalse();
    }
  }

  @Nested
  @DisplayName("Soft Delete Tests")
  class SoftDeleteTests {

    @Test
    @DisplayName("Should soft delete coupon")
    void shouldSoftDeleteCoupon() {
      coupon.delete();
      assertThat(coupon.isDeleted()).isTrue();
      assertThat(coupon.isActive()).isFalse();
    }
  }

  @Nested
  @DisplayName("First Order Tests")
  class FirstOrderTests {

    @Test
    @DisplayName("Should check first order only restriction")
    void shouldCheckFirstOrderOnlyRestriction() {
      coupon.setFirstOrderOnly(true);
      assertThat(coupon.isFirstOrderOnly()).isTrue();
    }

    @Test
    @DisplayName("Should check new users only restriction")
    void shouldCheckNewUsersOnlyRestriction() {
      coupon.setNewUsersOnly(true);
      assertThat(coupon.isNewUsersOnly()).isTrue();
    }
  }
}
