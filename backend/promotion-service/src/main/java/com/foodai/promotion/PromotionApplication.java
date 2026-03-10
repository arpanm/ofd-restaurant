package com.foodai.promotion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for Promotion Service.
 *
 * <p>This service is responsible for managing promotions, coupons, campaigns,
 * and customer segments for the FoodAI platform.
 *
 * @author FoodAI Team
 * @version 1.0
 * @since 2025-01-15
 */
@SpringBootApplication
@EnableMongoAuditing
@EnableKafka
@EnableCaching
@EnableScheduling
public class PromotionApplication {

  /**
   * Main method to start the Promotion Service.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    SpringApplication.run(PromotionApplication.class, args);
  }
}

