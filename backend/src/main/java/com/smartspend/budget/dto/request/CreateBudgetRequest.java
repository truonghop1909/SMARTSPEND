package com.smartspend.budget.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateBudgetRequest(

        @NotNull(message = "Category is required")
        Long categoryId,

        @Min(
                value = 2000,
                message = "Budget year is invalid"
        )
        @Max(
                value = 2100,
                message = "Budget year is invalid"
        )
        int year,

        @Min(
                value = 1,
                message = "Budget month must be between 1 and 12"
        )
        @Max(
                value = 12,
                message = "Budget month must be between 1 and 12"
        )
        int month,

        @NotNull(message = "Budget amount is required")
        @DecimalMin(
                value = "0.01",
                message = "Budget amount must be greater than 0"
        )
        BigDecimal amount

) {
}