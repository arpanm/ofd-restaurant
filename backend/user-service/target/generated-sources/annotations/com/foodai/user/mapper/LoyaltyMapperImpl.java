package com.foodai.user.mapper;

import com.foodai.user.domain.model.AchievementVO;
import com.foodai.user.domain.model.BadgeVO;
import com.foodai.user.domain.model.ChallengeProgressVO;
import com.foodai.user.domain.model.LoyaltyAccount;
import com.foodai.user.dto.response.AchievementResponse;
import com.foodai.user.dto.response.BadgeResponse;
import com.foodai.user.dto.response.ChallengeProgressResponse;
import com.foodai.user.dto.response.LoyaltyAccountResponse;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-10T12:27:22+0530",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260224-0835, environment: Java 21.0.10 (Eclipse Adoptium)"
)
@Component
public class LoyaltyMapperImpl implements LoyaltyMapper {

    @Override
    public LoyaltyAccountResponse toResponse(LoyaltyAccount account) {
        if ( account == null ) {
            return null;
        }

        LoyaltyAccountResponse.LoyaltyAccountResponseBuilder loyaltyAccountResponse = LoyaltyAccountResponse.builder();

        loyaltyAccountResponse.achievements( toAchievementResponseList( account.getAchievements() ) );
        loyaltyAccountResponse.activeChallenges( toChallengeProgressResponseList( account.getActiveChallenges() ) );
        loyaltyAccountResponse.badges( toBadgeResponseList( account.getBadges() ) );
        loyaltyAccountResponse.createdAt( account.getCreatedAt() );
        loyaltyAccountResponse.currentPoints( account.getCurrentPoints() );
        loyaltyAccountResponse.currentStreak( account.getCurrentStreak() );
        loyaltyAccountResponse.id( account.getId() );
        loyaltyAccountResponse.lifetimePointsEarned( account.getLifetimePointsEarned() );
        loyaltyAccountResponse.lifetimePointsRedeemed( account.getLifetimePointsRedeemed() );
        loyaltyAccountResponse.longestStreak( account.getLongestStreak() );
        loyaltyAccountResponse.pointsToNextTier( account.getPointsToNextTier() );
        loyaltyAccountResponse.referralCode( account.getReferralCode() );
        loyaltyAccountResponse.referralPointsEarned( account.getReferralPointsEarned() );
        loyaltyAccountResponse.status( account.getStatus() );
        loyaltyAccountResponse.tier( account.getTier() );
        loyaltyAccountResponse.tierPeriodEnd( account.getTierPeriodEnd() );
        loyaltyAccountResponse.tierPeriodStart( account.getTierPeriodStart() );
        loyaltyAccountResponse.totalReferrals( account.getTotalReferrals() );
        loyaltyAccountResponse.userId( account.getUserId() );

        loyaltyAccountResponse.tierName( account.getTier().name() );
        loyaltyAccountResponse.tierProgressPercentage( calculateTierProgress(account) );
        loyaltyAccountResponse.pointsMultiplier( account.getTierMultiplier() );
        loyaltyAccountResponse.tierDiscountPercentage( account.getTier().getDiscountPercentage() );
        loyaltyAccountResponse.freeDeliveriesPerMonth( account.getTier().getFreeDeliveriesPerMonth() );

        return loyaltyAccountResponse.build();
    }

    @Override
    public List<LoyaltyAccountResponse> toResponseList(List<LoyaltyAccount> accounts) {
        if ( accounts == null ) {
            return null;
        }

        List<LoyaltyAccountResponse> list = new ArrayList<LoyaltyAccountResponse>( accounts.size() );
        for ( LoyaltyAccount loyaltyAccount : accounts ) {
            list.add( toResponse( loyaltyAccount ) );
        }

        return list;
    }

