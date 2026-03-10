package com.foodai.promotion.service;

import com.foodai.promotion.domain.model.Campaign;
import com.foodai.promotion.domain.model.CampaignStatus;
import com.foodai.promotion.domain.repository.CampaignRepository;
import com.foodai.promotion.dto.request.CreateCampaignRequest;
import com.foodai.promotion.dto.response.CampaignResponse;
import com.foodai.promotion.exception.CampaignNotFoundException;
import com.foodai.promotion.infrastructure.messaging.PromotionEventPublisher;
import com.foodai.promotion.mapper.CampaignMapper;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing marketing campaigns.
 *
 * @author FoodAI Team
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CampaignService {

  private final CampaignRepository campaignRepository;
  private final CampaignMapper campaignMapper;
  private final PromotionEventPublisher eventPublisher;

  /**
   * Creates a new campaign.
   *
   * @param request the create request
   * @return the created campaign
   */
  @Transactional
  public CampaignResponse createCampaign(CreateCampaignRequest request) {
    log.info("Creating campaign: {}", request.getName());

    Campaign campaign = campaignMapper.toEntity(request);
    campaign.validate();
    Campaign saved = campaignRepository.save(campaign);

    eventPublisher.publishCampaignCreated(saved);
    log.info("Campaign created: id={}, name={}", saved.getId(), saved.getName());
    return campaignMapper.toResponse(saved);
  }

  /**
   * Gets a campaign by ID.
   *
   * @param id the campaign ID
   * @return the campaign
   */
  public CampaignResponse getCampaign(String id) {
    log.debug("Getting campaign: {}", id);
    Campaign campaign =
        campaignRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new CampaignNotFoundException(id));
    return campaignMapper.toResponse(campaign);
  }

  /**
   * Gets all campaigns with pagination.
   *
   * @param pageable pagination info
   * @return page of campaigns
   */
  public Page<CampaignResponse> getAllCampaigns(Pageable pageable) {
    log.debug("Getting all campaigns: page={}", pageable.getPageNumber());
    return campaignRepository.findByDeletedFalse(pageable).map(campaignMapper::toResponse);
  }

  /**
   * Gets campaigns by restaurant.
   *
   * @param restaurantId the restaurant ID
   * @param pageable pagination info
   * @return page of campaigns
   */
  public Page<CampaignResponse> getCampaignsByRestaurant(String restaurantId, Pageable pageable) {
    log.debug("Getting campaigns for restaurant: {}", restaurantId);
    return campaignRepository
        .findByRestaurantIdAndDeletedFalse(restaurantId, pageable)
        .map(campaignMapper::toResponse);
  }

  /**
   * Gets campaigns by status.
   *
   * @param status the status
   * @param pageable pagination info
   * @return page of campaigns
   */
  public Page<CampaignResponse> getCampaignsByStatus(CampaignStatus status, Pageable pageable) {
    log.debug("Getting campaigns by status: {}", status);
    return campaignRepository
        .findByStatusAndDeletedFalse(status, pageable)
        .map(campaignMapper::toResponse);
  }

  /**
   * Gets active campaigns for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return list of active campaigns
   */
  public List<CampaignResponse> getActiveCampaigns(String restaurantId) {
    log.debug("Getting active campaigns for restaurant: {}", restaurantId);
    return campaignRepository.findActiveCampaigns(restaurantId, Instant.now()).stream()
        .map(campaignMapper::toResponse)
        .collect(Collectors.toList());
  }

  /**
   * Updates a campaign.
   *
   * @param id the campaign ID
   * @param request the update request
   * @return the updated campaign
   */
  @Transactional
  public CampaignResponse updateCampaign(String id, CreateCampaignRequest request) {
    log.info("Updating campaign: {}", id);
    Campaign campaign =
        campaignRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new CampaignNotFoundException(id));

    // Update fields
    campaign.setName(request.getName());
    campaign.setDescription(request.getDescription());
    campaign.setType(request.getType());
    campaign.setChannels(request.getChannels());
    campaign.setTargetSegmentIds(request.getTargetSegmentIds());
    campaign.setContent(request.getContent());
    campaign.setSchedule(request.getSchedule());
    campaign.setBudget(request.getBudget());
    campaign.setLinkedPromotionIds(request.getLinkedPromotionIds());
    campaign.setLinkedCouponIds(request.getLinkedCouponIds());
    campaign.setTrackingEnabled(request.isTrackingEnabled());
    campaign.setAbTestEnabled(request.isAbTestEnabled());
    campaign.setVariantPercentage(request.getVariantPercentage());

    campaign.validate();
    Campaign saved = campaignRepository.save(campaign);

    eventPublisher.publishCampaignUpdated(saved);
    log.info("Campaign updated: {}", id);
    return campaignMapper.toResponse(saved);
  }

  /**
   * Activates a campaign.
   *
   * @param id the campaign ID
   * @return the updated campaign
   */
  @Transactional
  public CampaignResponse activateCampaign(String id) {
    log.info("Activating campaign: {}", id);
    Campaign campaign =
        campaignRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new CampaignNotFoundException(id));

    campaign.activate();
    Campaign saved = campaignRepository.save(campaign);

    eventPublisher.publishCampaignActivated(saved);
    log.info("Campaign activated: {}", id);
    return campaignMapper.toResponse(saved);
  }

  /**
   * Pauses a campaign.
   *
   * @param id the campaign ID
   * @return the updated campaign
   */
  @Transactional
  public CampaignResponse pauseCampaign(String id) {
    log.info("Pausing campaign: {}", id);
    Campaign campaign =
        campaignRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new CampaignNotFoundException(id));

    campaign.pause();
    Campaign saved = campaignRepository.save(campaign);

    eventPublisher.publishCampaignPaused(saved);
    log.info("Campaign paused: {}", id);
    return campaignMapper.toResponse(saved);
  }

  /**
   * Completes a campaign.
   *
   * @param id the campaign ID
   * @return the updated campaign
   */
  @Transactional
  public CampaignResponse completeCampaign(String id) {
    log.info("Completing campaign: {}", id);
    Campaign campaign =
        campaignRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new CampaignNotFoundException(id));

    campaign.complete();
    Campaign saved = campaignRepository.save(campaign);

    eventPublisher.publishCampaignCompleted(saved);
    log.info("Campaign completed: {}", id);
    return campaignMapper.toResponse(saved);
  }

  /**
   * Archives a campaign.
   *
   * @param id the campaign ID
   * @return the updated campaign
   */
  @Transactional
  public CampaignResponse archiveCampaign(String id) {
    log.info("Archiving campaign: {}", id);
    Campaign campaign =
        campaignRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new CampaignNotFoundException(id));

    campaign.archive();
    Campaign saved = campaignRepository.save(campaign);

    log.info("Campaign archived: {}", id);
    return campaignMapper.toResponse(saved);
  }

  /**
   * Deletes a campaign (soft delete).
   *
   * @param id the campaign ID
   */
  @Transactional
  public void deleteCampaign(String id) {
    log.info("Deleting campaign: {}", id);
    Campaign campaign =
        campaignRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new CampaignNotFoundException(id));

    campaign.delete();
    campaignRepository.save(campaign);

    log.info("Campaign deleted: {}", id);
  }

  /**
   * Records engagement metrics for a campaign.
   *
   * @param id the campaign ID
   * @param impressions number of impressions to add
   * @param clicks number of clicks to add
   * @param conversions number of conversions to add
   */
  @Transactional
  public void recordMetrics(String id, int impressions, int clicks, int conversions) {
    log.debug(
        "Recording metrics for campaign {}: impressions={}, clicks={}, conversions={}",
        id,
        impressions,
        clicks,
        conversions);

    Campaign campaign =
        campaignRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new CampaignNotFoundException(id));

    campaign.recordMetrics(impressions, clicks, conversions);
    campaignRepository.save(campaign);

    log.debug("Metrics recorded for campaign: {}", id);
  }

  /**
   * Updates budget spent for a campaign.
   *
   * @param id the campaign ID
   * @param amount the amount spent
   */
  @Transactional
  public void recordSpend(String id, java.math.BigDecimal amount) {
    log.debug("Recording spend for campaign {}: amount={}", id, amount);

    Campaign campaign =
        campaignRepository
            .findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new CampaignNotFoundException(id));

    campaign.recordSpend(amount);
    campaignRepository.save(campaign);

    log.debug("Spend recorded for campaign: {}", id);
  }

  /**
   * Scheduled task to update campaign statuses based on schedule.
   */
  @Scheduled(fixedRate = 60000) // Every minute
  @Transactional
  public void updateCampaignStatuses() {
    log.debug("Updating campaign statuses based on schedule");

    Instant now = Instant.now();

    // Start scheduled campaigns
    List<Campaign> toStart =
        campaignRepository.findByStatusAndScheduleStartTimeBefore(CampaignStatus.SCHEDULED, now);
    for (Campaign campaign : toStart) {
      campaign.activate();
      campaignRepository.save(campaign);
      eventPublisher.publishCampaignActivated(campaign);
      log.info("Campaign auto-started: {}", campaign.getId());
    }

    // Complete expired campaigns
    List<Campaign> toComplete =
        campaignRepository.findByStatusAndScheduleEndTimeBefore(CampaignStatus.ACTIVE, now);
    for (Campaign campaign : toComplete) {
      campaign.complete();
      campaignRepository.save(campaign);
      eventPublisher.publishCampaignCompleted(campaign);
      log.info("Campaign auto-completed: {}", campaign.getId());
    }
  }
}

