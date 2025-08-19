package com.spedine.trackit.service;

import com.spedine.trackit.dto.*;
import com.spedine.trackit.model.*;
import com.spedine.trackit.projection.ExpenseCategoryProjection;
import com.spedine.trackit.projection.ExpenseCountAndTotalProjection;
import com.spedine.trackit.projection.ExpenseCurrencyProjection;
import com.spedine.trackit.repository.ExpenseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {


    @Mock
    ExpenseRepository expenseRepository;

    @InjectMocks
    ExpenseService expenseService;

    User user;

    User user2;

    CreateExpenseRequest createBody;

    @BeforeEach
    void setup() {
        user = new User();
        user.setName("Test User");
        user.setEmail("test@test.com");
        user.setPassword("password");
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());

        user2 = new User();
        user2.setName("Test User 2");
        user2.setEmail("test@test.com");
        user2.setPassword("password");
        ReflectionTestUtils.setField(user2, "id", UUID.randomUUID());

        createBody = new CreateExpenseRequest(
                BigDecimal.valueOf(100.00),
                "Test Expense",
                LocalDateTime.now(),
                ECategory.FOOD,
                ECurrency.BRL,
                EPaymentMethod.BANK_TRANSFER
        );
    }

    @Test
    @DisplayName("Should save an expense successfully")
    void saveExpense() {
        expenseService.save(createBody, user);
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    @DisplayName("Should find all expenses with pagination and filtering")
    void findAll() {
        ExpenseFilter filter = new ExpenseFilter(null, null, null, null, null, null);
        Expense expense = new Expense();
        Page<Expense> page = new PageImpl<>(List.of(expense));

        when(expenseRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<ExpenseResponse> result = expenseService.findAll(user, 0, 10, filter);
        assertEquals(1, result.getTotalElements());
        assertEquals(ExpenseResponse.fromEntity(expense), result.getContent().get(0));
        assertEquals(1, result.getTotalPages());
    }

    @Test
    @DisplayName("Should update an existing expense")
    void updateExpense() {
        UUID expenseId = UUID.randomUUID();

        Expense existingExpense = new Expense();
        ReflectionTestUtils.setField(existingExpense, "id", expenseId);
        existingExpense.setAmount(BigDecimal.valueOf(50));
        existingExpense.setDescription("Old Description");
        existingExpense.setExpenseDate(LocalDateTime.now().minusDays(1));
        existingExpense.setCategory(ECategory.HEALTH);
        existingExpense.setCurrency(ECurrency.USD);
        existingExpense.setPaymentMethod(EPaymentMethod.CREDIT_CARD);
        existingExpense.setUser(user);

        UpdateExpenseRequest updateBody = new UpdateExpenseRequest(
                BigDecimal.valueOf(100),
                "Updated Description",
                LocalDateTime.now(),
                ECategory.FOOD,
                ECurrency.BRL,
                EPaymentMethod.BANK_TRANSFER
        );

        when(expenseRepository.findByIdAndUser_Id(expenseId, user.getId()))
                .thenReturn(Optional.of(existingExpense));

        when(expenseRepository.save(any(Expense.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ExpenseResponse response = expenseService.update(expenseId, updateBody, user);

        assertNotNull(response);
        assertEquals(updateBody.amount(), response.amount());
        assertEquals(updateBody.description(), response.description());
        assertEquals(updateBody.expenseDate(), response.expenseDate());
        assertEquals(updateBody.category(), response.category());
        assertEquals(updateBody.currency(), response.currency());
        assertEquals(updateBody.paymentMethod(), response.paymentMethod());

        verify(expenseRepository).findByIdAndUser_Id(expenseId, user.getId());
        verify(expenseRepository).save(existingExpense);
    }

    @Test
    @DisplayName("Should not change existing data when fields are null in the update request")
    void updateExpense2() {
        UUID expenseId = UUID.randomUUID();

        Expense existingExpense = new Expense();
        ReflectionTestUtils.setField(existingExpense, "id", expenseId);
        existingExpense.setAmount(BigDecimal.valueOf(50));
        existingExpense.setDescription("Old Description");
        existingExpense.setExpenseDate(LocalDateTime.now().minusDays(1));
        existingExpense.setCategory(ECategory.HEALTH);
        existingExpense.setCurrency(ECurrency.USD);
        existingExpense.setPaymentMethod(EPaymentMethod.CREDIT_CARD);
        existingExpense.setUser(user);

        UpdateExpenseRequest updateBody = new UpdateExpenseRequest(
                null, null, null,
                null, null, null
        );

        when(expenseRepository.findByIdAndUser_Id(expenseId, user.getId()))
                .thenReturn(Optional.of(existingExpense));

        when(expenseRepository.save(any(Expense.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ExpenseResponse response = expenseService.update(expenseId, updateBody, user);

        assertNotNull(response);
        assertEquals(existingExpense.getAmount(), response.amount());
        assertEquals(existingExpense.getDescription(), response.description());
        assertEquals(existingExpense.getExpenseDate(), response.expenseDate());
        assertEquals(existingExpense.getCategory(), response.category());
        assertEquals(existingExpense.getCurrency(), response.currency());
        assertEquals(existingExpense.getPaymentMethod(), response.paymentMethod());

        verify(expenseRepository).findByIdAndUser_Id(expenseId, user.getId());
        verify(expenseRepository).save(existingExpense);
    }

    @Test
    @DisplayName("Should delete an existing expense")
    void deleteExpense() {
        UUID expenseId = UUID.randomUUID();

        Expense existingExpense = new Expense();
        ReflectionTestUtils.setField(existingExpense, "id", expenseId);
        existingExpense.setAmount(BigDecimal.valueOf(50));
        existingExpense.setDescription("Old Description");
        existingExpense.setExpenseDate(LocalDateTime.now().minusDays(1));
        existingExpense.setCategory(ECategory.HEALTH);
        existingExpense.setCurrency(ECurrency.USD);
        existingExpense.setPaymentMethod(EPaymentMethod.CREDIT_CARD);
        existingExpense.setUser(user);

        when(expenseRepository.findByIdAndUser_Id(expenseId, user.getId()))
                .thenReturn(Optional.of(existingExpense));

        expenseService.delete(expenseId, user);

        verify(expenseRepository).findByIdAndUser_Id(expenseId, user.getId());
        verify(expenseRepository).delete(existingExpense);
    }

    @Test
    @DisplayName("Should not delete an existing expense because it belongs to a different user")
    void deleteExpense2() {
        UUID expenseId = UUID.randomUUID();

        Expense existingExpense = new Expense();
        ReflectionTestUtils.setField(existingExpense, "id", expenseId);
        existingExpense.setAmount(BigDecimal.valueOf(50));
        existingExpense.setDescription("Old Description");
        existingExpense.setExpenseDate(LocalDateTime.now().minusDays(1));
        existingExpense.setCategory(ECategory.HEALTH);
        existingExpense.setCurrency(ECurrency.USD);
        existingExpense.setPaymentMethod(EPaymentMethod.CREDIT_CARD);
        existingExpense.setUser(user);

        when(expenseRepository.findByIdAndUser_Id(expenseId, user2.getId()))
                .thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> expenseService.delete(expenseId, user2));

        assertEquals("Expense not found", thrown.getMessage());
        verify(expenseRepository, times(0)).delete(any(Expense.class));
    }

    @Test
    @DisplayName("Should return summary with default dates when both are null")
    void getExpenseSummary() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);

        when(expenseRepository.countAndSumTotalAmount(any(), any(), any()))
                .thenReturn(new ExpenseCountAndTotalProjection() {
                    @Override
                    public BigDecimal getTotalExpense() {
                        return BigDecimal.valueOf(100);
                    }
                    @Override
                    public int getCount() {
                        return 5;
                    }
                });

        when(expenseRepository.groupByCurrencyAndSumAmount(any(), any(), any()))
                .thenReturn(List.of(new ExpenseCurrencyProjection() {
                    @Override
                    public ECurrency getCurrency() {
                        return ECurrency.USD;
                    }

                    @Override
                    public BigDecimal getTotal() {
                        return BigDecimal.valueOf(100);
                    }
                }));

        when(expenseRepository.groupByExpenseCategoryAndSumAmount(any(), any(), any()))
                .thenReturn(List.of(new ExpenseCategoryProjection() {
                    @Override
                    public ECategory getCategory() {
                        return ECategory.FOOD;
                    }
                    @Override
                    public BigDecimal getTotal() {
                        return BigDecimal.valueOf(100);
                    }
                    @Override
                    public int getCount() {
                        return 5;
                    }
                }));

        when(expenseRepository.getMostUsedPaymentMethod(any(), any(), any()))
                .thenReturn(new PaymentMethodSummary(EPaymentMethod.CREDIT_CARD, 3L));

        ExpenseSummaryResponse response = expenseService.getExpenseSummary(user, null, null);

        assertEquals(firstDayOfMonth, response.startDate());
        assertEquals(today, response.endDate());
        assertEquals(BigDecimal.valueOf(100), response.total());
        assertEquals(5, response.count());
        assertEquals(ECurrency.USD, response.totalsByCurrency().get(0).getCurrency());
        assertEquals(ECategory.FOOD, response.totalsByCategory().get(0).getCategory());
        assertEquals(EPaymentMethod.CREDIT_CARD, response.mostUsedPaymentMethod().method());
    }

    @Test
    @DisplayName("Should throw exception when startDate is after endDate")
    void getExpenseSummary2() {
        LocalDate start = LocalDate.now();
        LocalDate end = start.minusDays(1);

        assertThrows(IllegalArgumentException.class, () ->
                expenseService.getExpenseSummary(user, start, end)
        );
    }


}