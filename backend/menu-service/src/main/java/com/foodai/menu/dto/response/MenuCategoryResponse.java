package com.foodai.menu.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for Menu Category.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Menu category response")
public class MenuCategoryResponse {

  @Schema(description = "Unique identifier", example = "cat123")
  private String id;

  @Schema(description = "Restaurant ID", example = "rest123")
  private String restaurantId;

  @Schema(description = "Category name", example = "Main Course")
  private String name;

  @Schema(description = "Description", example = "Traditional main course dishes")
  private String description;

  @Schema(description = "Icon URL", example = "https://images.foodai.com/icon-main-course.png")
  private String iconUrl;

  @Schema(description = "Display order", example = "1")
  private Integer displayOrder;

  @Schema(description = "Active status", example = "true")
  private boolean active;

  @Schema(description = "Creation timestamp")
  private Instant createdAt;

  @Schema(description = "Last update timestamp")
  private Instant updatedAt;
}

