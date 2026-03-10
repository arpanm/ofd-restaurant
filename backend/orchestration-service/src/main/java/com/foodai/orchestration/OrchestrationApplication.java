package com.foodai.orchestration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for Orchestration Service (API Gateway).
 *
 * <p>Routes requests to backend microservices: user, restaurant, menu, order, promotion.
 *
 * @author FoodAI Team
 * @version 1.0
 * @since 2025-01-15
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class OrchestrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrchestrationApplication.class, args);
    }
}

