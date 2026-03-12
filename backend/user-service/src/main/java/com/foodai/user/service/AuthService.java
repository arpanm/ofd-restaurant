package com.foodai.user.service;

import com.foodai.user.domain.model.User;
import com.foodai.user.domain.repository.UserRepository;
import com.foodai.user.dto.request.AuthRegisterRequest;
import com.foodai.user.dto.request.CreateUserRequest;
import com.foodai.user.dto.request.SendOtpRequest;
import com.foodai.user.dto.request.VerifyOtpRequest;
import com.foodai.user.dto.response.AuthResponse;
import com.foodai.user.dto.response.UserResponse;
import com.foodai.user.exception.UserNotFoundException;
import com.foodai.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final OtpService otpService;

    public void sendOtp(SendOtpRequest request) {
        String contact = request.getContact().trim();
        String type = request.getType().toUpperCase();
        String otp = otpService.generateAndStore(contact, type);
        if ("EMAIL".equals(type)) {
            log.info("Would send OTP to email {} (integrate email provider for production). OTP: {}", contact, otp);
        } else {
            log.info("Would send OTP to phone {} (integrate SMS provider for production). OTP: {}", contact, otp);
        }
    }

    public AuthResponse verifyOtp(VerifyOtpRequest request) {
        boolean valid = otpService.verifyAndConsume(request.getContact(), request.getOtp());
        if (!valid) {
            throw new IllegalArgumentException("Invalid or expired OTP");
        }
        String contact = request.getContact().trim();
        User user = findUserByContact(contact);
        if (user == null) {
            throw new UserNotFoundException("contact", contact + " (please register first)");
        }
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);
        UserResponse userResponse = userMapper.toResponse(user);
        String accessToken = jwtService.generateAccessToken(user.getId());
        String refreshToken = jwtService.generateRefreshToken(user.getId());
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpirySeconds())
                .user(userResponse)
                .build();
    }

    @Transactional
    public AuthResponse register(AuthRegisterRequest request) {
        CreateUserRequest createRequest = CreateUserRequest.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .referredBy(request.getReferredBy())
                .build();
        UserResponse userResponse = userService.createUser(createRequest);
        String accessToken = jwtService.generateAccessToken(userResponse.getId());
        String refreshToken = jwtService.generateRefreshToken(userResponse.getId());
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpirySeconds())
                .user(userResponse)
                .build();
    }

    public AuthResponse refresh(String refreshToken) {
        String userId = jwtService.validateAndGetUserId(refreshToken);
        UserResponse user = userService.getUserById(userId);
        String newAccess = jwtService.generateAccessToken(userId);
        return AuthResponse.builder()
                .accessToken(newAccess)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpirySeconds())
                .user(user)
                .build();
    }

    public void logout(String refreshToken) {
        // Client discards tokens; optional: blacklist refresh token in Redis
        log.debug("User logged out");
    }

    private User findUserByContact(String contact) {
        if (contact.contains("@")) {
            return userRepository.findByEmailIgnoreCaseAndDeletedFalse(contact).orElse(null);
        }
        return userRepository.findByPhoneAndDeletedFalse(contact).orElse(null);
    }
}
