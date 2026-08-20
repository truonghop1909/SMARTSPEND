package com.smartspend.budget.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateBudgetRequest(

        @NotNull(message = "Budget amount is required")
        @DecimalMin(
                value = "0.01",
                message = "Budget amount must be greater than 0"
        )
        BigDecimal amount

) {
}