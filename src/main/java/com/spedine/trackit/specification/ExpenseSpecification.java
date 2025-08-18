package com.spedine.trackit.specification;

import com.spedine.trackit.dto.ExpenseFilter;
import com.spedine.trackit.model.Expense;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExpenseSpecification {

    public static Specification<Expense> withFilters(UUID userId, ExpenseFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("user").get("id"), userId));

            // if startDate and/or endDate are provided, filter by that range
            if (filter.startDate() != null || filter.endDate() != null) {
                if (filter.startDate() != null && filter.endDate() != null) {
                    predicates.add(cb.between(root.get("expenseDate"), filter.startDate(), filter.endDate()));
                } else if (filter.startDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("expenseDate"), filter.startDate()));
                } else {
                    predicates.add(cb.lessThanOrEqualTo(root.get("expenseDate"), filter.endDate()));
                }
            } else if (filter.month() != null) {
                YearMonth ym = YearMonth.parse(filter.month());
                LocalDateTime start = ym.atDay(1).atStartOfDay();
                LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59, 999_999_999);
                predicates.add(cb.between(root.get("expenseDate"), start, end));
            }

            if (filter.category() != null) {
                predicates.add(cb.equal(root.get("category"), filter.category()));
            }
            if (filter.currency() != null) {
                predicates.add(cb.equal(root.get("currency"), filter.currency()));
            }
            if (filter.paymentMethod() != null) {
                predicates.add(cb.equal(root.get("paymentMethod"), filter.paymentMethod()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
