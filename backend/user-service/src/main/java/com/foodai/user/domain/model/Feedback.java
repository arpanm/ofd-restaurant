package com.foodai.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Feedback Entity.
 * Represents customer feedback, reviews, and support tickets.
 *
 * <p>Covers features from:
 * <ul>
 *   <li>CUSTOMER_FEEDBACK_TICKETING.md - Feedback submission, ticket management</li>
 * </ul>
 *
 * @author FoodAI Team
 */
@Document(collection = "feedback")
@CompoundIndexes({
    @CompoundIndex(name = "user_type_idx", def = "{'userId': 1, 'feedbackType': 1}"),
    @CompoundIndex(name = "order_idx", def = "{'orderId': 1}"),
    @CompoundIndex(name = "entity_idx", def = "{'entityType': 1, 'entityId': 1}")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {

    /**
     * Unique feedback identifier
     */
    @Id
    private String id;

    /**
     * User ID who submitted the feedback
     */
    @Indexed
    private String userId;

    /**
     * Type of feedback
     */
    @Indexed
    private FeedbackType feedbackType;

    /**
     * Category of feedback
     */
    private FeedbackCategory category;

    /**
     * Sub-category for detailed classification
     */
    private String subCategory;

    /**
     * Order ID (if feedback is related to an order)
     */
    private String orderId;

    /**
     * Entity type being reviewed (RESTAURANT, MENU_ITEM, RIDER, PLATFORM)
     */
    private FeedbackEntityType entityType;

    /**
     * Entity ID being reviewed
     */
    private String entityId;

    /**
     * Entity name (for display)
     */
    private String entityName;

    /**
     * Feedback title/subject
     */
    private String title;

    /**
     * Detailed feedback description
     */
    private String description;

    /**
     * Overall rating (1-5)
     */
    private Integer overallRating;

    /**
     * Food quality rating (1-5)
     */
    private Integer foodRating;

    /**
     * Delivery experience rating (1-5)
     */
    private Integer deliveryRating;

    /**
     * Packaging rating (1-5)
     */
    private Integer packagingRating;

    /**
     * Value for money rating (1-5)
     */
    private Integer valueRating;

    /**
     * Attached images
     */
    @Builder.Default
    private List<String> imageUrls = new ArrayList<>();

    /**
     * Tags for categorization
     */
    @Builder.Default
    private List<String> tags = new ArrayList<>();

    /**
     * Sentiment analysis result
     */
    private SentimentVO sentiment;

    /**
     * Priority level (for tickets)
     */
    @Builder.Default
    private FeedbackPriority priority = FeedbackPriority.MEDIUM;

    /**
     * Current status
     */
    @Indexed
    @Builder.Default
    private FeedbackStatus status = FeedbackStatus.SUBMITTED;

    /**
     * Assigned agent ID (for tickets)
     */
    private String assignedTo;

    /**
     * Resolution details
     */
    private String resolution;

    /**
     * Compensation offered (if any)
     */
    private CompensationVO compensation;

    /**
     * Response comments from support/restaurant
     */
    @Builder.Default
    private List<FeedbackCommentVO> comments = new ArrayList<>();

    /**
     * Whether feedback is public (visible to others)
     */
    @Builder.Default
    private boolean isPublic = true;

    /**
     * Whether user wants to be contacted
     */
    @Builder.Default
    private boolean contactRequested = false;

    /**
     * Preferred contact method
     */
    private String preferredContactMethod;

    /**
     * Source of feedback (APP, WEB, CALL, CHAT)
     */
    @Builder.Default
    private String source = "APP";

    /**
     * Device/platform info
     */
    private String deviceInfo;

    /**
     * App version
     */
    private String appVersion;

    /**
     * Creation timestamp
     */
    @CreatedDate
    private Instant createdAt;

    /**
     * Last update timestamp
     */
    @LastModifiedDate
    private Instant updatedAt;

    /**
     * When feedback was resolved
     */
    private Instant resolvedAt;

    /**
     * First response time (for SLA tracking)
     */
    private Instant firstResponseAt;

    /**
     * Soft delete flag
     */
    @Builder.Default
    private boolean deleted = false;

    // ==================== Domain Methods ====================

    /**
     * Adds a comment to the feedback.
     *
     * @param comment the comment to add
     */
    public void addComment(FeedbackCommentVO comment) {
        if (this.comments == null) {
            this.comments = new ArrayList<>();
        }
        this.comments.add(comment);
        
        // Update first response time if this is from support
        if (this.firstResponseAt == null && !comment.isFromUser()) {
            this.firstResponseAt = comment.getCreatedAt();
        }
    }

    /**
     * Updates the status.
     *
     * @param newStatus the new status
     */
    public void updateStatus(FeedbackStatus newStatus) {
        this.status = newStatus;
        if (newStatus == FeedbackStatus.RESOLVED || newStatus == FeedbackStatus.CLOSED) {
            this.resolvedAt = Instant.now();
        }
    }

    /**
     * Assigns to an agent.
     *
     * @param agentId the agent ID
     */
    public void assignTo(String agentId) {
        this.assignedTo = agentId;
        if (this.status == FeedbackStatus.SUBMITTED) {
            this.status = FeedbackStatus.IN_PROGRESS;
        }
    }

    /**
     * Resolves the feedback.
     *
     * @param resolution the resolution description
     */
    public void resolve(String resolution) {
        this.resolution = resolution;
        this.status = FeedbackStatus.RESOLVED;
        this.resolvedAt = Instant.now();
    }

    /**
     * Calculates average rating from all rating components.
     *
     * @return average rating
     */
    public double getAverageRating() {
        int count = 0;
        int total = 0;
        
        if (overallRating != null) { total += overallRating; count++; }
        if (foodRating != null) { total += foodRating; count++; }
        if (deliveryRating != null) { total += deliveryRating; count++; }
        if (packagingRating != null) { total += packagingRating; count++; }
        if (valueRating != null) { total += valueRating; count++; }
        
        return count > 0 ? (double) total / count : 0.0;
    }

    /**
     * Checks if this is a complaint (low rating or complaint type).
     *
     * @return true if complaint
     */
    public boolean isComplaint() {
        return feedbackType == FeedbackType.COMPLAINT || 
               (overallRating != null && overallRating <= 2);
    }
}

