package com.foodai.promotion.domain.model;

/**
 * Enumeration of promotion types supported by the platform.
 *
 * @author FoodAI Team
 */
public enum PromotionType {
  /** Percentage discount on order or items. */
  PERCENTAGE,

  /** Fixed amount off on order or items. */
  FIXED_AMOUNT,

  /** Buy one get one free offer. */
  BOGO,

  /** Bundle deal with multiple items. */
  BUNDLE,

  /** Free delivery offer. */
  FREE_DELIVERY,

  /** Discount activated when order reaches minimum value. */
  MIN_ORDER_DISCOUNT,

  /** First order discount for new users. */
  FIRST_ORDER,

  /** Cashback offer. */
  CASHBACK,

  /** Loyalty points multiplier. */
  LOYALTY_MULTIPLIER,

  /** Referral discount. */
  REFERRAL
}

