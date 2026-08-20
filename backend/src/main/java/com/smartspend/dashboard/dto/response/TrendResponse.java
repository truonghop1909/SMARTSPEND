package com.smartspend.dashboard.dto.response;

import java.math.BigDecimal;

public record TrendResponse(

        int year,

        int month,

        BigDecimal totalIncome,

        BigDecimal totalExpense,

        BigDecimal balance

) {
}