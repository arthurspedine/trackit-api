package com.spedine.trackit.projection;

import com.spedine.trackit.model.ECurrency;

import java.math.BigDecimal;

public interface ExpenseCurrencyProjection {
    ECurrency getCurrency();
    BigDecimal getTotal();
}
