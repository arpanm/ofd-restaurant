package com.foodai.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Value Object representing a user's badge.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BadgeVO {

    /**
     * Badge definition ID
     */
    private String badgeId;

    /**
     * Badge name
     */
    private String name;

    /**
     * Badge description
     */
    private String description;

    /**
     * Badge icon URL
     */
    private String iconUrl;

    /**
     * Badge tier (bronze, silver, gold, etc.)
     */
    private String badgeTier;

    /**
     * Badge category (foodie, explorer, etc.)
     */
    private String category;

    /**
     * When the badge was earned
     */
    private Instant earnedAt;

    /**
     * Whether this badge is currently displayed on profile
     */
    @Builder.Default
    private boolean displayed = false;

    /**
     * Badge rarity (common, rare, epic, legendary)
     */
    private String rarity;
}

