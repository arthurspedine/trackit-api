package com.spedine.trackit.dto;

import com.spedine.trackit.model.EPaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "PaymentMethodSummary", description = "Most used payment method summary")
public record PaymentMethodSummary(
        @Schema(description = "Payment method", example = "PIX")
        EPaymentMethod method,
        @Schema(description = "How many times it was used", example = "17")
        Long usageCount
) {
}
