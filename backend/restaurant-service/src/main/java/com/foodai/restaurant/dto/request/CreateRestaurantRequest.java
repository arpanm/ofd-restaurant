package com.foodai.restaurant.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for creating a new restaurant brand.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRestaurantRequest {
    
    @NotBlank(message = "Restaurant name is required")
    @Size(min = 2, max = 100, message = "Restaurant name must be between 2 and 100 characters")
    private String name;
    
    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    private String logo;
    
    private String coverImage;
    
    @NotEmpty(message = "At least one owner is required")
    @Valid
    private List<OwnerDTO> owners;
    
    @NotEmpty(message = "At least one contact is required")
    @Valid
    private List<ContactDTO> contacts;
    
    @NotEmpty(message = "At least one cuisine type is required")
    private List<String> cuisineTypes;
    
    @NotEmpty(message = "Required documents must be provided")
    @Valid
    private List<DocumentDTO> documents;
    
    @NotNull(message = "Contract details are required")
    @Valid
    private ContractDTO contract;
    
    @NotBlank(message = "Created by user ID is required")
    private String createdBy;
}


