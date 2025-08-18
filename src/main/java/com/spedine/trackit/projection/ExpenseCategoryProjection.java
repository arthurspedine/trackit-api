package com.spedine.trackit.projection;

import com.spedine.trackit.model.ECategory;

import java.math.BigDecimal;

public interface ExpenseCategoryProjection {
    ECategory getCategory();
    BigDecimal getTotal();
    int getCount();
}
