package com.spedine.trackit.service;

import com.spedine.trackit.dto.CreateExpenseRequest;
import com.spedine.trackit.dto.ExpenseFilter;
import com.spedine.trackit.dto.ExpenseResponse;
import com.spedine.trackit.model.Expense;
import com.spedine.trackit.model.User;
import com.spedine.trackit.repository.ExpenseRepository;
import com.spedine.trackit.specification.ExpenseSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

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

    public Page<ExpenseResponse> findAll(User user, int page, int size, ExpenseFilter filter) {
        Specification<Expense> specification = ExpenseSpecification.withFilters(user.getId(), filter);
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "expenseDate");
        return expenseRepository.findAll(specification, pageable).map(ExpenseResponse::fromEntity);
    }
}
