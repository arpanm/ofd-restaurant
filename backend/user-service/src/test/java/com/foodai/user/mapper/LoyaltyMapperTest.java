package com.foodai.user.mapper;

import com.foodai.user.domain.model.*;
import com.foodai.user.dto.response.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for LoyaltyMapper.
 */
class LoyaltyMapperTest {

    private LoyaltyMapper loyaltyMapper;

    @BeforeEach
    void setUp() {
        loyaltyMapper = Mappers.getMapper(LoyaltyMapper.class);
    }

    @Nested
    @DisplayName("ToResponse Tests")
    class ToResponseTests {

        @Test
        @DisplayName("Should map LoyaltyAccount to LoyaltyAccountResponse")
        void shouldMapLoyaltyAccountToResponse() {
            // Arrange
            LoyaltyAccount account = LoyaltyAccount.builder()
                .id("loyalty-123")
                .userId("user-123")
                .currentPoints(1000L)
                .lifetimePointsEarned(1500L)
                .lifetimePointsRedeemed(500L)
                .lifetimePointsExpired(0L)
                .tier(LoyaltyTier.SILVER)
                .tierPeriodPoints(1500L)
                .currentStreak(5)
                .longestStreak(10)
                .referralCode("FD12345678")
                .totalReferrals(3)
                .referralPointsEarned(1500L)
                .achievements(new ArrayList<>())
                .badges(new ArrayList<>())
                .activeChallenges(new ArrayList<>())
                .status(LoyaltyAccountStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();

            // Act
            LoyaltyAccountResponse response = loyaltyMapper.toResponse(account);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo("loyalty-123");
            assertThat(response.getUserId()).isEqualTo("user-123");
            assertThat(response.getCurrentPoints()).isEqualTo(1000L);
            assertThat(response.getLifetimePointsEarned()).isEqualTo(1500L);
            assertThat(response.getTier()).isEqualTo(LoyaltyTier.SILVER);
            assertThat(response.getCurrentStreak()).isEqualTo(5);
            assertThat(response.getReferralCode()).isEqualTo("FD12345678");
            assertThat(response.getTotalReferrals()).isEqualTo(3);
        }

        @Test
        @DisplayName("Should compute tier name and multiplier")
        void shouldComputeTierNameAndMultiplier() {
            // Arrange
            LoyaltyAccount account = LoyaltyAccount.builder()
                .id("loyalty-123")
                .userId("user-123")
                .tier(LoyaltyTier.GOLD)
                .achievements(new ArrayList<>())
                .badges(new ArrayList<>())
                .activeChallenges(new ArrayList<>())
                .build();

            // Act
            LoyaltyAccountResponse response = loyaltyMapper.toResponse(account);

            // Assert
            assertThat(response.getTierName()).isEqualTo("GOLD");
            assertThat(response.getPointsMultiplier()).isEqualTo(1.5);
        }

        @Test
        @DisplayName("Should handle null account")
        void shouldHandleNullAccount() {
            // Act
            LoyaltyAccountResponse response = loyaltyMapper.toResponse(null);

            // Assert
            assertThat(response).isNull();
        }
    }

    @Nested
    @DisplayName("Achievement Mapping Tests")
    class AchievementMappingTests {

        @Test
        @DisplayName("Should map AchievementVO to AchievementResponse")
        void shouldMapAchievementVOToResponse() {
            // Arrange
            AchievementVO vo = AchievementVO.builder()
                .achievementId("first-order")
                .name("First Order")
                .description("Placed your first order")
                .category(AchievementCategory.ORDERS)
                .iconUrl("https://example.com/icons/first-order.png")
                .pointsAwarded(100L)
                .unlockedAt(Instant.now())
                .build();

            // Act
            AchievementResponse response = loyaltyMapper.toAchievementResponse(vo);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getAchievementId()).isEqualTo("first-order");
            assertThat(response.getName()).isEqualTo("First Order");
            assertThat(response.getDescription()).isEqualTo("Placed your first order");
            assertThat(response.getCategory()).isEqualTo(AchievementCategory.ORDERS);
            assertThat(response.getPointsAwarded()).isEqualTo(100L);
        }