    @Override
    public AchievementResponse toAchievementResponse(AchievementVO achievement) {
        if ( achievement == null ) {
            return null;
        }

        AchievementResponse.AchievementResponseBuilder achievementResponse = AchievementResponse.builder();

        achievementResponse.achievementId( achievement.getAchievementId() );
        achievementResponse.category( achievement.getCategory() );
        achievementResponse.description( achievement.getDescription() );
        achievementResponse.iconUrl( achievement.getIconUrl() );
        achievementResponse.name( achievement.getName() );
        achievementResponse.pointsAwarded( achievement.getPointsAwarded() );
        achievementResponse.unlockedAt( achievement.getUnlockedAt() );

        return achievementResponse.build();
    }

    @Override
    public List<AchievementResponse> toAchievementResponseList(List<AchievementVO> achievements) {
        if ( achievements == null ) {
            return null;
        }

        List<AchievementResponse> list = new ArrayList<AchievementResponse>( achievements.size() );
        for ( AchievementVO achievementVO : achievements ) {
            list.add( toAchievementResponse( achievementVO ) );
        }

        return list;
    }

    @Override
    public BadgeResponse toBadgeResponse(BadgeVO badge) {
        if ( badge == null ) {
            return null;
        }

        BadgeResponse.BadgeResponseBuilder badgeResponse = BadgeResponse.builder();

        badgeResponse.badgeId( badge.getBadgeId() );
        badgeResponse.badgeTier( badge.getBadgeTier() );
        badgeResponse.category( badge.getCategory() );
        badgeResponse.description( badge.getDescription() );
        badgeResponse.displayed( badge.isDisplayed() );
        badgeResponse.earnedAt( badge.getEarnedAt() );
        badgeResponse.iconUrl( badge.getIconUrl() );
        badgeResponse.name( badge.getName() );
        badgeResponse.rarity( badge.getRarity() );

        return badgeResponse.build();
    }

    @Override
    public List<BadgeResponse> toBadgeResponseList(List<BadgeVO> badges) {
        if ( badges == null ) {
            return null;
        }

        List<BadgeResponse> list = new ArrayList<BadgeResponse>( badges.size() );
        for ( BadgeVO badgeVO : badges ) {
            list.add( toBadgeResponse( badgeVO ) );
        }

        return list;
    }

    @Override
    public ChallengeProgressResponse toChallengeProgressResponse(ChallengeProgressVO challenge) {
        if ( challenge == null ) {
            return null;
        }

        ChallengeProgressResponse.ChallengeProgressResponseBuilder challengeProgressResponse = ChallengeProgressResponse.builder();

        challengeProgressResponse.badgeReward( challenge.getBadgeReward() );
        challengeProgressResponse.challengeId( challenge.getChallengeId() );
        challengeProgressResponse.completedAt( challenge.getCompletedAt() );
        challengeProgressResponse.currentCount( challenge.getCurrentCount() );
        challengeProgressResponse.description( challenge.getDescription() );
        challengeProgressResponse.endDate( challenge.getEndDate() );
        challengeProgressResponse.iconUrl( challenge.getIconUrl() );
        challengeProgressResponse.name( challenge.getName() );
        challengeProgressResponse.pointsReward( challenge.getPointsReward() );
        challengeProgressResponse.startDate( challenge.getStartDate() );
        challengeProgressResponse.status( challenge.getStatus() );
        challengeProgressResponse.targetCount( challenge.getTargetCount() );

        challengeProgressResponse.progressPercentage( challenge.getProgressPercentage() );
        challengeProgressResponse.expired( challenge.isExpired() );

        return challengeProgressResponse.build();
    }

    @Override
    public List<ChallengeProgressResponse> toChallengeProgressResponseList(List<ChallengeProgressVO> challenges) {
        if ( challenges == null ) {
            return null;
        }

        List<ChallengeProgressResponse> list = new ArrayList<ChallengeProgressResponse>( challenges.size() );
        for ( ChallengeProgressVO challengeProgressVO : challenges ) {
            list.add( toChallengeProgressResponse( challengeProgressVO ) );
        }

        return list;
    }
}
