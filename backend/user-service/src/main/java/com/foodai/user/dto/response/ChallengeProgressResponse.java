package com.foodai.user.dto.response;

import com.foodai.user.domain.model.ChallengeStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for Challenge Progress.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Challenge progress response")
public class ChallengeProgressResponse {

    @Schema(description = "Challenge ID")
    private String challengeId;

    @Schema(description = "Challenge name")
    private String name;

    @Schema(description = "Challenge description")
    private String description;

    @Schema(description = "Icon URL")
    private String iconUrl;

    @Schema(description = "Target count")
    private Integer targetCount;

    @Schema(description = "Current progress")
    private Integer currentCount;

    @Schema(description = "Progress percentage")
    private Integer progressPercentage;

    @Schema(description = "Start date")
    private Instant startDate;

    @Schema(description = "End date")
    private Instant endDate;

    @Schema(description = "Points reward")
    private Long pointsReward;

    @Schema(description = "Badge reward")
    private String badgeReward;

    @Schema(description = "Challenge status")
    private ChallengeStatus status;

    @Schema(description = "Is expired")
    private boolean expired;

    @Schema(description = "Completed at")
    private Instant completedAt;
}

