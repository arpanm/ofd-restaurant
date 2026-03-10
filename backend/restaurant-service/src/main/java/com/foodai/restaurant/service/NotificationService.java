package com.foodai.restaurant.service;

/**
 * Sends verification email and SMS after onboarding.
 * Implement with real email/SMS provider (e.g. SendGrid, Twilio) in production.
 */
public interface NotificationService {

    /**
     * Send verification email to the given address. Contains link/code for email confirmation.
     */
    void sendVerificationEmail(String email, String restaurantName);

    /**
     * Send verification SMS to the given phone. Contains OTP or link for mobile confirmation.
     */
    void sendVerificationSms(String phone, String restaurantName);
}
