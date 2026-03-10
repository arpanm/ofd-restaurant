package com.foodai.menu.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new Menu Category.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new menu category")
public class CreateMenuCategoryRequest {

  @NotBlank(message = "Restaurant ID is required")
  @Schema(description = "Restaurant ID", example = "rest123", required = true)
  private String restaurantId;

  @NotBlank(message = "Category name is required")
  @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
  @Schema(description = "Category name", example = "Main Course", required = true)
  private String name;

  @Size(max = 200, message = "Description cannot exceed 200 characters")
  @Schema(description = "Category description", example = "Traditional main course dishes")
  private String description;

  @Schema(description = "Icon URL", example = "https://images.foodai.com/icon-main-course.png")
  private String iconUrl;

  @Min(value = 0, message = "Display order must be non-negative")
  @Schema(description = "Display order", example = "1")
  private Integer displayOrder;

  @Schema(description = "Active status", example = "true", defaultValue = "true")
  private boolean active = true;
}

