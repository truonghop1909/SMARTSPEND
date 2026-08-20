package com.smartspend.budget.service;

import com.smartspend.auth.entity.User;
import com.smartspend.auth.repository.UserRepository;
import com.smartspend.budget.dto.request.CreateBudgetRequest;
import com.smartspend.budget.dto.request.UpdateBudgetRequest;
import com.smartspend.budget.dto.response.BudgetResponse;
import com.smartspend.budget.entity.Budget;
import com.smartspend.budget.mapper.BudgetMapper;
import com.smartspend.budget.repository.BudgetRepository;
import com.smartspend.category.entity.Category;
import com.smartspend.category.entity.CategoryType;
import com.smartspend.category.repository.CategoryRepository;
import com.smartspend.common.exception.AppException;
import com.smartspend.common.exception.ErrorCode;
import com.smartspend.notification.entity.NotificationType;
import com.smartspend.notification.repository.NotificationRepository;
import com.smartspend.notification.service.NotificationService;
import com.smartspend.security.UserPrincipal;
import com.smartspend.transaction.entity.TransactionType;
import com.smartspend.transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.smartspend.dashboard.cache.DashboardCacheService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class BudgetServiceImpl
                implements BudgetService {

        private static final Logger log = LoggerFactory.getLogger(
                        BudgetServiceImpl.class);

        private static final String FINANCIAL_CONTEXT_CACHE_PREFIX = "financial-context:user:";

        private final BudgetRepository budgetRepository;
        private final CategoryRepository categoryRepository;
        private final UserRepository userRepository;
        private final TransactionRepository transactionRepository;
        private final BudgetMapper budgetMapper;
        private final RedisTemplate<String, Object> redisTemplate;
        private final NotificationService notificationService;
        private final NotificationRepository notificationRepository;
        private final DashboardCacheService dashboardCacheService;

        public BudgetServiceImpl(
                        BudgetRepository budgetRepository,
                        CategoryRepository categoryRepository,
                        UserRepository userRepository,
                        TransactionRepository transactionRepository,
                        BudgetMapper budgetMapper,
                        RedisTemplate<String, Object> redisTemplate,
                        NotificationService notificationService,
                        NotificationRepository notificationRepository,
                        DashboardCacheService dashboardCacheService) {
                this.budgetRepository = budgetRepository;

                this.categoryRepository = categoryRepository;

                this.userRepository = userRepository;

                this.transactionRepository = transactionRepository;

                this.budgetMapper = budgetMapper;

                this.redisTemplate = redisTemplate;

                this.notificationService = notificationService;

                this.notificationRepository = notificationRepository;

                this.dashboardCacheService = dashboardCacheService;
        }

        // =========================================================
        // GET ALL
        // =========================================================

        @Override
        @Transactional(readOnly = true)
        public List<BudgetResponse> getAll(
                        Integer year,
                        Integer month) {
                Long userId = getCurrentUserId();

                validatePeriodFilter(
                                year,
                                month);

                List<Budget> budgets;

                if (year != null
                                && month != null) {

                        budgets = budgetRepository
                                        .findAllByUserIdAndBudgetYearAndBudgetMonthOrderByCategoryNameAsc(
                                                        userId,
                                                        year,
                                                        month);

                } else {
                        budgets = budgetRepository
                                        .findAllByUserIdOrderByBudgetYearDescBudgetMonthDesc(
                                                        userId);
                }

                return budgets
                                .stream()
                                .map(budget -> toResponseWithSpent(
                                                budget,
                                                userId))
                                .toList();
        }

        // =========================================================
        // GET BY ID
        // =========================================================

        @Override
        @Transactional(readOnly = true)
        public BudgetResponse getById(
                        Long budgetId) {
                Long userId = getCurrentUserId();

                Budget budget = findOwnedBudget(
                                budgetId,
                                userId);

                return toResponseWithSpent(
                                budget,
                                userId);
        }

        // =========================================================
        // CREATE
        // =========================================================

        @Override
        @Transactional
        public BudgetResponse create(
                        CreateBudgetRequest request) {
                Long userId = getCurrentUserId();

                validateAmount(
                                request.amount());

                Category category = findAccessibleCategory(
                                request.categoryId(),
                                userId);

                validateExpenseCategory(
                                category);

                boolean duplicate = budgetRepository
                                .existsByUserIdAndCategoryIdAndBudgetYearAndBudgetMonth(
                                                userId,
                                                request.categoryId(),
                                                request.year(),
                                                request.month());

                if (duplicate) {
                        throw new AppException(
                                        ErrorCode.BUDGET_ALREADY_EXISTS);
                }

                User user = userRepository
                                .findById(userId)
                                .orElseThrow(() -> new AppException(
                                                ErrorCode.USER_NOT_FOUND));

                Budget budget = budgetMapper.toEntity(
                                request,
                                user,
                                category);

                Budget savedBudget = budgetRepository.save(
                                budget);

                dashboardCacheService.evict(
                                userId,
                                savedBudget.getBudgetYear(),
                                savedBudget.getBudgetMonth());

                invalidateFinancialContextCache(
                                userId);

                return toResponseWithSpent(
                                savedBudget,
                                userId);
        }

        // =========================================================
        // UPDATE
        // =========================================================

        @Override
        @Transactional
        public BudgetResponse update(
                        Long budgetId,
                        UpdateBudgetRequest request) {
                Long userId = getCurrentUserId();

                validateAmount(
                                request.amount());

                Budget budget = findOwnedBudget(
                                budgetId,
                                userId);

                budget.updateAmount(
                                request.amount());

                Budget updatedBudget = budgetRepository.save(
                                budget);

                dashboardCacheService.evict(
                                userId,
                                updatedBudget.getBudgetYear(),
                                updatedBudget.getBudgetMonth());

                invalidateFinancialContextCache(
                                userId);

                return toResponseWithSpent(
                                updatedBudget,
                                userId);
        }

        // =========================================================
        // DELETE
        // =========================================================

        @Override
        @Transactional
        public void delete(
                        Long budgetId) {
                Long userId = getCurrentUserId();

                Budget budget = findOwnedBudget(
                                budgetId,
                                userId);

                int year = budget.getBudgetYear();

                int month = budget.getBudgetMonth();

                budgetRepository.delete(
                                budget);

                dashboardCacheService.evict(
                                userId,
                                year,
                                month);

                invalidateFinancialContextCache(
                                userId);
        }

        // =========================================================
        // BUDGET ALERT
        // =========================================================

        @Override
        @Transactional
        public void checkBudgetAlert(
                        Long userId,
                        Long categoryId,
                        LocalDate transactionDate) {
                if (userId == null
                                || categoryId == null
                                || transactionDate == null) {

                        return;
                }

                int year = transactionDate.getYear();

                int month = transactionDate.getMonthValue();

                Budget budget = budgetRepository
                                .findByUserIdAndCategoryIdAndBudgetYearAndBudgetMonth(
                                                userId,
                                                categoryId,
                                                year,
                                                month)
                                .orElse(null);

                if (budget == null) {
                        return;
                }

                YearMonth period = YearMonth.of(
                                year,
                                month);

                BigDecimal spent = transactionRepository
                                .calculateAmountByCategoryAndPeriod(
                                                userId,
                                                categoryId,
                                                TransactionType.EXPENSE,
                                                period.atDay(1),
                                                period.atEndOfMonth());

                if (spent == null) {
                        spent = BigDecimal.ZERO;
                }

                BigDecimal percentage = spent
                                .multiply(
                                                BigDecimal.valueOf(100))
                                .divide(
                                                budget.getAmount(),
                                                2,
                                                RoundingMode.HALF_UP);

                if (percentage.compareTo(
                                BigDecimal.valueOf(100)) > 0) {

                        createBudgetAlertIfAbsent(
                                        userId,
                                        budget,
                                        "OVER",
                                        "Đã vượt ngân sách",
                                        "Chi tiêu của danh mục "
                                                        + budget.getCategory().getName()
                                                        + " đã vượt ngân sách tháng này.");

                        return;
                }

                if (percentage.compareTo(
                                BigDecimal.valueOf(100)) >= 0) {

                        createBudgetAlertIfAbsent(
                                        userId,
                                        budget,
                                        "100",
                                        "Đã dùng hết ngân sách",
                                        "Chi tiêu của danh mục "
                                                        + budget.getCategory().getName()
                                                        + " đã đạt 100% ngân sách tháng này.");

                        return;
                }

                if (percentage.compareTo(
                                BigDecimal.valueOf(80)) >= 0) {

                        createBudgetAlertIfAbsent(
                                        userId,
                                        budget,
                                        "80",
                                        "Sắp chạm ngân sách",
                                        "Chi tiêu của danh mục "
                                                        + budget.getCategory().getName()
                                                        + " đã đạt ít nhất 80% ngân sách tháng này.");
                }
        }

        // =========================================================
        // RESPONSE CALCULATION
        // =========================================================

        private BudgetResponse toResponseWithSpent(
                        Budget budget,
                        Long userId) {
                YearMonth period = YearMonth.of(
                                budget.getBudgetYear(),
                                budget.getBudgetMonth());

                LocalDate fromDate = period.atDay(1);

                LocalDate toDate = period.atEndOfMonth();

                BigDecimal spent = transactionRepository
                                .calculateAmountByCategoryAndPeriod(
                                                userId,
                                                budget.getCategory().getId(),
                                                TransactionType.EXPENSE,
                                                fromDate,
                                                toDate);

                return budgetMapper.toResponse(
                                budget,
                                spent);
        }

        // =========================================================
        // CATEGORY
        // =========================================================

        private Category findAccessibleCategory(
                        Long categoryId,
                        Long userId) {
                return categoryRepository
                                .findAccessibleById(
                                                categoryId,
                                                userId)
                                .orElseThrow(() -> new AppException(
                                                ErrorCode.CATEGORY_NOT_FOUND));
        }

        private void validateExpenseCategory(
                        Category category) {
                if (category.getType() != CategoryType.EXPENSE) {

                        throw new AppException(
                                        ErrorCode.BUDGET_CATEGORY_MUST_BE_EXPENSE);
                }
        }

        // =========================================================
        // BUDGET OWNERSHIP
        // =========================================================

        private Budget findOwnedBudget(
                        Long budgetId,
                        Long userId) {
                return budgetRepository
                                .findByIdAndUserId(
                                                budgetId,
                                                userId)
                                .orElseThrow(() -> new AppException(
                                                ErrorCode.BUDGET_NOT_FOUND));
        }

        // =========================================================
        // VALIDATION
        // =========================================================

        private void validateAmount(
                        BigDecimal amount) {
                if (amount == null
                                || amount.compareTo(
                                                BigDecimal.ZERO) <= 0) {

                        throw new AppException(
                                        ErrorCode.BUDGET_INVALID_AMOUNT);
                }
        }

        private void validatePeriodFilter(
                        Integer year,
                        Integer month) {
                if ((year == null) != (month == null)) {

                        throw new AppException(
                                        ErrorCode.BUDGET_INVALID_PERIOD);
                }

                if (month != null
                                && (month < 1
                                                || month > 12)) {

                        throw new AppException(
                                        ErrorCode.BUDGET_INVALID_PERIOD);
                }
        }

        // =========================================================
        // CURRENT USER
        // =========================================================

        private Long getCurrentUserId() {
                Authentication authentication = SecurityContextHolder
                                .getContext()
                                .getAuthentication();

                if (authentication == null
                                || !authentication.isAuthenticated()
                                || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {

                        throw new AppException(
                                        ErrorCode.UNAUTHORIZED);
                }

                return principal.getId();
        }

        // =========================================================
        // CACHE
        // =========================================================

        private void invalidateFinancialContextCache(
                        Long userId) {
                try {
                        redisTemplate.delete(
                                        FINANCIAL_CONTEXT_CACHE_PREFIX
                                                        + userId);

                } catch (DataAccessException exception) {
                        log.warn(
                                        "Unable to invalidate financial context cache. userId={}",
                                        userId);
                }
        }

        // =========================================================
        // NOTIFICATION
        // =========================================================

        private void createBudgetAlertIfAbsent(
                        Long userId,
                        Budget budget,
                        String threshold,
                        String title,
                        String content) {
                String actionUrl = "/api/budgets/"
                                + budget.getId()
                                + "?alert="
                                + threshold;

                boolean exists = notificationRepository
                                .existsByUserIdAndTypeAndActionUrl(
                                                userId,
                                                NotificationType.BUDGET,
                                                actionUrl);

                if (exists) {
                        return;
                }

                notificationService.create(
                                userId,
                                NotificationType.BUDGET,
                                title,
                                content,
                                actionUrl);
        }
}