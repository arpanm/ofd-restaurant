package com.foodai.user.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI documentation configuration.
 *
 * @author FoodAI Team
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("User Service API")
                .version("1.0.0")
                .description("""
                    User Management Service for FoodAI Platform.
                    
                    This service handles:
                    - User registration and authentication
                    - Profile and address management
                    - Dietary preferences and restrictions
                    - Loyalty points and gamification
                    - Diet planning and meal scheduling
                    - Customer feedback and support tickets
                    - AI personalization settings
                    """)
                .contact(new Contact()
                    .name("FoodAI Team")
                    .email("api@foodai.com"))
                .license(new License()
                    .name("Proprietary")
                    .url("https://foodai.com/license")))
            .servers(List.of(
                new Server().url("http://localhost:8081").description("Local Development"),
                new Server().url("https://api.foodai.com/user").description("Production")
            ));
    }
}

