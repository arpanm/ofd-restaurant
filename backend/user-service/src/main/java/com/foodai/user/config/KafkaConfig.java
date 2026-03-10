package com.foodai.user.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka configuration for the user service.
 *
 * @author FoodAI Team
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    // Topic names
    public static final String USER_REGISTERED_TOPIC = "user.registered";
    public static final String USER_PROFILE_UPDATED_TOPIC = "user.profile.updated";
    public static final String USER_DELETED_TOPIC = "user.deleted";
    public static final String LOYALTY_POINTS_EARNED_TOPIC = "loyalty.points.earned";
    public static final String LOYALTY_POINTS_REDEEMED_TOPIC = "loyalty.points.redeemed";
    public static final String LOYALTY_TIER_CHANGED_TOPIC = "loyalty.tier.changed";
    public static final String LOYALTY_REFERRAL_PROCESSED_TOPIC = "loyalty.referral.processed";

    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name(USER_REGISTERED_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic userProfileUpdatedTopic() {
        return TopicBuilder.name(USER_PROFILE_UPDATED_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic userDeletedTopic() {
        return TopicBuilder.name(USER_DELETED_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic loyaltyPointsEarnedTopic() {
        return TopicBuilder.name(LOYALTY_POINTS_EARNED_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic loyaltyPointsRedeemedTopic() {
        return TopicBuilder.name(LOYALTY_POINTS_REDEEMED_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic loyaltyTierChangedTopic() {
        return TopicBuilder.name(LOYALTY_TIER_CHANGED_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }

    @Bean
    public NewTopic loyaltyReferralProcessedTopic() {
        return TopicBuilder.name(LOYALTY_REFERRAL_PROCESSED_TOPIC)
            .partitions(3)
            .replicas(1)
            .build();
    }
}

