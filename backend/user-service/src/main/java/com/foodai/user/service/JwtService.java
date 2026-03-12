package com.foodai.user.service;

import com.foodai.user.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private static final String SUBJECT = "sub";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_REFRESH = "refresh";

    private final JwtProperties props;

    private SecretKey key() {
        byte[] bytes = props.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String generateAccessToken(String userId) {
        long now = System.currentTimeMillis();
        long expiry = now + props.getAccessTokenExpirySeconds() * 1000L;
        return Jwts.builder()
                .subject(userId)
                .claim("type", TYPE_ACCESS)
                .issuedAt(new Date(now))
                .expiration(new Date(expiry))
                .signWith(key())
                .compact();
    }

    public String generateRefreshToken(String userId) {
        long now = System.currentTimeMillis();
        long expiry = now + props.getRefreshTokenExpirySeconds() * 1000L;
        return Jwts.builder()
                .subject(userId)
                .claim("type", TYPE_REFRESH)
                .issuedAt(new Date(now))
                .expiration(new Date(expiry))
                .signWith(key())
                .compact();
    }

    public String validateAndGetUserId(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            log.debug("JWT expired: {}", e.getMessage());
            throw new IllegalArgumentException("Token expired");
        } catch (SignatureException | MalformedJwtException e) {
            log.debug("Invalid JWT: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid token");
        }
    }

    public long getAccessTokenExpirySeconds() {
        return props.getAccessTokenExpirySeconds();
    }
}
