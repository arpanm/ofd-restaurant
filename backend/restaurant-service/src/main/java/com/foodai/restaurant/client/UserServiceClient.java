package com.foodai.restaurant.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

/**
 * Client to call user-service (e.g. create owner user after onboarding).
 * Failures are logged but do not fail the onboarding flow.
 */
@Component
@Slf4j
public class UserServiceClient {

    private final WebClient webClient;

    public UserServiceClient(
        @Value("${services.user-service.url:http://localhost:8083}") String baseUrl
    ) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .defaultHeader("Content-Type", "application/json")
            .build();
    }

    /**
     * Create owner user so they can log in. Uses user-service POST /api/v1/users.
     * Phone format: ensure +91 prefix for Indian numbers if required by user-service.
     */
    public void createOwnerUser(String email, String phone, String ownerName) {
        if (email == null || email.isBlank() || phone == null || phone.isBlank()) {
            log.warn("Cannot create owner user: email or phone missing");
            return;
        }
        String firstName = ownerName != null && !ownerName.isBlank()
            ? (ownerName.contains(" ") ? ownerName.substring(0, ownerName.indexOf(" ")) : ownerName)
            : "Owner";
        String lastName = ownerName != null && ownerName.contains(" ")
            ? ownerName.substring(ownerName.indexOf(" ") + 1)
            : "";
        String phoneFormatted = phone.startsWith("+") ? phone : "+91" + phone;
        Map<String, Object> body = Map.of(
            "email", email,
            "phone", phoneFormatted,
            "firstName", firstName,
            "lastName", lastName != null ? lastName : ""
        );
        try {
            webClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
            log.info("Created owner user for email: {}", email);
        } catch (WebClientResponseException e) {
            log.warn("User-service create user failed ({}): {} - owner can still be added later", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.warn("User-service create user error: {}", e.getMessage());
        }
    }
}
