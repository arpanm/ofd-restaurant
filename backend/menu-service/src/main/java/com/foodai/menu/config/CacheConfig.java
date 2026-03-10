package com.foodai.menu.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis caching configuration.
 *
 * @author FoodAI Team
 */
@Configuration
@EnableCaching
@ConditionalOnClass(RedisConnectionFactory.class)
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis", matchIfMissing = true)
public class CacheConfig {

  /**
   * Configures Redis cache manager with custom TTL for different caches.
   *
   * @param connectionFactory the Redis connection factory
   * @return configured cache manager
   */
  @Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
    RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
        .entryTtl(Duration.ofHours(1))
        .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(
                new GenericJackson2JsonRedisSerializer()
            )
        );

    // Custom TTL for specific caches
    Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
    
    // Menu item cache: 1 hour
    cacheConfigurations.put("menuItem", defaultConfig.entryTtl(Duration.ofHours(1)));
    
    // Menu items list cache: 30 minutes
    cacheConfigurations.put("menuItems", defaultConfig.entryTtl(Duration.ofMinutes(30)));
    
    // Category cache: 24 hours
    cacheConfigurations.put("menuCategory", defaultConfig.entryTtl(Duration.ofHours(24)));
    cacheConfigurations.put("menuCategories", defaultConfig.entryTtl(Duration.ofHours(24)));
    cacheConfigurations.put("activeCategories", defaultConfig.entryTtl(Duration.ofHours(24)));
    
    // Top selling items: 1 hour
    cacheConfigurations.put("topSellingItems", defaultConfig.entryTtl(Duration.ofHours(1)));

    return RedisCacheManager.builder(connectionFactory)
        .cacheDefaults(defaultConfig)
        .withInitialCacheConfigurations(cacheConfigurations)
        .build();
  }
}

