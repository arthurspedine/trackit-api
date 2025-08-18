package com.spedine.trackit.controller;

import com.spedine.trackit.dto.CreateExpenseRequest;
import com.spedine.trackit.dto.ExpenseFilter;
import com.spedine.trackit.dto.ExpenseResponse;
import com.spedine.trackit.dto.MessageResponse;
import com.spedine.trackit.model.User;
import com.spedine.trackit.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> createExpense(@RequestBody @Valid CreateExpenseRequest body) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        expenseService.save(body, user);
        return ResponseEntity.ok(new MessageResponse("Expense created successfully"));
    }

    @GetMapping
    public ResponseEntity<Page<ExpenseResponse>> getExpenses(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @ModelAttribute ExpenseFilter filter
            ){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(expenseService.findAll(user, page, size, filter));
    }
}
