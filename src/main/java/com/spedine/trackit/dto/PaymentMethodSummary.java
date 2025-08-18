package com.spedine.trackit.dto;

import com.spedine.trackit.model.EPaymentMethod;

public record PaymentMethodSummary(
        EPaymentMethod method,
        Long usageCount
) {
}
