package com.foodai.promotion.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB configuration.
 *
 * @author FoodAI Team
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.foodai.promotion.domain.repository")
@EnableMongoAuditing
public class MongoConfig {
  // MongoDB configuration is handled via application.yml
}

