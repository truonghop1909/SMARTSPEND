package com.smartspend.dashboard.service;

import com.smartspend.dashboard.dto.response.DashboardResponse;
import com.smartspend.dashboard.dto.response.TrendResponse;

import java.util.List;

public interface DashboardService {

    DashboardResponse getDashboard(
            int year,
            int month
    );

    List<TrendResponse> getMonthlyTrend(
            int months
    );
}