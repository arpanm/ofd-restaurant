package com.foodai.restaurant.dto.request;

import com.foodai.restaurant.domain.model.OwnerRole;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for restaurant owner information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OwnerDTO {
    
    private String ownerId;
    
    @NotBlank(message = "Owner name is required")
    @Size(min = 2, max = 100, message = "Owner name must be between 2 and 100 characters")
    private String ownerName;
    
    @NotBlank(message = "Owner email is required")
    @Email(message = "Invalid email format")
    private String ownerEmail;
    
    @NotBlank(message = "Owner phone is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone must be a valid 10-digit Indian mobile number")
    private String ownerPhone;
    
    @NotNull(message = "Ownership percentage is required")
    @Min(value = 0, message = "Ownership percentage must be at least 0")
    @Max(value = 100, message = "Ownership percentage cannot exceed 100")
    private Double ownershipPercentage;
    
    @NotNull(message = "Owner role is required")
    private OwnerRole role;
    
    @NotNull(message = "Primary contact flag is required")
    private Boolean isPrimaryContact;
}


