package com.spedine.trackit.repository;

import com.spedine.trackit.dto.PaymentMethodSummary;
import com.spedine.trackit.model.ExpenseEntity;
import com.spedine.trackit.projection.ExpenseCategoryProjection;
import com.spedine.trackit.projection.ExpenseCountAndTotalProjection;
import com.spedine.trackit.projection.ExpenseCurrencyProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

interface ExpenseJpaRepository extends JpaRepository<ExpenseEntity, Long>, JpaSpecificationExecutor<ExpenseEntity> {
    Optional<ExpenseEntity> findByIdAndUser_Id(UUID id, UUID userId);

    @Query(value = """
                SELECT COUNT(e) as count, COALESCE(SUM(e.amount), 0) AS totalExpense
                FROM ExpenseEntity e
                WHERE e.user.id = :userId
                  AND e.expenseDate BETWEEN :startDate AND :endDate
            """)
    ExpenseCountAndTotalProjection countAndSumTotalAmount(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query(value = """
                SELECT
                       e.category AS category,
                       SUM(e.amount) AS total,
                       COUNT(e) AS count
                FROM ExpenseEntity e
                WHERE e.user.id = :userId
                    AND e.expenseDate BETWEEN :startDate AND :endDate
                GROUP BY e.category
                ORDER BY SUM(e.amount) DESC, e.category
            """)
    List<ExpenseCategoryProjection> groupByExpenseCategoryAndSumAmount(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query(value = """
                SELECT new com.spedine.trackit.dto.PaymentMethodSummary(
                       e.paymentMethod AS method,
                       COUNT(e) AS count
                )
                FROM ExpenseEntity e
                WHERE e.user.id = :userId
                    AND e.expenseDate BETWEEN :startDate AND :endDate
                GROUP BY e.paymentMethod
                ORDER BY COUNT(e) DESC, e.paymentMethod
                LIMIT 1
            """)
    PaymentMethodSummary getMostUsedPaymentMethod(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query(value = """
                SELECT
                       e.currency AS currency,
                       SUM(e.amount) AS total
                FROM ExpenseEntity e
                WHERE e.user.id = :userId
                    AND e.expenseDate >= :startDate
                    AND e.expenseDate <= :endDate
                GROUP BY e.currency
                ORDER BY SUM(e.amount) DESC, e.currency
            """)
    List<ExpenseCurrencyProjection> groupByCurrencyAndSumAmount(
            @Param("userId") UUID userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
