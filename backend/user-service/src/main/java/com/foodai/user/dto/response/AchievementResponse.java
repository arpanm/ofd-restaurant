package com.foodai.user.dto.response;

import com.foodai.user.domain.model.AchievementCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for Achievement.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Achievement response")
public class AchievementResponse {

    @Schema(description = "Achievement ID")
    private String achievementId;

    @Schema(description = "Achievement name")
    private String name;

    @Schema(description = "Achievement description")
    private String description;

    @Schema(description = "Icon URL")
    private String iconUrl;

    @Schema(description = "Category")
    private AchievementCategory category;

    @Schema(description = "Points awarded")
    private Long pointsAwarded;

    @Schema(description = "When unlocked")
    private Instant unlockedAt;
}

