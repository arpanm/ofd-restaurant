package com.foodai.user.dto.request;

import com.foodai.user.domain.model.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Request DTO for creating a new user.
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create a new user")
public class CreateUserRequest {

    @Email(message = "Invalid email format")
    @Schema(description = "User's email address", example = "user@example.com")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{9,14}$", message = "Invalid phone number format")
    @Schema(description = "User's phone number with country code", example = "+919876543210")
    private String phone;

    @NotBlank(message = "First name is required")
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

    @Schema(description = "Referral code of the referrer")
    private String referredBy;

    @Schema(description = "Language preference", example = "en")
    private String languagePreference;

    @Schema(description = "Timezone", example = "Asia/Kolkata")
    private String timezone;

    @Schema(description = "Initial address to add")
    private AddressDTO address;

    @Schema(description = "Dietary preferences")
    private DietaryPreferencesDTO dietaryPreferences;

    @Schema(description = "Cuisine preferences", example = "[\"Indian\", \"Chinese\", \"Italian\"]")
    private List<String> cuisinePreferences;

    @Schema(description = "User who created this account (for admin-created accounts)")
    private String createdBy;
}

