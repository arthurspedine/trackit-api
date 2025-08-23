package com.spedine.trackit.dto;

import com.spedine.trackit.projection.ExpenseCategoryProjection;
import com.spedine.trackit.projection.ExpenseCurrencyProjection;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(name = "ExpenseSummaryResponse", description = "Aggregated summary for a given period")
public record ExpenseSummaryResponse(
        @Schema(description = "Start date of the summary period", example = "2025-08-01")
        LocalDate startDate,
        @Schema(description = "End date of the summary period", example = "2025-08-31")
        LocalDate endDate,
        @Schema(description = "Totals grouped by currency")
        List<ExpenseCurrencyProjection> totalsByCurrency,
        @Schema(description = "Totals grouped by category (with count)")
        List<ExpenseCategoryProjection> totalsByCategory,
        @Schema(description = "Most frequently used payment method in the period")
        PaymentMethodSummary mostUsedPaymentMethod,
        @Schema(description = "Total amount across all expenses in the period", example = "2450.75")
        BigDecimal total,
        @Schema(description = "Number of expenses in the period", example = "42")
        Integer count
) {
}
