package com.spedine.trackit.projection;

import java.math.BigDecimal;

public interface ExpenseCountAndTotalProjection {
    int getCount();
    BigDecimal getTotalExpense();
}
