package com.foodai.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for diet reminder settings.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Diet plan reminder settings")
public class DietReminderSettingsDTO {

    @Schema(description = "Enable reminders")
    private Boolean enabled;

    @Schema(description = "Reminder lead time (minutes)", example = "30")
    private Integer reminderLeadTime;

    @Schema(description = "Send push notification")
    private Boolean pushNotification;

    @Schema(description = "Send SMS reminder")
    private Boolean smsReminder;

    @Schema(description = "Send email reminder")
    private Boolean emailReminder;

    @Schema(description = "Send daily summary")
    private Boolean dailySummary;

    @Schema(description = "Daily summary time", example = "08:00")
    private String dailySummaryTime;

    @Schema(description = "Send weekly report")
    private Boolean weeklyReport;
}

