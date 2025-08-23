package com.spedine.trackit.dto;

import com.spedine.trackit.model.ECurrency;
import com.spedine.trackit.model.ECategory;
import com.spedine.trackit.model.EPaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(name = "CreateExpenseRequest", description = "Payload to create a new expense")
public record CreateExpenseRequest(
        @NotNull
        @Positive
        @Schema(description = "Monetary amount (positive)", example = "129.90")
        BigDecimal amount,
        @NotNull
        @Size(min = 3, message = "Description must be at least 3 characters long")
        @Size(max = 255, message = "Description must not exceed 255 characters")
        @Schema(description = "Short description of the expense", example = "Groceries at market")
        String description,
        @NotNull
        @PastOrPresent
        @Schema(description = "When the expense occurred (ISO-8601)", example = "2025-08-22T18:30:00")
        LocalDateTime expenseDate,
        @NotNull
        @Schema(description = "Category of the expense", example = "FOOD")
        ECategory category,
        @NotNull
        @Schema(description = "Currency code", example = "BRL")
        ECurrency currency,
        @NotNull
        @Schema(description = "Payment method used", example = "PIX")
        EPaymentMethod paymentMethod
) {
}
