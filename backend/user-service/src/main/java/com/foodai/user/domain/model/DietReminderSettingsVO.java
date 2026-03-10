package com.foodai.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value Object representing diet plan reminder settings.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DietReminderSettingsVO {

    /**
     * Whether reminders are enabled
     */
    @Builder.Default
    private boolean enabled = true;

    /**
     * Minutes before meal time to send reminder
     */
    @Builder.Default
    private Integer reminderLeadTime = 30;

    /**
     * Whether to send push notification
     */
    @Builder.Default
    private boolean pushNotification = true;

    /**
     * Whether to send SMS reminder
     */
    @Builder.Default
    private boolean smsReminder = false;

    /**
     * Whether to send email reminder
     */
    @Builder.Default
    private boolean emailReminder = false;

    /**
     * Whether to send daily summary in morning
     */
    @Builder.Default
    private boolean dailySummary = true;

    /**
     * Time for daily summary (if enabled)
     */
    private String dailySummaryTime;

    /**
     * Whether to send weekly progress report
     */
    @Builder.Default
    private boolean weeklyReport = true;
}

