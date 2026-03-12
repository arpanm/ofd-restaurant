package com.foodai.user.controller;

import com.foodai.user.dto.request.AuthRegisterRequest;
import com.foodai.user.dto.request.SendOtpRequest;
import com.foodai.user.dto.request.VerifyOtpRequest;
import com.foodai.user.dto.response.ApiResponse;
import com.foodai.user.dto.response.AuthResponse;
import com.foodai.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth", description = "Registration, OTP login, token refresh, logout")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/send-otp")
    @Operation(summary = "Send OTP", description = "Send OTP to email or phone for login")
    public ResponseEntity<ApiResponse<Map<String, String>>> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        log.info("POST /api/v1/auth/send-otp for type={}", request.getType());
        authService.sendOtp(request);
        return ResponseEntity.ok(ApiResponse.success(Map.of("message", "OTP sent successfully")));
    }

    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP", description = "Verify OTP and return tokens and user")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        log.info("POST /api/v1/auth/verify-otp");
        AuthResponse response = authService.verifyOtp(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/register")
    @Operation(summary = "Register", description = "Register a new user and return tokens; then redirect to onboarding")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody AuthRegisterRequest request) {
        log.info("POST /api/v1/auth/register for email={}", request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Registered successfully", response));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Exchange refresh token for new access token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@RequestBody Map<String, String> body) {
        String refreshToken = body.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("refreshToken is required"));
        }
        AuthResponse response = authService.refresh(refreshToken);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidate session; client should discard tokens")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestBody(required = false) Map<String, String> body) {
        if (body != null && body.containsKey("refreshToken")) {
            authService.logout(body.get("refreshToken"));
        }
        return ResponseEntity.ok(ApiResponse.success("Logged out", null));
    }
}
