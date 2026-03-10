package com.foodai.restaurant.dto.response;

import com.foodai.restaurant.domain.model.ContactType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Response DTO for Contact information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponse {
    private String contactId;
    private ContactType contactType;
    private String name;
    private String phone;
    private String alternatePhone;
    private String email;
    private String designation;
    private Boolean isActive;
    private Instant addedAt;
    private String addedBy;
}


