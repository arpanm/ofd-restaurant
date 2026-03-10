package com.foodai.menu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB configuration.
 *
 * @author FoodAI Team
 */
@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "com.foodai.menu.domain.repository")
public class MongoConfig {
  // Auditing is enabled via annotation
  // Additional custom configuration can be added here if needed
}

