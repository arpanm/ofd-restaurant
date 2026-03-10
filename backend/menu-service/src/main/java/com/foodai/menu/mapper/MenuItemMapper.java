package com.foodai.menu.mapper;

import com.foodai.menu.domain.model.MenuItem;
import com.foodai.menu.dto.request.CreateMenuItemRequest;
import com.foodai.menu.dto.request.UpdateMenuItemRequest;
import com.foodai.menu.dto.response.MenuItemDetailResponse;
import com.foodai.menu.dto.response.MenuItemResponse;
import org.mapstruct.*;

import java.util.List;

/**
 * MapStruct mapper for MenuItem entity and DTOs.
 *
 * @author FoodAI Team
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MenuItemMapper {

  /**
   * Maps create request DTO to domain entity.
   *
   * @param request the create request
   * @return the domain entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deleted", constant = "false")
  @Mapping(target = "totalOrders", constant = "0")
  @Mapping(target = "reviewCount", constant = "0")
  @Mapping(target = "averageRating", expression = "java(java.math.BigDecimal.ZERO)")
  @Mapping(target = "status", expression = "java(com.foodai.menu.domain.model.MenuItemStatus.ACTIVE)")
  MenuItem toEntity(CreateMenuItemRequest request);

  /**
   * Updates existing entity from update request.
   *
   * @param request the update request
   * @param entity the existing entity to update
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "restaurantId", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  @Mapping(target = "totalOrders", ignore = true)
  @Mapping(target = "reviewCount", ignore = true)
  @Mapping(target = "averageRating", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromRequest(UpdateMenuItemRequest request, @MappingTarget MenuItem entity);

  /**
   * Maps domain entity to summary response DTO.
   *
   * @param entity the domain entity
   * @return the response DTO
   */
  @Mapping(target = "primaryImage", expression = "java(getPrimaryImage(entity))")
  MenuItemResponse toResponse(MenuItem entity);

  /**
   * Maps domain entity to detailed response DTO.
   *
   * @param entity the domain entity
   * @return the detailed response DTO
   */
  MenuItemDetailResponse toDetailResponse(MenuItem entity);

  /**
   * Maps list of entities to list of responses.
   *
   * @param entities list of domain entities
   * @return list of response DTOs
   */
  List<MenuItemResponse> toResponseList(List<MenuItem> entities);

  /**
   * Helper method to get primary image from images list.
   *
   * @param entity the menu item
   * @return primary image URL or null
   */
  default String getPrimaryImage(MenuItem entity) {
    if (entity.getImages() != null && !entity.getImages().isEmpty()) {
      return entity.getImages().get(0);
    }
    return null;
  }
}

