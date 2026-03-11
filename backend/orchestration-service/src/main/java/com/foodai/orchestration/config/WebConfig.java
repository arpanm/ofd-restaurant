package com.foodai.orchestration.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * CORS filter built from application properties (app.cors in application.yml and
 * application-{profile}.yml). When no allowed-origins are configured, CORS is effectively disabled.
 */
@Configuration
public class WebConfig {

  @Bean
  @Order(Ordered.HIGHEST_PRECEDENCE)
  public CorsFilter corsFilter(CorsProperties props) {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    if (props.getAllowedOrigins() != null && !props.getAllowedOrigins().isEmpty()) {
      config.setAllowedOrigins(props.getAllowedOrigins());
    }
    config.setAllowedMethods(props.getAllowedMethods());
    config.setAllowedHeaders(props.getAllowedHeaders());
    config.setExposedHeaders(props.getExposedHeaders());
    config.setAllowCredentials(props.isAllowCredentials());
    config.setMaxAge(props.getMaxAge());
    source.registerCorsConfiguration("/**", config);
    return new CorsFilter(source);
  }
}
