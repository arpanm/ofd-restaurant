package com.foodai.user.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for LoyaltyAccount domain entity.
 */
class LoyaltyAccountTest {

    private LoyaltyAccount account;

    @BeforeEach
    void setUp() {
        account = LoyaltyAccount.builder()
            .id("loyalty-123")
            .userId("user-123")
            .currentPoints(1000L)
            .lifetimePointsEarned(1500L)
            .lifetimePointsRedeemed(500L)
            .lifetimePointsExpired(0L)
            .tier(LoyaltyTier.BRONZE)
            .tierPeriodPoints(1000L)
            .currentStreak(0)
            .longestStreak(0)
            .totalReferrals(0)
            .referralPointsEarned(0L)
            .achievements(new ArrayList<>())
            .badges(new ArrayList<>())
            .activeChallenges(new ArrayList<>())
            .status(LoyaltyAccountStatus.ACTIVE)
            .build();
    }

    @Nested
    @DisplayName("Earn Points Tests")
    class EarnPointsTests {

        @Test
        @DisplayName("Should earn points and update balances")
        void shouldEarnPointsAndUpdateBalances() {
            // Act
            account.earnPoints(500L, "order:123");

            // Assert
            assertThat(account.getCurrentPoints()).isEqualTo(1500L);
            assertThat(account.getLifetimePointsEarned()).isEqualTo(2000L);
            assertThat(account.getTierPeriodPoints()).isEqualTo(1500L);
        }

        @Test
        @DisplayName("Should upgrade tier when enough points earned")
        void shouldUpgradeTier_whenEnoughPointsEarned() {
            // Arrange - start at Bronze with 0 points
            account.setTier(LoyaltyTier.BRONZE);
            account.setTierPeriodPoints(0L);

            // Act - earn 1500 points (enough for Silver)
            account.earnPoints(1500L, "order:123");

            // Assert
            assertThat(account.getTier()).isEqualTo(LoyaltyTier.SILVER);
        }
    }

    @Nested
    @DisplayName("Redeem Points Tests")
    class RedeemPointsTests {

        @Test
        @DisplayName("Should redeem points successfully")
        void shouldRedeemPointsSuccessfully() {
            // Act
            boolean result = account.redeemPoints(500L);

            // Assert
            assertThat(result).isTrue();
            assertThat(account.getCurrentPoints()).isEqualTo(500L);
            assertThat(account.getLifetimePointsRedeemed()).isEqualTo(1000L);
        }

        @Test
        @DisplayName("Should fail to redeem when insufficient points")
        void shouldFailToRedeem_whenInsufficientPoints() {
            // Act
            boolean result = account.redeemPoints(2000L);

            // Assert
            assertThat(result).isFalse();
            assertThat(account.getCurrentPoints()).isEqualTo(1000L); // Unchanged
        }
    }

    @Nested
    @DisplayName("Expire Points Tests")
    class ExpirePointsTests {

        @Test
        @DisplayName("Should expire points")
        void shouldExpirePoints() {
            // Act
            account.expirePoints(300L);

            // Assert
            assertThat(account.getCurrentPoints()).isEqualTo(700L);
            assertThat(account.getLifetimePointsExpired()).isEqualTo(300L);
        }

