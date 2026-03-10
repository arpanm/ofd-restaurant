package com.foodai.menu.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Value Object representing dynamic pricing configuration for a menu item.
 *
 * <p>Allows prices to be adjusted based on demand, time, inventory, etc.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DynamicPricingConfigVO {

  /**
   * Whether dynamic pricing is enabled for this item.
   */
  private boolean enabled;

  /**
   * Peak hour multiplier (e.g., 1.2 for 20% increase).
   */
  private BigDecimal peakHourMultiplier;

  /**
   * Off-peak hour multiplier (e.g., 0.9 for 10% discount).
   */
  private BigDecimal offPeakMultiplier;

  /**
   * Minimum price floor (never go below this).
   */
  private BigDecimal priceFloor;

  /**
   * Maximum price ceiling (never go above this).
   */
  private BigDecimal priceCeiling;

  /**
   * Whether to adjust price based on demand.
   */
  private boolean demandBasedPricing;

  /**
   * Whether to adjust price based on inventory levels.
   */
  private boolean inventoryBasedPricing;

  /**
   * Time slots for peak pricing.
   */
  private List<PriceTimeSlotVO> priceTimeSlots;

  /**
   * Validates the dynamic pricing configuration.
   *
   * @throws IllegalStateException if invalid
   */
  public void validate() {
    if (!enabled) {
      return;
    }

    if (peakHourMultiplier != null && peakHourMultiplier.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalStateException("Peak hour multiplier must be positive");
    }

    if (offPeakMultiplier != null && offPeakMultiplier.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalStateException("Off-peak multiplier must be positive");
    }

    if (priceFloor != null && priceFloor.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalStateException("Price floor must be positive");
    }

    if (priceCeiling != null && priceCeiling.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalStateException("Price ceiling must be positive");
    }

    if (priceFloor != null && priceCeiling != null && priceFloor.compareTo(priceCeiling) >= 0) {
      throw new IllegalStateException("Price floor cannot be greater than or equal to price ceiling");
    }
  }

  /**
   * Calculates the dynamic price based on current conditions.
   *
   * @param basePrice the base price
   * @param isPeakHour whether it's currently peak hour
   * @param demandLevel current demand level (0.0 to 1.0)
   * @param inventoryLevel current inventory level (0.0 to 1.0, where 1.0 is full stock)
   * @return the dynamically calculated price
   */
  public BigDecimal calculatePrice(BigDecimal basePrice, boolean isPeakHour,
                                   double demandLevel, double inventoryLevel) {
    if (!enabled) {
      return basePrice;
    }

    BigDecimal price = basePrice;

    // Apply peak/off-peak multiplier
    if (isPeakHour && peakHourMultiplier != null) {
      price = price.multiply(peakHourMultiplier);
    } else if (!isPeakHour && offPeakMultiplier != null) {
      price = price.multiply(offPeakMultiplier);
    }

    // Apply demand-based pricing
    if (demandBasedPricing && demandLevel > 0.7) {
      BigDecimal demandMultiplier = BigDecimal.valueOf(1.0 + (demandLevel - 0.7) * 0.5);
      price = price.multiply(demandMultiplier);
    }

    // Apply inventory-based pricing (low inventory = higher price)
    if (inventoryBasedPricing && inventoryLevel < 0.3) {
      BigDecimal inventoryMultiplier = BigDecimal.valueOf(1.0 + (0.3 - inventoryLevel) * 0.3);
      price = price.multiply(inventoryMultiplier);
    }

    // Apply floor and ceiling
    if (priceFloor != null && price.compareTo(priceFloor) < 0) {
      price = priceFloor;
    }

    if (priceCeiling != null && price.compareTo(priceCeiling) > 0) {
      price = priceCeiling;
    }

    return price.setScale(2, BigDecimal.ROUND_HALF_UP);
  }
}

