package com.smartspend.dashboard.service;

import com.smartspend.auth.entity.User;
import com.smartspend.auth.enums.AuthProvider;
import com.smartspend.dashboard.cache.DashboardCacheService;
import com.smartspend.dashboard.dto.response.CategoryStatisticResponse;
import com.smartspend.dashboard.dto.response.DashboardResponse;
import com.smartspend.dashboard.dto.response.DashboardSummaryResponse;
import com.smartspend.dashboard.dto.response.TrendResponse;
import com.smartspend.dashboard.projection.CategoryStatisticProjection;
import com.smartspend.dashboard.projection.MonthlyTrendProjection;
import com.smartspend.security.UserPrincipal;
import com.smartspend.transaction.entity.TransactionType;
import com.smartspend.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private DashboardCacheService dashboardCacheService;

    private DashboardServiceImpl dashboardService;

    private static final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        dashboardService =
                new DashboardServiceImpl(
                        transactionRepository,
                        dashboardCacheService
                );

        User user =
                new User(
                        "dashboard@example.com",
                        "password-hash",
                        "Dashboard User",
                        AuthProvider.LOCAL
                );

        ReflectionTestUtils.setField(
                user,
                "id",
                USER_ID
        );

        UserPrincipal principal =
                UserPrincipal.from(user);

        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(
                        principal,
                        null,
                        principal.getAuthorities()
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // =========================================================
    // CACHE
    // =========================================================

    @Test
    void getDashboard_shouldReturnCachedDashboard_whenCacheHit() {
        DashboardResponse cached =
                createDashboardResponse();

        when(
                dashboardCacheService.get(
                        USER_ID,
                        2026,
                        8
                )
        ).thenReturn(cached);

        DashboardResponse result =
                dashboardService.getDashboard(
                        2026,
                        8
                );

        assertSame(
                cached,
                result
        );

        verify(
                transactionRepository,
                never()
        ).calculateTotalAmountByTypeAndPeriod(
                anyLong(),
                any(),
                any(LocalDate.class),
                any(LocalDate.class)
        );

        verify(
                dashboardCacheService,
                never()
        ).put(
                anyLong(),
                anyInt(),
                anyInt(),
                any()
        );
    }

    // =========================================================
    // SUMMARY
    // =========================================================

    @Test
    void getDashboard_shouldCalculateSummary_whenCacheMiss() {
        when(
                dashboardCacheService.get(
                        USER_ID,
                        2026,
                        8
                )
        ).thenReturn(null);

        mockCurrentPeriodAmounts(
                new BigDecimal("20000000"),
                new BigDecimal("12000000")
        );

        mockPreviousPeriodAmounts(
                new BigDecimal("18000000"),
                new BigDecimal("10000000")
        );

        when(
                transactionRepository.findStatisticsByCategory(
                        eq(USER_ID),
                        eq(TransactionType.EXPENSE),
                        any(LocalDate.class),
                        any(LocalDate.class)
                )
        ).thenReturn(List.of());

        DashboardResponse result =
                dashboardService.getDashboard(
                        2026,
                        8
                );

        assertNotNull(result);

        assertEquals(
                new BigDecimal("20000000"),
                result.summary().totalIncome()
        );

        assertEquals(
                new BigDecimal("12000000"),
                result.summary().totalExpense()
        );

        assertEquals(
                new BigDecimal("8000000"),
                result.summary().balance()
        );

        verify(dashboardCacheService)
                .put(
                        USER_ID,
                        2026,
                        8,
                        result
                );
    }

    // =========================================================
    // CATEGORY STATISTICS
    // =========================================================

    @Test
    void getDashboard_shouldCalculateCategoryPercentage() {
        when(
                dashboardCacheService.get(
                        USER_ID,
                        2026,
                        8
                )
        ).thenReturn(null);

        mockCurrentPeriodAmounts(
                BigDecimal.ZERO,
                new BigDecimal("10000000")
        );

        mockPreviousPeriodAmounts(
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );

        CategoryStatisticProjection food =
                mockCategoryProjection(
                        10L,
                        "Ăn uống",
                        new BigDecimal("3000000")
                );

        CategoryStatisticProjection shopping =
                mockCategoryProjection(
                        20L,
                        "Mua sắm",
                        new BigDecimal("2000000")
                );

        when(
                transactionRepository.findStatisticsByCategory(
                        eq(USER_ID),
                        eq(TransactionType.EXPENSE),
                        any(LocalDate.class),
                        any(LocalDate.class)
                )
        ).thenReturn(
                List.of(
                        food,
                        shopping
                )
        );

        DashboardResponse result =
                dashboardService.getDashboard(
                        2026,
                        8
                );

        List<CategoryStatisticResponse> statistics =
                result.expenseByCategory();

        assertEquals(
                2,
                statistics.size()
        );

        assertEquals(
                new BigDecimal("30.00"),
                statistics.getFirst().percentage()
        );

        assertEquals(
                new BigDecimal("20.00"),
                statistics.get(1).percentage()
        );
    }

    @Test
    void getDashboard_shouldReturnZeroPercentage_whenTotalExpenseIsZero() {
        when(
                dashboardCacheService.get(
                        USER_ID,
                        2026,
                        8
                )
        ).thenReturn(null);

        mockCurrentPeriodAmounts(
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );

        mockPreviousPeriodAmounts(
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );

        CategoryStatisticProjection projection =
                mockCategoryProjection(
                        10L,
                        "Ăn uống",
                        BigDecimal.ZERO
                );

        when(
                transactionRepository.findStatisticsByCategory(
                        eq(USER_ID),
                        eq(TransactionType.EXPENSE),
                        any(LocalDate.class),
                        any(LocalDate.class)
                )
        ).thenReturn(
                List.of(projection)
        );

        DashboardResponse result =
                dashboardService.getDashboard(
                        2026,
                        8
                );

        assertEquals(
                BigDecimal.ZERO,
                result
                        .expenseByCategory()
                        .getFirst()
                        .percentage()
        );
    }

    // =========================================================
    // PERIOD COMPARISON
    // =========================================================

    @Test
    void getDashboard_shouldCalculatePeriodComparison() {
        when(
                dashboardCacheService.get(
                        USER_ID,
                        2026,
                        8
                )
        ).thenReturn(null);

        mockCurrentPeriodAmounts(
                new BigDecimal("20000000"),
                new BigDecimal("15000000")
        );

        mockPreviousPeriodAmounts(
                new BigDecimal("10000000"),
                new BigDecimal("10000000")
        );

        when(
                transactionRepository.findStatisticsByCategory(
                        eq(USER_ID),
                        eq(TransactionType.EXPENSE),
                        any(LocalDate.class),
                        any(LocalDate.class)
                )
        ).thenReturn(List.of());

        DashboardResponse result =
                dashboardService.getDashboard(
                        2026,
                        8
                );

        assertEquals(
                new BigDecimal("100.00"),
                result.comparison()
                        .incomeChangePercentage()
        );

        assertEquals(
                new BigDecimal("50.00"),
                result.comparison()
                        .expenseChangePercentage()
        );

        assertEquals(
                new BigDecimal("5000000"),
                result.comparison()
                        .currentBalance()
        );

        assertEquals(
                BigDecimal.ZERO,
                result.comparison()
                        .previousBalance()
        );
    }

    // =========================================================
    // MONTHLY TREND
    // =========================================================

    @Test
    void getMonthlyTrend_shouldMapProjectionToResponse() {
        MonthlyTrendProjection july =
                mockTrendProjection(
                        2026,
                        7,
                        new BigDecimal("20000000"),
                        new BigDecimal("12000000")
                );

        MonthlyTrendProjection august =
                mockTrendProjection(
                        2026,
                        8,
                        new BigDecimal("22000000"),
                        new BigDecimal("15000000")
                );

        when(
                transactionRepository.findMonthlyTrend(
                        eq(USER_ID),
                        any(LocalDate.class),
                        any(LocalDate.class)
                )
        ).thenReturn(
                List.of(
                        july,
                        august
                )
        );

        List<TrendResponse> result =
                dashboardService.getMonthlyTrend(
                        6
                );

        assertEquals(
                2,
                result.size()
        );

        assertEquals(
                2026,
                result.getFirst().year()
        );

        assertEquals(
                7,
                result.getFirst().month()
        );

        assertEquals(
                new BigDecimal("8000000"),
                result.getFirst().balance()
        );

        assertEquals(
                new BigDecimal("7000000"),
                result.get(1).balance()
        );
    }

    // =========================================================
    // HELPERS
    // =========================================================

    private void mockCurrentPeriodAmounts(
            BigDecimal income,
            BigDecimal expense
    ) {
        when(
                transactionRepository
                        .calculateTotalAmountByTypeAndPeriod(
                                USER_ID,
                                TransactionType.INCOME,
                                LocalDate.of(
                                        2026,
                                        8,
                                        1
                                ),
                                LocalDate.of(
                                        2026,
                                        8,
                                        31
                                )
                        )
        ).thenReturn(income);

        when(
                transactionRepository
                        .calculateTotalAmountByTypeAndPeriod(
                                USER_ID,
                                TransactionType.EXPENSE,
                                LocalDate.of(
                                        2026,
                                        8,
                                        1
                                ),
                                LocalDate.of(
                                        2026,
                                        8,
                                        31
                                )
                        )
        ).thenReturn(expense);
    }

    private void mockPreviousPeriodAmounts(
            BigDecimal income,
            BigDecimal expense
    ) {
        when(
                transactionRepository
                        .calculateTotalAmountByTypeAndPeriod(
                                USER_ID,
                                TransactionType.INCOME,
                                LocalDate.of(
                                        2026,
                                        7,
                                        1
                                ),
                                LocalDate.of(
                                        2026,
                                        7,
                                        31
                                )
                        )
        ).thenReturn(income);

        when(
                transactionRepository
                        .calculateTotalAmountByTypeAndPeriod(
                                USER_ID,
                                TransactionType.EXPENSE,
                                LocalDate.of(
                                        2026,
                                        7,
                                        1
                                ),
                                LocalDate.of(
                                        2026,
                                        7,
                                        31
                                )
                        )
        ).thenReturn(expense);
    }

    private CategoryStatisticProjection mockCategoryProjection(
            Long categoryId,
            String categoryName,
            BigDecimal amount
    ) {
        CategoryStatisticProjection projection =
                mock(
                        CategoryStatisticProjection.class
                );

        when(
                projection.getCategoryId()
        ).thenReturn(categoryId);

        when(
                projection.getCategoryName()
        ).thenReturn(categoryName);

        when(
                projection.getAmount()
        ).thenReturn(amount);

        return projection;
    }

    private MonthlyTrendProjection mockTrendProjection(
            int year,
            int month,
            BigDecimal income,
            BigDecimal expense
    ) {
        MonthlyTrendProjection projection =
                mock(
                        MonthlyTrendProjection.class
                );

        when(
                projection.getYear()
        ).thenReturn(year);

        when(
                projection.getMonth()
        ).thenReturn(month);

        when(
                projection.getTotalIncome()
        ).thenReturn(income);

        when(
                projection.getTotalExpense()
        ).thenReturn(expense);

        return projection;
    }

    private DashboardResponse createDashboardResponse() {
        return new DashboardResponse(
                new DashboardSummaryResponse(
                        new BigDecimal("20000000"),
                        new BigDecimal("12000000"),
                        new BigDecimal("8000000")
                ),
                List.of(),
                null
        );
    }
}