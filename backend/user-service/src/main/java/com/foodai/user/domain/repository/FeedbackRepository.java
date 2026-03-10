package com.foodai.user.domain.repository;

import com.foodai.user.domain.model.Feedback;
import com.foodai.user.domain.model.FeedbackCategory;
import com.foodai.user.domain.model.FeedbackEntityType;
import com.foodai.user.domain.model.FeedbackPriority;
import com.foodai.user.domain.model.FeedbackStatus;
import com.foodai.user.domain.model.FeedbackType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Feedback aggregate.
 * Provides data access operations for feedback and ticket entities.
 *
 * @author FoodAI Team
 */
@Repository
public interface FeedbackRepository extends MongoRepository<Feedback, String> {

    /**
     * Finds a non-deleted feedback by ID.
     *
     * @param id the feedback ID
     * @return Optional containing the feedback if found
     */
    Optional<Feedback> findByIdAndDeletedFalse(String id);

    /**
     * Finds all feedback for a user.
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of feedback
     */
    Page<Feedback> findByUserIdAndDeletedFalse(String userId, Pageable pageable);

    /**
     * Finds feedback for an order.
     *
     * @param orderId the order ID
     * @return list of feedback for the order
     */
    List<Feedback> findByOrderIdAndDeletedFalse(String orderId);

    /**
     * Finds feedback for an entity.
     *
     * @param entityType the entity type
     * @param entityId the entity ID
     * @param pageable pagination information
     * @return page of feedback
     */
    Page<Feedback> findByEntityTypeAndEntityIdAndDeletedFalse(
        FeedbackEntityType entityType, String entityId, Pageable pageable);

    /**
     * Finds feedback by type.
     *
     * @param feedbackType the feedback type
     * @param pageable pagination information
     * @return page of feedback
     */
    Page<Feedback> findByFeedbackTypeAndDeletedFalse(FeedbackType feedbackType, Pageable pageable);

    /**
     * Finds feedback by status.
     *
     * @param status the feedback status
     * @param pageable pagination information
     * @return page of feedback
     */
    Page<Feedback> findByStatusAndDeletedFalse(FeedbackStatus status, Pageable pageable);

    /**
     * Finds feedback by category.
     *
     * @param category the feedback category
     * @param pageable pagination information
     * @return page of feedback
     */
    Page<Feedback> findByCategoryAndDeletedFalse(FeedbackCategory category, Pageable pageable);

    /**
     * Finds feedback by priority.
     *
     * @param priority the priority level
     * @param pageable pagination information
     * @return page of feedback
     */
    Page<Feedback> findByPriorityAndDeletedFalse(FeedbackPriority priority, Pageable pageable);

    /**
     * Finds feedback assigned to an agent.
     *
     * @param assignedTo the agent ID
     * @param pageable pagination information
     * @return page of feedback
     */
    Page<Feedback> findByAssignedToAndDeletedFalse(String assignedTo, Pageable pageable);

    /**
     * Finds unassigned feedback.
     *
     * @param pageable pagination information
     * @return page of unassigned feedback
     */
    @Query("{ 'deleted': false, 'assignedTo': null, 'status': 'SUBMITTED' }")
    Page<Feedback> findUnassignedFeedback(Pageable pageable);

    /**
     * Finds public reviews for an entity.
     *
     * @param entityType the entity type
     * @param entityId the entity ID
     * @param pageable pagination information
     * @return page of public reviews
     */
    @Query("{ 'deleted': false, 'isPublic': true, 'entityType': ?0, 'entityId': ?1, 'feedbackType': 'REVIEW' }")
    Page<Feedback> findPublicReviews(FeedbackEntityType entityType, String entityId, Pageable pageable);

    /**
     * Calculates average rating for an entity.
     *
     * @param entityType the entity type
     * @param entityId the entity ID
     * @return average rating
     */
    @Query(value = "{ 'deleted': false, 'entityType': ?0, 'entityId': ?1, 'overallRating': { $ne: null } }",
           count = true)
    long countRatingsForEntity(FeedbackEntityType entityType, String entityId);

    /**
     * Finds feedback created after a certain time.
     *
     * @param afterTime the cutoff time
     * @param pageable pagination information
     * @return page of recent feedback
     */
    Page<Feedback> findByCreatedAtAfterAndDeletedFalse(Instant afterTime, Pageable pageable);

    /**
     * Finds high priority unresolved feedback.
     *
     * @return list of high priority feedback
     */
    @Query("{ 'deleted': false, 'priority': { $in: ['HIGH', 'CRITICAL'] }, 'status': { $nin: ['RESOLVED', 'CLOSED'] } }")
    List<Feedback> findHighPriorityUnresolvedFeedback();

    /**
     * Counts feedback by status for reporting.
     *
     * @param status the status
     * @return count
     */
    long countByStatusAndDeletedFalse(FeedbackStatus status);

    /**
     * Counts complaints for a user.
     *
     * @param userId the user ID
     * @return count of complaints
     */
    @Query(value = "{ 'deleted': false, 'userId': ?0, 'feedbackType': 'COMPLAINT' }", count = true)
    long countComplaintsForUser(String userId);
}

