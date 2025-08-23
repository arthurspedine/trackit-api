package com.spedine.trackit.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "expenses")
public class ExpenseEntity {
    @Id
    private UUID id;

    private BigDecimal amount;

    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime expenseDate;

    @Enumerated(EnumType.STRING)
    private ECategory category;

    @Enumerated(EnumType.STRING)
    private ECurrency currency;

    @Enumerated(EnumType.STRING)
    private EPaymentMethod paymentMethod;

    @ManyToOne(optional = false)
    private UserEntity user;

    protected ExpenseEntity() {
    }

    public ExpenseEntity(UUID id, BigDecimal amount, String description, LocalDateTime createdAt,
                         LocalDateTime expenseDate, ECategory category, ECurrency currency,
                         EPaymentMethod paymentMethod, UserEntity user) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.createdAt = createdAt;
        this.expenseDate = expenseDate;
        this.category = category;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.user = user;
    }

    public static ExpenseEntity fromDomain(Expense expense, UserEntity user) {
        return new ExpenseEntity(
                expense.getId(),
                expense.getAmount(),
                expense.getDescription(),
                expense.getCreatedAt(),
                expense.getExpenseDate(),
                expense.getCategory(),
                expense.getCurrency(),
                expense.getPaymentMethod(),
                user
        );
    }

    public Expense toDomain() {
        return new Expense(id, createdAt, amount, description, expenseDate, category, currency, paymentMethod, user.toDomain());
    }

    public UUID getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }
}
