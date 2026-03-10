package com.foodai.user.dto.request;

import com.foodai.user.domain.model.FeedbackCategory;
import com.foodai.user.domain.model.FeedbackEntityType;
import com.foodai.user.domain.model.FeedbackType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for creating feedback.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create feedback or ticket")
public class CreateFeedbackRequest {

    @NotNull(message = "User ID is required")
    @Schema(description = "User ID", required = true)
    private String userId;

    @NotNull(message = "Feedback type is required")
    @Schema(description = "Type of feedback")
    private FeedbackType feedbackType;

    @Schema(description = "Category of feedback")
    private FeedbackCategory category;

    @Schema(description = "Sub-category")
    private String subCategory;

    @Schema(description = "Order ID (if related to order)")
    private String orderId;

    @Schema(description = "Entity type being reviewed")
    private FeedbackEntityType entityType;

    @Schema(description = "Entity ID being reviewed")
    private String entityId;

    @Size(max = 200, message = "Title must be at most 200 characters")
    @Schema(description = "Feedback title/subject")
    private String title;

    @Size(max = 2000, message = "Description must be at most 2000 characters")
    @Schema(description = "Detailed description")
    private String description;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Schema(description = "Overall rating (1-5)", example = "4")
    private Integer overallRating;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Schema(description = "Food quality rating (1-5)")
    private Integer foodRating;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Schema(description = "Delivery rating (1-5)")
    private Integer deliveryRating;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Schema(description = "Packaging rating (1-5)")
    private Integer packagingRating;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    @Schema(description = "Value for money rating (1-5)")
    private Integer valueRating;

    @Schema(description = "Attached image URLs")
    private List<String> imageUrls;

    @Schema(description = "Tags for categorization")
    private List<String> tags;

    @Schema(description = "Make feedback public")
    private Boolean isPublic;

    @Schema(description = "Request contact from support")
    private Boolean contactRequested;

    @Schema(description = "Preferred contact method")
    private String preferredContactMethod;

    @Schema(description = "Source of feedback", example = "APP")
    private String source;

    @Schema(description = "Device info")
    private String deviceInfo;

    @Schema(description = "App version")
    private String appVersion;
}

