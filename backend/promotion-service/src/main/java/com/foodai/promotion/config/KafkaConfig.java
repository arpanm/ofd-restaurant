package com.foodai.promotion.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

/**
 * Kafka configuration for promotion service.
 *
 * @author FoodAI Team
 */
@Configuration
public class KafkaConfig {

  @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
  private String bootstrapServers;

  @Value("${spring.kafka.consumer.group-id:promotion-service}")
  private String groupId;

  /**
   * Creates the Kafka producer factory.
   *
   * @return the producer factory
   */
  @Bean
  public ProducerFactory<String, Object> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    configProps.put(ProducerConfig.ACKS_CONFIG, "all");
    configProps.put(ProducerConfig.RETRIES_CONFIG, 3);
    configProps.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  /**
   * Creates the Kafka template.
   *
   * @return the Kafka template
   */
  @Bean
  public KafkaTemplate<String, Object> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

  /**
   * Creates the Kafka consumer factory.
   *
   * @return the consumer factory
   */
  @Bean
  public ConsumerFactory<String, Map<String, Object>> consumerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    configProps.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

    JsonDeserializer<Map<String, Object>> deserializer = new JsonDeserializer<>(Map.class);
    deserializer.addTrustedPackages("*");
    deserializer.ignoreTypeHeaders();

    return new DefaultKafkaConsumerFactory<>(
        configProps, new StringDeserializer(), deserializer);
  }

  /**
   * Creates the Kafka listener container factory.
   *
   * @return the listener container factory
   */
  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, Map<String, Object>>
      kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, Map<String, Object>> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory.setConcurrency(3);
    factory.getContainerProperties().setAckMode(
        org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL_IMMEDIATE);
    return factory;
  }
}

