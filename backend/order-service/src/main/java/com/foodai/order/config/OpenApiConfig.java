package com.foodai.order.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for the Order Service.
 *
 * @author FoodAI Team
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8084}")
    private String serverPort;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Order Service API")
                .description("""
                    Order Management Service API for the FoodAI Platform.
                    
                    This service handles:
                    - **Cart Management**: Add, update, remove items; apply coupons
                    - **Order Creation**: Convert cart to order with delivery address and payment
                    - **Order Tracking**: Real-time status updates and timeline
                    - **Payment Processing**: Integration with Razorpay for secure payments
                    - **Refunds**: Handle refund requests for cancelled or problematic orders
                    
                    Supports multiple order types:
                    - Single restaurant orders
                    - Multi-restaurant orders
                    - AI chat-based orders
                    - Diet plan orders
                    - Party planning orders
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("FoodAI Team")
                    .email("api@foodai.com")
                    .url("https://foodai.com"))
                .license(new License()
                    .name("Proprietary")
                    .url("https://foodai.com/license")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:" + serverPort)
                    .description("Local Development Server"),
                new Server()
                    .url("https://api.foodai.com")
                    .description("Production Server")
            ));
    }
}

