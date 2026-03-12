package com.foodai.user.service;

import com.foodai.user.config.OtpProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private static final String OTP_PREFIX = "otp:";

    private final OtpProperties props;
    private final StringRedisTemplate redisTemplate;

    public String generateAndStore(String contact, String type) {
        String digits = "0123456789";
        StringBuilder otp = new StringBuilder(props.getLength());
        for (int i = 0; i < props.getLength(); i++) {
            otp.append(digits.charAt(ThreadLocalRandom.current().nextInt(digits.length())));
        }
        String code = otp.toString();
        String key = OTP_PREFIX + type + ":" + contact.trim().toLowerCase();
        redisTemplate.opsForValue().set(key, code, props.getExpirySeconds(), TimeUnit.SECONDS);
        log.debug("OTP stored for {} ({}), expires in {}s", contact, type, props.getExpirySeconds());
        return code;
    }

    public boolean verifyAndConsume(String contact, String otp) {
        String keyEmail = OTP_PREFIX + "EMAIL:" + contact.trim().toLowerCase();
        String keyPhone = OTP_PREFIX + "PHONE:" + contact.trim();
        String storedEmail = redisTemplate.opsForValue().get(keyEmail);
        String storedPhone = redisTemplate.opsForValue().get(keyPhone);
        String stored = storedEmail != null ? storedEmail : storedPhone;
        if (stored == null || !stored.equals(otp)) {
            return false;
        }
        if (storedEmail != null) redisTemplate.delete(keyEmail);
        else redisTemplate.delete(keyPhone);
        return true;
    }
}
