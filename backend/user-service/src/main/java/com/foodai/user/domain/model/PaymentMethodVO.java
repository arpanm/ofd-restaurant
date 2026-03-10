package com.foodai.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Value Object representing a saved payment method.
 *
 * <p>Covers features from:
 * <ul>
 *   <li>CHECKOUT_FLOW.md - Saved payment methods for quick checkout</li>
 * </ul>
 *
 * @author FoodAI Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodVO {

    /**
     * Unique identifier for this payment method
     */
    private String id;

    /**
     * Type of payment method
     */
    private PaymentMethodType type;

    /**
     * Display name (e.g., "Personal Card", "Office UPI")
     */
    private String displayName;

    /**
     * Whether this is the default payment method
     */
    @Builder.Default
    private boolean isDefault = false;

    // ==================== Card Details (masked) ====================

    /**
     * Last 4 digits of card number
     */
    private String cardLast4;

    /**
     * Card brand (VISA, MasterCard, etc.)
     */
    private String cardBrand;

    /**
     * Card expiry month (1-12)
     */
    private Integer cardExpiryMonth;

    /**
     * Card expiry year (YYYY)
     */
    private Integer cardExpiryYear;

    /**
     * Cardholder name
     */
    private String cardholderName;

    // ==================== UPI Details ====================

    /**
     * UPI ID (e.g., "user@upi")
     */
    private String upiId;

    /**
     * UPI app name (e.g., "Google Pay", "PhonePe")
     */
    private String upiApp;

    // ==================== Wallet Details ====================

    /**
     * Wallet provider name (e.g., "Paytm", "Amazon Pay")
     */
    private String walletProvider;

    /**
     * Linked phone number for wallet
     */
    private String walletPhone;

    // ==================== Bank Details ====================

    /**
     * Bank name for net banking
     */
    private String bankName;

    /**
     * Bank account last 4 digits
     */
    private String accountLast4;

    // ==================== Metadata ====================

    /**
     * Payment gateway token for this method
     */
    private String gatewayToken;

    /**
     * Whether this method is verified
     */
    @Builder.Default
    private boolean verified = false;

    /**
     * Whether this method is active
     */
    @Builder.Default
    private boolean active = true;

    /**
     * Creation timestamp
     */
    private Instant createdAt;

    /**
     * Last used timestamp
     */
    private Instant lastUsedAt;

    /**
     * Gets masked display string for this payment method.
     *
     * @return masked display string
     */
    public String getMaskedDisplay() {
        return switch (type) {
            case CREDIT_CARD, DEBIT_CARD -> 
                String.format("%s •••• %s", cardBrand != null ? cardBrand : "Card", cardLast4);
            case UPI -> String.format("UPI: %s", upiId);
            case WALLET -> String.format("%s Wallet", walletProvider);
            case NET_BANKING -> String.format("%s •••• %s", bankName, accountLast4);
            case COD -> "Cash on Delivery";
            default -> displayName;
        };
    }
}

