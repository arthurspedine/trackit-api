package com.spedine.trackit.repository;

import com.spedine.trackit.dto.ExpenseFilter;
import com.spedine.trackit.dto.PaymentMethodSummary;
import com.spedine.trackit.model.Expense;
import com.spedine.trackit.model.User;
import com.spedine.trackit.projection.ExpenseCategoryProjection;
import com.spedine.trackit.projection.ExpenseCountAndTotalProjection;
import com.spedine.trackit.projection.ExpenseCurrencyProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ExpenseRepository {
    Expense save(Expense expense);

    Expense findByIdAndUser_Id(UUID id, UUID userId);

    Page<Expense> findAll(User user, Pageable pageable, ExpenseFilter filter);

    void delete(Expense expense);

    ExpenseCountAndTotalProjection countAndSumTotalAmount(UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    List<ExpenseCategoryProjection> groupByExpenseCategoryAndSumAmount(UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    PaymentMethodSummary getMostUsedPaymentMethod(UUID userId, LocalDateTime startDate, LocalDateTime endDate);

    List<ExpenseCurrencyProjection> groupByCurrencyAndSumAmount(UUID userId, LocalDateTime startDate, LocalDateTime endDate);
}
