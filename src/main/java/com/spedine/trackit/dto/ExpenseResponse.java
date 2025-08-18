package com.spedine.trackit.dto;

import com.spedine.trackit.model.ECurrency;
import com.spedine.trackit.model.ECategory;
import com.spedine.trackit.model.EPaymentMethod;
import com.spedine.trackit.model.Expense;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ExpenseResponse(
        UUID id,
        BigDecimal amount,
        String description,
        LocalDateTime expenseDate,
        LocalDateTime createdAt,
        ECategory category,
        ECurrency currency,
        EPaymentMethod paymentMethod
) {
    public static ExpenseResponse fromEntity(Expense expense) {
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

