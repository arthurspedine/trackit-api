package com.spedine.trackit.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Expense Domain Entity Tests")
class ExpenseTest {

    User validUser;
    BigDecimal validAmount;
    String validDescription;
    LocalDateTime validExpenseDate;
    ECategory validCategory;
    ECurrency validCurrency;
    EPaymentMethod validPaymentMethod;

    @BeforeEach
    void setUp() {
        validUser = new User();
        validUser.setName("Test User");
        validUser.setEmail("test@example.com");
        validUser.setPassword("password");

        validAmount = new BigDecimal("100.50");
        validDescription = "Valid expense description";
        validExpenseDate = LocalDateTime.now().minusHours(1);
        validCategory = ECategory.FOOD;
        validCurrency = ECurrency.BRL;
        validPaymentMethod = EPaymentMethod.CREDIT_CARD;
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create expense with all valid parameters")
        void shouldCreateExpenseWithValidParameters() {
            Expense expense = new Expense(validAmount, validDescription, validExpenseDate,
                    validCategory, validCurrency, validPaymentMethod, validUser);

            assertNotNull(expense.getId());
            assertNotNull(expense.getCreatedAt());
            assertEquals(validAmount, expense.getAmount());
            assertEquals(validDescription, expense.getDescription());
            assertEquals(validExpenseDate, expense.getExpenseDate());
            assertEquals(validCategory, expense.getCategory());
            assertEquals(validCurrency, expense.getCurrency());
            assertEquals(validPaymentMethod, expense.getPaymentMethod());
            assertEquals(validUser, expense.getUser());
        }

        @Test
        @DisplayName("Should create expense with provided ID and createdAt")
        void shouldCreateExpenseWithProvidedIdAndCreatedAt() {
            UUID providedId = UUID.randomUUID();
            LocalDateTime providedCreatedAt = LocalDateTime.now().minusDays(1);

            Expense expense = new Expense(providedId, providedCreatedAt, validAmount, validDescription,
                    validExpenseDate, validCategory, validCurrency, validPaymentMethod, validUser);

            assertEquals(providedId, expense.getId());
            assertEquals(providedCreatedAt, expense.getCreatedAt());
        }

        @Test
        @DisplayName("Should generate ID and createdAt when null provided")
        void shouldGenerateIdAndCreatedAtWhenNull() {
            Expense expense = new Expense(null, null, validAmount, validDescription,
                    validExpenseDate, validCategory, validCurrency, validPaymentMethod, validUser);

            assertNotNull(expense.getId());
            assertNotNull(expense.getCreatedAt());
            assertTrue(expense.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        }
    }

    @Nested
    @DisplayName("Amount Validation Tests")
    class AmountValidationTests {

        @Test
        @DisplayName("Should accept positive amount")
        void shouldAcceptPositiveAmount() {
            BigDecimal positiveAmount = new BigDecimal("50.25");

            assertDoesNotThrow(() -> new Expense(positiveAmount, validDescription, validExpenseDate,
                    validCategory, validCurrency, validPaymentMethod, validUser));
        }

        @ParameterizedTest
        @DisplayName("Should reject null or non-positive amounts")
        @NullSource
        @ValueSource(strings = {"0", "-1", "-100.50"})
        void shouldRejectInvalidAmounts(String amountStr) {
            BigDecimal amount = amountStr != null ? new BigDecimal(amountStr) : null;

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> new Expense(amount, validDescription, validExpenseDate,
                            validCategory, validCurrency, validPaymentMethod, validUser));
            assertEquals("Amount must be positive", exception.getMessage());
        }

        @Test
        @DisplayName("Should update amount when valid")
        void shouldUpdateAmountWhenValid() {
            Expense expense = new Expense(validAmount, validDescription, validExpenseDate,
                    validCategory, validCurrency, validPaymentMethod, validUser);
            BigDecimal newAmount = new BigDecimal("200.75");

            expense.setAmount(newAmount);

            assertEquals(newAmount, expense.getAmount());
        }

        @Test
        @DisplayName("Should reject setting negative amount after creation")
        void shouldRejectSettingNegativeAmountAfterCreation() {
            Expense expense = new Expense(validAmount, validDescription, validExpenseDate,
                    validCategory, validCurrency, validPaymentMethod, validUser);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> expense.setAmount(new BigDecimal("-10")));
            assertEquals("Amount must be positive", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Description Validation Tests")
    class DescriptionValidationTests {

        @Test
        @DisplayName("Should accept valid description")
        void shouldAcceptValidDescription() {
            String validDesc = "Valid description";

            assertDoesNotThrow(() -> new Expense(validAmount, validDesc, validExpenseDate,
                    validCategory, validCurrency, validPaymentMethod, validUser));
        }

        @ParameterizedTest
        @DisplayName("Should reject invalid descriptions")
        @MethodSource("invalidDescriptions")
        void shouldRejectInvalidDescriptions(String description) {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> new Expense(validAmount, description, validExpenseDate,
                            validCategory, validCurrency, validPaymentMethod, validUser));
            assertEquals("Description must be between 3 and 255 characters", exception.getMessage());
        }

        static Stream<String> invalidDescriptions() {
            return Stream.of(
                    null,
                    "",
                    "  ",
                    "ab",
                    "a".repeat(256)
            );
        }


        @Test
        @DisplayName("Should accept description with exactly 3 characters")
        void shouldAcceptMinimumLengthDescription() {
            String minDescription = "abc";

            assertDoesNotThrow(() -> new Expense(validAmount, minDescription, validExpenseDate,
                    validCategory, validCurrency, validPaymentMethod, validUser));
        }

        @Test
        @DisplayName("Should accept description with exactly 255 characters")
        void shouldAcceptMaximumLengthDescription() {
            String maxDescription = "a".repeat(255);

            assertDoesNotThrow(() -> new Expense(validAmount, maxDescription, validExpenseDate,
                    validCategory, validCurrency, validPaymentMethod, validUser));
        }

        @Test
        @DisplayName("Should trim description before validation")
        void shouldTrimDescriptionBeforeValidation() {
            String descriptionWithSpaces = "  ab  ";

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> new Expense(validAmount, descriptionWithSpaces, validExpenseDate,
                            validCategory, validCurrency, validPaymentMethod, validUser));
            assertEquals("Description must be between 3 and 255 characters", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Expense Date Validation Tests")
    class ExpenseDateValidationTests {

        @Test
        @DisplayName("Should accept past expense date")
        void shouldAcceptPastExpenseDate() {
            LocalDateTime pastDate = LocalDateTime.now().minusDays(1);

            assertDoesNotThrow(() -> new Expense(validAmount, validDescription, pastDate,
                    validCategory, validCurrency, validPaymentMethod, validUser));
        }

        @Test
        @DisplayName("Should accept current time as expense date")
        void shouldAcceptCurrentTimeAsExpenseDate() {
            LocalDateTime now = LocalDateTime.now();

            assertDoesNotThrow(() -> new Expense(validAmount, validDescription, now,
                    validCategory, validCurrency, validPaymentMethod, validUser));
        }

        @Test
        @DisplayName("Should reject null expense date")
        void shouldRejectNullExpenseDate() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> new Expense(validAmount, validDescription, null,
                            validCategory, validCurrency, validPaymentMethod, validUser));
            assertEquals("Expense date must be in the past or present", exception.getMessage());
        }

        @Test
        @DisplayName("Should reject future expense date")
        void shouldRejectFutureExpenseDate() {
            LocalDateTime futureDate = LocalDateTime.now().plusDays(1);

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> new Expense(validAmount, validDescription, futureDate,
                            validCategory, validCurrency, validPaymentMethod, validUser));
            assertEquals("Expense date must be in the past or present", exception.getMessage());
        }

        @Test
        @DisplayName("Should update expense date when valid")
        void shouldUpdateExpenseDateWhenValid() {
            Expense expense = new Expense(validAmount, validDescription, validExpenseDate,
                    validCategory, validCurrency, validPaymentMethod, validUser);
            LocalDateTime newDate = LocalDateTime.now().minusHours(2);

            expense.setExpenseDate(newDate);

            assertEquals(newDate, expense.getExpenseDate());
        }
    }

