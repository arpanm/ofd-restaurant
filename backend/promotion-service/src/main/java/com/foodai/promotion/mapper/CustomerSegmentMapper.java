package com.foodai.promotion.mapper;

import com.foodai.promotion.domain.model.CustomerSegment;
import com.foodai.promotion.domain.model.SegmentCriteriaVO;
import com.foodai.promotion.dto.request.CreateSegmentRequest;
import com.foodai.promotion.dto.response.CustomerSegmentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

/**
 * MapStruct mapper for CustomerSegment entity and DTOs.
 *
 * @author FoodAI Team
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerSegmentMapper {

  /**
   * Maps create request to entity.
   *
   * @param request the create request
   * @return the segment entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deleted", constant = "false")
  @Mapping(target = "active", constant = "true")
  @Mapping(target = "custom", constant = "true")
  @Mapping(target = "aiSuggested", constant = "false")
  @Mapping(target = "customerCount", constant = "0")
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "lastRefreshed", ignore = true)
  @Mapping(target = "criteria", source = "request", qualifiedByName = "toCriteriaVO")
  CustomerSegment toEntity(CreateSegmentRequest request);

  /**
   * Maps entity to response.
   *
   * @param entity the segment entity
   * @return the response DTO
   */
  @Mapping(target = "criteria", source = "criteria", qualifiedByName = "toCriteriaResponse")
  CustomerSegmentResponse toResponse(CustomerSegment entity);

  /**
   * Creates SegmentCriteriaVO from request.
   *
   * @param request the request
   * @return the criteria VO
   */
  @Named("toCriteriaVO")
  default SegmentCriteriaVO toCriteriaVO(CreateSegmentRequest request) {
    if (request == null) {
      return null;
    }
    return SegmentCriteriaVO.builder()
        .minOrderCount(request.getMinOrderCount())
        .maxOrderCount(request.getMaxOrderCount())
        .minTotalSpent(request.getMinTotalSpent())
        .maxTotalSpent(request.getMaxTotalSpent())
        .minAverageOrderValue(request.getMinAverageOrderValue())
        .maxAverageOrderValue(request.getMaxAverageOrderValue())
        .daysSinceLastOrder(request.getDaysSinceLastOrder())
        .favoriteCategories(request.getFavoriteCategories())
        .favoriteCuisines(request.getFavoriteCuisines())
        .minAge(request.getMinAge())
        .maxAge(request.getMaxAge())
        .cities(request.getCities())
        .loyaltyTiers(request.getLoyaltyTiers())
        .dietaryPreferences(request.getDietaryPreferences())
        .build();
  }

  /**
   * Maps criteria VO to response.
   *
   * @param criteria the criteria VO
   * @return the criteria response
   */
  @Named("toCriteriaResponse")
  default CustomerSegmentResponse.CriteriaResponse toCriteriaResponse(SegmentCriteriaVO criteria) {
    if (criteria == null) {
      return null;
    }
    return CustomerSegmentResponse.CriteriaResponse.builder()
        .minOrderCount(criteria.getMinOrderCount())
        .maxOrderCount(criteria.getMaxOrderCount())
        .minTotalSpent(criteria.getMinTotalSpent())
        .maxTotalSpent(criteria.getMaxTotalSpent())
        .minAverageOrderValue(criteria.getMinAverageOrderValue())
        .maxAverageOrderValue(criteria.getMaxAverageOrderValue())
        .daysSinceLastOrder(criteria.getDaysSinceLastOrder())
        .favoriteCategories(criteria.getFavoriteCategories())
        .favoriteCuisines(criteria.getFavoriteCuisines())
        .minAge(criteria.getMinAge())
        .maxAge(criteria.getMaxAge())
        .cities(criteria.getCities())
        .loyaltyTiers(criteria.getLoyaltyTiers())
        .build();
  }
}

