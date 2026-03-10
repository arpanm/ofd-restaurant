package com.foodai.restaurant.domain.model;

/**
 * Enum representing budget categories for restaurants.
 * Used for filtering restaurants by price range.
 */
public enum BudgetType {
    BUDGET("Budget - Under ₹300 for two"),
    AFFORDABLE("Affordable - ₹300-600 for two"),
    MID_RANGE("Mid-Range - ₹600-1200 for two"),
    PREMIUM("Premium - ₹1200-2000 for two"),
    LUXURY("Luxury - Above ₹2000 for two");

    private final String description;

    BudgetType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get minimum price for this budget type (per two people).
     */
    public double getMinPrice() {
        return switch (this) {
            case BUDGET -> 0.0;
            case AFFORDABLE -> 300.0;
            case MID_RANGE -> 600.0;
            case PREMIUM -> 1200.0;
            case LUXURY -> 2000.0;
        };
    }

    /**
     * Get maximum price for this budget type (per two people).
     */
    public Double getMaxPrice() {
        return switch (this) {
            case BUDGET -> 300.0;
            case AFFORDABLE -> 600.0;
            case MID_RANGE -> 1200.0;
            case PREMIUM -> 2000.0;
            case LUXURY -> null; // No upper limit
        };
    }
}

