package com.foodai.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

    private String secret = "change-me-in-production-at-least-256-bits-long-for-hs256";
    private long accessTokenExpirySeconds = 3600L;
    private long refreshTokenExpirySeconds = 604800L;
}
