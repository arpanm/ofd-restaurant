package com.foodai.promotion.service;

import com.foodai.promotion.domain.model.Promotion;
import com.foodai.promotion.domain.model.PromotionStatus;
import com.foodai.promotion.domain.model.PromotionType;
import com.foodai.promotion.domain.repository.PromotionRepository;
import com.foodai.promotion.dto.request.CreatePromotionRequest;
import com.foodai.promotion.dto.response.PromotionResponse;
import com.foodai.promotion.exception.PromotionNotFoundException;
import com.foodai.promotion.mapper.PromotionMapper;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing promotions.
 *
 * @author FoodAI Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PromotionService {

  private final PromotionRepository promotionRepository;
  private final PromotionMapper promotionMapper;

  /**
   * Creates a new promotion.
   *
   * @param request the create request
   * @return the created promotion
   */
  @Transactional
  public PromotionResponse createPromotion(CreatePromotionRequest request) {
    log.info("Creating promotion: {}", request.getName());

    Promotion promotion = promotionMapper.toEntity(request);

    // Generate code if not provided
    if (promotion.getCode() == null || promotion.getCode().isBlank()) {
      promotion.setCode(generatePromoCode());
    } else {
      promotion.setCode(promotion.getCode().toUpperCase());
    }

    // Validate unique code
    if (promotionRepository.existsByCodeAndDeletedFalse(promotion.getCode())) {
      throw new IllegalStateException("Promotion code already exists: " + promotion.getCode());
    }

    // Set initial status based on schedule
    Instant now = Instant.now();
    if (promotion.getSchedule() != null) {
      promotion.updateStatusBySchedule(now);
    }

    promotion.validate();
    Promotion saved = promotionRepository.save(promotion);

    log.info("Promotion created: id={}, code={}", saved.getId(), saved.getCode());
    return promotionMapper.toResponse(saved);
  }

  /**
   * Gets a promotion by ID.
   *
   * @param id the promotion ID
   * @return the promotion
   */
  public PromotionResponse getPromotion(String id) {
    log.debug("Getting promotion: {}", id);
    Promotion promotion =
        promotionRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new PromotionNotFoundException(id));
    return promotionMapper.toResponse(promotion);
  }

  /**
   * Gets a promotion by code.
   *
   * @param code the promotion code
   * @return the promotion
   */
  public PromotionResponse getPromotionByCode(String code) {
    log.debug("Getting promotion by code: {}", code);
    Promotion promotion =
        promotionRepository
            .findByCodeAndDeletedFalse(code.toUpperCase())
            .orElseThrow(() -> new PromotionNotFoundException("code: " + code));
    return promotionMapper.toResponse(promotion);
  }

  /**
   * Gets all promotions with pagination.
   *
   * @param pageable pagination info
   * @return page of promotions
   */
  public Page<PromotionResponse> getAllPromotions(Pageable pageable) {
    log.debug("Getting all promotions: page={}", pageable.getPageNumber());
    return promotionRepository.findByDeletedFalse(pageable).map(promotionMapper::toResponse);
  }

  /**
   * Gets promotions by restaurant.
   *
   * @param restaurantId the restaurant ID
   * @param pageable pagination info
   * @return page of promotions
   */
  public Page<PromotionResponse> getPromotionsByRestaurant(String restaurantId, Pageable pageable) {
    log.debug("Getting promotions for restaurant: {}", restaurantId);
    return promotionRepository
        .findByRestaurantIdAndDeletedFalse(restaurantId, pageable)
        .map(promotionMapper::toResponse);
  }

  /**
   * Gets promotions by status.
   *
   * @param status the status
   * @param pageable pagination info
   * @return page of promotions
   */
  public Page<PromotionResponse> getPromotionsByStatus(PromotionStatus status, Pageable pageable) {
    log.debug("Getting promotions by status: {}", status);
    return promotionRepository
        .findByStatusAndDeletedFalse(status, pageable)
        .map(promotionMapper::toResponse);
  }

  /**
   * Gets promotions by restaurant and status.
   *
   * @param restaurantId the restaurant ID
   * @param status the status
   * @param pageable pagination info
   * @return page of promotions
   */
  public Page<PromotionResponse> getPromotionsByRestaurantAndStatus(
      String restaurantId, PromotionStatus status, Pageable pageable) {
    log.debug("Getting promotions for restaurant {} with status {}", restaurantId, status);
    return promotionRepository
        .findByRestaurantIdAndStatusAndDeletedFalse(restaurantId, status, pageable)
        .map(promotionMapper::toResponse);
  }

  /**
   * Gets active promotions for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return list of active promotions
   */
  public List<PromotionResponse> getActivePromotionsForRestaurant(String restaurantId) {
    log.debug("Getting active promotions for restaurant: {}", restaurantId);
    return promotionRepository
        .findActivePromotionsForRestaurant(restaurantId, Instant.now())
        .stream()
        .map(promotionMapper::toResponse)
        .collect(Collectors.toList());
  }

  /**
   * Activates a promotion.
   *
   * @param id the promotion ID
   * @return the updated promotion
   */
  @Transactional
  public PromotionResponse activatePromotion(String id) {
    log.info("Activating promotion: {}", id);
    Promotion promotion =
        promotionRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new PromotionNotFoundException(id));

    promotion.activate();
    Promotion saved = promotionRepository.save(promotion);

    log.info("Promotion activated: {}", id);
    return promotionMapper.toResponse(saved);
  }

  /**
   * Pauses a promotion.
   *
   * @param id the promotion ID
   * @return the updated promotion
   */
  @Transactional
  public PromotionResponse pausePromotion(String id) {
    log.info("Pausing promotion: {}", id);
    Promotion promotion =
        promotionRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new PromotionNotFoundException(id));

    promotion.pause();
    Promotion saved = promotionRepository.save(promotion);

    log.info("Promotion paused: {}", id);
    return promotionMapper.toResponse(saved);
  }

  /**
   * Resumes a paused promotion.
   *
   * @param id the promotion ID
   * @return the updated promotion
   */
  @Transactional
  public PromotionResponse resumePromotion(String id) {
    log.info("Resuming promotion: {}", id);
    Promotion promotion =
        promotionRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new PromotionNotFoundException(id));

    promotion.resume();
    Promotion saved = promotionRepository.save(promotion);

    log.info("Promotion resumed: {}", id);
    return promotionMapper.toResponse(saved);
  }

  /**
   * Ends a promotion.
   *
   * @param id the promotion ID
   * @return the updated promotion
   */
  @Transactional
  public PromotionResponse endPromotion(String id) {
    log.info("Ending promotion: {}", id);
    Promotion promotion =
        promotionRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new PromotionNotFoundException(id));

    promotion.end();
    Promotion saved = promotionRepository.save(promotion);

    log.info("Promotion ended: {}", id);
    return promotionMapper.toResponse(saved);
  }

  /**
   * Deletes a promotion (soft delete).
   *
   * @param id the promotion ID
   */
  @Transactional
  public void deletePromotion(String id) {
    log.info("Deleting promotion: {}", id);
    Promotion promotion =
        promotionRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new PromotionNotFoundException(id));

    promotion.delete();
    promotionRepository.save(promotion);

    log.info("Promotion deleted: {}", id);
  }

  /**
   * Records a usage of the promotion.
   *
   * @param id the promotion ID
   * @return the new usage count
   */
  @Transactional
  public int recordUsage(String id) {
    log.debug("Recording usage for promotion: {}", id);
    Promotion promotion =
        promotionRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new PromotionNotFoundException(id));

    int usage = promotion.recordUsage();
    promotionRepository.save(promotion);

    log.debug("Promotion usage recorded: id={}, count={}", id, usage);
    return usage;
  }

  /**
   * Generates a unique promo code.
   *
   * @return the generated code
   */
  private String generatePromoCode() {
    String code = "PROMO" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    while (promotionRepository.existsByCodeAndDeletedFalse(code)) {
      code = "PROMO" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
    return code;
  }
}

