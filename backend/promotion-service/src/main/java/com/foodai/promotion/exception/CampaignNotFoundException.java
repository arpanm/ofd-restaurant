package com.foodai.promotion.exception;

/**
 * Exception thrown when a campaign is not found.
 *
 * @author FoodAI Team
 */
public class CampaignNotFoundException extends RuntimeException {

  private static final String DEFAULT_MESSAGE = "Campaign not found with id: ";

  /**
   * Creates a new exception with campaign ID.
   *
   * @param campaignId the campaign ID
   */
  public CampaignNotFoundException(String campaignId) {
    super(DEFAULT_MESSAGE + campaignId);
  }

  /**
   * Creates a new exception with message and cause.
   *
   * @param message the message
   * @param cause the cause
   */
  public CampaignNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}

