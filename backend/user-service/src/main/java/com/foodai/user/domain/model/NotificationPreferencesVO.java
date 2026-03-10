package com.foodai.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Value Object representing a user's notification preferences.
 * Controls how and when the user receives various types of notifications.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreferencesVO {

    // ==================== Channel Preferences ====================

    /**
     * Whether to send push notifications
     */
    @Builder.Default
    private boolean pushEnabled = true;

    /**
     * Whether to send SMS notifications
     */
    @Builder.Default
    private boolean smsEnabled = true;

    /**
     * Whether to send email notifications
     */
    @Builder.Default
    private boolean emailEnabled = true;

    /**
     * Whether to send WhatsApp notifications
     */
    @Builder.Default
    private boolean whatsappEnabled = false;

    // ==================== Notification Types ====================

    /**
     * Order status updates (placed, preparing, out for delivery, delivered)
     */
    @Builder.Default
    private boolean orderUpdates = true;

    /**
     * Promotional offers and discounts
     */
    @Builder.Default
    private boolean promotions = true;

    /**
     * Recommendations based on preferences
     */
    @Builder.Default
    private boolean recommendations = true;

    /**
     * Loyalty points and rewards updates
     */
    @Builder.Default
    private boolean loyaltyUpdates = true;

    /**
     * Diet plan reminders
     */
    @Builder.Default
    private boolean dietReminders = true;

    /**
     * New restaurant/menu notifications
     */
    @Builder.Default
    private boolean newRestaurants = false;

    /**
     * Price drop alerts for favorite items
     */
    @Builder.Default
    private boolean priceAlerts = true;

    /**
     * Account and security notifications
     */
    @Builder.Default
    private boolean accountAlerts = true;

    /**
     * Feedback and survey requests
     */
    @Builder.Default
    private boolean feedbackRequests = true;

    // ==================== Timing Preferences ====================

    /**
     * Whether to enable quiet hours (no notifications during this time)
     */
    @Builder.Default
    private boolean quietHoursEnabled = false;

    /**
     * Quiet hours start time
     */
    private LocalTime quietHoursStart;

    /**
     * Quiet hours end time
     */
    private LocalTime quietHoursEnd;

    /**
     * Maximum notifications per day (0 = unlimited)
     */
    @Builder.Default
    private Integer maxNotificationsPerDay = 0;

    // ==================== Delivery Updates ====================

    /**
     * Live tracking updates during delivery
     */
    @Builder.Default
    private boolean liveTrackingUpdates = true;

    /**
     * Frequency of live tracking updates in seconds
     */
    @Builder.Default
    private Integer trackingUpdateFrequency = 30;

    /**
     * Notify when rider is nearby (within X meters)
     */
    @Builder.Default
    private Integer nearbyAlertDistance = 500;

    /**
     * Checks if notifications are allowed at current time.
     *
     * @param currentTime the current time
     * @return true if notifications are allowed
     */
    public boolean isNotificationAllowed(LocalTime currentTime) {
        if (!quietHoursEnabled || quietHoursStart == null || quietHoursEnd == null) {
            return true;
        }
        // Handle overnight quiet hours
        if (quietHoursStart.isAfter(quietHoursEnd)) {
            return currentTime.isAfter(quietHoursEnd) && currentTime.isBefore(quietHoursStart);
        }
        return currentTime.isBefore(quietHoursStart) || currentTime.isAfter(quietHoursEnd);
    }
}

