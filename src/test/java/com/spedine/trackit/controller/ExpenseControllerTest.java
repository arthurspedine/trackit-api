package com.spedine.trackit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spedine.trackit.dto.CreateExpenseRequest;
import com.spedine.trackit.dto.ExpenseFilter;
import com.spedine.trackit.dto.ExpenseResponse;
import com.spedine.trackit.dto.ExpenseSummaryResponse;
import com.spedine.trackit.dto.PaymentMethodSummary;
import com.spedine.trackit.dto.UpdateExpenseRequest;
import com.spedine.trackit.infra.SecurityFilter;
import com.spedine.trackit.model.ECategory;
import com.spedine.trackit.model.ECurrency;
import com.spedine.trackit.model.EPaymentMethod;
import com.spedine.trackit.model.User;
import com.spedine.trackit.service.ExpenseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExpenseController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExpenseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ExpenseService expenseService;

    @MockitoBean
    SecurityFilter securityFilter;

    @Autowired
    ObjectMapper objectMapper;

    User mockUser;

    @BeforeEach
    void setup() {
        mockUser = new User();
        mockUser.setName("Test User");
        mockUser.setEmail("test@test.com");
        mockUser.setPassword("password");
        ReflectionTestUtils.setField(mockUser, "id", UUID.randomUUID());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(mockUser, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Should create expense")
    @WithMockUser(username = "test@test.com")
    void createExpense() throws Exception {
        CreateExpenseRequest createBody = new CreateExpenseRequest(
                BigDecimal.valueOf(100.00),
                "Test Expense",
                LocalDateTime.now().minusHours(1),
                ECategory.FOOD,
                ECurrency.BRL,
                EPaymentMethod.BANK_TRANSFER
        );

        mockMvc.perform(
                post("/api/expenses")
                        .with(user(mockUser))
                        .content(objectMapper.writeValueAsString(createBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Expense created successfully"));
    }

    @Test
    @DisplayName("Should return 400 when amount is null")
    @WithMockUser(username = "test@test.com")
    void createExpense2() throws Exception {
        CreateExpenseRequest createBody = new CreateExpenseRequest(
                null,
                "Test Expense",
                LocalDateTime.now().minusHours(1),
                ECategory.FOOD,
                ECurrency.BRL,
                EPaymentMethod.BANK_TRANSFER
        );

        mockMvc.perform(
                post("/api/expenses")
                        .with(user(mockUser))
                        .content(objectMapper.writeValueAsString(createBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when amount is zero")
    @WithMockUser(username = "test@test.com")
    void createExpense3() throws Exception {
        CreateExpenseRequest createBody = new CreateExpenseRequest(
                BigDecimal.ZERO,
                "Test Expense",
                LocalDateTime.now().minusHours(1),
                ECategory.FOOD,
                ECurrency.BRL,
                EPaymentMethod.BANK_TRANSFER
        );

        mockMvc.perform(
                post("/api/expenses")
                        .with(user(mockUser))
                        .content(objectMapper.writeValueAsString(createBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when amount is negative")
    @WithMockUser(username = "test@test.com")
    void createExpense4() throws Exception {
        CreateExpenseRequest createBody = new CreateExpenseRequest(
                BigDecimal.valueOf(-10.00),
                "Test Expense",
                LocalDateTime.now().minusHours(1),
                ECategory.FOOD,
                ECurrency.BRL,
                EPaymentMethod.BANK_TRANSFER
        );

        mockMvc.perform(
                post("/api/expenses")
                        .with(user(mockUser))
                        .content(objectMapper.writeValueAsString(createBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when description is null")
    @WithMockUser(username = "test@test.com")
    void createExpense5() throws Exception {
        CreateExpenseRequest createBody = new CreateExpenseRequest(
                BigDecimal.valueOf(100.00),
                null,
                LocalDateTime.now().minusHours(1),
                ECategory.FOOD,
                ECurrency.BRL,
                EPaymentMethod.BANK_TRANSFER
        );

        mockMvc.perform(
                post("/api/expenses")
                        .with(user(mockUser))
                        .content(objectMapper.writeValueAsString(createBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when description is too short")
    @WithMockUser(username = "test@test.com")
    void createExpense6() throws Exception {
        CreateExpenseRequest createBody = new CreateExpenseRequest(
                BigDecimal.valueOf(100.00),
                "Hi",
                LocalDateTime.now().minusHours(1),
                ECategory.FOOD,
                ECurrency.BRL,
                EPaymentMethod.BANK_TRANSFER
        );

        mockMvc.perform(
                post("/api/expenses")
                        .with(user(mockUser))
                        .content(objectMapper.writeValueAsString(createBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when description is too long")
    @WithMockUser(username = "test@test.com")
    void createExpense7() throws Exception {
        String longDescription = "A".repeat(256); // 256 characters, exceeds max of 255

        CreateExpenseRequest createBody = new CreateExpenseRequest(
                BigDecimal.valueOf(100.00),
                longDescription,
                LocalDateTime.now().minusHours(1),
                ECategory.FOOD,
                ECurrency.BRL,
                EPaymentMethod.BANK_TRANSFER
        );

        mockMvc.perform(
                post("/api/expenses")
                        .with(user(mockUser))
                        .content(objectMapper.writeValueAsString(createBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should accept description with minimum valid length")
    @WithMockUser(username = "test@test.com")
    void createExpense8() throws Exception {
        CreateExpenseRequest createBody = new CreateExpenseRequest(
                BigDecimal.valueOf(100.00),
                "Yes", // 3 characters, minimum valid
                LocalDateTime.now().minusHours(1),
                ECategory.FOOD,
                ECurrency.BRL,
                EPaymentMethod.BANK_TRANSFER
        );

        mockMvc.perform(
                post("/api/expenses")
                        .with(user(mockUser))
                        .content(objectMapper.writeValueAsString(createBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should accept description with maximum valid length")
    @WithMockUser(username = "test@test.com")
    void createExpense9() throws Exception {
        String maxDescription = "A".repeat(255); // 255 characters, maximum valid

        CreateExpenseRequest createBody = new CreateExpenseRequest(
                BigDecimal.valueOf(100.00),
                maxDescription,
                LocalDateTime.now().minusHours(1),
                ECategory.FOOD,
                ECurrency.BRL,
                EPaymentMethod.BANK_TRANSFER
        );

        mockMvc.perform(
                post("/api/expenses")
                        .with(user(mockUser))
                        .content(objectMapper.writeValueAsString(createBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should return 400 when expense date is null")
    @WithMockUser(username = "test@test.com")
    void createExpense10() throws Exception {
        CreateExpenseRequest createBody = new CreateExpenseRequest(
                BigDecimal.valueOf(100.00),
                "Test Expense",
                null,
                ECategory.FOOD,
                ECurrency.BRL,
                EPaymentMethod.BANK_TRANSFER
        );

        mockMvc.perform(
                post("/api/expenses")
                        .with(user(mockUser))
                        .content(objectMapper.writeValueAsString(createBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when expense date is in the future")
    @WithMockUser(username = "test@test.com")
    void createExpense11() throws Exception {
        CreateExpenseRequest createBody = new CreateExpenseRequest(
                BigDecimal.valueOf(100.00),
                "Test Expense",
                LocalDateTime.now().plusDays(1),
                ECategory.FOOD,
                ECurrency.BRL,
                EPaymentMethod.BANK_TRANSFER
        );

        mockMvc.perform(
                post("/api/expenses")
                        .with(user(mockUser))
                        .content(objectMapper.writeValueAsString(createBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should accept expense date in the past")
    @WithMockUser(username = "test@test.com")
    void createExpense12() throws Exception {
        CreateExpenseRequest createBody = new CreateExpenseRequest(
                BigDecimal.valueOf(100.00),
                "Test Expense",
                LocalDateTime.now().minusDays(1),
                ECategory.FOOD,
                ECurrency.BRL,
                EPaymentMethod.BANK_TRANSFER
        );

        mockMvc.perform(
                post("/api/expenses")
                        .with(user(mockUser))
                        .content(objectMapper.writeValueAsString(createBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should accept expense date as current time")
    @WithMockUser(username = "test@test.com")
    void createExpense13() throws Exception {
        CreateExpenseRequest createBody = new CreateExpenseRequest(
                BigDecimal.valueOf(100.00),
                "Test Expense",
                LocalDateTime.now(),
                ECategory.FOOD,
                ECurrency.BRL,
                EPaymentMethod.BANK_TRANSFER
        );

        mockMvc.perform(
                post("/api/expenses")
                        .with(user(mockUser))
                        .content(objectMapper.writeValueAsString(createBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should return 400 when category is null")
    @WithMockUser(username = "test@test.com")
    void createExpense14() throws Exception {
        CreateExpenseRequest createBody = new CreateExpenseRequest(
                BigDecimal.valueOf(100.00),
                "Test Expense",
                LocalDateTime.now().minusHours(1),
                null,
                ECurrency.BRL,
                EPaymentMethod.BANK_TRANSFER
        );

        mockMvc.perform(
                post("/api/expenses")
                        .with(user(mockUser))
                        .content(objectMapper.writeValueAsString(createBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when currency is null")
    @WithMockUser(username = "test@test.com")
    void createExpense15() throws Exception {
        CreateExpenseRequest createBody = new CreateExpenseRequest(
                BigDecimal.valueOf(100.00),
                "Test Expense",
                LocalDateTime.now().minusHours(1),
                ECategory.FOOD,
                null,
                EPaymentMethod.BANK_TRANSFER
        );

        mockMvc.perform(
                post("/api/expenses")
                        .with(user(mockUser))
                        .content(objectMapper.writeValueAsString(createBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when payment method is null")
    @WithMockUser(username = "test@test.com")
    void createExpense16() throws Exception {
        CreateExpenseRequest createBody = new CreateExpenseRequest(
                BigDecimal.valueOf(100.00),
                "Test Expense",
                LocalDateTime.now().minusHours(1),
                ECategory.FOOD,
                ECurrency.BRL,
                null
        );

        mockMvc.perform(
                post("/api/expenses")
                        .with(user(mockUser))
                        .content(objectMapper.writeValueAsString(createBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when request body is malformed JSON")
    @WithMockUser(username = "test@test.com")
    void createExpense17() throws Exception {
        String malformedJson = "{\"amount\": \"invalid\", \"description\": 123}";

        mockMvc.perform(
                post("/api/expenses")
                        .with(user(mockUser))
                        .content(malformedJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when request body is empty")
    @WithMockUser(username = "test@test.com")
    void createExpense18() throws Exception {
        mockMvc.perform(
                post("/api/expenses")
                        .with(user(mockUser))
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get expenses with default pagination")
    @WithMockUser(username = "test@test.com")
    void getExpenses() throws Exception {
        ExpenseResponse expense1 = new ExpenseResponse(
                UUID.randomUUID(),
                BigDecimal.valueOf(100.00),
                "Test Expense 1",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusDays(1),
                ECategory.FOOD,
                ECurrency.BRL,
                EPaymentMethod.BANK_TRANSFER
        );

        ExpenseResponse expense2 = new ExpenseResponse(
                UUID.randomUUID(),
                BigDecimal.valueOf(50.00),
                "Test Expense 2",
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(2),
                ECategory.TRANSPORT,
                ECurrency.BRL,
                EPaymentMethod.CREDIT_CARD
        );

        Page<ExpenseResponse> mockPage = new PageImpl<>(
                List.of(expense1, expense2),
                PageRequest.of(0, 10),
                2
        );

        when(expenseService.findAll(eq(mockUser), eq(0), eq(10), any(ExpenseFilter.class)))
                .thenReturn(mockPage);

        mockMvc.perform(
                get("/api/expenses")
                        .with(user(mockUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @DisplayName("Should get expenses with custom pagination")
    @WithMockUser(username = "test@test.com")
    void getExpenses2() throws Exception {
        Page<ExpenseResponse> mockPage = new PageImpl<>(
                Collections.emptyList(),
                PageRequest.of(1, 5),
                0
        );

        when(expenseService.findAll(eq(mockUser), eq(1), eq(5), any(ExpenseFilter.class)))
                .thenReturn(mockPage);

        mockMvc.perform(
                get("/api/expenses")
                        .param("page", "1")
                        .param("size", "5")
                        .with(user(mockUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(1));
    }

    @Test
    @DisplayName("Should get expenses with filters")
    @WithMockUser(username = "test@test.com")
    void getExpenses3() throws Exception {
        Page<ExpenseResponse> mockPage = new PageImpl<>(
                Collections.emptyList(),
                PageRequest.of(0, 10),
                0
        );

        when(expenseService.findAll(eq(mockUser), eq(0), eq(10), any(ExpenseFilter.class)))
                .thenReturn(mockPage);

        mockMvc.perform(
                get("/api/expenses")
                        .param("category", "FOOD")
                        .param("currency", "BRL")
                        .param("paymentMethod", "CREDIT_CARD")
                        .with(user(mockUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should update expense successfully")
    @WithMockUser(username = "test@test.com")
    void updateExpense() throws Exception {
        UUID expenseId = UUID.randomUUID();
        UpdateExpenseRequest updateRequest = new UpdateExpenseRequest(
                BigDecimal.valueOf(150.00),
                "Updated Expense",
                LocalDateTime.now().minusHours(2),
                ECategory.HEALTH,
                ECurrency.BRL,
                EPaymentMethod.DEBIT_CARD
        );

        ExpenseResponse updatedExpense = new ExpenseResponse(
                expenseId,
                BigDecimal.valueOf(150.00),
                "Updated Expense",
                LocalDateTime.now().minusHours(2),
                LocalDateTime.now().minusDays(1),
                ECategory.HEALTH,
                ECurrency.BRL,
                EPaymentMethod.DEBIT_CARD
        );

        when(expenseService.update(eq(expenseId), eq(updateRequest), eq(mockUser)))
                .thenReturn(updatedExpense);

        mockMvc.perform(
                put("/api/expenses/{id}", expenseId)
                        .with(user(mockUser))
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expenseId.toString()))
                .andExpect(jsonPath("$.amount").value(150.00))
                .andExpect(jsonPath("$.description").value("Updated Expense"))
                .andExpect(jsonPath("$.category").value("HEALTH"))
                .andExpect(jsonPath("$.paymentMethod").value("DEBIT_CARD"));
    }

    @Test
    @DisplayName("Should return 400 when update amount is negative")
    @WithMockUser(username = "test@test.com")
    void updateExpense2() throws Exception {
        UUID expenseId = UUID.randomUUID();
        UpdateExpenseRequest updateRequest = new UpdateExpenseRequest(
                BigDecimal.valueOf(-50.00),
                "Updated Expense",
                LocalDateTime.now().minusHours(2),
                ECategory.HEALTH,
                ECurrency.BRL,
                EPaymentMethod.DEBIT_CARD
        );

        mockMvc.perform(
                put("/api/expenses/{id}", expenseId)
                        .with(user(mockUser))
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when update description is too short")
    @WithMockUser(username = "test@test.com")
    void updateExpense3() throws Exception {
        UUID expenseId = UUID.randomUUID();
        UpdateExpenseRequest updateRequest = new UpdateExpenseRequest(
                BigDecimal.valueOf(150.00),
                "Hi",
                LocalDateTime.now().minusHours(2),
                ECategory.HEALTH,
                ECurrency.BRL,
                EPaymentMethod.DEBIT_CARD
        );

        mockMvc.perform(
                put("/api/expenses/{id}", expenseId)
                        .with(user(mockUser))
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when update expense date is in future")
    @WithMockUser(username = "test@test.com")
    void updateExpense4() throws Exception {
        UUID expenseId = UUID.randomUUID();
        UpdateExpenseRequest updateRequest = new UpdateExpenseRequest(
                BigDecimal.valueOf(150.00),
                "Updated Expense",
                LocalDateTime.now().plusDays(1),
                ECategory.HEALTH,
                ECurrency.BRL,
                EPaymentMethod.DEBIT_CARD
        );

        mockMvc.perform(
                put("/api/expenses/{id}", expenseId)
                        .with(user(mockUser))
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when update request body is empty")
    @WithMockUser(username = "test@test.com")
    void updateExpense5() throws Exception {
        UUID expenseId = UUID.randomUUID();

        mockMvc.perform(
                put("/api/expenses/{id}", expenseId)
                        .with(user(mockUser))
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should delete expense successfully")
    @WithMockUser(username = "test@test.com")
    void deleteExpense() throws Exception {
        UUID expenseId = UUID.randomUUID();

        doNothing().when(expenseService).delete(eq(expenseId), eq(mockUser));

        mockMvc.perform(
                delete("/api/expenses/{id}", expenseId)
                        .with(user(mockUser)))
                .andExpect(status().isNoContent());

        verify(expenseService).delete(eq(expenseId), eq(mockUser));
    }

    @Test
    @DisplayName("Should handle invalid UUID format")
    @WithMockUser(username = "test@test.com")
    void deleteExpense2() throws Exception {
        mockMvc.perform(
                delete("/api/expenses/{id}", "invalid-uuid")
                        .with(user(mockUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get expense summary without date filters")
    @WithMockUser(username = "test@test.com")
    void getSummary() throws Exception {
        ExpenseSummaryResponse mockSummary = new ExpenseSummaryResponse(
                null,
                null,
                Collections.emptyList(),
                Collections.emptyList(),
                new PaymentMethodSummary(EPaymentMethod.CREDIT_CARD, 5L),
                BigDecimal.valueOf(500.00),
                10
        );

        when(expenseService.getExpenseSummary(eq(mockUser), eq(null), eq(null)))
                .thenReturn(mockSummary);

        mockMvc.perform(
                get("/api/expenses/summary")
                        .with(user(mockUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(500.00))
                .andExpect(jsonPath("$.count").value(10))
                .andExpect(jsonPath("$.mostUsedPaymentMethod.method").value("CREDIT_CARD"))
                .andExpect(jsonPath("$.mostUsedPaymentMethod.usageCount").value(5));
    }

    @Test
    @DisplayName("Should get expense summary with date filters")
    @WithMockUser(username = "test@test.com")
    void getSummary2() throws Exception {
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now();

        ExpenseSummaryResponse mockSummary = new ExpenseSummaryResponse(
                startDate,
                endDate,
                Collections.emptyList(),
                Collections.emptyList(),
                new PaymentMethodSummary(EPaymentMethod.DEBIT_CARD, 3L),
                BigDecimal.valueOf(250.00),
                5
        );

        when(expenseService.getExpenseSummary(eq(mockUser), eq(startDate), eq(endDate)))
                .thenReturn(mockSummary);

        mockMvc.perform(
                get("/api/expenses/summary")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .with(user(mockUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate").value(startDate.toString()))
                .andExpect(jsonPath("$.endDate").value(endDate.toString()))
                .andExpect(jsonPath("$.total").value(250.00))
                .andExpect(jsonPath("$.count").value(5))
                .andExpect(jsonPath("$.mostUsedPaymentMethod.method").value("DEBIT_CARD"));
    }

    @Test
    @DisplayName("Should get expense summary with only start date")
    @WithMockUser(username = "test@test.com")
    void getSummary3() throws Exception {
        LocalDate startDate = LocalDate.now().minusDays(7);

        ExpenseSummaryResponse mockSummary = new ExpenseSummaryResponse(
                startDate,
                null,
                Collections.emptyList(),
                Collections.emptyList(),
                new PaymentMethodSummary(EPaymentMethod.CASH, 2L),
                BigDecimal.valueOf(100.00),
                3
        );

        when(expenseService.getExpenseSummary(eq(mockUser), eq(startDate), eq(null)))
                .thenReturn(mockSummary);

        mockMvc.perform(
                get("/api/expenses/summary")
                        .param("startDate", startDate.toString())
                        .with(user(mockUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate").value(startDate.toString()))
                .andExpect(jsonPath("$.endDate").doesNotExist())
                .andExpect(jsonPath("$.total").value(100.00))
                .andExpect(jsonPath("$.count").value(3));
    }

    @Test
    @DisplayName("Should get expense summary with only end date")
    @WithMockUser(username = "test@test.com")
    void getSummary4() throws Exception {
        LocalDate endDate = LocalDate.now();

        ExpenseSummaryResponse mockSummary = new ExpenseSummaryResponse(
                null,
                endDate,
                Collections.emptyList(),
                Collections.emptyList(),
                new PaymentMethodSummary(EPaymentMethod.PIX, 4L),
                BigDecimal.valueOf(300.00),
                7
        );

        when(expenseService.getExpenseSummary(eq(mockUser), eq(null), eq(endDate)))
                .thenReturn(mockSummary);

        mockMvc.perform(
                get("/api/expenses/summary")
                        .param("endDate", endDate.toString())
                        .with(user(mockUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate").doesNotExist())
                .andExpect(jsonPath("$.endDate").value(endDate.toString()))
                .andExpect(jsonPath("$.total").value(300.00))
                .andExpect(jsonPath("$.count").value(7));
    }

    @Test
    @DisplayName("Should handle invalid date format in summary")
    @WithMockUser(username = "test@test.com")
    void getSummary5() throws Exception {
        mockMvc.perform(
                get("/api/expenses/summary")
                        .param("startDate", "invalid-date")
                        .with(user(mockUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

}
