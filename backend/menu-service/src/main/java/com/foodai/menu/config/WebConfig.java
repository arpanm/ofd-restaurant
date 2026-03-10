package com.foodai.menu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Web configuration for CORS and other web-related settings.
 *
 * @author FoodAI Team
 */
@Configuration
public class WebConfig {

  /**
   * Configures CORS filter to allow cross-origin requests.
   *
   * @return CORS filter
   */
  @Bean
  public CorsFilter corsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    
    // Allow all origins (for development - should be restricted in production)
    config.addAllowedOriginPattern("*");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    config.setAllowCredentials(true);
    
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }
}

