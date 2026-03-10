package com.foodai.restaurant.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

/**
 * Stub implementation that logs verification email/SMS. Replace with real provider in production.
 */
@Service
@Primary
@Slf4j
public class LoggingNotificationService implements NotificationService {

    @Override
    public void sendVerificationEmail(String email, String restaurantName) {
        log.info("Would send verification email to {} for restaurant: {} (integrate email provider for production)", email, restaurantName);
    }

    @Override
    public void sendVerificationSms(String phone, String restaurantName) {
        log.info("Would send verification SMS to {} for restaurant: {} (integrate SMS provider for production)", phone, restaurantName);
    }
}
