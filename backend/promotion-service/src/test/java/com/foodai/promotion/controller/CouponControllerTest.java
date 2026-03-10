package com.foodai.promotion.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.foodai.promotion.domain.model.PromotionType;
import com.foodai.promotion.dto.request.CreateCouponRequest;
import com.foodai.promotion.dto.request.ValidateCouponRequest;
import com.foodai.promotion.dto.response.CouponResponse;
import com.foodai.promotion.dto.response.CouponValidationResponse;
import com.foodai.promotion.exception.GlobalExceptionHandler;
import com.foodai.promotion.service.CouponService;
import java.math.BigDecimal;
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
 * Unit tests for CouponController.
 *
 * @author FoodAI Team
 */
@ExtendWith(MockitoExtension.class)
class CouponControllerTest {

  private MockMvc mockMvc;

  private ObjectMapper objectMapper;

  @Mock private CouponService couponService;

  @InjectMocks private CouponController couponController;

  private CouponResponse couponResponse;
  private CreateCouponRequest createRequest;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    mockMvc =
        MockMvcBuilders.standaloneSetup(couponController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();

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
  @DisplayName("Create Coupon Endpoint Tests")
  class CreateCouponEndpointTests {

    @Test
    @DisplayName("Should create coupon successfully")
    void shouldCreateCouponSuccessfully() throws Exception {
      when(couponService.createCoupon(any(CreateCouponRequest.class))).thenReturn(couponResponse);

      mockMvc
          .perform(
              post("/api/v1/coupons")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(createRequest)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.id").value("coupon-123"))
          .andExpect(jsonPath("$.data.code").value("SAVE10"));
    }
  }

  @Nested
  @DisplayName("Get Coupon Endpoint Tests")
  class GetCouponEndpointTests {

    @Test
    @DisplayName("Should get coupon by ID")
    void shouldGetCouponById() throws Exception {
      when(couponService.getCoupon("coupon-123")).thenReturn(couponResponse);

      mockMvc
          .perform(get("/api/v1/coupons/coupon-123"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.id").value("coupon-123"));
    }

    @Test
    @DisplayName("Should get coupon by code")
    void shouldGetCouponByCode() throws Exception {
      when(couponService.getCouponByCode("SAVE10")).thenReturn(couponResponse);

      mockMvc
          .perform(get("/api/v1/coupons/code/SAVE10"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.code").value("SAVE10"));
    }
  }

  @Nested
  @DisplayName("Get All Coupons Endpoint Tests")
  class GetAllCouponsEndpointTests {

    @Test
    @DisplayName("Should get all coupons with pagination")
    void shouldGetAllCouponsWithPagination() throws Exception {
      Pageable pageable = PageRequest.of(0, 20);
      when(couponService.getAllCoupons(any(Pageable.class)))
          .thenReturn(new PageImpl<>(List.of(couponResponse), pageable, 1));

      mockMvc
          .perform(get("/api/v1/coupons"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data.content[0].id").value("coupon-123"));
    }

    @Test
    @DisplayName("Should get platform-wide coupons")
    void shouldGetPlatformWideCoupons() throws Exception {
      when(couponService.getPlatformWideCoupons()).thenReturn(List.of(couponResponse));

      mockMvc
          .perform(get("/api/v1/coupons/platform"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true))
          .andExpect(jsonPath("$.data[0].id").value("coupon-123"));
    }
  }

  @Nested
  @DisplayName("Status Change Endpoint Tests")
  class StatusChangeEndpointTests {

    @Test
    @DisplayName("Should activate coupon")
    void shouldActivateCoupon() throws Exception {
      when(couponService.activateCoupon("coupon-123")).thenReturn(couponResponse);

      mockMvc
          .perform(put("/api/v1/coupons/coupon-123/activate"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should deactivate coupon")
    void shouldDeactivateCoupon() throws Exception {
      when(couponService.deactivateCoupon("coupon-123")).thenReturn(couponResponse);

      mockMvc
          .perform(put("/api/v1/coupons/coupon-123/deactivate"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true));
    }
  }

  @Nested
  @DisplayName("Delete Coupon Endpoint Tests")
  class DeleteCouponEndpointTests {

    @Test
    @DisplayName("Should delete coupon")
    void shouldDeleteCoupon() throws Exception {
      doNothing().when(couponService).deleteCoupon("coupon-123");

      mockMvc
          .perform(delete("/api/v1/coupons/coupon-123"))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.success").value(true));
    }
  }
}
