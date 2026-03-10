package com.foodai.user.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB configuration for the user service.
 *
 * @author FoodAI Team
 */
@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "com.foodai.user.domain.repository")
public class MongoConfig {
    // MongoDB configuration is handled via application.yml
    // Custom converters can be added here if needed
}

