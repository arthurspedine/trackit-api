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
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
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
        user = new User(
                "Test User",
                "test@test.com",
                "password"
        );

        user2 = new User(
                "Test User 2",
                "test2@test.com",
                "password"
        );

        createBody = new CreateExpenseRequest(
                BigDecimal.valueOf(100.00),
                "Test Expense",
                LocalDateTime.now().minusHours(1), // Past date to satisfy domain validation
                ECategory.FOOD,
                ECurrency.BRL,
                EPaymentMethod.BANK_TRANSFER
        );
    }

    @Test
    @DisplayName("Should save an expense successfully")
    void saveExpense() {
        Expense savedExpense = new Expense(
                createBody.amount(),
                createBody.description(),
                createBody.expenseDate(),
                createBody.category(),
                createBody.currency(),
                createBody.paymentMethod(),
                user
        );
        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        expenseService.save(createBody, user);

        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    @DisplayName("Should find all expenses with pagination and filtering")
    void findAll() {
        ExpenseFilter filter = new ExpenseFilter(
                String.valueOf(LocalDate.now().getMonthValue()),
                LocalDate.now().atStartOfDay(),
                LocalDate.now().atTime(LocalTime.MAX),
                createBody.currency(),
                createBody.category(),
                createBody.paymentMethod()
        );

        Expense expense = new Expense(
                createBody.amount(),
                createBody.description(),
                createBody.expenseDate(),
                createBody.category(),
                createBody.currency(),
                createBody.paymentMethod(),
                user
        );

        Page<Expense> page = new PageImpl<>(List.of(expense));
        when(expenseRepository.findAll(eq(user), any(Pageable.class), eq(filter))).thenReturn(page);

        PageResponse<ExpenseResponse> result = expenseService.findAll(user, 0, 10, filter);

        assertEquals(1, result.totalElements());
        assertEquals(ExpenseResponse.fromDomain(expense), result.content().get(0));
        assertEquals(1, result.totalPages());
    }

    @Test
    @DisplayName("Should update an existing expense")
    void updateExpense() {
        UUID expenseId = UUID.randomUUID();

        // Create existing expense using domain constructor
        Expense existingExpense = new Expense(
                expenseId,
                LocalDateTime.now().minusDays(2),
                BigDecimal.valueOf(50),
                "Old Description",
                LocalDateTime.now().minusDays(1),
                ECategory.HEALTH,
                ECurrency.USD,
                EPaymentMethod.CREDIT_CARD,
                user
        );

        UpdateExpenseRequest updateBody = new UpdateExpenseRequest(
                BigDecimal.valueOf(100),
                "Updated Description",
                LocalDateTime.now().minusHours(2), // Past date for validation
                ECategory.FOOD,
                ECurrency.BRL,
                EPaymentMethod.BANK_TRANSFER
        );

        when(expenseRepository.findByIdAndUser_Id(expenseId, user.getId()))
                .thenReturn(existingExpense);
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
    void updateExpenseWithNullFields() {
        UUID expenseId = UUID.randomUUID();

        Expense existingExpense = new Expense(
                expenseId,
                LocalDateTime.now().minusDays(2),
                BigDecimal.valueOf(50),
                "Old Description",
                LocalDateTime.now().minusDays(1),
                ECategory.HEALTH,
                ECurrency.USD,
                EPaymentMethod.CREDIT_CARD,
                user
        );

        UpdateExpenseRequest updateBody = new UpdateExpenseRequest(
                null, null, null, null, null, null
        );

        when(expenseRepository.findByIdAndUser_Id(expenseId, user.getId()))
                .thenReturn(existingExpense);
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

        // Create existing expense using domain constructor
        Expense existingExpense = new Expense(
                expenseId,
                LocalDateTime.now().minusDays(2),
                BigDecimal.valueOf(50),
                "Old Description",
                LocalDateTime.now().minusDays(1),
                ECategory.HEALTH,
                ECurrency.USD,
                EPaymentMethod.CREDIT_CARD,
                user
        );

        when(expenseRepository.findByIdAndUser_Id(expenseId, user.getId()))
                .thenReturn(existingExpense);

        expenseService.delete(expenseId, user);

        verify(expenseRepository).findByIdAndUser_Id(expenseId, user.getId());
        verify(expenseRepository).delete(existingExpense);
    }

    @Test
    @DisplayName("Should not delete an existing expense because it belongs to a different user")
    void deleteExpenseFromDifferentUser() {
        UUID expenseId = UUID.randomUUID();

        when(expenseRepository.findByIdAndUser_Id(expenseId, user2.getId()))
                .thenThrow(new EntityNotFoundException("Expense not found for id: " + expenseId));

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> expenseService.delete(expenseId, user2));

        assertEquals("Expense not found for id: " + expenseId, thrown.getMessage());
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
    void getExpenseSummaryWithInvalidDateRange() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().minusDays(1);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> expenseService.getExpenseSummary(user, startDate, endDate));

        assertEquals("Start date must be before or equal to end date", thrown.getMessage());
    }

    @Test
    @DisplayName("Should validate user input parameters")
    void shouldValidateUserInputParameters() {
        IllegalArgumentException thrown1 = assertThrows(IllegalArgumentException.class,
                () -> expenseService.findAll(null, 0, 10, null));
        assertEquals("User cannot be null", thrown1.getMessage());

        IllegalArgumentException thrown2 = assertThrows(IllegalArgumentException.class,
                () -> expenseService.findAll(user, -1, 10, null));
        assertEquals("Page number cannot be negative", thrown2.getMessage());

        IllegalArgumentException thrown3 = assertThrows(IllegalArgumentException.class,
                () -> expenseService.findAll(user, 0, 0, null));
        assertEquals("Page size must be greater than zero", thrown3.getMessage());
    }

    @Test
    @DisplayName("Should validate expense ID for operations")
    void shouldValidateExpenseIdForOperations() {
        IllegalArgumentException thrown1 = assertThrows(IllegalArgumentException.class,
                () -> expenseService.update(null, new UpdateExpenseRequest(null, null, null, null, null, null), user));
        assertEquals("Expense ID cannot be null", thrown1.getMessage());

        IllegalArgumentException thrown2 = assertThrows(IllegalArgumentException.class,
                () -> expenseService.delete(null, user));
        assertEquals("Expense ID cannot be null", thrown2.getMessage());
    }

    @Test
    @DisplayName("Should enforce domain validation on save")
    void shouldEnforceDomainValidationOnSave() {
        CreateExpenseRequest invalidRequest = new CreateExpenseRequest(
                BigDecimal.valueOf(-100.00),
                "Test Expense",
                LocalDateTime.now().minusHours(1),
                ECategory.FOOD,
                ECurrency.BRL,
                EPaymentMethod.BANK_TRANSFER
        );

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> expenseService.save(invalidRequest, user));
        assertEquals("Amount must be positive", thrown.getMessage());
    }

    @Test
    @DisplayName("Should enforce domain validation on update")
    void shouldEnforceDomainValidationOnUpdate() {
        UUID expenseId = UUID.randomUUID();
        Expense existingExpense = new Expense(
                expenseId,
                LocalDateTime.now().minusDays(2),
                BigDecimal.valueOf(50),
                "Valid Description",
                LocalDateTime.now().minusDays(1),
                ECategory.HEALTH,
                ECurrency.USD,
                EPaymentMethod.CREDIT_CARD,
                user
        );

        when(expenseRepository.findByIdAndUser_Id(expenseId, user.getId()))
                .thenReturn(existingExpense);

        UpdateExpenseRequest invalidRequest = new UpdateExpenseRequest(
                null, "ab", null, null, null, null
        );

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> expenseService.update(expenseId, invalidRequest, user));
        assertEquals("Description must be between 3 and 255 characters", thrown.getMessage());
    }
}