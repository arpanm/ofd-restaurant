package com.foodai.restaurant.dto.request;

import com.foodai.restaurant.domain.model.ContactType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for contact information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDTO {
    
    private String contactId;
    
    @NotNull(message = "Contact type is required")
    private ContactType contactType;
    
    @NotBlank(message = "Contact name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone must be a valid 10-digit Indian mobile number")
    private String phone;
    
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Alternate phone must be a valid 10-digit Indian mobile number")
    private String alternatePhone;
    
    @Email(message = "Invalid email format")
    private String email;
    
    private String designation;
    
    private Boolean isActive;
}


