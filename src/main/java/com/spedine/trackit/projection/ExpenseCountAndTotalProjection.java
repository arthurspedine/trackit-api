package com.spedine.trackit.projection;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "ExpenseCountAndTotalProjection", description = "Aggregate of count and total expense amount")
public interface ExpenseCountAndTotalProjection {
    @Schema(description = "Number of expenses", example = "42")
    int getCount();
    @Schema(description = "Total amount across expenses", example = "2450.75")
    BigDecimal getTotalExpense();
}
