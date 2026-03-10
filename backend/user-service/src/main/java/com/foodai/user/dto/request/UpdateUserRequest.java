package com.foodai.user.dto.request;

import com.foodai.user.domain.model.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for updating a user.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update user profile")
public class UpdateUserRequest {

    @Email(message = "Invalid email format")
    @Schema(description = "User's email address", example = "user@example.com")
    private String email;

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Schema(description = "User's first name", example = "John")
    private String firstName;

    @Size(max = 50, message = "Last name must be at most 50 characters")
    @Schema(description = "User's last name", example = "Doe")
    private String lastName;

    @Size(max = 50, message = "Display name must be at most 50 characters")
    @Schema(description = "Display name/nickname", example = "Johnny")
    private String displayName;

    @Schema(description = "Profile picture URL")
    private String avatarUrl;

    @Schema(description = "Date of birth", example = "1990-01-15")
    private LocalDate dateOfBirth;

    @Schema(description = "Gender")
    private Gender gender;

    @Schema(description = "Language preference", example = "en")
    private String languagePreference;

    @Schema(description = "Timezone", example = "Asia/Kolkata")
    private String timezone;

    @Schema(description = "Dietary preferences")
    private DietaryPreferencesDTO dietaryPreferences;

    @Schema(description = "Notification preferences")
    private NotificationPreferencesDTO notificationPreferences;

    @Schema(description = "Personalization settings")
    private PersonalizationSettingsDTO personalizationSettings;

    @Schema(description = "Cuisine preferences", example = "[\"Indian\", \"Chinese\", \"Italian\"]")
    private List<String> cuisinePreferences;

    @Schema(description = "User ID who is updating")
    private String updatedBy;
}

