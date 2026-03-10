package com.foodai.promotion.domain.model;

import java.time.Instant;
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
 * Domain entity representing a Promotion.
 *
 * <p>This is an aggregate root in the Promotion bounded context. Promotions can be created by
 * restaurants or the platform admin to offer discounts and deals to customers.
 *
 * @author FoodAI Team
 */
@Document(collection = "promotions")
@CompoundIndex(name = "idx_restaurant_status", def = "{'restaurantId': 1, 'status': 1}")
@CompoundIndex(name = "idx_status_schedule", def = "{'status': 1, 'schedule.startDate': 1}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {

  /** Unique identifier for the promotion. */
  @Id private String id;

  /** Name of the promotion. */
  @Indexed private String name;

  /** Description shown to customers. */
  private String description;

  /** Type of promotion (percentage, fixed, BOGO, bundle, etc.). */
  @Indexed private PromotionType type;

  /** Current status of the promotion. */
  @Indexed private PromotionStatus status;

  /** Restaurant ID (null for platform-wide promotions). */
  @Indexed private String restaurantId;

  /** Promo code (optional, auto-generated if not provided). */
  @Indexed(unique = true, sparse = true)
  private String code;

  /** Discount details. */
  private DiscountVO discount;

  /** Schedule details. */
  private ScheduleVO schedule;

  /** Usage limits. */
  private UsageLimitVO usageLimit;

  /** Where the promotion applies. */
  private ApplicabilityVO applicability;

  /** Terms and conditions. */
  private String terms;

  /** Whether this is a platform-wide promotion. */
  private boolean platformWide;

  /** Whether this is a featured promotion. */
  private boolean featured;

  /** Display priority (higher = shown first). */
  private int priority;

  /** URL to promotion banner image. */
  private String bannerUrl;

  /** Campaign ID if part of a campaign. */
  private String campaignId;

  /** Whether the promotion is soft deleted. */
  private boolean deleted;

  /** Creation timestamp. */
  @CreatedDate private Instant createdAt;

  /** Last update timestamp. */
  @LastModifiedDate private Instant updatedAt;

  /** Created by user ID. */
  private String createdBy;

  /**
   * Validates the promotion state.
   *
   * @throws IllegalStateException if promotion is in invalid state
   */
  public void validate() {
    if (name == null || name.isBlank()) {
      throw new IllegalStateException("Promotion name cannot be empty");
    }
    if (type == null) {
      throw new IllegalStateException("Promotion type is required");
    }
    if (discount == null) {
      throw new IllegalStateException("Discount details are required");
    }
    if (schedule == null) {
      throw new IllegalStateException("Schedule is required");
    }
  }

  /** Activates the promotion. */
  public void activate() {
    if (status == PromotionStatus.ENDED || status == PromotionStatus.CANCELLED) {
      throw new IllegalStateException("Cannot activate ended or cancelled promotion");
    }
    this.status = PromotionStatus.ACTIVE;
  }

  /** Pauses the promotion. */
  public void pause() {
    if (status != PromotionStatus.ACTIVE) {
      throw new IllegalStateException("Can only pause active promotions");
    }
    this.status = PromotionStatus.PAUSED;
  }

  /** Resumes a paused promotion. */
  public void resume() {
    if (status != PromotionStatus.PAUSED) {
      throw new IllegalStateException("Can only resume paused promotions");
    }
    this.status = PromotionStatus.ACTIVE;
  }

  /** Ends the promotion. */
  public void end() {
    if (status == PromotionStatus.CANCELLED) {
      throw new IllegalStateException("Cannot end cancelled promotion");
    }
    this.status = PromotionStatus.ENDED;
  }

  /** Cancels the promotion. */
  public void cancel() {
    this.status = PromotionStatus.CANCELLED;
  }

  /** Soft deletes the promotion. */
  public void delete() {
    this.deleted = true;
    this.status = PromotionStatus.CANCELLED;
  }

  /**
   * Checks if the promotion can be used.
   *
   * @return true if promotion can be used, false otherwise
   */
  public boolean canBeUsed() {
    if (deleted || status != PromotionStatus.ACTIVE) {
      return false;
    }
    if (usageLimit != null && usageLimit.isTotalLimitReached()) {
      return false;
    }
    if (schedule != null && !schedule.isActiveNow(Instant.now())) {
      return false;
    }
    return true;
  }

  /**
   * Records a usage of the promotion.
   *
   * @return the new usage count
   */
  public int recordUsage() {
    if (usageLimit == null) {
      usageLimit = UsageLimitVO.builder().currentUsage(0).build();
    }
    return usageLimit.incrementUsage();
  }

  /**
   * Updates the status based on schedule.
   *
   * @param now the current time
   */
  public void updateStatusBySchedule(Instant now) {
    if (status == PromotionStatus.CANCELLED || status == PromotionStatus.ENDED) {
      return;
    }
    if (schedule == null) {
      return;
    }
    if (schedule.isExpired(now)) {
      this.status = PromotionStatus.EXPIRED;
    } else if (schedule.isScheduled(now)) {
      this.status = PromotionStatus.SCHEDULED;
    } else if (status == PromotionStatus.SCHEDULED || status == PromotionStatus.DRAFT) {
      this.status = PromotionStatus.ACTIVE;
    }
  }
}

