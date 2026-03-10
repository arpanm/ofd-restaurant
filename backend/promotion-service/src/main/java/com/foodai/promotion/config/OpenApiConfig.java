package com.foodai.promotion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI documentation configuration.
 *
 * @author FoodAI Team
 */
@Configuration
public class OpenApiConfig {

  @Value("${server.port:8089}")
  private String serverPort;

  /**
   * Creates the OpenAPI configuration.
   *
   * @return the OpenAPI configuration
   */
  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Promotion Service API")
                .version("1.0.0")
                .description(
                    "API for managing promotions, coupons, campaigns, and customer segments "
                        + "in the FoodAI platform. Supports discount management, coupon validation, "
                        + "marketing campaign orchestration, and customer segmentation.")
                .contact(
                    new Contact()
                        .name("FoodAI Team")
                        .email("foodai-team@example.com")
                        .url("https://foodai.example.com"))
                .license(new License().name("Proprietary").url("https://foodai.example.com/license")))
        .servers(
            List.of(
                new Server()
                    .url("http://localhost:" + serverPort)
                    .description("Local development server"),
                new Server()
                    .url("https://api.foodai.example.com/promotions")
                    .description("Production server")));
  }
}

