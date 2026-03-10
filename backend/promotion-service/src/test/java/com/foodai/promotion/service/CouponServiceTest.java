package com.foodai.promotion.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.foodai.promotion.domain.model.Coupon;
import com.foodai.promotion.domain.model.PromotionType;
import com.foodai.promotion.domain.repository.CouponRepository;
import com.foodai.promotion.domain.repository.CouponUsageRepository;
import com.foodai.promotion.dto.request.CreateCouponRequest;
import com.foodai.promotion.dto.request.ValidateCouponRequest;
import com.foodai.promotion.dto.response.CouponResponse;
import com.foodai.promotion.dto.response.CouponValidationResponse;
import com.foodai.promotion.exception.CouponNotFoundException;
import com.foodai.promotion.mapper.CouponMapper;
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
 * Unit tests for CouponService.
 *
 * @author FoodAI Team
 */
@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

  @Mock private CouponRepository couponRepository;

  @Mock private CouponUsageRepository couponUsageRepository;

  @Mock private CouponMapper couponMapper;

  @InjectMocks private CouponService couponService;

  private Coupon coupon;
  private CouponResponse couponResponse;
  private CreateCouponRequest createRequest;

  @BeforeEach
  void setUp() {
    coupon = new Coupon();
    coupon.setId("coupon-123");
    coupon.setCode("SAVE10");
    coupon.setDescription("Save 10%");
    coupon.setType(PromotionType.PERCENTAGE);
    coupon.setValue(new BigDecimal("10"));
    coupon.setRestaurantId("restaurant-123");
    coupon.setActive(true);
    coupon.setUsageCount(0);
    coupon.setValidFrom(Instant.now().minus(1, ChronoUnit.DAYS));
    coupon.setValidUntil(Instant.now().plus(30, ChronoUnit.DAYS));

    couponResponse =
        CouponResponse.builder()
            .id("coupon-123")
            .code("SAVE10")
            .description("Save 10%")
            .type(PromotionType.PERCENTAGE)
            .value(new BigDecimal("10"))
            .active(true)
            .build();

    createRequest =
        CreateCouponRequest.builder()
            .code("SAVE10")
            .description("Save 10%")
            .type(PromotionType.PERCENTAGE)
            .value(new BigDecimal("10"))
            .restaurantId("restaurant-123")
            .build();
  }

  @Nested
  @DisplayName("Create Coupon Tests")
  class CreateCouponTests {

    @Test
    @DisplayName("Should create coupon successfully")
    void shouldCreateCouponSuccessfully() {
      when(couponRepository.existsByCodeIgnoreCaseAndDeletedFalse(anyString())).thenReturn(false);
      when(couponMapper.toEntity(any(CreateCouponRequest.class))).thenReturn(coupon);
      when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);
      when(couponMapper.toResponse(any(Coupon.class))).thenReturn(couponResponse);

      CouponResponse result = couponService.createCoupon(createRequest);

      assertThat(result).isNotNull();
      assertThat(result.getCode()).isEqualTo("SAVE10");
      verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    @DisplayName("Should throw when code already exists")
    void shouldThrowWhenCodeAlreadyExists() {
      when(couponRepository.existsByCodeIgnoreCaseAndDeletedFalse(anyString())).thenReturn(true);

      assertThatThrownBy(() -> couponService.createCoupon(createRequest))
          .isInstanceOf(IllegalStateException.class)
          .hasMessageContaining("already exists");
    }
  }

  @Nested
  @DisplayName("Get Coupon Tests")
  class GetCouponTests {

    @Test
    @DisplayName("Should get coupon by ID")
    void shouldGetCouponById() {
      when(couponRepository.findByIdAndDeletedFalse("coupon-123"))
          .thenReturn(Optional.of(coupon));
      when(couponMapper.toResponse(coupon)).thenReturn(couponResponse);

      CouponResponse result = couponService.getCoupon("coupon-123");

      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo("coupon-123");
    }

    @Test
    @DisplayName("Should throw when coupon not found")
    void shouldThrowWhenCouponNotFound() {
      when(couponRepository.findByIdAndDeletedFalse("invalid-id"))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> couponService.getCoupon("invalid-id"))
          .isInstanceOf(CouponNotFoundException.class);
    }
  }

  @Nested
  @DisplayName("Get All Coupons Tests")
  class GetAllCouponsTests {

    @Test
    @DisplayName("Should get all coupons with pagination")
    void shouldGetAllCouponsWithPagination() {
      Pageable pageable = PageRequest.of(0, 10);
      Page<Coupon> couponPage = new PageImpl<>(List.of(coupon), pageable, 1);

      when(couponRepository.findByDeletedFalse(pageable)).thenReturn(couponPage);
      when(couponMapper.toResponse(coupon)).thenReturn(couponResponse);

      Page<CouponResponse> result = couponService.getAllCoupons(pageable);

      assertThat(result).isNotNull();
      assertThat(result.getTotalElements()).isEqualTo(1);
    }
  }

  @Nested
  @DisplayName("Status Change Tests")
  class StatusChangeTests {

    @Test
    @DisplayName("Should activate coupon")
    void shouldActivateCoupon() {
      coupon.setActive(false);
      when(couponRepository.findByIdAndDeletedFalse("coupon-123"))
          .thenReturn(Optional.of(coupon));
      when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);
      when(couponMapper.toResponse(any(Coupon.class))).thenReturn(couponResponse);

      CouponResponse result = couponService.activateCoupon("coupon-123");

      assertThat(result).isNotNull();
      verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    @DisplayName("Should deactivate coupon")
    void shouldDeactivateCoupon() {
      when(couponRepository.findByIdAndDeletedFalse("coupon-123"))
          .thenReturn(Optional.of(coupon));
      when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);
      when(couponMapper.toResponse(any(Coupon.class))).thenReturn(couponResponse);

      CouponResponse result = couponService.deactivateCoupon("coupon-123");

      assertThat(result).isNotNull();
      verify(couponRepository).save(any(Coupon.class));
    }
  }

  @Nested
  @DisplayName("Delete Coupon Tests")
  class DeleteCouponTests {

    @Test
    @DisplayName("Should delete coupon")
    void shouldDeleteCoupon() {
      when(couponRepository.findByIdAndDeletedFalse("coupon-123"))
          .thenReturn(Optional.of(coupon));
      when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

      couponService.deleteCoupon("coupon-123");

      verify(couponRepository).save(any(Coupon.class));
    }

    @Test
    @DisplayName("Should throw when deleting non-existent coupon")
    void shouldThrowWhenDeletingNonExistentCoupon() {
      when(couponRepository.findByIdAndDeletedFalse("invalid-id"))
          .thenReturn(Optional.empty());

      assertThatThrownBy(() -> couponService.deleteCoupon("invalid-id"))
          .isInstanceOf(CouponNotFoundException.class);
    }
  }
}
