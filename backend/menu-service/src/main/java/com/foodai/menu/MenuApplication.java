package com.foodai.menu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Main application class for Menu Service.
 *
 * <p>This service is responsible for managing menu items, categories, pricing,
 * availability, customizations, and nutritional information for restaurants on the FoodAI platform.
 *
 * <p>Key Features:
 * <ul>
 *   <li>Menu Item CRUD operations</li>
 *   <li>Category management</li>
 *   <li>Dynamic pricing support</li>
 *   <li>Customization options</li>
 *   <li>Nutritional tracking</li>
 *   <li>Availability management</li>
 *   <li>Bulk operations</li>
 *   <li>AI-powered suggestions</li>
 * </ul>
 *
 * @author FoodAI Team
 * @version 1.0
 * @since 2025-01-15
 */
@SpringBootApplication
@EnableKafka
public class MenuApplication {

  public static void main(String[] args) {
    SpringApplication.run(MenuApplication.class, args);
  }
}

