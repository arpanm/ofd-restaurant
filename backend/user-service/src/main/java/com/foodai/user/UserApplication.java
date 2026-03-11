package com.foodai.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Main application class for User Service.
 *
 * <p>This service is responsible for managing user accounts, profiles, preferences,
 * loyalty programs, diet plans, feedback, and AI personalization features.
 *
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>User registration and authentication</li>
 *   <li>Profile and address management</li>
 *   <li>Dietary preferences and restrictions</li>
 *   <li>Loyalty points and gamification</li>
 *   <li>Diet planning and meal scheduling</li>
 *   <li>Customer feedback and support tickets</li>
 *   <li>AI personalization settings</li>
 *   <li>Search history and recommendations</li>
 * </ul>
 *
 * @author FoodAI Team
 * @version 1.0
 * @since 2025-01-15
 */
@SpringBootApplication
@EnableKafka
@EnableCaching
public class UserApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
    }
}

