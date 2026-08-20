package com.smartspend.dashboard.projection;

import java.math.BigDecimal;

public interface MonthlyTrendProjection {

    Integer getYear();

    Integer getMonth();

    BigDecimal getTotalIncome();

    BigDecimal getTotalExpense();
}