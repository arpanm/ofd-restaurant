package com.foodai.promotion.dto.response;

import com.foodai.promotion.domain.model.CampaignFrequency;
import com.foodai.promotion.domain.model.CampaignStatus;
import com.foodai.promotion.domain.model.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Campaign.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Campaign response")
public class CampaignResponse {

  @Schema(description = "Campaign ID")
  private String id;

  @Schema(description = "Campaign name")
  private String name;

  @Schema(description = "Campaign description")
  private String description;

  @Schema(description = "Campaign status")
  private CampaignStatus status;

  @Schema(description = "Restaurant ID")
  private String restaurantId;

  @Schema(description = "Target segment IDs")
  private Set<String> segmentIds;

  @Schema(description = "Marketing channels")
  private Set<ChannelType> channels;

  @Schema(description = "Start date")
  private Instant startDate;

  @Schema(description = "End date")
  private Instant endDate;

  @Schema(description = "Start time")
  private LocalTime startTime;

  @Schema(description = "Frequency")
  private CampaignFrequency frequency;

  @Schema(description = "Days of week")
  private Set<DayOfWeek> daysOfWeek;

  @Schema(description = "Creatives")
  private List<CreativeResponse> creatives;

  @Schema(description = "Linked promotion IDs")
  private Set<String> promotionIds;

  @Schema(description = "Budget")
  private BigDecimal budget;

  @Schema(description = "Amount spent")
  private BigDecimal spent;

  @Schema(description = "Performance metrics")
  private PerformanceResponse performance;

  @Schema(description = "AI suggested")
  private boolean aiSuggested;

  @Schema(description = "Created at")
  private Instant createdAt;

  @Schema(description = "Updated at")
  private Instant updatedAt;

  /**
   * Creative response.
   *
   * @author FoodAI Team
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CreativeResponse {
    @Schema(description = "Creative ID")
    private String id;

    @Schema(description = "Creative type")
    private String type;

    @Schema(description = "Content")
    private String content;

    @Schema(description = "Media URL")
    private String mediaUrl;

    @Schema(description = "AI generated")
    private boolean aiGenerated;
  }

  /**
   * Performance response.
   *
   * @author FoodAI Team
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PerformanceResponse {
    @Schema(description = "Sent count")
    private int sent;

    @Schema(description = "Delivered count")
    private int delivered;

    @Schema(description = "Opened count")
    private int opened;

    @Schema(description = "Clicked count")
    private int clicked;

    @Schema(description = "Converted count")
    private int converted;

    @Schema(description = "Revenue generated")
    private BigDecimal revenue;

    @Schema(description = "Cost")
    private BigDecimal cost;

    @Schema(description = "Delivery rate")
    private double deliveryRate;

    @Schema(description = "Open rate")
    private double openRate;

    @Schema(description = "Click rate")
    private double clickRate;

    @Schema(description = "Conversion rate")
    private double conversionRate;

    @Schema(description = "ROI")
    private double roi;
  }
}

