package com.foodai.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "app.otp")
public class OtpProperties {

    private int expirySeconds = 600;
    private int length = 6;
}
