package com.smartspend.budget.repository;

import com.smartspend.budget.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository
        extends JpaRepository<Budget, Long> {

    Optional<Budget> findByIdAndUserId(
            Long budgetId,
            Long userId
    );

    Optional<Budget>
    findByUserIdAndCategoryIdAndBudgetYearAndBudgetMonth(
            Long userId,
            Long categoryId,
            int budgetYear,
            int budgetMonth
    );

    boolean existsByUserIdAndCategoryIdAndBudgetYearAndBudgetMonth(
            Long userId,
            Long categoryId,
            int budgetYear,
            int budgetMonth
    );

    List<Budget>
    findAllByUserIdOrderByBudgetYearDescBudgetMonthDesc(
            Long userId
    );

    List<Budget>
    findAllByUserIdAndBudgetYearAndBudgetMonthOrderByCategoryNameAsc(
            Long userId,
            int budgetYear,
            int budgetMonth
    );
}