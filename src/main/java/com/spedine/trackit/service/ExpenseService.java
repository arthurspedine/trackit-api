package com.spedine.trackit.service;

import com.spedine.trackit.dto.*;
import com.spedine.trackit.model.Expense;
import com.spedine.trackit.model.User;
import com.spedine.trackit.projection.ExpenseCountAndTotalProjection;
import com.spedine.trackit.repository.ExpenseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    public void save(CreateExpenseRequest body, User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        Expense expense = new Expense(
                body.amount(),
                body.description(),
                body.expenseDate(),
                body.category(),
                body.currency(),
                body.paymentMethod(),
                user
        );
        repository.save(expense);
    }

    public PageResponse<ExpenseResponse> findAll(User user, int page, int size, ExpenseFilter filter) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "expenseDate");
        Page<ExpenseResponse> expenses = repository.findAll(user, pageable, filter).map(ExpenseResponse::fromDomain);
        return new PageResponse<>(expenses);
    }

    public ExpenseResponse update(UUID id, UpdateExpenseRequest body, User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (id == null) {
            throw new IllegalArgumentException("Expense ID cannot be null");
        }
        Expense expense = repository.findByIdAndUser_Id(id, user.getId());
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
        return ExpenseResponse.fromDomain(repository.save(expense));
    }

    public void delete(UUID id, User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (id == null) {
            throw new IllegalArgumentException("Expense ID cannot be null");
        }
        Expense expense = repository.findByIdAndUser_Id(id, user.getId());
        repository.delete(expense);
    }

    public ExpenseSummaryResponse getExpenseSummary(User user, LocalDate startDate, LocalDate endDate) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }

        if (endDate == null) {
            endDate = LocalDate.now();
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        ExpenseCountAndTotalProjection expenseCountAndTotal = repository.countAndSumTotalAmount(user.getId(), startDateTime, endDateTime);

        return new ExpenseSummaryResponse(
                startDate, endDate,
                repository.groupByCurrencyAndSumAmount(user.getId(), startDateTime, endDateTime),
                repository.groupByExpenseCategoryAndSumAmount(user.getId(), startDateTime, endDateTime),
                repository.getMostUsedPaymentMethod(user.getId(), startDateTime, endDateTime),
                expenseCountAndTotal.getTotalExpense(),
                expenseCountAndTotal.getCount()
        );
    }

    public ExpenseResponse findByUserAndId(UUID id, User user) {
        return ExpenseResponse.fromDomain(repository.findByIdAndUser_Id(id, user.getId()));
    }
}
