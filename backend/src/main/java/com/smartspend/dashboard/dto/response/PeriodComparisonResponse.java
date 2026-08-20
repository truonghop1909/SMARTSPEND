package com.smartspend.dashboard.dto.response;

import java.math.BigDecimal;

public record PeriodComparisonResponse(

        BigDecimal currentIncome,

        BigDecimal previousIncome,

        BigDecimal incomeChangePercentage,

        BigDecimal currentExpense,

        BigDecimal previousExpense,

        BigDecimal expenseChangePercentage,

        BigDecimal currentBalance,

        BigDecimal previousBalance

) {
}