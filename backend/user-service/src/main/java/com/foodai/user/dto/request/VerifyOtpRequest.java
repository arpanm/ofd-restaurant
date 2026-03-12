package com.foodai.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Request to verify OTP and complete login")
public class VerifyOtpRequest {

    @NotBlank(message = "Contact (email or phone) is required")
    @Schema(description = "Email or phone used when sending OTP")
    private String contact;

    @NotBlank(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
    @Pattern(regexp = "\\d{6}", message = "OTP must be 6 digits")
    @Schema(description = "6-digit OTP", example = "123456")
    private String otp;
}
