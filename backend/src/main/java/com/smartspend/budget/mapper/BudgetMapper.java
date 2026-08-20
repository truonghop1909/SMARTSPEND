package com.smartspend.budget.mapper;

import com.smartspend.auth.entity.User;
import com.smartspend.budget.dto.request.CreateBudgetRequest;
import com.smartspend.budget.dto.response.BudgetResponse;
import com.smartspend.budget.entity.Budget;
import com.smartspend.category.entity.Category;
import org.mapstruct.Mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface BudgetMapper {

    default Budget toEntity(
            CreateBudgetRequest request,
            User user,
            Category category
    ) {
        Objects.requireNonNull(request);
        Objects.requireNonNull(user);
        Objects.requireNonNull(category);

        return new Budget(
                user,
                category,
                request.year(),
                request.month(),
                request.amount()
        );
    }

    default BudgetResponse toResponse(
            Budget budget,
            BigDecimal spentAmount
    ) {
        BigDecimal spent =
                spentAmount != null
                        ? spentAmount
                        : BigDecimal.ZERO;

        BigDecimal remaining =
                budget.getAmount()
                        .subtract(spent);

        BigDecimal percentage =
                calculatePercentage(
                        spent,
                        budget.getAmount()
                );

        return new BudgetResponse(
                budget.getId(),
                budget.getCategory().getId(),
                budget.getCategory().getName(),
                budget.getBudgetYear(),
                budget.getBudgetMonth(),
                budget.getAmount(),
                spent,
                remaining,
                percentage,
                budget.getCreatedAt(),
                budget.getUpdatedAt()
        );
    }

    private BigDecimal calculatePercentage(
            BigDecimal spent,
            BigDecimal budgetAmount
    ) {
        if (budgetAmount == null
                || budgetAmount.compareTo(
                        BigDecimal.ZERO
                ) <= 0) {

            return BigDecimal.ZERO;
        }

        return spent
                .multiply(
                        BigDecimal.valueOf(100)
                )
                .divide(
                        budgetAmount,
                        2,
                        RoundingMode.HALF_UP
                );
    }
}