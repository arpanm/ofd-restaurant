package com.foodai.order.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * JPA configuration for the Order Service.
 *
 * @author FoodAI Team
 */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.foodai.order.domain.repository")
@EnableTransactionManagement
public class JpaConfig {
    // JPA configuration is handled through application.yml
    // This class enables auditing and transaction management
}

