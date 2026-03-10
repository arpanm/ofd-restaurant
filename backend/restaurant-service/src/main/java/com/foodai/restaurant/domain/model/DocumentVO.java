package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Value Object representing a document uploaded for verification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVO {
    /**
     * Type of document
     */
    private DocumentType type;
    
    /**
     * URL/path to the uploaded document
     */
    private String url;
    
    /**
     * Verification status of the document
     */
    private DocumentStatus verificationStatus;
    
    /**
     * Timestamp when document was uploaded
     */
    private Instant uploadedAt;
    
    /**
     * Remarks from verification team (if rejected)
     */
    private String remarks;
}


