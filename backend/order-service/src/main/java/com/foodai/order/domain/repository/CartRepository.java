package com.foodai.order.domain.repository;

import com.foodai.order.domain.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Cart document.
 *
 * @author FoodAI Team
 */
@Repository
public interface CartRepository extends MongoRepository<Cart, String> {

    /**
     * Finds a cart by user ID.
     */
    Optional<Cart> findByUserId(String userId);

    /**
     * Checks if a cart exists for a user.
     */
    boolean existsByUserId(String userId);

    /**
     * Deletes a cart by user ID.
     */
    void deleteByUserId(String userId);

    /**
     * Finds carts updated before a certain time (for cleanup).
     */
    List<Cart> findByUpdatedAtBefore(Instant beforeTime);

    /**
     * Finds carts by source.
     */
    List<Cart> findBySource(String source);

    /**
     * Finds carts with a specific diet plan ID.
     */
    List<Cart> findByDietPlanId(String dietPlanId);

    /**
     * Finds carts with a specific party plan ID.
     */
    List<Cart> findByPartyPlanId(String partyPlanId);

    /**
     * Finds carts with a specific chat session ID.
     */
    Optional<Cart> findByChatSessionId(String chatSessionId);
}

