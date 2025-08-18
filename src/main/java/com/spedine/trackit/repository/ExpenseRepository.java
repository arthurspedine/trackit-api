package com.spedine.trackit.repository;

import com.spedine.trackit.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, Long>, JpaSpecificationExecutor<Expense> {
    Expense findByIdAndUser_Id(UUID id, UUID userId);
}
