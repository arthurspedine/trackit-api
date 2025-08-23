package com.spedine.trackit.controller;

import com.spedine.trackit.dto.*;
import com.spedine.trackit.infra.util.AuthenticationUtil;
import com.spedine.trackit.model.User;
import com.spedine.trackit.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springdoc.core.annotations.ParameterObject;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/expenses")
@Tag(name = "Expenses", description = "Private endpoints for managing expenses. Requires Bearer JWT token.")
@SecurityRequirement(name = "bearerAuth")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final AuthenticationUtil authenticationUtil;

    public ExpenseController(ExpenseService expenseService, AuthenticationUtil authenticationUtil) {
        this.expenseService = expenseService;
        this.authenticationUtil = authenticationUtil;
    }

    @PostMapping
    @Operation(
            summary = "Create expense",
            description = "Create a new expense for the authenticated user."
    )
    public ResponseEntity<MessageResponse> createExpense(@RequestBody @Valid CreateExpenseRequest body) {
        User user = authenticationUtil.getCurrentUser();
        expenseService.save(body, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("Expense created successfully"));
    }

    @GetMapping
    @Operation(
        summary = "List expenses",
        description = "Get a paginated list of the user's expenses. Supports optional filters via query parameters."
    )
    public ResponseEntity<PageResponse<ExpenseResponse>> getExpenses(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @ParameterObject @ModelAttribute ExpenseFilter filter
    ) {
        User user = authenticationUtil.getCurrentUser();
        return ResponseEntity.ok(expenseService.findAll(user, page, size, filter));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get expense by ID",
            description = "Retrieve a single expense by its ID for the authenticated user."
    )
    public ResponseEntity<ExpenseResponse> getExpenseById(@PathVariable UUID id) {
        User user = authenticationUtil.getCurrentUser();
        ExpenseResponse expense = expenseService.findByUserAndId(id, user);
        return ResponseEntity.ok(expense);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update expense",
            description = "Update an existing expense by ID."
    )
    public ResponseEntity<ExpenseResponse> updateExpense(@PathVariable UUID id, @RequestBody @Valid UpdateExpenseRequest body) {
        User user = authenticationUtil.getCurrentUser();
        return ResponseEntity.ok(expenseService.update(id, body, user));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete expense",
            description = "Delete an expense by ID."
    )
    public ResponseEntity<MessageResponse> deleteExpense(@PathVariable UUID id) {
        User user = authenticationUtil.getCurrentUser();
        expenseService.delete(id, user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/summary")
    @Operation(
            summary = "Expense summary",
            description = "Get totals and breakdowns for a date range. If no dates are provided, returns overall summary."
    )
    public ResponseEntity<ExpenseSummaryResponse> getSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        User user = authenticationUtil.getCurrentUser();
        return ResponseEntity.ok(expenseService.getExpenseSummary(user, startDate, endDate));
    }
}