        @Test
        @DisplayName("Should not go below zero when expiring")
        void shouldNotGoBelowZero_whenExpiring() {
            // Act
            account.expirePoints(2000L);

            // Assert
            assertThat(account.getCurrentPoints()).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("Streak Tests")
    class StreakTests {

        @Test
        @DisplayName("Should start streak with first order")
        void shouldStartStreak_withFirstOrder() {
            // Act
            account.updateStreak(Instant.now());

            // Assert
            assertThat(account.getCurrentStreak()).isEqualTo(1);
            assertThat(account.getLongestStreak()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should increment streak for consecutive day")
        void shouldIncrementStreak_forConsecutiveDay() {
            // Arrange
            Instant yesterday = Instant.now().minusSeconds(86400);
            account.setLastOrderDate(yesterday);
            account.setCurrentStreak(5);

            // Act
            account.updateStreak(Instant.now());

            // Assert
            assertThat(account.getCurrentStreak()).isEqualTo(6);
        }

        @Test
        @DisplayName("Should reset streak after gap")
        void shouldResetStreak_afterGap() {
            // Arrange
            Instant threeDaysAgo = Instant.now().minusSeconds(3 * 86400);
            account.setLastOrderDate(threeDaysAgo);
            account.setCurrentStreak(5);
            account.setLongestStreak(5);

            // Act
            account.updateStreak(Instant.now());

            // Assert
            assertThat(account.getCurrentStreak()).isEqualTo(1);
            assertThat(account.getLongestStreak()).isEqualTo(5); // Unchanged
        }

        @Test
        @DisplayName("Should update longest streak")
        void shouldUpdateLongestStreak() {
            // Arrange
            account.setCurrentStreak(10);
            account.setLongestStreak(10);
            Instant yesterday = Instant.now().minusSeconds(86400);
            account.setLastOrderDate(yesterday);

            // Act
            account.updateStreak(Instant.now());

            // Assert
            assertThat(account.getCurrentStreak()).isEqualTo(11);
            assertThat(account.getLongestStreak()).isEqualTo(11);
        }
    }

    @Nested
    @DisplayName("Achievement Tests")
    class AchievementTests {

        @Test
        @DisplayName("Should add achievement")
        void shouldAddAchievement() {
            // Arrange
            AchievementVO achievement = AchievementVO.builder()
                .achievementId("first-order")
                .name("First Order")
                .pointsAwarded(100L)
                .build();

            // Act
            account.addAchievement(achievement);

            // Assert
            assertThat(account.getAchievements()).hasSize(1);
            assertThat(account.getCurrentPoints()).isEqualTo(1100L); // 1000 + 100 bonus
        }

        @Test
        @DisplayName("Should not add duplicate achievement")
        void shouldNotAddDuplicateAchievement() {
            // Arrange
            AchievementVO achievement = AchievementVO.builder()
                .achievementId("first-order")
                .name("First Order")
                .pointsAwarded(100L)
                .build();

            // Act
            account.addAchievement(achievement);
            account.addAchievement(achievement);

            // Assert
            assertThat(account.getAchievements()).hasSize(1);
            assertThat(account.getCurrentPoints()).isEqualTo(1100L); // Only 100 added once
        }
    }

    @Nested
    @DisplayName("Badge Tests")
    class BadgeTests {

        @Test
        @DisplayName("Should add badge")
        void shouldAddBadge() {
            // Arrange
            BadgeVO badge = BadgeVO.builder()
                .badgeId("foodie")
                .name("Foodie")
                .build();

            // Act
            account.addBadge(badge);

            // Assert
            assertThat(account.getBadges()).hasSize(1);
        }

        @Test
        @DisplayName("Should not add duplicate badge")
        void shouldNotAddDuplicateBadge() {
            // Arrange
            BadgeVO badge = BadgeVO.builder()
                .badgeId("foodie")
                .name("Foodie")
                .build();

            // Act
            account.addBadge(badge);
            account.addBadge(badge);

            // Assert
            assertThat(account.getBadges()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Referral Tests")
    class ReferralTests {

        @Test
        @DisplayName("Should record referral")
        void shouldRecordReferral() {
            // Act
            account.recordReferral(500L);

            // Assert
            assertThat(account.getTotalReferrals()).isEqualTo(1);
            assertThat(account.getReferralPointsEarned()).isEqualTo(500L);
            assertThat(account.getCurrentPoints()).isEqualTo(1500L);
        }
    }

    @Nested
    @DisplayName("Tier Multiplier Tests")
    class TierMultiplierTests {

        @Test
        @DisplayName("Should return correct multiplier for each tier")
        void shouldReturnCorrectMultiplier() {
            // Bronze
            account.setTier(LoyaltyTier.BRONZE);
            assertThat(account.getTierMultiplier()).isEqualTo(1.0);

            // Silver
            account.setTier(LoyaltyTier.SILVER);
            assertThat(account.getTierMultiplier()).isEqualTo(1.25);

            // Gold
            account.setTier(LoyaltyTier.GOLD);
            assertThat(account.getTierMultiplier()).isEqualTo(1.5);

            // Platinum
            account.setTier(LoyaltyTier.PLATINUM);
            assertThat(account.getTierMultiplier()).isEqualTo(1.75);

            // Diamond
            account.setTier(LoyaltyTier.DIAMOND);
            assertThat(account.getTierMultiplier()).isEqualTo(2.0);
        }
    }
}

