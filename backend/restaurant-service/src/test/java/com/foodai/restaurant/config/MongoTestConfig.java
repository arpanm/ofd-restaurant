package com.foodai.restaurant.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test configuration for MongoDB components.
 * Provides mock beans to avoid requiring a real MongoDB connection in unit tests.
 */
@TestConfiguration
public class MongoTestConfig {

    @Bean
    @Primary
    public MongoDatabaseFactory mongoDatabaseFactory() {
        return mock(MongoDatabaseFactory.class);
    }

    @Bean
    @Primary
    public MongoMappingContext mongoMappingContext() {
        return new MongoMappingContext();
    }

    @Bean
    @Primary
    public MongoConverter mongoConverter(MongoDatabaseFactory factory, MongoMappingContext context) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, context);
        converter.afterPropertiesSet();
        return converter;
    }

    @Bean
    @Primary
    public MongoTemplate mongoTemplate(MongoDatabaseFactory factory, MongoConverter converter) {
        MongoTemplate template = mock(MongoTemplate.class);
        when(template.getConverter()).thenReturn(converter);
        return template;
    }
}
