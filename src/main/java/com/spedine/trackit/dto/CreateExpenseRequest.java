package com.spedine.trackit.dto;

import com.spedine.trackit.model.ECurrency;
import com.spedine.trackit.model.EExpenseCategory;
import com.spedine.trackit.model.EPaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreateExpenseRequest(
        @NotNull
        @Positive
        BigDecimal amount,
        @NotNull
        @Size(min = 3, message = "Description must be at least 3 characters long")
        @Size(max = 255, message = "Description must not exceed 255 characters")
        String description,
        @NotNull
        @PastOrPresent
        LocalDateTime expenseDate,
        @NotNull
        EExpenseCategory category,
        @NotNull
        ECurrency currency,
        @NotNull
        EPaymentMethod paymentMethod
) {
}
