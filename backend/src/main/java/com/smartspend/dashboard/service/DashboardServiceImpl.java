package com.smartspend.dashboard.service;

import com.smartspend.common.exception.AppException;
import com.smartspend.common.exception.ErrorCode;
import com.smartspend.dashboard.cache.DashboardCacheService;
import com.smartspend.dashboard.dto.response.CategoryStatisticResponse;
import com.smartspend.dashboard.dto.response.DashboardResponse;
import com.smartspend.dashboard.dto.response.DashboardSummaryResponse;
import com.smartspend.dashboard.dto.response.PeriodComparisonResponse;
import com.smartspend.dashboard.dto.response.TrendResponse;
import com.smartspend.dashboard.projection.CategoryStatisticProjection;
import com.smartspend.dashboard.projection.MonthlyTrendProjection;
import com.smartspend.security.UserPrincipal;
import com.smartspend.transaction.entity.TransactionType;
import com.smartspend.transaction.repository.TransactionRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class DashboardServiceImpl
        implements DashboardService {

    private final TransactionRepository transactionRepository;
    private final DashboardCacheService dashboardCacheService;

    public DashboardServiceImpl(
            TransactionRepository transactionRepository,
            DashboardCacheService dashboardCacheService
    ) {
        this.transactionRepository =
                transactionRepository;

        this.dashboardCacheService =
                dashboardCacheService;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(
            int year,
            int month
    ) {
        validatePeriod(
                year,
                month
        );

        Long userId =
                getCurrentUserId();

        Object cached =
                dashboardCacheService.get(
                        userId,
                        year,
                        month
                );

        if (cached instanceof DashboardResponse dashboardResponse) {
            return dashboardResponse;
        }

        YearMonth period =
                YearMonth.of(
                        year,
                        month
                );

        LocalDate fromDate =
                period.atDay(1);

        LocalDate toDate =
                period.atEndOfMonth();

        BigDecimal totalIncome =
                safeAmount(
                        transactionRepository
                                .calculateTotalAmountByTypeAndPeriod(
                                        userId,
                                        TransactionType.INCOME,
                                        fromDate,
                                        toDate
                                )
                );

        BigDecimal totalExpense =
                safeAmount(
                        transactionRepository
                                .calculateTotalAmountByTypeAndPeriod(
                                        userId,
                                        TransactionType.EXPENSE,
                                        fromDate,
                                        toDate
                                )
                );

        DashboardSummaryResponse summary =
                new DashboardSummaryResponse(
                        totalIncome,
                        totalExpense,
                        totalIncome.subtract(
                                totalExpense
                        )
                );

        List<CategoryStatisticResponse> categoryStatistics =
                buildCategoryStatistics(
                        userId,
                        fromDate,
                        toDate,
                        totalExpense
                );

        PeriodComparisonResponse comparison =
                buildComparison(
                        userId,
                        period,
                        totalIncome,
                        totalExpense
                );

        DashboardResponse response =
                new DashboardResponse(
                        summary,
                        categoryStatistics,
                        comparison
                );

        dashboardCacheService.put(
                userId,
                year,
                month,
                response
        );

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrendResponse> getMonthlyTrend(
            int months
    ) {
        Long userId =
                getCurrentUserId();

        int resolvedMonths =
                Math.min(
                        Math.max(
                                months,
                                1
                        ),
                        24
                );

        YearMonth current =
                YearMonth.now();

        YearMonth start =
                current.minusMonths(
                        resolvedMonths - 1L
                );

        List<MonthlyTrendProjection> projections =
                transactionRepository
                        .findMonthlyTrend(
                                userId,
                                start.atDay(1),
                                current.atEndOfMonth()
                        );

        return projections
                .stream()
                .map(projection -> {
                    BigDecimal income =
                            safeAmount(
                                    projection.getTotalIncome()
                            );

                    BigDecimal expense =
                            safeAmount(
                                    projection.getTotalExpense()
                            );

                    return new TrendResponse(
                            projection.getYear(),
                            projection.getMonth(),
                            income,
                            expense,
                            income.subtract(
                                    expense
                            )
                    );
                })
                .toList();
    }

    private List<CategoryStatisticResponse>
    buildCategoryStatistics(
            Long userId,
            LocalDate fromDate,
            LocalDate toDate,
            BigDecimal totalExpense
    ) {
        List<CategoryStatisticProjection> projections =
                transactionRepository
                        .findStatisticsByCategory(
                                userId,
                                TransactionType.EXPENSE,
                                fromDate,
                                toDate
                        );

        return projections
                .stream()
                .map(projection -> {
                    BigDecimal amount =
                            safeAmount(
                                    projection.getAmount()
                            );

                    BigDecimal percentage =
                            calculatePercentage(
                                    amount,
                                    totalExpense
                            );

                    return new CategoryStatisticResponse(
                            projection.getCategoryId(),
                            projection.getCategoryName(),
                            amount,
                            percentage
                    );
                })
                .toList();
    }

    private PeriodComparisonResponse buildComparison(
            Long userId,
            YearMonth currentPeriod,
            BigDecimal currentIncome,
            BigDecimal currentExpense
    ) {
        YearMonth previousPeriod =
                currentPeriod.minusMonths(1);

        BigDecimal previousIncome =
                safeAmount(
                        transactionRepository
                                .calculateTotalAmountByTypeAndPeriod(
                                        userId,
                                        TransactionType.INCOME,
                                        previousPeriod.atDay(1),
                                        previousPeriod.atEndOfMonth()
                                )
                );

        BigDecimal previousExpense =
                safeAmount(
                        transactionRepository
                                .calculateTotalAmountByTypeAndPeriod(
                                        userId,
                                        TransactionType.EXPENSE,
                                        previousPeriod.atDay(1),
                                        previousPeriod.atEndOfMonth()
                                )
                );

        BigDecimal currentBalance =
                currentIncome.subtract(
                        currentExpense
                );

        BigDecimal previousBalance =
                previousIncome.subtract(
                        previousExpense
                );

        return new PeriodComparisonResponse(
                currentIncome,
                previousIncome,
                calculateChangePercentage(
                        currentIncome,
                        previousIncome
                ),
                currentExpense,
                previousExpense,
                calculateChangePercentage(
                        currentExpense,
                        previousExpense
                ),
                currentBalance,
                previousBalance
        );
    }

    private BigDecimal calculatePercentage(
            BigDecimal amount,
            BigDecimal total
    ) {
        if (total.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            return BigDecimal.ZERO;
        }

        return amount
                .multiply(
                        BigDecimal.valueOf(100)
                )
                .divide(
                        total,
                        2,
                        RoundingMode.HALF_UP
                );
    }

    private BigDecimal calculateChangePercentage(
            BigDecimal current,
            BigDecimal previous
    ) {
        if (previous.compareTo(
                BigDecimal.ZERO
        ) == 0) {

            if (current.compareTo(
                    BigDecimal.ZERO
            ) == 0) {
                return BigDecimal.ZERO;
            }

            return BigDecimal.valueOf(100);
        }

        return current
                .subtract(previous)
                .multiply(
                        BigDecimal.valueOf(100)
                )
                .divide(
                        previous.abs(),
                        2,
                        RoundingMode.HALF_UP
                );
    }

    private BigDecimal safeAmount(
            BigDecimal value
    ) {
        return value != null
                ? value
                : BigDecimal.ZERO;
    }

    private void validatePeriod(
            int year,
            int month
    ) {
        if (year < 2000
                || year > 2100
                || month < 1
                || month > 12) {

            throw new AppException(
                    ErrorCode.INVALID_REQUEST
            );
        }
    }

    private Long getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal()
                instanceof UserPrincipal principal)) {

            throw new AppException(
                    ErrorCode.UNAUTHORIZED
            );
        }

        return principal.getId();
    }
}