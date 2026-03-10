package com.foodai.user.domain.model;

/**
 * Enumeration of supported payment method types.
 *
 * @author FoodAI Team
 */
public enum PaymentMethodType {
    /**
     * Credit card payment
     */
    CREDIT_CARD,

    /**
     * Debit card payment
     */
    DEBIT_CARD,

    /**
     * UPI payment (Google Pay, PhonePe, Paytm, etc.)
     */
    UPI,

    /**
     * Digital wallet (Paytm Wallet, Amazon Pay, etc.)
     */
    WALLET,

    /**
     * Net banking
     */
    NET_BANKING,

    /**
     * Cash on delivery
     */
    COD,

    /**
     * Platform wallet/credits
     */
    PLATFORM_CREDITS
}

