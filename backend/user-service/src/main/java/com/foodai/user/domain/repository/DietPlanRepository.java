package com.foodai.user.domain.repository;

import com.foodai.user.domain.model.DietPlan;
import com.foodai.user.domain.model.DietPlanStatus;
import com.foodai.user.domain.model.DietPlanType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for DietPlan aggregate.
 * Provides data access operations for diet plan entities.
 *
 * @author FoodAI Team
 */
@Repository
public interface DietPlanRepository extends MongoRepository<DietPlan, String> {

    /**
     * Finds a non-deleted diet plan by ID.
     *
     * @param id the plan ID
     * @return Optional containing the plan if found
     */
    Optional<DietPlan> findByIdAndDeletedFalse(String id);

    /**
     * Finds all diet plans for a user.
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of diet plans
     */
    Page<DietPlan> findByUserIdAndDeletedFalse(String userId, Pageable pageable);

    /**
     * Finds active diet plans for a user.
     *
     * @param userId the user ID
     * @return list of active plans
     */
    List<DietPlan> findByUserIdAndStatusAndDeletedFalse(String userId, DietPlanStatus status);

    /**
     * Finds diet plans by status.
     *
     * @param status the plan status
     * @param pageable pagination information
     * @return page of plans
     */
    Page<DietPlan> findByStatusAndDeletedFalse(DietPlanStatus status, Pageable pageable);

    /**
     * Finds diet plans by type.
     *
     * @param planType the plan type
     * @param pageable pagination information
     * @return page of plans
     */
    Page<DietPlan> findByPlanTypeAndDeletedFalse(DietPlanType planType, Pageable pageable);

    /**
     * Finds plans starting on a specific date.
     *
     * @param startDate the start date
     * @return list of plans starting on that date
     */
    List<DietPlan> findByStartDateAndDeletedFalse(LocalDate startDate);

    /**
     * Finds plans with meals scheduled for today.
     *
     * @param today today's date
     * @return list of active plans
     */
    @Query("{ 'deleted': false, 'status': 'ACTIVE', 'startDate': { $lte: ?0 }, 'endDate': { $gte: ?0 } }")
    List<DietPlan> findActivePlansForDate(LocalDate today);

    /**
     * Finds plans with auto-order enabled.
     *
     * @return list of auto-order enabled plans
     */
    @Query("{ 'deleted': false, 'status': 'ACTIVE', 'autoOrderEnabled': true }")
    List<DietPlan> findAutoOrderEnabledPlans();

    /**
     * Counts active plans for a user.
     *
     * @param userId the user ID
     * @return count of active plans
     */
    long countByUserIdAndStatusAndDeletedFalse(String userId, DietPlanStatus status);

    /**
     * Checks if user has any active plan.
     *
     * @param userId the user ID
     * @return true if user has active plan
     */
    @Query(value = "{ 'userId': ?0, 'status': 'ACTIVE', 'deleted': false }", exists = true)
    boolean hasActivePlan(String userId);
}

