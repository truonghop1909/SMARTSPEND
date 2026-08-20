package com.smartspend.dashboard.dto.response;

import java.util.List;

public record DashboardResponse(

        DashboardSummaryResponse summary,

        List<CategoryStatisticResponse> expenseByCategory,

        PeriodComparisonResponse comparison

) {
}