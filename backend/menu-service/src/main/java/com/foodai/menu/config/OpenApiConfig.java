package com.foodai.menu.config;

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
 * OpenAPI/Swagger configuration.
 *
 * @author FoodAI Team
 */
@Configuration
public class OpenApiConfig {

  @Value("${server.port:8082}")
  private String serverPort;

  /**
   * Configures OpenAPI documentation.
   *
   * @return OpenAPI configuration
   */
  @Bean
  public OpenAPI menuServiceOpenAPI() {
    Server devServer = new Server();
    devServer.setUrl("http://localhost:" + serverPort);
    devServer.setDescription("Development server");

    Contact contact = new Contact();
    contact.setName("FoodAI Team");
    contact.setEmail("support@foodai.com");

    License license = new License()
        .name("MIT License")
        .url("https://opensource.org/licenses/MIT");

    Info info = new Info()
        .title("Menu Service API")
        .version("1.0.0")
        .description("RESTful API for managing restaurant menus, menu items, categories, pricing, and availability")
        .contact(contact)
        .license(license);

    return new OpenAPI()
        .info(info)
        .servers(List.of(devServer));
  }
}

