package com.foodai.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * Response DTO for Notification Preferences.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Notification preferences response")
public class NotificationPreferencesResponse {

    @Schema(description = "Push notifications enabled")
    private boolean pushEnabled;

    @Schema(description = "SMS notifications enabled")
    private boolean smsEnabled;

    @Schema(description = "Email notifications enabled")
    private boolean emailEnabled;

    @Schema(description = "WhatsApp notifications enabled")
    private boolean whatsappEnabled;

    @Schema(description = "Order updates enabled")
    private boolean orderUpdates;

    @Schema(description = "Promotions enabled")
    private boolean promotions;

    @Schema(description = "Recommendations enabled")
    private boolean recommendations;

    @Schema(description = "Loyalty updates enabled")
    private boolean loyaltyUpdates;

    @Schema(description = "Diet reminders enabled")
    private boolean dietReminders;

    @Schema(description = "Quiet hours enabled")
    private boolean quietHoursEnabled;

    @Schema(description = "Quiet hours start")
    private LocalTime quietHoursStart;

    @Schema(description = "Quiet hours end")
    private LocalTime quietHoursEnd;

    @Schema(description = "Live tracking updates enabled")
    private boolean liveTrackingUpdates;

    @Schema(description = "Nearby alert distance (meters)")
    private Integer nearbyAlertDistance;
}

