package com.spedine.trackit.projection;

import com.spedine.trackit.model.ECurrency;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "ExpenseCurrencyProjection", description = "Total expenses grouped by currency")
public interface ExpenseCurrencyProjection {
    @Schema(description = "Currency code", example = "BRL")
    ECurrency getCurrency();
    @Schema(description = "Total amount in the currency", example = "1234.56")
    BigDecimal getTotal();
}
