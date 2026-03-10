package com.foodai.order.domain.repository;

import com.foodai.order.domain.model.Order;
import com.foodai.order.domain.model.OrderStatus;
import com.foodai.order.domain.model.OrderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order aggregate.
 *
 * @author FoodAI Team
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, String> {

    /**
     * Finds an order by ID that is not deleted.
     */
    Optional<Order> findByIdAndDeletedFalse(String id);

    /**
     * Finds an order by order number.
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Finds all orders for a user.
     */
    Page<Order> findByUserIdAndDeletedFalseOrderByCreatedAtDesc(String userId, Pageable pageable);

    /**
     * Finds orders by user ID and status.
     */
    Page<Order> findByUserIdAndStatusAndDeletedFalse(String userId, OrderStatus status, Pageable pageable);

    /**
     * Finds orders by user ID and multiple statuses.
     */
    Page<Order> findByUserIdAndStatusInAndDeletedFalse(String userId, List<OrderStatus> statuses, Pageable pageable);

    /**
     * Finds active orders for a user (not delivered or cancelled).
     */
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.deleted = false " +
           "AND o.status NOT IN ('DELIVERED', 'CANCELLED', 'RETURNED') ORDER BY o.createdAt DESC")
    List<Order> findActiveOrdersByUserId(@Param("userId") String userId);

    /**
     * Finds past orders for a user (delivered, cancelled, or returned).
     */
    @Query("SELECT o FROM Order o WHERE o.userId = :userId AND o.deleted = false " +
           "AND o.status IN ('DELIVERED', 'CANCELLED', 'RETURNED') ORDER BY o.createdAt DESC")
    Page<Order> findPastOrdersByUserId(@Param("userId") String userId, Pageable pageable);

    /**
     * Finds orders by restaurant ID.
     */
    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.restaurantId = :restaurantId " +
           "AND o.deleted = false ORDER BY o.createdAt DESC")
    Page<Order> findByRestaurantId(@Param("restaurantId") String restaurantId, Pageable pageable);

    /**
     * Finds orders by restaurant ID and status.
     */
    @Query("SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.restaurantId = :restaurantId " +
           "AND o.status = :status AND o.deleted = false ORDER BY o.createdAt DESC")
    Page<Order> findByRestaurantIdAndStatus(@Param("restaurantId") String restaurantId, 
                                            @Param("status") OrderStatus status, 
                                            Pageable pageable);

    /**
     * Finds orders by rider ID.
     */
    Page<Order> findByRiderIdAndDeletedFalseOrderByCreatedAtDesc(String riderId, Pageable pageable);

    /**
     * Finds orders by order type.
     */
    Page<Order> findByOrderTypeAndDeletedFalse(OrderType orderType, Pageable pageable);

    /**
     * Finds orders by diet plan ID.
     */
    List<Order> findByDietPlanIdAndDeletedFalse(String dietPlanId);

    /**
     * Finds orders by party plan ID.
     */
    List<Order> findByPartyPlanIdAndDeletedFalse(String partyPlanId);

    /**
     * Finds orders created within a date range.
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate " +
           "AND o.deleted = false ORDER BY o.createdAt DESC")
    Page<Order> findByDateRange(@Param("startDate") Instant startDate, 
                                @Param("endDate") Instant endDate, 
                                Pageable pageable);

    /**
     * Counts orders by status for a restaurant.
     */
    @Query("SELECT COUNT(DISTINCT o) FROM Order o JOIN o.items i WHERE i.restaurantId = :restaurantId " +
           "AND o.status = :status AND o.deleted = false")
    long countByRestaurantIdAndStatus(@Param("restaurantId") String restaurantId, 
                                      @Param("status") OrderStatus status);

    /**
     * Counts total orders for a user.
     */
    long countByUserIdAndDeletedFalse(String userId);

    /**
     * Finds orders pending delivery assignment.
     */
    List<Order> findByStatusAndRiderIdIsNullAndDeletedFalse(OrderStatus status);

    /**
     * Checks if order number exists.
     */
    boolean existsByOrderNumber(String orderNumber);

    /**
     * Finds orders with scheduled delivery time.
     */
    @Query("SELECT o FROM Order o WHERE o.scheduledDeliveryTime IS NOT NULL " +
           "AND o.scheduledDeliveryTime BETWEEN :startTime AND :endTime " +
           "AND o.status = 'CONFIRMED' AND o.deleted = false")
    List<Order> findScheduledOrders(@Param("startTime") Instant startTime, 
                                    @Param("endTime") Instant endTime);
}

