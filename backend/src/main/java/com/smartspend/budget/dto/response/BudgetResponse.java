package com.smartspend.budget.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BudgetResponse(

        Long id,

        Long categoryId,

        String categoryName,

        int year,

        int month,

        BigDecimal amount,

        BigDecimal spentAmount,

        BigDecimal remainingAmount,

        BigDecimal usagePercentage,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}