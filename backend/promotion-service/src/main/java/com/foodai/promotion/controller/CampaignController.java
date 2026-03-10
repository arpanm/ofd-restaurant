package com.foodai.promotion.controller;

import com.foodai.promotion.domain.model.CampaignStatus;
import com.foodai.promotion.dto.request.CreateCampaignRequest;
import com.foodai.promotion.dto.response.ApiResponse;
import com.foodai.promotion.dto.response.CampaignResponse;
import com.foodai.promotion.service.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing campaigns.
 *
 * @author FoodAI Team
 */
@RestController
@RequestMapping("/api/v1/campaigns")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Campaigns", description = "Campaign management endpoints")
public class CampaignController {

  private final CampaignService campaignService;

  /**
   * Creates a new campaign.
   *
   * @param request the create request
   * @return the created campaign
   */
  @PostMapping
  @Operation(summary = "Create a new campaign")
  public ResponseEntity<ApiResponse<CampaignResponse>> createCampaign(
      @Valid @RequestBody CreateCampaignRequest request) {
    log.info("REST request to create campaign: {}", request.getName());
    CampaignResponse response = campaignService.createCampaign(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(response, "Campaign created successfully"));
  }

  /**
   * Gets a campaign by ID.
   *
   * @param id the campaign ID
   * @return the campaign
   */
  @GetMapping("/{id}")
  @Operation(summary = "Get campaign by ID")
  public ResponseEntity<ApiResponse<CampaignResponse>> getCampaign(
      @Parameter(description = "Campaign ID") @PathVariable String id) {
    log.debug("REST request to get campaign: {}", id);
    CampaignResponse response = campaignService.getCampaign(id);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  /**
   * Gets all campaigns.
   *
   * @param pageable pagination info
   * @return page of campaigns
   */
  @GetMapping
  @Operation(summary = "Get all campaigns")
  public ResponseEntity<ApiResponse<Page<CampaignResponse>>> getAllCampaigns(
      @PageableDefault(size = 20) Pageable pageable) {
    log.debug("REST request to get all campaigns");
    Page<CampaignResponse> page = campaignService.getAllCampaigns(pageable);
    return ResponseEntity.ok(ApiResponse.success(page));
  }

  /**
   * Gets campaigns by restaurant.
   *
   * @param restaurantId the restaurant ID
   * @param pageable pagination info
   * @return page of campaigns
   */
  @GetMapping("/restaurant/{restaurantId}")
  @Operation(summary = "Get campaigns by restaurant")
  public ResponseEntity<ApiResponse<Page<CampaignResponse>>> getCampaignsByRestaurant(
      @Parameter(description = "Restaurant ID") @PathVariable String restaurantId,
      @PageableDefault(size = 20) Pageable pageable) {
    log.debug("REST request to get campaigns for restaurant: {}", restaurantId);
    Page<CampaignResponse> page = campaignService.getCampaignsByRestaurant(restaurantId, pageable);
    return ResponseEntity.ok(ApiResponse.success(page));
  }

  /**
   * Gets campaigns by status.
   *
   * @param status the status
   * @param pageable pagination info
   * @return page of campaigns
   */
  @GetMapping("/status/{status}")
  @Operation(summary = "Get campaigns by status")
  public ResponseEntity<ApiResponse<Page<CampaignResponse>>> getCampaignsByStatus(
      @Parameter(description = "Campaign status") @PathVariable CampaignStatus status,
      @PageableDefault(size = 20) Pageable pageable) {
    log.debug("REST request to get campaigns by status: {}", status);
    Page<CampaignResponse> page = campaignService.getCampaignsByStatus(status, pageable);
    return ResponseEntity.ok(ApiResponse.success(page));
  }

  /**
   * Gets active campaigns for a restaurant.
   *
   * @param restaurantId the restaurant ID
   * @return list of active campaigns
   */
  @GetMapping("/restaurant/{restaurantId}/active")
  @Operation(summary = "Get active campaigns for restaurant")
  public ResponseEntity<ApiResponse<List<CampaignResponse>>> getActiveCampaigns(
      @Parameter(description = "Restaurant ID") @PathVariable String restaurantId) {
    log.debug("REST request to get active campaigns for restaurant: {}", restaurantId);
    List<CampaignResponse> campaigns = campaignService.getActiveCampaigns(restaurantId);
    return ResponseEntity.ok(ApiResponse.success(campaigns));
  }

  /**
   * Updates a campaign.
   *
   * @param id the campaign ID
   * @param request the update request
   * @return the updated campaign
   */
  @PutMapping("/{id}")
  @Operation(summary = "Update a campaign")
  public ResponseEntity<ApiResponse<CampaignResponse>> updateCampaign(
      @Parameter(description = "Campaign ID") @PathVariable String id,
      @Valid @RequestBody CreateCampaignRequest request) {
    log.info("REST request to update campaign: {}", id);
    CampaignResponse response = campaignService.updateCampaign(id, request);
    return ResponseEntity.ok(ApiResponse.success(response, "Campaign updated"));
  }

  /**
   * Activates a campaign.
   *
   * @param id the campaign ID
   * @return the updated campaign
   */
  @PutMapping("/{id}/activate")
  @Operation(summary = "Activate a campaign")
  public ResponseEntity<ApiResponse<CampaignResponse>> activateCampaign(
      @Parameter(description = "Campaign ID") @PathVariable String id) {
    log.info("REST request to activate campaign: {}", id);
    CampaignResponse response = campaignService.activateCampaign(id);
    return ResponseEntity.ok(ApiResponse.success(response, "Campaign activated"));
  }

  /**
   * Pauses a campaign.
   *
   * @param id the campaign ID
   * @return the updated campaign
   */
  @PutMapping("/{id}/pause")
  @Operation(summary = "Pause a campaign")
  public ResponseEntity<ApiResponse<CampaignResponse>> pauseCampaign(
      @Parameter(description = "Campaign ID") @PathVariable String id) {
    log.info("REST request to pause campaign: {}", id);
    CampaignResponse response = campaignService.pauseCampaign(id);
    return ResponseEntity.ok(ApiResponse.success(response, "Campaign paused"));
  }

  /**
   * Completes a campaign.
   *
   * @param id the campaign ID
   * @return the updated campaign
   */
  @PutMapping("/{id}/complete")
  @Operation(summary = "Complete a campaign")
  public ResponseEntity<ApiResponse<CampaignResponse>> completeCampaign(
      @Parameter(description = "Campaign ID") @PathVariable String id) {
    log.info("REST request to complete campaign: {}", id);
    CampaignResponse response = campaignService.completeCampaign(id);
    return ResponseEntity.ok(ApiResponse.success(response, "Campaign completed"));
  }

  /**
   * Archives a campaign.
   *
   * @param id the campaign ID
   * @return the updated campaign
   */
  @PutMapping("/{id}/archive")
  @Operation(summary = "Archive a campaign")
  public ResponseEntity<ApiResponse<CampaignResponse>> archiveCampaign(
      @Parameter(description = "Campaign ID") @PathVariable String id) {
    log.info("REST request to archive campaign: {}", id);
    CampaignResponse response = campaignService.archiveCampaign(id);
    return ResponseEntity.ok(ApiResponse.success(response, "Campaign archived"));
  }

  /**
   * Records metrics for a campaign.
   *
   * @param id the campaign ID
   * @param impressions number of impressions
   * @param clicks number of clicks
   * @param conversions number of conversions
   * @return success response
   */
  @PostMapping("/{id}/metrics")
  @Operation(summary = "Record metrics for a campaign")
  public ResponseEntity<ApiResponse<Void>> recordMetrics(
      @Parameter(description = "Campaign ID") @PathVariable String id,
      @RequestParam(defaultValue = "0") int impressions,
      @RequestParam(defaultValue = "0") int clicks,
      @RequestParam(defaultValue = "0") int conversions) {
    log.debug("REST request to record metrics for campaign: {}", id);
    campaignService.recordMetrics(id, impressions, clicks, conversions);
    return ResponseEntity.ok(ApiResponse.success(null, "Metrics recorded"));
  }

  /**
   * Records spend for a campaign.
   *
   * @param id the campaign ID
   * @param amount the amount spent
   * @return success response
   */
  @PostMapping("/{id}/spend")
  @Operation(summary = "Record spend for a campaign")
  public ResponseEntity<ApiResponse<Void>> recordSpend(
      @Parameter(description = "Campaign ID") @PathVariable String id,
      @RequestParam BigDecimal amount) {
    log.debug("REST request to record spend for campaign: {}", id);
    campaignService.recordSpend(id, amount);
    return ResponseEntity.ok(ApiResponse.success(null, "Spend recorded"));
  }

  /**
   * Deletes a campaign.
   *
   * @param id the campaign ID
   * @return no content
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a campaign")
  public ResponseEntity<ApiResponse<Void>> deleteCampaign(
      @Parameter(description = "Campaign ID") @PathVariable String id) {
    log.info("REST request to delete campaign: {}", id);
    campaignService.deleteCampaign(id);
    return ResponseEntity.ok(ApiResponse.success(null, "Campaign deleted"));
  }
}

