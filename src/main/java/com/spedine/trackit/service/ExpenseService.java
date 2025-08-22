package com.spedine.trackit.service;

import com.spedine.trackit.dto.*;
import com.spedine.trackit.model.Expense;
import com.spedine.trackit.model.User;
import com.spedine.trackit.projection.ExpenseCountAndTotalProjection;
import com.spedine.trackit.repository.ExpenseRepository;
import com.spedine.trackit.specification.ExpenseSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public void save(CreateExpenseRequest body, User user) {
        Expense expense = new Expense();
        expense.setAmount(body.amount());
        expense.setDescription(body.description());
        expense.setExpenseDate(body.expenseDate());
        expense.setCategory(body.category());
        expense.setCurrency(body.currency());
        expense.setPaymentMethod(body.paymentMethod());
        expense.setUser(user);
        expenseRepository.save(expense);
    }

    public PageResponse<ExpenseResponse> findAll(User user, int page, int size, ExpenseFilter filter) {
        Specification<Expense> specification = ExpenseSpecification.withFilters(user.getId(), filter);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "expenseDate");
        Page<ExpenseResponse> expenses = expenseRepository.findAll(specification, pageable).map(ExpenseResponse::fromEntity);
        return new PageResponse<>(expenses);
    }

    public ExpenseResponse update(UUID id, UpdateExpenseRequest body, User user) {
        Expense expense = findByIdAndUser(id, user.getId());
        if (body.amount() != null) {
            expense.setAmount(body.amount());
        }
        if (body.description() != null) {
            expense.setDescription(body.description());
        }
        if (body.expenseDate() != null) {
            expense.setExpenseDate(body.expenseDate());
        }
        if (body.category() != null) {
            expense.setCategory(body.category());
        }
        if (body.currency() != null) {
            expense.setCurrency(body.currency());
        }
        if (body.paymentMethod() != null) {
            expense.setPaymentMethod(body.paymentMethod());
        }
        return ExpenseResponse.fromEntity(expenseRepository.save(expense));
    }

    public void delete(UUID id, User user) {
        Expense expense = findByIdAndUser(id, user.getId());
        expenseRepository.delete(expense);
    }

    private Expense findByIdAndUser(UUID id, UUID userId) {
        return expenseRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found"));
    }

    public ExpenseSummaryResponse getExpenseSummary(User user, LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }

        if (endDate == null) {
            endDate = LocalDate.now();
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        ExpenseCountAndTotalProjection expenseCountAndTotal = expenseRepository.countAndSumTotalAmount(user.getId(), startDateTime, endDateTime);

        return new ExpenseSummaryResponse(
                startDate, endDate,
                expenseRepository.groupByCurrencyAndSumAmount(user.getId(), startDateTime, endDateTime),
                expenseRepository.groupByExpenseCategoryAndSumAmount(user.getId(), startDateTime, endDateTime),
                expenseRepository.getMostUsedPaymentMethod(user.getId(), startDateTime, endDateTime),
                expenseCountAndTotal.getTotalExpense(),
                expenseCountAndTotal.getCount()
        );
    }
}
