package com.foodai.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to send OTP for login")
public class SendOtpRequest {

    @NotBlank(message = "Contact (email or phone) is required")
    @Schema(description = "Email address or phone number", example = "user@example.com or +919876543210")
    private String contact;

    @NotBlank(message = "Type is required")
    @Pattern(regexp = "EMAIL|PHONE", message = "Type must be EMAIL or PHONE")
    @Schema(description = "Contact type", allowableValues = { "EMAIL", "PHONE" })
    private String type;
}
