package com.foodai.promotion.mapper;

import com.foodai.promotion.domain.model.Coupon;
import com.foodai.promotion.dto.request.CreateCouponRequest;
import com.foodai.promotion.dto.response.CouponResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

/**
 * MapStruct mapper for Coupon entity and DTOs.
 *
 * @author FoodAI Team
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CouponMapper {

  /**
   * Maps create request to entity.
   *
   * @param request the create request
   * @return the coupon entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deleted", constant = "false")
  @Mapping(target = "active", constant = "true")
  @Mapping(target = "currentUsage", constant = "0")
  @Mapping(target = "usedByUsers", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "code", source = "code", qualifiedByName = "toUpperCase")
  Coupon toEntity(CreateCouponRequest request);

  /**
   * Maps entity to response.
   *
   * @param entity the coupon entity
   * @return the response DTO
   */
  @Mapping(target = "remainingUsage", source = "entity", qualifiedByName = "getRemainingUsage")
  @Mapping(target = "expired", source = "entity", qualifiedByName = "isExpired")
  CouponResponse toResponse(Coupon entity);

  /**
   * Converts code to uppercase.
   *
   * @param code the code
   * @return uppercase code
   */
  @Named("toUpperCase")
  default String toUpperCase(String code) {
    return code != null ? code.toUpperCase() : null;
  }

  /**
   * Gets remaining usage from coupon.
   *
   * @param coupon the coupon
   * @return remaining usage
   */
  @Named("getRemainingUsage")
  default Integer getRemainingUsage(Coupon coupon) {
    return coupon != null ? coupon.getRemainingUsage() : null;
  }

  /**
   * Checks if coupon is expired.
   *
   * @param coupon the coupon
   * @return true if expired
   */
  @Named("isExpired")
  default boolean isExpired(Coupon coupon) {
    return coupon != null && coupon.isExpired();
  }
}

