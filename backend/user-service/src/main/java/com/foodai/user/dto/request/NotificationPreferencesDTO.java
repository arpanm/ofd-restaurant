package com.foodai.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

/**
 * DTO for notification preferences.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Notification preferences")
public class NotificationPreferencesDTO {

    @Schema(description = "Enable push notifications")
    private Boolean pushEnabled;

    @Schema(description = "Enable SMS notifications")
    private Boolean smsEnabled;

    @Schema(description = "Enable email notifications")
    private Boolean emailEnabled;

    @Schema(description = "Enable WhatsApp notifications")
    private Boolean whatsappEnabled;

    @Schema(description = "Receive order updates")
    private Boolean orderUpdates;

    @Schema(description = "Receive promotional offers")
    private Boolean promotions;

    @Schema(description = "Receive recommendations")
    private Boolean recommendations;

    @Schema(description = "Receive loyalty updates")
    private Boolean loyaltyUpdates;

    @Schema(description = "Receive diet reminders")
    private Boolean dietReminders;

    @Schema(description = "New restaurant notifications")
    private Boolean newRestaurants;

    @Schema(description = "Price drop alerts")
    private Boolean priceAlerts;

    @Schema(description = "Account alerts")
    private Boolean accountAlerts;

    @Schema(description = "Enable quiet hours")
    private Boolean quietHoursEnabled;

    @Schema(description = "Quiet hours start time", example = "22:00")
    private LocalTime quietHoursStart;

    @Schema(description = "Quiet hours end time", example = "07:00")
    private LocalTime quietHoursEnd;

    @Schema(description = "Live tracking updates during delivery")
    private Boolean liveTrackingUpdates;

    @Schema(description = "Nearby alert distance in meters", example = "500")
    private Integer nearbyAlertDistance;
}

