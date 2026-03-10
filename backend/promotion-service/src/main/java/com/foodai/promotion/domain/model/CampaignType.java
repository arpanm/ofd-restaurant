package com.foodai.promotion.domain.model;

/**
 * Type of marketing campaign.
 *
 * @author FoodAI Team
 */
public enum CampaignType {
  /** Promotional campaigns with discounts. */
  PROMOTIONAL,

  /** Seasonal campaigns (holidays, festivals). */
  SEASONAL,

  /** Flash sale campaigns. */
  FLASH_SALE,

  /** Loyalty reward campaigns. */
  LOYALTY,

  /** Re-engagement campaigns for dormant users. */
  RE_ENGAGEMENT,

  /** Brand awareness campaigns. */
  AWARENESS,

  /** Referral campaigns. */
  REFERRAL,

  /** New user acquisition campaigns. */
  ACQUISITION,

  /** Upsell/cross-sell campaigns. */
  UPSELL
}