    @Nested
    @DisplayName("Category Validation Tests")
    class CategoryValidationTests {

        @ParameterizedTest
        @DisplayName("Should accept all valid categories")
        @EnumSource(ECategory.class)
        void shouldAcceptAllValidCategories(ECategory category) {
            assertDoesNotThrow(() -> new Expense(validAmount, validDescription, validExpenseDate,
                    category, validCurrency, validPaymentMethod, validUser));
        }

        @Test
        @DisplayName("Should reject null category")
        void shouldRejectNullCategory() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> new Expense(validAmount, validDescription, validExpenseDate,
                            null, validCurrency, validPaymentMethod, validUser));
            assertEquals("Category cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should update category when valid")
        void shouldUpdateCategoryWhenValid() {
            Expense expense = new Expense(validAmount, validDescription, validExpenseDate,
                    validCategory, validCurrency, validPaymentMethod, validUser);
            ECategory newCategory = ECategory.TRANSPORT;

            expense.setCategory(newCategory);

            assertEquals(newCategory, expense.getCategory());
        }
    }

    @Nested
    @DisplayName("Currency Validation Tests")
    class CurrencyValidationTests {

        @ParameterizedTest
        @DisplayName("Should accept all valid currencies")
        @EnumSource(ECurrency.class)
        void shouldAcceptAllValidCurrencies(ECurrency currency) {
            assertDoesNotThrow(() -> new Expense(validAmount, validDescription, validExpenseDate,
                    validCategory, currency, validPaymentMethod, validUser));
        }

        @Test
        @DisplayName("Should reject null currency")
        void shouldRejectNullCurrency() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> new Expense(validAmount, validDescription, validExpenseDate,
                            validCategory, null, validPaymentMethod, validUser));
            assertEquals("Currency cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should update currency when valid")
        void shouldUpdateCurrencyWhenValid() {
            Expense expense = new Expense(validAmount, validDescription, validExpenseDate,
                    validCategory, validCurrency, validPaymentMethod, validUser);
            ECurrency newCurrency = ECurrency.USD;

            expense.setCurrency(newCurrency);

            assertEquals(newCurrency, expense.getCurrency());
        }
    }

    @Nested
    @DisplayName("Payment Method Validation Tests")
    class PaymentMethodValidationTests {

        @ParameterizedTest
        @DisplayName("Should accept all valid payment methods")
        @EnumSource(EPaymentMethod.class)
        void shouldAcceptAllValidPaymentMethods(EPaymentMethod paymentMethod) {
            assertDoesNotThrow(() -> new Expense(validAmount, validDescription, validExpenseDate,
                    validCategory, validCurrency, paymentMethod, validUser));
        }

        @Test
        @DisplayName("Should reject null payment method")
        void shouldRejectNullPaymentMethod() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> new Expense(validAmount, validDescription, validExpenseDate,
                            validCategory, validCurrency, null, validUser));
            assertEquals("Payment method cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should update payment method when valid")
        void shouldUpdatePaymentMethodWhenValid() {
            Expense expense = new Expense(validAmount, validDescription, validExpenseDate,
                    validCategory, validCurrency, validPaymentMethod, validUser);
            EPaymentMethod newPaymentMethod = EPaymentMethod.PIX;

            expense.setPaymentMethod(newPaymentMethod);

            assertEquals(newPaymentMethod, expense.getPaymentMethod());
        }
    }

    @Nested
    @DisplayName("User Validation Tests")
    class UserValidationTests {

        @Test
        @DisplayName("Should accept valid user")
        void shouldAcceptValidUser() {
            assertDoesNotThrow(() -> new Expense(validAmount, validDescription, validExpenseDate,
                    validCategory, validCurrency, validPaymentMethod, validUser));
        }

        @Test
        @DisplayName("Should reject null user")
        void shouldRejectNullUser() {
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> new Expense(validAmount, validDescription, validExpenseDate,
                            validCategory, validCurrency, validPaymentMethod, null));
            assertEquals("User cannot be null", exception.getMessage());
        }

        @Test
        @DisplayName("Should update user when valid")
        void shouldUpdateUserWhenValid() {
            Expense expense = new Expense(validAmount, validDescription, validExpenseDate,
                    validCategory, validCurrency, validPaymentMethod, validUser);
            User newUser = new User();
            newUser.setName("New User");
            newUser.setEmail("new@example.com");

            expense.setUser(newUser);

            assertEquals(newUser, expense.getUser());
        }
    }
}