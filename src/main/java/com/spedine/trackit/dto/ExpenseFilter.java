package com.spedine.trackit.dto;

import com.spedine.trackit.model.ECurrency;
import com.spedine.trackit.model.ECategory;
import com.spedine.trackit.model.EPaymentMethod;

import java.time.LocalDateTime;

public record ExpenseFilter(
        String month,
        LocalDateTime startDate,
        LocalDateTime endDate,
        ECurrency currency,
        ECategory category,
        EPaymentMethod paymentMethod
) {
}
