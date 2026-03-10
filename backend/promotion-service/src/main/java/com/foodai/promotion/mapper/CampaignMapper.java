package com.foodai.promotion.mapper;

import com.foodai.promotion.domain.model.Campaign;
import com.foodai.promotion.domain.model.CampaignStatus;
import com.foodai.promotion.domain.model.ScheduleVO;
import com.foodai.promotion.dto.request.CreateCampaignRequest;
import com.foodai.promotion.dto.response.CampaignResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

/**
 * MapStruct mapper for Campaign entity and DTOs.
 *
 * @author FoodAI Team
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CampaignMapper {

  /**
   * Maps create request to entity.
   *
   * @param request the create request
   * @return the campaign entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deleted", constant = "false")
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "status", constant = "DRAFT")
  @Mapping(target = "spent", ignore = true)
  @Mapping(target = "performance", ignore = true)
  @Mapping(target = "aiSuggested", constant = "false")
  @Mapping(target = "schedule", source = "request", qualifiedByName = "toScheduleVO")
  @Mapping(target = "creatives", source = "creatives", qualifiedByName = "toCreatives")
  Campaign toEntity(CreateCampaignRequest request);

  /**
   * Maps entity to response.
   *
   * @param entity the campaign entity
   * @return the response DTO
   */
  @Mapping(target = "startDate", source = "schedule.startDate")
  @Mapping(target = "endDate", source = "schedule.endDate")
  @Mapping(target = "startTime", source = "schedule.startTime")
  @Mapping(target = "frequency", source = "schedule.frequency")
  @Mapping(target = "daysOfWeek", source = "schedule.daysOfWeek")
  @Mapping(target = "creatives", source = "creatives", qualifiedByName = "toCreativeResponses")
  @Mapping(target = "performance", source = "performance", qualifiedByName = "toPerformanceResponse")
  CampaignResponse toResponse(Campaign entity);

  /**
   * Creates ScheduleVO from request.
   *
   * @param request the request
   * @return the schedule VO
   */
  @Named("toScheduleVO")
  default ScheduleVO toScheduleVO(CreateCampaignRequest request) {
    if (request == null) {
      return null;
    }
    return ScheduleVO.builder()
        .startDate(request.getStartDate())
        .endDate(request.getEndDate())
        .startTime(request.getStartTime())
        .frequency(request.getFrequency())
        .daysOfWeek(request.getDaysOfWeek())
        .build();
  }

  /**
   * Maps creative requests to entities.
   *
   * @param creatives the creative requests
   * @return the creative entities
   */
  @Named("toCreatives")
  default List<Campaign.CampaignCreative> toCreatives(
      List<CreateCampaignRequest.CampaignCreativeRequest> creatives) {
    if (creatives == null) {
      return null;
    }
    return creatives.stream()
        .map(
            c ->
                Campaign.CampaignCreative.builder()
                    .id(java.util.UUID.randomUUID().toString())
                    .type(c.getType())
                    .content(c.getContent())
                    .mediaUrl(c.getMediaUrl())
                    .aiGenerated(false)
                    .build())
        .collect(Collectors.toList());
  }

  /**
   * Maps creative entities to responses.
   *
   * @param creatives the creative entities
   * @return the creative responses
   */
  @Named("toCreativeResponses")
  default List<CampaignResponse.CreativeResponse> toCreativeResponses(
      List<Campaign.CampaignCreative> creatives) {
    if (creatives == null) {
      return null;
    }
    return creatives.stream()
        .map(
            c ->
                CampaignResponse.CreativeResponse.builder()
                    .id(c.getId())
                    .type(c.getType())
                    .content(c.getContent())
                    .mediaUrl(c.getMediaUrl())
                    .aiGenerated(c.isAiGenerated())
                    .build())
        .collect(Collectors.toList());
  }

  /**
   * Maps performance entity to response.
   *
   * @param performance the performance entity
   * @return the performance response
   */
  @Named("toPerformanceResponse")
  default CampaignResponse.PerformanceResponse toPerformanceResponse(
      Campaign.CampaignPerformance performance) {
    if (performance == null) {
      return null;
    }
    return CampaignResponse.PerformanceResponse.builder()
        .sent(performance.getSent())
        .delivered(performance.getDelivered())
        .opened(performance.getOpened())
        .clicked(performance.getClicked())
        .converted(performance.getConverted())
        .revenue(performance.getRevenue())
        .cost(performance.getCost())
        .deliveryRate(performance.getDeliveryRate())
        .openRate(performance.getOpenRate())
        .clickRate(performance.getClickRate())
        .conversionRate(performance.getConversionRate())
        .roi(performance.getRoi())
        .build();
  }
}

