package com.foodai.restaurant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Main application class for Restaurant Service.
 * 
 * This service handles:
 * - Restaurant onboarding (self-service and assisted)
 * - Multi-outlet management
 * - Contract and fee management
 * - Serviceability configuration
 * - TAT calculation
 * - Penalty management
 * 
 * @author FoodAI Platform
 * @version 1.0.0
 */
@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.foodai.restaurant.domain.repository")
@EnableCaching
public class RestaurantApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantApplication.class, args);
    }
}


