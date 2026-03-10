package com.foodai.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka configuration for the Order Service.
 *
 * <p>Defines topics for order-related events.
 *
 * @author FoodAI Team
 */
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.topics.order-created:order.created}")
    private String orderCreatedTopic;

    @Value("${spring.kafka.topics.order-confirmed:order.confirmed}")
    private String orderConfirmedTopic;

    @Value("${spring.kafka.topics.order-cancelled:order.cancelled}")
    private String orderCancelledTopic;

    @Value("${spring.kafka.topics.order-status-updated:order.status.updated}")
    private String orderStatusUpdatedTopic;

    @Value("${spring.kafka.topics.payment-completed:payment.completed}")
    private String paymentCompletedTopic;

    @Value("${spring.kafka.topics.refund-initiated:refund.initiated}")
    private String refundInitiatedTopic;

    private static final int PARTITIONS = 3;
    private static final short REPLICATION_FACTOR = 1;

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(orderCreatedTopic)
            .partitions(PARTITIONS)
            .replicas(REPLICATION_FACTOR)
            .build();
    }

    @Bean
    public NewTopic orderConfirmedTopic() {
        return TopicBuilder.name(orderConfirmedTopic)
            .partitions(PARTITIONS)
            .replicas(REPLICATION_FACTOR)
            .build();
    }

    @Bean
    public NewTopic orderCancelledTopic() {
        return TopicBuilder.name(orderCancelledTopic)
            .partitions(PARTITIONS)
            .replicas(REPLICATION_FACTOR)
            .build();
    }

    @Bean
    public NewTopic orderStatusUpdatedTopic() {
        return TopicBuilder.name(orderStatusUpdatedTopic)
            .partitions(PARTITIONS)
            .replicas(REPLICATION_FACTOR)
            .build();
    }

    @Bean
    public NewTopic paymentCompletedTopic() {
        return TopicBuilder.name(paymentCompletedTopic)
            .partitions(PARTITIONS)
            .replicas(REPLICATION_FACTOR)
            .build();
    }

    @Bean
    public NewTopic refundInitiatedTopic() {
        return TopicBuilder.name(refundInitiatedTopic)
            .partitions(PARTITIONS)
            .replicas(REPLICATION_FACTOR)
            .build();
    }
}