        @Test
        @DisplayName("Should map list of AchievementVO to list of AchievementResponse")
        void shouldMapListOfAchievements() {
            // Arrange
            List<AchievementVO> voList = List.of(
                AchievementVO.builder().achievementId("a1").name("Achievement 1").build(),
                AchievementVO.builder().achievementId("a2").name("Achievement 2").build()
            );

            // Act
            List<AchievementResponse> responseList = loyaltyMapper.toAchievementResponseList(voList);

            // Assert
            assertThat(responseList).hasSize(2);
            assertThat(responseList.get(0).getName()).isEqualTo("Achievement 1");
            assertThat(responseList.get(1).getName()).isEqualTo("Achievement 2");
        }
    }

    @Nested
    @DisplayName("Badge Mapping Tests")
    class BadgeMappingTests {

        @Test
        @DisplayName("Should map BadgeVO to BadgeResponse")
        void shouldMapBadgeVOToResponse() {
            // Arrange
            BadgeVO vo = BadgeVO.builder()
                .badgeId("foodie")
                .name("Foodie")
                .description("Ordered 10 times")
                .iconUrl("https://example.com/badges/foodie.png")
                .rarity("common")
                .earnedAt(Instant.now())
                .build();

            // Act
            BadgeResponse response = loyaltyMapper.toBadgeResponse(vo);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getBadgeId()).isEqualTo("foodie");
            assertThat(response.getName()).isEqualTo("Foodie");
            assertThat(response.getDescription()).isEqualTo("Ordered 10 times");
            assertThat(response.getRarity()).isEqualTo("common");
        }

        @Test
        @DisplayName("Should map list of BadgeVO to list of BadgeResponse")
        void shouldMapListOfBadges() {
            // Arrange
            List<BadgeVO> voList = List.of(
                BadgeVO.builder().badgeId("b1").name("Badge 1").build(),
                BadgeVO.builder().badgeId("b2").name("Badge 2").build()
            );

            // Act
            List<BadgeResponse> responseList = loyaltyMapper.toBadgeResponseList(voList);

            // Assert
            assertThat(responseList).hasSize(2);
            assertThat(responseList.get(0).getName()).isEqualTo("Badge 1");
            assertThat(responseList.get(1).getName()).isEqualTo("Badge 2");
        }
    }

    @Nested
    @DisplayName("Challenge Mapping Tests")
    class ChallengeMappingTests {

        @Test
        @DisplayName("Should map ChallengeProgressVO to ChallengeProgressResponse")
        void shouldMapChallengeProgressVOToResponse() {
            // Arrange
            ChallengeProgressVO vo = ChallengeProgressVO.builder()
                .challengeId("challenge-123")
                .name("Weekend Warrior")
                .description("Order 5 times this weekend")
                .currentCount(3)
                .targetCount(5)
                .status(ChallengeStatus.IN_PROGRESS)
                .pointsReward(200L)
                .startDate(Instant.now())
                .endDate(Instant.now().plusSeconds(86400 * 2))
                .build();

            // Act
            ChallengeProgressResponse response = loyaltyMapper.toChallengeProgressResponse(vo);

            // Assert
            assertThat(response).isNotNull();
            assertThat(response.getChallengeId()).isEqualTo("challenge-123");
            assertThat(response.getName()).isEqualTo("Weekend Warrior");
            assertThat(response.getCurrentCount()).isEqualTo(3);
            assertThat(response.getTargetCount()).isEqualTo(5);
            assertThat(response.getStatus()).isEqualTo(ChallengeStatus.IN_PROGRESS);
            assertThat(response.getPointsReward()).isEqualTo(200L);
        }

        @Test
        @DisplayName("Should compute progress percentage")
        void shouldComputeProgressPercentage() {
            // Arrange
            ChallengeProgressVO vo = ChallengeProgressVO.builder()
                .challengeId("challenge-123")
                .currentCount(3)
                .targetCount(5)
                .build();

            // Act
            ChallengeProgressResponse response = loyaltyMapper.toChallengeProgressResponse(vo);

            // Assert
            assertThat(response.getProgressPercentage()).isEqualTo(60);
        }
    }
}
