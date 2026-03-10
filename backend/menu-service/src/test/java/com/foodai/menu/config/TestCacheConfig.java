package com.foodai.menu.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Test configuration that disables caching for tests.
 *
 * @author FoodAI Team
 */
@TestConfiguration
@EnableCaching
public class TestCacheConfig {

  @Bean
  @Primary
  public CacheManager cacheManager() {
    return new NoOpCacheManager();
  }
}

