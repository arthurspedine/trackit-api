package com.spedine.trackit.dto;

import com.spedine.trackit.model.ECurrency;
import com.spedine.trackit.model.ECategory;
import com.spedine.trackit.model.EPaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(name = "ExpenseFilter", description = "Optional filters for listing expenses. If provided, filters are combined (AND).")
public record ExpenseFilter(
        @Schema(description = "Year-month string to filter by month (YYYY-MM)", example = "2025-08")
        String month,
        @Schema(description = "Filter start date/time (ISO-8601)", example = "2025-08-01T00:00:00")
        LocalDateTime startDate,
        @Schema(description = "Filter end date/time (ISO-8601)", example = "2025-08-31T23:59:59")
        LocalDateTime endDate,
        @Schema(description = "Filter by currency", example = "BRL")
        ECurrency currency,
        @Schema(description = "Filter by category", example = "FOOD")
        ECategory category,
        @Schema(description = "Filter by payment method", example = "PIX")
        EPaymentMethod paymentMethod
) {
}
