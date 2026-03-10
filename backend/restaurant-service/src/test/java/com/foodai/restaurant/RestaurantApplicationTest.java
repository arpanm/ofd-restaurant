package com.foodai.restaurant;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * Basic integration test to ensure application context loads successfully.
 * 
 * NOTE: Requires MongoDB and Kafka to be running.
 * Run ./manage-services.sh start before executing tests.
 */
@SpringBootTest
@ActiveProfiles("test")
class RestaurantApplicationTest {

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void contextLoads() {
        // This test ensures that the Spring Boot application context loads successfully
        // If there are any configuration issues, this test will fail
    }
}


