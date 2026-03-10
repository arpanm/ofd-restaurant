package com.foodai.order.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB configuration for the Order Service.
 *
 * @author FoodAI Team
 */
@Configuration
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "com.foodai.order.domain.repository")
public class MongoConfig {
    // MongoDB configuration is handled through application.yml
    // This class enables auditing and explicitly configures repository scanning
}

