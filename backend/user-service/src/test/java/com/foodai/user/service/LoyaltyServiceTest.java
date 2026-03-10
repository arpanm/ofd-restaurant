package com.foodai.user.service;

import com.foodai.user.domain.model.*;
import com.foodai.user.domain.repository.LoyaltyAccountRepository;
import com.foodai.user.dto.response.LoyaltyAccountResponse;
import com.foodai.user.exception.LoyaltyAccountNotFoundException;
import com.foodai.user.mapper.LoyaltyMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LoyaltyService.
 */
@ExtendWith(MockitoExtension.class)
class LoyaltyServiceTest {

    @Mock
    private LoyaltyAccountRepository loyaltyAccountRepository;

    @Mock
    private LoyaltyMapper loyaltyMapper;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private LoyaltyService loyaltyService;

    private LoyaltyAccount testAccount;
    private LoyaltyAccountResponse testAccountResponse;

    @BeforeEach
    void setUp() {
        testAccount = LoyaltyAccount.builder()
            .id("loyalty-123")
            .userId("user-123")
            .currentPoints(1000L)
            .lifetimePointsEarned(1500L)
            .lifetimePointsRedeemed(500L)
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
            .tierPeriodStart(Instant.now())
            .createdAt(Instant.now())
            .build();

        testAccountResponse = LoyaltyAccountResponse.builder()
            .id("loyalty-123")
            .userId("user-123")
            .currentPoints(1000L)
            .tier(LoyaltyTier.SILVER)
            .tierName("SILVER")
            .pointsMultiplier(1.25)
            .currentStreak(5)
            .referralCode("FD12345678")
            .build();
    }

    @Nested
    @DisplayName("Create Account Tests")
    class CreateAccountTests {

        @Test
        @DisplayName("Should create loyalty account successfully")
        void shouldCreateAccountSuccessfully() {
            // Arrange
            when(loyaltyAccountRepository.findByUserId("user-123")).thenReturn(Optional.empty());
            when(loyaltyAccountRepository.existsByReferralCode(anyString())).thenReturn(false);
            when(loyaltyAccountRepository.save(any(LoyaltyAccount.class))).thenReturn(testAccount);
            when(loyaltyMapper.toResponse(any(LoyaltyAccount.class))).thenReturn(testAccountResponse);

            // Act
            LoyaltyAccountResponse result = loyaltyService.createAccount("user-123");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo("user-123");
            verify(loyaltyAccountRepository).save(any(LoyaltyAccount.class));
        }

