package com.foodai.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Value Object representing an unlocked achievement.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementVO {

    /**
     * Achievement definition ID
     */
    private String achievementId;

    /**
     * Achievement name
     */
    private String name;

    /**
     * Achievement description
     */
    private String description;

    /**
     * Achievement icon URL
     */
    private String iconUrl;

    /**
     * Achievement category
     */
    private AchievementCategory category;

    /**
     * Points awarded for this achievement
     */
    private Long pointsAwarded;

    /**
     * When the achievement was unlocked
     */
    private Instant unlockedAt;

    /**
     * Progress percentage at unlock (for progressive achievements)
     */
    private Integer progressAtUnlock;
}

