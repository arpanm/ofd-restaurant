package com.foodai.restaurant.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka configuration.
 * Defines topics for restaurant service events.
 */
@Configuration
public class KafkaConfig {
    
    // Restaurant Brand Level Topics
    
    @Bean
    public NewTopic restaurantRegisteredTopic() {
        return TopicBuilder.name("restaurant.registered")
            .partitions(3)
            .replicas(1)
            .build();
    }
    
    @Bean
    public NewTopic restaurantApprovedTopic() {
        return TopicBuilder.name("restaurant.approved")
            .partitions(3)
            .replicas(1)
            .build();
    }
    
    @Bean
    public NewTopic restaurantUpdatedTopic() {
        return TopicBuilder.name("restaurant.updated")
            .partitions(3)
            .replicas(1)
            .build();
    }
    
    // Outlet Level Topics
    
    @Bean
    public NewTopic outletCreatedTopic() {
        return TopicBuilder.name("outlet.created")
            .partitions(3)
            .replicas(1)
            .build();
    }
    
    @Bean
    public NewTopic outletApprovedTopic() {
        return TopicBuilder.name("outlet.approved")
            .partitions(3)
            .replicas(1)
            .build();
    }
    
    @Bean
    public NewTopic outletActivatedTopic() {
        return TopicBuilder.name("outlet.activated")
            .partitions(3)
            .replicas(1)
            .build();
    }
    
    @Bean
    public NewTopic outletSuspendedTopic() {
        return TopicBuilder.name("outlet.suspended")
            .partitions(3)
            .replicas(1)
            .build();
    }
    
    // Serviceability Topics
    
    @Bean
    public NewTopic serviceabilityUpdatedTopic() {
        return TopicBuilder.name("outlet.serviceability.updated")
            .partitions(3)
            .replicas(1)
            .build();
    }
    
    // Contract Topics
    
    @Bean
    public NewTopic contractUpdatedTopic() {
        return TopicBuilder.name("contract.updated")
            .partitions(3)
            .replicas(1)
            .build();
    }
    
    @Bean
    public NewTopic contractSignedTopic() {
        return TopicBuilder.name("contract.signed")
            .partitions(3)
            .replicas(1)
            .build();
    }
    
    // Penalty Topics
    
    @Bean
    public NewTopic penaltyAppliedTopic() {
        return TopicBuilder.name("penalty.applied")
            .partitions(3)
            .replicas(1)
            .build();
    }
}


