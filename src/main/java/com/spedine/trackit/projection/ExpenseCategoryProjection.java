package com.spedine.trackit.projection;

import com.spedine.trackit.model.ECategory;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "ExpenseCategoryProjection", description = "Totals and counts grouped by category")
public interface ExpenseCategoryProjection {
    @Schema(description = "Expense category", example = "FOOD")
    ECategory getCategory();
    @Schema(description = "Total amount in this category", example = "987.65")
    BigDecimal getTotal();
    @Schema(description = "Number of expenses in this category", example = "12")
    int getCount();
}
