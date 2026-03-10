package com.foodai.promotion.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Domain entity representing a Marketing Campaign.
 *
 * <p>Campaigns are multi-channel marketing initiatives that can include promotions, target specific
 * customer segments, and track performance metrics.
 *
 * @author FoodAI Team
 */
@Document(collection = "campaigns")
@CompoundIndex(name = "idx_restaurant_status", def = "{'restaurantId': 1, 'status': 1}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {

  /** Unique identifier. */
  @Id private String id;

  /** Campaign name. */
  @Indexed private String name;

  /** Campaign description. */
  private String description;

  /** Current status. */
  @Indexed private CampaignStatus status;

  /** Restaurant ID (null for platform campaigns). */
  @Indexed private String restaurantId;

  /** Target customer segment IDs. */
  @Builder.Default private Set<String> segmentIds = new HashSet<>();

  /** Target customer segment IDs (alias). */
  @Builder.Default private Set<String> targetSegmentIds = new HashSet<>();

  /** Marketing channels to use. */
  @Builder.Default private Set<ChannelType> channels = new HashSet<>();

  /** Campaign type. */
  private CampaignType type;

  /** Content map for channel-specific content. */
  private java.util.Map<String, String> content;

  /** Schedule details. */
  private ScheduleVO schedule;

  /** Campaign creatives/content. */
  @Builder.Default private List<CampaignCreative> creatives = new ArrayList<>();

  /** Linked promotion IDs. */
  @Builder.Default private Set<String> promotionIds = new HashSet<>();

  /** Linked promotion IDs (alias). */
  @Builder.Default private Set<String> linkedPromotionIds = new HashSet<>();

  /** Linked coupon IDs. */
  @Builder.Default private Set<String> linkedCouponIds = new HashSet<>();

  /** Budget allocated for the campaign. */
  private BigDecimal budget;

  /** Whether tracking is enabled. */
  private boolean trackingEnabled;

  /** Whether A/B testing is enabled. */
  private boolean abTestEnabled;

  /** A/B test variant percentage. */
  private Integer variantPercentage;

  /** Amount spent so far. */
  @Builder.Default private BigDecimal spent = BigDecimal.ZERO;

  /** Performance metrics. */
  private CampaignPerformance performance;

  /** Whether this is an AI-suggested campaign. */
  private boolean aiSuggested;

  /** Whether the campaign is deleted. */
  private boolean deleted;

  /** Creation timestamp. */
  @CreatedDate private Instant createdAt;

  /** Last update timestamp. */
  @LastModifiedDate private Instant updatedAt;

  /** Created by user ID. */
  private String createdBy;

  /**
   * Campaign creative content.
   *
   * @author FoodAI Team
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CampaignCreative {
    /** Creative ID. */
    private String id;

    /** Creative type (text, image, video). */
    private String type;

    /** Content (text) or URL (media). */
    private String content;

    /** Media URL (for images/videos). */
    private String mediaUrl;

    /** Whether AI-generated. */
    private boolean aiGenerated;

    /** Channel-specific content. */
    private String channelContent;
  }

  /**
   * Campaign performance metrics.
   *
   * @author FoodAI Team
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CampaignPerformance {
    /** Total messages sent. */
    @Builder.Default private int sent = 0;

    /** Messages delivered. */
    @Builder.Default private int delivered = 0;

    /** Messages opened. */
    @Builder.Default private int opened = 0;

    /** Links clicked. */
    @Builder.Default private int clicked = 0;

    /** Conversions (orders placed). */
    @Builder.Default private int converted = 0;

    /** Total revenue generated. */
    @Builder.Default private BigDecimal revenue = BigDecimal.ZERO;

    /** Total cost. */
    @Builder.Default private BigDecimal cost = BigDecimal.ZERO;

    /**
     * Gets the delivery rate.
     *
     * @return delivery rate percentage
     */
    public double getDeliveryRate() {
      return sent > 0 ? (double) delivered / sent * 100 : 0;
    }

    /**
     * Gets the open rate.
     *
     * @return open rate percentage
     */
    public double getOpenRate() {
      return delivered > 0 ? (double) opened / delivered * 100 : 0;
    }

    /**
     * Gets the click rate.
     *
     * @return click rate percentage
     */
    public double getClickRate() {
      return opened > 0 ? (double) clicked / opened * 100 : 0;
    }

    /**
     * Gets the conversion rate.
     *
     * @return conversion rate percentage
     */
    public double getConversionRate() {
      return clicked > 0 ? (double) converted / clicked * 100 : 0;
    }

    /**
     * Gets the ROI.
     *
     * @return ROI percentage
     */
    public double getRoi() {
      if (cost == null || cost.compareTo(BigDecimal.ZERO) == 0) {
        return 0;
      }
      return revenue
          .subtract(cost)
          .divide(cost, 4, java.math.RoundingMode.HALF_UP)
          .multiply(BigDecimal.valueOf(100))
          .doubleValue();
    }
  }

  /** Activates the campaign. */
  public void activate() {
    if (status == CampaignStatus.COMPLETED || status == CampaignStatus.CANCELLED) {
      throw new IllegalStateException("Cannot activate completed or cancelled campaign");
    }
    this.status = CampaignStatus.ACTIVE;
  }

  /** Pauses the campaign. */
  public void pause() {
    if (status != CampaignStatus.ACTIVE) {
      throw new IllegalStateException("Can only pause active campaigns");
    }
    this.status = CampaignStatus.PAUSED;
  }

  /** Resumes a paused campaign. */
  public void resume() {
    if (status != CampaignStatus.PAUSED) {
      throw new IllegalStateException("Can only resume paused campaigns");
    }
    this.status = CampaignStatus.ACTIVE;
  }

  /** Completes the campaign. */
  public void complete() {
    this.status = CampaignStatus.COMPLETED;
  }

  /** Cancels the campaign. */
  public void cancel() {
    this.status = CampaignStatus.CANCELLED;
  }

  /** Soft deletes the campaign. */
  public void delete() {
    this.deleted = true;
    this.status = CampaignStatus.CANCELLED;
  }

  /** Archives the campaign. */
  public void archive() {
    this.status = CampaignStatus.ARCHIVED;
  }

  /** Validates the campaign. */
  public void validate() {
    if (name == null || name.isBlank()) {
      throw new IllegalStateException("Campaign name is required");
    }
    if (channels == null || channels.isEmpty()) {
      throw new IllegalStateException("At least one channel is required");
    }
  }

  /**
   * Records metrics for the campaign.
   *
   * @param impressions number of impressions
   * @param clicks number of clicks
   * @param conversions number of conversions
   */
  public void recordMetrics(int impressions, int clicks, int conversions) {
    if (performance == null) {
      performance = CampaignPerformance.builder().build();
    }
    performance.setSent(performance.getSent() + impressions);
    performance.setClicked(performance.getClicked() + clicks);
    performance.setConverted(performance.getConverted() + conversions);
  }

  /**
   * Records spend for the campaign.
   *
   * @param amount the amount spent
   */
  public void recordSpend(BigDecimal amount) {
    if (this.spent == null) {
      this.spent = BigDecimal.ZERO;
    }
    this.spent = this.spent.add(amount);
  }

  /**
   * Records campaign send event.
   *
   * @param count number of messages sent
   */
  public void recordSend(int count) {
    if (performance == null) {
      performance = CampaignPerformance.builder().build();
    }
    performance.setSent(performance.getSent() + count);
  }

  /**
   * Records campaign delivery event.
   *
   * @param count number of messages delivered
   */
  public void recordDelivery(int count) {
    if (performance == null) {
      performance = CampaignPerformance.builder().build();
    }
    performance.setDelivered(performance.getDelivered() + count);
  }

  /**
   * Records campaign conversion.
   *
   * @param revenue revenue generated
   */
  public void recordConversion(BigDecimal revenue) {
    if (performance == null) {
      performance = CampaignPerformance.builder().build();
    }
    performance.setConverted(performance.getConverted() + 1);
    performance.setRevenue(performance.getRevenue().add(revenue));
  }
}