        @Test
        @DisplayName("Should throw exception when account already exists")
        void shouldThrowException_whenAccountAlreadyExists() {
            // Arrange
            when(loyaltyAccountRepository.findByUserId("user-123")).thenReturn(Optional.of(testAccount));

            // Act & Assert
            assertThatThrownBy(() -> loyaltyService.createAccount("user-123"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists");

            verify(loyaltyAccountRepository, never()).save(any(LoyaltyAccount.class));
        }
    }

    @Nested
    @DisplayName("Get Account Tests")
    class GetAccountTests {

        @Test
        @DisplayName("Should return account by user ID")
        void shouldReturnAccountByUserId() {
            // Arrange
            when(loyaltyAccountRepository.findByUserId("user-123")).thenReturn(Optional.of(testAccount));
            when(loyaltyMapper.toResponse(testAccount)).thenReturn(testAccountResponse);

            // Act
            LoyaltyAccountResponse result = loyaltyService.getAccountByUserId("user-123");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo("user-123");
        }

        @Test
        @DisplayName("Should throw exception when account not found")
        void shouldThrowException_whenAccountNotFound() {
            // Arrange
            when(loyaltyAccountRepository.findByUserId("unknown")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> loyaltyService.getAccountByUserId("unknown"))
                .isInstanceOf(LoyaltyAccountNotFoundException.class);
        }

        @Test
        @DisplayName("Should return account by ID")
        void shouldReturnAccountById() {
            // Arrange
            when(loyaltyAccountRepository.findById("loyalty-123")).thenReturn(Optional.of(testAccount));
            when(loyaltyMapper.toResponse(testAccount)).thenReturn(testAccountResponse);

            // Act
            LoyaltyAccountResponse result = loyaltyService.getAccountById("loyalty-123");

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("loyalty-123");
        }
    }

    @Nested
    @DisplayName("Earn Points Tests")
    class EarnPointsTests {

        @Test
        @DisplayName("Should earn points for order")
        void shouldEarnPointsForOrder() {
            // Arrange
            when(loyaltyAccountRepository.findByUserId("user-123")).thenReturn(Optional.of(testAccount));
            when(loyaltyAccountRepository.save(any(LoyaltyAccount.class))).thenReturn(testAccount);
            when(loyaltyMapper.toResponse(any(LoyaltyAccount.class))).thenReturn(testAccountResponse);

            long initialPoints = testAccount.getCurrentPoints();

            // Act
            LoyaltyAccountResponse result = loyaltyService.earnPointsForOrder("user-123", 500.0, "order-123");

            // Assert
            assertThat(result).isNotNull();
            assertThat(testAccount.getCurrentPoints()).isGreaterThan(initialPoints);
            verify(loyaltyAccountRepository).save(testAccount);
        }

        @Test
        @DisplayName("Should apply tier multiplier when earning points")
        void shouldApplyTierMultiplier() {
            // Arrange
            testAccount.setCurrentPoints(0L);
            testAccount.setTier(LoyaltyTier.GOLD); // 1.5x multiplier
            
            when(loyaltyAccountRepository.findByUserId("user-123")).thenReturn(Optional.of(testAccount));
            when(loyaltyAccountRepository.save(any(LoyaltyAccount.class))).thenReturn(testAccount);
            when(loyaltyMapper.toResponse(any(LoyaltyAccount.class))).thenReturn(testAccountResponse);

            // Act
            loyaltyService.earnPointsForOrder("user-123", 100.0, "order-123");

            // Assert
            // Base points = 100, with 1.5x multiplier = 150
            assertThat(testAccount.getCurrentPoints()).isEqualTo(150L);
        }
    }

    @Nested
    @DisplayName("Redeem Points Tests")
    class RedeemPointsTests {

        @Test
        @DisplayName("Should redeem points successfully")
        void shouldRedeemPointsSuccessfully() {
            // Arrange
            testAccount.setCurrentPoints(1000L);
            when(loyaltyAccountRepository.findByUserId("user-123")).thenReturn(Optional.of(testAccount));
            when(loyaltyAccountRepository.save(any(LoyaltyAccount.class))).thenReturn(testAccount);
            when(loyaltyMapper.toResponse(any(LoyaltyAccount.class))).thenReturn(testAccountResponse);

            // Act
            LoyaltyAccountResponse result = loyaltyService.redeemPoints("user-123", 500L, "discount");

            // Assert
            assertThat(result).isNotNull();
            assertThat(testAccount.getCurrentPoints()).isEqualTo(500L);
            verify(loyaltyAccountRepository).save(testAccount);
        }

        @Test
        @DisplayName("Should throw exception when insufficient points")
        void shouldThrowException_whenInsufficientPoints() {
            // Arrange
            testAccount.setCurrentPoints(100L);
            when(loyaltyAccountRepository.findByUserId("user-123")).thenReturn(Optional.of(testAccount));

            // Act & Assert
            assertThatThrownBy(() -> loyaltyService.redeemPoints("user-123", 500L, "discount"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Insufficient points");

            verify(loyaltyAccountRepository, never()).save(any(LoyaltyAccount.class));
        }
    }

    @Nested
    @DisplayName("Referral Tests")
    class ReferralTests {

        @Test
        @DisplayName("Should process referral successfully")
        void shouldProcessReferralSuccessfully() {
            // Arrange
            LoyaltyAccount refereeAccount = LoyaltyAccount.builder()
                .id("loyalty-456")
                .userId("user-456")
                .currentPoints(0L)
                .build();

            when(loyaltyAccountRepository.findByReferralCode("FD12345678")).thenReturn(Optional.of(testAccount));
            when(loyaltyAccountRepository.findByUserId("user-456")).thenReturn(Optional.of(refereeAccount));
            when(loyaltyAccountRepository.save(any(LoyaltyAccount.class))).thenAnswer(i -> i.getArgument(0));
            when(loyaltyMapper.toResponse(any(LoyaltyAccount.class))).thenReturn(testAccountResponse);

            int initialReferrals = testAccount.getTotalReferrals();

            // Act
            LoyaltyAccountResponse result = loyaltyService.processReferral("FD12345678", "user-456");

            // Assert
            assertThat(result).isNotNull();
            assertThat(testAccount.getTotalReferrals()).isEqualTo(initialReferrals + 1);
            verify(loyaltyAccountRepository, times(2)).save(any(LoyaltyAccount.class));
        }

        @Test
        @DisplayName("Should throw exception for invalid referral code")
        void shouldThrowException_whenInvalidReferralCode() {
            // Arrange
            when(loyaltyAccountRepository.findByReferralCode("INVALID")).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> loyaltyService.processReferral("INVALID", "user-456"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid referral code");
        }
    }

    @Nested
    @DisplayName("Achievement Tests")
    class AchievementTests {

        @Test
        @DisplayName("Should award achievement successfully")
        void shouldAwardAchievementSuccessfully() {
            // Arrange
            AchievementVO achievement = AchievementVO.builder()
                .achievementId("first-order")
                .name("First Order")
                .description("Placed your first order")
                .pointsAwarded(100L)
                .build();

            when(loyaltyAccountRepository.findByUserId("user-123")).thenReturn(Optional.of(testAccount));
            when(loyaltyAccountRepository.save(any(LoyaltyAccount.class))).thenReturn(testAccount);
            when(loyaltyMapper.toResponse(any(LoyaltyAccount.class))).thenReturn(testAccountResponse);

            // Act
            LoyaltyAccountResponse result = loyaltyService.awardAchievement("user-123", achievement);

            // Assert
            assertThat(result).isNotNull();
            assertThat(testAccount.getAchievements()).contains(achievement);
            verify(loyaltyAccountRepository).save(testAccount);
        }
    }

    @Nested
    @DisplayName("Badge Tests")
    class BadgeTests {

        @Test
        @DisplayName("Should award badge successfully")
        void shouldAwardBadgeSuccessfully() {
            // Arrange
            BadgeVO badge = BadgeVO.builder()
                .badgeId("foodie")
                .name("Foodie")
                .description("Ordered 10 times")
                .rarity("common")
                .build();

            when(loyaltyAccountRepository.findByUserId("user-123")).thenReturn(Optional.of(testAccount));
            when(loyaltyAccountRepository.save(any(LoyaltyAccount.class))).thenReturn(testAccount);
            when(loyaltyMapper.toResponse(any(LoyaltyAccount.class))).thenReturn(testAccountResponse);

            // Act
            LoyaltyAccountResponse result = loyaltyService.awardBadge("user-123", badge);

            // Assert
            assertThat(result).isNotNull();
            assertThat(testAccount.getBadges()).contains(badge);
            verify(loyaltyAccountRepository).save(testAccount);
        }
    }
}

