package com.foodai.promotion.domain.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Domain entity representing a Customer Segment.
 *
 * <p>Customer segments group users based on criteria for targeted marketing and promotions.
 *
 * @author FoodAI Team
 */
@Document(collection = "customer_segments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSegment {

  /** Unique identifier. */
  @Id private String id;

  /** Segment name. */
  @Indexed private String name;

  /** Segment description. */
  private String description;

  /** Restaurant ID (null for platform-wide segments). */
  @Indexed private String restaurantId;

  /** Criteria for segment membership. */
  private SegmentCriteriaVO criteria;

  /** Estimated customer count. */
  private int customerCount;

  /** Whether this is a custom segment (vs pre-defined). */
  private boolean custom;

  /** Whether this segment is dynamic (auto-calculated) or static (manual). */
  private boolean dynamic;

  /** Static user IDs for manual segments. */
  @Builder.Default private Set<String> staticUserIds = new HashSet<>();

  /** Whether this is an AI-suggested segment. */
  private boolean aiSuggested;

  /** Whether segment is active. */
  @Builder.Default private boolean active = true;

  /** Whether segment is deleted. */
  private boolean deleted;

  /** Creation timestamp. */
  @CreatedDate private Instant createdAt;

  /** Last update timestamp. */
  @LastModifiedDate private Instant updatedAt;

  /** Created by user ID. */
  private String createdBy;

  /** Last refresh timestamp. */
  private Instant lastRefreshed;

  /** Activates the segment. */
  public void activate() {
    this.active = true;
  }

  /** Deactivates the segment. */
  public void deactivate() {
    this.active = false;
  }

  /** Soft deletes the segment. */
  public void delete() {
    this.deleted = true;
    this.active = false;
  }

  /**
   * Updates the customer count.
   *
   * @param count new customer count
   */
  public void updateCustomerCount(int count) {
    this.customerCount = count;
    this.lastRefreshed = Instant.now();
  }

  /**
   * Checks if the segment needs refresh.
   *
   * @param maxAge maximum age in hours
   * @return true if needs refresh, false otherwise
   */
  public boolean needsRefresh(int maxAge) {
    if (lastRefreshed == null) {
      return true;
    }
    long hoursSinceRefresh =
        java.time.Duration.between(lastRefreshed, Instant.now()).toHours();
    return hoursSinceRefresh >= maxAge;
  }

  /** Validates the segment. */
  public void validate() {
    if (name == null || name.isBlank()) {
      throw new IllegalStateException("Segment name is required");
    }
    if (!dynamic && (staticUserIds == null || staticUserIds.isEmpty()) && criteria == null) {
      throw new IllegalStateException("Either criteria or static user IDs must be provided");
    }
  }

  /**
   * Adds a user to the segment.
   *
   * @param userId the user ID to add
   */
  public void addUser(String userId) {
    if (staticUserIds == null) {
      staticUserIds = new HashSet<>();
    }
    staticUserIds.add(userId);
    customerCount = staticUserIds.size();
  }

  /**
   * Removes a user from the segment.
   *
   * @param userId the user ID to remove
   */
  public void removeUser(String userId) {
    if (staticUserIds != null) {
      staticUserIds.remove(userId);
      customerCount = staticUserIds.size();
    }
  }

  /**
   * Gets the user count.
   *
   * @return the user count
   */
  public int getUserCount() {
    if (staticUserIds != null && !dynamic) {
      return staticUserIds.size();
    }
    return customerCount;
  }
}

