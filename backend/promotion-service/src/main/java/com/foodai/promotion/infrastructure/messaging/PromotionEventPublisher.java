package com.foodai.promotion.infrastructure.messaging;

import com.foodai.promotion.domain.model.Campaign;
import com.foodai.promotion.domain.model.Coupon;
import com.foodai.promotion.domain.model.Promotion;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * Publisher for promotion-related events to Kafka.
 *
 * @author FoodAI Team
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PromotionEventPublisher {

  private static final String PROMOTION_EVENTS_TOPIC = "promotion-events";
  private static final String CAMPAIGN_EVENTS_TOPIC = "campaign-events";
  private static final String COUPON_EVENTS_TOPIC = "coupon-events";

  private final KafkaTemplate<String, Object> kafkaTemplate;

  // Promotion Events

  /**
   * Publishes a promotion created event.
   *
   * @param promotion the promotion
   */
  public void publishPromotionCreated(Promotion promotion) {
    publishPromotionEvent("PROMOTION_CREATED", promotion);
  }

  /**
   * Publishes a promotion activated event.
   *
   * @param promotion the promotion
   */
  public void publishPromotionActivated(Promotion promotion) {
    publishPromotionEvent("PROMOTION_ACTIVATED", promotion);
  }

  /**
   * Publishes a promotion paused event.
   *
   * @param promotion the promotion
   */
  public void publishPromotionPaused(Promotion promotion) {
    publishPromotionEvent("PROMOTION_PAUSED", promotion);
  }

  /**
   * Publishes a promotion ended event.
   *
   * @param promotion the promotion
   */
  public void publishPromotionEnded(Promotion promotion) {
    publishPromotionEvent("PROMOTION_ENDED", promotion);
  }

  /**
   * Publishes a promotion usage recorded event.
   *
   * @param promotion the promotion
   */
  public void publishPromotionUsageRecorded(Promotion promotion) {
    publishPromotionEvent("PROMOTION_USAGE_RECORDED", promotion);
  }

  private void publishPromotionEvent(String eventType, Promotion promotion) {
    Map<String, Object> event = new HashMap<>();
    event.put("eventType", eventType);
    event.put("promotionId", promotion.getId());
    event.put("code", promotion.getCode());
    event.put("restaurantId", promotion.getRestaurantId());
    event.put("status", promotion.getStatus().name());
    event.put("timestamp", System.currentTimeMillis());

    log.info("Publishing promotion event: type={}, id={}", eventType, promotion.getId());
    kafkaTemplate
        .send(PROMOTION_EVENTS_TOPIC, promotion.getId(), event)
        .whenComplete(
            (result, ex) -> {
              if (ex != null) {
                log.error("Failed to publish promotion event: {}", eventType, ex);
              } else {
                log.debug("Promotion event published: {}", eventType);
              }
            });
  }

  // Campaign Events

  /**
   * Publishes a campaign created event.
   *
   * @param campaign the campaign
   */
  public void publishCampaignCreated(Campaign campaign) {
    publishCampaignEvent("CAMPAIGN_CREATED", campaign);
  }

  /**
   * Publishes a campaign updated event.
   *
   * @param campaign the campaign
   */
  public void publishCampaignUpdated(Campaign campaign) {
    publishCampaignEvent("CAMPAIGN_UPDATED", campaign);
  }

  /**
   * Publishes a campaign activated event.
   *
   * @param campaign the campaign
   */
  public void publishCampaignActivated(Campaign campaign) {
    publishCampaignEvent("CAMPAIGN_ACTIVATED", campaign);
  }

  /**
   * Publishes a campaign paused event.
   *
   * @param campaign the campaign
   */
  public void publishCampaignPaused(Campaign campaign) {
    publishCampaignEvent("CAMPAIGN_PAUSED", campaign);
  }

  /**
   * Publishes a campaign completed event.
   *
   * @param campaign the campaign
   */
  public void publishCampaignCompleted(Campaign campaign) {
    publishCampaignEvent("CAMPAIGN_COMPLETED", campaign);
  }

  private void publishCampaignEvent(String eventType, Campaign campaign) {
    Map<String, Object> event = new HashMap<>();
    event.put("eventType", eventType);
    event.put("campaignId", campaign.getId());
    event.put("name", campaign.getName());
    event.put("restaurantId", campaign.getRestaurantId());
    event.put("status", campaign.getStatus().name());
    event.put("timestamp", System.currentTimeMillis());

    log.info("Publishing campaign event: type={}, id={}", eventType, campaign.getId());
    kafkaTemplate
        .send(CAMPAIGN_EVENTS_TOPIC, campaign.getId(), event)
        .whenComplete(
            (result, ex) -> {
              if (ex != null) {
                log.error("Failed to publish campaign event: {}", eventType, ex);
              } else {
                log.debug("Campaign event published: {}", eventType);
              }
            });
  }

  // Coupon Events

  /**
   * Publishes a coupon created event.
   *
   * @param coupon the coupon
   */
  public void publishCouponCreated(Coupon coupon) {
    publishCouponEvent("COUPON_CREATED", coupon);
  }

  /**
   * Publishes a coupon applied event.
   *
   * @param coupon the coupon
   * @param userId the user ID
   * @param orderId the order ID
   */
  public void publishCouponApplied(Coupon coupon, String userId, String orderId) {
    Map<String, Object> event = new HashMap<>();
    event.put("eventType", "COUPON_APPLIED");
    event.put("couponId", coupon.getId());
    event.put("code", coupon.getCode());
    event.put("userId", userId);
    event.put("orderId", orderId);
    event.put("timestamp", System.currentTimeMillis());

    log.info("Publishing coupon applied event: code={}, order={}", coupon.getCode(), orderId);
    kafkaTemplate
        .send(COUPON_EVENTS_TOPIC, coupon.getId(), event)
        .whenComplete(
            (result, ex) -> {
              if (ex != null) {
                log.error("Failed to publish coupon applied event", ex);
              } else {
                log.debug("Coupon applied event published");
              }
            });
  }

  /**
   * Publishes a coupon deactivated event.
   *
   * @param coupon the coupon
   */
  public void publishCouponDeactivated(Coupon coupon) {
    publishCouponEvent("COUPON_DEACTIVATED", coupon);
  }

  private void publishCouponEvent(String eventType, Coupon coupon) {
    Map<String, Object> event = new HashMap<>();
    event.put("eventType", eventType);
    event.put("couponId", coupon.getId());
    event.put("code", coupon.getCode());
    event.put("restaurantId", coupon.getRestaurantId());
    event.put("active", coupon.isActive());
    event.put("timestamp", System.currentTimeMillis());

    log.info("Publishing coupon event: type={}, code={}", eventType, coupon.getCode());
    kafkaTemplate
        .send(COUPON_EVENTS_TOPIC, coupon.getId(), event)
        .whenComplete(
            (result, ex) -> {
              if (ex != null) {
                log.error("Failed to publish coupon event: {}", eventType, ex);
              } else {
                log.debug("Coupon event published: {}", eventType);
              }
            });
  }
}

