package com.foodai.promotion.dto.request;

import com.foodai.promotion.domain.model.CampaignFrequency;
import com.foodai.promotion.domain.model.CampaignType;
import com.foodai.promotion.domain.model.ChannelType;
import com.foodai.promotion.domain.model.ScheduleVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a marketing campaign.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a marketing campaign")
public class CreateCampaignRequest {

  @NotBlank(message = "Campaign name is required")
  @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
  @Schema(description = "Campaign name", example = "Weekend Flash Sale")
  private String name;

  @Size(max = 1000, message = "Description must not exceed 1000 characters")
  @Schema(description = "Campaign description")
  private String description;

  @Schema(description = "Restaurant ID (null for platform campaigns)")
  private String restaurantId;

  @Schema(description = "Campaign type")
  private CampaignType type;

  @NotEmpty(message = "At least one segment is required")
  @Schema(description = "Target segment IDs")
  private Set<String> segmentIds;

  @Schema(description = "Target segment IDs (alias)")
  private Set<String> targetSegmentIds;

  @NotEmpty(message = "At least one channel is required")
  @Schema(description = "Marketing channels to use")
  private Set<ChannelType> channels;

  @Schema(description = "Content map for channel-specific content")
  private Map<String, String> content;

  @Schema(description = "Schedule details")
  private ScheduleVO schedule;

  @Schema(description = "Campaign start date")
  private Instant startDate;

  @Schema(description = "Campaign end date")
  private Instant endDate;

  @Schema(description = "Start time for scheduled sends")
  private LocalTime startTime;

  @Schema(description = "Campaign frequency")
  private CampaignFrequency frequency;

  @Schema(description = "Days of week for weekly campaigns")
  private Set<DayOfWeek> daysOfWeek;

  @Schema(description = "Campaign creatives/content")
  private List<CampaignCreativeRequest> creatives;

  @Schema(description = "Linked promotion IDs")
  private Set<String> promotionIds;

  @Schema(description = "Linked promotion IDs (alias)")
  private Set<String> linkedPromotionIds;

  @Schema(description = "Linked coupon IDs")
  private Set<String> linkedCouponIds;

  @Schema(description = "Budget allocated")
  private BigDecimal budget;

  @Schema(description = "Whether tracking is enabled")
  private boolean trackingEnabled;

  @Schema(description = "Whether A/B testing is enabled")
  private boolean abTestEnabled;

  @Schema(description = "A/B test variant percentage")
  private Integer variantPercentage;

  /**
   * Campaign creative request.
   *
   * @author FoodAI Team
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CampaignCreativeRequest {
    @Schema(description = "Creative type (text, image, video)")
    private String type;

    @Schema(description = "Text content")
    private String content;

    @Schema(description = "Media URL")
    private String mediaUrl;
  }
}

