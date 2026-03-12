package com.foodai.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to register a new user (then redirect to onboarding)")
public class AuthRegisterRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    @Schema(description = "First name", example = "John")
    private String firstName;

    @Size(max = 50)
    @Schema(description = "Last name", example = "Doe")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Email address", example = "user@example.com")
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{9,14}$", message = "Invalid phone format")
    @Schema(description = "Phone with country code", example = "+919876543210")
    private String phone;

    @Schema(description = "Referrer user ID or referral code (optional)")
    private String referredBy;
}
