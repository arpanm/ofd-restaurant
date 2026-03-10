package com.foodai.orchestration.config;

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
 * OpenAPI documentation configuration.
 *
 * @author FoodAI Team
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8081}")
    private String serverPort;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Orchestration Service API")
                .description("Saga orchestration service for distributed transaction management")
                .version("1.0.0")
                .contact(new Contact()
                    .name("FoodAI Team")
                    .email("team@foodai.com"))
                .license(new License()
                    .name("Proprietary")
                    .url("https://foodai.com/license")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:" + serverPort)
                    .description("Local Development Server"),
                new Server()
                    .url("https://api.foodai.com/orchestration")
                    .description("Production Server")
            ));
    }
}

