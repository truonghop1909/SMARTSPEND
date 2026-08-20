package com.smartspend.dashboard.controller;

import com.smartspend.dashboard.dto.response.DashboardResponse;
import com.smartspend.dashboard.dto.response.TrendResponse;
import com.smartspend.dashboard.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService
    ) {
        this.dashboardService =
                dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponse> getDashboard(
            @RequestParam(required = false)
            Integer year,

            @RequestParam(required = false)
            Integer month
    ) {
        YearMonth current =
                YearMonth.now();

        int resolvedYear =
                year != null
                        ? year
                        : current.getYear();

        int resolvedMonth =
                month != null
                        ? month
                        : current.getMonthValue();

        return ResponseEntity.ok(
                dashboardService.getDashboard(
                        resolvedYear,
                        resolvedMonth
                )
        );
    }

    @GetMapping("/trend")
    public ResponseEntity<List<TrendResponse>> getTrend(
            @RequestParam(
                    defaultValue = "6"
            )
            int months
    ) {
        return ResponseEntity.ok(
                dashboardService
                        .getMonthlyTrend(
                                months
                        )
        );
    }
}