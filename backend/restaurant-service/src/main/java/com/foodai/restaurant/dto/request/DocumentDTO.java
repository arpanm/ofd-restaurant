package com.foodai.restaurant.dto.request;

import com.foodai.restaurant.domain.model.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for document information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
    
    @NotNull(message = "Document type is required")
    private DocumentType type;
    
    @NotBlank(message = "Document URL is required")
    private String url;
    
    private String remarks;
}


