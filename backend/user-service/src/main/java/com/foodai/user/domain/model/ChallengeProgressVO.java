package com.foodai.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Value Object representing progress on an active challenge.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeProgressVO {

    /**
     * Challenge definition ID
     */
    private String challengeId;

    /**
     * Challenge name
     */
    private String name;

    /**
     * Challenge description
     */
    private String description;

    /**
     * Challenge icon URL
     */
    private String iconUrl;

    /**
     * Target count to complete the challenge
     */
    private Integer targetCount;

    /**
     * Current progress count
     */
    @Builder.Default
    private Integer currentCount = 0;

    /**
     * Challenge start date
     */
    private Instant startDate;

    /**
     * Challenge end date
     */
    private Instant endDate;

    /**
     * Points awarded on completion
     */
    private Long pointsReward;

    /**
     * Badge awarded on completion (if any)
     */
    private String badgeReward;

    /**
     * Challenge status
     */
    @Builder.Default
    private ChallengeStatus status = ChallengeStatus.IN_PROGRESS;

    /**
     * When the challenge was completed (if completed)
     */
    private Instant completedAt;

    /**
     * Gets progress percentage.
     *
     * @return progress as percentage (0-100)
     */
    public int getProgressPercentage() {
        if (targetCount == null || targetCount == 0) {
            return 0;
        }
        return Math.min(100, (currentCount * 100) / targetCount);
    }

    /**
     * Checks if challenge is expired.
     *
     * @return true if expired
     */
    public boolean isExpired() {
        return endDate != null && Instant.now().isAfter(endDate);
    }

    /**
     * Increments progress.
     *
     * @param increment the amount to increment
     */
    public void incrementProgress(int increment) {
        this.currentCount += increment;
        if (this.currentCount >= this.targetCount) {
            this.status = ChallengeStatus.COMPLETED;
            this.completedAt = Instant.now();
        }
    }
}

