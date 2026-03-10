package com.foodai.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * Value Object representing a comment on feedback.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackCommentVO {

    /**
     * Comment unique identifier
     */
    private String id;

    /**
     * Comment author ID
     */
    private String authorId;

    /**
     * Author name for display
     */
    private String authorName;

    /**
     * Author role (USER, SUPPORT_AGENT, RESTAURANT, SYSTEM)
     */
    private String authorRole;

    /**
     * Whether this comment is from the user (vs support)
     */
    private boolean fromUser;

    /**
     * Comment text
     */
    private String text;

    /**
     * Attached images
     */
    private List<String> attachments;

    /**
     * Whether this is an internal note (not visible to user)
     */
    @Builder.Default
    private boolean internalNote = false;

    /**
     * Creation timestamp
     */
    private Instant createdAt;

    /**
     * Edit timestamp (if edited)
     */
    private Instant editedAt;
}

