package com.spedine.trackit.dto;

import com.spedine.trackit.model.ECurrency;
import com.spedine.trackit.model.ECategory;
import com.spedine.trackit.model.EPaymentMethod;
import com.spedine.trackit.model.Expense;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(name = "ExpenseResponse", description = "Expense data returned by the API")
public record ExpenseResponse(
        @Schema(description = "Expense unique identifier", example = "5a1e3a3e-4f8c-4c1b-9c8e-2a7a9a1b2c3d")
        UUID id,
        @Schema(description = "Monetary amount", example = "129.90")
        BigDecimal amount,
        @Schema(description = "Short description", example = "Groceries at market")
        String description,
        @Schema(description = "When the expense occurred (ISO-8601)", example = "2025-08-22T18:30:00")
        LocalDateTime expenseDate,
        @Schema(description = "When the expense was created (ISO-8601)", example = "2025-08-22T18:35:10")
        LocalDateTime createdAt,
        @Schema(description = "Category of the expense", example = "FOOD")
        ECategory category,
        @Schema(description = "Currency code", example = "BRL")
        ECurrency currency,
        @Schema(description = "Payment method used", example = "PIX")
        EPaymentMethod paymentMethod
) {
    public static ExpenseResponse fromDomain(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getAmount(),
                expense.getDescription(),
                expense.getExpenseDate(),
                expense.getCreatedAt(),
                expense.getCategory(),
                expense.getCurrency(),
                expense.getPaymentMethod()
        );
    }
}
