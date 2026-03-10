package com.foodai.restaurant.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * MongoDB configuration.
 * Enables auditing for automatic @CreatedDate and @LastModifiedDate handling.
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {
    // MongoDB auditing is enabled
    // Auto-index creation is configured in application.yml
}


