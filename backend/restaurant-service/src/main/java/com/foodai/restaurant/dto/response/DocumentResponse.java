package com.foodai.restaurant.dto.response;

import com.foodai.restaurant.domain.model.DocumentStatus;
import com.foodai.restaurant.domain.model.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for Document information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private DocumentType type;
    private String url;
    private DocumentStatus verificationStatus;
    private Instant uploadedAt;
    private String remarks;
}


