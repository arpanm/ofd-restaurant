package com.foodai.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Main application class for Order Service.
 *
 * <p>This service is responsible for managing the complete order lifecycle including:
 * - Cart management (single and multi-restaurant)
 * - Checkout flow with address and payment selection
 * - Order creation, tracking, and status updates
 * - Payment processing and refunds
 * - Support for various order types (regular, AI chat, diet plan, party)
 *
 * @author FoodAI Team
 * @version 1.0
 * @since 2025-01-15
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableMongoAuditing
@EnableKafka
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}

