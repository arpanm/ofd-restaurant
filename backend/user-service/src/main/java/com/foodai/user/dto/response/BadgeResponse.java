package com.foodai.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for Badge.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Badge response")
public class BadgeResponse {

    @Schema(description = "Badge ID")
    private String badgeId;

    @Schema(description = "Badge name")
    private String name;

    @Schema(description = "Badge description")
    private String description;

    @Schema(description = "Icon URL")
    private String iconUrl;

    @Schema(description = "Badge tier")
    private String badgeTier;

    @Schema(description = "Category")
    private String category;

    @Schema(description = "When earned")
    private Instant earnedAt;

    @Schema(description = "Is displayed on profile")
    private boolean displayed;

    @Schema(description = "Rarity")
    private String rarity;
}

