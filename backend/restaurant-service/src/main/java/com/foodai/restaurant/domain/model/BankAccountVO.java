package com.foodai.restaurant.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Value object for restaurant payout bank account details.
 * Used for settlement and payouts.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountVO {

    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String accountHolderName;
    /**
     * URL to uploaded cancelled cheque or bank statement document.
     */
    private String cancelledChequeDocumentUrl;
}
