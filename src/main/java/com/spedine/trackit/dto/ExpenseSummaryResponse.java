package com.spedine.trackit.dto;

import com.spedine.trackit.projection.ExpenseCategoryProjection;
import com.spedine.trackit.projection.ExpenseCurrencyProjection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ExpenseSummaryResponse(
        LocalDate startDate,
        LocalDate endDate,
        List<ExpenseCurrencyProjection> totalsByCurrency,
        List<ExpenseCategoryProjection> totalsByCategory,
        PaymentMethodSummary mostUsedPaymentMethod,
        BigDecimal total,
        Integer count
) {
}
