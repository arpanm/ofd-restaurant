package com.foodai.promotion.infrastructure.messaging;

import com.foodai.promotion.service.PromotionService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumer for events from other services that affect promotions.
 *
 * @author FoodAI Team
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PromotionEventConsumer {

  private final PromotionService promotionService;

  /**
   * Handles order events to record promotion usage.
   *
   * @param event the order event
   */
  @KafkaListener(topics = "order-events", groupId = "promotion-service")
  public void handleOrderEvent(Map<String, Object> event) {
    String eventType = (String) event.get("eventType");
    log.debug("Received order event: {}", eventType);

    if ("ORDER_COMPLETED".equals(eventType)) {
      handleOrderCompleted(event);
    }
  }

  /**
   * Handles user events for segment updates.
   *
   * @param event the user event
   */
  @KafkaListener(topics = "user-events", groupId = "promotion-service")
  public void handleUserEvent(Map<String, Object> event) {
    String eventType = (String) event.get("eventType");
    log.debug("Received user event: {}", eventType);

    // Handle user events for segment membership updates
    switch (eventType) {
      case "USER_REGISTERED" -> handleUserRegistered(event);
      case "USER_TIER_CHANGED" -> handleUserTierChanged(event);
      default -> log.trace("Ignoring user event: {}", eventType);
    }
  }

  /**
   * Handles restaurant events for promotion/campaign updates.
   *
   * @param event the restaurant event
   */
  @KafkaListener(topics = "restaurant-events", groupId = "promotion-service")
  public void handleRestaurantEvent(Map<String, Object> event) {
    String eventType = (String) event.get("eventType");
    log.debug("Received restaurant event: {}", eventType);

    // Handle restaurant events
    if ("RESTAURANT_DEACTIVATED".equals(eventType)) {
      handleRestaurantDeactivated(event);
    }
  }

  private void handleOrderCompleted(Map<String, Object> event) {
    String promotionId = (String) event.get("promotionId");

    if (promotionId != null && !promotionId.isBlank()) {
      try {
        promotionService.recordUsage(promotionId);
        log.info("Recorded promotion usage for order: promotionId={}", promotionId);
      } catch (Exception e) {
        log.error("Failed to record promotion usage: promotionId={}", promotionId, e);
      }
    }
  }

  private void handleUserRegistered(Map<String, Object> event) {
    String userId = (String) event.get("userId");
    log.info("New user registered: {}", userId);
    // In real implementation, evaluate dynamic segments for new user
  }

  private void handleUserTierChanged(Map<String, Object> event) {
    String userId = (String) event.get("userId");
    String newTier = (String) event.get("newTier");
    log.info("User tier changed: userId={}, newTier={}", userId, newTier);
    // In real implementation, update segment membership
  }

  private void handleRestaurantDeactivated(Map<String, Object> event) {
    String restaurantId = (String) event.get("restaurantId");
    log.info("Restaurant deactivated: {}", restaurantId);
    // In real implementation, pause/end promotions and campaigns for this restaurant
  }
}

