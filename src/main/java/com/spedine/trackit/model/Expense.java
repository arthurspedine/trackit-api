package com.spedine.trackit.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Expense {

    private UUID id;
    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime expenseDate;
    private ECategory category;
    private ECurrency currency;
    private EPaymentMethod paymentMethod;
    private User user;

    private Expense() {
    }

    public Expense(BigDecimal amount, String description, LocalDateTime expenseDate,
                   ECategory category, ECurrency currency, EPaymentMethod paymentMethod, User user) {
        this(null, null, amount, description, expenseDate, category, currency, paymentMethod, user);
    }

    public Expense(UUID id, LocalDateTime createdAt, BigDecimal amount, String description,
                   LocalDateTime expenseDate, ECategory category, ECurrency currency,
                   EPaymentMethod paymentMethod, User user) {
        this.id = (id != null) ? id : UUID.randomUUID();
        this.createdAt = (createdAt != null) ? createdAt : LocalDateTime.now();

        setAmount(amount);
        setDescription(description);
        setExpenseDate(expenseDate);
        setCategory(category);
        setCurrency(currency);
        setPaymentMethod(paymentMethod);
        setUser(user);
    }

    public void setAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.amount = amount;
    }

    public void setDescription(String description) {
        if (description == null || description.trim().length() < 3 || description.length() > 255) {
            throw new IllegalArgumentException("Description must be between 3 and 255 characters");
        }
        this.description = description;
    }

    public void setExpenseDate(LocalDateTime expenseDate) {
        if (expenseDate == null || expenseDate.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Expense date must be in the past or present");
        }
        this.expenseDate = expenseDate;
    }

    public void setCategory(ECategory category) {
        if (category == null) throw new IllegalArgumentException("Category cannot be null");
        this.category = category;
    }

    public void setCurrency(ECurrency currency) {
        if (currency == null) throw new IllegalArgumentException("Currency cannot be null");
        this.currency = currency;
    }

    public void setPaymentMethod(EPaymentMethod paymentMethod) {
        if (paymentMethod == null) throw new IllegalArgumentException("Payment method cannot be null");
        this.paymentMethod = paymentMethod;
    }

    public void setUser(User user) {
        if (user == null) throw new IllegalArgumentException("User cannot be null");
        this.user = user;
    }

    public UUID getId() { return id; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getExpenseDate() { return expenseDate; }
    public ECategory getCategory() { return category; }
    public ECurrency getCurrency() { return currency; }
    public EPaymentMethod getPaymentMethod() { return paymentMethod; }
    public User getUser() { return user; }
}
