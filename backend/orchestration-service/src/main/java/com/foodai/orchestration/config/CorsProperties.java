package com.foodai.orchestration.config;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * CORS settings read from application.yml (app.cors). Profile-specific overrides in
 * application-dev.yml / application-prod.yml.
 */
@Component
@ConfigurationProperties(prefix = "app.cors")
public class CorsProperties {

  private List<String> allowedOrigins = new ArrayList<>();
  private List<String> allowedMethods = List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
  private List<String> allowedHeaders = List.of("*");
  private List<String> exposedHeaders = List.of("*");
  private boolean allowCredentials = true;
  private long maxAge = 3600;

  public List<String> getAllowedOrigins() {
    return allowedOrigins;
  }

  public void setAllowedOrigins(List<String> allowedOrigins) {
    this.allowedOrigins = allowedOrigins != null ? allowedOrigins : new ArrayList<>();
  }

  /** For prod: single value from env (e.g. CORS_ALLOWED_ORIGINS) may be comma-separated. */
  public void setAllowedOrigins(String allowedOrigins) {
    if (allowedOrigins == null || allowedOrigins.isBlank()) {
      this.allowedOrigins = new ArrayList<>();
      return;
    }
    this.allowedOrigins = List.of(allowedOrigins.split(",\\s*"));
  }

  public List<String> getAllowedMethods() {
    return allowedMethods;
  }

  public void setAllowedMethods(List<String> allowedMethods) {
    this.allowedMethods = allowedMethods != null ? allowedMethods : List.of();
  }

  public List<String> getAllowedHeaders() {
    return allowedHeaders;
  }

  public void setAllowedHeaders(List<String> allowedHeaders) {
    this.allowedHeaders = allowedHeaders != null ? allowedHeaders : List.of();
  }

  public List<String> getExposedHeaders() {
    return exposedHeaders;
  }

  public void setExposedHeaders(List<String> exposedHeaders) {
    this.exposedHeaders = exposedHeaders != null ? exposedHeaders : List.of();
  }

  public boolean isAllowCredentials() {
    return allowCredentials;
  }

  public void setAllowCredentials(boolean allowCredentials) {
    this.allowCredentials = allowCredentials;
  }

  public long getMaxAge() {
    return maxAge;
  }

  public void setMaxAge(long maxAge) {
    this.maxAge = maxAge;
  }
}
