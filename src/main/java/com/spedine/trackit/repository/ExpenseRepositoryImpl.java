package com.spedine.trackit.repository;

import com.spedine.trackit.dto.ExpenseFilter;
import com.spedine.trackit.dto.PaymentMethodSummary;
import com.spedine.trackit.model.Expense;
import com.spedine.trackit.model.ExpenseEntity;
import com.spedine.trackit.model.User;
import com.spedine.trackit.projection.ExpenseCategoryProjection;
import com.spedine.trackit.projection.ExpenseCountAndTotalProjection;
import com.spedine.trackit.projection.ExpenseCurrencyProjection;
import com.spedine.trackit.specification.ExpenseSpecification;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
class ExpenseRepositoryImpl implements ExpenseRepository {

    private final ExpenseJpaRepository jpaRepository;

    public ExpenseRepositoryImpl(ExpenseJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Expense save(Expense expense) {
        ExpenseEntity expenseEntity = ExpenseEntity.fromDomain(expense);
        return jpaRepository.save(expenseEntity).toDomain();
    }

    @Override
    public Expense findByIdAndUser_Id(UUID id, UUID userId) {
        return jpaRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new EntityNotFoundException("Expense not found for id: " + id))
                .toDomain();
    }

    @Override
    public Page<Expense> findAll(User user, Pageable pageable, ExpenseFilter filter) {
        Specification<ExpenseEntity> specification = ExpenseSpecification.withFilters(user.getId(), filter);
        return jpaRepository.findAll(specification, pageable).map(ExpenseEntity::toDomain);
    }

    @Override
    public void delete(Expense expense) {
        jpaRepository.delete(ExpenseEntity.fromDomain(expense));
    }

    @Override
    public ExpenseCountAndTotalProjection countAndSumTotalAmount(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaRepository.countAndSumTotalAmount(userId, startDate, endDate);
    }

    @Override
    public List<ExpenseCategoryProjection> groupByExpenseCategoryAndSumAmount(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaRepository.groupByExpenseCategoryAndSumAmount(userId, startDate, endDate);
    }

    @Override
    public PaymentMethodSummary getMostUsedPaymentMethod(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaRepository.getMostUsedPaymentMethod(userId, startDate, endDate);
    }

    @Override
    public List<ExpenseCurrencyProjection> groupByCurrencyAndSumAmount(UUID userId, LocalDateTime startDate, LocalDateTime endDate) {
        return jpaRepository.groupByCurrencyAndSumAmount(userId, startDate, endDate);
    }
}
