package com.spedine.trackit.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EPaymentMethod", description = "Supported payment methods (e.g., CASH, CREDIT_CARD, PIX)")
public enum EPaymentMethod {
    CASH,
    CREDIT_CARD,
    DEBIT_CARD,
    PIX,
    BANK_TRANSFER,
    OTHER
}
