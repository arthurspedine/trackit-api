package com.spedine.trackit.dto;

import com.spedine.trackit.model.ECurrency;
import com.spedine.trackit.model.ECategory;
import com.spedine.trackit.model.EPaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(name = "UpdateExpenseRequest", description = "Payload to update fields of an existing expense. All fields are optional.")
public record UpdateExpenseRequest(
        @Positive
        @Schema(description = "New monetary amount (positive)", example = "150.00")
        BigDecimal amount,
        @Size(min = 3, message = "Description must be at least 3 characters long")
        @Size(max = 255, message = "Description must not exceed 255 characters")
        @Schema(description = "New description", example = "Weekly groceries")
        String description,
        @PastOrPresent
        @Schema(description = "New expense date/time (ISO-8601)", example = "2025-08-22T19:45:00")
        LocalDateTime expenseDate,
        @Schema(description = "New category", example = "FOOD")
        ECategory category,
        @Schema(description = "New currency", example = "USD")
        ECurrency currency,
        @Schema(description = "New payment method", example = "CREDIT_CARD")
        EPaymentMethod paymentMethod
) {
}
