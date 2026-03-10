package com.foodai.promotion.mapper;

import com.foodai.promotion.domain.model.ApplicabilityVO;
import com.foodai.promotion.domain.model.DiscountVO;
import com.foodai.promotion.domain.model.Promotion;
import com.foodai.promotion.domain.model.PromotionStatus;
import com.foodai.promotion.domain.model.ScheduleVO;
import com.foodai.promotion.domain.model.UsageLimitVO;
import com.foodai.promotion.dto.request.CreatePromotionRequest;
import com.foodai.promotion.dto.response.PromotionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

/**
 * MapStruct mapper for Promotion entity and DTOs.
 *
 * @author FoodAI Team
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PromotionMapper {

  /**
   * Maps create request to entity.
   *
   * @param request the create request
   * @return the promotion entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deleted", constant = "false")
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "status", constant = "DRAFT")
  @Mapping(target = "campaignId", ignore = true)
  @Mapping(target = "discount", source = "request", qualifiedByName = "toDiscountVO")
  @Mapping(target = "schedule", source = "request", qualifiedByName = "toScheduleVO")
  @Mapping(target = "usageLimit", source = "request", qualifiedByName = "toUsageLimitVO")
  @Mapping(target = "applicability", source = "request", qualifiedByName = "toApplicabilityVO")
  Promotion toEntity(CreatePromotionRequest request);

  /**
   * Maps entity to response.
   *
   * @param entity the promotion entity
   * @return the response DTO
   */
  @Mapping(target = "discountValue", source = "discount.value")
  @Mapping(target = "minOrderValue", source = "discount.minOrderValue")
  @Mapping(target = "maxDiscount", source = "discount.maxDiscount")
  @Mapping(target = "startDate", source = "schedule.startDate")
  @Mapping(target = "endDate", source = "schedule.endDate")
  @Mapping(target = "startTime", source = "schedule.startTime")
  @Mapping(target = "endTime", source = "schedule.endTime")
  @Mapping(target = "daysOfWeek", source = "schedule.daysOfWeek")
  @Mapping(target = "totalUsageLimit", source = "usageLimit.totalLimit")
  @Mapping(target = "perUserLimit", source = "usageLimit.perUserLimit")
  @Mapping(target = "currentUsage", source = "usageLimit.currentUsage")
  @Mapping(target = "remainingUsage", source = "usageLimit", qualifiedByName = "getRemainingUsage")
  @Mapping(target = "usagePercentage", source = "usageLimit", qualifiedByName = "getUsagePercentage")
  @Mapping(target = "applicableRestaurants", source = "applicability.restaurantIds")
  @Mapping(target = "applicableMenuItems", source = "applicability.menuItemIds")
  @Mapping(target = "applicableCategories", source = "applicability.categories")
  PromotionResponse toResponse(Promotion entity);

  /**
   * Creates DiscountVO from request.
   *
   * @param request the create request
   * @return the discount VO
   */
  @Named("toDiscountVO")
  default DiscountVO toDiscountVO(CreatePromotionRequest request) {
    if (request == null) {
      return null;
    }
    return DiscountVO.builder()
        .type(request.getType())
        .value(request.getDiscountValue())
        .minOrderValue(request.getMinOrderValue())
        .maxDiscount(request.getMaxDiscount())
        .build();
  }

  /**
   * Creates ScheduleVO from request.
   *
   * @param request the create request
   * @return the schedule VO
   */
  @Named("toScheduleVO")
  default ScheduleVO toScheduleVO(CreatePromotionRequest request) {
    if (request == null) {
      return null;
    }
    return ScheduleVO.builder()
        .startDate(request.getStartDate())
        .endDate(request.getEndDate())
        .startTime(request.getStartTime())
        .endTime(request.getEndTime())
        .daysOfWeek(request.getDaysOfWeek())
        .build();
  }

  /**
   * Creates UsageLimitVO from request.
   *
   * @param request the create request
   * @return the usage limit VO
   */
  @Named("toUsageLimitVO")
  default UsageLimitVO toUsageLimitVO(CreatePromotionRequest request) {
    if (request == null) {
      return null;
    }
    return UsageLimitVO.builder()
        .totalLimit(request.getTotalUsageLimit())
        .perUserLimit(request.getPerUserLimit())
        .currentUsage(0)
        .build();
  }

  /**
   * Creates ApplicabilityVO from request.
   *
   * @param request the create request
   * @return the applicability VO
   */
  @Named("toApplicabilityVO")
  default ApplicabilityVO toApplicabilityVO(CreatePromotionRequest request) {
    if (request == null) {
      return null;
    }
    return ApplicabilityVO.builder()
        .restaurantIds(request.getApplicableRestaurants())
        .menuItemIds(request.getApplicableMenuItems())
        .categories(request.getApplicableCategories())
        .build();
  }

  /**
   * Gets remaining usage.
   *
   * @param usageLimit the usage limit
   * @return remaining usage
   */
  @Named("getRemainingUsage")
  default Integer getRemainingUsage(UsageLimitVO usageLimit) {
    return usageLimit != null ? usageLimit.getRemainingUsage() : null;
  }

  /**
   * Gets usage percentage.
   *
   * @param usageLimit the usage limit
   * @return usage percentage
   */
  @Named("getUsagePercentage")
  default double getUsagePercentage(UsageLimitVO usageLimit) {
    return usageLimit != null ? usageLimit.getUsagePercentage() : 0;
  }
}

