package com.spedine.trackit.repository;

import com.spedine.trackit.dto.CreateExpenseRequest;
import com.spedine.trackit.dto.PaymentMethodSummary;
import com.spedine.trackit.model.*;
import com.spedine.trackit.projection.ExpenseCategoryProjection;
import com.spedine.trackit.projection.ExpenseCountAndTotalProjection;
import com.spedine.trackit.projection.ExpenseCurrencyProjection;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ExpenseRepositoryTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    ExpenseRepository expenseRepository;

    User user;

    // Five expenses
    // Category FOOD: 3 expenses
    // Category HEALTH: 2 expense
    // Category ENTERTAINMENT: 2 expense
    // Total amount: 100 + 200 + 50 + 150 + 450 + 50 + 50
    // Total count: 7 expenses - One expense in the next month
    // Payment methods: CASH, CREDIT_CARD, DEBIT_CARD, BANK_TRANSFER
    @BeforeEach
    void setup() {
        user = new User();
        user.setName("Test User");
        user.setEmail("test@test.com");
        user.setPassword("password");
        entityManager.persist(user);

        createExpense(
                new CreateExpenseRequest(
                        BigDecimal.valueOf(100),
                        "Test Expense 1",
                        LocalDateTime.now(),
                        ECategory.FOOD,
                        ECurrency.GBP,
                        EPaymentMethod.CASH
                ),
                user
        );
        createExpense(
                new CreateExpenseRequest(
                        BigDecimal.valueOf(200),
                        "Test Expense 2",
                        LocalDateTime.now(),
                        ECategory.FOOD,
                        ECurrency.GBP,
                        EPaymentMethod.CREDIT_CARD
                ),
                user
        );
        createExpense(
                new CreateExpenseRequest(
                        BigDecimal.valueOf(50),
                        "Test Expense 3",
                        LocalDateTime.now(),
                        ECategory.FOOD,
                        ECurrency.GBP,
                        EPaymentMethod.CREDIT_CARD
                ),
                user
        );
        createExpense(
                new CreateExpenseRequest(
                        BigDecimal.valueOf(150),
                        "Test Expense 4",
                        LocalDateTime.now(),
                        ECategory.HEALTH,
                        ECurrency.GBP,
                        EPaymentMethod.DEBIT_CARD
                ),
                user
        );
        createExpense(
                new CreateExpenseRequest(
                        BigDecimal.valueOf(450),
                        "Test Expense 5",
                        LocalDateTime.now(),
                        ECategory.ENTERTAINMENT,
                        ECurrency.BRL,
                        EPaymentMethod.CREDIT_CARD
                ),
                user
        );

        createExpense(
                new CreateExpenseRequest(
                        BigDecimal.valueOf(50),
                        "Test Expense 6",
                        LocalDateTime.now().plusMonths(1),
                        ECategory.ENTERTAINMENT,
                        ECurrency.BRL,
                        EPaymentMethod.CREDIT_CARD
                ),
                user
        );

        createExpense(
                new CreateExpenseRequest(
                        BigDecimal.valueOf(50),
                        "Test Expense 7",
                        LocalDateTime.now().plusMonths(1),
                        ECategory.HEALTH,
                        ECurrency.BRL,
                        EPaymentMethod.BANK_TRANSFER
                ),
                user
        );

    }

    @Test
    @DisplayName("Should return an expense by ID and user ID")
    void findByIdAndUser_Id() {
        Expense expense = createExpense(
                new CreateExpenseRequest(
                        BigDecimal.valueOf(100),
                        "Test Expense",
                        LocalDateTime.now(),
                        ECategory.FOOD,
                        ECurrency.GBP,
                        EPaymentMethod.CASH
                ),
                user
        );

        Optional<Expense> savedExpense = expenseRepository.findByIdAndUser_Id(expense.getId(), user.getId());

        assertThat(savedExpense).isNotEmpty();
    }

    @Test
    @DisplayName("Should not return an expense by ID and user ID")
    void findByIdAndUser_Id2() {
        Optional<Expense> savedExpense = expenseRepository.findByIdAndUser_Id(UUID.randomUUID(), user.getId());

        assertThat(savedExpense).isEmpty();
    }

    @Test
    @DisplayName("Should count 5 and sum total amount of expenses of 950")
    void countAndSumTotalAmount() {
        ExpenseCountAndTotalProjection expenseCountAndTotalProjection = expenseRepository.countAndSumTotalAmount(
                user.getId(),
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atTime(LocalTime.MAX)
        );
        assertThat(expenseCountAndTotalProjection).isNotNull();
        assertThat(expenseCountAndTotalProjection.getCount()).isEqualTo(5);
        assertThat(expenseCountAndTotalProjection.getTotalExpense()).isEqualByComparingTo(BigDecimal.valueOf(950));
    }

    @Test
    @DisplayName("Should count 0 and sum total amount of expenses of 0 when no expenses in the date range")
    void countAndSumTotalAmount2() {
        ExpenseCountAndTotalProjection expenseCountAndTotalProjection = expenseRepository.countAndSumTotalAmount(
                user.getId(),
                LocalDate.now().atStartOfDay().minusYears(1),
                LocalDate.now().atTime(LocalTime.MAX).minusYears(1)
        );
        assertThat(expenseCountAndTotalProjection).isNotNull();
        assertThat(expenseCountAndTotalProjection.getCount()).isEqualTo(0);
        assertThat(expenseCountAndTotalProjection.getTotalExpense()).isEqualByComparingTo(BigDecimal.valueOf(0));
    }

    @Test
    @DisplayName("Should group expenses by category and sum amount with Entertainment with highest amount")
    void groupByExpenseCategoryAndSumAmount() {
        List<ExpenseCategoryProjection> expenseCategoryProjections = expenseRepository.groupByExpenseCategoryAndSumAmount(
                user.getId(),
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atTime(LocalTime.MAX).plusMonths(1) // to get the next month expense
        );
        assertThat(expenseCategoryProjections).isNotNull();
        assertThat(expenseCategoryProjections).hasSize(3); // 3 Categories

        // Entertainment expenses
        assertThat(expenseCategoryProjections.get(0).getCategory()).isEqualTo(ECategory.ENTERTAINMENT);
        assertThat(expenseCategoryProjections.get(0).getCount()).isEqualTo(2);
        assertThat(expenseCategoryProjections.get(0).getTotal()).isEqualByComparingTo(BigDecimal.valueOf(500));

        // Food expenses
        assertThat(expenseCategoryProjections.get(1).getCategory()).isEqualTo(ECategory.FOOD);
        assertThat(expenseCategoryProjections.get(1).getCount()).isEqualTo(3);
        assertThat(expenseCategoryProjections.get(1).getTotal()).isEqualByComparingTo(BigDecimal.valueOf(350));

        // Health expenses
        assertThat(expenseCategoryProjections.get(2).getCategory()).isEqualTo(ECategory.HEALTH);
        assertThat(expenseCategoryProjections.get(2).getCount()).isEqualTo(2);
        assertThat(expenseCategoryProjections.get(2).getTotal()).isEqualByComparingTo(BigDecimal.valueOf(200));
    }

    @Test
    @DisplayName("Should return empty list when no expenses in the date range")
    void groupByExpenseCategoryAndSumAmount2() {
        List<ExpenseCategoryProjection> expenseCategoryProjections = expenseRepository.groupByExpenseCategoryAndSumAmount(
                user.getId(),
                LocalDate.now().atStartOfDay().minusYears(1),
                LocalDate.now().atTime(LocalTime.MAX).minusYears(1)
        );

        assertThat(expenseCategoryProjections).isNotNull();
        assertThat(expenseCategoryProjections).isEmpty();
    }

    @Test
    @DisplayName("Should group expenses by currency and sum amount and return GBP with highest amount")
    void groupByCurrencyAndSumAmount() {
        List<ExpenseCurrencyProjection> expenseCurrencyProjections = expenseRepository.groupByCurrencyAndSumAmount(
                user.getId(),
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atTime(LocalTime.MAX)
        );

        assertThat(expenseCurrencyProjections).isNotNull();
        assertThat(expenseCurrencyProjections).hasSize(2); // 2 Currencies
        // GBP expenses
        assertThat(expenseCurrencyProjections.get(0).getCurrency()).isEqualTo(ECurrency.GBP);
        assertThat(expenseCurrencyProjections.get(0).getTotal()).isEqualByComparingTo(BigDecimal.valueOf(500));

        // BRL expenses
        assertThat(expenseCurrencyProjections.get(1).getCurrency()).isEqualTo(ECurrency.BRL);
        assertThat(expenseCurrencyProjections.get(1).getTotal()).isEqualByComparingTo(BigDecimal.valueOf(450));
    }

    @Test
    @DisplayName("Should return empty list when no expenses in the date range")
    void groupByCurrencyAndSumAmount2() {
        List<ExpenseCurrencyProjection> expenseCurrencyProjections = expenseRepository.groupByCurrencyAndSumAmount(
                user.getId(),
                LocalDate.now().atStartOfDay().minusYears(1),
                LocalDate.now().atTime(LocalTime.MAX).minusYears(1)
        );

        assertThat(expenseCurrencyProjections).isNotNull();
        assertThat(expenseCurrencyProjections).isEmpty();
    }

    @Test
    @DisplayName("Should return CREDIT_CARD as most used payment method")
    void getMostUsedPaymentMethod() {
        PaymentMethodSummary paymentMethodSummary = expenseRepository.getMostUsedPaymentMethod(
                user.getId(),
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atTime(LocalTime.MAX)
        );

        assertThat(paymentMethodSummary).isNotNull();
        assertThat(paymentMethodSummary.method()).isEqualTo(EPaymentMethod.CREDIT_CARD);
        assertThat(paymentMethodSummary.usageCount()).isEqualTo(3); // 3 CREDIT_CARD expenses
    }

    @Test
    @DisplayName("Should return BANK_TRANSFER as the most used when counts are equal, using ascending alphabetical order as a tiebreaker")
    void getMostUsedPaymentMethod2() {
        PaymentMethodSummary paymentMethodSummary = expenseRepository.getMostUsedPaymentMethod(
                user.getId(),
                LocalDate.now().atStartOfDay().plusMonths(1),
                LocalDate.now().atTime(LocalTime.MAX).plusMonths(1)
        );

        assertThat(paymentMethodSummary).isNotNull();
        assertThat(paymentMethodSummary.method()).isEqualTo(EPaymentMethod.BANK_TRANSFER);
        assertThat(paymentMethodSummary.usageCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return null when no expenses in the date range")
    void getMostUsedPaymentMethod3() {
        PaymentMethodSummary paymentMethodSummary = expenseRepository.getMostUsedPaymentMethod(
                user.getId(),
                LocalDate.now().atStartOfDay().minusYears(1),
                LocalDate.now().atTime(LocalTime.MAX).minusYears(1)
        );

        assertThat(paymentMethodSummary).isNull();
    }

    private Expense createExpense(CreateExpenseRequest createExpenseRequest, User user) {
        Expense expense = new Expense();
        expense.setUser(user);
        expense.setAmount(createExpenseRequest.amount());
        expense.setDescription(createExpenseRequest.description());
        expense.setCategory(createExpenseRequest.category());
        expense.setCurrency(createExpenseRequest.currency());
        expense.setExpenseDate(createExpenseRequest.expenseDate());
        expense.setPaymentMethod(createExpenseRequest.paymentMethod());
        entityManager.persist(expense);
        return expense;
    }

}