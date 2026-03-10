package com.foodai.menu.mapper;

import com.foodai.menu.domain.model.MenuCategory;
import com.foodai.menu.dto.request.CreateMenuCategoryRequest;
import com.foodai.menu.dto.response.MenuCategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

/**
 * MapStruct mapper for MenuCategory entity and DTOs.
 *
 * @author FoodAI Team
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MenuCategoryMapper {

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
  MenuCategory toEntity(CreateMenuCategoryRequest request);

  /**
   * Maps domain entity to response DTO.
   *
   * @param entity the domain entity
   * @return the response DTO
   */
  MenuCategoryResponse toResponse(MenuCategory entity);

  /**
   * Maps list of entities to list of responses.
   *
   * @param entities list of domain entities
   * @return list of response DTOs
   */
  List<MenuCategoryResponse> toResponseList(List<MenuCategory> entities);
}

