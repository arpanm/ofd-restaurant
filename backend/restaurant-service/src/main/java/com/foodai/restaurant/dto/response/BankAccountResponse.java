package com.foodai.restaurant.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for bank account details (masked for security in responses).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountResponse {
    private String bankName;
    private String accountNumberLast4;
    private String ifscCode;
    private String accountHolderName;
}
