package com.foodai.promotion.service;

import com.foodai.promotion.domain.model.CustomerSegment;
import com.foodai.promotion.domain.repository.CustomerSegmentRepository;
import com.foodai.promotion.dto.request.CreateSegmentRequest;
import com.foodai.promotion.dto.response.CustomerSegmentResponse;
import com.foodai.promotion.exception.SegmentNotFoundException;
import com.foodai.promotion.mapper.CustomerSegmentMapper;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing customer segments.
 *
 * @author FoodAI Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomerSegmentService {

  private final CustomerSegmentRepository segmentRepository;
  private final CustomerSegmentMapper segmentMapper;

  /**
   * Creates a new customer segment.
   *
   * @param request the create request
   * @return the created segment
   */
  @Transactional
  public CustomerSegmentResponse createSegment(CreateSegmentRequest request) {
    log.info("Creating customer segment: {}", request.getName());

    CustomerSegment segment = segmentMapper.toEntity(request);
    segment.validate();
    CustomerSegment saved = segmentRepository.save(segment);

    log.info("Customer segment created: id={}, name={}", saved.getId(), saved.getName());
    return segmentMapper.toResponse(saved);
  }

  /**
   * Gets a segment by ID.
   *
   * @param id the segment ID
   * @return the segment
   */
  public CustomerSegmentResponse getSegment(String id) {
    log.debug("Getting segment: {}", id);
    CustomerSegment segment =
        segmentRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new SegmentNotFoundException(id));
    return segmentMapper.toResponse(segment);
  }

  /**
   * Gets all segments with pagination.
   *
   * @param pageable pagination info
   * @return page of segments
   */
  public Page<CustomerSegmentResponse> getAllSegments(Pageable pageable) {
    log.debug("Getting all segments: page={}", pageable.getPageNumber());
    return segmentRepository.findByDeletedFalse(pageable).map(segmentMapper::toResponse);
  }

  /**
   * Gets segments by restaurant.
   *
   * @param restaurantId the restaurant ID
   * @param pageable pagination info
   * @return page of segments
   */
  public Page<CustomerSegmentResponse> getSegmentsByRestaurant(
      String restaurantId, Pageable pageable) {
    log.debug("Getting segments for restaurant: {}", restaurantId);
    return segmentRepository
        .findByRestaurantIdAndDeletedFalse(restaurantId, pageable)
        .map(segmentMapper::toResponse);
  }

  /**
   * Gets active segments.
   *
   * @return list of active segments
   */
  public List<CustomerSegmentResponse> getActiveSegments() {
    log.debug("Getting active segments");
    return segmentRepository.findByActiveTrue().stream()
        .map(segmentMapper::toResponse)
        .collect(Collectors.toList());
  }

  /**
   * Gets active segments for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return list of active segments
   */
  public List<CustomerSegmentResponse> getActiveSegmentsForRestaurant(String restaurantId) {
    log.debug("Getting active segments for restaurant: {}", restaurantId);
    return segmentRepository.findByRestaurantIdAndActiveTrue(restaurantId).stream()
        .map(segmentMapper::toResponse)
        .collect(Collectors.toList());
  }

  /**
   * Updates a segment.
   *
   * @param id the segment ID
   * @param request the update request
   * @return the updated segment
   */
  @Transactional
  public CustomerSegmentResponse updateSegment(String id, CreateSegmentRequest request) {
    log.info("Updating segment: {}", id);
    CustomerSegment segment =
        segmentRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new SegmentNotFoundException(id));

    // Update fields
    segment.setName(request.getName());
    segment.setDescription(request.getDescription());
    segment.setCriteria(request.getCriteria());
    segment.setDynamic(request.isDynamic());
    segment.setStaticUserIds(request.getStaticUserIds());
    segment.setUpdatedAt(Instant.now());

    segment.validate();
    CustomerSegment saved = segmentRepository.save(segment);

    log.info("Segment updated: {}", id);
    return segmentMapper.toResponse(saved);
  }

  /**
   * Activates a segment.
   *
   * @param id the segment ID
   * @return the updated segment
   */
  @Transactional
  public CustomerSegmentResponse activateSegment(String id) {
    log.info("Activating segment: {}", id);
    CustomerSegment segment =
        segmentRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new SegmentNotFoundException(id));

    segment.activate();
    CustomerSegment saved = segmentRepository.save(segment);

    log.info("Segment activated: {}", id);
    return segmentMapper.toResponse(saved);
  }

  /**
   * Deactivates a segment.
   *
   * @param id the segment ID
   * @return the updated segment
   */
  @Transactional
  public CustomerSegmentResponse deactivateSegment(String id) {
    log.info("Deactivating segment: {}", id);
    CustomerSegment segment =
        segmentRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new SegmentNotFoundException(id));

    segment.deactivate();
    CustomerSegment saved = segmentRepository.save(segment);

    log.info("Segment deactivated: {}", id);
    return segmentMapper.toResponse(saved);
  }

  /**
   * Deletes a segment (soft delete).
   *
   * @param id the segment ID
   */
  @Transactional
  public void deleteSegment(String id) {
    log.info("Deleting segment: {}", id);
    CustomerSegment segment =
        segmentRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new SegmentNotFoundException(id));

    segment.delete();
    segmentRepository.save(segment);

    log.info("Segment deleted: {}", id);
  }

  /**
   * Recalculates segment membership for a user.
   *
   * @param userId the user ID
   * @return list of segment IDs the user belongs to
   */
  public List<String> calculateUserSegments(String userId) {
    log.debug("Calculating segments for user: {}", userId);
    // In real implementation, this would evaluate segment criteria
    // For now, return static membership
    return segmentRepository.findByStaticUserIdsContaining(userId).stream()
        .map(CustomerSegment::getId)
        .collect(Collectors.toList());
  }

  /**
   * Adds a user to a static segment.
   *
   * @param segmentId the segment ID
   * @param userId the user ID
   * @return the updated segment
   */
  @Transactional
  public CustomerSegmentResponse addUserToSegment(String segmentId, String userId) {
    log.info("Adding user {} to segment {}", userId, segmentId);
    CustomerSegment segment =
        segmentRepository
            .findByIdAndDeletedFalse(segmentId)
            .orElseThrow(() -> new SegmentNotFoundException(segmentId));

    segment.addUser(userId);
    CustomerSegment saved = segmentRepository.save(segment);

    log.info("User added to segment: userId={}, segmentId={}", userId, segmentId);
    return segmentMapper.toResponse(saved);
  }

  /**
   * Removes a user from a static segment.
   *
   * @param segmentId the segment ID
   * @param userId the user ID
   * @return the updated segment
   */
  @Transactional
  public CustomerSegmentResponse removeUserFromSegment(String segmentId, String userId) {
    log.info("Removing user {} from segment {}", userId, segmentId);
    CustomerSegment segment =
        segmentRepository
            .findByIdAndDeletedFalse(segmentId)
            .orElseThrow(() -> new SegmentNotFoundException(segmentId));

    segment.removeUser(userId);
    CustomerSegment saved = segmentRepository.save(segment);

    log.info("User removed from segment: userId={}, segmentId={}", userId, segmentId);
    return segmentMapper.toResponse(saved);
  }

  /**
   * Gets the count of users in a segment.
   *
   * @param id the segment ID
   * @return the user count
   */
  public int getSegmentUserCount(String id) {
    log.debug("Getting user count for segment: {}", id);
    CustomerSegment segment =
        segmentRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new SegmentNotFoundException(id));
    return segment.getUserCount();
  }
}

