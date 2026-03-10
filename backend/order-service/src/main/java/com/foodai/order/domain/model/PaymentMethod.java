package com.foodai.order.domain.model;

/**
 * Enumeration representing supported payment methods.
 *
 * @author FoodAI Team
 */
public enum PaymentMethod {
    /**
     * UPI payment (Google Pay, PhonePe, Paytm, etc.).
     */
    UPI,

    /**
     * Credit or Debit Card.
     */
    CARD,

    /**
     * Net Banking.
     */
    NET_BANKING,

    /**
     * Digital Wallets (Paytm Wallet, Mobikwik, Amazon Pay, etc.).
     */
    WALLET,

    /**
     * Cash on Delivery.
     */
    COD,

    /**
     * Buy Now Pay Later.
     */
    BNPL,

    /**
     * EMI payment.
     */
    EMI
}

