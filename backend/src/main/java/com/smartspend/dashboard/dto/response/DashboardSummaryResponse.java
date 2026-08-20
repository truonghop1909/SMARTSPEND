package com.smartspend.dashboard.dto.response;

import java.math.BigDecimal;

public record DashboardSummaryResponse(

        BigDecimal totalIncome,

        BigDecimal totalExpense,

        BigDecimal balance

) {
}