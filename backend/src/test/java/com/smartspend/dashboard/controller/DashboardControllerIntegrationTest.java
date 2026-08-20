package com.smartspend.dashboard.controller;

import com.smartspend.dashboard.dto.response.CategoryStatisticResponse;
import com.smartspend.dashboard.dto.response.DashboardResponse;
import com.smartspend.dashboard.dto.response.DashboardSummaryResponse;
import com.smartspend.dashboard.dto.response.PeriodComparisonResponse;
import com.smartspend.dashboard.dto.response.TrendResponse;
import com.smartspend.dashboard.service.DashboardService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
@ActiveProfiles("test")
class DashboardControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    // =========================================================
    // SECURITY
    // =========================================================

    @Test
    void getDashboard_shouldReturnUnauthorized_withoutAuthentication()
            throws Exception {

        mockMvc.perform(
                        get("/api/dashboard")
                                .with(anonymous())
                )
                .andExpect(
                        status().isUnauthorized()
                );
    }

    // =========================================================
    // DASHBOARD
    // =========================================================

    @Test
    @WithMockUser
    void getDashboard_shouldReturnDashboard()
            throws Exception {

        DashboardResponse response =
                createDashboardResponse();

        when(
                dashboardService.getDashboard(
                        2026,
                        8
                )
        ).thenReturn(response);

        mockMvc.perform(
                        get("/api/dashboard")
                                .param(
                                        "year",
                                        "2026"
                                )
                                .param(
                                        "month",
                                        "8"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.summary.totalIncome"
                        ).value(20000000)
                )
                .andExpect(
                        jsonPath(
                                "$.summary.totalExpense"
                        ).value(12000000)
                )
                .andExpect(
                        jsonPath(
                                "$.summary.balance"
                        ).value(8000000)
                )
                .andExpect(
                        jsonPath(
                                "$.expenseByCategory[0].categoryId"
                        ).value(10)
                )
                .andExpect(
                        jsonPath(
                                "$.expenseByCategory[0].categoryName"
                        ).value("Ăn uống")
                )
                .andExpect(
                        jsonPath(
                                "$.expenseByCategory[0].percentage"
                        ).value(25.00)
                );

        verify(dashboardService)
                .getDashboard(
                        2026,
                        8
                );
    }

    // =========================================================
    // TREND
    // =========================================================

    @Test
    @WithMockUser
    void getTrend_shouldReturnMonthlyTrend()
            throws Exception {

        List<TrendResponse> response =
                List.of(
                        new TrendResponse(
                                2026,
                                7,
                                new BigDecimal(
                                        "20000000"
                                ),
                                new BigDecimal(
                                        "12000000"
                                ),
                                new BigDecimal(
                                        "8000000"
                                )
                        ),
                        new TrendResponse(
                                2026,
                                8,
                                new BigDecimal(
                                        "22000000"
                                ),
                                new BigDecimal(
                                        "15000000"
                                ),
                                new BigDecimal(
                                        "7000000"
                                )
                        )
                );

        when(
                dashboardService.getMonthlyTrend(
                        6
                )
        ).thenReturn(response);

        mockMvc.perform(
                        get(
                                "/api/dashboard/trend"
                        )
                                .param(
                                        "months",
                                        "6"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$[0].year"
                        ).value(2026)
                )
                .andExpect(
                        jsonPath(
                                "$[0].month"
                        ).value(7)
                )
                .andExpect(
                        jsonPath(
                                "$[0].totalIncome"
                        ).value(20000000)
                )
                .andExpect(
                        jsonPath(
                                "$[0].totalExpense"
                        ).value(12000000)
                )
                .andExpect(
                        jsonPath(
                                "$[0].balance"
                        ).value(8000000)
                )
                .andExpect(
                        jsonPath(
                                "$[1].month"
                        ).value(8)
                );

        verify(dashboardService)
                .getMonthlyTrend(
                        6
                );
    }

    // =========================================================
    // DEFAULT TREND MONTHS
    // =========================================================

    @Test
    @WithMockUser
    void getTrend_shouldUseSixMonthsByDefault()
            throws Exception {

        when(
                dashboardService.getMonthlyTrend(
                        6
                )
        ).thenReturn(
                List.of()
        );

        mockMvc.perform(
                        get(
                                "/api/dashboard/trend"
                        )
                )
                .andExpect(
                        status().isOk()
                );

        verify(dashboardService)
                .getMonthlyTrend(
                        6
                );
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private DashboardResponse createDashboardResponse() {
        DashboardSummaryResponse summary =
                new DashboardSummaryResponse(
                        new BigDecimal(
                                "20000000"
                        ),
                        new BigDecimal(
                                "12000000"
                        ),
                        new BigDecimal(
                                "8000000"
                        )
                );

        List<CategoryStatisticResponse> categories =
                List.of(
                        new CategoryStatisticResponse(
                                10L,
                                "Ăn uống",
                                new BigDecimal(
                                        "3000000"
                                ),
                                new BigDecimal(
                                        "25.00"
                                )
                        )
                );

        PeriodComparisonResponse comparison =
                new PeriodComparisonResponse(
                        new BigDecimal(
                                "20000000"
                        ),
                        new BigDecimal(
                                "18000000"
                        ),
                        new BigDecimal(
                                "11.11"
                        ),
                        new BigDecimal(
                                "12000000"
                        ),
                        new BigDecimal(
                                "10000000"
                        ),
                        new BigDecimal(
                                "20.00"
                        ),
                        new BigDecimal(
                                "8000000"
                        ),
                        new BigDecimal(
                                "8000000"
                        )
                );

        return new DashboardResponse(
                summary,
                categories,
                comparison
        );
    }
}