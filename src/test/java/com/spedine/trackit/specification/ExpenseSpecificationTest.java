package com.spedine.trackit.specification;

import com.spedine.trackit.dto.ExpenseFilter;
import com.spedine.trackit.model.*;
import com.spedine.trackit.repository.ExpenseRepository;
import com.spedine.trackit.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class ExpenseSpecificationTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private Expense expense1;
    private Expense expense2;
    private Expense expense3;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setName("user1");
        user1.setEmail("user1@test.com");
        user1.setPassword("password");
        user1 = userRepository.save(user1);

        user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@test.com");
        user2.setPassword("password");
        user2 = userRepository.save(user2);

        expense1 = new Expense();
        expense1.setAmount(new BigDecimal("100.00"));
        expense1.setDescription("Test expense 1");
        expense1.setExpenseDate(LocalDateTime.of(2025, 8, 15, 10, 0));
        expense1.setCategory(ECategory.FOOD);
        expense1.setCurrency(ECurrency.BRL);
        expense1.setPaymentMethod(EPaymentMethod.CREDIT_CARD);
        expense1.setUser(user1);
        expense1 = expenseRepository.save(expense1);

        expense2 = new Expense();
        expense2.setAmount(new BigDecimal("200.00"));
        expense2.setDescription("Test expense 2");
        expense2.setExpenseDate(LocalDateTime.of(2025, 7, 15, 10, 0));
        expense2.setCategory(ECategory.TRANSPORT);
        expense2.setCurrency(ECurrency.USD);
        expense2.setPaymentMethod(EPaymentMethod.DEBIT_CARD);
        expense2.setUser(user1);
        expense2 = expenseRepository.save(expense2);

        expense3 = new Expense();
        expense3.setAmount(new BigDecimal("300.00"));
        expense3.setDescription("Test expense 3");
        expense3.setExpenseDate(LocalDateTime.of(2024, 1, 20, 10, 0));
        expense3.setCategory(ECategory.FOOD);
        expense3.setCurrency(ECurrency.BRL);
        expense3.setPaymentMethod(EPaymentMethod.CREDIT_CARD);
        expense3.setUser(user2);
        expense3 = expenseRepository.save(expense3);
    }

    @Test
    @DisplayName("Should filter by user only")
    void withFilters() {
        ExpenseFilter filter = new ExpenseFilter(null, null, null, null, null, null);
        Specification<Expense> spec = ExpenseSpecification.withFilters(user1.getId(), filter);

        Page<Expense> result = expenseRepository.findAll(spec, PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(e -> e.getUser().getId().equals(user1.getId())));
    }

    @Test
    @DisplayName("Should filter by month")
    void withFilters2() {
        ExpenseFilter filter = new ExpenseFilter("2025-08", null, null, null, null, null);
        Specification<Expense> spec = ExpenseSpecification.withFilters(user1.getId(), filter);

        Page<Expense> result = expenseRepository.findAll(spec, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(expense1.getId(), result.getContent().get(0).getId());
    }

    @Test
    @DisplayName("Should filter by date range")
    void withFilters3() {
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2025, 8, 31, 23, 59);
        ExpenseFilter filter = new ExpenseFilter(null, startDate, endDate, null, null, null);
        Specification<Expense> spec = ExpenseSpecification.withFilters(user1.getId(), filter);

        Page<Expense> result = expenseRepository.findAll(spec, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(expense1.getId(), result.getContent().get(0).getId());
    }

    @Test
    @DisplayName("Should filter by category")
    void withFilters4() {
        ExpenseFilter filter = new ExpenseFilter(null, null, null, null, ECategory.FOOD, null);
        Specification<Expense> spec = ExpenseSpecification.withFilters(user1.getId(), filter);

        Page<Expense> result = expenseRepository.findAll(spec, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(expense1.getId(), result.getContent().get(0).getId());
    }

    @Test
    @DisplayName("Should filter by currency")
    void withFilters5() {
        ExpenseFilter filter = new ExpenseFilter(null, null, null, ECurrency.USD, null, null);
        Specification<Expense> spec = ExpenseSpecification.withFilters(user1.getId(), filter);

        Page<Expense> result = expenseRepository.findAll(spec, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(expense2.getId(), result.getContent().get(0).getId());
    }

    @Test
    @DisplayName("Should filter by payment method")
    void withFilters6() {
        ExpenseFilter filter = new ExpenseFilter(null, null, null, null, null, EPaymentMethod.DEBIT_CARD);
        Specification<Expense> spec = ExpenseSpecification.withFilters(user1.getId(), filter);

        Page<Expense> result = expenseRepository.findAll(spec, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(expense2.getId(), result.getContent().get(0).getId());
    }

    @Test
    @DisplayName("Should combine multiple filters")
    void withFilters7() {
        ExpenseFilter filter = new ExpenseFilter(null, null, null, ECurrency.BRL, ECategory.FOOD, EPaymentMethod.CREDIT_CARD);
        Specification<Expense> spec = ExpenseSpecification.withFilters(user1.getId(), filter);

        Page<Expense> result = expenseRepository.findAll(spec, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(expense1.getId(), result.getContent().get(0).getId());
    }

    @Test
    @DisplayName("Should return empty when no match")
    void withFilters8() {
        ExpenseFilter filter = new ExpenseFilter(null, null, null, null, ECategory.OTHER, null);
        Specification<Expense> spec = ExpenseSpecification.withFilters(user1.getId(), filter);

        Page<Expense> result = expenseRepository.findAll(spec, PageRequest.of(0, 10));

        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    @DisplayName("Should filter by start date only")
    void withFilters9() {
        LocalDateTime startDate = LocalDateTime.of(2025, 8, 1, 0, 0);
        ExpenseFilter filter = new ExpenseFilter(null, startDate, null, null, null, null);
        Specification<Expense> spec = ExpenseSpecification.withFilters(user1.getId(), filter);

        Page<Expense> result = expenseRepository.findAll(spec, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(expense1.getId(), result.getContent().get(0).getId());
    }

    @Test
    @DisplayName("Should filter by end date only")
    void withFilters10() {
        LocalDateTime endDate = LocalDateTime.of(2024, 1, 31, 23, 59);
        ExpenseFilter filter = new ExpenseFilter(null, null, endDate, null, null, null);
        Specification<Expense> spec = ExpenseSpecification.withFilters(user2.getId(), filter);

        Page<Expense> result = expenseRepository.findAll(spec, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(expense3.getId(), result.getContent().get(0).getId());
    }
}